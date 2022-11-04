package com.visioncameradynamsoftdocumentnormalizer;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import com.dynamsoft.core.Quadrilateral;
import com.dynamsoft.ddn.DetectedQuadResult;
import com.dynamsoft.ddn.NormalizedImageResult;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

import java.io.File;

public class VisionCameraNormalizationPlugin extends FrameProcessorPlugin {
    private VisionCameraDynamsoftDocumentNormalizerModule mModule;
    @Override
    public Object callback(ImageProxy image, Object[] params) {
        Log.d("DDN","normalize");
        WritableNativeMap result = new WritableNativeMap();
        try {
            ReadableNativeMap config = getConfig(params);
            @SuppressLint("UnsafeOptInUsageError")
            Bitmap bitmap = BitmapUtils.getBitmap(image);
            DetectedQuadResult[] quadResults = mModule.getDDN().detectQuad(bitmap);
            if (quadResults != null) {
                if (quadResults.length>0) {
                    NormalizedImageResult normalizedImageResult = mModule.getDDN().normalize(bitmap,quadResults[0].location);
                    Bitmap normalizedImage = normalizedImageResult.image.toBitmap();
                    if (config.hasKey("saveNormalizationResultAsFile")) {
                        if (config.getBoolean("saveNormalizationResultAsFile")) {
                            File cacheDir = mModule.getContext().getCacheDir();
                            String fileName = System.currentTimeMillis() + ".jpg";
                            String path = BitmapUtils.saveImage(normalizedImage,cacheDir,fileName);
                            result.putString("imageURL",path);
                        }
                    }
                    if (config.hasKey("includeNormalizationResultAsBase64")) {
                        if (config.getBoolean("includeNormalizationResultAsBase64")) {
                            String base64 = BitmapUtils.bitmap2Base64(normalizedImage);
                            result.putString("imageBase64",base64);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    VisionCameraNormalizationPlugin(VisionCameraDynamsoftDocumentNormalizerModule module) {
        super("detectAndNormalize");
        mModule = module;
    }

    private ReadableNativeMap getConfig(Object[] params){
        if (params.length>0) {
            if (params[0] instanceof ReadableNativeMap) {
                ReadableNativeMap config = (ReadableNativeMap) params[0];
                return config;
            }
        }
        return null;
    }
}
