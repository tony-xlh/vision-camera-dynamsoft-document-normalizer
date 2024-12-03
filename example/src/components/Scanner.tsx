import * as React from 'react';
import { Alert, Dimensions, Platform, SafeAreaView, StyleSheet } from 'react-native';
import { Camera, type PhotoFile, runAtTargetFps, useCameraDevice, useCameraDevices, useCameraFormat, useFrameProcessor } from 'react-native-vision-camera';
import * as DDN from "vision-camera-dynamsoft-document-normalizer";
import { Svg, Polygon } from 'react-native-svg';
import type { DetectedQuadResult } from 'vision-camera-dynamsoft-document-normalizer';
import { useEffect, useRef, useState } from 'react';
import { Worklets,useSharedValue } from 'react-native-worklets-core';
import { intersectionOverUnion, sleep } from '../Utils';

export interface ScannerProps{
    onScanned?: (path:PhotoFile|null) => void;
  }
  
export default function Scanner(props:ScannerProps) {
  const camera = useRef<Camera|null>(null)
  const [isActive,setIsActive] = useState(true);
  const [hasPermission, setHasPermission] = useState(false);
  const [detectionResults,setDetectionResults] = useState([] as DetectedQuadResult[]);
  const convertAndSetResults = (records:Record<string,DetectedQuadResult>) => {
    let results:DetectedQuadResult[] = [];
    for (let index = 0; index < Object.keys(records).length; index++) {
      const result = records[Object.keys(records)[index]];
      results.push(result);
    }
    setDetectionResults(results);
  }
  const convertAndSetResultsJS = Worklets.createRunOnJS(convertAndSetResults);
  const frameWidth = useSharedValue(1920);
  const frameHeight = useSharedValue(1080);
  const [viewBox,setViewBox] = useState("0 0 1080 1920");
  const [pointsText, setPointsText] = useState("default");
  const takenShared = useSharedValue(false);
  const photo = useRef<PhotoFile|null>(null);
  const previousResults = useRef([] as DetectedQuadResult[]);
  const device = useCameraDevice("back");
  const cameraFormat = useCameraFormat(device, [
    { videoAspectRatio: 16 / 9 },
    { photoAspectRatio: 16 / 9 },
    { videoResolution: { width: 1920, height: 1080 } },
    { fps: 60 },
  ]);
  useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'granted');
    })();
  }, []);

  const getFrameSize = () => {
    let width, height;
    if (frameWidth.value>frameHeight.value){
      if (Dimensions.get('window').width>Dimensions.get('window').height) {
        width = frameWidth.value;
        height = frameHeight.value;
      }else{
        console.log("Has rotation");
        width = frameHeight.value;
        height = frameWidth.value;
      }
    }else if (frameWidth.value<frameHeight.value) {
      if (Dimensions.get('window').width<Dimensions.get('window').height) {
        width = frameWidth.value;
        height = frameHeight.value;
      }else{
        console.log("Has rotation");
        width = frameHeight.value;
        height = frameWidth.value;
      }
    }
    return [width, height];
  }

  const updateViewBox = () => {
    const frameSize = getFrameSize();
    setViewBox("0 0 "+frameSize[0]+" "+frameSize[1]);
    console.log("viewBox"+viewBox);
  }

  const updateViewBoxJS = Worklets.createRunOnJS(updateViewBox);
  const updatePointsData = () => {
    if (detectionResults.length>0) {
      let result = detectionResults[0];
      if (result) {
        let location = result.location;
        let pointsData = location.points[0].x + "," + location.points[0].y + " ";
        pointsData = pointsData + location.points[1].x + "," + location.points[1].y +" ";
        pointsData = pointsData + location.points[2].x + "," + location.points[2].y +" ";
        pointsData = pointsData + location.points[3].x + "," + location.points[3].y;
        setPointsText(pointsData);
      }
    }
  }

  const updatePointsDataJS = Worklets.createRunOnJS(updatePointsData);
  
  useEffect(() => {
    if (pointsText != "default") {
      console.log("pointsText changed");
      checkIfSteady();
    }
  }, [pointsText]);


  const takePhoto = async () => {
    console.log("take photo");
    if (camera.current) {
      console.log("using camera");
      takenShared.value = true;
      const result = await DDN.getNormalizationResult({includeNormalizationResultAsBase64:true});
      console.log(result);
      await sleep(500);
      photo.current = await camera.current.takePhoto();
      if (photo.current) {
        console.log(photo.current);
        setIsActive(false);
        if (Platform.OS === "android") {
          if (photo.current.metadata && photo.current.metadata.Orientation === 6) {
            console.log("rotate bitmap for Android");
            await DDN.rotateFile(photo.current.path,90);
          }
        }
        if (props.onScanned) {
          console.log(photo.current);
          props.onScanned(photo.current);
        }
      }else{
        Alert.alert("","Failed to take a photo");
        takenShared.value = false;
      }
    }
  }

  const checkIfSteady = async () => {
    if (detectionResults.length == 0) {
      return;
    }
    let result = detectionResults[0];
    console.log("previousResults");
    console.log(previousResults);
    if (result) {
      if (previousResults.current.length >= 2) {
        previousResults.current.push(result);
        if (steady() == true) {
          await takePhoto();
          console.log("steady");
        }else{
          console.log("shift result");
          previousResults.current.shift();
        }
      }else{
        console.log("add result");
        previousResults.current.push(result);
      }
    }
  }

  const steady = () => {
    if (previousResults.current[0] && previousResults.current[1] && previousResults.current[2]) {
      let iou1 = intersectionOverUnion(previousResults.current[0].location.points,previousResults.current[1].location.points);
      let iou2 = intersectionOverUnion(previousResults.current[1].location.points,previousResults.current[2].location.points);
      let iou3 = intersectionOverUnion(previousResults.current[0].location.points,previousResults.current[2].location.points);
      console.log(iou1);
      console.log(iou2);
      console.log(iou3);
      if (iou1>0.9 && iou2>0.9 && iou3>0.9) {
        return true;
      }else{
        return false;
      }
    }
    return false;
  }

  useEffect(() => {
    updateViewBox();
    updatePointsData();
  }, [detectionResults]);

  const frameProcessor = useFrameProcessor((frame) => {
    'worklet'
    console.log("detect frame");
    console.log(frame.toString());
    if (takenShared.value === false) {
      runAtTargetFps(3, () => {
        'worklet'
        try {
          const results = DDN.detect(frame,"",true);
          console.log(results);
          if (Object.keys(results).length>0) {
            frameWidth.value = frame.width;
            frameHeight.value = frame.height;
            convertAndSetResultsJS(results);
          }
        } catch (error) {
          console.log(error);
        }
      })
    }
  }, [])

  return (
      <SafeAreaView style={styles.container}>
        {device != null &&
        hasPermission && (
        <>
            <Camera
              style={StyleSheet.absoluteFill}
              ref={camera}
              isActive={isActive}
              device={device}
              photo={true}
              format={cameraFormat}
              frameProcessor={frameProcessor}
              pixelFormat='yuv'
              resizeMode='contain'
            />
            <Svg preserveAspectRatio='xMidYMid slice' style={StyleSheet.absoluteFill} viewBox={viewBox}>
              {pointsText != "default" && (
                <Polygon
                  points={pointsText}
                  fill="lime"
                  stroke="green"
                  opacity="0.5"
                  strokeWidth="1"
                />
              )}
            </Svg>
        </>)}
      </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1
  },
});
