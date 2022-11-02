package com.visioncameradynamsoftdocumentnormalizer;
import android.util.Log;

import androidx.camera.core.ImageProxy;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

public class VisionCameraNormalizationPlugin extends FrameProcessorPlugin {
    private VisionCameraDynamsoftDocumentNormalizerModule mModule;
    @Override
    public Object callback(ImageProxy image, Object[] params) {
        // code goes here
        Log.d("DDN","normalize");
        return null;
    }

    VisionCameraNormalizationPlugin(VisionCameraDynamsoftDocumentNormalizerModule module) {
        super("normalize");
        mModule = module;
    }
}
