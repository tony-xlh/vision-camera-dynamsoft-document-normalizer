import { NativeModules, Platform } from 'react-native';
import {VisionCameraProxy,  Frame} from 'react-native-vision-camera';


const LINKING_ERROR =
  `The package 'vision-camera-dynamsoft-document-normalizer' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const VisionCameraDynamsoftDocumentNormalizer = NativeModules.VisionCameraDynamsoftDocumentNormalizer  ? NativeModules.VisionCameraDynamsoftDocumentNormalizer  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

/**
 * Init the license of Dynamsoft Document Normalizer
 */
export function initLicense(license:string): Promise<boolean> {
  return VisionCameraDynamsoftDocumentNormalizer.initLicense(license);
}

/**
 * Init the runtime settings from a JSON template
 */
export function initRuntimeSettingsFromString(template:string): Promise<boolean> {
  return VisionCameraDynamsoftDocumentNormalizer.initRuntimeSettingsFromString(template);
}

/**
 * Detect documents in an image file
 */
export function detectFile(url:string): Promise<DetectedQuadResult[]> {
  return VisionCameraDynamsoftDocumentNormalizer.detectFile(url);
}

/**
 * Normalize an image file
 */
export function normalizeFile(url:string, quad:Quadrilateral, config: NormalizationConfig): Promise<NormalizedImageResult> {
  return VisionCameraDynamsoftDocumentNormalizer.normalizeFile(url, quad, config);
}

/**
 * Rotate an image file. Android only.
 */
 export function rotateFile(url:string, degrees:number): Promise<NormalizedImageResult> {
  return VisionCameraDynamsoftDocumentNormalizer.rotateFile(url, degrees);
}

/**
 * Config of whether to save the normalized as a file and base64.
 */
export interface NormalizationConfig{
  saveNormalizationResultAsFile?: boolean;
  includeNormalizationResultAsBase64?: boolean;
}

/**
 * Normalization result containing the image path or base64
 */
export interface NormalizedImageResult {
  imageURL?: string;
  imageBase64?: string;
}

export interface DetectedQuadResult {
  location: Quadrilateral;
  confidenceAsDocumentBoundary: number;
}

export interface Point {
  x:number;
  y:number;
}

export interface Quadrilateral {
  points: [Point, Point, Point, Point];
}

export interface Rect {
  left:number;
  right:number;
  top:number;
  bottom:number;
  width:number;
  height:number;
}

const plugin = VisionCameraProxy.initFrameProcessorPlugin('detect')

/**
 * Detect documents from the camera preview
 */
export function detect(frame: Frame): DetectedQuadResult[] {
  'worklet'
  if (plugin == null) throw new Error('Failed to load Frame Processor Plugin "decode"!')
  return plugin.call(frame) as any;
}
