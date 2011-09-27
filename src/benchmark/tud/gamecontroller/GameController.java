package benchmark.tud.gamecontroller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import benchmark.tud.gamecontroller.game.GameInterface;
import benchmark.tud.gamecontroller.game.Match;
import benchmark.tud.gamecontroller.game.Move;
import benchmark.tud.gamecontroller.game.StateInterface;
import benchmark.tud.gamecontroller.game.TermFactoryInterface;
import benchmark.tud.gamecontroller.game.TermInterface;
import benchmark.tud.gamecontroller.game.javaprover.Game;
import benchmark.tud.gamecontroller.game.javaprover.State;
import benchmark.tud.gamecontroller.game.javaprover.Term;
import benchmark.tud.gamecontroller.game.javaprover.TermFactory;
import benchmark.tud.gamecontroller.logging.PlainTextLogFormatter;
import benchmark.tud.gamecontroller.logging.UnbufferedStreamHandler;
import benchmark.tud.gamecontroller.players.LegalPlayerInfo;
import benchmark.tud.gamecontroller.players.Player;
import benchmark.tud.gamecontroller.players.PlayerFactory;
import benchmark.tud.gamecontroller.players.PlayerInfo;
import benchmark.tud.gamecontroller.players.RandomPlayerInfo;
import benchmark.tud.gamecontroller.players.RemotePlayerInfo;
import benchmark.tud.gamecontroller.scrambling.GameScrambler;
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
 * GameController.java
 * Modified by Team Centurio
 */

