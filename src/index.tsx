import { NativeModules, Platform } from 'react-native';
import {VisionCameraProxy,  type Frame} from 'react-native-vision-camera';


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
export function detectFile(url:string,template?:string): Promise<DetectedQuadResult[]> {
  return VisionCameraDynamsoftDocumentNormalizer.detectFile(url,template ?? "");
}

/**
 * Normalize an image file
 */
export function normalizeFile(url:string, quad:Quadrilateral, config: NormalizationConfig, template?:string): Promise<NormalizedImageResult> {
  return VisionCameraDynamsoftDocumentNormalizer.normalizeFile(url, quad, config, template ?? "");
}

/**
 * Detect documents in an image file encoded as base64
 */
export function detectBase64(base64:string,template?:string): Promise<DetectedQuadResult[]> {
  return VisionCameraDynamsoftDocumentNormalizer.detectBase64(base64,template ?? "");
}

/**
 * Normalize an image file encoded as base64
 */
export function normalizeBase64(base64:string, quad:Quadrilateral, config: NormalizationConfig, template?:string): Promise<NormalizedImageResult> {
  return VisionCameraDynamsoftDocumentNormalizer.normalizeBase64(base64, quad, config, template ?? "");
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
  area: number;
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

const plugin = VisionCameraProxy.initFrameProcessorPlugin('detect',{})

/**
 * Detect documents from the camera preview
 */
export function detect(frame: Frame,template?: string): Record<string,DetectedQuadResult> {
  'worklet'
  if (plugin == null) throw new Error('Failed to load Frame Processor Plugin "detect"!')
  if (template) {
    let record:Record<string,any> = {};
    record["template"] = template;
    return plugin.call(frame,record) as any;
  }else{
    return plugin.call(frame) as any;
  }
  
}
