package itu.dluj.thesisgesturetraining;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * 
 * 
 */
public class MainActivity extends Activity implements CvCameraViewListener2 {
	
//	private JavaCameraViewExtended mOpenCvCameraView;
	private JavaCameraView mOpenCvCameraView;
	private GestureTrainingHandler statesHandler;
	final Handler mHandler = new Handler();
	private String sDeviceModel = android.os.Build.MODEL;
	private int cameraIndex;
	private int screenWidth;
	private int screenHeight;
	private String TAG = "itu.dluj.thesisgesturetraining";

//	private Mat mProcessed;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(TAG, "MainActivity :: called onCreate");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);

		mOpenCvCameraView = (JavaCameraView) findViewById(R.id.cameraView);
//		mOpenCvCameraView = (JavaCameraViewExtended) findViewById(R.id.cameraView);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

		if(
//				sDeviceModel.equals("Nexus 5") ||
				sDeviceModel.equals("GT-S6810P")
				){
			mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
			cameraIndex = CameraBridgeViewBase.CAMERA_ID_FRONT;
		}else{
			mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_ANY);
			cameraIndex = CameraBridgeViewBase.CAMERA_ID_ANY;
		}
		mOpenCvCameraView.enableFpsMeter();
		mOpenCvCameraView.setCvCameraViewListener(this);
	
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        mItemPreviewRGBA  = menu.add("Preview RGBA");
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
    
	/**
	 * *********************************************
	 * OpenCV -
	 * *********************************************
	 */

	 @Override
	 public void onPause()
	 {
		 Log.i(TAG, "MainActivity :: app paused");
		 super.onPause();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	 }

	 public void onDestroy() {
		 Log.i(TAG, "MainActivity :: "+Environment.DIRECTORY_DOWNLOADS.toString());
		 String state = Environment.getExternalStorageState();
		    if (Environment.MEDIA_MOUNTED.equals(state)) {
		    	Log.i(TAG, "MainActivity :: Mdia mounted");
				 File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				 if(!path.mkdirs()){
					 Log.i(TAG, "MainActivity :: Error mkdirs");
				 }
				 //				 File path = this.getExternalFilesDir(null);
				 String fileName = "logcatParticipant6.txt";
				 File file = new File(path, fileName);
			     try {
			    	 OutputStream os = new FileOutputStream(file);
			    	 Log.i(TAG, "MainActivity :: writting");
			    	 os.write(("testng").getBytes());
			    	 os.close();
			    	 Log.i(TAG, "MainActivity :: closing");
			    	 Runtime.getRuntime().exec("logcat -v threadtime -d -f " + file.getPath() +" *:S itu.dluj.thesisgesturetraining");
			    	 Log.i(TAG, "MainActivity :: logcat done :: "+ path.toString());
			    	 Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
			    	    mediaScanIntent.setData(Uri.fromFile(file));
			    	    this.sendBroadcast(mediaScanIntent);
				} catch (IOException e) {
					 Log.i(TAG, "MainActivity :: "+e.toString());
				}
		    }else{
		    	Log.i(TAG, "MainActivity :: Media NOT mounted");
		    }

		 
		 Log.i("crash", "app crashed");
	     super.onDestroy();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	 }
	 
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				Log.i(TAG, "MainActivity :: OpenCV loaded successfully");
				mOpenCvCameraView.enableView();

			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};

	@Override
	public void onResume()
	{
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
	}

	@Override
	public void onCameraViewStarted(int width, int height) {				
//		mOpenCvCameraView.setFpsRange(30000, 30000);
		Log.i(TAG, "MainActivity :: size:: w:"+ width+" h:"+height);
		if(width == 0){
			width = 720;
		}
		if(height == 0){
			height = 480;
		}
		statesHandler = new GestureTrainingHandler(width, height, getApplicationContext());
		screenHeight = height;
		screenWidth = width;
//		Log.i("MainActivity", "FPSRange::"+ mOpenCvCameraView.getFpsRange());
	}

	@Override
	public void onCameraViewStopped() {
		 Log.i(TAG, "MainActivity :: camera view stopped");
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Mat output = new Mat();
//		Log.i("DEVICE", sDeviceModel);
		if(cameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK){  
			Core.flip(inputFrame.rgba(), output, 1);
		}else{
			output = inputFrame.rgba();
		}
		
//		Mat gray = new Mat();
//		Imgproc.cvtColor(output, gray, Imgproc.COLOR_RGB2GRAY);
//		Imgproc.equalizeHist(gray, gray);
//		Imgproc.cvtColor(gray, output, Imgproc.COLOR_GRAY2RGBA);
//		
		
		Mat outputScaled = new Mat();
//		Log.i("MainActivity", "dims output before pyrdown::"+ output.cols());
		Imgproc.pyrDown(output, outputScaled);
//		Log.i("MainActivity", "dims scaled after pyrdown::"+ outputScaled.cols());
		outputScaled = statesHandler.handleFrame(outputScaled);
		Imgproc.pyrUp(outputScaled, output);
//		Log.i("MainActivity", "dims output after pyrup::"+ output.cols());
		Point handCentroid = statesHandler.getHandCentroid();
		if(handCentroid != null){
//			Log.i("StatesHandler", "centroidNormal::"+temp.toString());
			mOpenCvCameraView.resetFMAreas(handCentroid, screenWidth, screenHeight);
		}
        return output;
	}

}
