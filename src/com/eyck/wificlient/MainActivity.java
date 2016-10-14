package com.eyck.wificlient;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity implements OnClickListener{

	ImageButton con;
	ImageButton exit;
	static ImageView image;
	public static boolean flag;
	public static int t;
	public static String absPicPath;
	public static String picPath;
	public static String picPath1;
	public static String picPath2;
	public static String ip;
	public static int port;
	ImageButton left;
	ImageButton right;
	ImageButton up;
	ImageButton down;
	ImageButton stop;
	ImageButton sendFlag;
	private static boolean leftChange=false;
	private static boolean rightChange=false;
	private static boolean upChange=false;
	private static boolean downChange=false;
	private static boolean stopChange=false;
	private static boolean sendFlagChange=false;
	public static int count;
	public static int color=0;
    ThreadListener myThread=null;
  //获取传感器管理对象
  	private SensorManager sensorManager;
  	private TextView showX;
  	private TextView showY;
  	private TextView showZ;
  	private TextView showState;
  	private static int vState=-1;
  	private static int vStates=-1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        con=(ImageButton)findViewById(R.id.con);
        exit=(ImageButton)findViewById(R.id.exit);
        image=(ImageView) findViewById(R.id.image);
        left=(ImageButton) findViewById(R.id.ic_left);
        right=(ImageButton) findViewById(R.id.ic_right);
        up=(ImageButton) findViewById(R.id.ic_up);
        down=(ImageButton) findViewById(R.id.ic_down);
        stop=(ImageButton) findViewById(R.id.ic_stop);
        sendFlag=(ImageButton) findViewById(R.id.ic_flag);
        showState=(TextView) findViewById(R.id.show_state);
        File sdCardDir=Environment.getExternalStorageDirectory();
        try {
			absPicPath=sdCardDir.getCanonicalPath()+"/";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        /**************电脑************/
//        ip="192.168.191.1";//电脑wifi
//        port=30000;
        /*************wifi模块*******/
        ip="192.168.100.1";//wifi模块
        port=50000;
        try {
			picPath1=Environment.getExternalStorageDirectory().getCanonicalPath()+"/1.jpg";
			picPath2=Environment.getExternalStorageDirectory().getCanonicalPath()+"/2.jpg";
			//Log.d("TagTest1", picPath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        final Handler myHandler=new Handler(){
        	public void handleMessage(Message msg){
        		if(msg.what==0x123){
        			if(((t++)%2)==1){
        				showPic(picPath1);
        			}else{
        				showPic(picPath2);
        			}
        		}
        	}
        };
        con.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				myThread=new ThreadListener(ip,port,myHandler);
				myThread.start();
				//new ThreadBtn(ip, port).start();
				flag=true;
				
			}
		});
        exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        left.setOnClickListener(this);
		right.setOnClickListener(this);
		up.setOnClickListener(this);
		down.setOnClickListener(this);
		stop.setOnClickListener(this);
		sendFlag.setOnClickListener(this);
		
		new Timer().schedule(new TimerTask(){

			@Override
			public void run() {
				setVStates();
			}
        	
        },0,100);
        
		//传感器
		sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //获取加速度传感器对象
        Sensor accelerometerensor=sensorManager.
        		getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //获取地磁传感器对象
        Sensor magneticSensor=sensorManager.
        		getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        //注册加速度传感器监听
        sensorManager.registerListener(listener, accelerometerensor,
        		SensorManager.SENSOR_DELAY_NORMAL);//提升精确度
        //注册地磁传感器监听
        sensorManager.registerListener(listener, magneticSensor, 
        		SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    protected void onDestroy(){
    	super.onDestroy();
    	if(sensorManager!=null){
    		sensorManager.unregisterListener(listener);
    	}
    }
    private SensorEventListener listener=new SensorEventListener() {
    	float[] accelerometerValues=new float[3];
		float[] magneticValues=new float[3];
		@Override
		public void onSensorChanged(SensorEvent event) {
		
			//判断是加速度传感器还是地磁传感器
			if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
				//赋值时要调用clone方法
				accelerometerValues=event.values.clone();
			}else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
				magneticValues=event.values.clone();
			}
			float[] R=new float[9];
			float[] values=new float[3];
			SensorManager.
				getRotationMatrix(R, null, accelerometerValues, magneticValues);
			SensorManager.getOrientation(R, values);
			
			
			int xValue=(int)(Math.toDegrees(values[0]));//转换为角度
			int yValue=(int)(Math.toDegrees(values[1]));
			int zValue=(int)(Math.toDegrees(values[2]));
			if(yValue>45){
				vStates=90;
			}else if(yValue<(-45)){
				vStates=0;
			}else{
				vStates=yValue+45;
			}
			//Log.d("TagTest", "vState:"+vState);
		}
		
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
	};
    
    /*********************************/
    
    private static void showPic(String mess){
    	try{
    		BitmapFactory.Options options = new BitmapFactory.Options();
    		options.inSampleSize = 1;
    		Bitmap bm = BitmapFactory.decodeFile(mess, options);
    		image.setImageBitmap(bm);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    public void onClick(View v) {
		switch(v.getId()){
		case R.id.ic_left:
			leftChange=true;
			//new ThreadBtn(ip,port).start();
			break;
		case R.id.ic_right:
			rightChange=true;
			//new ThreadBtn(ip,port).start();
			break;
		case R.id.ic_up:
			upChange=true;
			//new ThreadBtn(ip,port).start();
			break;
		case R.id.ic_down:
			downChange=true;
			break;
			//new ThreadBtn(ip,port).start();
		case R.id.ic_stop:
			stopChange=true;
			break;
		case R.id.ic_flag:
			sendFlagChange=true;
			if((color++)%2==1){
				showState.setText("");
			}else{
				showState.setText("自动");
			}
			Log.d("TagTest", "zidong");
			break;
		default:
			break;
		}
	}
    public static boolean getLeftState(){
    	return leftChange;
    }
    public static boolean getRightState(){
    	return rightChange;
    }
    public static boolean getUpState(){
    	return upChange;
    }
    public static boolean getDownState(){
    	return downChange;
    }
    public static boolean getStopState(){
    	return stopChange;
    }
    public static boolean getSendFlagState(){
    	return sendFlagChange;
    }
    public static void setState(){
    	leftChange=false;
    	rightChange=false;
    	upChange=false;
    	downChange=false;
    	stopChange=false;
    	sendFlagChange=false;
    }
    public static int getVState(){
    	return vState;
    }
    public static void setVState(){
    	vState=-1;
    }
    public static void setVStates(){
    	vState=vStates;
    }
}
