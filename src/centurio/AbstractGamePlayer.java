package centurio;

import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DateFormat;

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
 * This class implements the abstract class for the GamePlayer. It is from the GGP
 * framework of the University Dresden.
 *
 * @author University Dresden
 * @version 1.0
 */

public abstract class AbstractGamePlayer extends NanoHTTPD {
	
	public AbstractGamePlayer(int port) throws IOException {
		super(port);
	}
	
	public Response serve( String uri, String method, Properties header, Properties parms, String data )
	{
		try{
			String response_string=null;
			if(data!=null){
				System.out.println(DateFormat.getTimeInstance(DateFormat.FULL).format(Calendar.getInstance().getTime()));
				System.out.println("Command: " + data);
				String command=getCommand(data);
				if(command==null){
					throw(new IllegalArgumentException("Unknown message format"));
				}else if(command.equals("START")){
					response_string="READY";
					commandStart(data);
				}else if(command.equals("PLAY")){
					response_string=commandPlay(data);
/*				}else if(command.equals("replay")){
					response_string=commandReplay(data);*/
				}else if(command.equals("STOP")){
					response_string="DONE";
					commandStop(data);
				}else{
					throw(new IllegalArgumentException("Unknown command:"+command));
				}
			}else{
				throw(new IllegalArgumentException("Message is empty!"));
			}
			System.out.println(DateFormat.getTimeInstance(DateFormat.FULL).format(Calendar.getInstance().getTime()));
			System.out.println("Response:"+response_string);
			if(response_string!=null && response_string.equals("")) response_string=null;
			return new Response( HTTP_OK, "text/acl", response_string );
		}catch(IllegalArgumentException ex){
			System.err.println(ex);
			ex.printStackTrace();
			return new Response( HTTP_BADREQUEST, "text/acl", "NIL" );
		}
	}

	abstract protected void commandStart(String msg);

	abstract protected String commandPlay(String msg);

	abstract protected void commandStop(String msg);

	private String getCommand(String msg){
		String cmd=null;
		try{
			Matcher m=Pattern.compile("\\(([^\\s]*)\\s").matcher(msg);
			if(m.lookingAt()){
				cmd=m.group(1);
			}
			cmd.toUpperCase();
		}catch(Exception ex){
			System.err.println("Pattern to extract command did not match!");
			ex.printStackTrace();
		}
		return cmd;
	}

	public void waitForExit(){
		try {
			server_thread.join(); // wait for server thread to exit
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}