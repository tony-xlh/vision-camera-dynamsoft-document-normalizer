package com.visioncameradynamsoftdocumentnormalizer;

import android.graphics.Point;

import com.dynamsoft.core.Quadrilateral;
import com.dynamsoft.ddn.DetectedQuadResult;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

public class Utils {
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

    public static WritableNativeMap getMapFromDetectedQuadResult(DetectedQuadResult result){
        WritableNativeMap map = new WritableNativeMap();
        map.putInt("confidenceAsDocumentBoundary",result.confidenceAsDocumentBoundary);
        map.putMap("location",getMapFromLocation(result.location));
        return map;
    }

    private static WritableNativeMap getMapFromLocation(Quadrilateral location){
        WritableNativeMap map = new WritableNativeMap();
        WritableNativeArray points = new WritableNativeArray();
        for (Point point: location.points) {
            WritableNativeMap pointAsMap = new WritableNativeMap();
            pointAsMap.putInt("x",point.x);
            pointAsMap.putInt("y",point.y);
            points.pushMap(pointAsMap);
        }
        map.putArray("points",points);
        return map;
    }
}
