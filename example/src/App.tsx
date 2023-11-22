import * as React from 'react';
import { Alert, SafeAreaView, StyleSheet, Text, TouchableOpacity } from "react-native";
import Scanner from './components/Scanner';
import type { PhotoFile } from 'react-native-vision-camera';
import type * as DDN from "vision-camera-dynamsoft-document-normalizer";
import Cropper from './components/Cropper';
import ResultViewer from './components/ResultViewer';


export default function App() {
  const [showScanner,setShowScanner] = React.useState(false);
  const [showCropper,setShowCropper] = React.useState(false);
  const [showResultViewer,setShowResultViewer] = React.useState(false);
  const [photoTaken,setPhotoTaken] = React.useState<PhotoFile|undefined>();
  const [photoPath,setPhotoPath] = React.useState<string>("");
  const [points,setPoints] = React.useState<DDN.Point[]>([]);
  const onPressed = () => {
    setShowScanner(true);
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
        <TouchableOpacity
          style={styles.button}
          onPress={() => onPressed()}
        >
          <Text style={styles.buttonText}>Scan Document</Text>
        </TouchableOpacity>
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
});