package com.eyck.wificlient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ThreadListener extends Thread {
	private String ip=null;
	Handler myHandler=null;
	public static String mes=null;
	public static int counts=0;
	public Message msg=new Message();
	public static String pathLoad=null;
	private static int port=0;
	public ThreadListener(String ip,int port){
		this.ip=ip;
		this.port=port;
	}
	public ThreadListener(String ip,int port,Handler myHandler){
		this.ip=ip;
		this.port=port;
		this.myHandler=myHandler;
	}
	public void run(){
		try{
			Message msg=new Message();
			Socket socket=new Socket(ip,port);
			//Log.d("TagTest", ip);
			//BufferedReader br=new BufferedReader(
			//		new InputStreamReader(socket.getInputStream()));
			InputStream is=socket.getInputStream();
			DataInputStream dis=new DataInputStream(is);
			PrintStream	ps=new PrintStream(socket.getOutputStream());
			byte[] buff=new byte[20480];
			byte[] buffer=new byte[20480];
			int i=0;
			int temp=0;
			int temps=0;
			boolean startFlag=false;
			int hasread=0;
			int hasRead=0;
			Log.d("TagTest", "beforewhile");
			while((hasRead=dis.read(buff))>0){
				//Log.d("TagTest", "beforewhile");
				outer:
				for(i=0;i<hasRead;i++){
					if(buff[i]==(-1)&&buff[i+1]==(-40))
					{
						startFlag=true;
					}else
					if((buff[i]==(-1)&&buff[i+1]==(-39))||temp>=20000)
					{
						//Log.d("TagTest", "temp="+temp);
						if((buff[i]==(-1)&&buff[i+1]==(-39))&&temp>=1024){
							//Log.d("TagTest", "inwhile");
							buffer[temp++]=buff[i];
							buffer[temp++]=buff[i+1];
							temps=temp;
							writes(buffer,temps);
						}
						startFlag=false;
						temp=0;
						break outer;
					}
					if(startFlag)
					{
						buffer[temp++]=buff[i];
					}
				}
				control(ps);
				//vControl(ps);
			}
			
			//Log.d("TagTest", "mess33");
			dis.close();
			//br.close();
			socket.close();
		}catch(IOException ie){
			ie.printStackTrace();
		}
	}
	private void writes(byte[] buff,int hasRead){
		try{
			//如果手机插入了SD卡，而且应用程序具有访问SD的权限
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				File sdCardDir=Environment.getExternalStorageDirectory();
				if(((counts++)%2)==1){
					pathLoad=sdCardDir.getCanonicalPath()+"/1.jpg";
				}
				else{
					pathLoad=sdCardDir.getCanonicalPath()+"/2.jpg";
				}
				//Log.d("TagTest",pathLoad+"b"+counts);
				File targetFile=new File(pathLoad);
				//以指定文件创建RandomAccessFile对象
				FileOutputStream fos=new FileOutputStream(targetFile);
				fos.write(buff,0,hasRead);
				fos.close();
				myHandler.sendEmptyMessage(0x123);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void control(PrintStream ps){
		if(MainActivity.getLeftState()==true){
			MainActivity.setState();
			ps.print('l');
			Log.d("TagTest", "l");
		}
		if(MainActivity.getRightState()==true){
			MainActivity.setState();
			ps.print('r');
			Log.d("TagTest", "r");
		}
		if(MainActivity.getUpState()==true){
			MainActivity.setState();
			ps.println('u');
			Log.d("TagTest", "u");
		}
		if(MainActivity.getDownState()==true){
			MainActivity.setState();
			ps.println('d');
			Log.d("TagTest", "d");
		}
		if(MainActivity.getStopState()==true){
			MainActivity.setState();
			ps.println('s');
			Log.d("TagTest", "s");
		}
	}
	private void vControl(final PrintStream ps){
		int valueC=MainActivity.getVState();
		if(valueC>=0){
			MainActivity.setVState();
			ps.print(valueC);
			Log.d("TagTest", "v:"+valueC);
		}
		
	}
}
