import React, { useEffect, useState } from "react";
import { Image, SafeAreaView, StyleSheet} from "react-native";

export default function ResultViewerScreen({route, navigation}) {
  const [normalizedImagePath, setNormalizedImagePath] = useState<undefined|string>(undefined);

  useEffect(() => {
    console.log(route.params);
  }, []);

  const normalize = () => {

  }


  return (
    <SafeAreaView style={styles.container}>
      {normalizedImagePath && (
        <Image
          style={StyleSheet.absoluteFill}
          source={{uri:"file://"+normalizedImagePath}}
        />  
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex:1,
  },
});