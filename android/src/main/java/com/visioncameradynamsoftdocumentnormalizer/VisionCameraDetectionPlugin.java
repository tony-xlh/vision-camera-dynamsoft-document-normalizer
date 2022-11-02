package com.visioncameradynamsoftdocumentnormalizer;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import com.dynamsoft.ddn.DetectedQuadResult;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

import java.nio.ByteBuffer;

public class VisionCameraDetectionPlugin extends FrameProcessorPlugin {
    private VisionCameraDynamsoftDocumentNormalizerModule mModule;
    @Override
    public Object callback(ImageProxy image, Object[] params) {
        Log.d("DDN","detect");
        WritableNativeMap result = new WritableNativeMap();
        WritableNativeArray quadResultsWrapped = new WritableNativeArray();
        try {
            DetectedQuadResult[] quadResults = mModule.detectImageProxy(image);
            for (DetectedQuadResult quad:quadResults) {
                quadResultsWrapped.pushMap(Utils.getMapFromDetectedQuadResult(quad));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.putArray("quadResults",quadResultsWrapped);
        return result;
    }

    VisionCameraDetectionPlugin(VisionCameraDynamsoftDocumentNormalizerModule module) {
        super("detect");
        mModule = module;
    }
}
