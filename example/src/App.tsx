import * as React from 'react';
import { SafeAreaView, StyleSheet } from 'react-native';
import { Camera, useCameraDevices, useFrameProcessor } from 'react-native-vision-camera';
import * as DDN from "vision-camera-dynamsoft-document-normalizer";
import { Svg, Polygon } from 'react-native-svg';
import * as REA from 'react-native-reanimated';
import type { DetectedQuadResult } from 'vision-camera-dynamsoft-document-normalizer';

export default function App() {
  const [hasPermission, setHasPermission] = React.useState(false);
  const [frameWidth, setFrameWidth] = React.useState(0);
  const [frameHeight, setFrameHeight] = React.useState(0);
  const [detectionResults, setDetectionResults] = React.useState([] as DetectedQuadResult[]);
  const devices = useCameraDevices();
  const device = devices.back;
  const frameProcessor = useFrameProcessor((frame) => {
    'worklet'
    const results = DDN.detect(frame);
    console.log(results);
  }, [])

  React.useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'authorized');
      let result = await DDN.initLicense("DLS2eyJoYW5kc2hha2VDb2RlIjoiMTAwMjI3NzYzLVRYbE5iMkpwYkdWUWNtOXFYMlJrYmciLCJvcmdhbml6YXRpb25JRCI6IjEwMDIyNzc2MyIsImNoZWNrQ29kZSI6MTM0ODY2MDUyMn0=");
      console.log(result);
    })();
  }, []);

  const getViewBox = () => {
    const viewBox = "0 0 "+frameWidth+" "+frameHeight;
    return viewBox;
  }

  const callFromFrameProcessor = () => {
    console.log("called");
  }
  const updateFrameSize = (width:number, height:number) => {
    if (width != frameWidth && height != frameHeight) {
      console.log("update frame size: "+width+"x"+height);
      setFrameWidth(width);
      setFrameHeight(height);
    }
  }

  const getPointsData = (result:DetectedQuadResult) => {
    let location = result.location;
    let pointsData = location.points[0].x + "," + location.points[0].y + " ";
    pointsData = pointsData + location.points[1].x + "," + location.points[1].y +" ";
    pointsData = pointsData + location.points[2].x + "," + location.points[2].y +" ";
    pointsData = pointsData + location.points[3].x + "," + location.points[3].x;
    console.log("pointsData:");
    console.log(pointsData);
    return pointsData;
  }

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
            {frameWidth != 0 && (
              <Svg preserveAspectRatio='xMidYMid slice' style={StyleSheet.absoluteFill} viewBox={getViewBox()}>
               {detectionResults.map((result, idx) => (
                  <Polygon key={idx}
                  points={getPointsData(result)}
                  fill="lime"
                  stroke="green"
                  opacity="0.5"
                  strokeWidth="1"
                />
                ))}
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