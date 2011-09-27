package benchmark.tud.gamecontroller.players;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

import benchmark.tud.gamecontroller.MessageSentNotifier;
import benchmark.tud.gamecontroller.game.InvalidKIFException;
import benchmark.tud.gamecontroller.game.Match;
import benchmark.tud.gamecontroller.game.Move;
import benchmark.tud.gamecontroller.game.Role;
import benchmark.tud.gamecontroller.game.StateInterface;
import benchmark.tud.gamecontroller.game.TermFactoryInterface;
import benchmark.tud.gamecontroller.game.TermInterface;
import benchmark.tud.gamecontroller.scrambling.GameScramblerInterface;

/**
 * Centurio a General Game Player
 *
 * Copyright (C) 2008 Felix Maximilian Möller, Marius Schneider and Martin Wegner
 *
 * This file is part of Centurio.
 *
 * Centurio is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Centurio is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Centurio. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Part of the GameMaster from the University Dresden.
 *
 * @author University Dresden
 * @author Felix Maximilian Möller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */

/*
 * RemotePlayer.java
 * Modified by Team Centurio
 */

public class RemotePlayer<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> extends AbstractPlayer<T,S> {

	private String host;
	private int port;
	private TermFactoryInterface<T> termfactory;
	private GameScramblerInterface gamescrambler=null;
	private Logger logger;
	
	public RemotePlayer(String name, String host, int port, TermFactoryInterface<T> termfactory) {
		super(name);
		this.host=host;
		this.port=port;
		this.termfactory=termfactory;
		this.logger=Logger.getLogger("tud.gamecontroller");
	}

	public void gameStart(Match<T,S> match, Role<T> role, MessageSentNotifier notifier) {
		super.gameStart(match, role, notifier);
		this.gamescrambler=match.getScrambler();
		String msg="(START "+
				match.getMatchID()+" "+
				gamescrambler.scramble(role.getKIFForm())+
				" ("+gamescrambler.scramble(match.getGame().getKIFGameDescription())+") "+
				match.getStartclock()+" "+match.getPlayclock()+")";
		sendMsg(msg, match.getStartclock(), notifier);
	}

	public Move<T> gamePlay(List<Move<T>> priormoves, MessageSentNotifier notifier) {
		Move<T> move=null;
		String msg="(PLAY "+match.getMatchID()+" ";
		if(priormoves==null){
			msg+="NIL)";
		}else{
			msg+="(";
			for(Move<T> m:priormoves){
				msg+=gamescrambler.scramble(m.getKIFForm())+" ";
			}
			msg+="))";
		}
		String reply=sendMsg(msg, match.getPlayclock(), notifier), descrambledReply;
		//logger.info("reply from "+this.getName()+": "+reply);
		if(reply!=null){
			descrambledReply=gamescrambler.descramble(reply);
			T moveterm=null;
			try {
				moveterm = termfactory.getTermFromKIF(descrambledReply);
			} catch (InvalidKIFException ex) {
				System.err.println("Error parsing reply \""+reply+"\" from "+this+":"+ex.getMessage());
			}
			if(moveterm!=null && !moveterm.isGround()){
				System.err.println("Reply \""+reply+"\" from "+this+" is not a ground term. (descrambled:\""+descrambledReply+"\")");
			}else{
				move=new Move<T>(moveterm);
			}
		}
		return move;
	}

	public void gameStop(List<Move<T>> priormoves, MessageSentNotifier notifier) {
		super.gameStop(priormoves, notifier);
		String msg="(STOP "+match.getMatchID()+" ";
		if(priormoves==null){
			msg+="NIL)";
		}else{
			msg+="(";
			for(Move<T> m:priormoves){
				msg+=gamescrambler.scramble(m.getKIFForm())+" ";
			}
			msg+="))";
		}
		sendMsg(msg, match.getPlayclock(), notifier);
	}

	private String sendMsg(String msg, int timeout, MessageSentNotifier notifier) {
		String reply=null;
		Socket s;
		try {
			s = new Socket(host, port);
			
			OutputStream out=s.getOutputStream();
			PrintWriter pw=new PrintWriter(out);
			pw.print("POST / HTTP/1.0\r\n");
			pw.print("Accept: text/delim\r\n");
			pw.print("Sender: Gamecontroller\r\n");
			pw.print("Receiver: "+host+"\r\n");
			pw.print("Content-type: text/acl\r\n");	
			pw.print("Content-length: "+msg.length()+"\r\n");	
			pw.print("\r\n");	
	
			pw.print(msg);
			pw.flush();
			//logger.info("message to "+this.getName()+" sent");
			notifier.messageWasSent();
			
			InputStream is = s.getInputStream();
			if ( is == null) return null;
			BufferedReader in = new BufferedReader( new InputStreamReader( is ));
			String line;
			line = in.readLine();
			while( line!=null && line.trim().length() > 0 ){
				line = in.readLine();
			}
	
			char[] cbuf=new char[1024];
			int len;
			while((len=in.read(cbuf,0,1023))!=-1){
				line=new String(cbuf,0,len);
				if(reply==null) reply=line;
				else reply += line;
			}
			out.close();
			in.close();
		} catch (UnknownHostException e) {
			System.err.println("error: unknown host \""+ host+ "\"");
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("error: io error for "+ this+" : "+e.getMessage());
			System.exit(-1);
		}
		return reply;
	}

	public String toString(){
//		return "remote("+getName()+", "+host+":"+port+")";
		return "remote("+host+":"+port+")";
	}
}
