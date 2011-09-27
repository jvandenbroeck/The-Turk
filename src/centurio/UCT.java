package centurio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import ASP.ASPOracle;

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
 * This class implements an artificial intelligence which bases on Upper Confidence bounds
 * applied to Trees.
 *
 * @author Felix Maximilian M�ller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */
public abstract class UCT extends Thread {
	
	
	static protected double RRLVisits = 150;
	/**
	 * proportion between exploration and exploitation in UCT
	 */
	static protected double UCTK = GamePlayer.getConfigMap("UCTK");		
	/**
	 * proportion between exploration and exploitation in RAVE
	 */
	static protected double RAVEK = GamePlayer.getConfigMap("RAVEK");		
	/**
	 * proportion between UCT and RAVE
	 */
	static protected double betaK = GamePlayer.getConfigMap("betaK");		
	/**
	 * discount of the result
	 */
	static protected double discount = GamePlayer.getConfigMap("discount"); 	
	/**
	 * constant for calculating threshold for Progressive Widening
	 */
	static protected int progBoundFactor = GamePlayer.getConfigMap("progBoundFactor").intValue();
	/**
	 * constant A for Progressvie Widening
	 */
	static protected double progA = GamePlayer.getConfigMap("progA");
	/**
	 * constant B for Progressive Widening
	 */
	static protected double progB = GamePlayer.getConfigMap("progB");
	/**
	 * threshold for Progressive Widening
	 */
	static protected int progBoundVisits = 0;
	/**
	 * root of the game tree
	 */
	static protected Node root = null;				
	/**
	 * used actions in a sequence
	 */
	protected ArrayList<Object> usedActions = null; 	
	/**
	 *  connection to the (prolog) engine for the game logic
	 */
	protected final GameLogic gl; 
	/**
	 * initialize Rave values with a global move value -> map
	 */
	protected static SyncHashMap<Object, double[]> raveInitMap = new SyncHashMap<Object, double[]>(16);
	
	/**
	 * Discount factor softmax
	 */
	static protected double  temperature;
	static protected String tildeSettings;
	static protected double  gamma;
	static protected String thisServer;
	static protected String aceServer;
	static protected double minHorizon;
	static protected double gammaQ;
	/**
	 * for benchmarking
	 */
	protected int visitCounter = 0;
	/**
	 * for benchmarking
	 */
	 protected int randomGameCounter = 0;
	 
	
	static public int RRLCounter = 0;
	
	protected int core;
	
	/**
	 * Constructor 
	 */
	public UCT(String matchID, Integer core) {
		gl = new GameLogic(matchID);	
		usedActions = new ArrayList<Object>();
		this.core = core;
	}
	
	public void incrementRandomGames(){
			randomGameCounter++;
	}
	
	/**
	 * returns the counter of runs done by this thread<br>
	 * of benchmarking interesting 
	 * @return the counter of runs done by this thread
	 */
	public int getVisitCounter() {
		return visitCounter;
	}

	/**
	 * returns the counter of random games done by this thread <br>
	 * of benchmarking interesting 
	 * @return the counter of random games done by this thread
	 */
	public int getRandomGameCounter() {
		return randomGameCounter;
	}

