
# vision-camera-dynamsoft-document-normalizer

A React Native Vision Camera frame processor plugin for [Dynamsoft Document Normalizer](https://www.dynamsoft.com/document-normalizer/docs/).

It can detect document boundaries and run perspective transformation to get a normalized image.

[Demo video](https://user-images.githubusercontent.com/5462205/200720562-a7b91e06-cf6c-4235-a8cd-ef200012a42a.MP4)

If you do not want to use Vision Camera, you can use the [offical React Native package](https://github.com/Dynamsoft/capture-vision-react-native-samples) by Dynamsoft.

## Versions

For vision-camera v2, use versions 0.x.

For vision-camera v3, use versions 1.x - 2.x.

For vision-camera v4, use versions >= 3.0.0.

## SDK Versions Used for Different Platforms

| Product      | Android |    iOS |
| ----------- | ----------- | -----------  | 
| Dynamsoft Document Normalizer    | 2.6       | 2.6     |

## Installation

```sh
yarn add vision-camera-dynamsoft-document-normalizer
cd ios && pod install
```

Add the plugin to your `babel.config.js`:

```js
module.exports = {
   plugins: [['react-native-worklets-core/plugin']],
    // ...
```

> Note: You have to restart metro-bundler for changes in the `babel.config.js` file to take effect.

## Usage

1. Scan documents with vision camera.
   
   ```js
   import { detect } from 'vision-camera-dynamsoft-document-normalizer';
 
   // ...
   const frameProcessor = useFrameProcessor((frame) => {
     'worklet';
     const detectionResults = detect(frame);
   }, []);
   ```
   
2. Scan documents from a file (or use `detectBase64`).

   ```ts
   let detectionResults = await detectFile(photoPath);
   ```

3. Normalize a document image with the detection result (or use `normalizeBase64`).

   ```ts
   let normalizedImageResult = await normalizeFile(photoPath, detectionResult.location,{saveNormalizationResultAsFile:true});
   ```

4. License initialization ([apply for a trial license](https://www.dynamsoft.com/customer/license/trialLicense/?product=dcv&package=cross-platform)).

   ```ts
   await initLicense("your license");
   ```

## Supported Platforms

* Android
* iOS

## Blog

[Build a Document Normalization React Native Vision Camera Plugin](https://www.dynamsoft.com/codepool/react-native-vision-camera-document-normalizer-plugin.html)

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
