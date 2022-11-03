import React from "react";
import { SafeAreaView, StyleSheet, Text, TouchableOpacity } from "react-native";

export default function HomeScreen({route, navigation}) {
  const onPressed = (target:string) => {
    navigation.navigate(
      {
        name: target
      }
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <TouchableOpacity
        style={styles.button}
        onPress={() => onPressed("Scanner")}
      >
        <Text style={styles.buttonText}>Live Scan</Text>
      </TouchableOpacity>
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