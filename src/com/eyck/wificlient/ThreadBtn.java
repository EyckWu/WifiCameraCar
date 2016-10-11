package com.eyck.wificlient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import android.util.Log;


public class ThreadBtn extends Thread {

	String ip;
	int port;
	private static boolean leftChange=false;
	private static boolean rightChange=false;
	private static boolean upChange=false;
	private static boolean downChange=false;
	
	public ThreadBtn(String ip,int port){
		this.ip=ip;
		this.port=port;
//		this.leftChange=MainActivity.getLeftState();
//		this.rightChange=MainActivity.getRightState();
//		this.upChange=MainActivity.getUpState();
//		this.downChange=MainActivity.getDownState();
	}
	public void run() {
		try {
			Log.d("TagTest", "BTN");
			Socket socket=new Socket(ip,port);
			PrintStream	ps=new PrintStream(socket.getOutputStream());
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
			ps.close();
			socket.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
}
