import * as React from 'react';
import { Alert, Dimensions, Platform, SafeAreaView, StyleSheet } from 'react-native';
import { Camera, PhotoFile, useCameraDevice, useCameraDevices, useFrameProcessor } from 'react-native-vision-camera';
import * as DDN from "vision-camera-dynamsoft-document-normalizer";
import { Svg, Polygon } from 'react-native-svg';
import type { DetectedQuadResult } from 'vision-camera-dynamsoft-document-normalizer';
import { useEffect, useRef, useState } from 'react';
import { Worklets,useSharedValue } from 'react-native-worklets-core';
import { intersectionOverUnion } from '../Utils';

export interface ScannerProps{
    onScanned?: (path:PhotoFile|null) => void;
  }
  
export default function Scanner(props:ScannerProps) {
  const camera = useRef<Camera|null>(null)
  const [isActive,setIsActive] = useState(true);
  const [hasPermission, setHasPermission] = useState(false);
  const detectionResults = useSharedValue([] as DetectedQuadResult[]);
  const frameWidth = useSharedValue(1920);
  const frameHeight = useSharedValue(1080);
  const [viewBox,setViewBox] = useState("0 0 1080 1920");
  const [pointsText, setPointsText] = useState("default");
  const taken = useSharedValue(false);
  const photo = useRef<PhotoFile|null>(null);
  const previousResults = useRef([] as DetectedQuadResult[]);

  const device = useCameraDevice("back");
  const frameProcessor = useFrameProcessor((frame) => {
    'worklet'
    console.log(taken.value)
    console.log("detect frame");
    console.log(frame);
    if (taken.value === false) {
      const results = DDN.detect(frame);
      console.log(results);
      if (results.length>0) {
        frameWidth.value = frame.width;
        frameHeight.value = frame.height;
        detectionResults.value = results;
        updateViewBoxJS();
        updatePointsDataJS();
      }
    }
  }, [])

  useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'granted');
      let result = await DDN.initLicense("DLS2eyJoYW5kc2hha2VDb2RlIjoiMjAwMDAxLTE2NDk4Mjk3OTI2MzUiLCJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSIsInNlc3Npb25QYXNzd29yZCI6IndTcGR6Vm05WDJrcEQ5YUoifQ==");
      console.log("Licesne valid: ");
      console.log(result);
      if (result === false) {
        Alert.alert("DDN","License invalid");
      }
    })();
  }, []);

  const getFrameSize = () => {
    let width, height;
    if (Platform.OS === 'android') {
      if (frameWidth>frameHeight && Dimensions.get('window').width>Dimensions.get('window').height){
        width = frameWidth;
        height = frameHeight;
      }else {
        console.log("Has rotation");
        width = frameHeight;
        height = frameWidth;
      }
    } else {
      width = frameWidth;
      height = frameHeight;
    }
    return [width, height];
  }

  const updateViewBox = () => {
    const frameSize = getFrameSize();
    setViewBox("0 0 "+frameSize[0]+" "+frameSize[1]);
    console.log("viewBox"+viewBox);
  }

  const updateViewBoxJS = Worklets.createRunInJsFn(updateViewBox);
  const updatePointsData = () => {
    if (detectionResults.value.length>0) {
      let result = detectionResults.value[0];
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

  const updatePointsDataJS = Worklets.createRunInJsFn(updatePointsData);
  
  useEffect(() => {
    console.log("pointsText changed");
    checkIfSteady();
  }, [pointsText]);


  const takePhoto = async () => {
    console.log("take photo");
    if (camera.current) {
      taken.value = true;
      console.log("using camera");
      photo.current = await camera.current.takePhoto();
      if (photo.current) {
        setIsActive(false);
        if (Platform.OS === "android") {
          if (photo.current.metadata!.Orientation === 6) {
            console.log("rotate bitmap for Android");
            await DDN.rotateFile(photo.current.path,90);
          }
        }
        if (props.onScanned) {
          console.log(photo.current);
          props.onScanned(photo.current);
        }
      }
    }
  }

  const checkIfSteady = async () => {
    let result = detectionResults.value[0];
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
              frameProcessor={frameProcessor}
            />
            <Svg preserveAspectRatio='xMidYMid slice' style={StyleSheet.absoluteFill} viewBox={viewBox}>
              <Polygon
                points={pointsText}
                fill="lime"
                stroke="green"
                opacity="0.5"
                strokeWidth="1"
              />
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
