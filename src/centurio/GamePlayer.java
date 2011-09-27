package centurio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.parctechnologies.eclipse.EclipseException;

import ASP.*;
import parser.STCompoundTermTranslator;
import parser.STListStatesTranslator;
import parser.STTranslator;

/**
 * Centurio a General Game Player
 *
 * Copyright (C) 2008 Felix Maximilian M�ller, Marius Schneider and Martin Wegner
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
 * This class is responsible for starting a game, sending messages to the gamemaster and
 * receiving them. The class also controls the end of a game and the behavior of the slaves.
 *
 * @author Felix Maximilian M�ller
 * @author Marius Schneider
 * @author Martin Wegner
 * @author University Dresden
 * @version 1.0
 */
public class GamePlayer extends AbstractGamePlayer {

	private static String playerRole;
	private String matchID;
	private static int roleIndex; // the place the Centurios move stands in the list of moves of all Players
	private Short startClock, playClock;
	private boolean firstMove;
	private STCompoundTermTranslator moveTranslator = new STCompoundTermTranslator();
	private Collection translatedMove = new ArrayList(); // contains the translated moves of the play/stop message
	private GameLogic gl;
	private UCT slaveBag[];
	//gameproperties
	private static boolean soloGame;
	private boolean parallelGame;
	private int numberOfPlayers;
	private static HashMap<String, Object> configMap = new HashMap<String, Object>();
	private static GamePlayer gp;
	private int moveCounter;
	private int communicationOverhead;
	private int ASPOverhead = 0;
	private ASPOracle oracle = null;
	private static Object lastSubmittedMove;
	private int lastVisits;
	private int lastRandomGames;
	
	private File qtree = new File("./qtree.pl");
	private long modified;
	/**
	 * Constructor
	 * @param port
	 * @throws IOException
	 */
	public GamePlayer(int port) throws IOException {
		super(port);
	}

	/**
	 * saves the gameplayer instance
	 * @param object
	 */
	static public void saveGamePlayerObject(GamePlayer object) {
		gp = object;
	}

	/**
	 * wakes up the game player
	 */
	static public void wakeUp() {

		synchronized(gp) {
			gp.notify();
		}

	}

	/**
	 * set the configmap, which contains all configurable constants
	 * @param configMap
	 */
	static public void setConfigMap(HashMap<String, Object> configMap) {
		GamePlayer.configMap = configMap;
	}

	/**
	 * gets a constant from the configmap
	 * @param configValue
	 * @return a constant from the configmap
	 */
	static public Float getConfigMap(String configValue) {
		return (Float) GamePlayer.configMap.get(configValue);
	}
	
	static public String getConfigMapString(String configValue) {
		System.out.println("get: " + configValue + "value: " + (String) GamePlayer.configMap.get(configValue));
		return (String) GamePlayer.configMap.get(configValue);
	}

	/**
	 * returns the role of centurio in the actual game
	 * @return the role of centurio in the actual game
	 */
	final static String getPlayerRole() {
		return playerRole;
	}

	/**
	 * returns the role index of centurio in the actual game
	 * @return the role index of centurio in the actual game
	 */
	final static int getRoleIndex() {
		return roleIndex;
	}

	/**
	 * returns the boolean value true if the game is a solo game
	 * @return the boolean value true if the game is a solo game
	 */
	final static boolean getSoloGame() {
		return soloGame;
	}

	/**
	 * returns the last submitted move
	 * @return the last submitted move
	 */
	final static Object getLastSubmittedMove() {
		return lastSubmittedMove;
	}

	/**
	 * set the boolean to identify if the game is a solo game
	 * @param soloGame
	 */
	public void setSoloGame(boolean soloGame) {
		GamePlayer.soloGame = soloGame;
	}

