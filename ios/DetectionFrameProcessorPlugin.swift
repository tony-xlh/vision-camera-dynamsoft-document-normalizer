//
//  DetectionFrameProcessorPlugin.swift
//  VisionCameraDynamsoftDocumentNormalizer
//
//  Created by xulihang on 2022/11/4.
//  Copyright © 2022 Facebook. All rights reserved.
//

import Foundation
import DynamsoftCaptureVisionBundle

@objc(DetectionFrameProcessorPlugin)
public class DetectionFrameProcessorPlugin: FrameProcessorPlugin {
    public override init(proxy: VisionCameraProxyHolder, options: [AnyHashable : Any]! = [:]) {
        super.init(proxy: proxy, options: options)
    }
    public override func callback(_ frame: Frame, withArguments arguments: [AnyHashable: Any]?) -> Any? {
        guard let imageBuffer = CMSampleBufferGetImageBuffer(frame.buffer) else {
            print("Failed to get CVPixelBuffer!")
            return nil
          }
        let ciImage = CIImage(cvPixelBuffer: imageBuffer)

        guard let cgImage = CIContext().createCGImage(ciImage, from: ciImage.extent) else {
            print("Failed to create CGImage!")
            return nil
        }
        var templateName = "DetectDocumentBoundaries_Default"
        if arguments != nil {
            if arguments?["template"] != nil {
                let template = arguments?["template"] as! String
                if template != "" {
                    templateName = template
                }
            }
        }
        
        var returned_results: [Any] = []
        var image = UIImage(cgImage: cgImage)
        var degree = 0.0;
        if frame.orientation == UIImage.Orientation.left {
            degree = 90.0;
        }else if frame.orientation == UIImage.Orientation.down {
            degree = 180.0;
        }

        if degree != 0.0 {
            image = DetectionFrameProcessorPlugin.rotate(image:image,degree:degree)
        }

        let capturedResult = VisionCameraDynamsoftDocumentNormalizer.cvr.captureFromImage(image, templateName: templateName)
        let results = capturedResult.items
        if results != nil {
            for result in results! {
                returned_results.append(Utils.wrapDetectionResult(result:result as! DetectedQuadResultItem))
            }
        }
        return returned_results
    }
    public static func rotate(image: UIImage, degree: CGFloat) -> UIImage {
        let radians = degree / (180.0 / .pi)
        let rotatedSize = CGRect(origin: .zero, size: image.size)
            .applying(CGAffineTransform(rotationAngle: CGFloat(radians)))
            .integral.size
        UIGraphicsBeginImageContext(rotatedSize)
        if let context = UIGraphicsGetCurrentContext() {
            let origin = CGPoint(x: rotatedSize.width / 2.0,
                                 y: rotatedSize.height / 2.0)
            context.translateBy(x: origin.x, y: origin.y)
            context.rotate(by: radians)
            image.draw(in: CGRect(x: -origin.y, y: -origin.x,
                                  width: image.size.width, height: image.size.height))
            let rotatedImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()

            return rotatedImage ?? image
        }
        return image
    }
}
