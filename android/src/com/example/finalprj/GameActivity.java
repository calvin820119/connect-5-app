package com.example.finalprj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
	final int ackTimeout = 2000;
	String ServerIP = "";
	String GAMEID = null;
	String TYPE = "";
	private String user;
	private String fight_name;
	Button btn,exitbtn;
	AliveThread at = null;
	int LastX=-1;
	int LastY=-1;
	int pushCount = 0;
	boolean MyTurn = false;
	final int boardsize = 13;
	//boolean[][] boardmap = new boolean[9][9];
	boolean[][] boardmap = new boolean[boardsize][boardsize];
	//graph item
	Button pushbtn;
	RadioButton[][] board = new RadioButton[boardsize][boardsize];
	RadioGroup BoardColumn0;
	RadioGroup BoardColumn1;
	RadioGroup BoardColumn2;
	RadioGroup BoardColumn3;
	RadioGroup BoardColumn4;
	RadioGroup BoardColumn5;
	RadioGroup BoardColumn6;
	RadioGroup BoardColumn7;
	RadioGroup BoardColumn8;
	RadioGroup BoardColumn9;
	RadioGroup BoardColumnA;
	RadioGroup BoardColumnB;
	RadioGroup BoardColumnC;
	//
	EditText msgenter;
	TextView msglist;
	int step=0;
	
	private void SetEnable(Boolean set){
		for(int i=0;i<boardsize;i++){
			for(int j=0;j<boardsize;j++){
				if(!boardmap[i][j])	
					board[i][j].setEnabled(set);
			}
		}
		
		
		
	//	if(TYPE.equals("fight")){
			if(set){
				((TextView)findViewById(R.id.play1)).setText("->");
				((TextView)findViewById(R.id.play2)).setText("");
			}else{
				((TextView)findViewById(R.id.play2)).setText("->");
				((TextView)findViewById(R.id.play1)).setText("");
			}
	//	}else{
	//		if((step % 2) == 0){
	//			((TextView)findViewById(R.id.play1)).setText("->");
	//			((TextView)findViewById(R.id.play2)).setText("");
	//		}else{
	//			((TextView)findViewById(R.id.play2)).setText("->");
	//			((TextView)findViewById(R.id.play1)).setText("");
	//		}
	//	}
		if(TYPE.equals("watch")){
			pushbtn.setEnabled(false);
			pushbtn.setText("第   步");
		}else{
			pushbtn.setEnabled(!set);
		}
		
		if(set == false){
			if(LastX >= 0 && LastY >= 0){
				final Drawable whiteIcon = getResources().getDrawable( R.drawable.white );
				board[LastX][LastY].setBackgroundDrawable(whiteIcon);
			}
			
			pushCount = 0;
		}
		pushbtn.setText("催促對手["+(3-pushCount)+"]");
		
		
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		final Drawable blackIcon = getResources().getDrawable( R.drawable.black );
		
		
	//	btn = (Button) findViewById(R.id.stop);
		
		exitbtn = (Button) findViewById(R.id.exitbtn);
		
		Bundle b = getIntent().getExtras();
		user = b.getString("_USER");
		fight_name = b.getString("_FIGHT_NAME");
		GAMEID = b.getString("_GAMD_ID");
		TYPE = b.getString("Act_TYPE");
		ServerIP = b.getString("_SERVER_IP");
		TextView txtv2 =  (TextView) findViewById(R.id.textView2);
		pushbtn = (Button)findViewById(R.id.pushbtn);
		TextView txtv3 = (TextView) findViewById(R.id.textView3);
		msglist = (TextView)findViewById(R.id.messagelist);
		msgenter = (EditText)findViewById(R.id.msginput);
		if(TYPE.equals("fight")){
			txtv2.setText("你: "+user);
			txtv3.setText("對手: "+fight_name);
			pushbtn.setText("催促對手["+(3-pushCount)+"]");
			msglist.setMovementMethod(new ScrollingMovementMethod());
		}else if(TYPE.equals("watch")){
			txtv2.setText("黑棋: "+user);
			txtv3.setText("白棋: "+fight_name);
			pushbtn.setEnabled(false);
			msglist.setEnabled(false);
			msgenter.setEnabled(false);
		}
	//	if(TYPE.equals("fight")){
			at = new AliveThread();
			at.start();
	//	}else if(TYPE.equals("watch")){
			
	//	}
		
			
		BoardColumn0 = (RadioGroup) findViewById(R.id.rg0);
		BoardColumn1 = (RadioGroup) findViewById(R.id.rg1);
		BoardColumn2 = (RadioGroup) findViewById(R.id.rg2);
		BoardColumn3 = (RadioGroup) findViewById(R.id.rg3);
		BoardColumn4 = (RadioGroup) findViewById(R.id.rg4);
		BoardColumn5 = (RadioGroup) findViewById(R.id.rg5);
		BoardColumn6 = (RadioGroup) findViewById(R.id.rg6);
		BoardColumn7 = (RadioGroup) findViewById(R.id.rg7);
		BoardColumn8 = (RadioGroup) findViewById(R.id.rg8);
		BoardColumn9 = (RadioGroup) findViewById(R.id.rg9);
		BoardColumnA = (RadioGroup) findViewById(R.id.rgA);
		BoardColumnB = (RadioGroup) findViewById(R.id.rgB);
		BoardColumnC = (RadioGroup) findViewById(R.id.rgC);
		
		board[0][0] = (RadioButton)findViewById(R.id.radio00);
		board[0][1] = (RadioButton)findViewById(R.id.radio01);
		board[0][2] = (RadioButton)findViewById(R.id.radio02);
		board[0][3] = (RadioButton)findViewById(R.id.radio03);
		board[0][4] = (RadioButton)findViewById(R.id.radio04);
		board[0][5] = (RadioButton)findViewById(R.id.radio05);
		board[0][6] = (RadioButton)findViewById(R.id.radio06);
		board[0][7] = (RadioButton)findViewById(R.id.radio07);
		board[0][8] = (RadioButton)findViewById(R.id.radio08);
		board[0][9] = (RadioButton)findViewById(R.id.radio09);
		board[0][10] = (RadioButton)findViewById(R.id.radio0A);
		board[0][11] = (RadioButton)findViewById(R.id.radio0B);
		board[0][12] = (RadioButton)findViewById(R.id.radio0C);
		
		board[1][0] = (RadioButton)findViewById(R.id.RadioButton10);
		board[1][1] = (RadioButton)findViewById(R.id.RadioButton11);
		board[1][2] = (RadioButton)findViewById(R.id.RadioButton12);
		board[1][3] = (RadioButton)findViewById(R.id.RadioButton13);
		board[1][4] = (RadioButton)findViewById(R.id.RadioButton14);
		board[1][5] = (RadioButton)findViewById(R.id.RadioButton15);
		board[1][6] = (RadioButton)findViewById(R.id.RadioButton16);
		board[1][7] = (RadioButton)findViewById(R.id.RadioButton17);
		board[1][8] = (RadioButton)findViewById(R.id.RadioButton18);
		board[1][9] = (RadioButton)findViewById(R.id.RadioButton19);
		board[1][10] = (RadioButton)findViewById(R.id.RadioButton1A);
		board[1][11] = (RadioButton)findViewById(R.id.RadioButton1B);
		board[1][12] = (RadioButton)findViewById(R.id.RadioButton1C);
		
		board[2][0] = (RadioButton)findViewById(R.id.RadioButton20);
		board[2][1] = (RadioButton)findViewById(R.id.RadioButton21);
		board[2][2] = (RadioButton)findViewById(R.id.RadioButton22);
		board[2][3] = (RadioButton)findViewById(R.id.RadioButton23);
		board[2][4] = (RadioButton)findViewById(R.id.RadioButton24);
		board[2][5] = (RadioButton)findViewById(R.id.RadioButton25);
		board[2][6] = (RadioButton)findViewById(R.id.RadioButton26);
		board[2][7] = (RadioButton)findViewById(R.id.RadioButton27);
		board[2][8] = (RadioButton)findViewById(R.id.RadioButton28);
		board[2][9] = (RadioButton)findViewById(R.id.RadioButton29);
		board[2][10] = (RadioButton)findViewById(R.id.RadioButton2A);
		board[2][11] = (RadioButton)findViewById(R.id.RadioButton2B);
		board[2][12] = (RadioButton)findViewById(R.id.RadioButton2C);
		
		board[3][0] = (RadioButton)findViewById(R.id.RadioButton30);
		board[3][1] = (RadioButton)findViewById(R.id.RadioButton31);
		board[3][2] = (RadioButton)findViewById(R.id.RadioButton32);
		board[3][3] = (RadioButton)findViewById(R.id.RadioButton33);
		board[3][4] = (RadioButton)findViewById(R.id.RadioButton34);
		board[3][5] = (RadioButton)findViewById(R.id.RadioButton35);
		board[3][6] = (RadioButton)findViewById(R.id.RadioButton36);
		board[3][7] = (RadioButton)findViewById(R.id.RadioButton37);
		board[3][8] = (RadioButton)findViewById(R.id.RadioButton38);
		board[3][9] = (RadioButton)findViewById(R.id.RadioButton39);
		board[3][10] = (RadioButton)findViewById(R.id.RadioButton3A);
		board[3][11] = (RadioButton)findViewById(R.id.RadioButton3B);
		board[3][12] = (RadioButton)findViewById(R.id.RadioButton3C);
		
		board[4][0] = (RadioButton)findViewById(R.id.RadioButton40);
		board[4][1] = (RadioButton)findViewById(R.id.RadioButton41);
		board[4][2] = (RadioButton)findViewById(R.id.RadioButton42);
		board[4][3] = (RadioButton)findViewById(R.id.RadioButton43);
		board[4][4] = (RadioButton)findViewById(R.id.RadioButton44);
		board[4][5] = (RadioButton)findViewById(R.id.RadioButton45);
		board[4][6] = (RadioButton)findViewById(R.id.RadioButton46);
		board[4][7] = (RadioButton)findViewById(R.id.RadioButton47);
		board[4][8] = (RadioButton)findViewById(R.id.RadioButton48);
		board[4][9] = (RadioButton)findViewById(R.id.RadioButton49);
		board[4][10] = (RadioButton)findViewById(R.id.RadioButton4A);
		board[4][11] = (RadioButton)findViewById(R.id.RadioButton4B);
		board[4][12] = (RadioButton)findViewById(R.id.RadioButton4C);
		
		board[5][0] = (RadioButton)findViewById(R.id.RadioButton50);
		board[5][1] = (RadioButton)findViewById(R.id.RadioButton51);
		board[5][2] = (RadioButton)findViewById(R.id.RadioButton52);
		board[5][3] = (RadioButton)findViewById(R.id.RadioButton53);
		board[5][4] = (RadioButton)findViewById(R.id.RadioButton54);
		board[5][5] = (RadioButton)findViewById(R.id.RadioButton55);
		board[5][6] = (RadioButton)findViewById(R.id.RadioButton56);
		board[5][7] = (RadioButton)findViewById(R.id.RadioButton57);
		board[5][8] = (RadioButton)findViewById(R.id.RadioButton58);
		board[5][9] = (RadioButton)findViewById(R.id.RadioButton59);
		board[5][10] = (RadioButton)findViewById(R.id.RadioButton5A);
		board[5][11] = (RadioButton)findViewById(R.id.RadioButton5B);
		board[5][12] = (RadioButton)findViewById(R.id.RadioButton5C);
		
		board[6][0] = (RadioButton)findViewById(R.id.RadioButton60);
		board[6][1] = (RadioButton)findViewById(R.id.RadioButton61);
		board[6][2] = (RadioButton)findViewById(R.id.RadioButton62);
		board[6][3] = (RadioButton)findViewById(R.id.RadioButton63);
		board[6][4] = (RadioButton)findViewById(R.id.RadioButton64);
		board[6][5] = (RadioButton)findViewById(R.id.RadioButton65);
		board[6][6] = (RadioButton)findViewById(R.id.RadioButton66);
		board[6][7] = (RadioButton)findViewById(R.id.RadioButton67);
		board[6][8] = (RadioButton)findViewById(R.id.RadioButton68);
		board[6][9] = (RadioButton)findViewById(R.id.RadioButton69);
		board[6][10] = (RadioButton)findViewById(R.id.RadioButton6A);
		board[6][11] = (RadioButton)findViewById(R.id.RadioButton6B);
		board[6][12] = (RadioButton)findViewById(R.id.RadioButton6C);
		
		board[7][0] = (RadioButton)findViewById(R.id.RadioButton70);
		board[7][1] = (RadioButton)findViewById(R.id.RadioButton71);
		board[7][2] = (RadioButton)findViewById(R.id.RadioButton72);
		board[7][3] = (RadioButton)findViewById(R.id.RadioButton73);
		board[7][4] = (RadioButton)findViewById(R.id.RadioButton74);
		board[7][5] = (RadioButton)findViewById(R.id.RadioButton75);
		board[7][6] = (RadioButton)findViewById(R.id.RadioButton76);
		board[7][7] = (RadioButton)findViewById(R.id.RadioButton77);
		board[7][8] = (RadioButton)findViewById(R.id.RadioButton78);
		board[7][9] = (RadioButton)findViewById(R.id.RadioButton79);
		board[7][10] = (RadioButton)findViewById(R.id.RadioButton7A);
		board[7][11] = (RadioButton)findViewById(R.id.RadioButton7B);
		board[7][12] = (RadioButton)findViewById(R.id.RadioButton7C);
		
		board[8][0] = (RadioButton)findViewById(R.id.RadioButton80);
		board[8][1] = (RadioButton)findViewById(R.id.RadioButton81);
		board[8][2] = (RadioButton)findViewById(R.id.RadioButton82);
		board[8][3] = (RadioButton)findViewById(R.id.RadioButton83);
		board[8][4] = (RadioButton)findViewById(R.id.RadioButton84);
		board[8][5] = (RadioButton)findViewById(R.id.RadioButton85);
		board[8][6] = (RadioButton)findViewById(R.id.RadioButton86);
		board[8][7] = (RadioButton)findViewById(R.id.RadioButton87);
		board[8][8] = (RadioButton)findViewById(R.id.RadioButton88);
		board[8][9] = (RadioButton)findViewById(R.id.RadioButton89);
		board[8][10] = (RadioButton)findViewById(R.id.RadioButton8A);
		board[8][11] = (RadioButton)findViewById(R.id.RadioButton8B);
		board[8][12] = (RadioButton)findViewById(R.id.RadioButton8C);
		
		board[9][0] = (RadioButton)findViewById(R.id.RadioButton90);
		board[9][1] = (RadioButton)findViewById(R.id.RadioButton91);
		board[9][2] = (RadioButton)findViewById(R.id.RadioButton92);
		board[9][3] = (RadioButton)findViewById(R.id.RadioButton93);
		board[9][4] = (RadioButton)findViewById(R.id.RadioButton94);
		board[9][5] = (RadioButton)findViewById(R.id.RadioButton95);
		board[9][6] = (RadioButton)findViewById(R.id.RadioButton96);
		board[9][7] = (RadioButton)findViewById(R.id.RadioButton97);
		board[9][8] = (RadioButton)findViewById(R.id.RadioButton98);
		board[9][9] = (RadioButton)findViewById(R.id.RadioButton99);
		board[9][10] = (RadioButton)findViewById(R.id.RadioButton9A);
		board[9][11] = (RadioButton)findViewById(R.id.RadioButton9B);
		board[9][12] = (RadioButton)findViewById(R.id.RadioButton9C);
		
		board[10][0] = (RadioButton)findViewById(R.id.RadioButtonA0);
		board[10][1] = (RadioButton)findViewById(R.id.RadioButtonA1);
		board[10][2] = (RadioButton)findViewById(R.id.RadioButtonA2);
		board[10][3] = (RadioButton)findViewById(R.id.RadioButtonA3);
		board[10][4] = (RadioButton)findViewById(R.id.RadioButtonA4);
		board[10][5] = (RadioButton)findViewById(R.id.RadioButtonA5);
		board[10][6] = (RadioButton)findViewById(R.id.RadioButtonA6);
		board[10][7] = (RadioButton)findViewById(R.id.RadioButtonA7);
		board[10][8] = (RadioButton)findViewById(R.id.RadioButtonA8);
		board[10][9] = (RadioButton)findViewById(R.id.RadioButtonA9);
		board[10][10] = (RadioButton)findViewById(R.id.RadioButtonAA);
		board[10][11] = (RadioButton)findViewById(R.id.RadioButtonAB);
		board[10][12] = (RadioButton)findViewById(R.id.RadioButtonAC);
		
		board[11][0] = (RadioButton)findViewById(R.id.RadioButtonB0);
		board[11][1] = (RadioButton)findViewById(R.id.RadioButtonB1);
		board[11][2] = (RadioButton)findViewById(R.id.RadioButtonB2);
		board[11][3] = (RadioButton)findViewById(R.id.RadioButtonB3);
		board[11][4] = (RadioButton)findViewById(R.id.RadioButtonB4);
		board[11][5] = (RadioButton)findViewById(R.id.RadioButtonB5);
		board[11][6] = (RadioButton)findViewById(R.id.RadioButtonB6);
		board[11][7] = (RadioButton)findViewById(R.id.RadioButtonB7);
		board[11][8] = (RadioButton)findViewById(R.id.RadioButtonB8);
		board[11][9] = (RadioButton)findViewById(R.id.RadioButtonB9);
		board[11][10] = (RadioButton)findViewById(R.id.RadioButtonBA);
		board[11][11] = (RadioButton)findViewById(R.id.RadioButtonBB);
		board[11][12] = (RadioButton)findViewById(R.id.RadioButtonBC);
		
		board[12][0] = (RadioButton)findViewById(R.id.RadioButtonC0);
		board[12][1] = (RadioButton)findViewById(R.id.RadioButtonC1);
		board[12][2] = (RadioButton)findViewById(R.id.RadioButtonC2);
		board[12][3] = (RadioButton)findViewById(R.id.RadioButtonC3);
		board[12][4] = (RadioButton)findViewById(R.id.RadioButtonC4);
		board[12][5] = (RadioButton)findViewById(R.id.RadioButtonC5);
		board[12][6] = (RadioButton)findViewById(R.id.RadioButtonC6);
		board[12][7] = (RadioButton)findViewById(R.id.RadioButtonC7);
		board[12][8] = (RadioButton)findViewById(R.id.RadioButtonC8);
		board[12][9] = (RadioButton)findViewById(R.id.RadioButtonC9);
		board[12][10] = (RadioButton)findViewById(R.id.RadioButtonCA);
		board[12][11] = (RadioButton)findViewById(R.id.RadioButtonCB);
		board[12][12] = (RadioButton)findViewById(R.id.RadioButtonCC);
		
		
		SetEnable(MyTurn);
		
		BoardColumn0.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				
			//	((TextView)findViewById(R.id.msg)).setText(group.toString());
				
				String x="",y="";
				switch (checkedId){
				case R.id.radio00:
					x="0";	y="0";
					board[0][0].setEnabled(false);
					board[0][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.radio01:
					x="0";	y="1";
					board[0][1].setEnabled(false);
					board[0][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.radio02:
					x="0";	y="2";
					board[0][2].setEnabled(false);
					board[0][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.radio03:
					x="0";	y="3";
					board[0][3].setEnabled(false);
					board[0][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.radio04:
					x="0";	y="4";
					board[0][4].setEnabled(false);
					board[0][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.radio05:
					x="0";	y="5";
					board[0][5].setEnabled(false);
					board[0][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.radio06:
					x="0";	y="6";
					board[0][6].setEnabled(false);
					board[0][6].setBackgroundDrawable(blackIcon);
					break;
				
				case R.id.radio07:
					x="0";	y="7";
					board[0][7].setEnabled(false);
					board[0][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.radio08:
					x="0";	y="8";
					board[0][8].setEnabled(false);
					board[0][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.radio09:
					x="0";	y="9";
					board[0][9].setEnabled(false);
					board[0][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.radio0A:
					x="0";	y="10";
					board[0][10].setEnabled(false);
					board[0][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.radio0B:
					x="0";	y="11";
					board[0][11].setEnabled(false);
					board[0][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.radio0C:
					x="0";	y="12";
					board[0][12].setEnabled(false);
					board[0][12].setBackgroundDrawable(blackIcon);
					break;
				}
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
				new SocketSend().execute(user,"draw",x,y);
				
        		
        		
			}
		});
		
		BoardColumn1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String x="",y="";
				switch (checkedId){
				case R.id.RadioButton10:
					x="1";	y="0";
					board[1][0].setEnabled(false);
					board[1][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton11:
					x="1";	y="1";
					board[1][1].setEnabled(false);
					board[1][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton12:
					x="1";	y="2";
					board[1][2].setEnabled(false);
					board[1][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton13:
					x="1";	y="3";
					board[1][3].setEnabled(false);
					board[1][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton14:
					x="1";	y="4";
					board[1][4].setEnabled(false);
					board[1][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton15:
					x="1";	y="5";
					board[1][5].setEnabled(false);
					board[1][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton16:
					x="1";	y="6";
					board[1][6].setEnabled(false);
					board[1][6].setBackgroundDrawable(blackIcon);
					break;				
				case R.id.RadioButton17:
					x="1";	y="7";
					board[1][7].setEnabled(false);
					board[1][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton18:
					x="1";	y="8";
					board[1][8].setEnabled(false);
					board[1][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton19:
					x="1";	y="9";
					board[1][9].setEnabled(false);
					board[1][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton1A:
					x="1";	y="10";
					board[1][10].setEnabled(false);
					board[1][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton1B:
					x="1";	y="11";
					board[1][11].setEnabled(false);
					board[1][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton1C:
					x="1";	y="12";
					board[1][12].setEnabled(false);
					board[1][12].setBackgroundDrawable(blackIcon);
					break;
				}
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
				new SocketSend().execute(user,"draw",x,y);
				
			}
		});
		
		BoardColumn2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String x="",y="";
				switch (checkedId){
				case R.id.RadioButton20:
					x="2";	y="0";
					board[2][0].setEnabled(false);
					board[2][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton21:
					x="2";	y="1";
					board[2][1].setEnabled(false);
					board[2][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton22:
					x="2";	y="2";
					board[2][2].setEnabled(false);
					board[2][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton23:
					x="2";	y="3";
					board[2][3].setEnabled(false);
					board[2][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton24:
					x="2";	y="4";
					board[2][4].setEnabled(false);
					board[2][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton25:
					x="2";	y="5";
					board[2][5].setEnabled(false);
					board[2][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton26:
					x="2";	y="6";
					board[2][6].setEnabled(false);
					board[2][6].setBackgroundDrawable(blackIcon);
					break;				
				case R.id.RadioButton27:
					x="2";	y="7";
					board[2][7].setEnabled(false);
					board[2][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton28:
					x="2";	y="8";
					board[2][8].setEnabled(false);
					board[2][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton29:
					x="2";	y="9";
					board[2][9].setEnabled(false);
					board[2][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton2A:
					x="2";	y="10";
					board[2][10].setEnabled(false);
					board[2][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton2B:
					x="2";	y="11";
					board[2][11].setEnabled(false);
					board[2][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton2C:
					x="2";	y="12";
					board[2][12].setEnabled(false);
					board[2][12].setBackgroundDrawable(blackIcon);
					break;
				}
			
				new SocketSend().execute(user,"draw",x,y);
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
			}
		});
		
		BoardColumn3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String x="",y="";
				switch (checkedId){
				case R.id.RadioButton30:
					x="3";	y="0";
					board[3][0].setEnabled(false);
					board[3][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton31:
					x="3";	y="1";
					board[3][1].setEnabled(false);
					board[3][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton32:
					x="3";	y="2";
					board[3][2].setEnabled(false);
					board[3][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton33:
					x="3";	y="3";
					board[3][3].setEnabled(false);
					board[3][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton34:
					x="3";	y="4";
					board[3][4].setEnabled(false);
					board[3][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton35:
					x="3";	y="5";
					board[3][5].setEnabled(false);
					board[3][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton36:
					x="3";	y="6";
					board[3][6].setEnabled(false);
					board[3][6].setBackgroundDrawable(blackIcon);
					break;				
				case R.id.RadioButton37:
					x="3";	y="7";
					board[3][7].setEnabled(false);
					board[3][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton38:
					x="3";	y="8";
					board[3][8].setEnabled(false);
					board[3][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton39:
					x="3";	y="9";
					board[3][9].setEnabled(false);
					board[3][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton3A:
					x="3";	y="10";
					board[3][10].setEnabled(false);
					board[3][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton3B:
					x="3";	y="11";
					board[3][11].setEnabled(false);
					board[3][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton3C:
					x="3";	y="12";
					board[3][12].setEnabled(false);
					board[3][12].setBackgroundDrawable(blackIcon);
					break;
				}
			
				new SocketSend().execute(user,"draw",x,y);
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
			}
		});
		
		BoardColumn4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String x="",y="";
				switch (checkedId){
				case R.id.RadioButton40:
					x="4";	y="0";
					board[4][0].setEnabled(false);
					board[4][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton41:
					x="4";	y="1";
					board[4][1].setEnabled(false);
					board[4][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton42:
					x="4";	y="2";
					board[4][2].setEnabled(false);
					board[4][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton43:
					x="4";	y="3";
					board[4][3].setEnabled(false);
					board[4][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton44:
					x="4";	y="4";
					board[4][4].setEnabled(false);
					board[4][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton45:
					x="4";	y="5";
					board[4][5].setEnabled(false);
					board[4][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton46:
					x="4";	y="6";
					board[4][6].setEnabled(false);
					board[4][6].setBackgroundDrawable(blackIcon);
					break;				
				case R.id.RadioButton47:
					x="4";	y="7";
					board[4][7].setEnabled(false);
					board[4][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton48:
					x="4";	y="8";
					board[4][8].setEnabled(false);
					board[4][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton49:
					x="4";	y="9";
					board[4][9].setEnabled(false);
					board[4][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton4A:
					x="4";	y="10";
					board[4][10].setEnabled(false);
					board[4][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton4B:
					x="4";	y="11";
					board[4][11].setEnabled(false);
					board[4][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton4C:
					x="4";	y="12";
					board[4][12].setEnabled(false);
					board[4][12].setBackgroundDrawable(blackIcon);
					break;
				}
			
				new SocketSend().execute(user,"draw",x,y);
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
			}
		});
		
		BoardColumn5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String x="",y="";
				switch (checkedId){
				case R.id.RadioButton50:
					x="5";	y="0";
					board[5][0].setEnabled(false);
					board[5][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton51:
					x="5";	y="1";
					board[5][1].setEnabled(false);
					board[5][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton52:
					x="5";	y="2";
					board[5][2].setEnabled(false);
					board[5][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton53:
					x="5";	y="3";
					board[5][3].setEnabled(false);
					board[5][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton54:
					x="5";	y="4";
					board[5][4].setEnabled(false);
					board[5][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton55:
					x="5";	y="5";
					board[5][5].setEnabled(false);
					board[5][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton56:
					x="5";	y="6";
					board[5][6].setEnabled(false);
					board[5][6].setBackgroundDrawable(blackIcon);
					break;				
				case R.id.RadioButton57:
					x="5";	y="7";
					board[5][7].setEnabled(false);
					board[5][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton58:
					x="5";	y="8";
					board[5][8].setEnabled(false);
					board[5][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton59:
					x="5";	y="9";
					board[5][9].setEnabled(false);
					board[5][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton5A:
					x="5";	y="10";
					board[5][10].setEnabled(false);
					board[5][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton5B:
					x="5";	y="11";
					board[5][11].setEnabled(false);
					board[5][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton5C:
					x="5";	y="12";
					board[5][12].setEnabled(false);
					board[5][12].setBackgroundDrawable(blackIcon);
					break;
				}
			
				new SocketSend().execute(user,"draw",x,y);
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
			}
		});
		
		BoardColumn6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String x="",y="";
				switch (checkedId){
				case R.id.RadioButton60:
					x="6";	y="0";
					board[6][0].setEnabled(false);
					board[6][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton61:
					x="6";	y="1";
					board[6][1].setEnabled(false);
					board[6][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton62:
					x="6";	y="2";
					board[6][2].setEnabled(false);
					board[6][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton63:
					x="6";	y="3";
					board[6][3].setEnabled(false);
					board[6][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton64:
					x="6";	y="4";
					board[6][4].setEnabled(false);
					board[6][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton65:
					x="6";	y="5";
					board[6][5].setEnabled(false);
					board[6][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton66:
					x="6";	y="6";
					board[6][6].setEnabled(false);
					board[6][6].setBackgroundDrawable(blackIcon);
					break;				
				case R.id.RadioButton67:
					x="6";	y="7";
					board[6][7].setEnabled(false);
					board[6][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton68:
					x="6";	y="8";
					board[6][8].setEnabled(false);
					board[6][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton69:
					x="6";	y="9";
					board[6][9].setEnabled(false);
					board[6][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton6A:
					x="6";	y="10";
					board[6][10].setEnabled(false);
					board[6][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton6B:
					x="6";	y="11";
					board[6][11].setEnabled(false);
					board[6][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton6C:
					x="6";	y="12";
					board[6][12].setEnabled(false);
					board[6][12].setBackgroundDrawable(blackIcon);
					break;
				}
			
				new SocketSend().execute(user,"draw",x,y);
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
			}
		});
		
		BoardColumn7.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String x="",y="";
				switch (checkedId){
				case R.id.RadioButton70:
					x="7";	y="0";
					board[7][0].setEnabled(false);
					board[7][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton71:
					x="7";	y="1";
					board[7][1].setEnabled(false);
					board[7][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton72:
					x="7";	y="2";
					board[7][2].setEnabled(false);
					board[7][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton73:
					x="7";	y="3";
					board[7][3].setEnabled(false);
					board[7][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton74:
					x="7";	y="4";
					board[7][4].setEnabled(false);
					board[7][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton75:
					x="7";	y="5";
					board[7][5].setEnabled(false);
					board[7][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton76:
					x="7";	y="6";
					board[7][6].setEnabled(false);
					board[7][6].setBackgroundDrawable(blackIcon);
					break;				
				case R.id.RadioButton77:
					x="7";	y="7";
					board[7][7].setEnabled(false);
					board[7][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton78:
					x="7";	y="8";
					board[7][8].setEnabled(false);
					board[7][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton79:
					x="7";	y="9";
					board[7][9].setEnabled(false);
					board[7][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton7A:
					x="7";	y="10";
					board[7][10].setEnabled(false);
					board[7][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton7B:
					x="7";	y="11";
					board[7][11].setEnabled(false);
					board[7][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton7C:
					x="7";	y="12";
					board[7][12].setEnabled(false);
					board[7][12].setBackgroundDrawable(blackIcon);
					break;
				}
			
				new SocketSend().execute(user,"draw",x,y);
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
			}
		});
		
		BoardColumn8.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String x="",y="";
				switch (checkedId){
				case R.id.RadioButton80:
					x="8";	y="0";
					board[8][0].setEnabled(false);
					board[8][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton81:
					x="8";	y="1";
					board[8][1].setEnabled(false);
					board[8][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton82:
					x="8";	y="2";
					board[8][2].setEnabled(false);
					board[8][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton83:
					x="8";	y="3";
					board[8][3].setEnabled(false);
					board[8][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton84:
					x="8";	y="4";
					board[8][4].setEnabled(false);
					board[8][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton85:
					x="8";	y="5";
					board[8][5].setEnabled(false);
					board[8][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton86:
					x="8";	y="6";
					board[8][6].setEnabled(false);
					board[8][6].setBackgroundDrawable(blackIcon);
					break;				
				case R.id.RadioButton87:
					x="8";	y="7";
					board[8][7].setEnabled(false);
					board[8][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton88:
					x="8";	y="8";
					board[8][8].setEnabled(false);
					board[8][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton89:
					x="8";	y="9";
					board[8][9].setEnabled(false);
					board[8][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton8A:
					x="8";	y="10";
					board[8][10].setEnabled(false);
					board[8][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton8B:
					x="8";	y="11";
					board[8][11].setEnabled(false);
					board[8][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton8C:
					x="8";	y="12";
					board[8][12].setEnabled(false);
					board[8][12].setBackgroundDrawable(blackIcon);
					break;
				}
			
				new SocketSend().execute(user,"draw",x,y);
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
			}
		});
		
		BoardColumn9.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String x="",y="";
				switch (checkedId){
				case R.id.RadioButton90:
					x="9";	y="0";
					board[9][0].setEnabled(false);
					board[9][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton91:
					x="9";	y="1";
					board[9][1].setEnabled(false);
					board[9][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton92:
					x="9";	y="2";
					board[9][2].setEnabled(false);
					board[9][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton93:
					x="9";	y="3";
					board[9][3].setEnabled(false);
					board[9][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton94:
					x="9";	y="4";
					board[9][4].setEnabled(false);
					board[9][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton95:
					x="9";	y="5";
					board[9][5].setEnabled(false);
					board[9][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton96:
					x="9";	y="6";
					board[9][6].setEnabled(false);
					board[9][6].setBackgroundDrawable(blackIcon);
					break;				
				case R.id.RadioButton97:
					x="9";	y="7";
					board[9][7].setEnabled(false);
					board[9][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton98:
					x="9";	y="8";
					board[9][8].setEnabled(false);
					board[9][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton99:
					x="9";	y="9";
					board[9][9].setEnabled(false);
					board[9][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton9A:
					x="9";	y="10";
					board[9][10].setEnabled(false);
					board[9][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton9B:
					x="9";	y="11";
					board[9][11].setEnabled(false);
					board[9][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButton9C:
					x="9";	y="12";
					board[9][12].setEnabled(false);
					board[9][12].setBackgroundDrawable(blackIcon);
					break;
				}
			
				new SocketSend().execute(user,"draw",x,y);
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
			}
		});
		
		BoardColumnA.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String x="",y="";
				switch (checkedId){
				case R.id.RadioButtonA0:
					x="10";	y="0";
					board[10][0].setEnabled(false);
					board[10][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonA1:
					x="10";	y="1";
					board[10][1].setEnabled(false);
					board[10][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonA2:
					x="10";	y="2";
					board[10][2].setEnabled(false);
					board[10][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonA3:
					x="10";	y="3";
					board[10][3].setEnabled(false);
					board[10][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonA4:
					x="10";	y="4";
					board[10][4].setEnabled(false);
					board[10][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonA5:
					x="10";	y="5";
					board[10][5].setEnabled(false);
					board[10][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonA6:
					x="10";	y="6";
					board[10][6].setEnabled(false);
					board[10][6].setBackgroundDrawable(blackIcon);
					break;				
				case R.id.RadioButtonA7:
					x="10";	y="7";
					board[10][7].setEnabled(false);
					board[10][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonA8:
					x="10";	y="8";
					board[10][8].setEnabled(false);
					board[10][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonA9:
					x="10";	y="9";
					board[10][9].setEnabled(false);
					board[10][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonAA:
					x="10";	y="10";
					board[10][10].setEnabled(false);
					board[10][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonAB:
					x="10";	y="11";
					board[10][11].setEnabled(false);
					board[10][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonAC:
					x="10";	y="12";
					board[10][12].setEnabled(false);
					board[10][12].setBackgroundDrawable(blackIcon);
					break;
				}
			
				new SocketSend().execute(user,"draw",x,y);
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
			}
		});
		
		BoardColumnB.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String x="",y="";
				switch (checkedId){
				case R.id.RadioButtonB0:
					x="11";	y="0";
					board[11][0].setEnabled(false);
					board[11][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonB1:
					x="11";	y="1";
					board[11][1].setEnabled(false);
					board[11][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonB2:
					x="11";	y="2";
					board[11][2].setEnabled(false);
					board[11][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonB3:
					x="11";	y="3";
					board[11][3].setEnabled(false);
					board[11][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonB4:
					x="11";	y="4";
					board[11][4].setEnabled(false);
					board[11][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonB5:
					x="11";	y="5";
					board[11][5].setEnabled(false);
					board[11][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonB6:
					x="11";	y="6";
					board[11][6].setEnabled(false);
					board[11][6].setBackgroundDrawable(blackIcon);
					break;				
				case R.id.RadioButtonB7:
					x="11";	y="7";
					board[11][7].setEnabled(false);
					board[11][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonB8:
					x="11";	y="8";
					board[11][8].setEnabled(false);
					board[11][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonB9:
					x="11";	y="9";
					board[11][9].setEnabled(false);
					board[11][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonBA:
					x="11";	y="10";
					board[11][10].setEnabled(false);
					board[11][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonBB:
					x="11";	y="11";
					board[11][11].setEnabled(false);
					board[11][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonBC:
					x="11";	y="12";
					board[11][12].setEnabled(false);
					board[11][12].setBackgroundDrawable(blackIcon);
					break;
				}
			
				new SocketSend().execute(user,"draw",x,y);
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
			}
		});
		
		BoardColumnC.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String x="",y="";
				switch (checkedId){
				case R.id.RadioButtonC0:
					x="12";	y="0";
					board[12][0].setEnabled(false);
					board[12][0].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonC1:
					x="12";	y="1";
					board[12][1].setEnabled(false);
					board[12][1].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonC2:
					x="12";	y="2";
					board[12][2].setEnabled(false);
					board[12][2].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonC3:
					x="12";	y="3";
					board[12][3].setEnabled(false);
					board[12][3].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonC4:
					x="12";	y="4";
					board[12][4].setEnabled(false);
					board[12][4].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonC5:
					x="12";	y="5";
					board[12][5].setEnabled(false);
					board[12][5].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonC6:
					x="12";	y="6";
					board[12][6].setEnabled(false);
					board[12][6].setBackgroundDrawable(blackIcon);
					break;				
				case R.id.RadioButtonC7:
					x="12";	y="7";
					board[12][7].setEnabled(false);
					board[12][7].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonC8:
					x="12";	y="8";
					board[12][8].setEnabled(false);
					board[12][8].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonC9:
					x="12";	y="9";
					board[12][9].setEnabled(false);
					board[12][9].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonCA:
					x="12";	y="10";
					board[12][10].setEnabled(false);
					board[12][10].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonCB:
					x="12";	y="11";
					board[12][11].setEnabled(false);
					board[12][11].setBackgroundDrawable(blackIcon);
					break;
				case R.id.RadioButtonCC:
					x="12";	y="12";
					board[12][12].setEnabled(false);
					board[12][12].setBackgroundDrawable(blackIcon);
					break;
				}
			
				new SocketSend().execute(user,"draw",x,y);
				MyTurn = false;		//set ui enable
        		SetEnable(MyTurn);	//set ui enable
			}
		});
		
		for(int i=0;i<boardsize;i++){
			for(int j=0;j<boardsize;j++){
				boardmap[i][j] = false;
			}
		}
	
		
		msgenter.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == event.ACTION_DOWN)
				if(keyCode == KeyEvent.KEYCODE_ENTER){
						if(msgenter.getText().length() > 0){
							if(msgenter.getText().toString().indexOf(":") == -1 &&
									msgenter.getText().toString().indexOf(";") == -1	){
								new SocketSend().execute(user,"msg",msgenter.getText().toString());
								msglist.append(user + " > " + msgenter.getText().toString()+"\n");
								msgenter.getText().clear();
							}else{
								msglist.append("*訊息中不能帶有冒號分號！*"+"\n");
								msgenter.getText().clear();
							}
						return true;
					}
				}
				
				return false;
			}
		});
		
		
		exitbtn.setOnClickListener(new View.OnClickListener() {
			//離開按鈕
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(TYPE.equals("fight")){
				new SocketSend().execute(user,"exit");
				}else{
					//	觀戰模式
					if(at.flag == true){
        				at.flag = false;
        				at.interrupt();
        				at = null;
        			}
					Intent i = new Intent();
	        		i.setClass(GameActivity.this, MainActivity.class);
	        		startActivity(i);
	        		GameActivity.this.finish();
					
				}
			}
		});
		
		pushbtn.setOnClickListener(new View.OnClickListener() {
			//催促按鈕
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				new SocketSend().execute(user,"push");
					
				pushCount++;
					
				if(pushCount >= 3){
					pushbtn.setEnabled(false);
				}
				pushbtn.setText("催促對手["+(3-pushCount)+"]");
			}
		});
		
		if(TYPE.equals("watch")){	SetEnable(false);	}
	}
	
	private class SocketSend extends AsyncTask<String, Void, Void> {
		byte[] sentbuf= new byte[1024];
		byte[] revbuf= new byte[1024];
		SocketAddress dstEP; 
		InetAddress  dstIP;
		Socket socket = null;
		
		final int dstPort = 30003;		// game server
		String type;
	//	String user;
		@Override
        protected void onPreExecute() {
			super.onPreExecute();
        	try{
        		dstIP = InetAddress.getByName(ServerIP);
        		dstEP = new InetSocketAddress(dstIP,dstPort);
        	}
        	catch(UnknownHostException e){
        	}
        	
		}
		
        @Override
        protected Void doInBackground(String... params) {
        	type = params[1];
        	String TransStr = "";
        	if(type == "ack"){
        		TransStr += "ack:" + GAMEID+":" +params[0];
        	}else if(type == "draw"){
        		TransStr += "d:" +  GAMEID+":"+params[0]+":"+ params[2] +":"+ params[3];	//d:user:x:y
        		boardmap[Integer.parseInt(params[2])][Integer.parseInt(params[3])] = true;
        	}else if(type == "exit"){
        		TransStr += "e:" + GAMEID +":"+ params[0];
        	}else if(type == "msg"){
        		TransStr += "m:" + GAMEID + ":" + params[0] +":"+params[2]; 
        	}else if(type == "watch"){
        		TransStr += "w:" + GAMEID +":"+ params[0];
        	}else if(type == "push"){
        		TransStr += "p:" + GAMEID +":"+ params[0];
        	}
        	
        	try{
				socket = new Socket();
						
				socket.connect(dstEP,2000);
						
				OutputStream out = null;
				InputStream in = null;
						
				out = socket.getOutputStream();
				in = socket.getInputStream();
				sentbuf = new byte[TransStr.length() +10];
				//System.arraycopy(TransStr.getBytes(), 0, sentbuf, 0, TransStr.length());
				sentbuf = TransStr.getBytes("UTF-8");
				out.write(sentbuf);
				revbuf = new byte[1024];
				in.read(revbuf);
				socket.close();
					
	        }catch(SocketException e){
	        	
	        }catch(IOException e){
	        	
	        }
        	
        	return null;
        }
        
        @SuppressLint("NewApi")
		@Override
        protected void onPostExecute(Void unused) {
        	final Drawable blackIcon = getResources().getDrawable( R.drawable.black );
			final Drawable whiteIcon = getResources().getDrawable( R.drawable.white );
			final Drawable ewhiteIcon = getResources().getDrawable( R.drawable.ewhite );
			try{
			
	        	if(type == "ack"){
	        		
	        		String[] cmd = new String(revbuf,"UTF-8").trim().split(":");
	        		//Iswin : winner : Isupdate : x(update) : y(update)
	        		
	        		if(cmd.length >= 5)
	        		if(cmd[5].equals("y")){
	    				int x = Integer.valueOf(cmd[6].trim()).intValue();
	    				int y = Integer.valueOf(cmd[7].trim()).intValue();
	    				
	    				if(x == -1 && y == -1);
	    				else{
	    					board[x][y].setBackgroundDrawable(ewhiteIcon);
	    					board[x][y].setEnabled(false);
	    					boardmap[x][y] = true;
	    					
	    					LastX = x;
		    				LastY = y;
	    				}
	    				if(!cmd[0].equals("w"))
//Toast disable				Toast.makeText(GameActivity.this, "your turn", Toast.LENGTH_LONG).show();
	    				MyTurn = true;		//set ui enable
	    				SetEnable(MyTurn);	//set ui enable
	    				
	    				
	    			}
	        		
	        		if(cmd.length >= 4)
	        		if(cmd[4].equals("y")){
	        			Toast.makeText(GameActivity.this, "請快點下下一步棋！", Toast.LENGTH_SHORT).show();
	        		}
	        		
	        		
	        		if(cmd[0].equals("w")){
	        			at.flag = false;
	    				at.interrupt();
	    				at = null;
	        			
	        			if(cmd[1].equals("y")){
	        				
	        				new AlertDialog.Builder(GameActivity.this).setTitle("遊戲結束").setMessage("你贏了!").setPositiveButton("返回登入畫面", new DialogInterface.OnClickListener() {
	    						
	    						@Override
	    						public void onClick(DialogInterface dialog, int which) {
	    							// TODO Auto-generated method stub
	    							
	    							Intent i = new Intent();
	    			        		i.setClass(GameActivity.this, MainActivity.class);
	    			        		startActivity(i);
	    			        		GameActivity.this.finish();
	    						}
	    					}).setCancelable(false).show();
	        				
	        			}else if(cmd[1].equals("n")){
	        				
	        				new AlertDialog.Builder(GameActivity.this).setTitle("遊戲結束").setMessage("你輸了!").setPositiveButton("返回登入畫面", new DialogInterface.OnClickListener() {
	    						
	    						@Override
	    						public void onClick(DialogInterface dialog, int which) {
	    							// TODO Auto-generated method stub
	    							
	    							Intent i = new Intent();
	    			        		i.setClass(GameActivity.this, MainActivity.class);
	    			        		startActivity(i);
	    			        		GameActivity.this.finish();
	    						}
	    					}).setCancelable(false).show();
	        			}
	        			//	Return to main form.
	        		}else if(cmd[0].equals("e")){
        				Toast.makeText(GameActivity.this, "系統錯誤，回到主畫面。", Toast.LENGTH_LONG).show();
	        			
	        			try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// a83g; TODO Auto-generated catch block
							e.printStackTrace();
						}
	        				
	        			if(at.flag == true){
	        				at.flag = false;
	        				at.interrupt();
	        				at = null;
	        			}
	        				
	        			Intent i = new Intent();
	        			i.setClass(GameActivity.this, MainActivity.class);
	        			startActivity(i);
	        			GameActivity.this.finish();
        			}
	        		
	        		if(cmd.length >= 2)
	        		if(cmd[2].equals("y")){
	        				
	        			if(!cmd[3].equals(user)){
	        				Toast.makeText(GameActivity.this, "對手退出遊戲", Toast.LENGTH_LONG).show();
	        			}else{
	        				Toast.makeText(GameActivity.this, "已通知對手退出遊戲", Toast.LENGTH_LONG).show();
	        			}
	        				
	        			try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        				
	        			if(at.flag == true){
	        				at.flag = false;
	        				at.interrupt();
	        				at = null;
	        			}
	        				
	        			Intent i = new Intent();
	        			i.setClass(GameActivity.this, MainActivity.class);
	        			startActivity(i);
	        			GameActivity.this.finish();
	        		}else{
	        			//
	        		}
	        		
	        		if(cmd.length >= 8)
	        		if(cmd[8].equals("y")){
	        			//cmd 8  =>  msg
	        			msglist.append(fight_name+" > ");
	        			String msg[] = cmd[9].split(";");
	        			
	        			for(int ii = 0; ii< msg.length; ii++)
	        				msglist.append(" "+msg[ii]+"\n");
	        			
	        			msg = null;
	        			if((msglist.getLineHeight()*msglist.getLineCount())-msglist.getHeight() > 0)
	        			msglist.setScrollY(
	        					(msglist.getLineHeight()*msglist.getLineCount())-msglist.getHeight()
	        					);
	        			
	        		}
	        		
	        	}else if(type == "msg"){
	        		//
	        	}else if(type == "draw"){
	        		//        		
	        	}else if(type == "exit"){
	        		//
	        	}else if(type == "watch"){
	        		//	觀戰模式
	        		String[] cmd = new String(revbuf,"UTF-8").trim().split(":");
	        		if(cmd[0].equals("s")){
	        			//update
	        			for(int i=1;i<cmd.length;i++){
	        				String uuser =  cmd[i].split(";")[0];
	        				int x = Integer.valueOf(cmd[i].split(";")[1].trim()).intValue();
		    				int y = Integer.valueOf(cmd[i].split(";")[2].trim()).intValue();
	        				
	        				if(uuser.equals("0")){
	        					board[x][y].setBackgroundDrawable(blackIcon);
	        				}else if(uuser.equals("1")){
	        					board[x][y].setBackgroundDrawable(whiteIcon);
	        				}
	        				step++;
	        				
	        				pushbtn.setText("第 "+String.valueOf(step)+" 步");
	        				
	        				if((step % 2) == 0){
	        					((TextView)findViewById(R.id.play1)).setText("->");
	        					((TextView)findViewById(R.id.play2)).setText("");
	        				}else{
	        					((TextView)findViewById(R.id.play2)).setText("->");
	        					((TextView)findViewById(R.id.play1)).setText("");
	        				}
	        				
	        			}
	        		}else if(cmd[0].equals("n")){
	        			//	沒有新訊息
	        		}else if(cmd[0].equals("e")){
	        			//	離開
	        			if(at.flag == true){
	        				at.flag = false;
	        				at.interrupt();
	        				at = null;
	        			}
	        			new AlertDialog.Builder(GameActivity.this).setTitle("系統訊息").setMessage("該局遊戲已結束。").setPositiveButton("返回登入畫面", new DialogInterface.OnClickListener() {
    						
    						@Override
    						public void onClick(DialogInterface dialog, int which) {
    							// TODO Auto-generated method stub
    							
    							Intent i = new Intent();
    			        		i.setClass(GameActivity.this, MainActivity.class);
    			        		startActivity(i);
    			        		GameActivity.this.finish();
    						}
    					}).setCancelable(false).show();
	        			
	        		}
	        	}else if(type == "push"){
	        		//
	        	}
			}catch(Exception e){	e.printStackTrace();	}
    	}
	}
	
	
	
	private class AliveThread extends Thread{
		
		boolean flag = true;
	//	private String step;
		
		@Override
		public void run(){
			while(flag){
				if(TYPE.equals("fight")){
					new SocketSend().execute(user,"ack");
				}else if(TYPE.equals("watch")){
					new SocketSend().execute(String.valueOf(step),"watch");
				}
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

