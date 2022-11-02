package com.visioncameradynamsoftdocumentnormalizer;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import com.dynamsoft.core.Quadrilateral;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

public class VisionCameraNormalizationPlugin extends FrameProcessorPlugin {
    private VisionCameraDynamsoftDocumentNormalizerModule mModule;
    @Override
    public Object callback(ImageProxy image, Object[] params) {
        Log.d("DDN","normalize");
        WritableNativeMap result = new WritableNativeMap();
        try {
            ReadableNativeMap config = getConfig(params);
            ReadableNativeMap quadMap = config.getMap("quad");
            ReadableArray pointsArray = quadMap.getArray("points");
            Quadrilateral quad = new Quadrilateral();
            quad.points = Utils.convertPoints(pointsArray);
            Bitmap bitmap = mModule.normalizeImageProxy(image,quad);
            if (config.hasKey("saveNormalizationResultAsFile")) {
                if (config.getBoolean("saveNormalizationResultAsFile")) {
                    String path = BitmapUtils.saveImage(bitmap);
                    result.putString("imageURL",path);
                }
            }
            if (config.hasKey("includeNormalizationResultAsBase64")) {
                if (config.getBoolean("includeNormalizationResultAsBase64")) {
                    String base64 = BitmapUtils.bitmap2Base64(bitmap);
                    result.putString("imageBase64",base64);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    VisionCameraNormalizationPlugin(VisionCameraDynamsoftDocumentNormalizerModule module) {
        super("normalize");
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
