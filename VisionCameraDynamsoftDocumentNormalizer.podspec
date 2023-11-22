require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "VisionCameraDynamsoftDocumentNormalizer"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "11.0" }
  s.source       = { :git => "https://github.com/tony-xlh/vision-camera-dynamsoft-document-normalizer.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,swift}"

  s.dependency "React-Core"
  s.dependency "DynamsoftCore", "= 2.0.2"
  s.dependency "DynamsoftImageProcessing", "= 1.0.30"
  s.dependency "DynamsoftIntermediateResult", "= 1.0.30"
  s.dependency "DynamsoftDocumentNormalizer", "= 1.0.30"
end
