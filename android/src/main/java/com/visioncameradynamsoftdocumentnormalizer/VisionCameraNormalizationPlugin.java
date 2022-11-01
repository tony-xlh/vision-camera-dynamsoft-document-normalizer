package com.visioncameradynamsoftdocumentnormalizer;
import androidx.camera.core.ImageProxy;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

public class VisionCameraNormalizationPlugin extends FrameProcessorPlugin {
    private VisionCameraDynamsoftDocumentNormalizerModule mModule;
    @Override
    public Object callback(ImageProxy image, Object[] params) {
        // code goes here
        return null;
    }

    VisionCameraNormalizationPlugin(VisionCameraDynamsoftDocumentNormalizerModule module) {
        super("normalize");
        mModule = module;
    }
}
