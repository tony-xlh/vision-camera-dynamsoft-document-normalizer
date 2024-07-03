import DynamsoftCore
import DynamsoftLicense
import DynamsoftCaptureVisionRouter
import DynamsoftDocumentNormalizer

@objc(VisionCameraDynamsoftDocumentNormalizer)
class VisionCameraDynamsoftDocumentNormalizer: NSObject,LicenseVerificationListener  {
    
    static var cvr:CaptureVisionRouter = CaptureVisionRouter()
    var licenseResolveBlock:RCTPromiseResolveBlock!;
    var licenseRejectBlock:RCTPromiseRejectBlock!;
    
    override init(){
        super.init()
        loadTemplate()
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
    
    func loadTemplate(){
        try? VisionCameraDynamsoftDocumentNormalizer.cvr.initSettings("{\"CaptureVisionTemplates\": [{\"Name\": \"Default\"},{\"Name\": \"DetectDocumentBoundaries_Default\",\"ImageROIProcessingNameArray\": [\"roi-detect-document-boundaries\"]},{\"Name\": \"DetectAndNormalizeDocument_Binary\",\"ImageROIProcessingNameArray\": [\"roi-detect-and-normalize-document-binary\"]},{\"Name\": \"DetectAndNormalizeDocument_Gray\",\"ImageROIProcessingNameArray\": [\"roi-detect-and-normalize-document-gray\"]},{\"Name\": \"DetectAndNormalizeDocument_Color\",\"ImageROIProcessingNameArray\": [\"roi-detect-and-normalize-document-color\"]},{\"Name\": \"NormalizeDocument_Binary\",\"ImageROIProcessingNameArray\": [\"roi-normalize-document-binary\"]},{\"Name\": \"NormalizeDocument_Gray\",\"ImageROIProcessingNameArray\": [\"roi-normalize-document-gray\"]},{\"Name\": \"NormalizeDocument_Color\",\"ImageROIProcessingNameArray\": [\"roi-normalize-document-color\"]}],\"TargetROIDefOptions\": [{\"Name\": \"roi-detect-document-boundaries\",\"TaskSettingNameArray\": [\"task-detect-document-boundaries\"]},{\"Name\": \"roi-detect-and-normalize-document-binary\",\"TaskSettingNameArray\": [\"task-detect-and-normalize-document-binary\"]},{\"Name\": \"roi-detect-and-normalize-document-gray\",\"TaskSettingNameArray\": [\"task-detect-and-normalize-document-gray\"]},{\"Name\": \"roi-detect-and-normalize-document-color\",\"TaskSettingNameArray\": [\"task-detect-and-normalize-document-color\"]},{\"Name\": \"roi-normalize-document-binary\",\"TaskSettingNameArray\": [\"task-normalize-document-binary\"]},{\"Name\": \"roi-normalize-document-gray\",\"TaskSettingNameArray\": [\"task-normalize-document-gray\"]},{\"Name\": \"roi-normalize-document-color\",\"TaskSettingNameArray\": [\"task-normalize-document-color\"]}],\"DocumentNormalizerTaskSettingOptions\": [{\"Name\": \"task-detect-and-normalize-document-binary\",\"ColourMode\": \"ICM_BINARY\",\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-detect-and-normalize\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-detect-and-normalize\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-detect-and-normalize\"}]},{\"Name\": \"task-detect-and-normalize-document-gray\",\"ColourMode\": \"ICM_GRAYSCALE\",\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-detect-and-normalize\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-detect-and-normalize\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-detect-and-normalize\"}]},{\"Name\": \"task-detect-and-normalize-document-color\",\"ColourMode\": \"ICM_COLOUR\",\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-detect-and-normalize\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-detect-and-normalize\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-detect-and-normalize\"}]},{\"Name\": \"task-detect-document-boundaries\",\"TerminateSetting\": {\"Section\": \"ST_DOCUMENT_DETECTION\"},\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-detect\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-detect\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-detect\"}]},{\"Name\": \"task-normalize-document-binary\",\"StartSection\": \"ST_DOCUMENT_NORMALIZATION\",\"ColourMode\": \"ICM_BINARY\",\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-normalize\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-normalize\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-normalize\"}]},{\"Name\": \"task-normalize-document-gray\",\"ColourMode\": \"ICM_GRAYSCALE\",\"StartSection\": \"ST_DOCUMENT_NORMALIZATION\",\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-normalize\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-normalize\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-normalize\"}]},{\"Name\": \"task-normalize-document-color\",\"ColourMode\": \"ICM_COLOUR\",\"StartSection\": \"ST_DOCUMENT_NORMALIZATION\",\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-normalize\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-normalize\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-normalize\"}]}],\"ImageParameterOptions\": [{\"Name\": \"ip-detect-and-normalize\",\"BinarizationModes\": [{\"Mode\": \"BM_LOCAL_BLOCK\",\"BlockSizeX\": 0,\"BlockSizeY\": 0,\"EnableFillBinaryVacancy\": 0}],\"TextDetectionMode\": {\"Mode\": \"TTDM_WORD\",\"Direction\": \"HORIZONTAL\",\"Sensitivity\": 7}},{\"Name\": \"ip-detect\",\"BinarizationModes\": [{\"Mode\": \"BM_LOCAL_BLOCK\",\"BlockSizeX\": 0,\"BlockSizeY\": 0,\"EnableFillBinaryVacancy\": 0,\"ThresholdCompensation\": 7}],\"TextDetectionMode\": {\"Mode\": \"TTDM_WORD\",\"Direction\": \"HORIZONTAL\",\"Sensitivity\": 7},\"ScaleDownThreshold\": 512},{\"Name\": \"ip-normalize\",\"BinarizationModes\": [{\"Mode\": \"BM_LOCAL_BLOCK\",\"BlockSizeX\": 0,\"BlockSizeY\": 0,\"EnableFillBinaryVacancy\": 0}],\"TextDetectionMode\": {\"Mode\": \"TTDM_WORD\",\"Direction\": \"HORIZONTAL\",\"Sensitivity\": 7}}]}")
    }
    
    @objc(detectFile:template:withResolver:withRejecter:)
    func detectFile(path:String,template:String,resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {

        var returned_results: [Any] = []
        
        let imageURL = URL(fileURLWithPath: path.replacingOccurrences(of: "file://", with: ""))
        var image = UIImage(contentsOfFile: imageURL.path)!
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

        var templateName:String
        if template != "" {
            templateName = template
        }else{
            templateName = "NormalizeDocument_Color"
        }
        var returned_result:[String:String] = [:]
        
        let imageURL = URL(fileURLWithPath: path.replacingOccurrences(of: "file://", with: ""))
        var image = UIImage(contentsOfFile: imageURL.path)!
        image = BitmapUtils.normalizedImage(image)
        let points = quad["points"] as! [[String:NSNumber]]
        let quad = Quadrilateral.init(pointArray: convertPoints(points))
        let settings = try? VisionCameraDynamsoftDocumentNormalizer.cvr.getSimplifiedSettings(template)
        settings?.roi = quad
        settings?.roiMeasuredInPercentage = false
        try? VisionCameraDynamsoftDocumentNormalizer.cvr.updateSettings(template, settings: settings!)
        
        let capturedResult =  VisionCameraDynamsoftDocumentNormalizer.cvr.captureFromImage(image, templateName: templateName)
        let results = capturedResult.items
        if results != nil {
            if results?.count ?? 0 > 0 {
                let normalizedImageResult:NormalizedImageResultItem = results![0] as! NormalizedImageResultItem
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