	/**
	 * standard run-method of a thread <br>
	 * infinite loop, which can only terminate with a interrupt <br>
	 */
	public void run() {
		while (! isInterrupted())
		{
			if (!URPar.isInPanicMode()) {
				usedActions.clear();
				double[] result;
				try {
					result = calc_Episode(root);
				} catch (final PanicModeException e) {
					continue;
				}
				
				visitCounter++;
				updateInitRave(result);
			} else {
				try {
					sleep(500);
				} catch (final InterruptedException e) {
					interrupt();
				}
			}
		}
		this.gl.destroyEclipse();
	} 

	
	/**
	 * Create the first root of the game tree which represents the initial state
	 */
	public static void createRoot(GameLogic gl) {
		UCT.UCTK = GamePlayer.getConfigMap("UCTK");
		UCT.RAVEK = GamePlayer.getConfigMap("RAVEK");
		UCT.betaK = GamePlayer.getConfigMap("betaK");
		UCT.discount = GamePlayer.getConfigMap("discount");
		UCT.progBoundFactor = GamePlayer.getConfigMap("progBoundFactor").intValue();
		UCT.progA = GamePlayer.getConfigMap("progA");
		UCT.progB = GamePlayer.getConfigMap("progB");
		UCT.temperature = GamePlayer.getConfigMap("temperature");
		UCT.tildeSettings = GamePlayer.getConfigMapString("tildeSettings");
		UCT.gamma = GamePlayer.getConfigMap("gamma");
		UCT.thisServer = GamePlayer.getConfigMapString("thisServer");
		UCT.aceServer = GamePlayer.getConfigMapString("aceServer");
		UCT.minHorizon = GamePlayer.getConfigMap("minHorizon");
		UCT.gammaQ = GamePlayer.getConfigMap("gammaQ");
		
		//clean up URSolos static game
		URSolo.destroyStaticGame();

		//clean up ASP information
		ASPOracle.resetFoundAnswer();

		//clean up URPar information
		URPar.reset();

		//create root with init state
		Object initState = gl.getInitState();
		// die 20 sind erstmal ausm Bauch herausentstanden ^^
		root = new Node(initState,gl.activePlayer(initState),20);
		if (gl.isInPanicMode(root.getState())) {
			URPar.setPanicMode(gl.getRandomMove(root.getState()));
		} else {
			try {
				root.createChildren(gl);
			} catch (final PanicModeException e) {
				return;
			}
		}
		
		progBoundVisits = progBoundFactor*root.getChildsSize();
		
		//clean up nodes hashmap
		Node.resetTranspositionTable();
		raveInitMap.syncClear();
		Node.setDivideChildBound();
	}
	
	/**
	 * set new root: find node Child with Child.move.equals(translatedMove) 
	 */
	public static void changeRoot(GameLogic gl, Object translatedMove) {
		if (URPar.isInPanicMode()) {
			root = root.createSuccessorNode(gl, gl.getNextState(root.getState(), translatedMove));
			if (!gl.isTerminal(root.getState())) {
				URPar.setPanicMode(gl.getRandomMove(root.getState()));
			}
		} else {

			final Node rootTemp = root;
			try {
				// #################### begin: exclusive access to childs ####################
				rootTemp.getExclusiveAccessToChilds();

		 		   for (final Child child : rootTemp.getChildren())
		 		   {
		 			   if(translatedMove.equals(child.get_move())) {
		 				   root = child.get_child_pointer();
		 				   System.out.println("new State: "+STTranslator.printKIF(root.getState()));
		 				   if (GamePlayer.getSoloGame()) {
		 					   if (!GamePlayer.getLastSubmittedMove().equals(STTranslator.printKIF(((LinkedList<Object>)translatedMove).get(GamePlayer.getRoleIndex())))) {
		 						  ASPOracle.resetFoundAnswer();
		 						  URSolo.destroyStaticGame();
		 						  System.err.println("The last submitted move differs from the received move!");
		 					   } else {
		 						   URSolo.incHighestGameMoveIndex();
		 					   }
		 				   }
		 				   return;
		 			   }
		 		   }

			} finally {
				rootTemp.giveExclusiveAccessToChildsBack();
	    		// #################### end: exclusive access to childs ####################
	    	}
			
			System.err.println("No new root found!");
		}
	}
	
	public void updateQtree(){
		gl.updateQtree();
	}
	
