package com.visioncameradynamsoftdocumentnormalizer;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VisionCameraDynamsoftDocumentNormalizerPackage implements ReactPackage {
    @NonNull
    @Override
    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        VisionCameraDynamsoftDocumentNormalizerModule module = new VisionCameraDynamsoftDocumentNormalizerModule(reactContext);
        modules.add(module);
        VisionCameraDetectionPlugin detectionPlugin = new VisionCameraDetectionPlugin(module);
        VisionCameraNormalizationPlugin normalizationPlugin = new VisionCameraNormalizationPlugin(module);
        FrameProcessorPlugin.register(detectionPlugin);
        FrameProcessorPlugin.register(normalizationPlugin);
        return modules;
    }

    @NonNull
    @Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}
