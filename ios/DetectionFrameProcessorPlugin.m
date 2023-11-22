//
//  DetectionFrameProcessorPlugin.m
//  VisionCameraDynamsoftDocumentNormalizer
//
//  Created by xulihang on 2022/11/6.
//  Copyright © 2022 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <VisionCamera/FrameProcessorPlugin.h>
#import <VisionCamera/FrameProcessorPluginRegistry.h>
#import <VisionCamera/Frame.h>
#import "VisionCameraDynamsoftBarcodeReader-Swift.h"

@interface DetectionFrameProcessorPlugin (FrameProcessorPluginLoader)
@end

@implementation DetectionFrameProcessorPlugin (FrameProcessorPluginLoader)

+ (void)load
{
  [FrameProcessorPluginRegistry addFrameProcessorPlugin:@"detect"
                                        withInitializer:^FrameProcessorPlugin* (NSDictionary* options) {
    return [[DetectionFrameProcessorPlugin alloc] init];
  }];
}

@end
