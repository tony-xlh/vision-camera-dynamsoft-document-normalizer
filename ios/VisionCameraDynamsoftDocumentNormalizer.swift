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
    
    @objc(normalizeFile:quad:withResolver:withRejecter:)
    func normalizeFile(path:String, quad:[String:Any],resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        do {
            let points = quad["points"] as! [[String:Int]]
            let quadrilateral = iQuadrilateral.init()
            quadrilateral.points = convertPoints(points)
            let normalizedImageResult = try VisionCameraDynamsoftDocumentNormalizer.ddn.normalizeFile(path, quad: quadrilateral)
            print("normalized image width: ")
            print(normalizedImageResult.image.width)
            resolve(true)
        }catch {
            print("Unexpected error: \(error).")
            resolve(false)
        }
    }
    
    func convertPoints(_ points:[[String:Int]]) -> [CGPoint] {
        var CGPoints:[CGPoint] = [];
        for point in points {
            let x = point["x"]!
            let y = point["y"]!
            let cgPoint = CGPoint(x: x, y: y)
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
