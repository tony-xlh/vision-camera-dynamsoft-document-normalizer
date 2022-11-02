package com.visioncameradynamsoftdocumentnormalizer;
import android.util.Log;

import androidx.camera.core.ImageProxy;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

public class VisionCameraDetectionPlugin extends FrameProcessorPlugin {
    private VisionCameraDynamsoftDocumentNormalizerModule mModule;
    @Override
    public Object callback(ImageProxy image, Object[] params) {
        // code goes here
        Log.d("DDN","detect");
        return null;
    }

    VisionCameraDetectionPlugin(VisionCameraDynamsoftDocumentNormalizerModule module) {
        super("detect");
        mModule = module;
    }
}
