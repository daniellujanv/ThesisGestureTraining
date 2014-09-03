package itu.dluj.gestures;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import android.util.Log;

public class Gestures {

	public static int secondsToWait = 2;
	private static String TAG = "itu.dluj.thesisgesturetraining";
	private final static double iScaleFactor = 2.0;

	/************************************* Available Gestures *****************************************************/
	/*
	 * Detection of Init gesture
	 */
	public static boolean detectInitGesture(List<Point[]> lDefects, Point centroid) {
		int positiveDefects = 0;
		//		int defects[] = convexityDefects.toArray();
		//		List<Point> contour = handContour.toList();
		//		Point centroid = Tools.getCentroid(handContour);
		//		MatOfPoint2f m2fHandContour = new MatOfPoint2f(handContour.toArray());
		/*
		 * convexityDefects -> structure containing (by order) start, end, depth_point, depth.
		 * depth-> farthest point (depth_point) distance to convex hull
		 */
		Point start;
		//		Point end;
		Point farthest;
		//distance between the farthest point and convex hull
		/*
		 * Removing defects
		 * we want to keep only defects above centroid of hand
		 */
		if(lDefects.size() >= 3){
			for(int i=0; i< lDefects.size(); i++){
				start = lDefects.get(i)[0];
				//			end = contour.get(defects[i+1]);
				farthest = lDefects.get(i)[1];
				//			distancefarthest = Math.round(defects[i+3]/256.0);
				//top left screen x=0,y=0. Otherwise would be end.x > centroid.x
				//			if((start.y < centroid.y && farthest.y < centroid.y) && (start.y < farthest.y)){
				//distance between the center of hand and defect
				double distanceCenterPoint = Tools.getDistanceBetweenPoints(centroid, start);
				//distance between the start of defect and farthest point
				double distancefarthestPoint = Tools.getDistanceBetweenPoints(start, farthest);
				//relation of distances from 1) center of hand to defect and 2)start of defect to farthest point 
				double relationCenterPoint_farthestPoint = (distanceCenterPoint/distancefarthestPoint);
//				Log.i(TAG+"Init", "relCenter_farthest: "+relationCenterPoint_farthestPoint
//						);
				if((relationCenterPoint_farthestPoint <= 2.0)){
					//				        points
					//					Core.circle(mRgb, end, 5, red, -1);
					positiveDefects = positiveDefects + 1;
				}
				//			}
			}
			//		Log.i("ImageInteraction", "Init gesture::defect number -> "+ positiveDefects);
			if(positiveDefects >= 3){
				Log.i(TAG, "Gesture :: Init Detected");
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	/*
	 * Detection of End gesture
	 */
	public static boolean detectEndGesture(List<Point[]> lDefects, Point centroid) {
		int positiveDefects = 0;
		//		int defects[] = convexityDefects.toArray();
		//		List<Point> contour = handContour.toList();
		//		Point centroid = Tools.getCentroid(handContour);
		//		MatOfPoint2f m2fHandContour = new MatOfPoint2f(handContour.toArray());

		/*
		 * convexityDefects -> structure containing (by order) start, end, depth_point, depth.
		 * depth-> farthest point (depth_point) distance to convex hull
		 */
		Point start;
		//		Point end;
		Point farthest;
		//		float distancefarthest;
		/*
		 * Removing defects
		 * we want to keep only defects above centroid of hand
		 */
		if(lDefects.size() >= 3){		
			for(int i=0; i< lDefects.size(); i++){
				start = lDefects.get(i)[0];
				//			end = contour.get(defects[i+1]);
				farthest = lDefects.get(i)[1];
				//			distancefarthest = Math.round(defects[i+3]/256.0);
				//top left screen x=0,y=0. Otherwise would be end.x > centroid.x
				//			if(start.y < centroid.y && farthest.y < centroid.y){
				//			if((start.y < centroid.y && farthest.y < centroid.y) && (start.y < farthest.y)){	
				//distance between the center of hand and defect
				double distanceCenterPoint = Tools.getDistanceBetweenPoints(centroid, start);
				//distance between the start of defect and farthest point
				double distancefarthestPoint = Tools.getDistanceBetweenPoints(start, farthest);
				//relation of distances from 1) center of hand to defect and 2)start of defect to farthest point 
				double relationCenterPoint_farthestPoint = (distanceCenterPoint/distancefarthestPoint);
//				Log.i(TAG+"End", "relCenter_farthest: "+relationCenterPoint_farthestPoint
//						);
				if((relationCenterPoint_farthestPoint >= 3.0)){
					//				        points
					//					Core.circle(mRgb, end, 5, red, -1);
					positiveDefects = positiveDefects + 1;
				}
				//			}
			}
			//		Log.i("ImageInteraction", "End gesture::defect number -> "+ positiveDefects);
			if(positiveDefects >= 3){
				Log.i(TAG, "Gesture :: End Detected");
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	/*
	 * Detection of PointSelect gesture
	 */
	public static Point detectPointSelectGesture(List<Point[]> lDefects, Point centroid, boolean initDetected) {
		//		int defects[] = convexityDefects.toArray();
		//		List<Point> contour = handContour.toList();
		List<Point> finalDefects = new ArrayList<Point>();
		//		Point centroid = Tools.getCentroid(handContour);
		//		MatOfPoint2f m2fHandContour = new MatOfPoint2f(handContour.toArray());

		/*
		 * convexityDefects -> structure containing (by order) start, end, depth_point, depth.
		 * depth-> farthest point (depth_point) distance to convex hull
		 */
		Point start;
		//		Point end;
		Point farthest;
		//		double distancePointHull = 0;
		int positiveDefects = 0;
//		Log.i(TAG+"PointSelect", "defects#::"+);
		/*
		 * Look for PointSelect_Init gesture :: 1 finger lifted up
		 * - Removing defects
		 * -- we want to keep only defects above centroid of hand
		 */

		/**************** PRE-STROKE********************/
		if(!initDetected && lDefects.size() == 1){
			//Log.i(TAG+"PointSelectInit", "looking for pre-stroke");
			for(int i=0; i< lDefects.size(); i++){
				start = lDefects.get(i)[0];
				//			end = contour.get(defects[i+1]);
				farthest = lDefects.get(i)[1];
				//top left screen x=0,y=0. Otherwise would be end.x > centroid.x
				//distance between the center of hand and defect
				double distanceCenterPoint = Tools.getDistanceBetweenPoints(centroid, start);
				//distance between the start of defect and farthest point
				double distancefarthestPoint = Tools.getDistanceBetweenPoints(start, farthest);
				//relation of distances from 1) center of hand to defect and 2)start of defect to farthest point 
				double relationCenterPoint_farthestPoint = distanceCenterPoint/distancefarthestPoint;
				//Log.i(TAG+"PointSelectInit", "center_endFurtherst:: "+relationCenterPoint_farthestPoint);
				if(relationCenterPoint_farthestPoint < 2.0){
					finalDefects.add(start);
					positiveDefects = positiveDefects + 1;
				}
			}
			//relation between length of positive defect and average length of negative defects
			if(finalDefects.size() == 1){
				Point defect_one = finalDefects.get(0);
				//Log.i(TAG, "Gesture :: PointSelectInit Detected");
				return defect_one;				
			}
		}else
			/**************** POST-STROKE *******************/
			if(initDetected && lDefects.size() == 2){
				//Log.i(TAG+"PointSelectEnd", "looking for post-stroke");
				for(int i=0; i< lDefects.size(); i++){
					start = lDefects.get(i)[0];
					//			end = contour.get(defects[i+1]);
					farthest = lDefects.get(i)[1];
					//top left screen x=0,y=0. Otherwise would be end.x > centroid.x
					//distance between the center of hand and defect
					double distanceCenterPoint = Tools.getDistanceBetweenPoints(centroid, start);
					//distance between the start of defect and farthest point
					double distancefarthestPoint = Tools.getDistanceBetweenPoints(start, farthest);
					//relation of distances from 1) center of hand to defect and 2)start of defect to farthest point 
					double relationCenterPoint_farthestPoint = distanceCenterPoint/distancefarthestPoint;
					//Log.i(TAG+"PointSelectEnd", "center_endFurtherst:: "+relationCenterPoint_farthestPoint);
					if(relationCenterPoint_farthestPoint < 2.0){
						finalDefects.add(start);
						positiveDefects = positiveDefects + 1;
					}
				}
				if(finalDefects.size() == 2){
					Point defect_one = finalDefects.get(0);
					Log.i(TAG+"PointSelect", "PointSelect Detected");
					return defect_one;				
				}				
			}	

		return null;
	}

	/*
	 * Detection of Swipe gesture
	 */
	public static Point detectSwipeGesture(List<Point[]> lDefects, Point centroid, boolean initDetected) {
		int positiveDefects = 0;
		//		int defects[] = convexityDefects.toArray();
		//		List<Point> contour = handContour.toList();
		List<Point> finalDefects = new ArrayList<Point>();
		//		Point centroid = Tools.getCentroid(handContour);
		//		MatOfPoint2f m2fHandContour = new MatOfPoint2f(handContour.toArray());

		/*
		 * convexityDefects -> structure containing (by order) start, end, depth_point, depth.
		 * depth-> farthest point (depth_point) distance to convex hull
		 */
		Point start;
		//		Point end;
		Point farthest;
		//		double distancePointHull;
		//		Log.i("Gestures::Swipe", "Swipe gesture::beginning");
		/*
		 * - Removing defects
		 * -- we want to keep only defects above centroid of hand
		 */
		if(lDefects.size() == 2){
			for(int i=0; i< lDefects.size(); i++){
				start = lDefects.get(i)[0];
				//			end = contour.get(defects[i+1]);
				farthest = lDefects.get(i)[1];
				//			distancePointHull = defects[i+3]/256.0;
				//top left screen x=0,y=0. Otherwise would be end.x > centroid.x
				//			if( (start.y < centroid.y && farthest.y < centroid.y) && (start.y < farthest.y)){
				//distance between the center of hand and defect
				double distanceCenterPoint = Tools.getDistanceBetweenPoints(centroid, start);
				//distance between the start of defect and farthest point
				double distanceEndfarthest = Tools.getDistanceBetweenPoints(start, farthest);
				//relation of distances from 1) center of hand to defect and 2)start of defect to farthest point 
				double relationCenter_EndFurtherst = distanceCenterPoint/distanceEndfarthest;
				//Log.i(TAG+"Swipe", "center_endFurtherst:: "+relationCenter_EndFurtherst);
				if(relationCenter_EndFurtherst < 2.0){
					finalDefects.add(start);
					positiveDefects = positiveDefects + 1;
				}
			}
			if(finalDefects.size() == 2){
				Point defect_one = finalDefects.get(0);
				Point defect_two = finalDefects.get(1);
				double distanceCenterPoint_1 = Tools.getDistanceBetweenPoints(defect_one, centroid);
				double distanceCenterPoint_2 = Tools.getDistanceBetweenPoints(defect_two, centroid);
				//relation of the distance between the points and the center
				//should be less than 1 because fingers are almost the same size
				// _2/_1 because defects are obtained clockwise. finger to the right is pointing finger and
				//should be smaller giving the correct result... otherwise _1/_2 > 1.0
				double relDistanceCenterPoint = distanceCenterPoint_2 / distanceCenterPoint_1;
//				Log.i(TAG+"Swipe", "relationDistCenter::"+ relDistanceCenterPoint
//						+ " point1::"+distanceCenterPoint_1
//						+ " point2::"+distanceCenterPoint_2
//						);
				if(relDistanceCenterPoint < 1.0){
					if(!initDetected){
						//Log.i(TAG, "Gesture :: SwipeInit Detected");
					}else{
						Log.i(TAG, "Gesture :: Swipe Detected");
					}
					return defect_one;
				}
			}	
		}
		return null;
	}

	/*
	 * Detection of Rotation gesture
	 */
	public static Point detectRotateGesture(List<Point[]> lDefects, Point centroid, boolean initDetected) {
		int positiveDefects = 0;
		//		int negativeDefects = 0;
		//		int defects[] = convexityDefects.toArray();
		//		List<Point> contour = handContour.toList();
		List<Point> finalDefects = new ArrayList<Point>();
		//		Point centroid = Tools.getCentroid(handContour);
		/*
		 * convexityDefects -> structure containing (by order) start, end, depth_point, depth.
		 * depth-> farthest point (depth_point) distance to convex hull
		 */
		Point start;
		//		Point end;
		Point farthest;
		//		Log.i("ImageInteraction", "Rotate gesture::beginning");
		/*
		 * Look for Rotate_Init gesture
		 * - Removing defects
		 * -- we want to keep only defects above centroid of hand
		 */
		if(lDefects.size() == 2){
			for(int i=0; i< lDefects.size(); i++){
				start = lDefects.get(i)[0];
				//			end = contour.get(defects[i+1]);
				farthest = lDefects.get(i)[1];
				//distance between the center of hand and defect
				double distanceCenterPoint = Tools.getDistanceBetweenPoints(centroid, start);
				//distance between the start of defect and farthest point
				double distancefarthestPoint = Tools.getDistanceBetweenPoints(start, farthest);
				//relation of distances from 1) center of hand to defect and 2)start of defect to farthest point 
				double relationCenterPoint_farthestPoint = distanceCenterPoint/distancefarthestPoint; 
				//top left screen x=0,y=0. Otherwise would be end.x > centroid.x
				//			if( (start.y < centroid.y && farthest.y < centroid.y) && (start.y < farthest.y)){
//				Log.i(TAG+"Rotate",
//						"relCenterPoint_farthestPoint: " + relationCenterPoint_farthestPoint
//						);
				if(relationCenterPoint_farthestPoint < 2.0){
					finalDefects.add(start);
					positiveDefects = positiveDefects + 1;
				}
				//				else{
				//					negativeDefects = negativeDefects + 1;
				//				}
				//			}
			}
			//			Log.i(TAG+"Rotate","finalDefects: "+ finalDefects.size());			

			if(positiveDefects == 2){
				Point defect_one = finalDefects.get(0);
				Point defect_two = finalDefects.get(1);
				double distanceCenterHull_one = Tools.getDistanceBetweenPoints(centroid, defect_one);
				double distanceCenterHull_two = Tools.getDistanceBetweenPoints(centroid, defect_two);
				double distanceOneTwo = Tools.getDistanceBetweenPoints(defect_one, defect_two);
				//				Core.line(mRgb, defect_one, centroid, blue, 3);
				//				//			        line from center of contour to convexhull point
				//				Core.line(mRgb, defect_two, centroid, blue, 3);
				//				Core.line(mRgb, defect_two, defect_one, blue, 3);
				//			        points
				//			Core.circle(mRgb, defect_one, 5, Tools.red, -1);
				//			Core.circle(mRgb, defect_two, 5, Tools.red, -1);
				//distance between fingers has to be greather than 1.3 times the distance from the
				//center to both points
				if((distanceOneTwo >= distanceCenterHull_one ) && (distanceOneTwo >= distanceCenterHull_two)){
					if(!initDetected){
						Point detectedPoint = Tools.getPointBetweenPoints(defect_one, defect_two);
						//					Core.circle(mRgb, rotateInitPos, 5, Tools.magenta, -1);
						//Log.i(TAG, "Gesture :: RotateInit Detected");

						return detectedPoint;
					}else{
						Point detectedPoint = Tools.getPointBetweenPoints(defect_one, defect_two);
						Log.i(TAG, "Gesture :: Rotate Detected");

						//					Log.i("null-check", "init::"+ rotateInitPos.toString());
						//					Log.i("null-check", " end::"+rotateEndPos.toString());
						return detectedPoint;
					}
				}
				//			Log.i("ImageInteraction", "Rotate gesture::defect number -> "+ positiveDefects);
			}
		}
		return null;
	}

	/*
	 * Detection of Zoom gesture
	 */
	public static double detectZoomGesture(List<Point[]> lDefects, Point centroid, boolean initDetected) {
		int positiveDefects = 0;
		int negativeDefects = 0;
		//		int defects[] = convexityDefects.toArray();
		//		List<Point> contour = handContour.toList();
		List<Point> finalDefects = new ArrayList<Point>();
		//		Point centroid = Tools.getCentroid(handContour);
		/*
		 * convexityDefects -> structure containing (by order) start, end, depth_point, depth.
		 * depth-> farthest point (depth_point) distance to convex hull
		 */
		Point start;
		//		Point end;
		Point farthest;
		//		Log.i("ImageInteraction", "Zoom gesture::beginning");
		/*
		 * Look for Zoom_Init gesture
		 * - Removing defects
		 * -- we want to keep only defects above centroid of hand
		 */
		if(lDefects.size() == 2){
			for(int i=0; i< lDefects.size(); i++){
				start = lDefects.get(i)[0];
				//			end = contour.get(defects[i+1]);
				farthest = lDefects.get(i)[1];
				//distance between the center of hand and defect
				double distanceCenterHull = Tools.getDistanceBetweenPoints(centroid, start);
				//top left screen x=0,y=0. Otherwise would be end.x > centroid.x
				//distance between the start of defect and farthest point
				double distancefarthestPoint = Tools.getDistanceBetweenPoints(start, farthest);
				//relation of distances from 1) center of hand to defect and 2)start of defect to farthest point 
				double relationCenterPoint_farthestPoint = distanceCenterHull/distancefarthestPoint; 
//				Log.i(TAG+"Swipe",
//						"relcenter_farthest: "+relationCenterPoint_farthestPoint
						//					+ " centerHull" + distanceCenterHull
						//					+ " farthest" + distancefarthest
//						);			//top left screen x=0,y=0. Otherwise would be end.x > centroid.x
				//			if((start.y < centroid.y && farthest.y < centroid.y) && (start.y < farthest.y)){
				if((relationCenterPoint_farthestPoint < 2.0)){
					finalDefects.add(start);
					positiveDefects = positiveDefects + 1;
				}else{
					negativeDefects = negativeDefects + 1;
				}
				//			}
			}
			//		Log.i("ImageInteraction","Defects Zoom :: finalDefects: "+ finalDefects.size()
			//				);			
			if(positiveDefects == 2 && negativeDefects == 0){
				Point defect_one = finalDefects.get(0);
				Point defect_two = finalDefects.get(1);
				double distanceCenterHull_one = Tools.getDistanceBetweenPoints(centroid, defect_one);
				double distanceCenterHull_two = Tools.getDistanceBetweenPoints(centroid, defect_two);
				double distanceOneTwo = Tools.getDistanceBetweenPoints(defect_one, defect_two);
				//			        points
				//			Core.circle(mRgb, defect_one, 5, Tools.red, -1);
				//			Core.circle(mRgb, defect_two, 5, Tools.red, -1);
				//-distance between the fingers has to be less than 1.3 times the distance 
				//from the center to the end of both fingers
				if(!initDetected){
					if((distanceOneTwo < distanceCenterHull_one ) && (distanceOneTwo < distanceCenterHull_two)){
						double detectedDistance = Tools.getDistanceBetweenPoints(defect_one, defect_two);
						//Log.i(TAG, "Gesture :: ZoomInit Detected");
						return detectedDistance;
					}
				}else{
					double zoomEndDistance = Tools.getDistanceBetweenPoints(defect_one, defect_two);
					Log.i(TAG, "Gesture :: Zoom Detected");
					return zoomEndDistance;
				}
			}
		}
		return -1;
	}

	/********************************** Tools *************************************************************/

	/*
	 * filter defects
	 * returns: list of array of points
	 * point[0] = start
	 * point[1] = farthest
	 */
	public static List<Point[]> filterDefects(MatOfInt4 convexityDefects, MatOfPoint handContour){
		List<Point[]> finalDefects = new ArrayList<Point[]>();

		int defects[] = convexityDefects.toArray();
		List<Point> contour = handContour.toList();
		Point centroid = Tools.getCentroid(handContour);
		/*
		 * convexityDefects -> structure containing (by order) start, end, depth_point, depth.
		 */
		Point start;
		//		Point end;
		Point farthest;
		//		float depth;
		for(int i=0; i< defects.length; i=i+4){
			start = contour.get(defects[i]);
			//			end = contour.get(defects[i+1]);
			farthest = contour.get(defects[i+2]);
			if((start.y < centroid.y && farthest.y < centroid.y) && (start.y < farthest.y)){
				Point[] points = new Point[2];
				points[0] = start;
				points[1] = farthest;
				finalDefects.add(points);
			}
		}
		return finalDefects;
	}

	public static MatOfPoint shiftContour(MatOfPoint mHand) {
		Point[] points = mHand.toArray();
		Point temp = points[0].clone();

		double toShift = temp.y;
		temp.y = temp.y * iScaleFactor;
		toShift = temp.y - toShift;		

		for(int i=0; i< points.length; i++){
			points[i].y += toShift; 
		}
		MatOfPoint mResult = new MatOfPoint();
		mResult.fromArray(points);
		return mResult;
	}
}
