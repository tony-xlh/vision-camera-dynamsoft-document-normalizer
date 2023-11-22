//
//  Utils.swift
//  VisionCameraDynamsoftDocumentNormalizer
//
//  Created by xulihang on 2022/11/4.
//  Copyright © 2022 Facebook. All rights reserved.
//

import Foundation
import DynamsoftDocumentNormalizer

class Utils {
    
    static public func convertBase64ToImage(_ imageStr:String) ->UIImage?{
        if let data: NSData = NSData(base64Encoded: imageStr, options:NSData.Base64DecodingOptions.ignoreUnknownCharacters)
        {
            if let image: UIImage = UIImage(data: data as Data)
            {
                return image
            }
        }
        return nil
    }
    
    static func getBase64FromImage(_ image:UIImage) -> String{
        let dataTmp = image.jpegData(compressionQuality: 100)
        if let data = dataTmp {
            return data.base64EncodedString()
        }
        return ""
    }
    
    
    static func wrapDetectionResult (result:iDetectedQuadResult) -> [String: Any] {
        var dict: [String: Any] = [:]
        dict["confidenceAsDocumentBoundary"] = result.confidenceAsDocumentBoundary
        dict["location"] = wrapLocation(location:result.location)
        return dict
    }
    
    static private func wrapLocation (location:iQuadrilateral?) -> [String: Any] {
        var dict: [String: Any] = [:]
        var points: [[String:CGFloat]] = []
        let CGPoints = location!.points as! [CGPoint]
        for point in CGPoints {
            var pointDict: [String:CGFloat] = [:]
            pointDict["x"] = point.x
            pointDict["y"] = point.y
            points.append(pointDict)
        }
        dict["points"] = points
        return dict
    }
    
}
