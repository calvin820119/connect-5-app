package com.example.finalprj;

// import java network file
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	final int ackTimeout = 2000;
	private PowerManager mPowerManager;
	private WakeLock mWakeLock;
	private boolean IsServerAlive = true;
	Button btn1,btn2,exitbtn;//,btn3;
	EditText txt;
	TextView txtv;
	RadioGroup gameBoard;
	ListView lv,lv2;
	boolean IsLogin = false;
	String MainUser = null;
	InetAddress serverAddr; 
	Socket socket = null;
	byte[] buf = new byte[1024];
	AliveThread alivethread = null; 
	ArrayAdapter<String> adapter,madapter;
	String ServerIP = "";
	InetAddress  dstIP;
	ProgressDialog MessageDialog;
	Switch alivecounttxt;
	ArrayList<String> mgadapter;
	HorizontalScrollView hs1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		alivecounttxt = (Switch)findViewById(R.id.switch1);
		
		btn1 = (Button) findViewById(R.id.ConnectBtn);
		btn2 = (Button) findViewById(R.id.pushbtn);
	//	btn3 = (Button) findViewById(R.id.button2);
		txt = (EditText) findViewById(R.id.MsgTxt);
		txtv = (TextView) findViewById(R.id.msg);
		lv = (ListView) findViewById(R.id.listView1);
		lv2 = (ListView) findViewById(R.id.listView2);
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		madapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		hs1 = (HorizontalScrollView)findViewById(R.id.horizontalScrollView1);
		mgadapter = new ArrayList<String>();
		IsLogin = false;
		SetLoginEnable(IsLogin);
		
		MessageDialog = new ProgressDialog(MainActivity.this);
		MessageDialog.setCancelable(false);
		
		mPowerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(mPowerManager.SCREEN_BRIGHT_WAKE_LOCK, "BackLight");
		mWakeLock.acquire();
		SharedPreferences settings = getSharedPreferences("_PreSetting",0); 
		((EditText)findViewById(R.id.txtIP)).setText(settings.getString("serverip", ""));
		txt.setText(settings.getString("username", ""));
		
		btn1.setOnClickListener(new View.OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				String ip = ((EditText)findViewById(R.id.txtIP)).getText().toString();
			
				if(txt.getText().toString().length() > 0 && ip.length() > 0){
					if(txt.getText().toString().indexOf(":") == -1 && txt.getText().toString().indexOf(";") == -1 ){
						try {
							ServerIP = ((EditText)findViewById(R.id.txtIP)).getText().toString();
							dstIP = InetAddress.getByName( ServerIP );
							MainUser =  txt.getText().toString().trim();
							SharedPreferences settings = getSharedPreferences("_PreSetting",0);
							settings.edit().putString("serverip", ServerIP).commit();
							settings.edit().putString("username", MainUser).commit();
						
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						new SocketSend().execute(MainUser,"login");
					}else{
						new AlertDialog.Builder(MainActivity.this).setTitle("格式錯誤").setMessage("輸入格式有錯誤，玩家名稱不能包含冒號分號！").setCancelable(true).show();
					}
				}else{
					new AlertDialog.Builder(MainActivity.this).setTitle("格式錯誤").setMessage("輸入格式有錯誤，請確認IP與玩家名稱！").setCancelable(true).show();
		        }
			}
		});
		
		 btn2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				IsLogin = false;
				SetLoginEnable(IsLogin);
				// TODO Auto-generated method stub
				new SocketSend().execute(MainUser,"logout");
				
			}
		});
		
		 
		 lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
 			public void onItemClick(AdapterView arg0,View arg1,int arg2,long arg3){
 				ListView listview = (ListView) arg0;
 				final String Suser = listview.getItemAtPosition(arg2).toString();
 				//listview.setClickable(true);
 				new AlertDialog.Builder(MainActivity.this).setTitle("挑戰玩家").setMessage("玩家: "+Suser).setPositiveButton("挑戰!", new DialogInterface.OnClickListener() {
						
						@SuppressWarnings("deprecation")
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//new ChallengeSend().execute(user,Suser);
							MessageDialog = new ProgressDialog(MainActivity.this);
							MessageDialog.setMessage("等待回復中....");
							MessageDialog.setButton("cancel", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									//cancel challenage
									new SocketSend().execute(MainUser,"cancelch",Suser);
								}
							});
							
							MessageDialog.setCancelable(false);
							MessageDialog.show();
							
							
					    
							
							
							new SocketSend().execute(MainUser,"challenage",Suser);
							
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					}).setCancelable(false).show();
 			}
			});
		 
		 lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 
	 			public void onItemClick(AdapterView arg0,View arg1,int arg2,long arg3){
	 				ListView listview = (ListView) arg0;
	 				final String gameID = mgadapter.get(arg2);
	 				final String Suser = listview.getItemAtPosition(arg2).toString();
	 				//listview.setClickable(true);
	 				new AlertDialog.Builder(MainActivity.this).setTitle("觀看對戰").setMessage("玩家A: "+Suser.split("vs")[0]+"\n玩家B: "+Suser.split("vs")[1]).setPositiveButton("確認", new DialogInterface.OnClickListener() {
							
							@SuppressWarnings("deprecation")
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								alivethread.flag = false;
			    				alivethread.interrupt();
			    				alivethread = null;
			        			Intent i = new Intent();
			    				Bundle b = new Bundle();
			    				b.putString("_USER", Suser.split("vs")[0]);
			    				b.putString("_FIGHT_NAME", Suser.split("vs")[1]);
			    				b.putString("_GAMD_ID", gameID);
			    				b.putString("Act_TYPE", "watch");
			    				b.putString("_SERVER_IP", ServerIP);
			    				i.putExtras(b);
			    				i.setClass(MainActivity.this, GameActivity.class);
			    				
			    				MessageDialog.dismiss();
			    				startActivity(i);
			    				MainActivity.this.finish();
								
								
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						}).setCancelable(false).show();
	 			}
				});
		 
		 
		 exitbtn = (Button)findViewById(R.id.exitbtn);
		 exitbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(alivethread != null)
				if(alivethread.flag == true){
					alivethread.flag = false;
					alivethread.interrupt();
					alivethread = null;
				}
				MainActivity.this.finish();
			}
		});
		 
		 alivecounttxt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					hs1.scrollTo(((ListView)findViewById(R.id.listView1)).getWidth(), 0);//((ListView)findViewById(R.id.listView1)).getWidth()
				}else{
					hs1.scrollTo(0, 0);
				}
			}
		});
		 
	}
	
	private void SetLoginEnable(boolean set){
		findViewById(R.id.ConnectBtn).setEnabled(!set);
		findViewById(R.id.pushbtn).setEnabled(set);
		findViewById(R.id.MsgTxt).setEnabled(!set);
		if(!set){
		//	((TextView)findViewById(R.id.alivecount)).setText("");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
			adapter.clear();
			((ListView) findViewById(R.id.listView1)).setAdapter(adapter);
		}
		((EditText)findViewById(R.id.txtIP)).setEnabled(!set);
	}
	
	private AlertDialog ab;
	
	
	private class SocketSend extends AsyncTask<String, Void, Void> {
		byte[] sentbuf= new byte[1024];
		byte[] revbuf= new byte[1024];
		
		SocketAddress dstEP; 
		
		Socket socket = null;
		
		final int dstPort = 30001;
		String type;
		String user;
		
		@Override
        protected void onPreExecute() {
			super.onPreExecute();
			
			
        //	try{
        	//	dstIP = InetAddress.getByName("192.168.0.3");
        		dstEP = new InetSocketAddress(dstIP,dstPort);
        //	}
        //	catch(UnknownHostException e){
        //		txtv.setText(e.toString());
        //	}
		}
		
        @Override
        protected Void doInBackground(String... params) {
        	type = params[1];
        	user = params[0];
        	//txtv1.setText(type);
        	IsServerAlive = true;
	        try{
				socket = new Socket();
				
				socket.connect(dstEP,2000);
				
				OutputStream out = null;
				InputStream in = null;
						
				out = socket.getOutputStream();
				in = socket.getInputStream();
				
				String TransStr = "";
				
				if(type == "login"){
					TransStr = "s:"+user;
	        	}else if(type == "logout"){
	        		TransStr = "o:"+user;
	        	}else if(type == "ack"){
	        		TransStr = "ack:"+user;
	        	}else if(type == "challenage"){
	        		TransStr = "ch:"+user+":"+params[2];
	        	}else if(type == "fight"){
	        		TransStr = "f:"+user+":"+params[2];
	        	}else if(type == "ignore"){
	        		TransStr = "i:"+params[2]+":"+user;
	        	}else if(type == "cancelch"){
	        		TransStr = "cch:"+params[2]+":"+user;
	        	}
				sentbuf = new byte[TransStr.length() + 10];
				//System.arraycopy(TransStr.getBytes(), 0, sentbuf, 0, TransStr.length());
				sentbuf = TransStr.getBytes("UTF-8");
				
				
				out.write(sentbuf);
				revbuf = new byte[1024];
				in.read(revbuf);
				socket.close();
	        }catch (UnknownHostException e) {
	        	IsServerAlive = false;
	        //	e.printStackTrace();
	        	return null;
	        }catch(SocketException e){
	        	IsServerAlive = false;
	        //	e.printStackTrace();
	        	return null;
	        }catch(IOException e){
	        	IsServerAlive = false;
	        //	e.printStackTrace();
	        	return null;
	        }
	        return null;
        }        
        ProgressDialog pd;
        @Override
        protected void onPostExecute(Void unused) {
        	try{
	        	if(IsServerAlive){
	        		
		        	if(type == "login"){
		        		String[] cmd = null;
						
						cmd = new String(revbuf,"UTF-8").split(":");
						
		        		if(cmd[0] == "e" && cmd[1] == "r" ){
			        		txtv.setText("Name Reserved !");
		        		}else{
		        			//txtv.setText(new String(revbuf));
			        		if(alivethread == null){
			        			alivethread = new AliveThread();
			        			alivethread.start();
			        		}
			        		
			        		IsLogin = true;
							SetLoginEnable(IsLogin);
		        		}
		        		//***
		        	//	MainUser = txt.getText().toString();
						
		        		
		        	}else if(type == "logout"){
		        		String[] cmd = new String(revbuf,"UTF-8").split(":");
		        		if(cmd[0] == "e" && cmd[1] == "nf"){
		        			//txtv.setText("Not found");
		        		}else{
		        			if(alivethread != null){
		        				alivethread.flag = false;
		        				alivethread.interrupt();
		        				alivethread = null;
		        			}
		        		}
		        	}else if(type == "ack"){
		        		final String[] cmd = new String(revbuf,"UTF-8").trim().split(":");
		        	//	txtv.setText(new String(cmd[1]));
		        		
		        		if(cmd[1].equals("emp")){
		        		//	adapter.clear();
		        		//	((TextView)findViewById(R.id.alivecount)).setText("");
			        	//	lv.setAdapter(adapter);
			        		
			        	//	IsLogin = false;
						//	SetLoginEnable(IsLogin);
		        			if(alivethread != null){
		        				alivethread.flag = false;
		        				alivethread.interrupt();
		        				alivethread = null;
		        			}
		        			
			        		Toast.makeText(MainActivity.this, "請重新登入", Toast.LENGTH_LONG);
			        		
			        		Intent i = new Intent();
			        		i.setClass(MainActivity.this, MainActivity.class);
			        		startActivity(i);
			        		
			        		
		        		}else{
		        		
		        			
			        		
			        		if(cmd[1].equals(new String("y"))){
			        		//	Toast.makeText(MainActivity.this, cmd[2], Toast.LENGTH_SHORT).show();
			        			ab = new AlertDialog.Builder(MainActivity.this).setTitle("有玩家挑戰你").setMessage("玩家: "+cmd[2]).setPositiveButton("接受!", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										//new ChallengeSend().execute(user,Suser);
										MessageDialog = new ProgressDialog(MainActivity.this);
										MessageDialog = new ProgressDialog(MainActivity.this);
										MessageDialog.setCancelable(false);
												
										MessageDialog.setMessage("傳送中....");
										MessageDialog.show();
								//		pd = new ProgressDialog(MainActivity.this);
								//		pd.setCancelable(false);
										
								//		pd.setMessage("傳送中....");
								//		pd.show();
										new SocketSend().execute(MainUser,"fight",cmd[2]);
										
									}
								}).setNegativeButton("拒絕", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										new SocketSend().execute(MainUser,"ignore",cmd[2]);
									}
								}).setCancelable(false).show();
			        		}else if(cmd[1].equals(new String("c"))){
			        			ab.dismiss();
			        			Toast.makeText(MainActivity.this, "玩家已取消挑戰!", Toast.LENGTH_SHORT).show();
			        		}
			        		
			        		
			        		if(cmd[3].equals(new String("y"))){
			        			alivethread.flag = false;
			    				alivethread.interrupt();
			    				alivethread = null;
			        		//	pd.dismiss();
			    				
			        			//fight
			        			Intent i = new Intent();
			    				Bundle b = new Bundle();
			    				b.putString("_USER", MainUser);
			    				b.putString("_FIGHT_NAME", cmd[4]);
			    				b.putString("_GAMD_ID", cmd[5]);
			    				b.putString("_SERVER_IP", ServerIP);
			    				b.putString("Act_TYPE", "fight");
			    				i.putExtras(b);
			    				i.setClass(MainActivity.this, GameActivity.class);
			    				
			    				MessageDialog.dismiss();
			    				startActivity(i);
			    				MainActivity.this.finish();
			        		}else if(cmd[3].equals("i")){
			        			MessageDialog.dismiss();
			        			Toast.makeText(MainActivity.this,"玩家拒絕挑戰", Toast.LENGTH_SHORT).show();
			        		}
			        		
			        		if(cmd.length >= 6){
				        		adapter.clear();
				        		int alivecount = 0;
				        		int ii=0;
				        		for(ii=0;ii<Integer.parseInt(cmd[6]);ii++){
				        			alivecount++;
				        			if(!cmd[ii+7].equals(MainUser)){
				        				adapter.add(cmd[ii+7]);
				        			}
				        		}
				        		
				        	//	alivecounttxt.setText("會員上線人數:"+alivecount);
				        	//	alivecounttxt.setTextOff("玩家名單["+alivecount+"]");
				        		
				        		if(!adapter.isEmpty())
				        		{	lv.setAdapter(adapter);}
				        		
				        		madapter.clear();
				        		mgadapter.clear();
				        //		Toast.makeText(MainActivity.this, "b", Toast.LENGTH_SHORT).show();
				        		for(int i=0;i<Integer.parseInt(cmd[7+ii]);i++){
				        			madapter.add(cmd[8+ii+i].split(";")[0]);
				        			mgadapter.add(cmd[8+ii+i].split(";")[1]);
				        			
				        		}
				        		
				        		
				        		if(!madapter.isEmpty())
				        		{	lv2.setAdapter(madapter);	}
				        		
				        		
				        		
			        		}
		        		}
		        		
		        	}else if(type == "challenage"){
		        		final String[] cmd = new String(revbuf,"UTF-8").trim().split(":");
		        		if(cmd[0].equals("n")){
		        			MessageDialog.dismiss();
		        			Toast.makeText(MainActivity.this, "該玩家已被挑戰中...", Toast.LENGTH_SHORT).show();
		        		}else if(cmd[0].equals("s")){
		        			//
		        		}
		        	}else if(type == "fight"){
		        		
		        	}
		        	
		        }else{
		        	//Toast.makeText(MainActivity.this, "cannot connect", Toast.LENGTH_LONG).show();	
		        	if(type == "login")
		        		new AlertDialog.Builder(MainActivity.this).setTitle("連線錯誤").setMessage("伺服器連線失敗，請確認IP位置！").setCancelable(true).show();
		        
		        
		        }
	        }catch(Exception e){	e.printStackTrace();	}
        }
    }
	
	
	
	private class AliveThread extends Thread{
		
		boolean flag = true;
		
		@Override
		public void run(){
			while(flag){
				new SocketSend().execute(MainUser,"ack");
					
				try{
					Thread.sleep(ackTimeout);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

