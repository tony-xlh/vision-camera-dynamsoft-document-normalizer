package com.visioncameradynamsoftdocumentnormalizer;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dynamsoft.core.CoreException;
import com.dynamsoft.core.LicenseManager;
import com.dynamsoft.core.LicenseVerificationListener;
import com.dynamsoft.ddn.DocumentNormalizer;
import com.dynamsoft.ddn.DocumentNormalizerException;
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
    public void normalizeFile(String license, Promise promise) {
    }

    @ReactMethod
    public void normalizeBase64(String license, Promise promise) {
    }

    @ReactMethod
    public void detectBase64(String license, Promise promise) {
    }

}
