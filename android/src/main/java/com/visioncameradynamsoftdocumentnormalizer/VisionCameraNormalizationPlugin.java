package com.visioncameradynamsoftdocumentnormalizer;
import androidx.camera.core.ImageProxy;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

public class VisionCameraNormalizationPlugin extends FrameProcessorPlugin {

    @Override
    public Object callback(ImageProxy image, Object[] params) {
        // code goes here
        return null;
    }

    VisionCameraNormalizationPlugin() {
        super("normalize");
    }
}
