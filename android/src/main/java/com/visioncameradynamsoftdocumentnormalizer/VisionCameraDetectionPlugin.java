package com.visioncameradynamsoftdocumentnormalizer;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.dynamsoft.core.basic_structures.CapturedResult;
import com.dynamsoft.core.basic_structures.CapturedResultItem;
import com.dynamsoft.cvr.CaptureVisionRouter;
import com.dynamsoft.ddn.DetectedQuadResultItem;
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
            if (arguments != null ) {
                if (arguments.containsKey("template")) {
                    templateName = (String) arguments.get("template");
                }
            }
            Bitmap bitmap = BitmapUtils.getBitmap(frame);
            CapturedResult capturedResult = cvr.capture(bitmap,templateName);
            CapturedResultItem[] results = capturedResult.getItems();
            if (results != null) {
                for (CapturedResultItem quad:results) {
                    WritableNativeMap map = Utils.getMapFromDetectedQuadResult((DetectedQuadResultItem) quad);
                    quadResultsWrapped.add(map.toHashMap());
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
