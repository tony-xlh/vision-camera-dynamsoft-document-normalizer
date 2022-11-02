import * as React from 'react';
import { SafeAreaView, StyleSheet } from 'react-native';
import { Camera, useCameraDevices, useFrameProcessor } from 'react-native-vision-camera';
import * as DDN from "vision-camera-dynamsoft-document-normalizer";
import { Svg, Polygon } from 'react-native-svg';
import * as REA from 'react-native-reanimated';
import type { DetectedQuadResult } from 'vision-camera-dynamsoft-document-normalizer';

export default function App() {
  const [hasPermission, setHasPermission] = React.useState(false);
  const detectionResults = REA.useSharedValue([] as DetectedQuadResult[]);
  const frameWidth = REA.useSharedValue(0);
  const frameHeight = REA.useSharedValue(0);
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

  React.useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'authorized');
      let result = await DDN.initLicense("DLS2eyJoYW5kc2hha2VDb2RlIjoiMTAwMjI3NzYzLVRYbE5iMkpwYkdWUWNtOXFYMlJrYmciLCJvcmdhbml6YXRpb25JRCI6IjEwMDIyNzc2MyIsImNoZWNrQ29kZSI6MTM0ODY2MDUyMn0=");
      console.log(result);
    })();
  }, []);

  return (
      <SafeAreaView style={styles.container}>
        {device != null &&
        hasPermission && (
        <>
            <Camera
            style={StyleSheet.absoluteFill}
            device={device}
            isActive={true}
            frameProcessor={frameProcessor}
            frameProcessorFps={5}
            />
            {frameWidth.value != 0 && (
              <Svg preserveAspectRatio='xMidYMid slice' style={StyleSheet.absoluteFill} viewBox={viewBox.value}>
                <Polygon
                  points={pointsData.value}
                  fill="lime"
                  stroke="green"
                  opacity="0.5"
                  strokeWidth="1"
                />
              </Svg>
            )}
        </>)}
      </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1
  },
});