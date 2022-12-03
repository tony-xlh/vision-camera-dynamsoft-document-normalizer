import React, { useEffect, useState } from "react";
import { Image, SafeAreaView, StyleSheet, Text, TouchableOpacity, View} from "react-native";
import * as DDN from "vision-camera-dynamsoft-document-normalizer";
import type { DetectedQuadResult } from "vision-camera-dynamsoft-document-normalizer";
import Svg, { Polygon } from "react-native-svg";
import type { PhotoFile } from "react-native-vision-camera";


export default function CropperScreen({route, navigation}) {
  const [detectionResult,setDetectionResult] = useState<undefined|DetectedQuadResult>();
  const [photoPath,setPhotoPath] = useState<string|undefined>();
  const [viewBox,setViewBox] = useState<string|undefined>();
  const [pointsData,setPointsData] = useState<string|undefined>();
  useEffect(() => {
    if (route.params.photo) {
      let photo:PhotoFile = route.params.photo;
      setPhotoPath(photo.path);
      setViewBox("0 0 "+photo.width+" "+photo.height);
      detectFile(photo.path);
    }
  }, []);

  const detectFile = async (path:string) => {
    let results = await DDN.detectFile(path);
    if (results.length>0) {
      setDetectionResult(results[0]);
    }
  }

  useEffect(() => {
    updatePointsData();
  }, [detectionResult]);

  const updatePointsData = () => {
    if (detectionResult) {
      let location = detectionResult.location;
      let data = location.points[0].x + "," + location.points[0].y + " ";
      data = data + location.points[1].x + "," + location.points[1].y +" ";
      data = data + location.points[2].x + "," + location.points[2].y +" ";
      data = data + location.points[3].x + "," + location.points[3].y;
      setPointsData(data)
    }
  }

  const retake = () => {
    navigation.goBack();
  }

  const okay = () => {
    navigation.navigate(
      {
        params: {photoPath:photoPath,detectionResult:detectionResult},
        name: "ResultViewer"
      }
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      {photoPath != undefined && 
      pointsData != undefined && 
      viewBox != undefined && (
        <>
          <Image
            style={StyleSheet.absoluteFill}
            source={{uri:"file://"+photoPath}}
          />
          <Svg preserveAspectRatio='xMidYMid slice' style={StyleSheet.absoluteFill} viewBox={viewBox}>
            <Polygon
              points={pointsData}
              fill="lime"
              stroke="green"
              opacity="0.5"
              strokeWidth="1"
            />
          </Svg>
        </>
      )}
      
      <View style={styles.control}>
        <View style={{flex:0.5}}>
          <TouchableOpacity onPress={retake} style={styles.button}>
            <Text style={{fontSize: 15, color: "black", alignSelf: "center"}}>Retake</Text>
          </TouchableOpacity>
        </View>
        <View style={{flex:0.5}}>
          <TouchableOpacity onPress={okay} style={styles.button}>
            <Text style={{fontSize: 15, color: "black", alignSelf: "center"}}>Okay</Text>
          </TouchableOpacity>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex:1,
  },
  control:{
    flexDirection:"row",
    position: 'absolute',
    bottom: 0,
    height: "15%",
    width:"100%",
    alignSelf:"flex-start",
    alignItems: 'center',
  },
  radioContainer:{
    flex: 0.7,
    padding: 5,
    margin: 3,
  },
  buttonContainer:{
    flex: 0.3,
    padding: 5,
    margin: 3,
  },
  button: {
    backgroundColor: "ghostwhite",
    borderColor:"black", 
    borderWidth:2, 
    borderRadius:5,
    padding: 8,
    margin: 3,
  },
  image: {
    resizeMode:"contain",
  }
});