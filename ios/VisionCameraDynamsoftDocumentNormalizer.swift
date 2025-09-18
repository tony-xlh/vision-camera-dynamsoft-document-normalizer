import DynamsoftCaptureVisionBundle

@objc(VisionCameraDynamsoftDocumentNormalizer)
class VisionCameraDynamsoftDocumentNormalizer: NSObject,LicenseVerificationListener  {
    
    static var cvr:CaptureVisionRouter = CaptureVisionRouter()
    var licenseResolveBlock:RCTPromiseResolveBlock!;
    var licenseRejectBlock:RCTPromiseRejectBlock!;
    
    override init(){
        super.init()
    }
    
    @objc(initRuntimeSettingsFromString:withResolver:withRejecter:)
    func initRuntimeSettingsFromString(template:String, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        do {
            try VisionCameraDynamsoftDocumentNormalizer.cvr.initSettings(template)
            resolve(true)
        }catch {
            print("Unexpected error: \(error).")
            resolve(false)
        }
    }
    
    @objc(detectFile:template:withResolver:withRejecter:)
    func detectFile(path:String,template:String,resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        detectFileImpl(path,template,resolve,reject)
    }
    
    @objc(detectBase64:template:withResolver:withRejecter:)
    func detectBase64(base64:String,template:String,resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        detectFileImpl(base64,template,resolve,reject)
    }
    
    func detectFileImpl(_ str:String,_ template:String,_ resolve:RCTPromiseResolveBlock,_ reject:RCTPromiseRejectBlock) -> Void {
        
        var returned_results: [Any] = []
        let imageURL = URL(fileURLWithPath: str.replacingOccurrences(of: "file://", with: ""))
        let fileExists = FileManager.default.fileExists(atPath: imageURL.path)
        var image:UIImage
        if (fileExists) {
            image = UIImage(contentsOfFile: imageURL.path)!
        }else{
            image = Utils.convertBase64ToImage(str)!
        }

        image = BitmapUtils.normalizedImage(image)
        var templateName:String
        if template != "" {
            templateName = template
        }else{
            templateName = "DetectDocumentBoundaries_Default"
        }
        
        let capturedResult =  VisionCameraDynamsoftDocumentNormalizer.cvr.captureFromImage(image, templateName: templateName)
        let results = capturedResult.items
        
        if results != nil {
            for result in results! {
                returned_results.append(Utils.wrapDetectionResult(result:result as! DetectedQuadResultItem))
            }
        }

        resolve(returned_results)
    }
    
    @objc(normalizeFile:quad:config:template:withResolver:withRejecter:)
    func normalizeFile(path:String,quad:[String:Any], config:[String:Any],template:String,resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        normalizeFileImpl(path,quad,config,template,resolve,reject)
    }
    
    @objc(normalizeBase64:quad:config:template:withResolver:withRejecter:)
    func normalizeBase64(base64:String,quad:[String:Any], config:[String:Any],template:String,resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        normalizeFileImpl(base64,quad,config,template,resolve,reject)
    }
    
    func normalizeFileImpl(_ str:String,_ quad:[String:Any], _ config:[String:Any],_ template:String,_ resolve:RCTPromiseResolveBlock,_ reject:RCTPromiseRejectBlock) -> Void {
        var templateName:String
        if template != "" {
            templateName = template
        }else{
            templateName = "NormalizeDocument_Default"
        }
        var returned_result:[String:String] = [:]

        let imageURL = URL(fileURLWithPath: str.replacingOccurrences(of: "file://", with: ""))
        let fileExists = FileManager.default.fileExists(atPath: imageURL.path)
        var image:UIImage
        if (fileExists) {
            image = UIImage(contentsOfFile: imageURL.path)!
        }else{
            image = Utils.convertBase64ToImage(str)!
        }
        
        image = BitmapUtils.normalizedImage(image)
        let points = quad["points"] as! [[String:NSNumber]]
        let quad = Quadrilateral.init(pointArray: convertPoints(points))
        let settings = try? VisionCameraDynamsoftDocumentNormalizer.cvr.getSimplifiedSettings(templateName)
        settings?.roi = quad
        settings?.roiMeasuredInPercentage = false
        try? VisionCameraDynamsoftDocumentNormalizer.cvr.updateSettings(templateName, settings: settings!)
        
        let capturedResult =  VisionCameraDynamsoftDocumentNormalizer.cvr.captureFromImage(image, templateName: templateName)
        let results = capturedResult.items
        if results != nil {
            if results?.count ?? 0 > 0 {
                let normalizedImageResult:DeskewedImageResultItem = results![0] as! DeskewedImageResultItem
                let normalizedUIImage = try? normalizedImageResult.imageData?.toUIImage()
                if config["saveNormalizationResultAsFile"] != nil {
                    if config["saveNormalizationResultAsFile"] as! Bool == true {
                        let url = FileManager.default.temporaryDirectory
                                                .appendingPathComponent(UUID().uuidString)
                                                .appendingPathExtension("jpeg")
                        try? normalizedUIImage?.jpegData(compressionQuality: 1.0)?.write(to: url)
                        returned_result["imageURL"] = url.path
                    }
                }
                if config["includeNormalizationResultAsBase64"] != nil {
                    if config["includeNormalizationResultAsBase64"] as! Bool == true {
                        let base64 = Utils.getBase64FromImage(normalizedUIImage!)
                        returned_result["imageBase64"] = base64
                    }
                }
            }
        }
        resolve(returned_result)
    }
    
  func convertPoints(_ points: [[String: NSNumber]]) -> [NSValue] {
      var nsValues: [NSValue] = []
      
      for point in points {
          let x = point["x"]!
          let y = point["y"]!
          let intX = x.intValue
          let intY = y.intValue
          let cgPoint = CGPoint(x: intX, y: intY)
          let nsValue = NSValue(cgPoint: cgPoint)
          nsValues.append(nsValue)
      }
      
      return nsValues
   }
    
    @objc(initLicense:withResolver:withRejecter:)
    func initLicense(license:String, resolve:@escaping RCTPromiseResolveBlock,reject:@escaping RCTPromiseRejectBlock) -> Void {
        LicenseManager.initLicense(license, verificationDelegate: self)
        licenseResolveBlock = resolve;
        licenseRejectBlock = reject;
    }
    
    func onLicenseVerified(_ isSuccess: Bool, error: Error?) {
        var msg:String? = nil
        if(error != nil)
        {
            let err = error as NSError?
            msg = err!.userInfo[NSUnderlyingErrorKey] as? String
            print("Server license verify failed: ", msg ?? "")
        }
        licenseResolveBlock(isSuccess);
    }
}
