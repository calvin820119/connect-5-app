
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;



public class server {
	
	/*		package define
	 * 		
	 * 		* member:
	 *		s	login
	 *		o	logout
	 *		ack	ack
	 *		ch	challenge 
	 *		i	reject challenge
	 *		f	fight
	 *
	 *		* game:
	 *		
	 *		
	 *
	 */
	
	
	
	static public void main(String[] args){
		ServerSocket socket;
		BufferedInputStream in;
		BufferedOutputStream out;
		byte[] revbuf = new byte[1024];
		byte[] sndbuf = new byte[1024];
		Table_Member tb = new Table_Member();
		ArrayList<Game> gl = new ArrayList<Game>();
		int _GAME_ID = 0;
		
		TimerThread tt = new TimerThread(tb);
		tt.start();
		GameBoard gb = new GameBoard(gl);
		gb.start();
		
		try{
			socket = new ServerSocket(30001);
			
			System.out.println("Server Starting!");
			System.out.println("Listening ...");
			
			
			while(true){
				Socket client = socket.accept();
				
				//client.setSoTimeout(15000);
				out = new BufferedOutputStream(client.getOutputStream());
				in = new BufferedInputStream(client.getInputStream());
				
				int length = in.read(revbuf);
				String rev = new String(revbuf,0,length,"UTF-8").trim();
				String [] cmd = rev.split(":");
				String callback = "";
				//	echo
				System.out.println(rev);
				
				switch(cmd[0]){
					case "s":
						
						if( !tb.FindName(cmd[1])){
						//	System.out.println("Login "+cmd[1]);
							tb.Register(cmd[1]);
							callback = "s:s";//tb.MemList();
							System.out.println("login");
						}else{
							callback = "e:r";
							System.out.println("同樣姓名登入");
						}
						sndbuf = new byte[callback.length() + 10];
					//	System.arraycopy(callback.getBytes(), 0, sndbuf, 0, callback.length());
						sndbuf = callback.getBytes("UTF-8");
						out.write(sndbuf);
						break;
						
					case "o":
						
						if(tb.FindName(cmd[1]) == true){
						//	System.out.println("Logout "+cmd[1]);
							tb.MemLogout(cmd[1]);
							callback = "o:s";														
						}else{
							System.out.println("name not found");
							callback = "e:nf";
						}
						sndbuf = new byte[callback.length() +10];
					//	System.arraycopy(callback.getBytes(), 0, sndbuf, 0, callback.length());
						sndbuf = callback.getBytes();
						out.write(sndbuf);
						sndbuf = null;
							
						break;
						
					case "ack":
						if(!tb.FindName(cmd[1])){
							callback = "ack:emp";
						}else{
						
						
							tb.SetAckAlive(cmd[1]);
							
						//	System.out.println("ack "+cmd[1]);
							
							callback = "ack:";
							if(tb.IsReq(cmd[1]) == true){
								String chkch = tb.IsChannenge(cmd[1]);
								if(chkch != null){
									callback += "y:" + chkch;
								}else{
									if(tb.GetCancelChelleage(cmd[1])){
										callback += "c:emp";
										tb.ClearCancelChelleage(cmd[1]);
										tb.ClearReq(cmd[1]);
									}else{
										callback += "n:emp";
									}
									
								}
								chkch = null;
							//	tb.ClearReq(cmd[1]);
							}else{
								
								callback += "n:" + "emp";
							}
							
							//if(tb.GetIgnore(cmd[1]))
							if(tb.GetFight(cmd[1]) == true){
								callback += ":" + (tb.GetFight(cmd[1])?"y:"+tb.GetFightName(cmd[1])+":"+tb.GetGameID(cmd[1]):"n:emp:emp");
								try{
								tb.MemLogout(cmd[1]);
								}
								catch(Exception e){e.printStackTrace(); }
							}else if(tb.GetIgnore(cmd[1]) == true){
								callback += ":i:emp:emp";
								tb.ClearIgnore(cmd[1]);
							}else{
								callback += ":n:emp:emp";
							}
							
							callback += tb.MemList();
							//ack : Is_ch : ch_name : Is_fight : fight_name : gamdid :memlist...
							
							callback += gb.GetGameList();
						}
						
//**** print console ****//
System.out.println(callback);
//**** print console ****//

						sndbuf = new byte[callback.length() +10];
					//	System.arraycopy(callback.getBytes(), 0, sndbuf, 0, callback.length());
						sndbuf = callback.getBytes("UTF-8");						
						out.write(sndbuf);
						
						sndbuf = null;
						
						break;
						
					case "ch":
						callback = "";
						if(tb.IsReq(cmd[2])){
							callback += "n:";
						}else{
							tb.SetChallenge(cmd[1], cmd[2]);
							callback += "s:";
							tb.SetReq(cmd[1]);
						}
						
						sndbuf = new byte[ callback.length() +10];
					//	System.arraycopy(callback.getBytes(), 0, sndbuf, 0, callback.length());
						sndbuf = callback.getBytes("UTF-8");						
						out.write(sndbuf);
						sndbuf = null;
						System.out.println(callback);
						break;
					
					case "cch":
						try{
							tb.ClearReq(cmd[2]);
							tb.SetCancelChelleage(cmd[1]);
						}
						catch(Exception e){	e.printStackTrace();	}
						break;
						
					case "i":
						tb.ClearReq(cmd[1]);
						tb.ClearReq(cmd[2]);
						tb.SetIgnore(cmd[1]);
						break;
						
					case "f":
						tb.SetFight(cmd[2]);
						
						tb.SetFight(cmd[1]);
						
						tb.SetFightName(cmd[2],	cmd[1]);
						tb.SetFightName(cmd[1],	cmd[2]);
						
						tb.SetGameID(cmd[2], _GAME_ID);
						tb.SetGameID(cmd[1], _GAME_ID);
						
						Game newgame = new Game(cmd[2],cmd[1],Integer.toString( _GAME_ID));
						
						gl.add(newgame);
						
						//	auto logout the two players
					//	tb.MemLogout(cmd[1]);
					//	tb.MemLogout(cmd[2]);
						
						System.out.println("gID: "+_GAME_ID+" started");
						System.out.println("Player1: "+cmd[2]);
						System.out.println("Player2: "+cmd[1]);
						_GAME_ID++;
						break;
						
					default:
						System.out.println("Cannot fatch command");
						//out.write(new Byte("Cannot fatch command"));
						break;
						
				}
				
			//	client.shutdownInput();
			//	client.shutdownOutput();
				
				out.close();
				in.close();
				client.close();
				rev = null;
				cmd = null;
				callback = null;
				
				System.gc();
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}



//	membertable  //
class Table_Member{
	private int alive_mem_count;
	public ArrayList<Member> memory_list = new ArrayList<Member>();
	
	
	public Table_Member(){
		this.alive_mem_count = 0;
	}
	
	public int GetMemCount(){
		return this.alive_mem_count;
	}
	
	public boolean GetFight(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name))
				return memory_list.get(i)._Fight;
		}
		return false;
	}
	
