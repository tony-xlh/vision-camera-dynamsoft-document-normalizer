package com.visioncameradynamsoftdocumentnormalizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dynamsoft.core.basic_structures.CapturedResultItem;
import com.dynamsoft.core.basic_structures.Quadrilateral;
import com.dynamsoft.cvr.CapturedResult;
import com.dynamsoft.cvr.CaptureVisionRouter;
import com.dynamsoft.cvr.CaptureVisionRouterException;
import com.dynamsoft.cvr.SimplifiedCaptureVisionSettings;
import com.dynamsoft.ddn.DetectedQuadResultItem;
import com.dynamsoft.ddn.DocumentNormalizerException;
import com.dynamsoft.ddn.DeskewedImageResultItem;
import com.dynamsoft.ddn.EnhancedImageResultItem;
import com.dynamsoft.ddn.ProcessedDocumentResult;
import com.dynamsoft.license.LicenseManager;
import com.dynamsoft.license.LicenseVerificationListener;
import com.dynamsoft.utility.ImageIO;
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
    public static CaptureVisionRouter cvr;
    public VisionCameraDynamsoftDocumentNormalizerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        initCVR();
    }

    private void initCVR(){
        try {
            cvr = new CaptureVisionRouter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Context getContext(){
        return mContext;
    }
    public CaptureVisionRouter getCVR(){
        return cvr;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void initLicense(String license, Promise promise) {
        LicenseManager.initLicense(license, new LicenseVerificationListener() {
            @Override
            public void onLicenseVerified(boolean isSuccess, Exception error) {
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
            cvr.initSettings(template);
            promise.resolve(true);
        } catch (CaptureVisionRouterException e) {
            e.printStackTrace();
            promise.reject("DDN",e.getMessage());
        }
    }

    @ReactMethod
    public void detectFile(String filePath, String template,Promise promise) {
        detectFileImpl(filePath,template,false,promise);
    }

    @ReactMethod
    public void detectBase64(String base64, String template,Promise promise) {
        detectFileImpl(base64,template,true,promise);
    }

    private void detectFileImpl(String str, String template,Boolean isBase64, Promise promise) {
        String templateName = "DetectDocumentBoundaries_Default";
        if (!template.equals("")) {
            templateName = template;
        }
        WritableNativeArray returnResult = new WritableNativeArray();
        try {
            String filePath = str;
            Bitmap bitmap = null;
            if (isBase64) {
                bitmap = BitmapUtils.base642Bitmap(str);
            }else{
                File file = new File(str);
                if (file.exists() == false) { //convert uri to path if the string is uri
                    Uri uri = Uri.parse(str);
                    filePath = uri.getPath();
                }
            }

            CapturedResult capturedResult;
            if (bitmap != null) {
                capturedResult = cvr.capture(bitmap, templateName);
            }else{
                capturedResult = cvr.capture(filePath, templateName);
            }
            for (CapturedResultItem quad:capturedResult.getItems()) {
                returnResult.pushMap(Utils.getMapFromDetectedQuadResult((DetectedQuadResultItem) quad));
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
            if (file.exists() == false) { //convert uri to path
                Uri uri = Uri.parse(filePath);
                filePath = uri.getPath();
                file = new File(filePath);
            }
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
    public void normalizeFile(String filePath, ReadableMap quad, ReadableMap config, String template, Promise promise) {
        normalizeFileImpl(filePath,false,quad,config,template,promise);
    }

    @ReactMethod
    public void normalizeBase64(String base64, ReadableMap quad, ReadableMap config, String template, Promise promise) {
        normalizeFileImpl(base64,true,quad,config,template,promise);
    }

    private void normalizeFileImpl(String str, Boolean isBase64, ReadableMap quad, ReadableMap config, String template, Promise promise){
        WritableNativeMap returnResult = new WritableNativeMap();
        String templateName = "NormalizeDocument_Default";
        if (!template.equals("")) {
            templateName = template;
        }
        try {
            String filePath = str;
            Bitmap bitmap = null;
            if (isBase64) {
                bitmap = BitmapUtils.base642Bitmap(str);
            }else{
                File file = new File(str);
                if (file.exists() == false) { //convert uri to path
                    Uri uri = Uri.parse(str);
                    filePath = uri.getPath();
                }
            }

            ReadableArray points = quad.getArray("points");
            Quadrilateral quadrilateral = new Quadrilateral();
            quadrilateral.points = convertPoints(points);
            SimplifiedCaptureVisionSettings settings = cvr.getSimplifiedSettings(templateName);
            settings.roi = quadrilateral;
            settings.roiMeasuredInPercentage = false;
            cvr.updateSettings(templateName,settings);
            CapturedResult capturedResult;
            if (bitmap != null) {
                capturedResult = cvr.capture(bitmap,templateName);
            }else{
                capturedResult = cvr.capture(filePath,templateName);
            }

            ProcessedDocumentResult processedDocumentResult = capturedResult.getProcessedDocumentResult();
            EnhancedImageResultItem result = processedDocumentResult.getEnhancedImageResultItems()[0];

            if (config.hasKey("saveNormalizationResultAsFile")) {
                if (config.getBoolean("saveNormalizationResultAsFile")) {
                    File cacheDir = mContext.getCacheDir();
                    String fileName = System.currentTimeMillis() + ".jpg";
                    File output = new File(cacheDir,fileName);
                    new ImageIO().saveToFile(result.getImageData(),output.getAbsolutePath(),true);
                    returnResult.putString("imageURL",output.getAbsolutePath());
                }
            }
            if (config.hasKey("includeNormalizationResultAsBase64")) {
                if (config.getBoolean("includeNormalizationResultAsBase64")) {
                    Bitmap bm = result.getImageData().toBitmap();
                    String base64 = BitmapUtils.bitmap2Base64(bm);
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
            p.x = quadPoints.getMap(i).getInt("x");
            p.y = quadPoints.getMap(i).getInt("y");
            points[i] = p;
        }
        return points;
    }
}