	/**
	 * returns the number of players of the actual game
	 * @return the number of players of the actual game
	 */
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	/**
	 * sets the number of player of the actual game
	 * @param numberOfPlayers
	 */
	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}
	
	
	/**
	 * prints the properties of the current game
	 *
	 */
	private void printGameProporties() {
		System.out.print("Match " + matchID + " is a ");
		if(soloGame)
			System.out.println("single player game.");
		else {
			if(parallelGame)
				System.out.println("game with parallel moves.");
			else
				System.out.println("game with sequential moves.");
		}
		System.out.println("Number of Players: " + numberOfPlayers);
	}

	/**
	 * this method is called when a new match begins
	 */
	protected void commandStart(String msg){
		// msg="(START <MATCH ID> <ROLE> <GAME DESCRIPTION> <STARTCLOCK> <PLAYCLOCK>)
		// e.g. msg="(START tictactoe1 white ((role white) (role black) ...) 1800 120)" means:
		//       - the current match is called "match0815"
		//       - your role is "white",
		//       - after at most 1800 seconds, you have to return from the commandStart method
		//       - for each move you have 120 seconds
		
		// Done:
		//    - use the time to "contemplate" about the game description
		//      and return on time (before STARTCLOCK is over!)

		Long startTime = System.currentTimeMillis();
		communicationOverhead = ((Float) configMap.get("communicationOverhead")).intValue();
		moveCounter = 0;

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY); // GamePlayer-thread gets higher priority as slave threads

		Pattern regex = Pattern.compile("^\\(START\\s+(\\S+)\\s+(\\S+)\\s+\\((.+)\\)\\s+(\\d+)\\s+(\\d+)\\)$", Pattern.DOTALL);
		Matcher matcher = regex.matcher(msg);

		if(matcher.matches()) {

			matchID = matcher.group(1);
			playerRole = matcher.group(2).toLowerCase();
			String gameDescription = matcher.group(3);
			startClock = Short.parseShort(matcher.group(4));
			playClock = Short.parseShort(matcher.group(5));

			try {

				FileWriter fw = new FileWriter("./kif/" + matchID + ".kif");
				fw.write(gameDescription);
				fw.close();

			}

			catch(IOException e) {
				System.err.println("(GamePlayer - commandStart) error message: write gameDescription - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());				
			}

			System.out.println("Centurio's role is: " + playerRole);

			firstMove = true; // avoids putting moves of the first play message into translatedMove

			try { // parses the game description

				STListStatesTranslator translator = new STListStatesTranslator();
				translator.translate(new StringReader(gameDescription), new PrintStream(new File("./pl/" + matchID + ".pl")));

			}

			catch(IOException e) {
				System.err.println("(GamePlayer - commandStart) error message: gameDescription translator - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
			}

			catch(STListStatesTranslator.ParseException e) {
				System.err.println("(GamePlayer - commandStart) error message: gameDescription translator - STListStatesTranslator.ParseException\n\n" + e.getMessage() + "\n\n" + e.toString());
			} 

			// delete all slaves and eclipse prolog instances

			if(slaveBag != null) {

				for(UCT slave : slaveBag)
					slave.interrupt();

				boolean jump;
				while (true) {
					jump = true;
					for(final UCT slave : slaveBag) {						
						if (slave.isAlive()) {
							System.out.println("This slave thread is alive: " + slave.getId());
							jump = false;
						}
					}
					try {
						Thread.sleep(500);
					} catch (final InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					if (jump) {
						break;
					}
				}

				//clean up gameplayer
				slaveBag = null;

			}

			if(gl != null) {

				gl.destroyEclipse();
				gl = null;

			}

			// how many processors are available to VM
			int NumberOfProcessors = 1;
			if (configMap.containsKey("cores")) {
				NumberOfProcessors = ((Float)configMap.get("cores")).intValue();
			} else {
				NumberOfProcessors = Runtime.getRuntime().availableProcessors();
			}

			gl = new GameLogic(matchID);  //connection to the (prolog) engine for the game logic
			// find gameproperties			
			gl.createBias(); // create RRL bias
			
			copyfile("./qtree.false.pl","./qtree.pl");
			qtree = new File("./qtree.pl");
			
			
			
			
			
			
			
			
			
			numberOfPlayers = gl.getNumberOfPlayers();
			soloGame = numberOfPlayers == 1;
			
			if (soloGame)
			{
				//starts ASP Oracle
				NumberOfProcessors--; // one Processor for ASP
				System.out.println("Start ASP Engine!");
				oracle = new ASPOracle(gameDescription, matchID);
				oracle.setPriority(Thread.MAX_PRIORITY);
				oracle.start();
				ASPOverhead = 500;
			}
			
			parallelGame = gl.isParallel();
			UCT.createRoot(gl);

			if(soloGame) {
				slaveBag = new URSolo[NumberOfProcessors+1];
				for(int i=0;i<NumberOfProcessors;i++){
					slaveBag[i] = new URSolo(matchID);
					slaveBag[i].start();
				}
			}else {
				if(parallelGame){
					slaveBag = new URPar[NumberOfProcessors];
					for(int i=0;i<NumberOfProcessors;i++){
						slaveBag[i] = new URPar(matchID,i);
						slaveBag[i].start();
					}
				}else
				{
					slaveBag = new URSeq[NumberOfProcessors];
					for(int i=0;i<NumberOfProcessors;i++){
						slaveBag[i] = new URSeq(matchID,i);
						slaveBag[i].start();
					}
				}
				
			}
		
			// find index of Centurios role
			roleIndex = gl.findIndex(playerRole);
			
			printGameProporties();

			if (!URPar.isInPanicMode() && !URSolo.getStaticSoloGameReady() && !ASPOracle.getFoundAnswer()) {
				synchronized (gp) { // lets the slaves working until the start clock is over minus 2 second overhead for communication

	            	try {
	            		gp.wait((startClock - communicationOverhead) * 1000 - ASPOverhead - (System.currentTimeMillis() - startTime));
	            	}

	            	catch(InterruptedException e) {
	            		System.err.println("(GamePlayer - commandStart) error message: Sleeping - InterruptedException\n\n" + e.getMessage() + "\n\n" + e.toString());
	            	}

	            }
			}
			
            if (soloGame)
            {
            	if (!ASPOracle.getFoundAnswer()) {
            		oracle.destroyASP();
            	}
            	System.out.println("ASP has found Answer: " + ASPOracle.getFoundAnswer());
				slaveBag[NumberOfProcessors] = new URSolo(matchID);
				slaveBag[NumberOfProcessors].start();
            	
            }
            
		}
		else
			System.err.println("Could not interprete START message from server.");

	}

	
	
	/*
	 * Copy file
	 */
	private static void copyfile(String srFile, String dtFile){
	    try{
	      File f1 = new File(srFile);
	      File f2 = new File(dtFile);
	      InputStream in = new FileInputStream(f1);
	      
	      //For Append the file.
//	      OutputStream out = new FileOutputStream(f2,true);

	      //For Overwrite the file.
	      OutputStream out = new FileOutputStream(f2);

	      byte[] buf = new byte[1024];
	      int len;
	      while ((len = in.read(buf)) > 0){
	        out.write(buf, 0, len);
	      }
	      in.close();
	      out.close();
	      System.out.println("File copied.");
	    }
	    catch(FileNotFoundException ex){
	      System.out.println(ex.getMessage() + " in the specified directory.");
	      System.exit(0);
	    }
	    catch(IOException e){
	      System.out.println(e.getMessage());      
	    }
	  }
	
	
	
	
	/**
	 * this method is called once for each move
	 * @return the move of this player
	 */
	protected String commandPlay(String msg){
		// msg="(PLAY <MATCH ID> <JOINT MOVE>)
		// <JOINT MOVE> will be NIL for the first PLAY message and the list of the moves of all the players in the previous state
		// e.g. msg="(PLAY tictactoe1 NIL)" for the first PLAY message
		//   or msg="(PLAY tictactoe1 ((MARK 1 2) NOOP))" if white marked cell (1,2) and black did a "noop".
		
		// Done:
		//    - calculate the new state from the old one and the <JOINT MOVE>
		//    - use the time to find the best of your possible moves in the current state
		//    - return your move (instead of "NIL") on time (before PLAYCLOCK is over!)

		
		if (qtree.lastModified() > modified){
			System.out.println("Change in qtree detected, updating.. ");
			modified = qtree.lastModified();
			
		
			gl.updateQtree();
			
			for(UCT slave : slaveBag){
				slave.updateQtree();
			}
			
			UCT.RRLCounter = 0; // will trigger write + update 
			
			File f = new File("./state");
			f.delete();
			
		}

		
		
		
		Long startTime = System.currentTimeMillis();
		moveCounter++;

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		Pattern regex = Pattern.compile("^\\(PLAY\\s+(\\S+)\\s+\\(?(.+)\\)?\\)$", Pattern.DOTALL);
		Matcher matcher = regex.matcher(msg);

		if(matcher.matches()) {

			if(matchID.equals(matcher.group(1))) {

				if(!firstMove) { // it is not the first play message and the moves of the play message will be translated and stored into translatedMove
					
					String moves = matcher.group(2);

					try {

						FileWriter fw = new FileWriter("./visits.log", true);
						int diff;
						for (UCT slave : slaveBag) {
							fw.write("moveCounter: " + moveCounter + "\n");
							diff = slave.getVisitCounter() - lastVisits;
							lastVisits = slave.getVisitCounter();
							fw.write("visits: " + diff + "\n");
							diff = slave.getRandomGameCounter() - lastRandomGames;
							lastRandomGames = slave.getRandomGameCounter();
							fw.write("randomGames: " + diff + "\n");
						}
						fw.close();

					} catch (final IOException e) {
						System.err.println("(GamePlayer - commandPlay) error message: write visits log - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());				
					}
					
					try {
						translatedMove = moveTranslator.translate(new StringReader(moves), false);
					}
					catch(IOException e) {
						System.err.println("(GamePlayer - commandPlay) error message: move translator - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
					}
					catch(STCompoundTermTranslator.ParseException e) {
						System.err.println("(GamePlayer - commandPlay) error message: move translator - STCompoundTermTranslator.ParseException\n\n" + e.getMessage() + "\n\n" + e.toString());
					}

					//if we play a static solo game, create the children of the root
					//if(soloGame && URSolo.getStaticSoloGame())
					if(!URPar.isInPanicMode()) {
						try {
							UCT.root.createChildren(gl);
						} catch (final PanicModeException e) {
						}
					}
					UCT.changeRoot(gl, translatedMove);

				}

				else {

					firstMove = false; // detects the first play message

					try {

						FileWriter fw = new FileWriter("./visits.log", true);
						fw.write(matchID + "\n");
						if (configMap.containsKey("cores")) {
							fw.write("Cores: " + GamePlayer.getConfigMap("cores").intValue() + "\n");
						} else {
							fw.write("Cores: " + Runtime.getRuntime().availableProcessors() + "\n");
						}
						fw.write("root visits before first move response: " + UCT.root.getVisits() + "\n");
						for (UCT slave : slaveBag) {
							fw.write("moveCounter: " + moveCounter + "\n");
							lastVisits = slave.getVisitCounter();
							fw.write("visits: " + lastVisits + "\n");
							lastRandomGames = slave.getRandomGameCounter();
							fw.write("randomGames: " + lastRandomGames + "\n");							
						}
						fw.close();

					} catch (final IOException e) {
						System.err.println("(GamePlayer - commandPlay) error message: write visits log - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());				
					}

				}

				Object nextMove = "NIL";

				if(!URPar.isInPanicMode() && !URSolo.getStaticSoloGameReady() && !ASPOracle.getFoundAnswer()) {

					synchronized (gp) { // lets the slaves working until the play clock is over minus 2 second overhead for communication and 0,1 seconds for getBestMove

			           	try {
		            		gp.wait((playClock - communicationOverhead) * 1000 - 100 - (System.currentTimeMillis() - startTime));
		            	}

		            	catch(InterruptedException e) {
		            		System.err.println("(GamePlayer - commandPlay) error message: Sleeping - InterruptedException\n\n" + e.getMessage() + "\n\n" + e.toString());
		            	}

		            }

				} else {
					if(!URPar.isInPanicMode()) {
						try {
							UCT.root.createChildren(gl);
						} catch (final PanicModeException e) {
						}
					}
				}

				if (ASPOracle.getFoundAnswer())
				{
					String doesOracle = oracle.askOracle();
					if (doesOracle != null)
					{
						System.out.println("ASPmove : " +doesOracle);
						lastSubmittedMove = doesOracle;
						return doesOracle;
					}
					else
					{
						URSolo.destroyStaticGame();
						ASPOracle.resetFoundAnswer();
						System.err.println("Something went wrong with the ASP Answer! (GamePlayer)");
					}
								
				}
				
				// call getBestMove of UR Class that fits to gametype 
				if(soloGame) {
					nextMove = URSolo.getBestMove(roleIndex);
				}
				else {

					if(parallelGame)
						nextMove = URPar.getBestMove(roleIndex);
					
					else
						nextMove = URSeq.getBestMove(roleIndex);

				}
				
				
				lastSubmittedMove = STTranslator.printKIF(nextMove);
				return STTranslator.printKIF(nextMove);

			}

			else {

				System.err.println("Wrong match ID in PLAY message.");
				lastSubmittedMove = "NIL";
				return "NIL";

			}

		}

		else {

			System.err.println("Could not interprete PLAY message from server.");
			lastSubmittedMove = "NIL";
			return "NIL";

		}

	}

	/**
	 * this method is called if the match is over
	 */
	protected void commandStop(String msg){
		// msg="(STOP <MATCH ID> <JOINT MOVE>)
		
		// Done:
		//    - clean up the GamePlayer for the next match
		//    - be happy if you have won, think about what went wrong if you have lost ;-)

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		Pattern regex = Pattern.compile("^\\(STOP\\s+(\\S+)\\s+\\(?(.+)\\)?\\)$", Pattern.DOTALL);
		Matcher matcher = regex.matcher(msg);

		if(matcher.matches()) {

			if(matchID.equals(matcher.group(1))) {

				String moves = matcher.group(2);

				try { // the moves of the stop message will be translated and stored into translatedMove
					translatedMove = moveTranslator.translate(new StringReader(moves), false);
				}

				catch(IOException e) {
					System.err.println("(GamePlayer - commandStop) error message: move translator - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
				}

				catch(STCompoundTermTranslator.ParseException e) {
					System.err.println("(GamePlayer - commandStop) error message: move translator - STCompoundTermTranslator.ParseException\n\n" + e.getMessage() + "\n\n" + e.toString());
				}
				
				//if(soloGame && URSolo.getStaticSoloGame())
				if(!URPar.isInPanicMode()) {
					try {
						UCT.root.createChildren(gl);
					} catch (final PanicModeException e) {
					}
				}

				UCT.changeRoot(gl, translatedMove);
				double[] scores = UCT.root.getScore();
				System.out.println("root: "+STTranslator.printKIF(UCT.root.getState()));
				for(int i = 0; i < scores.length; i++) {
					System.out.println("Player " + STTranslator.printKIF(GameLogic.getPlayerList().get(i)) + " has " +  scores[i]*100 + " points."); // print the scores of all players
				}
				
				System.out.println("Centurio's role was: " + playerRole);

				try {

					FileWriter fw = new FileWriter("./visits.log", true);
					int diff;
					for (UCT slave : slaveBag) {
						fw.write("moveCounter: " + moveCounter + "\n");
						diff = slave.getVisitCounter() - lastVisits;
						fw.write("visits: " + diff + "\n");
						diff = slave.getRandomGameCounter() - lastRandomGames;
						fw.write("randomGames: " + diff + "\n");
					}
					int x = 1;
					int allVisits = 0;
					int allRandomGames = 0;

					for(UCT slave : slaveBag) {

						fw.write("Slave " + x + " visits: " + slave.getVisitCounter() + "\n");
						fw.write("Slave " + x++ + " randomGames: " + slave.getRandomGameCounter() + "\n");

					}

					for(UCT slave : slaveBag) {

						allVisits = allVisits + slave.getVisitCounter();
						allRandomGames = allRandomGames + slave.getRandomGameCounter();

					}

					fw.write("All slave visits: " + allVisits + "\n");
					fw.write("All slave randomGames: " + allRandomGames + "\n");
					fw.write("Moves: " + moveCounter + "\n");
					fw.write("Visits/Move: " + allVisits / moveCounter + "\n");
					fw.write("randomGames/Move: " + allRandomGames / moveCounter + "\n");
					fw.write("\n");
					fw.close();

				}

				catch(IOException e) {
					System.err.println("(GamePlayer - commandStop) error message: write visits log - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());				
				}

				// delete all slaves and eclipse prolog instances

				for(UCT slave : slaveBag)
					slave.interrupt();

				boolean jump;
				while (true) {
					jump = true;
					for(final UCT slave : slaveBag) {						
						if (slave.isAlive()) {
							System.out.println("This slave thread is alive: " + slave.getId());
							jump = false;
						}
					}
					try {
						Thread.sleep(500);
					} catch (final InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					if (jump) {
						break;
					}
				}

				//clean up gameplayer
				slaveBag = null;
				gl.destroyEclipse();
				gl = null;
				System.gc();

			}

			else
				System.err.println("Wrong match ID in STOP message.");

		}

		else
			System.err.println("Could not interprete STOP message from server.");

	}

}
