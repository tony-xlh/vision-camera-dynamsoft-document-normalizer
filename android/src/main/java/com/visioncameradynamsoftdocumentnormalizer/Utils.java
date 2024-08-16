package com.visioncameradynamsoftdocumentnormalizer;

import android.graphics.Point;
import android.util.Log;

import com.dynamsoft.core.basic_structures.Quadrilateral;
import com.dynamsoft.ddn.DetectedQuadResultItem;
import com.facebook.react.bridge.NativeMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    public static boolean rotatePoints = false;
    public static int degree = 0;
    public static int imageWidth = 0;
    public static int imageHeight = 0;
    public static Point[] convertPoints(ReadableArray pointsArray){
        Point[] points = new Point[4];
        for (int i = 0; i < pointsArray.size(); i++) {
            ReadableMap pointMap = pointsArray.getMap(i);
            Point point = new Point();
            point.x = pointMap.getInt("x");
            point.y = pointMap.getInt("y");
            points[i] = point;
        }
        return points;
    }

    public static Point rotatedPoint(Point point) {
        Point rotatedPoint = new Point();
        switch (degree){
            case 90:
                rotatedPoint.x = imageHeight - point.y;
                rotatedPoint.y = point.x;
                break;
            case 180:
                rotatedPoint.x = imageWidth - point.x;
                rotatedPoint.y = imageHeight - point.y;
                break;
            case 270:
                rotatedPoint.x = imageHeight - point.y;
                rotatedPoint.y = imageWidth - point.x;
                break;
            default:
                rotatedPoint.x = point.x;
                rotatedPoint.y = point.y;
        }
        return rotatedPoint;
    }

    public static Map<String, Object> convertNativeMap(ReadableNativeMap map){
        Map<String, Object> hashMap = new HashMap<>();
        ReadableMapKeySetIterator iterator = map.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            Log.d("DDN",key);
            ReadableType type = map.getType(key);
            if (type == ReadableType.Map) {
                WritableNativeMap converted = (WritableNativeMap) map.getMap(key);
                hashMap.put(key,convertNativeMap(converted));
            }else if (type == ReadableType.Array) {
                ReadableArray array = map.getArray(key);
                List<Object> arrayConverted = new ArrayList<>();
                for (Object item:array.toArrayList()) {
                    if (item instanceof ReadableNativeMap) {
                        arrayConverted.add(convertNativeMap((WritableNativeMap) item));
                    }else{
                        arrayConverted.add(item);
                    }
                }
                hashMap.put(key,arrayConverted);
            }else if (type == ReadableType.Boolean) {
                hashMap.put(key,map.getBoolean(key));
            }else if (type == ReadableType.Number) {
                hashMap.put(key,map.getInt(key));
            }else if (type == ReadableType.String) {
                hashMap.put(key,map.getString(key));
            }
        }
        return hashMap;
    }

    public static WritableNativeMap getMapFromDetectedQuadResult(DetectedQuadResultItem result){
        WritableNativeMap map = new WritableNativeMap();
        map.putInt("confidenceAsDocumentBoundary",result.getConfidenceAsDocumentBoundary());
        map.putMap("location",getMapFromLocation(result.getLocation()));
        map.putInt("area",result.getLocation().getArea());
        return map;
    }

    private static WritableNativeMap getMapFromLocation(Quadrilateral location){
        WritableNativeMap map = new WritableNativeMap();
        WritableNativeArray points = new WritableNativeArray();
        Point[] locationPoints = location.points;
        if (rotatePoints) {
            locationPoints = rotatedPoints(locationPoints);
        }
        for (Point point: locationPoints) {
            WritableNativeMap pointAsMap = new WritableNativeMap();

            pointAsMap.putInt("x",point.x);
            pointAsMap.putInt("y",point.y);

            points.pushMap(pointAsMap);
        }
        map.putArray("points",points);
        return map;
    }

    private static Point[] rotatedPoints(Point[] points) {
      Log.d("DDN","rotatedPoints");
        int width = imageWidth;
        int height = imageHeight;
        if (degree == 90 || degree == 270) {
          width = imageHeight;
          height = imageWidth;
        }

        int centerX = width/2;
        int centerY = height/2;
        Point topLeftResult = points[0];
        Point topRightResult = points[1];
        Point bottomRightResult = points[2];
        Point bottomLeftResult = points[3];
        for (Point point:points) {
            point = rotatedPoint(point);
            if (point.x - centerX < 0 && point.y - centerY < 0) {
               topLeftResult = point;
            }else if (point.x - centerX > 0 && point.y - centerY < 0) {
               topRightResult = point;
            }else if (point.x - centerX > 0 && point.y - centerY > 0) {
               bottomRightResult = point;
            }else if (point.x - centerX < 0 && point.y - centerY > 0) {
               bottomLeftResult = point;
            }
        }
        Point[] sortedPoints = new Point[4];
        sortedPoints[0] = topLeftResult;
        sortedPoints[1] = topRightResult;
        sortedPoints[2] = bottomRightResult;
        sortedPoints[3] = bottomLeftResult;
        Log.d("DDN","topleft: "+topLeftResult.x);
        Log.d("DDN","topleft: "+topLeftResult.y);
        Log.d("DDN","topRight: "+topRightResult.x);
        Log.d("DDN","topRight: "+topRightResult.x);
        return sortedPoints;
    }
}
