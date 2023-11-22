#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(VisionCameraDynamsoftDocumentNormalizer, NSObject)

RCT_EXTERN_METHOD(initLicense:(NSString)license
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(initRuntimeSettingsFromString:(NSString *)template
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)


RCT_EXTERN_METHOD(detectFile:(NSString *)path
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(normalizeFile:(NSString *)path
                  quad:(NSDictionary *)quad
                  config:(NSDictionary *)config
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