	public void SetFight(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name))
				memory_list.get(i)._Fight = true;
		}
		
	}
	
	public void Register(String name){
		this.alive_mem_count++;
		Member mem = new Member(name);
		memory_list.add(mem);
		mem = null;//
	}
	
	public boolean FindName(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name))	
				return true;	
		}
		return false;
	}
	
	public void MemLogout(String name){
		
		int i;
		for(i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name))
				break;
		}
		memory_list.remove(i);
		this.alive_mem_count--;
		
	}
	
	//	評估中	//
	public String MemList(){
		String output = "";//
		output += ":"+memory_list.size();
		try{//
			for(int i=0;i<memory_list.size();i++){
				output += ":" + memory_list.get(i)._Name;	
			}
		return output;
		
		}catch(Exception e){	e.printStackTrace();	}//
		finally{	output = null;	}//
		
		return output;//
	}
	
	public void SetChallenge(String nameA,String nameB){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(nameB)){
				memory_list.get(i)._ReqName = nameA;
				memory_list.get(i)._IsReq = true;
				System.out.println(nameA +"-->" + nameB);//
				break;
			}
		}
	}
	
	public void SetReq(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				memory_list.get(i)._IsReq = true;
			}
		}
	}
	
	public boolean IsReq(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				return memory_list.get(i)._IsReq;
			}
		}
		return false;
	}
	
	public void ClearReq(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				 memory_list.get(i)._IsReq = false;
			}
		}
	}
	
	public String IsChannenge(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				if(memory_list.get(i)._IsReq == true){
				//	memory_list.get(i)._IsReq = false;
					String target = memory_list.get(i)._ReqName;
					memory_list.get(i)._ReqName = null;
					return target;
				}
			}
		}
		return null;
	}
	
	public void SetAckAlive(String name){
		
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				memory_list.get(i)._Alive = true;
				//System.out.println(name);//
				break;
			}
		}
	}
	
	public void CheckAlive(){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Alive == false){
				System.out.println(memory_list.get(i)._Name+" 閒置被登出");
				
				MemLogout(memory_list.get(i)._Name);
				
				break;
			}else{
				memory_list.get(i)._Alive = false;
			}
		}
	}
	
	public void SetFightName(String user,String target){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(user))
				memory_list.get(i)._FightName = target;				
		}
	}
	
	public String GetFightName(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				return memory_list.get(i)._FightName;
			}
		}
		return null;
	}
	
	public void SetGameID(String name,int ID){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				memory_list.get(i)._gamdID = ID;
			}
		}
	}
	
	public int GetGameID(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				return memory_list.get(i)._gamdID;
			}
		}
		return -1 ;
	}
	
	public void SetIgnore(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				memory_list.get(i)._Ignore = true;
			}
		}
	}
	
	public void ClearIgnore(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				memory_list.get(i)._Ignore = false;
			}
		}
	}
	
	public void SetCancelChelleage(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				memory_list.get(i)._CancelChallenge = true;
			}
		}
	}
	
	public void ClearCancelChelleage(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				memory_list.get(i)._CancelChallenge = false;
			}
		}
	}
	
	public boolean GetCancelChelleage(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				return memory_list.get(i)._CancelChallenge;
			}
		}
		return false ;
	}
	
	public boolean GetIgnore(String name){
		for(int i=0;i<memory_list.size();i++){
			if(memory_list.get(i)._Name.equals(name)){
				return memory_list.get(i)._Ignore;
			}
		}
		return false ;
	}
}


