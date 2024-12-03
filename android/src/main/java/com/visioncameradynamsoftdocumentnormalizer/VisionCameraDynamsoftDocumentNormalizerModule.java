package com.visioncameradynamsoftdocumentnormalizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dynamsoft.core.basic_structures.CapturedResultItem;
import com.dynamsoft.core.basic_structures.ImageData;
import com.dynamsoft.core.basic_structures.Quadrilateral;
import com.dynamsoft.cvr.CapturedResult;
import com.dynamsoft.cvr.CaptureVisionRouter;
import com.dynamsoft.cvr.CaptureVisionRouterException;
import com.dynamsoft.cvr.SimplifiedCaptureVisionSettings;
import com.dynamsoft.ddn.DetectedQuadResultItem;
import com.dynamsoft.ddn.DocumentNormalizerException;
import com.dynamsoft.ddn.NormalizedImageResultItem;
import com.dynamsoft.license.LicenseManager;
import com.dynamsoft.license.LicenseVerificationListener;
import com.dynamsoft.utility.ImageManager;
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
    public static ImageData normalizedImage = null;
    private Context mContext;
    public static CaptureVisionRouter cvr;
    public VisionCameraDynamsoftDocumentNormalizerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        initCVR();
    }

    private void initCVR(){
        try {
            cvr = new CaptureVisionRouter(mContext);
            try {
                cvr.initSettings("{\"CaptureVisionTemplates\": [{\"Name\": \"Default\"},{\"Name\": \"DetectDocumentBoundaries_Default\",\"ImageROIProcessingNameArray\": [\"roi-detect-document-boundaries\"]},{\"Name\": \"DetectAndNormalizeDocument_Binary\",\"ImageROIProcessingNameArray\": [\"roi-detect-and-normalize-document-binary\"]},{\"Name\": \"DetectAndNormalizeDocument_Gray\",\"ImageROIProcessingNameArray\": [\"roi-detect-and-normalize-document-gray\"]},{\"Name\": \"DetectAndNormalizeDocument_Color\",\"ImageROIProcessingNameArray\": [\"roi-detect-and-normalize-document-color\"]},{\"Name\": \"NormalizeDocument_Binary\",\"ImageROIProcessingNameArray\": [\"roi-normalize-document-binary\"]},{\"Name\": \"NormalizeDocument_Gray\",\"ImageROIProcessingNameArray\": [\"roi-normalize-document-gray\"]},{\"Name\": \"NormalizeDocument_Color\",\"ImageROIProcessingNameArray\": [\"roi-normalize-document-color\"]}],\"TargetROIDefOptions\": [{\"Name\": \"roi-detect-document-boundaries\",\"TaskSettingNameArray\": [\"task-detect-document-boundaries\"]},{\"Name\": \"roi-detect-and-normalize-document-binary\",\"TaskSettingNameArray\": [\"task-detect-and-normalize-document-binary\"]},{\"Name\": \"roi-detect-and-normalize-document-gray\",\"TaskSettingNameArray\": [\"task-detect-and-normalize-document-gray\"]},{\"Name\": \"roi-detect-and-normalize-document-color\",\"TaskSettingNameArray\": [\"task-detect-and-normalize-document-color\"]},{\"Name\": \"roi-normalize-document-binary\",\"TaskSettingNameArray\": [\"task-normalize-document-binary\"]},{\"Name\": \"roi-normalize-document-gray\",\"TaskSettingNameArray\": [\"task-normalize-document-gray\"]},{\"Name\": \"roi-normalize-document-color\",\"TaskSettingNameArray\": [\"task-normalize-document-color\"]}],\"DocumentNormalizerTaskSettingOptions\": [{\"Name\": \"task-detect-and-normalize-document-binary\",\"ColourMode\": \"ICM_BINARY\",\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-detect-and-normalize\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-detect-and-normalize\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-detect-and-normalize\"}]},{\"Name\": \"task-detect-and-normalize-document-gray\",\"ColourMode\": \"ICM_GRAYSCALE\",\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-detect-and-normalize\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-detect-and-normalize\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-detect-and-normalize\"}]},{\"Name\": \"task-detect-and-normalize-document-color\",\"ColourMode\": \"ICM_COLOUR\",\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-detect-and-normalize\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-detect-and-normalize\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-detect-and-normalize\"}]},{\"Name\": \"task-detect-document-boundaries\",\"TerminateSetting\": {\"Section\": \"ST_DOCUMENT_DETECTION\"},\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-detect\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-detect\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-detect\"}]},{\"Name\": \"task-normalize-document-binary\",\"StartSection\": \"ST_DOCUMENT_NORMALIZATION\",\"ColourMode\": \"ICM_BINARY\",\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-normalize\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-normalize\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-normalize\"}]},{\"Name\": \"task-normalize-document-gray\",\"ColourMode\": \"ICM_GRAYSCALE\",\"StartSection\": \"ST_DOCUMENT_NORMALIZATION\",\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-normalize\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-normalize\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-normalize\"}]},{\"Name\": \"task-normalize-document-color\",\"ColourMode\": \"ICM_COLOUR\",\"StartSection\": \"ST_DOCUMENT_NORMALIZATION\",\"SectionImageParameterArray\": [{\"Section\": \"ST_REGION_PREDETECTION\",\"ImageParameterName\": \"ip-normalize\"},{\"Section\": \"ST_DOCUMENT_DETECTION\",\"ImageParameterName\": \"ip-normalize\"},{\"Section\": \"ST_DOCUMENT_NORMALIZATION\",\"ImageParameterName\": \"ip-normalize\"}]}],\"ImageParameterOptions\": [{\"Name\": \"ip-detect-and-normalize\",\"BinarizationModes\": [{\"Mode\": \"BM_LOCAL_BLOCK\",\"BlockSizeX\": 0,\"BlockSizeY\": 0,\"EnableFillBinaryVacancy\": 0}],\"TextDetectionMode\": {\"Mode\": \"TTDM_WORD\",\"Direction\": \"HORIZONTAL\",\"Sensitivity\": 7}},{\"Name\": \"ip-detect\",\"BinarizationModes\": [{\"Mode\": \"BM_LOCAL_BLOCK\",\"BlockSizeX\": 0,\"BlockSizeY\": 0,\"EnableFillBinaryVacancy\": 0,\"ThresholdCompensation\": 7}],\"TextDetectionMode\": {\"Mode\": \"TTDM_WORD\",\"Direction\": \"HORIZONTAL\",\"Sensitivity\": 7},\"ScaleDownThreshold\": 512},{\"Name\": \"ip-normalize\",\"BinarizationModes\": [{\"Mode\": \"BM_LOCAL_BLOCK\",\"BlockSizeX\": 0,\"BlockSizeY\": 0,\"EnableFillBinaryVacancy\": 0}],\"TextDetectionMode\": {\"Mode\": \"TTDM_WORD\",\"Direction\": \"HORIZONTAL\",\"Sensitivity\": 7}}]}");
            } catch (CaptureVisionRouterException e) {
                throw new RuntimeException(e);
            }
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
        LicenseManager.initLicense(license, mContext, new LicenseVerificationListener() {
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
        String templateName = "DetectDocumentBoundaries_Default";
        if (!template.equals("")) {
            templateName = template;
        }
        WritableNativeArray returnResult = new WritableNativeArray();
        try {
            File file = new File(filePath);
            if (file.exists() == false) { //convert uri to path
                Uri uri = Uri.parse(filePath);
                filePath = uri.getPath();
            }
            CapturedResult capturedResult = cvr.capture(filePath,templateName);
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
    public void getNormalizationResult(ReadableMap config, Promise promise) {
        WritableNativeMap returnResult = new WritableNativeMap();
        try {
          if (config.hasKey("saveNormalizationResultAsFile")) {
            if (config.getBoolean("saveNormalizationResultAsFile")) {
              File cacheDir = mContext.getCacheDir();
              String fileName = System.currentTimeMillis() + ".jpg";
              File output = new File(cacheDir,fileName);
              new ImageManager().saveToFile(normalizedImage,output.getAbsolutePath(),true);
              returnResult.putString("imageURL",output.getAbsolutePath());
            }
          }
          if (config.hasKey("includeNormalizationResultAsBase64")) {
            if (config.getBoolean("includeNormalizationResultAsBase64")) {
              Bitmap bm = normalizedImage.toBitmap();
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

    @ReactMethod
    public void normalizeFile(String filePath, ReadableMap quad, ReadableMap config, String template, Promise promise) {
        WritableNativeMap returnResult = new WritableNativeMap();
        String templateName = "NormalizeDocument_Color";
        if (!template.equals("")) {
            templateName = template;
        }
        Log.d("DDN",templateName);
        try {
            File file = new File(filePath);
            if (file.exists() == false) { //convert uri to path
                Uri uri = Uri.parse(filePath);
                filePath = uri.getPath();
            }
            ReadableArray points = quad.getArray("points");
            Quadrilateral quadrilateral = new Quadrilateral();
            quadrilateral.points = convertPoints(points);
            SimplifiedCaptureVisionSettings settings = cvr.getSimplifiedSettings(templateName);
            settings.roi = quadrilateral;
            settings.roiMeasuredInPercentage = false;
            cvr.updateSettings(templateName,settings);
            CapturedResult capturedResult = cvr.capture(filePath,templateName);
            NormalizedImageResultItem result = (NormalizedImageResultItem) capturedResult.getItems()[0];

            if (config.hasKey("saveNormalizationResultAsFile")) {
                if (config.getBoolean("saveNormalizationResultAsFile")) {
                    File cacheDir = mContext.getCacheDir();
                    String fileName = System.currentTimeMillis() + ".jpg";
                    File output = new File(cacheDir,fileName);
                    new ImageManager().saveToFile(result.getImageData(),output.getAbsolutePath(),true);
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
