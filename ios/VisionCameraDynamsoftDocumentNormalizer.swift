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
            let normalizedImageResult = try VisionCameraDynamsoftDocumentNormalizer.ddn.normalizeFile(path, quad: quadrilateral)
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
    
    
    @objc(initLicense:withResolver:withRejecter:)
    func initLicense(license:String, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        DynamsoftLicenseManager.initLicense(license, verificationDelegate: self)
        resolve(true)
    }
    
    func licenseVerificationCallback(_ isSuccess: Bool, error: Error?) {
        print(isSuccess)
    }
}
