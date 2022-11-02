package com.visioncameradynamsoftdocumentnormalizer;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import com.dynamsoft.core.CoreException;
import com.dynamsoft.ddn.DetectedQuadResult;
import com.dynamsoft.ddn.DocumentNormalizer;
import com.dynamsoft.ddn.DocumentNormalizerException;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

public class VisionCameraDetectionPlugin extends FrameProcessorPlugin {
    private VisionCameraDynamsoftDocumentNormalizerModule mModule;
    @Override
    public Object callback(ImageProxy image, Object[] params) {
        Log.d("DDN","detect");
        WritableNativeMap result = new WritableNativeMap();
        WritableNativeArray quadResultsWrapped = new WritableNativeArray();
        try {
            Log.d("DDN","try detect");
            DetectedQuadResult[] quadResults = mModule.detectImageProxy(image);
            Log.d("DDN","quad results length: "+quadResults.length);
            for (DetectedQuadResult quad:quadResults) {
                quadResultsWrapped.pushMap(Utils.getMapFromDetectedQuadResult(quad));
            }
        } catch (DocumentNormalizerException e) {
            e.printStackTrace();
        }
        result.putArray("quadResults",quadResultsWrapped);
        Log.d("DDN",result.toString());
        return result;
    }

    VisionCameraDetectionPlugin(VisionCameraDynamsoftDocumentNormalizerModule module) {
        super("detect");
        mModule = module;
    }
}
