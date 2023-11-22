import type { Point, Rect } from "vision-camera-dynamsoft-document-normalizer";

export function sleep(time:number){
  return new Promise(function(resolve){
    setTimeout(resolve, time);
  });
}

export function intersectionOverUnion(pts1:Point[] ,pts2:Point[]) : number {
  let rect1 = getRectFromPoints(pts1);
  let rect2 = getRectFromPoints(pts2);
  return rectIntersectionOverUnion(rect1, rect2);
}

function rectIntersectionOverUnion(rect1:Rect, rect2:Rect) : number {
  let leftColumnMax = Math.max(rect1.left, rect2.left);
  let rightColumnMin = Math.min(rect1.right,rect2.right);
  let upRowMax = Math.max(rect1.top, rect2.top);
  let downRowMin = Math.min(rect1.bottom,rect2.bottom);

  if (leftColumnMax>=rightColumnMin || downRowMin<=upRowMax){
    return 0;
  }

  let s1 = rect1.width*rect1.height;
  let s2 = rect2.width*rect2.height;
  let sCross = (downRowMin-upRowMax)*(rightColumnMin-leftColumnMax);
  return sCross/(s1+s2-sCross);
}

function getRectFromPoints(points:Point[]) : Rect {
  if (points[0]) {
    let left:number;
    let top:number;
    let right:number;
    let bottom:number;
    
    left = points[0].x;
    top = points[0].y;
    right = 0;
    bottom = 0;

    points.forEach(point => {
      left = Math.min(point.x,left);
      top = Math.min(point.y,top);
      right = Math.max(point.x,right);
      bottom = Math.max(point.y,bottom);
    });

    let r:Rect = {
      left: left,
      top: top,
      right: right,
      bottom: bottom,
      width: right - left,
      height: bottom - top
    };
    
    return r;
  }else{
    throw new Error("Invalid number of points");
  }
}

