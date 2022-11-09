//
//  BitmapUtils.swift
//  VisionCameraDynamsoftDocumentNormalizer
//
//  Created by xulihang on 2022/11/4.
//  Copyright © 2022 Facebook. All rights reserved.
//

import Foundation

class BitmapUtils {
    
    static public func normalizedImage(_ image:UIImage) -> UIImage {
        if image.imageOrientation == UIImage.Orientation.up {
            return image
        }
        UIGraphicsBeginImageContextWithOptions(image.size, false, image.scale)
        image.draw(in: CGRect(x:0,y:0,width:image.size.width,height:image.size.height))
        let normalized = UIGraphicsGetImageFromCurrentImageContext()!
        UIGraphicsEndImageContext();
        return normalized
    }
}

