import DynamsoftDocumentNormalizer

@objc(VisionCameraDynamsoftDocumentNormalizer)
class VisionCameraDynamsoftDocumentNormalizer: NSObject,LicenseVerificationListener  {
    static var ddn:DynamsoftDocumentNormalizer = DynamsoftDocumentNormalizer()
    
    @objc(initRuntimeSettingsFromString:withResolver:withRejecter:)
    func initRuntimeSettingsFromString(template:String, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        do {
            try VisionCameraDynamsoftDocumentNormalizer.ddn.initRuntimeSettingsFromString(template)
            resolve(true)
        }catch {
            print("Unexpected error: \(error).")
            resolve(false)
        }
    }
    
    @objc(normalizeFile:quad:config:withResolver:withRejecter:)
    func normalizeFile(path:String,quad:[String:Any], config:[String:Any],resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        do {
            var returned_result:[String:String] = [:]
            let points = quad["points"] as! [[String:NSNumber]]
            let quadrilateral = iQuadrilateral.init()
            quadrilateral.points = convertPoints(points)
            let imageURL = URL(fileURLWithPath: path)
            let image = UIImage(contentsOfFile: imageURL.path)!
            print("image orientation: ")
            print(image.imageOrientation.rawValue)
            print("image width: ")
            print(image.size.width)
            var rotatedImage:UIImage!
            DispatchQueue.main.sync {
                rotatedImage = imageRotatedByDegrees(image:image, degrees:90, flip:false)
            }
            
            print("rotatedImage width: ")
            print(rotatedImage.size.width)
            let normalizedImageResult = try VisionCameraDynamsoftDocumentNormalizer.ddn.normalizeImage(rotatedImage, quad: quadrilateral)
            print("normalized image width: ")
            print(normalizedImageResult.image.width)
            
            if config["saveNormalizationResultAsFile"] != nil {
                if config["saveNormalizationResultAsFile"] as! Bool == true {
                    let tmpDir = NSTemporaryDirectory()
                    let timestamp = String(format: "%f", Date().timeIntervalSince1970*1000)
                    let filePath = tmpDir + "/" + timestamp + ".png"
                    do{
                        try normalizedImageResult.saveToFile(filePath)
                        returned_result["imageURL"] = filePath
                    }catch {
                        print(error)
                    }
                }
            }
            if config["includeNormalizationResultAsBase64"] != nil {
                if config["includeNormalizationResultAsBase64"] as! Bool == true {
                    do{
                        let normalizedUIImage = try normalizedImageResult.image.toUIImage()
                        let base64 = Utils.getBase64FromImage(normalizedUIImage)
                        returned_result["imageBase64"] = base64
                    }catch{
                        print(error)
                    }
                }
            }

            resolve(returned_result)
        }catch {
            print("Unexpected error: \(error).")
            resolve(false)
        }
    }
    
    func convertPoints(_ points:[[String:NSNumber]]) -> [CGPoint] {
        var CGPoints:[CGPoint] = [];
        for point in points {
            let x = point["x"]!
            let y = point["y"]!
            let cgPoint = CGPoint(x: x.intValue, y: y.intValue)
            CGPoints.append(cgPoint)
        }
        return CGPoints
    }
    
    public func imageRotatedByDegrees(image:UIImage, degrees: CGFloat, flip: Bool) -> UIImage {
        let radiansToDegrees: (CGFloat) -> CGFloat = {
            return $0 * (180.0 / CGFloat.pi)
        }
        let degreesToRadians: (CGFloat) -> CGFloat = {
            return $0 / 180.0 * CGFloat.pi
        }

        // calculate the size of the rotated view's containing box for our drawing space
        let rotatedViewBox = UIView(frame: CGRect(origin: .zero, size: image.size))
        let t = CGAffineTransform(rotationAngle: degreesToRadians(degrees));
        rotatedViewBox.transform = t
        let rotatedSize = rotatedViewBox.frame.size

        // Create the bitmap context
        UIGraphicsBeginImageContext(rotatedSize)
        let bitmap = UIGraphicsGetCurrentContext()

        // Move the origin to the middle of the image so we will rotate and scale around the center.
        bitmap?.translateBy(x: rotatedSize.width / 2.0, y: rotatedSize.height / 2.0)

        //   // Rotate the image context
        bitmap?.rotate(by: degreesToRadians(degrees))

        // Now, draw the rotated/scaled image into the context
        var yFlip: CGFloat

        if(flip){
            yFlip = CGFloat(-1.0)
        } else {
            yFlip = CGFloat(1.0)
        }

        bitmap?.scaleBy(x: yFlip, y: -1.0)
        let rect = CGRect(x: -image.size.width / 2, y: -image.size.height / 2, width: image.size.width, height: image.size.height)

        bitmap?.draw(image.cgImage!, in: rect)

        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return newImage!
    }
    
    @objc(initLicense:withResolver:withRejecter:)
    func initLicense(license:String, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        DynamsoftLicenseManager.initLicense(license, verificationDelegate: self)
        resolve(true)
    }
    
    func licenseVerificationCallback(_ isSuccess: Bool, error: Error?) {
        print(isSuccess)
    }
}
