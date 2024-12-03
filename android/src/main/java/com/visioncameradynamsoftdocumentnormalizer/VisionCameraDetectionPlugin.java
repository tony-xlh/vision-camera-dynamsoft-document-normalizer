package com.visioncameradynamsoftdocumentnormalizer;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.dynamsoft.core.basic_structures.CapturedResultItem;
import com.dynamsoft.core.basic_structures.Quadrilateral;
import com.dynamsoft.cvr.CaptureVisionRouter;
import com.dynamsoft.cvr.CapturedResult;
import com.dynamsoft.cvr.SimplifiedCaptureVisionSettings;
import com.dynamsoft.ddn.DetectedQuadResultItem;
import com.dynamsoft.ddn.DetectedQuadsResult;
import com.dynamsoft.ddn.NormalizedImageResultItem;
import com.dynamsoft.ddn.NormalizedImagesResult;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.mrousavy.camera.core.FrameInvalidError;
import com.mrousavy.camera.frameprocessors.Frame;
import com.mrousavy.camera.frameprocessors.FrameProcessorPlugin;
import com.mrousavy.camera.frameprocessors.VisionCameraProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisionCameraDetectionPlugin extends FrameProcessorPlugin {
    private CaptureVisionRouter cvr = VisionCameraDynamsoftDocumentNormalizerModule.cvr;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public Object callback(@NonNull Frame frame, @Nullable Map<String, Object> arguments) {
        List<Object> quadResultsWrapped = new ArrayList<>();
        try {
            String templateName = "DetectDocumentBoundaries_Default";
            Boolean saveNormalizationResult = false;
            if (arguments != null ) {
                if (arguments.containsKey("template")) {
                    templateName = (String) arguments.get("template");
                }
                if (arguments.containsKey("saveNormalizationResult")) {
                  saveNormalizationResult = (Boolean) arguments.get("saveNormalizationResult");
                  if (saveNormalizationResult == false) {
                    VisionCameraDynamsoftDocumentNormalizerModule.normalizedImage = null;
                  }
                }
            }
            Bitmap bitmap = BitmapUtils.getBitmap(frame);
            CapturedResult capturedResult = cvr.capture(bitmap,templateName);
            DetectedQuadsResult detectedQuadsResult = capturedResult.getDetectedQuadsResult();
            if (detectedQuadsResult != null) {
                DetectedQuadResultItem[] results = detectedQuadsResult.getItems();
                for (DetectedQuadResultItem quad:results) {
                    WritableNativeMap map = Utils.getMapFromDetectedQuadResult(quad);
                    quadResultsWrapped.add(map.toHashMap());
                }
                if (saveNormalizationResult && results.length>0) {
                    Log.d("DDN","save normalization result");
                    DetectedQuadResultItem detectedQuadResultItem = (DetectedQuadResultItem) results[0];
                    String normalizationTemplateName = "NormalizeDocument_Color"; // alternatives: NormalizeDocument_Gray, NormalizeDocument_Color
                    Quadrilateral quad = new Quadrilateral();
                    quad.points = detectedQuadResultItem.getLocation().points;
                    SimplifiedCaptureVisionSettings settings = cvr.getSimplifiedSettings(normalizationTemplateName);
                    settings.roi = quad;
                    settings.roiMeasuredInPercentage = false;
                    cvr.updateSettings(normalizationTemplateName,settings); //pass the polygon to the capture router
                    CapturedResult normalizationResult = cvr.capture(bitmap,normalizationTemplateName); //run normalization
                    NormalizedImagesResult normalizedImagesResult = normalizationResult.getNormalizedImagesResult();
                    if (normalizedImagesResult != null) {
                        NormalizedImageResultItem[] normalizedImageResultItems = normalizedImagesResult.getItems();
                        if (normalizedImageResultItems.length>0) {
                            VisionCameraDynamsoftDocumentNormalizerModule.normalizedImage = normalizedImageResultItems[0].getImageData();
                        }
                    }
                }
            }
        } catch (Exception | FrameInvalidError e) {
            e.printStackTrace();
            Log.d("DDN",e.getMessage());
        }
        return quadResultsWrapped;
    }
    VisionCameraDetectionPlugin(@NonNull VisionCameraProxy proxy, @Nullable Map<String, Object> options) {super();}
}
