# vision-camera-dynamsoft-document-normalizer

A React Native Vision Camera frame processor plugin for [Dynamsoft Document Normalizer](https://www.dynamsoft.com/document-normalizer/docs/).

## Supported Platforms

* Android
* iOS (WIP)

## Installation

```sh
npm install vision-camera-dynamsoft-document-normalizer
```

make sure you correctly setup react-native-reanimated and add this to your `babel.config.js`

```js
[
  'react-native-reanimated/plugin',
  {
    globals: ['__detect','__detectAndNormalize'],
  },
]
```


## Usage

```js
import * as DDN from "vision-camera-dynamsoft-document-normalizer";
import type { DetectedQuadResult, Point, Quadrilateral } from 'vision-camera-dynamsoft-document-normalizer';

export default function App() {

  useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'authorized');
      let result = await DDN.initLicense("DLS2eyJoYW5kc2hha2VDb2RlIjoiMjAwMDAxLTE2NDk4Mjk3OTI2MzUiLCJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSIsInNlc3Npb25QYXNzd29yZCI6IndTcGR6Vm05WDJrcEQ5YUoifQ=="); // init license. Apply for a 30-day trial license here: https://www.dynamsoft.com/customer/license/trialLicense/?product=ddn
    })();
  }, []);

  const frameProcessor = useFrameProcessor((frame) => {
    'worklet'
    const detectionResults = DDN.detect(frame);
  }, [])
  
  const normalizeImage = (photoPath:string, detectionResult:DetectedQuadResult) => {
    let normalizedImageResult = await DDN.normalizeFile(photoPath, detectionResult.location,{saveNormalizationResultAsFile:true});
  }
}

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
