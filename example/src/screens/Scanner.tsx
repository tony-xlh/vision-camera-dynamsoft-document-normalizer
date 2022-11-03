import * as React from 'react';
import { SafeAreaView, StyleSheet } from 'react-native';
import { Camera, useCameraDevices, useFrameProcessor } from 'react-native-vision-camera';
import * as DDN from "vision-camera-dynamsoft-document-normalizer";
import { Svg, Polygon } from 'react-native-svg';
import * as REA from 'react-native-reanimated';
import type { DetectedQuadResult } from 'vision-camera-dynamsoft-document-normalizer';
import { useEffect, useRef, useState } from 'react';
import { intersectionOverUnion } from '../Utils';

export default function ScannerScreen() {
  const camera = useRef<Camera>(null)
  const [hasPermission, setHasPermission] = useState(false);
  const detectionResults = REA.useSharedValue([] as DetectedQuadResult[]);
  const frameWidth = REA.useSharedValue(0);
  const frameHeight = REA.useSharedValue(0);
  const [pointsText, setPointsText] = useState("default");
  const [isActive, setIsActive] = useState(true);
  const previousResults = useRef([] as DetectedQuadResult[]);
  const viewBox = REA.useDerivedValue(() => {
    console.log("update viewbox");
    let viewBox = "";
    viewBox = "0 0 "+frameHeight.value+" "+frameWidth.value;
    console.log(viewBox);
    return viewBox;
  }, [frameWidth,frameHeight]);
  const pointsData = REA.useDerivedValue(() => {
    console.log("update pointsData");
    let data = "";
    if (detectionResults.value.length>0) {
      let result = detectionResults.value[0]; 
      if (result) {
        let location = result.location;
        let pointsData = location.points[0].x + "," + location.points[0].y + " ";
        pointsData = pointsData + location.points[1].x + "," + location.points[1].y +" ";
        pointsData = pointsData + location.points[2].x + "," + location.points[2].y +" ";
        pointsData = pointsData + location.points[3].x + "," + location.points[3].y;
        data = pointsData;
      }
    }
    REA.runOnJS(setPointsText)(data);
    console.log(data);
    return data;
  }, [detectionResults]);
  const devices = useCameraDevices();
  const device = devices.back;
  const frameProcessor = useFrameProcessor((frame) => {
    'worklet'
    const results = DDN.detect(frame);
    console.log(results);
    frameWidth.value = frame.width;
    frameHeight.value = frame.height;
    detectionResults.value = results;
  }, [])

  useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'authorized');
      let result = await DDN.initLicense("DLS2eyJoYW5kc2hha2VDb2RlIjoiMTAwMjI3NzYzLVRYbE5iMkpwYkdWUWNtOXFYMlJrYmciLCJvcmdhbml6YXRpb25JRCI6IjEwMDIyNzc2MyIsImNoZWNrQ29kZSI6MTM0ODY2MDUyMn0=");
      console.log(result);
    })();
  }, []);

  useEffect(() => {
    console.log("pointsText changed");
    checkIfSteady();
  }, [pointsText]);


  const takePhoto = async () => {
    if (camera.current) {
      const photo = await camera.current.takePhoto();
      console.log(photo);
    }
  }


  const empty = () => {
    console.log("test");
  }

  const checkIfSteady = () => {
    let result = detectionResults.value[0];
    console.log("previousResults");
    console.log(previousResults);
    if (result) {
      if (previousResults.current.length >= 3) {
        if (steady() == true) {
          setIsActive(false);
          console.log("steady");
        }else{
          console.log("shift and add result");
          previousResults.current.shift();
          previousResults.current.push(result);
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
      let iou3 = intersectionOverUnion(previousResults.current[2].location.points,previousResults.current[1].location.points);
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
            device={device}
            isActive={isActive}
            photo={true}
            frameProcessor={frameProcessor}
            frameProcessorFps={5}
            />

              <>
              <Svg preserveAspectRatio='xMidYMid slice' style={StyleSheet.absoluteFill} viewBox={viewBox.value}>
                <Polygon
                  points={pointsData.value}
                  fill="lime"
                  stroke="green"
                  opacity="0.5"
                  strokeWidth="1"
                />
              </Svg>
              </>
        </>)}
      </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1
  },
  text: {
    fontSize: 20,
    color: 'white',
    fontWeight: 'bold',
  },
});