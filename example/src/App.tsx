import * as React from 'react';
import { SafeAreaView, StyleSheet } from 'react-native';
import { Camera, useCameraDevices, useFrameProcessor } from 'react-native-vision-camera';
import * as DDN from "vision-camera-dynamsoft-document-normalizer";
import * as REA from 'react-native-reanimated';

export default function App() {
  const [hasPermission, setHasPermission] = React.useState(false);
  const devices = useCameraDevices();
  const device = devices.back;
  const frameProcessor = useFrameProcessor((frame) => {
    'worklet'
    const detect = DDN.detect(frame);
    const normalize = DDN.normalize(frame,{includeNormalizationResultAsBase64:false});
    console.log(detect);
    console.log(normalize);
  }, [])

  React.useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'authorized');
      let result = await DDN.initLicense("DLS2eyJoYW5kc2hha2VDb2RlIjoiMjAwMDAxLTE2NDk4Mjk3OTI2MzUiLCJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSIsInNlc3Npb25QYXNzd29yZCI6IndTcGR6Vm05WDJrcEQ5YUoifQ==");
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
        </>)}
      </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1
  },
});