public class GameController<
		T extends TermInterface,
		S extends StateInterface<T,S>
		>{
	public static final String GAMEDIR="games/";
	private Match<T,S> match;
	private GameInterface<T,S> game;
	private S currentState;
	private List<Player<T,S>> players;
	private int startclock;
	private int playclock;
	private int goalValues[]=null;
	private Logger logger;
	private Collection<GameControllerListener<T,S>> listeners;
	private TermFactoryInterface<T> termfactory;
	private static HashMap<String, Short> resultMap = new HashMap<String, Short>(); // HashMap for the benchmark results
	private static String currentGameName; // String with the current game name

	public static void clearResultMap() {
		GameController.resultMap.clear();
	}

	public static HashMap<String, Short> getResultMap() {
		return GameController.resultMap;
	}

	public static void setCurrentGameName(String gameName) {
		GameController.currentGameName = gameName;
	}

	public static String getCurrentGameName() {
		return GameController.currentGameName;
	}

	public GameController(Match<T,S> match, Collection<PlayerInfo> players, TermFactoryInterface<T> termfactory) {
		this(match,players,termfactory,null);
	}
	public GameController(Match<T,S> match, Collection<PlayerInfo> players, TermFactoryInterface<T> termfactory, Logger logger) {
		this.match=match;
		this.game=match.getGame();
		this.startclock=match.getStartclock();
		this.playclock=match.getPlayclock();
		if(logger==null){
			this.logger=Logger.getLogger("tud.gamecontroller");
		}else{
			this.logger=logger;
		}
		this.termfactory=termfactory;
		initializePlayers(players);
		listeners=new LinkedList<GameControllerListener<T,S>>();
	}

	public void addListener(GameControllerListener<T,S> l){
		listeners.add(l);
	}

	public void removeListener(GameControllerListener<T,S> l){
		listeners.remove(l);
	}
	
	private void fireGameStart(S currentState){
		for(GameControllerListener<T,S> l:listeners){
			l.gameStarted(match, players, currentState);
		}
	}
	private void fireGameStep(List<Move<T>> moves, S currentState){
		for(GameControllerListener<T,S> l:listeners){
			l.gameStep(moves, currentState);
		}
	}
	private void fireGameStop(S currentState, int goalValues[]){
		for(GameControllerListener<T,S> l:listeners){
			l.gameStopped(currentState, goalValues);
		}
	}
	
	private void initializePlayers(Collection<PlayerInfo> definedplayers) {
		players=new ArrayList<Player<T,S>>();
		goalValues=new int[game.getNumberOfRoles()];
		for(int i=0; i<game.getNumberOfRoles(); i++){
			players.add(null);
			goalValues[i]=-1;
		}
		for(PlayerInfo p:definedplayers){
			if(p!=null){
				if(p.getRoleindex()<players.size()){
					if(players.get(p.getRoleindex())==null){
						players.set(p.getRoleindex(),PlayerFactory. <T,S> createPlayer(p, termfactory));
					}else{
						throw new IllegalArgumentException("duplicate roleindex in given player list");
					}
				}else{
					throw new IllegalArgumentException("roleindex must be between 0 and n-1 for an n-player game");
				}
			}
		}
		for(int i=0; i<players.size(); i++){
			if(players.get(i)==null){
				players.set(i,PlayerFactory. <T,S> createPlayer(new RandomPlayerInfo(i), termfactory));
			}
		}
	}

	public void runGame(Byte ourRole) {
		int step=1;
		currentState=game.getInitialState();
		List<Move<T>> priormoves=null;
		//logger.info("starting game with startclock="+startclock+", playclock="+playclock);
		//logger.info("step:"+step);
		//logger.info("current state:"+currentState);
		gameStart();
		fireGameStart(currentState);
		while(!currentState.isTerminal()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			final List<Move<T>> moves = gamePlay(step, priormoves);
			currentState=currentState.getSuccessor(moves);
			fireGameStep(moves, currentState);
			priormoves=moves;
			step++;
			//logger.info("step:"+step);
			//logger.info("current state:"+currentState);
		}
		gameStop(priormoves);
		String goalmsg="Game over! results: ";
		for(int i=0;i<goalValues.length;i++){
			goalValues[i]=currentState.getGoalValue(game.getRole(i+1));
			goalmsg+=goalValues[i]+" ";
		}
		fireGameStop(currentState, goalValues);
		//logger.info(goalmsg);
		Integer points = currentState.getGoalValue(game.getRole(ourRole));
		Byte rank = 1;
		Boolean draw = true;
		for(int i=0;i<goalValues.length;i++){
			if(goalValues[i] > points)
				rank++;
			if(goalValues[i] != points || goalValues.length == 1)
				draw = false;
		}
		if(draw)
			rank = 0;
		if(points == 0)
			rank = -1;
		Short rankCounter = resultMap.get(rank.toString());
		if(rankCounter == null)
			rankCounter = 0;
		resultMap.put(rank.toString(), ++rankCounter);
		Short result = resultMap.get(GameController.getCurrentGameName() + points.toString());
		if(result == null)
			result = 0;
		resultMap.put(GameController.getCurrentGameName() + points.toString(), ++result);
		Short matchCounter = resultMap.get(GameController.getCurrentGameName() + "matches");
		if(matchCounter == null)
			matchCounter = 0;
		resultMap.put(GameController.getCurrentGameName() + "matches", ++matchCounter);
	}

	private void runThreads(Collection<? extends AbstractPlayerThread<T,S>> threads, Level loglevel){
		for(AbstractPlayerThread<T,S> t:threads){
			t.start();
		}
		for(AbstractPlayerThread<T,S> t:threads){
			if(!t.waitUntilDeadline()){
				Short timeOuts = resultMap.get(GameController.getCurrentGameName() + t.getPlayer() + "timeOuts");
				if(timeOuts == null)
					timeOuts = 0;
				resultMap.put(GameController.getCurrentGameName() + t.getPlayer() + "timeOuts", ++timeOuts);
				timeOuts = resultMap.get(t.getPlayer() + "timeOuts");
				if(timeOuts == null)
					timeOuts = 0;
				resultMap.put(t.getPlayer() + "timeOuts", ++timeOuts);
				System.err.println("player "+t.getPlayer()+" timed out!");
			}
		}
	}

	private void gameStart() {
		Collection<PlayerThreadStart<T,S>> playerthreads=new LinkedList<PlayerThreadStart<T,S>>();
		for(int i=0;i<players.size();i++){
			//logger.info("player "+i+": "+players.get(i));
			playerthreads.add(new PlayerThreadStart<T,S>(i+1, players.get(i), match, startclock*1000+1000));
		}
		//logger.info("Sending start messages ...");
		runThreads(playerthreads, Level.WARNING);
	}

	private List<Move<T>> gamePlay(int step, List<Move<T>> priormoves) {
		final List<Move<T>> moves=new ArrayList<Move<T>>();
		Collection<PlayerThreadPlay<T,S>> playerthreads=new LinkedList<PlayerThreadPlay<T,S>>();
		for(int i=0;i<players.size();i++){
			moves.add(null);
			playerthreads.add(new PlayerThreadPlay<T,S>(i+1, players.get(i), match, priormoves, playclock*1000+1000));
		}
		//logger.info("Sending play messages ...");
		runThreads(playerthreads, Level.SEVERE);
		for(PlayerThreadPlay<T,S> pt:playerthreads){
			int i=pt.getRoleIndex()-1;
			Move<T> move=pt.getMove();
			if(move==null || !currentState.isLegal(game.getRole(i+1), move)){
				Short illegalMoves = resultMap.get(GameController.getCurrentGameName() + players.get(i) + "illegalMoves");
				if(illegalMoves == null)
					illegalMoves = 0;
				resultMap.put(GameController.getCurrentGameName() + players.get(i) + "illegalMoves", ++illegalMoves);
				illegalMoves = resultMap.get(players.get(i) + "illegalMoves");
				if(illegalMoves == null)
					illegalMoves = 0;
				resultMap.put(players.get(i) + "illegalMoves", ++illegalMoves);
				System.err.println("Illegal move \""+move+"\" from "+players.get(i)+ " in step "+step);
				moves.set(i,currentState.getLegalMove(game.getRole(i+1)));
			}else{
				moves.set(i,move);
			}
		}
		String movesmsg="moves: ";
		for(Move<T> m:moves){
			movesmsg+=m+" ";
		}
		//logger.info(movesmsg);
		return moves;
	}

	private void gameStop(List<Move<T>> priormoves) {
		Collection<PlayerThreadStop<T,S>> playerthreads=new LinkedList<PlayerThreadStop<T,S>>();
		for(int i=0;i<players.size();i++){
			playerthreads.add(new PlayerThreadStop<T,S>(i+1, players.get(i), match, priormoves, playclock*1000+1000));
		}
		//logger.info("Sending stop messages ...");
		runThreads(playerthreads, Level.WARNING);
	}
	
	public int[] getGoalValues() {
		return goalValues;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	private static int parsePlayerArguments(int index, String argv[], Game game, List<PlayerInfo> playerinfos){
		int roleindex=0;
		String host="";
		int port=0;
		PlayerInfo player=null;
		if(index<argv.length){
			if(argv[index].equals("-remote")){
				if(argv.length>index+3){
					roleindex=getIntArg(argv[index+1], "roleindex");
					if(roleindex<1 || roleindex>game.getNumberOfRoles()){
						System.err.println("roleindex out of bounds");
						printUsage();
						System.exit(-1);
					}
					host=argv[index+2];
					port=getIntArg(argv[index+3], "port");
					player=new RemotePlayerInfo(roleindex-1, host, port);
					index+=4;
				}else{
					missingArgumentsExit(argv[index]);
				}
			}else if(argv[index].equals("-legal") || argv[index].equals("-random")){
				if(argv.length>index+1){
					roleindex=getIntArg(argv[index+1], "roleindex");
					if(roleindex<1 || roleindex>game.getNumberOfRoles()){
						System.err.println("roleindex out of bounds");
						printUsage();
						System.exit(-1);
					}
				}else{
					missingArgumentsExit(argv[index]);
				}
				if(argv[index].equals("-legal")){
					player=new LegalPlayerInfo(roleindex-1);
				}else{
					player=new RandomPlayerInfo(roleindex-1);
				}
				index+=2;
			}else{
				System.err.println("invalid argument: "+argv[index]);
				printUsage();
				System.exit(-1);
			}
			if(playerinfos.get(roleindex-1)==null){
				playerinfos.set(roleindex-1,player);
			}else{
				System.err.println("duplicate roleindex: "+roleindex);
				printUsage();
				System.exit(-1);
			}
		}
		return index;
	}

	public static void printUsage(){
		System.out.println("usage:\n java -jar gamecontroller.jar MATCHID GAMEFILE STARTCLOCK PLAYCLOCK [ -printxml OUTPUTDIR XSLT ] [-scramble WORDFILE] { -remote ROLEINDEX HOST PORT | -legal ROLEINDEX | -random ROLEINDEX } ...");
		System.out.println("example:\n java -jar gamecontroller.jar A_Tictactoe_Match tictactoe.gdl 120 30 -remote 2 localhost 4000");
	}
	public static int getIntArg(String arg, String argName){
		try{
			return Integer.parseInt(arg);
		}catch(NumberFormatException ex){
			System.err.println(argName+" argument is not an integer");
			printUsage();
			System.exit(-1);
		}
		return -1;
	
	}
	public static void missingArgumentsExit(String arg){
		System.err.println("missing arguments for "+arg);
		printUsage();
		System.exit(-1);
	}
	public static void main(String argv[]){
		Game game=null;
		int startclock=0, playclock=0;
		String matchID, stylesheet=null, xmloutputdir=null;
		int index=0;
		GameScramblerInterface gameScrambler=null;
		if(argv.length>=4){
			matchID=argv[index]; ++index;
			game=Game.readFromFile(argv[index]); ++index;
			startclock=getIntArg(argv[index],"startclock"); ++index;
			playclock=getIntArg(argv[index],"playclock"); ++index;
			
			List<PlayerInfo> playerinfos=new LinkedList<PlayerInfo>();
			for(int i=0;i<game.getNumberOfRoles();i++){
				playerinfos.add(null);
			}
			while(index<argv.length){
				if(argv[index].equals("-printxml")){
					if(index+2<argv.length){
						xmloutputdir=argv[index+1];
						stylesheet=argv[index+2];
						index+=3;
					}else{
						missingArgumentsExit(argv[index]);
					}
				}else if(argv[index].equals("-scramble")){
					if(index+1<argv.length){
						gameScrambler=new GameScrambler(new File(argv[index+1]));
						index+=2;
					}else{
						missingArgumentsExit(argv[index]);
					}
				}else{
					index=parsePlayerArguments(index, argv, game, playerinfos);
				}
			}

			Logger logger=Logger.getLogger("tud.gamecontroller");
			logger.setUseParentHandlers(false);
			logger.addHandler(new UnbufferedStreamHandler(System.out, new PlainTextLogFormatter()));
			logger.setLevel(Level.ALL);
			GameController<Term, State> gc=new GameController<Term, State>(new Match<Term, State>(matchID, game, startclock, playclock, gameScrambler), playerinfos, new TermFactory(), logger);
			//logger.info("match:"+matchID);
			//logger.info("game:"+argv[1]);
			if(stylesheet!=null){
				XMLGameStateWriter<Term, State> gsw=new XMLGameStateWriter<Term, State>(xmloutputdir, stylesheet);
				gc.addListener(gsw);
			}
			Byte ourRole = 1;
			GameController.setCurrentGameName("game");
			if(argv.length > 6 && "-remote".equals(argv[4]) && "localhost".equals(argv[6]))
				ourRole = Byte.parseByte(argv[5]);
			if(argv.length > 1) {
				Pattern regex = Pattern.compile("^\\.\\/kif\\/(.+)\\.kif$", Pattern.DOTALL);
				Matcher matcher = regex.matcher(argv[1]);
				if(matcher.matches())
					GameController.setCurrentGameName(matcher.group(1));
			}
			gc.runGame(ourRole);
		}else{
			System.err.println("wrong number of arguments");
			printUsage();
			System.exit(-1);
		}
	}
}
