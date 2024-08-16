package com.visioncameradynamsoftdocumentnormalizer;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.dynamsoft.core.basic_structures.CapturedResultItem;
import com.dynamsoft.core.basic_structures.EnumImagePixelFormat;
import com.dynamsoft.core.basic_structures.ImageData;
import com.dynamsoft.cvr.CaptureVisionRouter;
import com.dynamsoft.cvr.CapturedResult;
import com.dynamsoft.ddn.DetectedQuadResultItem;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.mrousavy.camera.core.FrameInvalidError;
import com.mrousavy.camera.frameprocessors.Frame;
import com.mrousavy.camera.frameprocessors.FrameProcessorPlugin;
import com.mrousavy.camera.frameprocessors.VisionCameraProxy;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisionCameraDetectionPlugin extends FrameProcessorPlugin {
    private CaptureVisionRouter cvr = VisionCameraDynamsoftDocumentNormalizerModule.cvr;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public Object callback(@NonNull Frame frame, @Nullable Map<String, Object> arguments) {
        List<Object> quadResultsWrapped = new ArrayList<>();
        try {
            String templateName = "DetectDocumentBoundaries_Default";
            boolean rotateImage = true;
            if (arguments != null ) {
                if (arguments.containsKey("template")) {
                    templateName = (String) arguments.get("template");
                }
                if (arguments.containsKey("rotateImage")) {
                    rotateImage = (boolean) arguments.get("rotateImage");
                }
            }
            CapturedResult capturedResult = detect(frame,rotateImage,templateName);
            CapturedResultItem[] results = capturedResult.getItems();
            if (results != null) {
                setRotation(frame,rotateImage);
                for (CapturedResultItem quad:results) {
                    WritableNativeMap map = Utils.getMapFromDetectedQuadResult((DetectedQuadResultItem) quad);
                    quadResultsWrapped.add(map.toHashMap());
                }
            }
        } catch (Exception | FrameInvalidError e) {
            e.printStackTrace();
            Log.d("DDN",e.getMessage());
        }
        return quadResultsWrapped;
    }

    private void setRotation(Frame frame, Boolean rotateImage) throws FrameInvalidError {
      Utils.rotatePoints = !rotateImage;
      if (rotateImage == false) {
        Image image = frame.getImage();
        Utils.degree = BitmapUtils.getRotationDegreeFromOrientation(frame.getOrientation());
        Utils.imageWidth = image.getWidth();
        Utils.imageHeight = image.getHeight();
      }
    }

    private CapturedResult detect(Frame frame, Boolean rotateImage,String templateName) throws FrameInvalidError {
        CapturedResult capturedResult = null;
        if (rotateImage){
            Bitmap bitmap = BitmapUtils.getBitmap(frame);
            capturedResult = cvr.capture(bitmap,templateName);
        }else{
            Image image = frame.getImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            int nRowStride = image.getPlanes()[0].getRowStride();
            int nPixelStride = image.getPlanes()[0].getPixelStride();
            int length = buffer.remaining();
            byte[] bytes = new byte[length];
            buffer.get(bytes);
            ImageData imageData = new ImageData();
            imageData.bytes = bytes;
            imageData.width = image.getWidth();
            imageData.height = image.getHeight();
            imageData.stride = nRowStride*nPixelStride;
            imageData.format = EnumImagePixelFormat.IPF_NV21;
            capturedResult = cvr.capture(imageData,templateName);
        }
        return capturedResult;
    }
    VisionCameraDetectionPlugin(@NonNull VisionCameraProxy proxy, @Nullable Map<String, Object> options) {super();}
}
