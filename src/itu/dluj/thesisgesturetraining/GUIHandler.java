package itu.dluj.thesisgesturetraining;

import itu.dluj.gestures.Gestures;
import itu.dluj.gestures.Tools;

import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class GUIHandler {

	private int screenWidth;
	private int screenHeight;

	private Point[] backButtonCoords;
	private Point[] imagesButtonCoord;
	private Point[] patientOneCoords;
	private Point[] patientTwoCoords;
	private Point[] patientInfoCoords;
	private Point[] patientImgsCoords;
	private Point[] fullScreenImgCoords;

	private Mat mPointSelectIcon;
	private Mat mPointSelectIconSelected;
	private Mat mSwipeIcon;
	private Mat mSwipeIconSelected;
	private Mat mInitIcon;
	private Mat mZoomIcon;
	private Mat mZoomIconSelected;
	private Mat mRotateIcon;
	private Mat mRotateIconSelected;
	private Rect[] gestureIconsRoi;
	private Point[] gestureIconsCoords;

	private String hoverNone = "none";
	private String currentHover = hoverNone;
	private String hoverPatient1 = "hoverPatient1";
	private String hoverPatient2 = "hoverPatient2";
	private String hoverbackbutton = "hoverBackButton";
	private String hoverImgsButton = "hoverImgsButton";
	private String hoverImg1 = "hoverImg1";
	private String hoverImg2 = "hoverImg2";
	private String hoverBigImage = "hoverBigImage";

	//	private Point pBigImgCenter;
	//	private Size sBigImageOriginalSize;
	private int zoomLevel;
	private int maxZoom = 2;
	private Point pFullScreenImgCenter;

	private String[] patientInfoText;
	private Point infoCoords;
	private Point warningCoords;

	private Rect[] patientImgsRoi;
	private Rect[] profilePicRoi;
	private Rect fullScreenImgRoi;

	private Rect aidRoi;
	private Mat mAidRotate;
	private Mat mAidSwipe;
	private Mat mAidZoom;
	private Mat mRgb;

	public boolean imagesBtnClicked;
	public boolean backBtnClicked;
	//	public boolean bigImgShowing;
	private int iCurrentPatient;
	private String sCurrentPatient;
	private int iCurrentImg;
	private int iCurrentImgPage;
	private int numberImgs = 4;

	private Mat[] mPatientImages;
	private Mat[] mFullScreenImages;
	private Mat[] mProfilePics;

	private Resources resources;

	private Context appContext;

	public GUIHandler(int width, int height, Context context){
		screenWidth = width;
		screenHeight = height;
		imagesBtnClicked = false;
		//		imagesBtnClicked = true;
		backBtnClicked = false;

		mRgb = new Mat();

		zoomLevel = 0;
		//		pFullScreenImgCenter = new Point (0, 0);

		appContext = context;
		resources = appContext.getResources();

		//0 to 2
		iCurrentImg = 0;
		//1, 2, 3,----, n
		iCurrentImgPage = 1;
		//0 or 1... -1 == null
		iCurrentPatient = -1;
		sCurrentPatient = "N/A";

		warningCoords = new Point(screenWidth*0.05, screenHeight*0.95);
		infoCoords = new Point(screenWidth*0.60, screenHeight*0.95);

		profilePicRoi = new Rect[2];// 0 == patient0, 1 == patient1
		mProfilePics = new Mat[2];
		/***************************ICONS***********************************/
		mPointSelectIcon = new Mat();
		mPointSelectIconSelected = new Mat();
		mSwipeIcon = new Mat();
		mSwipeIconSelected = new Mat();
		mZoomIcon  = new Mat();
		mZoomIconSelected  = new Mat();
		mRotateIcon = new Mat();
		mRotateIconSelected = new Mat();
		mInitIcon = new Mat();


		gestureIconsRoi = new Rect[4];
		gestureIconsCoords = new Point[8];
		gestureIconsCoords[0] = new Point(0.0, screenHeight*0.24);//color rectangle
		gestureIconsCoords[1] = new Point(0.0, screenHeight*0.25);// picture
		gestureIconsCoords[2] = new Point(0.0, screenHeight*0.39);//c
		gestureIconsCoords[3] = new Point(0.0, screenHeight*0.40);//p
		gestureIconsCoords[4] = new Point(0.0, screenHeight*0.54);//c
		gestureIconsCoords[5] = new Point(0.0, screenHeight*0.55);//p
		gestureIconsCoords[6] = new Point(0.0, screenHeight*0.69);//c
		gestureIconsCoords[7] = new Point(0.0, screenHeight*0.70);//p

		int widthIcon = (int)  Math.ceil((double)(screenHeight*0.14));
		int heightIcon = widthIcon;
		//POINT SELECT ICON NORMAL
		gestureIconsRoi[0] = new Rect(gestureIconsCoords[1],new Size(widthIcon, heightIcon));
		Bitmap icon = BitmapFactory.decodeResource(resources, R.drawable.icon_pointselect_normal);
		Bitmap bitmap = Bitmap.createScaledBitmap( icon, widthIcon, heightIcon, true);
		Utils.bitmapToMat(bitmap, mPointSelectIcon, true);
		Imgproc.cvtColor(mPointSelectIcon, mPointSelectIcon, Imgproc.COLOR_RGBA2RGB);
		//POINT SELECT ICON SELECTED
		icon = BitmapFactory.decodeResource(resources, R.drawable.icon_pointselect_selected);
		bitmap = Bitmap.createScaledBitmap( icon, widthIcon, heightIcon, true);
		Utils.bitmapToMat(bitmap, mPointSelectIconSelected, true);
		Imgproc.cvtColor(mPointSelectIconSelected, mPointSelectIconSelected, Imgproc.COLOR_RGBA2RGB);
		//INIT ICON
		icon = BitmapFactory.decodeResource(resources, R.drawable.icon_gesture_init_normal);
		bitmap = Bitmap.createScaledBitmap( icon, widthIcon, heightIcon, true);
		Utils.bitmapToMat(bitmap, mInitIcon, true);
		Imgproc.cvtColor(mInitIcon, mInitIcon, Imgproc.COLOR_RGBA2RGB);		
		//SWIPE ICON
		gestureIconsRoi[1] = new Rect(gestureIconsCoords[3],new Size(widthIcon, heightIcon));
		icon = BitmapFactory.decodeResource(resources, R.drawable.icon_gesture_swipe_normal);
		bitmap = Bitmap.createScaledBitmap( icon, widthIcon, heightIcon, true);
		Utils.bitmapToMat(bitmap, mSwipeIcon, true);
		Imgproc.cvtColor(mSwipeIcon, mSwipeIcon, Imgproc.COLOR_RGBA2RGB);
		//SWIPE ICON SELECTED
		gestureIconsRoi[1] = new Rect(gestureIconsCoords[3],new Size(widthIcon, heightIcon));
		icon = BitmapFactory.decodeResource(resources, R.drawable.icon_gesture_swipe_selected);
		bitmap = Bitmap.createScaledBitmap( icon, widthIcon, heightIcon, true);
		Utils.bitmapToMat(bitmap, mSwipeIconSelected, true);
		Imgproc.cvtColor(mSwipeIconSelected, mSwipeIconSelected, Imgproc.COLOR_RGBA2RGB);
		//ZOOM
		gestureIconsRoi[2] = new Rect(gestureIconsCoords[5],new Size(widthIcon, heightIcon));
		icon = BitmapFactory.decodeResource(resources, R.drawable.icon_gesture_zoom_normal);
		bitmap = Bitmap.createScaledBitmap( icon, widthIcon, heightIcon, true);
		Utils.bitmapToMat(bitmap, mZoomIcon, true);
		Imgproc.cvtColor(mZoomIcon, mZoomIcon, Imgproc.COLOR_RGBA2RGB);
		//ZOOM SELECTED
		gestureIconsRoi[2] = new Rect(gestureIconsCoords[5],new Size(widthIcon, heightIcon));
		icon = BitmapFactory.decodeResource(resources, R.drawable.icon_gesture_zoom_selected);
		bitmap = Bitmap.createScaledBitmap( icon, widthIcon, heightIcon, true);
		Utils.bitmapToMat(bitmap, mZoomIconSelected, true);
		Imgproc.cvtColor(mZoomIconSelected, mZoomIconSelected, Imgproc.COLOR_RGBA2RGB);
		//ROTATE
		gestureIconsRoi[3] = new Rect(gestureIconsCoords[7],new Size(widthIcon, heightIcon));
		icon = BitmapFactory.decodeResource(resources, R.drawable.icon_gesture_rotate_normal);
		bitmap = Bitmap.createScaledBitmap( icon, widthIcon, heightIcon, true);
		Utils.bitmapToMat(bitmap, mRotateIcon, true);
		Imgproc.cvtColor(mRotateIcon, mRotateIcon, Imgproc.COLOR_RGBA2RGB);
		//ROTATE SELECTED
		gestureIconsRoi[3] = new Rect(gestureIconsCoords[7],new Size(widthIcon, heightIcon));
		icon = BitmapFactory.decodeResource(resources, R.drawable.icon_gesture_rotate_selected);
		bitmap = Bitmap.createScaledBitmap( icon, widthIcon, heightIcon, true);
		Utils.bitmapToMat(bitmap, mRotateIconSelected, true);
		Imgproc.cvtColor(mRotateIconSelected, mRotateIconSelected, Imgproc.COLOR_RGBA2RGB);
		icon.recycle();

		//Coords [0] == upper left inner rectangle
		//Coords [1] == lower right inner rectangle
		//Coords [2] == upper left outer rectangle
		//Coords [3] == lower right outer rectangle
		//Coords [4] == text coords

		/**********************BACK BUTTON******************************/
		//BackButton coords
		backButtonCoords = new Point[10];
		backButtonCoords[0] = new Point(0.0, 0.0);//normal coordinates top-left clickable
		backButtonCoords[1] = new Point(screenWidth*0.50, screenHeight);//n bottom -right 
		backButtonCoords[2] = new Point(0.0, 0.0);//n top-left hover
		backButtonCoords[3] = new Point(screenWidth*0.50, screenHeight*0.15);//n bottom - right
		backButtonCoords[4] = new Point(screenWidth*0.19, screenHeight*0.10);//n text
		backButtonCoords[5] = new Point(0.0, 0.0);//fullscreenImg coordinates top-left clickable
		backButtonCoords[6] = new Point(screenWidth*0.35, screenHeight);//f bottom-right
		backButtonCoords[7] = new Point(0.0, 0.0);//f top-left hover
		backButtonCoords[8] = new Point(screenWidth*0.35, screenHeight*0.15);//f bottom-right
		backButtonCoords[9] = new Point(screenWidth*0.05, screenHeight*0.10);//f text

		/**********************IMAGES BUTTON******************************/

		//ImagesButton coords
		imagesButtonCoord = new Point[5];
		imagesButtonCoord[0] = new Point(screenWidth*0.50, 0.0); // top-left clickable
		imagesButtonCoord[1] = new Point(screenWidth, screenHeight); //bottom-right
		imagesButtonCoord[2] = new Point(screenWidth*0.50, 0.0); // top-left hover
		imagesButtonCoord[3] = new Point(screenWidth, screenHeight*0.15); //bottom-right
		imagesButtonCoord[4] = new Point(screenWidth*0.69, screenHeight*0.10); //text

		/**********************PATIENT ONE******************************/
		//PatientOne coords
		patientOneCoords = new Point[6];
		patientOneCoords[0] = new Point(0.0, 0.0);//left-top clickable rectangle
		patientOneCoords[1] = new Point(screenWidth*0.50, screenHeight);//right-bottom
		patientOneCoords[2] = new Point(0.0, 0.0); // left-top hover rectangle
		patientOneCoords[3] = new Point(screenWidth*0.50, screenHeight*0.14);//right-bottom
		patientOneCoords[4] = new Point(screenWidth*0.05, screenHeight*0.10);//text
		patientOneCoords[5] = new Point(screenWidth*0.10, screenHeight*0.25); //profile pic

		int widthProfPic = (int) Math.abs((patientOneCoords[1].x - patientOneCoords[5].x)*0.90);
		int heightProfPic = (int) Math.abs((patientOneCoords[1].y - patientOneCoords[5].y)*0.90);
		profilePicRoi[0] = new Rect(patientOneCoords[5],new Size(widthProfPic, heightProfPic));
		Bitmap profilePic = BitmapFactory.decodeResource(resources, R.drawable.profilepic);
		bitmap = Bitmap.createScaledBitmap( profilePic, widthProfPic, heightProfPic, true);
		mProfilePics[0] = new Mat();
		Utils.bitmapToMat(bitmap, mProfilePics[0], true);
		Imgproc.cvtColor(mProfilePics[0], mProfilePics[0], Imgproc.COLOR_RGBA2RGB);

		/**********************PATIENT TWO******************************/

		//PatientTwo Coords
		patientTwoCoords = new Point[6];
		patientTwoCoords[0] = new Point(screenWidth*0.50, 0.0);//left-top clickable rect
		patientTwoCoords[1] = new Point(screenWidth, screenHeight);//right bottom
		patientTwoCoords[2] = new Point(screenWidth*0.50, 0.0); //left-top hover rect
		patientTwoCoords[3] = new Point(screenWidth, screenHeight*0.14); //right-bottom
		patientTwoCoords[4] = new Point(screenWidth*0.55, screenHeight*0.10);//text
		patientTwoCoords[5] = new Point(screenWidth*0.60, screenHeight*0.25);//profile pic

		widthProfPic = (int) Math.abs((patientTwoCoords[1].x - patientTwoCoords[5].x)*0.90);
		heightProfPic = (int) Math.abs((patientTwoCoords[1].y - patientTwoCoords[5].y)*0.90);
		profilePicRoi[1] = new Rect(patientTwoCoords[5],new Size(widthProfPic, heightProfPic));
		//uncomment next two lines in case of different profile pic
		//		profilePic = BitmapFactory.decodeResource(resources, R.drawable.profilepic);
		//		bitmap = Bitmap.createScaledBitmap( profilePic, widthProfPic, heightProfPic, true);
		mProfilePics[1] = new Mat();
		Utils.bitmapToMat(bitmap, mProfilePics[1], true);
		Imgproc.cvtColor(mProfilePics[1], mProfilePics[1], Imgproc.COLOR_RGBA2RGB);

		/**********************PATIENT INFO******************************/

		//PatientInfo Coords
		patientInfoCoords = new Point[3];
		patientInfoCoords[0] = new Point(screenWidth*0.10, screenHeight*0.25);
		patientInfoCoords[1] = new Point(screenWidth, screenHeight);
		patientInfoCoords[2] = new Point(screenWidth*0.12, screenHeight*0.35); //text

		/**********************PATIENT IMAGES******************************/
		//PatientImages Coords
		patientImgsCoords = new Point[10];
		patientImgsCoords[0] = new Point(screenWidth*0.10, screenHeight*0.23);//top-left hover img1
		patientImgsCoords[1] = new Point(screenWidth*0.50, screenHeight*0.25);//bottom-right hover img1
		patientImgsCoords[2] = new Point(screenWidth*0.10, screenHeight*0.25);//img1 upperleft
		patientImgsCoords[3] = new Point(screenWidth*0.50, screenHeight);//img1 lowerright
		patientImgsCoords[4] = new Point(screenWidth*0.50, screenHeight*0.25);//img2 upperleft
		patientImgsCoords[5] = new Point(screenWidth*0.90, screenHeight);//img2 lowerright
		patientImgsCoords[6] = new Point(screenWidth*0.50, screenHeight*0.23);//top-left hover img2
		patientImgsCoords[7] = new Point(screenWidth*0.90, screenHeight*0.25);//top-left hover img2
		patientImgsCoords[8] = new Point(screenWidth*0.10, screenHeight*0.23);//img1 outer upperleft
		patientImgsCoords[9] = new Point(screenWidth*0.50, screenHeight*0.23);//img2 outer upperleft

		/**********************FULLSCREEN IMAGE******************************/
		//fullScreenImg Coords
		fullScreenImgCoords = new Point[5];
		fullScreenImgCoords[0] = new Point(screenWidth*0.35, 0.0); //top-left clickable
		fullScreenImgCoords[1] = new Point(screenWidth, screenHeight); //bottom-right
		fullScreenImgCoords[2] = new Point(screenWidth*0.35, 0.0); // top-left hover
		fullScreenImgCoords[3] = new Point(screenWidth, screenHeight*0.02); // bottom-right
		fullScreenImgCoords[4] = new Point((screenWidth-(screenHeight-screenHeight*0.02)), screenHeight*0.02);//top left point where images will be drawn

		/**********************TEXT******************************/
		//PatientInfo Text
		patientInfoText = new String[6];
		//		patientInfoText[0] = "Lorem ipsum dolor sit amet,";
		patientInfoText[0] = "Say: Hello, I am playing a doctor,";
		//		patientInfoText[1] = "consectetur adipisicing elit,";
		patientInfoText[1] = "this is an example of a record.";
		//		patientInfoText[2] = "sed do eiusmod tempor incididunt";
		patientInfoText[2] = "Now, click the Images button above";
		//		patientInfoText[3] = "ut labore et dolore magna aliqua.";
		patientInfoText[3] = "to see the images!.";
		patientInfoText[4] = "Please select number 3.";
		//		patientInfoText[4] = "Ut enim ad minim veniam...";
		patientInfoText[5] = "When clicked, it will appear bigger!";

		/**********************IMAGES******************************/
		//load images just one time
		/*********Normal scale images**********/
		Bitmap bitmap_0 = BitmapFactory.decodeResource(resources, R.drawable.xr_0);
		Bitmap bitmap_1 = BitmapFactory.decodeResource(resources, R.drawable.xr_1);
		Bitmap bitmap_2 = BitmapFactory.decodeResource(resources, R.drawable.xr_2);
		Bitmap bitmap_3 = BitmapFactory.decodeResource(resources, R.drawable.xr_3);

		mPatientImages = new Mat[4];
		patientImgsRoi = new Rect[2];

		patientImgsRoi[0] = new Rect(patientImgsCoords[2], patientImgsCoords[3]);
		patientImgsRoi[1] = new Rect(patientImgsCoords[4], patientImgsCoords[5]);

		int bWidth = (int)(patientImgsCoords[3].x - patientImgsCoords[2].x);
		int bHeight = (int)(patientImgsCoords[3].y - patientImgsCoords[2].y);

		//image 0
		bitmap = Bitmap.createScaledBitmap( bitmap_0, bWidth, bHeight, true);
		mPatientImages[0] = new Mat();
		Utils.bitmapToMat(bitmap, mPatientImages[0], true);
		Imgproc.cvtColor(mPatientImages[0], mPatientImages[0], Imgproc.COLOR_RGBA2RGB);

		//image 1
		bitmap = Bitmap.createScaledBitmap(bitmap_1, bWidth, bHeight, true);
		mPatientImages[1] = new Mat();
		Utils.bitmapToMat(bitmap, mPatientImages[1], true);
		Imgproc.cvtColor(mPatientImages[1], mPatientImages[1], Imgproc.COLOR_RGBA2RGB);

		//image 2
		bitmap = Bitmap.createScaledBitmap(bitmap_2, bWidth, bHeight, true);
		mPatientImages[2] = new Mat();
		Utils.bitmapToMat(bitmap, mPatientImages[2], true);
		Imgproc.cvtColor(mPatientImages[2], mPatientImages[2], Imgproc.COLOR_RGBA2RGB);
		//image 3
		bitmap = Bitmap.createScaledBitmap(bitmap_3, bWidth, bHeight, true);
		mPatientImages[3] = new Mat();
		Utils.bitmapToMat(bitmap, mPatientImages[3], true);
		Imgproc.cvtColor(mPatientImages[3], mPatientImages[3], Imgproc.COLOR_RGBA2RGB);

		//ROI for Aid feedback
		aidRoi = new Rect(new Point(screenWidth*0.15, screenHeight*0.35), new Point(screenWidth*0.85, screenHeight*0.85));
		//aid rotate
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.rotate_aid);
		bitmap = Bitmap.createScaledBitmap( bitmap, aidRoi.width, aidRoi.height, true);
		mAidRotate = new Mat();
		Utils.bitmapToMat(bitmap, mAidRotate, true);
		Imgproc.cvtColor(mAidRotate, mAidRotate, Imgproc.COLOR_RGBA2RGB);
		//aid swipe
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.swipe_aid);
		bitmap = Bitmap.createScaledBitmap( bitmap, aidRoi.width, aidRoi.height, true);
		mAidSwipe = new Mat();
		Utils.bitmapToMat(bitmap, mAidSwipe, true);
		Imgproc.cvtColor(mAidSwipe, mAidSwipe, Imgproc.COLOR_RGBA2RGB);		
		//aid zoom
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.zoom_aid);
		bitmap = Bitmap.createScaledBitmap( bitmap, aidRoi.width, aidRoi.height, true);
		mAidZoom = new Mat();
		Utils.bitmapToMat(bitmap, mAidZoom, true);
		Imgproc.cvtColor(mAidZoom, mAidZoom, Imgproc.COLOR_RGBA2RGB);		

		/*********************** Full screen images*****************************/
		mFullScreenImages = new Mat[4];

		bHeight = (int)Math.ceil( (double)(fullScreenImgCoords[1].y - fullScreenImgCoords[4].y));
		bWidth = (int)Math.ceil( (double)(fullScreenImgCoords[1].x - fullScreenImgCoords[4].x));

		fullScreenImgRoi = new Rect((int) (fullScreenImgCoords[4].x), (int)fullScreenImgCoords[4].y, bWidth, bHeight);
		pFullScreenImgCenter = new Point(bWidth/2, bHeight/2);

		//image 0
		bitmap = Bitmap.createScaledBitmap( bitmap_0, bWidth, bHeight, true);
		mFullScreenImages[0] = new Mat();
		Utils.bitmapToMat(bitmap, mFullScreenImages[0], true);
		Imgproc.cvtColor(mFullScreenImages[0], mFullScreenImages[0], Imgproc.COLOR_RGBA2RGB);

		//image 1
		bitmap = Bitmap.createScaledBitmap(bitmap_1, bWidth, bHeight, true);
		mFullScreenImages[1] = new Mat();
		Utils.bitmapToMat(bitmap, mFullScreenImages[1], true);
		Imgproc.cvtColor(mFullScreenImages[1], mFullScreenImages[1], Imgproc.COLOR_RGBA2RGB);

		//image 2
		bitmap = Bitmap.createScaledBitmap(bitmap_2, bWidth, bHeight, true);
		mFullScreenImages[2] = new Mat();
		Utils.bitmapToMat(bitmap, mFullScreenImages[2], true);
		Imgproc.cvtColor(mFullScreenImages[2], mFullScreenImages[2], Imgproc.COLOR_RGBA2RGB);

		//image 3
		bitmap = Bitmap.createScaledBitmap(bitmap_3, bWidth, bHeight, true);
		mFullScreenImages[3] = new Mat();
		Utils.bitmapToMat(bitmap, mFullScreenImages[3], true);
		Imgproc.cvtColor(mFullScreenImages[3], mFullScreenImages[3], Imgproc.COLOR_RGBA2RGB);

		bitmap.recycle();
		bitmap_0.recycle();
		bitmap_1.recycle();
		bitmap_2.recycle();
		bitmap_3.recycle();
	}

	/******************************** Drawing methods ****************************************/
	/*
	 * 
	 * 
	 */
	public void drawBackButton(boolean fullScreenImage){
		if(!fullScreenImage){
			if(currentHover == hoverbackbutton){
				Core.rectangle(mRgb, backButtonCoords[2], backButtonCoords[3], Tools.green, -1);
			}else{
				Core.rectangle(mRgb, backButtonCoords[2], backButtonCoords[3], Tools.blue, -1);
			}
			Core.rectangle(mRgb, backButtonCoords[0], backButtonCoords[1], Tools.lightGray, 1);
			mRgb = writeToImage(mRgb, backButtonCoords[4], "Back");
		}else{
			if(currentHover == hoverbackbutton){
				Core.rectangle(mRgb, backButtonCoords[7], backButtonCoords[8], Tools.green, -1);
			}else{
				Core.rectangle(mRgb, backButtonCoords[7], backButtonCoords[8], Tools.blue, -1);
			}
			Core.rectangle(mRgb, backButtonCoords[5], backButtonCoords[6], Tools.lightGray, 2);
			mRgb = writeToImage(mRgb, backButtonCoords[9], "Back", 0.7);
		}
	}

	/*
	 * 
	 * 
	 */
	public void drawImagesButton(){
		if(currentHover == hoverImgsButton){
			Core.rectangle(mRgb, imagesButtonCoord[2], imagesButtonCoord[3], Tools.green, -1);
		}else{
			Core.rectangle(mRgb, imagesButtonCoord[2], imagesButtonCoord[3], Tools.blue, -1);
		}
		Core.rectangle(mRgb, imagesButtonCoord[0], imagesButtonCoord[1], Tools.lightGray, 1);
		if(!imagesBtnClicked){
			mRgb = writeToImage(mRgb, imagesButtonCoord[4], "Images");
		}else{
			mRgb = writeToImage(mRgb, imagesButtonCoord[4], "Info");
		}
	}

	/*
	 * 
	 * 
	 */
	public void drawPatientsToSelect(){
		//		Mat rec = Mat.zeros(size, type);
		//Patient One

		if(currentHover == hoverPatient1){
			Core.rectangle(mRgb, patientOneCoords[2], patientOneCoords[3], Tools.green, -1);
		}else{
			Core.rectangle(mRgb, patientOneCoords[2], patientOneCoords[3], Tools.blue, -1);
		}
		Core.rectangle(mRgb, patientOneCoords[0], patientOneCoords[1], Tools.lightGray, 1);
		mRgb = writeToImage(mRgb, patientOneCoords[4], "Patient: Tom", 0.7);
		Log.i("GUIHandler", " profPicRoi::"+profilePicRoi[0].size().toString()
				+ " profPicSize::"+ mProfilePics[0].size().toString()
				);
		Core.addWeighted(mRgb.submat(profilePicRoi[0]), 0.0, mProfilePics[0], 1.0, 0, mRgb.submat(profilePicRoi[0]));

		//Patient Two

		if(currentHover == hoverPatient2){
			Core.rectangle(mRgb, patientTwoCoords[2], patientTwoCoords[3], Tools.green, -1);
		}else{
			Core.rectangle(mRgb, patientTwoCoords[2], patientTwoCoords[3], Tools.blue, -1);
		}
		Core.rectangle(mRgb, patientTwoCoords[0], patientTwoCoords[1], Tools.lightGray, 1);
		mRgb = writeToImage(mRgb, patientTwoCoords[4], "Patient: Paul", 0.7);
		Log.i("GUIHandler", " profPicRoi2::"+profilePicRoi[1].size().toString()
				+ " profPicSize::"+ mProfilePics[1].size().toString()
				);
		Core.addWeighted(mRgb.submat(profilePicRoi[1]), 0.0, mProfilePics[1], 1.0, 0, mRgb.submat(profilePicRoi[1]));
	}

	/*
	 * 
	 * 
	 */
	public void drawPatientInfo(){
		//		Mat rec = Mat.zeros(size, type);
		drawImagesButton();
		drawBackButton(false);
		if(!imagesBtnClicked){
			Core.rectangle(mRgb, patientInfoCoords[0], patientInfoCoords[1], Tools.lightGray, -1);
			mRgb = writeToImage(mRgb, patientInfoCoords[2], sCurrentPatient +"'s Info");

			Point temp = patientInfoCoords[2].clone();
			for(int i = 0; i<patientInfoText.length; i++){
				temp.y = temp.y + screenHeight*0.09;
				//			Log.i("GUIHandler","Point "+i+"::"+temp.toString());
				mRgb = writeToImage(mRgb, temp, patientInfoText[i], 0.55);
			}
		}else{
			drawPatientImages();
		}
	}

	/*
	 * 
	 * 
	 */
	public void drawPatientImages(){
		if(currentHover == hoverImg1){
			Core.rectangle(mRgb, patientImgsCoords[0], patientImgsCoords[1], Tools.green, -1);
		}else{
			Core.rectangle(mRgb, patientImgsCoords[0], patientImgsCoords[1], Tools.blue, -1);
		}

		if(currentHover == hoverImg2){
			Core.rectangle(mRgb, patientImgsCoords[6], patientImgsCoords[7], Tools.green, -1);
		}else{
			Core.rectangle(mRgb, patientImgsCoords[6], patientImgsCoords[7], Tools.blue, -1);
		}

		//		Log.i("GUIHandler","rec::"+mRgb.submat(patientImgsRoi[1]).size().toString()+" mBitmap::"+mPatientImages[iCurrentImg].size().toString());

		Core.addWeighted(mRgb.submat(patientImgsRoi[0]), 0.0, mPatientImages[iCurrentImgPage*2 - 2], 1.0, 0, mRgb.submat(patientImgsRoi[0]));
		mRgb = writeToImage(mRgb, new Point(patientImgsRoi[0].tl().x*1.05, patientImgsRoi[0].tl().y*1.20), ""+(iCurrentImgPage*2 - 1), 0.4);

		Core.addWeighted(mRgb.submat(patientImgsRoi[1]), 0.0, mPatientImages[iCurrentImgPage*2 - 1], 1.0, 0, mRgb.submat(patientImgsRoi[1]));
		mRgb = writeToImage(mRgb,  new Point(patientImgsRoi[1].tl().x*1.05, patientImgsRoi[1].tl().y*1.20), ""+(iCurrentImgPage*2), 0.4);

		Point pPageNumberCircle = new Point();
		pPageNumberCircle.x = patientImgsCoords[9].x*0.97;
		pPageNumberCircle.y = patientImgsCoords[9].y*0.90;

		for(int i = 1; i<= (int)Math.ceil((double)numberImgs/2); i++){
			if(i == iCurrentImgPage){
				Core.circle(mRgb, pPageNumberCircle, 4, Tools.white, -1);
			}else{
				Core.circle(mRgb, pPageNumberCircle, 4, Tools.white, 1);				
			}
			pPageNumberCircle.x = pPageNumberCircle.x * 1.06;
		}
	}

	/*
	 * 
	 * 
	 */
	public void drawFullScreenImage(){
		//		Mat rec = Mat.zeros(size, type);
		//Doc One
		drawBackButton(true);
		//patInfoCoords[5] upper left point for image
		//patInfoCoords[1] lower righ point for image (same as both squares)

		if(zoomLevel != 0){

			Mat[] toDraw = new Mat[zoomLevel+1];

			int finalWidth = fullScreenImgRoi.width;
			int finalHeight = fullScreenImgRoi.height;
			int smallWidth = (int) Math.ceil((double)(finalWidth/( Math.pow(2,zoomLevel)))); //for pyrUp to work the sizes have to be mult of 2
			int smallHeight = (int) Math.ceil((double)(finalHeight/( Math.pow(2,zoomLevel))));

			//			int smallX = (int) ((finalWidth - smallWidth)/( Math.pow(2,zoomLevel)));
			//			int smallY = (int) ((finalHeight - smallHeight)/( Math.pow(2,zoomLevel))); //for zoomed image

			int smallX = (int) (pFullScreenImgCenter.x - (smallWidth/2)) ;
			int smallY = (int) (pFullScreenImgCenter.y - (smallHeight/2));

			int bigWidth = smallWidth*2;
			int bigHeight = smallHeight*2;

			while(smallX < 0){
				pFullScreenImgCenter.x = pFullScreenImgCenter.x + 10;
				smallX = (int) ((pFullScreenImgCenter.x - (smallWidth/2))/( Math.pow(2,zoomLevel)));
			}
			while(smallY < 0){
				pFullScreenImgCenter.y = pFullScreenImgCenter.y + 10;
				smallY = (int) ((pFullScreenImgCenter.y - (smallHeight/2))/( Math.pow(2,zoomLevel)));
			}
			while((smallX + smallWidth) >= finalWidth){
				pFullScreenImgCenter.x = pFullScreenImgCenter.x - 10;
				smallX = (int) ((pFullScreenImgCenter.x - (smallWidth/2))/( Math.pow(2,zoomLevel)));
			}
			while((smallY + smallHeight) >= finalHeight){
				pFullScreenImgCenter.y = pFullScreenImgCenter.y - 10;
				smallY = (int) ((pFullScreenImgCenter.y - (smallHeight/2))/( Math.pow(2,zoomLevel)));

			}
			//			Log.i("GUIHandler", "DrawFullScreen::NewCenter::"+pFullScreenImgCenter.toString());
			Rect zoomRoi = new Rect(smallX, smallY, smallWidth, smallHeight);
			Size bigSize = new Size(bigWidth, bigHeight);	
			toDraw[0] = mFullScreenImages[iCurrentImg].submat(zoomRoi);

			for(int i=1; i<=zoomLevel; i++){

				bigSize = new Size(bigWidth, bigHeight);				
				toDraw[i] = new Mat();
				Imgproc.pyrUp(toDraw[i-1], toDraw[i], bigSize);
				//				Log.i("GUIHandler", " zoomLev::"+i
				//						+ " smallToDraw::"+ toDraw[i-1].size().toString()
				//						+ " newSize::"+ bigSize.toString()
				//						+ " toDrawSize::"+ toDraw[i].size().toString() 
				//						);
				bigWidth = bigWidth*2;
				bigHeight = bigHeight*2;

			}

			Log.i("GUIHandler","rec::"+mRgb.submat(fullScreenImgRoi).size().toString()+" toDraw::"+toDraw[zoomLevel].size().toString()
					+ " imgRoi::"+fullScreenImgRoi.toString());
			Core.addWeighted(mRgb.submat(fullScreenImgRoi), 0.0, 
					toDraw[zoomLevel], 1.0, 0, mRgb.submat(fullScreenImgRoi));
		}else {
			Log.i("GUIHandler","rec::"+mRgb.submat(fullScreenImgRoi).size().toString()+" toDraw::"+mFullScreenImages[iCurrentImg].size().toString()
					+ " imgRoi::"+fullScreenImgRoi.toString());
			Core.addWeighted(mRgb.submat(fullScreenImgRoi), 0.0, 
					mFullScreenImages[iCurrentImg], 1.0, 0, mRgb.submat(fullScreenImgRoi));
		}
		if(currentHover == hoverBigImage){
			Core.rectangle(mRgb, fullScreenImgCoords[2], fullScreenImgCoords[3], Tools.green, -1);
		}else if(zoomLevel != 0){
			Core.rectangle(mRgb, fullScreenImgCoords[2], fullScreenImgCoords[3], Tools.blue, -1);
		}
		Core.rectangle(mRgb, fullScreenImgCoords[0], fullScreenImgCoords[1], Tools.lightGray, 2);

		Point pZoomLevel = new Point();
		pZoomLevel.x = fullScreenImgCoords[0].x*1.05;
		pZoomLevel.y = screenHeight*0.05;
		for(int i = 0; i<= maxZoom; i++){
			if(i == zoomLevel){
				Core.circle(mRgb, pZoomLevel, (2*i + 2), Tools.white, -1);
			}else{
				Core.circle(mRgb, pZoomLevel, (2*i + 2), Tools.white, 1);				
			}
			pZoomLevel.x = pZoomLevel.x * 1.10;
		}

	}


	/*
	 * 
	 * 
	 */
	public Mat drawGui(boolean goodContour, List<MatOfPoint> lHandContour, Size size, int type, String gesture, boolean drawAid, long second, String gestureInTurn){
		mRgb = Mat.zeros(size, type);
		Point temp;
		switch (gestureInTurn) {
		case GestureTrainingHandler.sStatePointSelect:
			temp = gestureIconsRoi[0].br().clone();
			temp.x = temp.x*1.15;
			temp.y = temp.y*0.85;
			Core.circle(mRgb, temp, 5, Tools.white, -1);
			break;
		case GestureTrainingHandler.sStateSwipe:
			temp = gestureIconsRoi[1].br().clone();
			temp.x = temp.x*1.15;
			temp.y = temp.y*0.85;
			Core.circle(mRgb, temp, 5, Tools.white, -1);
			break;
		case GestureTrainingHandler.sStateZoom:
			temp = gestureIconsRoi[2].br().clone();
			temp.x = temp.x*1.15;
			temp.y = temp.y*0.85;
			Core.circle(mRgb, temp, 5, Tools.white, -1);
			break;
		case GestureTrainingHandler.sStateRotate:
			temp = gestureIconsRoi[3].br().clone();
			temp.x = temp.x*1.15;
			temp.y = temp.y*0.85;
			Core.circle(mRgb, temp, 5, Tools.white, -1);
			break;
		default:
			break;
		}
	
		
		/*****************PATIENT SELECTION*********************/
		//		if(currentOverallState == GestureTrainingHandler.sPatientSelectionState){
		/****************ZERO STATE -- ONLY DRAW INIT GESTURE**************************/
		//			if(gesture == GestureTrainingHandler.sStateZero){
		//				//INIT ICON
		//				Core.addWeighted(mRgb.submat(gestureIconsRoi[0]), 0.0, mInitIcon, 1.0, 0, mRgb.submat(gestureIconsRoi[0]));
		//			}else{
		//				//SWIPE ICON IN GRAY
		//				Core.rectangle(mRgb,gestureIconsRoi[1].tl(), gestureIconsRoi[1].br(), Tools.lightGray, -1);
		//				//ZOOM ICON IN GRAY
		//				Core.rectangle(mRgb, gestureIconsRoi[2].tl(), gestureIconsRoi[2].br(), Tools.lightGray, -1);
		//				//ROTATE ICON IN GRAY
		//				Core.rectangle(mRgb, gestureIconsRoi[3].tl(), gestureIconsRoi[3].br(), Tools.lightGray, -1);
		//
		//				//POINTSELECT ICON
		Core.rectangle(mRgb, gestureIconsCoords[0], new Point( Math.ceil((double)(screenHeight*0.14)), gestureIconsCoords[1].y ), Tools.cyan, -1);
		if(gesture != GestureTrainingHandler.sStatePointSelect){
			Core.addWeighted(mRgb.submat(gestureIconsRoi[0]), 0.0, mPointSelectIcon, 1.0, 0, mRgb.submat(gestureIconsRoi[0]));
		}else{
			Core.addWeighted(mRgb.submat(gestureIconsRoi[0]), 0.0, mPointSelectIconSelected, 1.0, 0, mRgb.submat(gestureIconsRoi[0]));
		}
		//			}
		//		}else 
		/***********************RECORD VIEWING**************************/
		//			if(currentOverallState == GestureTrainingHandler.sRecordViewingState){
		//ZOOM ICON IN GRAY
		//				Core.rectangle(mRgb, gestureIconsRoi[2].tl(), gestureIconsRoi[2].br(), Tools.lightGray, -1);
		//				//ROTATE ICON IN GRAY
		//				Core.rectangle(mRgb, gestureIconsRoi[3].tl(), gestureIconsRoi[3].br(), Tools.lightGray, -1);

		//				if(imagesBtnClicked){
		//SWIPE ICON
		Core.rectangle(mRgb, gestureIconsCoords[2], new Point(Math.ceil((double)(screenHeight*0.14)), gestureIconsCoords[3].y ), Tools.white, -1);
		if(gesture != GestureTrainingHandler.sStateSwipe){
			Core.addWeighted(mRgb.submat(gestureIconsRoi[1]), 0.0, mSwipeIcon, 1.0, 0, mRgb.submat(gestureIconsRoi[1]));
		}else{
			Core.addWeighted(mRgb.submat(gestureIconsRoi[1]), 0.0, mSwipeIconSelected, 1.0, 0, mRgb.submat(gestureIconsRoi[1]));
		}
		//				}else{
		//SWIPE ICON IN GRAY
		//					Core.rectangle(mRgb,gestureIconsRoi[1].tl(), gestureIconsRoi[1].br(), Tools.lightGray, -1);		
		//				}

		//POINTSELECT ICON
		//				Core.rectangle(mRgb, gestureIconsCoords[0], new Point(Math.ceil((double)(screenHeight*0.14)), gestureIconsCoords[1].y ), Tools.cyan, -1);
		//				if(gesture != GestureTrainingHandler.sStatePointSelect){
		//					Core.addWeighted(mRgb.submat(gestureIconsRoi[0]), 0.0, mPointSelectIcon, 1.0, 0, mRgb.submat(gestureIconsRoi[0]));
		//				}else{
		//					Core.addWeighted(mRgb.submat(gestureIconsRoi[0]), 0.0, mPointSelectIconSelected, 1.0, 0, mRgb.submat(gestureIconsRoi[0]));
		//				}
		//				drawPatientInfo();
		//			}else 
		//				/********************IMAGE INTERACTION***********************/
		//				if(currentOverallState == GestureTrainingHandler.sImageInteractionState){
		//SWIPE ICON IN GRAY
		//					Core.rectangle(mRgb,gestureIconsRoi[1].tl(), gestureIconsRoi[1].br(), Tools.lightGray, -1);
		//					drawFullScreenImage();

		//POINTSELECT ICON
		//					Core.rectangle(mRgb, gestureIconsCoords[0], new Point(Math.ceil((double)(screenHeight*0.14)), gestureIconsCoords[1].y ), Tools.cyan, -1);
		//					if(gesture != GestureTrainingHandler.sStatePointSelect){
		//						Core.addWeighted(mRgb.submat(gestureIconsRoi[0]), 0.0, mPointSelectIcon, 1.0, 0, mRgb.submat(gestureIconsRoi[0]));
		//					}else{
		//						Core.addWeighted(mRgb.submat(gestureIconsRoi[0]), 0.0, mPointSelectIconSelected, 1.0, 0, mRgb.submat(gestureIconsRoi[0]));
		//					}
		//ZOOM ICON
		Core.rectangle(mRgb, gestureIconsCoords[4], new Point(Math.ceil((double)(screenHeight*0.14)), gestureIconsCoords[5].y ), Tools.orange, -1);
		if(gesture != GestureTrainingHandler.sStateZoom){
			Core.addWeighted(mRgb.submat(gestureIconsRoi[2]), 0.0, mZoomIcon, 1.0, 0, mRgb.submat(gestureIconsRoi[2]));
		}else{
			Core.addWeighted(mRgb.submat(gestureIconsRoi[2]), 0.0, mZoomIconSelected, 1.0, 0, mRgb.submat(gestureIconsRoi[2]));
		}
		//ROTATE ICON
		Core.rectangle(mRgb, gestureIconsCoords[6], new Point(Math.ceil((double)(screenHeight*0.14)), gestureIconsCoords[7].y ), Tools.magenta, -1);
		if(gesture != GestureTrainingHandler.sStateRotate){
			Core.addWeighted(mRgb.submat(gestureIconsRoi[3]), 0.0, mRotateIcon, 1.0, 0, mRgb.submat(gestureIconsRoi[3]));
		}else{
			Core.addWeighted(mRgb.submat(gestureIconsRoi[3]), 0.0, mRotateIconSelected, 1.0, 0, mRgb.submat(gestureIconsRoi[3]));
		}
		//				}

		int fill = 5;
		switch (gesture) {
		case GestureTrainingHandler.sStateZero:
			currentHover = hoverNone;
			if(goodContour){
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.gray, 2);
			}else{
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.lightGray, -1);
			}
			break;
		case GestureTrainingHandler.sStateEnd:
			currentHover = hoverNone;
			if(goodContour){
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.lightGreen, fill);
			}else{
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.lightGray, -1);
			}
			break;
		case GestureTrainingHandler.sStateInit:
			currentHover = hoverNone;
			if(goodContour){
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.green, 2);
			}else{
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.lightGray, -1);
			}
			break;
		case GestureTrainingHandler.sStatePointSelect:
			if(goodContour){
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.cyan, fill);
			}else{
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.lightGray, -1);
			}
			break;
		case GestureTrainingHandler.sStateRotate:
			currentHover = hoverNone;
			if(goodContour){
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.magenta, fill);
			}else{
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.lightGray, -1);
			}
			break;
		case GestureTrainingHandler.sStateSwipe:
			currentHover = hoverNone;
			if(goodContour){
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.white,fill);
			}else{
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.lightGray, -1);
			}
			break;
		case GestureTrainingHandler.sStateZoom:
			currentHover = hoverNone;
			if(goodContour){
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.orange, fill);
			}else{
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.lightGray, -1);
			}
			break;
		default:
			break;
		}

		if(second >= 1.0){
			Imgproc.drawContours(mRgb, lHandContour, -1, Tools.red, -1);
		}

		if(drawAid){
			drawAid(gesture);
		}

		mRgb = writeInfoToImage(mRgb, gestureInTurn);

		return mRgb;
	}


	/*
	 * 
	 * 
	 */
	public void drawAid(String gesture){
		//		Mat rec = mRgb;
		if(gesture == GestureTrainingHandler.sStateRotate){
			Core.addWeighted(mRgb.submat(aidRoi), 0.2, mAidRotate, 1.0, 0, mRgb.submat(aidRoi));
		}else if(gesture == GestureTrainingHandler.sStateSwipe){
			Core.addWeighted(mRgb.submat(aidRoi), 0.2, mAidSwipe, 1.0, 0, mRgb.submat(aidRoi));
		}else if(gesture == GestureTrainingHandler.sStateZoom){
			Core.addWeighted(mRgb.submat(aidRoi), 0.2, mAidZoom, 1.0, 0, mRgb.submat(aidRoi));			
		}
		//		return mRgb;
	}

	/******************************* Action methods *******************************************/
	/*
	 * onClick 
	 * returns true if click is accepted (clicked on something)
	 */
	public boolean onClick(Point click){
		////		return true;
		//		/*
		//		 * - if iCurrentPatient == -1 --> we are in PatientSelection
		//		 * 		- check in which patient the click is inside of
		//		 * Coords [2] == upper left outer rectangle
		//		 * Coords [3] == lower right outer rectangle
		//		 */
		////		if(GestureTrainingHandler.currentOverallState == GestureTrainingHandler.sPatientSelectionState){//PatSelection
		//			Rect rect_one = new Rect(patientOneCoords[0], patientOneCoords[1]);
		//			Rect rect_two = new Rect(patientTwoCoords[0], patientTwoCoords[1]);
		//			if(click.inside(rect_one)){
		//				iCurrentPatient = 0;
		//				sCurrentPatient = "Tom";
		//				currentHover = hoverNone;
		//				return true;
		//			}else if(click.inside(rect_two)){
		//				iCurrentPatient = 1;
		//				sCurrentPatient = "Paul";
		//				currentHover = hoverNone;
		//				return true;
		//			}
		////		}else if(GestureTrainingHandler.currentOverallState == GestureTrainingHandler.sRecordViewingState){//RecordViewing
		//			/*
		//			 * - if iCurrentPatient != -1 --> we are in RecordViewing
		//			 * 		- if backBtnClicked == true --> previousState
		//			 * 		- if imagesBtnClicked == true --> stay and show images
		//			 * 		- if back&&imagesBtn == false --> nextState
		//			 */
		//			Rect rect_backBtn = new Rect(backButtonCoords[0],backButtonCoords[1]);
		//			Rect rect_imagesBtn = new Rect(imagesButtonCoord[0], imagesButtonCoord[1]);
		//			//			Rect rect_img = new Rect(patientImgsCoords[2], patientImgsCoords[3]);
		//
		//			if(imagesBtnClicked){
		//				//click on left image
		//				if( imagesBtnClicked == true && click.inside(patientImgsRoi[0])){
		//					backBtnClicked = false;
		//					//				imagesBtnClicked = false;
		//					//					bigImgShowing = true;
		//					// page 1 shows images --> 0 , 1 ... 1*2 - 2 = 0
		//					iCurrentImg = iCurrentImgPage*2 - 2;
		//					currentHover = hoverNone;
		//					return true;
		//				}else 
		//					//click on right image
		//					if( imagesBtnClicked == true && click.inside(patientImgsRoi[1])){
		//						backBtnClicked = false;
		//						// page 1 shows images --> 0 , 1 ... 1*2 - 1 = 1
		//						iCurrentImg = iCurrentImgPage*2 - 1;
		//
		//						//					iCurrentImg = (iCurrentImg == (numberImgs - 1))? 0:iCurrentImg+1;
		//
		//						//				imagesBtnClicked = false;
		//						//					bigImgShowing = true;
		//						currentHover = hoverNone;
		//						return true;
		//					}
		//			}
		//			if(click.inside(rect_backBtn)){
		//				//go back to state PatientSelect
		//				iCurrentPatient = -1;
		//				sCurrentPatient = "N/A";
		//				iCurrentImg = 0;
		//				backBtnClicked = true;
		//				currentHover = hoverNone;
		//				imagesBtnClicked = false;
		//				return true;
		//			}else if(click.inside(rect_imagesBtn)){
		//				backBtnClicked = false;
		//				imagesBtnClicked = !imagesBtnClicked;
		//				currentHover = hoverNone;
		//				return false;
		//			}
		////		}else{//ImgInteraction
		//			/*
		//			 * - if bigImgShowin == true --> we are in ImageInteraction
		//			 * 		- if backBtnClicked == true --> previousState
		//			 */
		//			/*
		//			 * Coords [2] == upper left outer rectangle
		//			 * Coords [3] == lower right outer rectangle
		//			 */
		//			Rect rect_back = new Rect(backButtonCoords[5],backButtonCoords[6]);
		//			Rect rect_fullScreenImg = new Rect(fullScreenImgCoords[0], fullScreenImgCoords[1]);
		//
		//			if(click.inside(rect_back)){
		//				//go back to state PatientSelect
		//				backBtnClicked = false;
		//				//					imagesBtnClicked = false;
		//				//				bigImgShowing = false;
		//				currentHover = hoverNone;
		//				zoomLevel = 0;
		//				return true;
		//			}else if(click.inside(rect_fullScreenImg)){
		//				if(zoomLevel != 0){
		//					click.x = click.x - (screenWidth - fullScreenImgRoi.width);
		//					double alphax = (click.x - pFullScreenImgCenter.x)/(2*zoomLevel);
		//					pFullScreenImgCenter.x = pFullScreenImgCenter.x + alphax;
		//					double betay = (click.y - pFullScreenImgCenter.y)/(2*zoomLevel);
		//					pFullScreenImgCenter.y = pFullScreenImgCenter.y + betay;
		//					//					Log.i("GUIHandler", "OnClick::NewCenter::"+pFullScreenImgCenter.toString());
		//					currentHover = hoverNone;
		//				}
		//				return false;
		//			}
		////		}
		return true;
	}

	/*
	 * hover 
	 * sets Hover action over available clickable objects
	 */
	public void hover(Point click){
		/*
//		 * - if iCurrentPatient == -1 --> we are in PatientSelection
//		 * 		- check in which patient the click is inside of
//		 * Coords [2] == upper left outer rectangle
//		 * Coords [3] == lower right outer rectangle
//		 */
		////		if(GestureTrainingHandler.currentOverallState == GestureTrainingHandler.sPatientSelectionState){//PatSelection
		//			Rect rect_one = new Rect(patientOneCoords[0], patientOneCoords[1]);
		//			Rect rect_two = new Rect(patientTwoCoords[0], patientTwoCoords[1]);
		//			if(click.inside(rect_one)){
		//				currentHover = hoverPatient1;
		//				return;
		//			}else if(click.inside(rect_two)){
		//				currentHover = hoverPatient2;
		//				return;
		//			}
		////		}else if(GestureTrainingHandler.currentOverallState == GestureTrainingHandler.sRecordViewingState){//RecordViewing
		//			/*
		//			 * - if iCurrentPatient != -1 --> we are in RecordViewing
		//			 * 		- if backBtnClicked == true --> previousState
		//			 * 		- if imagesBtnClicked == true --> stay and show images
		//			 * 		- if back&&imagesBtn == false --> nextState
		//			 */
		//			Rect rect_backBtn = new Rect(backButtonCoords[0],backButtonCoords[1]);
		//			Rect rect_imagesBtn = new Rect(imagesButtonCoord[0], imagesButtonCoord[1]);
		//			//			Rect rect_img = new Rect(patientImgsCoords[2], patientImgsCoords[3]);
		//
		//			if(imagesBtnClicked){
		//				if( imagesBtnClicked == true && click.inside(patientImgsRoi[0])){
		//					currentHover = hoverImg1;
		//					return;
		//				}else if( imagesBtnClicked == true && click.inside(patientImgsRoi[1])){
		//					currentHover = hoverImg2;
		//					return;
		//				}
		//			}
		//			if(click.inside(rect_backBtn)){
		//				//hover back button
		//				currentHover = hoverbackbutton;
		//				return;
		//			}else if(click.inside(rect_imagesBtn)){
		//				//hover on images button
		//				currentHover = hoverImgsButton;
		//				return;
		//			}
		////		}else{//ImgInteraction
		//			/*
		//			 * - if bigImgShowin == true --> we are in ImageInteraction
		//			 * 		- if backBtnClicked == true --> previousState
		//			 */
		//			/*
		//			 * Coords [2] == upper left outer rectangle
		//			 * Coords [3] == lower right outer rectangle
		//			 */
		//			Rect rect_back = new Rect(backButtonCoords[5],backButtonCoords[6]);
		//			Rect rect_fullScreenImg = new Rect(fullScreenImgCoords[0], fullScreenImgCoords[1]);
		//
		//			if(click.inside(rect_back)){
		//				currentHover = hoverbackbutton;
		//				return;
		//			}else if(click.inside(rect_fullScreenImg)){
		//				if(zoomLevel != 0){
		//					currentHover = hoverBigImage;
		//				}else{
		//					currentHover = hoverNone;
		//				}
		//				return;
		//			}
		//		}
		currentHover = hoverNone;
	}

	/*
	 * swipe
	 * returns true if swipe is accepted (if images are showing)
	 */
	public boolean swipe(String side){
		//		if(imagesBtnClicked == true){
		//			if(side == "right"){
		//				//				iCurrentImg = (iCurrentImg == 0)? (numberImgs - 1): iCurrentImg - 1;
		//				iCurrentImgPage = (iCurrentImgPage == 1)? (int)Math.ceil((double)numberImgs/2) : iCurrentImgPage - 1;
		//				return true;
		//			}else if(side == "left"){
		//				//				iCurrentImg = (iCurrentImg == (numberImgs - 1))? 0: iCurrentImg + 1;
		//				iCurrentImgPage = (iCurrentImgPage == (int)Math.ceil((double)numberImgs/2))? 1 : iCurrentImgPage + 1 ;
		//				return true;
		//			}
		//		}
		return true;
	}

	/*
	 * rotate
	 * returns true if rotate is accepted
	 */
	public boolean rotate(String side){
		//		if(GestureTrainingHandler.currentOverallState == GestureTrainingHandler.sImageInteractionState){
		//			if(side == "left"){
		//				Core.transpose(mFullScreenImages[iCurrentImg], mFullScreenImages[iCurrentImg]);
		//				Core.flip(mFullScreenImages[iCurrentImg], mFullScreenImages[iCurrentImg], 0);
		//				//				mFullScreenImages[iCurrentImg] = mFullScreenImages[iCurrentImg].t();
		//				return true;
		//			}else if(side == "right"){
		//				//				mFullScreenImages[iCurrentImg] = mFullScreenImages[iCurrentImg].t();
		//				Core.transpose(mFullScreenImages[iCurrentImg], mFullScreenImages[iCurrentImg]);
		//				Core.flip(mFullScreenImages[iCurrentImg], mFullScreenImages[iCurrentImg], 1);
		//				return true;
		//			}
		//		}
		return true;
	}

	/*
	 * zoom
	 * returns true if zoom is accepted
	 * zoomLevels -> 0 (no zoom), 1, 2
	 */
	public boolean zoom(String zoom){
		//		if(GestureTrainingHandler.currentOverallState == GestureTrainingHandler.sImageInteractionState){
		//			if(zoom == "in"){
		//				zoomLevel = (zoomLevel < maxZoom)? zoomLevel + 1 : 0;
		//				return true;
		//			}else if(zoom == "out"){
		//
		//				zoomLevel = (zoomLevel > 0)? zoomLevel - 1 : 0;
		//				return true;
		//			}
		//		}
		return true;
	}

	/******************************* Utility methods ******************************************/

	/*
	 * Utility method - writes to color image
	 */
	public Mat writeInfoToImage(Mat mRgb, final String string) {
		Core.putText(mRgb, string, infoCoords, Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(0,0,0), 5);
		Core.putText(mRgb, string, infoCoords, Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255,255,255), 1);
		return mRgb;
	}

	/*
	 * Utility method - writes to color image
	 */
	public Mat writeWarningToImage(Mat mRgb, final String string) {
		Core.putText(mRgb, string, warningCoords, Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(0,0,0), 5);
		Core.putText(mRgb, string, warningCoords, Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255,255,255), 1);
		return mRgb;
	}

	/*
	 * Utility method - writes to GUI image
	 */
	public Mat writeToImage(Mat mRgb, Point point, final String string) {
		Core.putText(mRgb, string, point, Core.FONT_HERSHEY_COMPLEX_SMALL, 1.0, new Scalar(200,200,200), 2);
		return mRgb;
	}

	/*
	 * Utility method - writes to GUI image
	 */
	public Mat writeToImage(Mat mRgb, Point point, final String string, double scale) {
		Core.putText(mRgb, string, point, Core.FONT_HERSHEY_SIMPLEX, scale, new Scalar(200,200,200), 2);
		return mRgb;
	}
}