    /**
	  * This method goes through a path of the gametree 
	  * until it reaches a node which is terminal,
	  * a node which hasn't any visits or 
	  * a node which hasn't any childs until now. 
	  * In the second case a random game will be played. 
	  * In the third his children will be calculated (next states) 
	  * and one of them will be chosen for another call of playSimulation
	  * @param node the current node in the path
	  * @return returns the result of simulation (between 0 and 1, see also playRandomGame)
      * @throws PanicModeException 
	  */
	protected double[] calc_Episode(Node node) throws PanicModeException {
		double[] result = null;
	   	if (node.isStateTerminal())
		{
			result = node.getScore();
		}
	   	else {
	   		if (node.getVisits() == 0)
	   		{	
	   			Object[] StateActions = null;
	   			// Write first RRLVisits to file
	   			
	   		
   				// StateActions = gl.randomGame(node.getState(),UCT.alpha);
	   		
	   			
	   		// Write first RRLVisits to file
				if (core == 0) { // i voor eerste slave enkel
					if (RRLCounter == 0) {

						StateActions = gl.rrlGameWrite(node.getState(),
								UCT.temperature, UCT.gamma);
						System.out.println("Writing state.. ");

					} else if (RRLCounter < RRLVisits) {
						// write
						StateActions = gl.rrlGameWrite(node.getState(),
								UCT.temperature, UCT.gamma);
						System.out.print(".");

					} else if (RRLCounter == RRLVisits) {
						// no write, start proc generate qtree
						System.out.println("");
						System.out.println("Let's send our samples to the ACE server and process them.. "); // check
						gl.runRRL();
						StateActions = gl.rrlGame(node.getState(),
								UCT.temperature, UCT.gamma);
					} else {
						StateActions = gl.rrlGame(node.getState(),
								UCT.temperature, UCT.gamma);
					}

					

					incrementRRLCounter();

				} else {
					// andere slaves normal playout
					StateActions = gl.rrlGame(node.getState(), UCT.temperature,
							UCT.gamma);

				}
 			
	   			
				incrementRandomGames();

	   			
	   			double[] normScores = null;
	   			normScores = gl.scoresState(StateActions[0]);
	   			//pick actions out of StateActions
	   			int depth = 0;
				for(Object Move: (LinkedList<Object>)StateActions[1]){
					   usedActions.add(Move);
					   depth++;
				}
				 result = normScores;
				 double treeDiscount = Math.pow(discount,depth);
				 for (int i=0; i< result.length; i++)
			   	{
			   		result[i] = treeDiscount * result[i];
			   	}

	   		}
	   		else
	   		{
	   			if (node.getChildsSize() == 0)
	   			{
					node.createChildren(gl);
	   			}
	   			Child choosenChild = URSelect(node);
	   			choosenChild.get_child_pointer().incNumberOfThreads();
	   			result = calc_Episode(choosenChild.get_child_pointer());
	   			choosenChild.get_child_pointer().decNumberOfThreads();
	   				   			
	   			usedActions.add(choosenChild.get_move());
	   			update_Child(choosenChild, node, result, usedActions);

	   			
	   		}
	   	}	
	   	for (int i=0; i< result.length; i++)
	   	{
	   		result[i] = discount * result[i];
	   	}
	   	node.incVisits();
	   	return result;
  }
	
	protected void incrementRRLCounter() {
			RRLCounter++;
		
		
	}

	/**
	 * Select a child-node of a node which has the highest uct-value. 
	 * The uct-value will be calculated with the winning probability 
	 * and the number of visits of child-node and the parent-node.
	 * This decides which node will be examined next.
	 * @param node parent-node
	 * @return the child-node with the highest uct-value
	 */
    protected abstract Child URSelect(Node node);
	
    /**
    * Updates the information about a Child 
    * with a given result of random game,
    * @param child the child of node the information should be updated for
    * @param node The node that�s child should be updated
    * @param result has influence to the update
    * @param usedActions list of actions are used in the past
    */
   protected abstract void update_Child(Child child, Node node, double[] result, ArrayList<Object> usedActions);

   /**
    * Sorts the childs of a node with reference to heuristic values
    * @param node its childs will be sorted
    */
   final void sortChilds(final Node node)
   {
		node.sortChilds(new Comparator<Child>() {
			public final int compare(final Child child1, final Child child2) 
			{
				double child1Middle = 0;
				double child2Middle = 0;
				for (final double uct : child1.get_all_Q_UCT()) {
					child1Middle += uct;
				}
				for (final double uct : child2.get_all_Q_UCT()) {
					child2Middle += uct;
				}
				return (int) ((child1Middle - child2Middle) * -1000.0);
			   }
			}
		);
   }
   
   protected void updateInitRave(double[] scores)
   {
	   for (Object action : usedActions)
	   {
		   double[] moveScore = raveInitMap.syncGet(action);
		   if (moveScore == null)
		   {
			   moveScore = scores;
		   }
		   else
		   {
			   for(int i = 0; i<scores.length; i++)
			   {
				   moveScore[i] += (1/8)*(scores[i]/100-moveScore[i]);
			   }
		   }
		   raveInitMap.syncPut(action, moveScore);
	   }
   }
}