//	unit member class  //
class Member{
	boolean _Ignore;
	String _Name;
	String _ReqName;
	String _FightName;
	boolean _Alive;
	boolean _IsReq;
	boolean _Fight;
	boolean _CancelChallenge;
	int _gamdID;
	
	public Member(String name){
		this._Ignore = false;
		this._Name = name;
		this._IsReq = false;
		this._ReqName = null;
		this._Alive = true;
		this._Fight = false;
		this._FightName = null;
		this._gamdID = -1 ;
		this._CancelChallenge = false;
	}
}

class TimerThread extends Thread{
	Table_Member table;
	public TimerThread(Table_Member t){
		this.table = t;
	}
	
	public void run(){
		while(true){
			try {
				Thread.sleep(5000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(table.memory_list.size() > 0){
				table.CheckAlive();
			//	System.out.println("thread checked");
			}else{
			//	System.out.println("thread empty");
			}
			System.gc();
		}
	}
}

class Game {
	boolean PushA,PushB;
	boolean IsExitA = false;
	boolean IsExitB = false;
	public String GameID;
	boolean Aren = false;
	boolean Bren = false;
	int step;
	int runA;
	int runB;
	String UserA;
	String UserB;
	boolean[][][] board = new boolean[2][13][13];
	int Ax,Ay,Bx,By;
	String IsWin = null;
	String Exituser = null;
	boolean IsLoginA = false;
	boolean IsLoginB = false;
	ArrayList<String> boardstep = new ArrayList<String>();
	ArrayList<String> Amsg = new ArrayList<String>();
	ArrayList<String> Bmsg = new ArrayList<String>();
	
	public Game(String first,String second,String ID){
		this.step = 0;
		this.GameID = ID;
		this.UserA = first;
		this.UserB = second;
		for(int i=0;i<13;i++){
			for(int j=0;j<13;j++){
				board[0][i][j] = false;	//A
				board[1][i][j] = false;	//B
			}
		}
		this.runA = 0;
		this.runB = 0;
		this.Bren = true;
		this.Ax = 0;
		this.Ay = 0;
		this.Bx = -1;	//initial
		this.By = -1;
		
	}
	
	public boolean checkWin(int user,int x,int y){
		
	//	if(user == 0){
			// check board[0]
	//	}else if(user == 1){
			// check board[1]
	//	}
		
		int count = 1;
		for(int i=x-1;i>=0;i--){
			if(board[user][i][y] == true){
				count++;
				if(count >= 5)	return true;
			}
			else	break;
		}
		for(int i=x+1;i<=12;i++){
			if(board[user][i][y] == true){
				count++;
				if(count >= 5)	return true;
			}
			else	break;
		}
		
		count = 1;
		for(int i=y-1;i>=0;i--){
			if(board[user][x][i] == true){
				count++;
				if(count >= 5)	return true;
			}
			else	break;
		}
		for(int i=y+1;i<=12;i++){
			if(board[user][x][i] == true){
				count++;
				if(count >= 5)	return true;
			}
			else	break;
		}
		count = 1;
		for(int i=x+1,j=y+1;i<=12 && j<=12;i++,j++){
			if(board[user][i][j] == true){
				count++;
				if(count >= 5)	return true;
			}
			else	break;
		}
		for(int i=x-1,j=y-1;i>=0 && j>=0;i--,j--){
			if(board[user][i][j] == true){
				count++;
				if(count >= 5)	return true;
			}
			else	break;
		}
		count = 1;
		for(int i=x+1,j=y-1;i<=12 && j>=0;i++,j--){
			if(board[user][i][j] == true){
				count++;
				if(count >= 5)	return true;
			}
			else	break;
		}
		for(int i=x-1,j=y+1;i>=0 && j<=12;i--,j++){
			if(board[user][i][j] == true){
				count++;
				if(count >= 5)	return true;
			}
			else	break;
		}
		
		return false;
	}
	
	public void SetBoard(int user,int x,int y){
		this.board[user][x][y] = true;
		if(user == 0){
			this.Ax = x;
			this.Ay = y;
			this.Aren = true;
		}else if(user == 1){
			this.Bx = x;
			this.By = y;
			this.Bren = true;
		}
		step++;
		boardstep.add(user+";"+x+";"+y);
		System.out.println(step);
	}
	
	public void PushMsg(int user,String smsg){
		if(user == 1)
			this.Amsg.add(smsg);
		else if(user == 2)
			this.Bmsg.add(smsg);
	}
	
	public String PopMsg(int user){
		String ret = "";
		if(user == 1){
			if(this.Amsg.size() == 0)	return null;
			while(this.Amsg.size() > 0)
				ret += this.Amsg.remove(0) + ";";
		}else if(user == 2){
			if(this.Bmsg.size() == 0)	return null;
			while(this.Bmsg.size() > 0)
				ret += this.Bmsg.remove(0) + "\n";
		}
		return ret;
	}
	
	public String getBoard(int startstep){
		String ret = "";
	//	if(step == startstep)	return null;
		for(int i=startstep;i<step;i++){
			ret += ":"+ boardstep.get(i);
		}
		
		return ret;
	}
}

//game  //
class GameBoard extends Thread{
	boolean flag = true;

	ServerSocket socket;
	BufferedInputStream in;
	BufferedOutputStream out;
	byte[] revbuf = new byte[1024];
	byte[] sndbuf = new byte[1024];
	
	
	
	public ArrayList<Game> GameList = null;
	
	public GameBoard(ArrayList<Game> list){
		this.GameList = list;
	}
	
	public void run(){
		try{
			socket = new ServerSocket(30003);
			
			while(flag){
				Socket client = socket.accept();
				
				out = new BufferedOutputStream(client.getOutputStream());
				in = new BufferedInputStream(client.getInputStream());
				
				int length = in.read(revbuf);
				String rev = new String(revbuf,0,length,"UTF-8").trim();
				
				String [] cmd = rev.split(":");
				String _callback = null;
				String gID;
				//	ack	:	gid	:	user
				//	d	:	gid	:	user	:	x	:	y
				
				System.out.println("in->"+rev);
				
				switch(cmd[0]){
					case "d":
						_callback = "";
						gID = cmd[1];
						for(int i=0;i<GameList.size();i++){
							if(GameList.get(i).GameID.equals(gID)){
								if(cmd[2].equals(GameList.get(i).UserA)){
									int x = Integer.parseInt(cmd[3]);
									int y = Integer.parseInt(cmd[4]);
									GameList.get(i).SetBoard(0, x, y);
									if(GameList.get(i).checkWin(0,x,y)){
										GameList.get(i).IsWin = "A";
									}
								}
								if(cmd[2].equals(GameList.get(i).UserB)){
									int x = Integer.parseInt(cmd[3]);
									int y = Integer.parseInt(cmd[4]);
									GameList.get(i).SetBoard(1, x, y);
									if(GameList.get(i).checkWin(1,x,y)){
										GameList.get(i).IsWin = "B";
									}
								}
								
							//	GameList.get(i).step++;
							}
						}
						
						_callback = "n:";
						
						sndbuf = new byte[_callback.length() +10];
						//System.arraycopy(_callback.getBytes(), 0, sndbuf, 0, _callback.length());
						sndbuf = _callback.getBytes("UTF-8");
						out.write(sndbuf);
			//			System.out.println("back->"+_callback);
						break;
						
						
					case "ack":
					//	int remove = -1;
						boolean isExist = false;
						_callback = "";
						gID = cmd[1];
						for(int i=0;i<GameList.size();i++){
							if(GameList.get(i).GameID.equals(gID)){
								isExist = true;
								
								if(cmd[2].equals(GameList.get(i).UserA)){
									//
									GameList.get(i).IsLoginA = true;
									if(GameList.get(i).IsLoginB){
										GameList.get(i).runA++;
										GameList.get(i).runB=0;
										if(GameList.get(i).runA - GameList.get(i).runB > 10){
											GameList.get(i).Exituser = GameList.get(i).UserB;
											GameList.get(i).IsExitB = true;
											System.out.println("**B exit**");
										}
									}
									//
									if(GameList.get(i).IsWin != null){
										_callback += "w:" + (GameList.get(i).IsWin.equals("A")?"y":"n");
										//is win
									//	remove = i;
										GameList.get(i).IsExitA = true;
									//	System.out.println("gID: "+gID+" has been finished");
									}else{
										_callback += "n:emp";
									}
									
									if(GameList.get(i).Exituser != null){
										_callback += ":y:" + GameList.get(i).Exituser;
									//	GameList.get(i).Exituser = null;
										GameList.get(i).IsExitA = true;
									}else{
										_callback += ":n:emp";
									}
									
									if(GameList.get(i).PushA == true){
										_callback += ":y";
										GameList.get(i).PushA = false;
									}else{
										_callback += ":n";
									}
									
									if(GameList.get(i).Bren){
										_callback += ":y:" + GameList.get(i).Bx + ":" + GameList.get(i).By;
										GameList.get(i).Bren = false;
									}else{
										_callback += ":n:z:z";
									}
									
									String mm = GameList.get(i).PopMsg(1);
									if(mm != null){
										_callback += ":y:" + mm;
									}else{
										_callback += ":n:";
									}
									
									
									
								}else if(cmd[2].equals(GameList.get(i).UserB)){
									//
									GameList.get(i).IsLoginB = true;
									if(GameList.get(i).IsLoginA){
										GameList.get(i).runB++;
										GameList.get(i).runA=0;
										if(GameList.get(i).runB - GameList.get(i).runA > 10){
											GameList.get(i).Exituser = GameList.get(i).UserA;
											GameList.get(i).IsExitA = true;
											System.out.println("**A exit**");
										}
									}
									//
									if(GameList.get(i).IsWin != null){
										_callback += "w:" + (GameList.get(i).IsWin.equals("B")?"y":"n");
										//is win
									//	GameList.remove(i);
										//remove = i;
										GameList.get(i).IsExitB = true;
									//	System.out.println("gID: "+gID+" has been finished");
									}else{
										_callback += "n:emp";
									}
									
									if(GameList.get(i).Exituser != null){
										_callback += ":y:" + GameList.get(i).Exituser;
									//	GameList.get(i).Exituser = null;
										GameList.get(i).IsExitB = true;
									}else{
										_callback += ":n:emp";
									}
									
									if(GameList.get(i).PushB == true){
										_callback += ":y";
										GameList.get(i).PushB = false;
									}else{
										_callback += ":n";
									}
									
									if(GameList.get(i).Aren){
										_callback += ":y:" + GameList.get(i).Ax + ":" + GameList.get(i).Ay;
										GameList.get(i).Aren = false;
									}else{
										_callback += ":n:z:z";
									}
									
									String mm = GameList.get(i).PopMsg(2);
									if(mm != null){
										_callback += ":y:" + mm;
									}else{
										_callback += ":n:";
									}
								}else{	isExist = false;	}
							}							
						}
						
						if(!isExist){
							_callback = "e:emp";
						}
						
						//Iswin : winner : IsExit : Exituser : IsPush : Isupdate : x(update) : y(update) : msg... 
						sndbuf = new byte[_callback.length() +10];
						//System.arraycopy(_callback.getBytes(), 0, sndbuf, 0, _callback.length());
						sndbuf = _callback.getBytes("UTF-8");
						out.write(sndbuf);
		//				System.out.println("back->"+_callback);
						
					//	if(remove != -1){
					//		GameList.remove(remove);
					//	}
						
						for(int i=0;i<GameList.size();i++){
							if(GameList.get(i).GameID.equals(gID)){
								if(GameList.get(i).IsExitA && GameList.get(i).IsExitB){
									GameList.remove(i);
									System.out.println("gID: "+gID+" has been finished");
								}
							}
						}
						
						
						break;
						
					case "e":
						_callback = "";
						gID = cmd[1];
						for(int i=0;i<GameList.size();i++){
							if(GameList.get(i).GameID.equals(gID)){
								GameList.get(i).Exituser = cmd[2];
							}
						}
						
						_callback += "n:";
						sndbuf = new byte[_callback.length() +10];
					//	System.arraycopy(_callback.getBytes(), 0, sndbuf, 0, _callback.length());
						sndbuf = _callback.getBytes("UTF-8");
						out.write(sndbuf);
					//	System.out.println("back->"+_callback);
						break;
						
					case "m":
						_callback = "";
						gID = cmd[1];
						for(int i=0;i<GameList.size();i++){
							if(GameList.get(i).GameID.equals(gID)){
								if(cmd[2].equals(GameList.get(i).UserA)){
									GameList.get(i).PushMsg(2, cmd[3]);
								}else if(cmd[2].equals(GameList.get(i).UserB)){
									GameList.get(i).PushMsg(1, cmd[3]);
								}
							}
						}
						_callback += "n:";
						sndbuf = new byte[_callback.length() +10];
						//	System.arraycopy(_callback.getBytes(), 0, sndbuf, 0, _callback.length());
						sndbuf = _callback.getBytes("UTF-8");
						out.write(sndbuf);
						break;
					case "w":
						_callback = "s";
						isExist = false;
						gID = cmd[1];
						
						for(int i=0;i<GameList.size();i++){
							
							if(GameList.get(i).GameID.equals(gID)){
								isExist = true;
								
								if( Integer.parseInt(cmd[2]) == GameList.get(i).step)
									_callback="n:";
								else
									_callback += GameList.get(i).getBoard(Integer.parseInt(cmd[2]));
							}
						}
						if(!isExist){	_callback = "e:";	}
						
						sndbuf = new byte[_callback.length() +10];
						sndbuf = _callback.getBytes("UTF-8");
						out.write(sndbuf);
						break;
					
					case "p":	//push
						_callback = "n:";
						gID = cmd[1];
						for(int i=0;i<GameList.size();i++){
							if(GameList.get(i).GameID.equals(gID)){
								if(cmd[2].equals(GameList.get(i).UserA)){
									GameList.get(i).PushB = true;
								}else if(cmd[2].equals(GameList.get(i).UserB)){
									GameList.get(i).PushA = true;
								}
							}
						}
						
						sndbuf = new byte[_callback.length() +10];
						sndbuf = _callback.getBytes("UTF-8");
						out.write(sndbuf);
						break;
				}
//**** print console ****//
System.out.println("back->"+_callback);
//**** print console ****//
				out.close();
				in.close();
				client.close();
				
				rev = null;
				cmd = null;
				_callback = null;
				gID = null;
				
				
				System.gc();
			}
			
		}catch(Exception e){
			System.out.println("error");
			e.printStackTrace();
		}
	}
	
	public String GetGameList(){
		String output = "";
		output += ":" + GameList.size();
		for(int i=0;i<GameList.size();i++){
			output += ":" + GameList.get(i).UserA + " vs " + GameList.get(i).UserB+";"+GameList.get(i).GameID;
		}
		
		return output;
	}
	
	
	
}


