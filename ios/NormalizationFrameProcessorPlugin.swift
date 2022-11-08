//
//  NormalizationFrameProcessorPlugin.swift
//  VisionCameraDynamsoftDocumentNormalizer
//
//  Created by xulihang on 2022/11/4.
//  Copyright © 2022 Facebook. All rights reserved.
//

import Foundation

@objc(NormalizationFrameProcessorPlugin)
public class NormalizationFrameProcessorPlugin: NSObject, FrameProcessorPluginBase {
    private static let context = CIContext(options: nil)
    @objc
    public static func callback(_ frame: Frame!, withArgs args: [Any]!) -> Any! {
        guard let config = getConfig(withArgs: args) else {
            print("Failed to get config!")
            return nil
        }
        guard let imageBuffer = CMSampleBufferGetImageBuffer(frame.buffer) else {
            print("Failed to get CVPixelBuffer!")
            return nil
          }
        let ciImage = CIImage(cvPixelBuffer: imageBuffer)

        guard let cgImage = context.createCGImage(ciImage, from: ciImage.extent) else {
            print("Failed to create CGImage!")
            return nil
        }
        
        var returned_result: [String:Any] = [:]
        let image = UIImage(cgImage: cgImage)
        
        let results = try? VisionCameraDynamsoftDocumentNormalizer.ddn.detectQuadFromImage(image)
        if results != nil {
            if results?.count ?? 0 > 0 {
                let normalizedImageResult = try? VisionCameraDynamsoftDocumentNormalizer.ddn.normalizeImage(image, quad: results![0].location)
                if config["saveNormalizationResultAsFile"] != nil {
                    if config["saveNormalizationResultAsFile"] as! Bool == true {
                        let tmpDir = NSTemporaryDirectory()
                        let timestamp = String(format: "%f", Date().timeIntervalSince1970*1000)
                        let filePath = tmpDir + "/" + timestamp + ".png"
                        do{
                            try normalizedImageResult?.saveToFile(filePath)
                            returned_result["imageURL"] = filePath
                        }catch {
                            print(error)
                        }
                    }
                }
                if config["includeNormalizationResultAsBase64"] != nil {
                    if config["includeNormalizationResultAsBase64"] as! Bool == true {
                        do{
                            let normalizedUIImage = try normalizedImageResult?.image.toUIImage()
                            let base64 = Utils.getBase64FromImage(normalizedUIImage!)
                            returned_result["imageBase64"] = base64
                        }catch{
                            print(error)
                        }
                    }
                }
            }
        }
        return returned_result
    }
    
    static func getConfig(withArgs args: [Any]!) -> [String:Any]! {
        if args.count>0 {
            let config = args[0] as? [String: Any]
            return config
        }
        return nil
    }
}
