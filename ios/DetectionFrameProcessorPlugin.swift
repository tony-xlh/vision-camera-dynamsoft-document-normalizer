//
//  DetectionFrameProcessorPlugin.swift
//  VisionCameraDynamsoftDocumentNormalizer
//
//  Created by xulihang on 2022/11/4.
//  Copyright © 2022 Facebook. All rights reserved.
//

import Foundation

@objc(DetectionFrameProcessorPlugin)
public class DetectionFrameProcessorPlugin: NSObject, FrameProcessorPluginBase {
    private static let context = CIContext(options: nil)
    @objc
    public static func callback(_ frame: Frame!, withArgs _: [Any]!) -> Any! {
        guard let imageBuffer = CMSampleBufferGetImageBuffer(frame.buffer) else {
            print("Failed to get CVPixelBuffer!")
            return nil
          }
        let ciImage = CIImage(cvPixelBuffer: imageBuffer)

        guard let cgImage = context.createCGImage(ciImage, from: ciImage.extent) else {
            print("Failed to create CGImage!")
            return nil
        }
        
        var returned_results: [Any] = []
        let image = UIImage(cgImage: cgImage)
        
        let results = try? VisionCameraDynamsoftDocumentNormalizer.ddn.detectQuadFromImage(image)
        if results != nil {
            for result in results! {
                returned_results.append(Utils.wrapDetectionResult(result:result))
            }
        }
        return returned_results
    }
}
