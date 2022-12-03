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
    
    @objc(detectFile:withResolver:withRejecter:)
    func detectFile(path:String,resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {

        var returned_results: [Any] = []
        
        let imageURL = URL(fileURLWithPath: path)
        var image = UIImage(contentsOfFile: imageURL.path)!
        image = BitmapUtils.normalizedImage(image)
        
        let results = try? VisionCameraDynamsoftDocumentNormalizer.ddn.detectQuadFromImage(image)
        
        if results != nil {
            for result in results! {
                returned_results.append(Utils.wrapDetectionResult(result:result))
            }
        }

        resolve(returned_results)

    }
    
    @objc(normalizeFile:quad:config:withResolver:withRejecter:)
    func normalizeFile(path:String,quad:[String:Any], config:[String:Any],resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        do {
            var returned_result:[String:String] = [:]
            
            let imageURL = URL(fileURLWithPath: path)
            var image = UIImage(contentsOfFile: imageURL.path)!
            image = BitmapUtils.normalizedImage(image)
            let points = quad["points"] as! [[String:NSNumber]]
            let quadrilateral = iQuadrilateral.init()
            quadrilateral.points = convertPoints(points)
            
            let bpp = image.cgImage?.bitsPerPixel
            var pixelFormat:EnumImagePixelFormat
            switch (bpp) {
               case 1:
                pixelFormat = EnumImagePixelFormat.binary
                break;
               case 8:
                pixelFormat = EnumImagePixelFormat.grayScaled
                break;
               case 32:
                pixelFormat = EnumImagePixelFormat.ARGB_8888
                print("ARGB888")
                break;
               case 48:
                pixelFormat = EnumImagePixelFormat.RGB_161616;
                break;
               case 64:
                pixelFormat = EnumImagePixelFormat.ARGB_16161616;
                break;
               default:
                pixelFormat = EnumImagePixelFormat.RGB_888;
                print("RGB888")
                break;
            }
            
            let data = iImageData.init()
            data.bytes = image.cgImage?.dataProvider?.data as! Data
            data.orientation = 0
            data.stride = image.cgImage!.bytesPerRow
            data.width = image.cgImage!.width
            data.height = image.cgImage!.height
            data.format = pixelFormat
            
            let normalizedImageResult = try VisionCameraDynamsoftDocumentNormalizer.ddn.normalizeBuffer(data, quad: quadrilateral)
            //print("normalized image width: ")
            //print(normalizedImageResult.image.width)
            
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
            let intX = x.intValue
            let intY = y.intValue
            let cgPoint = CGPoint(x: intX, y: intY)
            CGPoints.append(cgPoint)
        }
        return CGPoints
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
