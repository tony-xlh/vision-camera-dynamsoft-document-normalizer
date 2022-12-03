package com.visioncameradynamsoftdocumentnormalizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
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
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;

import java.io.File;
import java.io.FileOutputStream;

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

    public Context getContext(){
        return mContext;
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
            ddn.initRuntimeSettingsFromString(template);
            promise.resolve(true);
        } catch (DocumentNormalizerException e) {
            e.printStackTrace();
            promise.reject("DDN",e.getMessage());
        }
    }

    @ReactMethod
    public void detectFile(String filePath, Promise promise) {
        WritableNativeArray returnResult = new WritableNativeArray();
        try {
            DetectedQuadResult[] quadResults = ddn.detectQuad(filePath);
            for (DetectedQuadResult quad:quadResults) {
                returnResult.pushMap(Utils.getMapFromDetectedQuadResult(quad));
            }
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("DDN",e.getMessage());
            return;
        }
        promise.resolve(returnResult);
    }

    @ReactMethod
    public void rotateFile(String filePath, int degrees, Promise promise) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        bitmap = rotateBitmap(bitmap,degrees,false,false);
        File file = new File(filePath);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("DDN",e.getMessage());
            return;
        }
        promise.resolve(true);
    }

    private static Bitmap rotateBitmap(
            Bitmap bitmap, int rotationDegrees, boolean flipX, boolean flipY) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);

        // Mirror the image along the X or Y axis.
        matrix.postScale(flipX ? -1.0f : 1.0f, flipY ? -1.0f : 1.0f);
        Bitmap rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Recycle the old bitmap if it has changed.
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }
        return rotatedBitmap;
    }


    @ReactMethod
    public void normalizeFile(String filePath, ReadableMap quad, ReadableMap config, Promise promise) {
        WritableNativeMap returnResult = new WritableNativeMap();
        Log.d("DDN",quad.toString());
        ReadableArray points = quad.getArray("points");
        Quadrilateral quadrilateral = new Quadrilateral();
        quadrilateral.points = convertPoints(points);
        try {
            NormalizedImageResult result = ddn.normalize(filePath,quadrilateral);
            if (config.hasKey("saveNormalizationResultAsFile")) {
                if (config.getBoolean("saveNormalizationResultAsFile")) {
                    //String path = filePath+".jpg";
                    //result.saveToFile(path);
                    File cacheDir = mContext.getCacheDir();
                    String fileName = System.currentTimeMillis() + ".jpg";
                    String path = BitmapUtils.saveImage(result.image.toBitmap(), cacheDir, fileName);
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
            if (e instanceof DocumentNormalizerException) {
                Log.d("DDN","Error code: "+((DocumentNormalizerException) e).getErrorCode());
            }
            promise.reject("DDN",e.getMessage());
            return;
        }
        promise.resolve(returnResult);
    }

    private Point[] convertPoints(ReadableArray quadPoints){
        Point[] points = new Point[4];
        for (int i = 0; i < quadPoints.size(); i++) {
            Point p = new Point();
            p.x = quadPoints.getMap(i).getInt("x");
            p.y = quadPoints.getMap(i).getInt("y");
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
