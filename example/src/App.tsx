import * as React from 'react';
import { Alert, SafeAreaView, StyleSheet, Switch, Text, TouchableOpacity, View } from "react-native";
import Scanner from './components/Scanner';
import * as DDN from "vision-camera-dynamsoft-document-normalizer";
import Cropper from './components/Cropper';
import ResultViewer from './components/ResultViewer';
import { useEffect } from 'react';
import type { PhotoFile } from 'react-native-vision-camera';
import { blackWhite, originalColor } from './Templates';

export default function App() {
  const [showScanner,setShowScanner] = React.useState(false);
  const [showCropper,setShowCropper] = React.useState(false);
  const [showResultViewer,setShowResultViewer] = React.useState(false);
  const [photoTaken,setPhotoTaken] = React.useState<PhotoFile|undefined>();
  const [photoPath,setPhotoPath] = React.useState<string>("");
  const [points,setPoints] = React.useState<DDN.Point[]>([]);
  const [status,setStatus] = React.useState<string>("Initializing...");
  const [scanInBlackWhite,setScanInBlackWhite] = React.useState(false);
  useEffect(() => {
    (async () => {
      let license = "DLS2eyJoYW5kc2hha2VDb2RlIjoiMjAwMDAxLTE2NDk4Mjk3OTI2MzUiLCJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSIsInNlc3Npb25QYXNzd29yZCI6IndTcGR6Vm05WDJrcEQ5YUoifQ=="; //one-day public trial
      let result = await DDN.initLicense(license);
      console.log("Licesne valid: ");
      console.log(result);
      if (result === false) {
        Alert.alert("DDN","License invalid");
      }else{
        setStatus("");
      }
    })();
  }, []);

  const onPressed = () => {
    if (status === "Initializing...") {
      Alert.alert("DDN","Please wait for the initialization.");
    }else{
      setShowScanner(true);
    }
  }

  const onScanned = (photo:PhotoFile|null) => {
    if (photo) {
      setShowScanner(false);
      setPhotoTaken(photo);
      setShowCropper(true);
    }else{
      Alert.alert("Error","Failed to take a photo. Please try again.");
      setShowScanner(false);
    }
  }

  const updateTemplate = async () => {
    const newValue = !scanInBlackWhite;
    setScanInBlackWhite(newValue);
    if (newValue) {
      await DDN.initRuntimeSettingsFromString(blackWhite);
    }else{
      await DDN.initRuntimeSettingsFromString(originalColor);
    }
  }

  const renderBody = () => {
    if (showScanner) {
      return (
        <Scanner onScanned={onScanned}></Scanner>
      )
    }else if (showCropper){
      return (
        <Cropper 
          photo={photoTaken}
          onCanceled={()=>{
            setShowCropper(false);
            setShowScanner(true);
          }}
          onConfirmed={(path,adjustedPoints)=>{
            setPhotoPath(path);
            setPoints(adjustedPoints);
            setShowCropper(false);
            setShowResultViewer(true);
          }}
        ></Cropper>
      )
    }else if (showResultViewer){
      return (
        <ResultViewer photoPath={photoPath} points={points} 
          onBack={()=>{
            setShowResultViewer(false);
          }}  
        ></ResultViewer>
      )
    }else{
      return (
        <>
          <TouchableOpacity
            style={styles.button}
            onPress={() => onPressed()}
          >
            <Text style={styles.buttonText}>Scan Document</Text>
          </TouchableOpacity>
          <View style={styles.option}>
            <Text>Black and White</Text>
            <Switch value={scanInBlackWhite} onChange={() => updateTemplate()}></Switch>
          </View>
          <Text>{status}</Text>
        </>
        )
    }
  }
  return (
    <SafeAreaView style={styles.container}>
      {renderBody()}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex:1,
  },
  info: {
    margin: 8,
  },
  button: {
    alignItems: "center",
    backgroundColor: "rgb(33, 150, 243)",
    margin: 8,
    padding: 10,
  },
  buttonText:{
    color: "#FFFFFF",
  },
  option:{
    flex:1,
    alignItems: 'flex-start',
  }
});