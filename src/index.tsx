import { NativeModules, Platform } from 'react-native';
import type { Frame } from 'react-native-vision-camera'

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


export function normalizeFile(url:string, config: NormalizationConfig): Promise<NormalizedImageResult> {
  return VisionCameraDynamsoftDocumentNormalizer.normalizeFile(url, config);
}

export function normalizeBase64(base64:string, config: NormalizationConfig): Promise<NormalizedImageResult> {
  return VisionCameraDynamsoftDocumentNormalizer.normalizeBase64(base64, config);
}

export function normalize(frame: Frame, config: NormalizationConfig): NormalizedImageResult {
  'worklet'
  // @ts-ignore
  // eslint-disable-next-line no-undef
  return __normalize(frame, config)
}

export function detectBase64(base64:string, config: DetectionConfig): Promise<number> {
  return VisionCameraDynamsoftDocumentNormalizer.detectBase64(base64, config);
}

export function detect(frame: Frame, config: DetectionConfig): DetectedQuadResult {
  'worklet'
  // @ts-ignore
  // eslint-disable-next-line no-undef
  return __detect(frame, config)
}

export interface NormalizationConfig{
  license?: string;
  saveNormalizationResult?: boolean;
  includeNormalizationResultAsBase64: boolean;
}

export interface DetectionConfig{
  license?: string;
}

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
  coordinate: [number, number];
}

export interface Quadrilateral {
  points: [Point, Point, Point, Point];
}
