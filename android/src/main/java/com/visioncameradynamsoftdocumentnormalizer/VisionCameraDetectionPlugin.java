package com.visioncameradynamsoftdocumentnormalizer;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.dynamsoft.ddn.DetectedQuadResult;
import com.dynamsoft.ddn.DocumentNormalizer;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.mrousavy.camera.frameprocessor.Frame;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisionCameraDetectionPlugin extends FrameProcessorPlugin {
    private DocumentNormalizer ddn = VisionCameraDynamsoftDocumentNormalizerModule.ddn;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public Object callback(@NonNull Frame frame, @Nullable Map<String, Object> arguments) {
        List<Object> quadResultsWrapped = new ArrayList<>();
        try {
            Bitmap bitmap = BitmapUtils.getBitmap(frame);
            DetectedQuadResult[] quadResults = ddn.detectQuad(bitmap);
            if (quadResults != null) {
                Log.d("DDN","length: "+quadResults.length);
                for (DetectedQuadResult quad:quadResults) {
                    WritableNativeMap map = Utils.getMapFromDetectedQuadResult(quad);
                    quadResultsWrapped.add(map.toHashMap());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DDN",e.getMessage());
        }
        Log.d("DDN","quadResultsWrapped: "+quadResultsWrapped.size());
        return quadResultsWrapped;
    }

    VisionCameraDetectionPlugin()
    {
        super(null);
    }
}
