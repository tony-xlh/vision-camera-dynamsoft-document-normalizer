package com.visioncameradynamsoftdocumentnormalizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;

import com.dynamsoft.core.CoreException;
import com.dynamsoft.core.LicenseManager;
import com.dynamsoft.core.LicenseVerificationListener;
import com.dynamsoft.core.Quadrilateral;
import com.dynamsoft.ddn.DetectedQuadResult;
import com.dynamsoft.ddn.DocumentNormalizer;
import com.dynamsoft.ddn.DocumentNormalizerException;
import com.dynamsoft.ddn.NormalizedImageResult;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = VisionCameraDynamsoftDocumentNormalizerModule.NAME)
public class VisionCameraDynamsoftDocumentNormalizerModule extends ReactContextBaseJavaModule {
    public static final String NAME = "VisionCameraDynamsoftDocumentNormalizer";
    private Context mContext;
    private DocumentNormalizer ddn;
    public VisionCameraDynamsoftDocumentNormalizerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        initDDN();
    }

    private void initDDN(){
        try {
            ddn = new DocumentNormalizer();
        } catch (DocumentNormalizerException e) {
            e.printStackTrace();
        }
    }

    public DocumentNormalizer getDDN(){
        return ddn;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void initLicense(String license, Promise promise) {
        LicenseManager.initLicense(license, mContext, new LicenseVerificationListener() {
            @Override
            public void licenseVerificationCallback(boolean isSuccess, CoreException error) {
                if(!isSuccess){
                    error.printStackTrace();
                    promise.resolve(false);
                }else{
                    Log.d("DDN","license valid");
                    promise.resolve(true);
                }
            }
        });
    }

    @ReactMethod
    public void initRuntimeSettingsFromString(String template, Promise promise) {
        try {
            ddn.initRuntimeSettingsFromFile(template);
            promise.resolve(true);
        } catch (DocumentNormalizerException e) {
            e.printStackTrace();
            promise.reject("DDN",e.getMessage());
        }
    }

    @ReactMethod
    public void detectFile(String filePath, Promise promise) {

    }

    @ReactMethod
    public void normalizeFile(String filePath, Promise promise) {

    }

    @SuppressLint("UnsafeOptInUsageError")
    public Bitmap normalizeImageProxy(ImageProxy image, Quadrilateral quad) throws DocumentNormalizerException, CoreException {
        Bitmap bitmap = BitmapUtils.getBitmap(image);
        NormalizedImageResult result = ddn.normalize(bitmap, quad);
        return result.image.toBitmap();
    }

    @SuppressLint("UnsafeOptInUsageError")
    public DetectedQuadResult[] detectImageProxy(ImageProxy image) throws Exception {
        Bitmap bitmap = BitmapUtils.getBitmap(image);
        DetectedQuadResult[] results = ddn.detectQuad(bitmap);
        return results;
    }

}
