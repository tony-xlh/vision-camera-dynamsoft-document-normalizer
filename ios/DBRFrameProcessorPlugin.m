//
//  VisionCameraDBRPlugin.m
//  vision-camera-dynamsoft-document-normalizer
//
//  Created by xulihang on 2022/1/26.
//

#import <Foundation/Foundation.h>
#import <VisionCamera/FrameProcessorPlugin.h>
#import <VisionCamera/FrameProcessorPluginRegistry.h>
#import <VisionCamera/Frame.h>
#import "VisionCameraDynamsoftDocumentNormalizer-Swift.h"

@interface DBRFrameProcessorPlugin (FrameProcessorPluginLoader)
@end

@implementation DBRFrameProcessorPlugin (FrameProcessorPluginLoader)

+ (void)load
{
  [FrameProcessorPluginRegistry addFrameProcessorPlugin:@"decode"
                                        withInitializer:^FrameProcessorPlugin* (NSDictionary* options) {
    return [[DBRFrameProcessorPlugin alloc] init];
  }];
}

@end
