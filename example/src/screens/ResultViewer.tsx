import React, { useEffect, useRef, useState } from "react";
import { Image, SafeAreaView, StyleSheet, Text, TouchableOpacity, View} from "react-native";
import RadioForm from 'react-native-simple-radio-button';
import { DetectedQuadResult, normalizeFile } from "vision-camera-dynamsoft-document-normalizer";

const radio_props = [
  {label: 'Binary', value: 0 },
  {label: 'Gray', value: 1 },
  {label: 'Color', value: 2 }
];

export default function ResultViewerScreen({route, navigation}) {
  const [normalizedImagePath, setNormalizedImagePath] = useState<undefined|string>(undefined);

  useEffect(() => {
    console.log(route.params);
  }, []);

  const save = () => {
    console.log("save");
  }

  const normalize = async (value:number) => {
    console.log(value);
    let detectionResult:DetectedQuadResult = route.params.detectionResult;
    let photoPath = route.params.photoPath;
    let normalizedImageResult = await normalizeFile(photoPath, detectionResult.location,{saveNormalizationResultAsFile:true});
    console.log(normalizedImageResult);
  }


  return (
    <SafeAreaView style={styles.container}>
      {normalizedImagePath && (
        <Image
          style={StyleSheet.absoluteFill}
          source={{uri:"file://"+normalizedImagePath}}
        />
      )}
      <View style={styles.control}>
        <View style={styles.buttonContainer}>
          <TouchableOpacity onPress={save} style={styles.button}>
            <Text style={{fontSize: 15, color: "black", alignSelf: "center"}}>Save</Text>
          </TouchableOpacity>
        </View>
        <View style={styles.radioContainer}>
          <RadioForm
            radio_props={radio_props}
            initial={0}
            formHorizontal={true}
            labelHorizontal={false}
            
            onPress={(value) => {normalize(value)}}
          />
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
});