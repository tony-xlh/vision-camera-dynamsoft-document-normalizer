package com.visioncameradynamsoftdocumentnormalizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.camera.core.ImageProxy;

import com.dynamsoft.core.CoreException;
import com.dynamsoft.core.ImageData;
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
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;

import java.io.IOException;

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
    public void normalizeFile(String filePath, ReadableArray quad, ReadableMap config, Promise promise) {
        WritableNativeMap returnResult = new WritableNativeMap();
        Log.d("DDN",quad.toString());
        Log.d("DDN","asd"+quad.size());
        Quadrilateral quadrilateral = new Quadrilateral();
        quadrilateral.points = convertPoints(quad);
        try {
            NormalizedImageResult result = ddn.normalize(filePath,quadrilateral);
            if (config.hasKey("saveNormalizationResultAsFile")) {
                if (config.getBoolean("saveNormalizationResultAsFile")) {
                    String path = filePath+".jpg";
                    result.saveToFile(path);
                    returnResult.putString("imageURL",path);
                }
            }
            if (config.hasKey("includeNormalizationResultAsBase64")) {
                if (config.getBoolean("includeNormalizationResultAsBase64")) {
                    String base64 = BitmapUtils.bitmap2Base64(result.image.toBitmap());
                    returnResult.putString("imageBase64",base64);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("DDN",e.getMessage());
            return;
        }
        promise.resolve(returnResult);
    }

    private Point[] convertPoints(ReadableArray quadPoints){
        Point[] points = new Point[4];
        for (int i = 0; i < quadPoints.size(); i++) {
            Point p = new Point();
            p.x = quadPoints.getMap(0).getInt("x");
            p.y = quadPoints.getMap(0).getInt("y");
            points[i] = p;
        }
        return points;
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
