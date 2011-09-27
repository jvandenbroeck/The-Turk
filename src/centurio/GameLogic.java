package centurio;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import parser.STTranslator;

import com.parctechnologies.eclipse.Atom;
import com.parctechnologies.eclipse.CompoundTerm;
import com.parctechnologies.eclipse.CompoundTermImpl;
import com.parctechnologies.eclipse.EclipseEngineOptions;
import com.parctechnologies.eclipse.EclipseException;
import com.parctechnologies.eclipse.OutOfProcessEclipse;

/**
 * Centurio a General Game Player
 *
 * Copyright (C) 2008 Felix Maximilian Mller, Marius Schneider and Martin Wegner
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
 * This class defines the methods for the game logic, which will be used from the
 * artificial intelligence.
 *
 * @author Felix Maximilian Mller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */
public class GameLogic {

	private OutOfProcessEclipse Eclipse = null;
	private int numberOfPlayers = 0; // number of players in the current game
	static private LinkedList<Atom> PlayerList; // list of all players
	static private boolean isParallelGame;
	static private String matchID;
	// static private String matchIDprev = "none";
	// static private String matchIDrrl = "none";
	
    static private boolean debug = false;
    
   
	/**
	 * Constructor <br>
	 * initialize a prolog engine
	 * @param matchID
	 */
	public GameLogic(String matchID)
	{	
		
		GameLogic.matchID = matchID;
		
		 //initialize ECLiPSe engine and load game description
		 try {
			EclipseEngineOptions options = new EclipseEngineOptions(new File(""));
			options.setLocalSize(256);
			Eclipse = new OutOfProcessEclipse(options);
			//Setup the randomseed
			int random;
			Random rand = new Random();
			random = rand.nextInt();
			Eclipse.rpc(new CompoundTermImpl("seed", random));
			//load the GameLogic and game description file
			Eclipse.compile(new File("./logic/GameLogic.pl"));
			Eclipse.compile(new File("./pl/" + matchID + ".pl"));
			//disable warnings in Prolog
			Eclipse.rpc("set_stream(warning_output, null)");
			getAllPlayers();
		
			//Eclipse.compile(new File("./qtree.pl"));
			
			
			
			
			updateQtree();
			
			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("(GameLogic - GameLogic) error message: create eclipse instance or set the seed for random - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
		} catch (EclipseException e) {
			e.printStackTrace();
			System.err.println("(GameLogic - GameLogic) error message: create eclipse instance or set the seed for random - EclipseException\n\n" + e.getMessage() + "\n\n" + e.toString());
		}
	}
	
	
	public void createBias(){
		
		System.out.println("Initializing..");
		
		System.out.println("RRL Init Started.. ");

		String players  = "";

		for(Atom Role:PlayerList){
			players = players + " " + Role.functor();
		}

		players = players + "";
	
		String[] commands = {"ruby", "./rrl_start.rb", GameLogic.matchID, players, GamePlayer.getConfigMapString("aceServer")};
	
	
		System.out.println("ace server: " + GamePlayer.getConfigMapString("aceServer"));
		System.out.println("players: " + players);

		execute(commands);
		
	}
	
	

	
	
	
	public void updateQtree(){
	
		try {
			Eclipse.compile(new File("./qtree.pl"));
					
		} catch (EclipseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		// String[] commands = {"cat ./ace/qtree.pl"};
		// execute(commands);
		
	}
	
	/**
	 * calculates all player of the current game
	 * updates the variable numberOfPlayers
	 * @return all player of the current game in KIF Format,<br>
	 * e.g. :"[com.parctechnologies.eclipse.Atom with [functor=dropper], com.parctechnologies.eclipse.Atom with [functor=player]]" <br><br>
	 * to get the Player in String Format cast each element to CompoundTerm and access with "Object.funktor()"
	 */
	public LinkedList<Atom> getAllPlayers()
	{
		CompoundTerm Query = null;
		try {
			//%% roles(+Roles)bestimmt eine Liste von allen Rollen.
			//gibt zur�ck: "[com.parctechnologies.eclipse.Atom with [functor=dropper], com.parctechnologies.eclipse.Atom with [functor=player]]"
			Query =	Eclipse.rpc("roles(Roles)");
		} catch (IOException e) {
			System.err.println("(GameLogic - getAllPlayers) error message: get a list of all players - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
		} catch (EclipseException e) {
			System.err.println("(GameLogic - getAllPlayers) error message: get a list of all players - EclipseException\n\n" + e.getMessage() + "\n\n" + e.toString());
		}
		PlayerList = (LinkedList<Atom>) Query.arg(1);
		numberOfPlayers=PlayerList.size();
		
		return PlayerList;
	}
	
	
	
	private void execute(String[] commands){
		System.out.println("Starting a command.. ");
		System.out.println(Arrays.toString(commands));
		System.out.println("--------------------------------------------");
		 try {  
	           // Process p = 
	            	Runtime.getRuntime().exec(commands);  
	    //        BufferedReader in = new BufferedReader(  
	      //                          new InputStreamReader(p.getInputStream()));  
	        //    String line = null;  
	          //  while ((line = in.readLine()) != null) {  
	            //    System.out.println("EX: " + line);  
	           // }  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }
	        
		System.out.println("--------------------------------------------");

	        
	}
	/**
	 * determines in which place the Centurios move stands in the list of moves of all Players
	 * @return all player of the current game in KIF Format,<br>
	 * e.g. :"[com.parctechnologies.eclipse.Atom with [functor=dropper], com.parctechnologies.eclipse.Atom with [functor=player]]" <br><br>
	 * to get the Player in String Format cast each element to CompoundTerm and access with "Object.funktor()"
	 */
	public int findIndex(String playerRole)
	{
		for(Atom Role:PlayerList){
			if(playerRole.equals(Role.functor()))
				return PlayerList.indexOf(Role);
		}
		return -1;
		
	}
	
	/**
	 * determines if the game is parallel one
	 * @return is true if the game is parallel and false if not
	 */
	public boolean isParallel()
	{
		try {
			//%% isParallelGame()
			//%% Prolog predicate checks if more than 1 Player in Initstate have more than one legal move
			Eclipse.rpc("isParallelGame");
		} catch (IOException e) {
			System.err.println("(GameLogic - isParallel) error message: check if the game is a parallel one - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
		} catch (EclipseException e) {
			isParallelGame = false;
			return isParallelGame;
		}
		isParallelGame = true;
		return isParallelGame;
	}

	/**
	 * Plays a whole random Game (sequence from
	 * actions and calculated states) till a
	 * terminal state. 
	 * LinkedList of CoumpoundTerm needs type LinkedList<CompoundTerm>
	 * @param State state where the random game begins
	 * @return [0] reached terminal state(CompoundTerm),<br> 
	 *         [1] used actions in the sequence(LinkedList of CoumpoundTerm)
	 */
	public Object[] rrlGame(Object State, double T, double gamma)
	{
		CompoundTerm Query = null;
		Object[] Result = new Object[2];
		//%% randomGame(+CurrentState,-TerminalState,[],-ActionPath)
		try {
			Query =	Eclipse.rpc(new CompoundTermImpl("rrlGame", State, null, new Atom("[]"), null, T, gamma));
		} catch (IOException e) {
			System.err.println("(GameLogic - randomGame(Object State)) error message: play a random game - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
			System.err.println("T:\n" + T);

			e.printStackTrace();
			
		} catch (EclipseException e) {
			System.err.println("(GameLogic - randomGame(Object State)) error message: play a random game - EclipseException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
			System.err.println("T:\n" + T);
			
			e.printStackTrace();
		}
		Result[0]=Query.arg(2); // terminal state
		Result[1]=Query.arg(4); // moves
		
		// System.out.println("ok playing rrl");
	
                if(debug){
                try{
		    // Create file 
		    FileWriter fstream = new FileWriter("actions.txt",true);
		        BufferedWriter out = new BufferedWriter(fstream);
		    out.write(Result[1].toString() + "\n\n");
		    //Close the output stream
		    out.close();
		    }catch (Exception e){//Catch exception if any
		      System.err.println("Error: " + e.getMessage());
		    }
                }
		
		return Result;
	}
	
	
	
	/**
	 * 
	 * Plays a random game, which will be written to the state file
	 * 
	 * Plays a whole random Game (sequence from
	 * actions and calculated states) till a
	 * terminal state. 
	 * LinkedList of CoumpoundTerm needs type LinkedList<CompoundTerm>
	 * @param State state where the random game begins
	 * @return [0] reached terminal state(CompoundTerm),<br> 
	 *         [1] used actions in the sequence(LinkedList of CoumpoundTerm)
	 */
	public Object[] rrlGameWrite(Object State, double T, double gamma)
	{
		CompoundTerm Query = null;
		Object[] Result = new Object[2];
		//%% randomGame(+CurrentState,-TerminalState,[],-ActionPath)
		try {
			Query =	Eclipse.rpc(new CompoundTermImpl("rrlGameWrite", State, null, new Atom("[]"), null, T, gamma));
		} catch (IOException e) {
			System.err.println("(GameLogic - randomGame(Object State)) error message: play a random game - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
			e.printStackTrace();
		} catch (EclipseException e) {
			System.err.println("(GameLogic - randomGame(Object State)) error message: play a random game - EclipseException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
			e.printStackTrace();
		}
		Result[0]=Query.arg(2);
		Result[1]=Query.arg(4);
		
                if(debug){
		try{
		    // Create file 
		    FileWriter fstream = new FileWriter("actions.txt",true);
		        BufferedWriter out = new BufferedWriter(fstream);
		    out.write(Result[1].toString() + "\n\n");
		    //Close the output stream
		    out.close();
		    }catch (Exception e){//Catch exception if any
		      System.err.println("Error: " + e.getMessage());
		    }
                }
		
		return Result;
	}
	
	
	
	
	/**
	 * Plays a n random Games (sequence from actions and calculated states) till a
	 * terminal state. 
	 * @param 	State state where the random game begins
	 * @param	n number of games to play
	 * @return 	Object[i][0] terminal state reached(CompoundTerm),<br> 
	 *			Object[i][1] used actions in the sequence(LinkedList of CoumpoundTerm)<br> 
	 *         	where i stands for the i'th randomgame
	 */
//	public Object randomGame(Object State, int n)
//	{
//		CompoundTerm Query = null;
//		//%% randomGameN(+CurrentState,+N,-ListOfTerminalstatesNActionpaths)
//		try {
//			Query =	Eclipse.rpc(new CompoundTermImpl("randomGameN", State, n, null));
//		} catch (IOException e) {
//			System.err.println("(GameLogic - randomGane(Object State, int n)) error message: play a random game with number" + n + " - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
//		} catch (EclipseException e) {
//			System.err.println("(GameLogic - randomGame(Object State, int n)) error message: play a random game with number" + n + " - EclipseException\n\n" + e.getMessage() + "\n\n" + e.toString());
//		}
//		
//		return Query.arg(3);
//	}
	 
	/**
	* calculates the scores of all players of 
	* a given state
	* @param State given State
	* @return scores of all players
	*/
	public double[] scoresState(Object State)
	{
		CompoundTerm Query = null;
		double[] Result = null;
		//%% winningStateScoreWithRole(+CurrentState,-Score)
		//ermittelt nur die Punkte der Spieler
		try {
			Query =	Eclipse.rpc(new CompoundTermImpl("scoresOfAllPlayers", State, null));
		} catch (IOException e) {
			System.err.println("(GameLogic - scoresState) error message: calculate the scores of all players - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
		} catch (EclipseException e) {
			System.err.println("(GameLogic - scoresState) error message: calculate the scores of all players - EclipseException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
		}
				
		Collection<CompoundTerm> ScoreList = new LinkedList<CompoundTerm>();
		ScoreList = (LinkedList<CompoundTerm>) Query.arg(2);
		Result = new double[PlayerList.size()];
		int i = 0;
		
		for (CompoundTerm RoleScorePair : ScoreList){
			
			i = findIndex(((Atom)RoleScorePair.arg(1)).functor().toString());
			Result[i] = ((Integer) RoleScorePair.arg(2)).doubleValue() / 100;			
			
		}
		return Result;
	}
	
	/**
	 * calculates all legal moves of a given state
	 * @param State given state
	 * @return all legal moves
	 */
	public LinkedList<LinkedList<Object>> getAllLegalMoves(Object State)
	{
		CompoundTerm Query = null;
		//legalMovesOfAllPlayers(+State,-LegalMovesOfAllPlayers)
		try {
			Query =	Eclipse.rpc(new CompoundTermImpl("legalMovesOfAllPlayersCombination", State, null));
		} catch (IOException e) {
			System.err.println("(GameLogic - getAllLegalMoves) error message: get all legal moves for all players - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
		} catch (EclipseException e) {
			System.err.println("(GameLogic - getAllLegalMoves) error message: get all legal moves for all players - EclipseException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
		}
		// TODO Zu pr�fen ob das Casting so ok ist
		return (LinkedList<LinkedList<Object>>) Query.arg(2);

	}
	
	/**
	 * calculates the initial state of the game
	 * @return initial state as an Object
	 */
	public Object getInitState()
	{
		CompoundTerm Query = null;
		try {
			//%% init_State()
			Query =	Eclipse.rpc("initState(InitState)");
		} catch (IOException e) {
			System.err.println("(GameLogic - getNextState) error message: compute the next state - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
		} catch (EclipseException e) {
			System.err.println("(GameLogic - getNextState) error message: compute the next state - EclipseException\n\n" + e.getMessage() + "\n\n" + e.toString());
		}
			
		return Query.arg(1);
	}
	
	/**
	 * calculates the next state of a given state and action. <br>
	 * the representation of the state has to be unique!!!
	 * @param State given state
	 * @param Move action, which will be done from the state
	 * @return next state as a String[]
	 */
	public Object getNextState(Object State, Object Moves)
	{
		CompoundTerm Query = null;
		try {
			//%% nextState(+Current_State,+Move,-Next_State)
			Query =	Eclipse.rpc(new CompoundTermImpl("nextState", State, Moves, null));
		} catch (IOException e) {
			System.err.println("(GameLogic - getNextState) error message: compute the next state - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
			System.err.println("Error in State:\n" + STTranslator.printKIF(Moves));
		} catch (EclipseException e) {
			System.err.println("(GameLogic - getNextState) error message: compute the next state - EclipseException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
			System.err.println("Error in State:\n" + STTranslator.printKIF(Moves));
		}
			
		return Query.arg(3);
	}
	
	/**
	 * calculates if a state is terminal or not
	 * @param State given state
	 * @return terminal yes or no
	 */
	public boolean isTerminal(Object State)
	{
		try {
			//%% booleanTerminal(Current_State,Terminal)
			Eclipse.rpc(new CompoundTermImpl("terminalState", State));
		} catch (IOException e) {
			System.err.println("(GameLogic - isTerminal) error message: check if the current state is terminal - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
		} catch (EclipseException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * calculates which player (id) makes 
	 * the next move in a given state
	 * @param State given state
	 * @return player id or -1 in parallel games
	 */
	public int activePlayer(Object State)
	{
		if (State == null) System.out.println("State: "+State);
		CompoundTerm Query = null;
		try {
			//%% currentPlayer(State,CurrentPlayer)
			if(isParallelGame)
				return -1;
			else{
				Query =	Eclipse.rpc(new CompoundTermImpl("currentPlayer", State, null));
			}
		} catch (IOException e) {
			System.err.println("(GameLogic - activePlayer) error message: get the active player - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
		} catch (EclipseException e) {
			System.err.println("(GameLogic - activePlayer) error message: get the active player - EclipseException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
		}
		return PlayerList.indexOf(Query.arg(2));
	}
	/**
	 * calculates the next state of a given state and action. <br>
	 * the representation of the state has to be unique!!!
	 * @param State given state
	 * @param Move action, which will be done from the state
	 * @return next state as a String[]
	 */
	public Object[] mergedCreateChildrenRequest(Object State, Object Move)
	{
		Object[] result = new Object[3];
		CompoundTerm Query = null;
		try {
			//%% nextState(+Current_State,+Move,-Next_State)
			if(isParallelGame)
				Query =	Eclipse.rpc(new CompoundTermImpl("mergedCreateChildrenRequestParallel", State, Move, null, null));
			else
				Query =	Eclipse.rpc(new CompoundTermImpl("mergedCreateChildrenRequest", State, Move, null, null, null));
		} catch (IOException e) {
			System.err.println("(GameLogic - getNextState) error message: compute the next state - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
			System.err.println("Error in State:\n" + STTranslator.printKIF(Move));
		} catch (EclipseException e) {
			System.err.println("(GameLogic - getNextState) error message: compute the next state - EclipseException\n\n" + e.getMessage() + "\n\n" + e.toString());
			System.err.println("Error in State:\n" + STTranslator.printKIF(State));
			System.err.println("Error in State:\n" + STTranslator.printKIF(Move));
		}
			result[0] = Query.arg(3);
			result[1] = ((CompoundTerm)(Query.arg(4))).functor();
			if(isParallelGame)
				result[2] = -1;
			else
				result[2] = PlayerList.indexOf(Query.arg(5));
		return result;
	}

	/**
	 * returns the number of playes in the actual game
	 * @return the number of playes in the actual game
	 */
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	/**
	 * sets the number of player in the actual game
	 * @param numberOfPlayers
	 */
	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	/**
	 * returns a list of all participating player
	 * @return a list of all participating player
	 */
	static public LinkedList<Atom> getPlayerList() {
		return PlayerList;
	}
	
	/**
	 * destroys the prolog eclipse engine
	 */
	public void destroyEclipse() {
		try {
			Eclipse.destroy();
		} catch (IOException e) {
			System.err.println("(GameLogic - destroyEclipse) error message: destroy GameLogic eclipse - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
		}
	}

	/**
	 * returns the prolog engine of this instance
	 * @return the prolog engine of this instance
	 */
	public OutOfProcessEclipse getEclipse() {
		return Eclipse;
	}

	final boolean isInPanicMode(final Object State)
	{
		try {
			Eclipse.rpc(new CompoundTermImpl("isInPanicMode", State));
		} catch (IOException e) {
			System.err.println("(GameLogic - isInPanicMode) error message: check if Centurio is in panic mode - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
		} catch (EclipseException e) {
			return false;
		}
		return true;
	}

	final Object getRandomMove(final Object State)
	{
		CompoundTerm Query = null;
		try {
			Query = Eclipse.rpc(new CompoundTermImpl("legalMoves", State, new Atom(GamePlayer.getPlayerRole()), null));
		} catch (IOException e) {
			System.err.println("(GameLogic - getRandomMove) error message: get a random move - IOException\n\n" + e.getMessage() + "\n\n" + e.toString());
		} catch (EclipseException e) {
			System.err.println("(GameLogic - getRandomMove) error message: get a random move - EclipseException\n\n" + e.getMessage() + "\n\n" + e.toString());
		}
		LinkedList<Object> linkedList = (LinkedList<Object>) Query.arg(3);
		Random random = new Random();
		return linkedList.get(random.nextInt(linkedList.size()));
	}

	public void runRRL() {
		
	// 	if(!GameLogic.matchID.equals(GameLogic.matchIDrrl)){
		System.out.println("RRL Processing Started.. ");

		String players = "";

		for(Atom Role:PlayerList){
		players = players + " " + Role.functor();
		}

		players = players + "";

		System.out.println("players: " + players);
		String[] commands = {"ruby", "./rrl_process.rb", GameLogic.matchID, players, UCT.aceServer, UCT.thisServer, "" + UCT.minHorizon, "" + UCT.gammaQ};
	//	System.out.println("players: " + players);

		execute(commands);
		
		//GameLogic.matchIDrrl = GameLogic.matchID; 
	//	}
		
	}
	
}
