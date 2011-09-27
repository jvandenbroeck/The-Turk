package centurio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

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
 * Implementation of UCT with RAVE in relation to solo games.
 *
 * @author Felix Maximilian M�ller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */
public class URSolo extends UCT {

	/**
	 * the lock to synchronize the threads for static games
	 */
	private final static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
	private final static ReadLock readLock = readWriteLock.readLock();
	private final static WriteLock writeLock = readWriteLock.writeLock();
	/**
	 * is set true if we found a game with 100 points through randomGame
	 */
	static private boolean staticSoloGame = false;
	/**
	 * is set true if the static game is ready to play
	 */
	static private boolean staticSoloGameReady = false; 
	/**
	 * is set true if we found a game with 100 points through randomGame and we are propagating the result up and create the list of static moves
	 */
	static private boolean propagate = false ; 
	/**
	 * a hash value of the root node (global)
	 */
	static private int globalRootHash;
	/**
	 * a hash value of the root node (local)
	 */
	private int threadsRootHash;
	/**
	 * signals that this thread has found the static game
	 */
	private boolean owner; 
	/**
	 * actions of the static game
	 */
	static private LinkedList<LinkedList<Object>> staticActions;
	/**
	 * actual random move index
	 */
	static int actualRandomMoveIndex;
	/**
	 * highest game score
	 */
	static double highestGame = -1.0;
	/**
	 * highest game score temp
	 */
	private double highestGameTemp = -1.0;
	/**
	 * actions of the highest game
	 */
	static private LinkedList<LinkedList<Object>> highestGameActions;
	/**
	 * actions of the highest game temp
	 */
	private LinkedList<LinkedList<Object>> highestGameActionsTemp;
	/**
	 * actual highest game move index
	 */
	static int actualHighestGameMoveIndex = 0;
	/**
	 * found new highest game to propagate
	 */
	private boolean newHighestGame = false;
	/**
	 * a hash value of the root node (local)
	 */
	private int highestGameRootHash;
	
	/**
	 * Constructor
	 * @param matchID
	 */
	public URSolo(String matchID) {
		super(matchID,0);
		owner = false;
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
	  * @see UCT
	  */
	@Override protected double[] calc_Episode(Node node) {
	   	double[] result = null;
	   	if(owner){
	   		owner = false;
	   		// before static game could finish propagation, the playtime is expired
	   		if((root.hashCode() != globalRootHash)){
				   destroyStaticGame();
				   System.err.println("static Game canceled due to an invalid root (playclock may expired during Episode)");
	   		}else{
	   			staticSoloGameReady = true;
	   			propagate = false;
		   		System.out.println("Switching to static mode! As from now we replay a random game got 100 points.");
		   		System.out.println("\tstatic game settings:");
		   		System.out.println("\t\tnumber of moves as from current state: " + staticActions.size());
		   		System.out.print("\t\tmoves in the static game: ");
		   		for(Object move : staticActions){
		   			System.out.print(" -> " + STTranslator.printKIF(move));
		   		}
		   		System.out.println("");
		   		GamePlayer.wakeUp();
	   		}
	   	}
	   	if (newHighestGame && root.hashCode() == highestGameRootHash) {
	   		try {
				writeLock.lock();
				highestGame = highestGameTemp;
				highestGameActions = highestGameActionsTemp;
				actualHighestGameMoveIndex = 0;
			} finally {
				writeLock.unlock();
			}
	   	}
	   	newHighestGame = false;
	   	if (node.isStateTerminal())
		{
			result = node.getScore();
			//result[0] = result[0]/10.0;
			//System.out.println(this.getId() + " has reached a terminal State!(" + result[0] + ")(This part of the tree is fully calculated!)");
		}
	   	else {
	   		if (node.getVisits() == 0)
	   		{









	   			threadsRootHash = root.hashCode(); //hashing to compare root before and after random game
	   			Object[] StateActions = gl.rrlGame(node.getState(), UCT.temperature, UCT.gamma);
	   			randomGameCounter++;
	   			double[] normScores = null;
	   			normScores = gl.scoresState(StateActions[0]);
	   			//System.out.println(this.getId() + " has played the " + randomGameCounter + " random game!(" + normScores[0] + ")");
	   			//pick actions out of StateActions
	   			int depth = 0;
				for(Object Move: (LinkedList<Object>)StateActions[1]){
					   usedActions.add(Move);
					   depth++;
				}
				 result = normScores;
				 //backup actions of the randomGame to play it static
				 if(result[0] == 1.0){
					 System.out.println(this.getName() + "found a random game with 100 points!");
					 try {
						writeLock.lock();
						if(!staticSoloGame){
							if(threadsRootHash == root.hashCode()){
								staticSoloGame = true;
								propagate = true;
								owner = true;
								staticActions = (LinkedList<LinkedList<Object>>)StateActions[1];
								globalRootHash = threadsRootHash;
								System.out.println("Found a random game with 100 points!");
								System.out.println("\tit has " + ((LinkedList<Object>)StateActions[1]).size() + " and the folowing moves from current node:");
								for(Object move : (LinkedList<Object>)StateActions[1]){
									System.out.print(" -> " + STTranslator.printKIF(move));
								}
							}else {
								System.out.println("Root changed during randomGame! For this reason the static game with 100 points won�t start!");
							}
						}
					} finally {
						writeLock.unlock();
					}
				 } else {
					final double tempHighestGame;
					try {
						readLock.lock();
						tempHighestGame = highestGame;
					} finally {
						readLock.unlock();
					}
					if (result[0] > tempHighestGame && threadsRootHash == root.hashCode()) {
						System.out.println("New highest game: " + result[0]);
						highestGameTemp = result[0];
						highestGameActionsTemp = (LinkedList<LinkedList<Object>>)StateActions[1];
						highestGameRootHash = threadsRootHash;
						newHighestGame = true;
					}
				 }
				 
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
	   				try {
						node.createChildren(gl);
					} catch (final PanicModeException e) {
						e.printStackTrace();
					}
					try {
						// #################### begin: exclusive access to childs ####################
				 		node.getExclusiveAccessToChilds();					
				 		for (final Child child : node.getChildren()) {
				 			if (child.get_child_pointer().isStateTerminal() && child.get_child_pointer().getScore()[0] == 1.0) {
				 				System.out.println(this.getName() + "found a random game with 100 points after createChildren!");
								 try {
									writeLock.lock();
									if(!staticSoloGame){
										if(threadsRootHash == root.hashCode()){
											staticSoloGame = true;
											propagate = true;
											owner = true;
											staticActions = new LinkedList<LinkedList<Object>>();
											staticActions.add(child.get_move());
											globalRootHash = threadsRootHash;
											result = child.get_child_pointer().getScore();
											System.out.println("Found a random game with 100 points!");
											System.out.println("\tit has 1 and the folowing moves from current node:");
											System.out.println(" -> " + STTranslator.printKIF(child.get_move()));
										}else {
											System.out.println("Root changed during randomGame! For this reason the static game with 100 points won�t start!");
										}
									}
								} finally {
									writeLock.unlock();
								}
								break;
				 			}
				 		}
					} finally {
						node.giveExclusiveAccessToChildsBack();
						// #################### end: exclusive access to childs ####################
					}
	   			}
	   			if (!staticSoloGame) {
	   				Child choosenChild = URSelect(node);
	   				choosenChild.get_child_pointer().incNumberOfThreads();
	   				result = calc_Episode(choosenChild.get_child_pointer());
	   				choosenChild.get_child_pointer().decNumberOfThreads();

	   				//if (choosenChild.get_child_pointer().get_nr_threads() > 1 || choosenChild.get_child_pointer().get_nr_threads() < 0)
		   			//System.out.println("Threads: "+choosenChild.get_child_pointer().get_nr_threads());
		   			//add the move of choosenChild to the static movelist because a randomGame gained 100 points
		   			if(propagate && owner){
		   				staticActions.add(0, choosenChild.get_move());
		   				System.out.println(STTranslator.printKIF(choosenChild.get_move()) + " has been propagated");
		   			} else if (newHighestGame) {
		   				highestGameActionsTemp.add(0, choosenChild.get_move());
		   			}
		   				
		   			
		   			usedActions.add(choosenChild.get_move());
		   			update_Child(choosenChild, node, result, usedActions);

	   			} else {
	   				result = new double[1];
	   				result[0] = 1.0;
	   			}
	   		}
	   	}	
	   	for (int i=0; i< result.length; i++)
	   	{
	   		result[i] = discount * result[i];
	   	}
	   	node.incVisits();
	   	return result;
 }
	
	
	/**
	 * Select a child-node of a node which has the highest uct-value. 
	 * The uct-value will be calculated with the winning probability 
	 * and the number of visits of child-node and the parent-node.
	 * This decides which node will be examined next.
	 * @param node parent-node
	 * @return the child-node with the highest uct-value
	 */
	protected Child URSelect(Node node) {
	    	Child next = null;
	    	Child best = null;
	    	double beta = 0;
	    	double max = -1;
	    	double Q_UR = 0;
	    	double Q_P_UCT = 0;
	    	double Q_P_RAVE = 0;
	    	double visits_RAVE = 0;
	    	Iterator<Child> it = null;
	    	int childBound = node.getChildsSize();
	    	if (node.getVisits() == progBoundVisits)
	    	{
	    		sortChilds(node);
	    		childBound = node.getKthChildBound();
	    	}
	    	if (node.getVisits() > progBoundVisits)
	    	{
	    		int kthBound =  (int) (node.getKthChildBound()+ Math.log(node.getVisits()/progA)/Math.log(progB));
	    		if (kthBound > node.getKthChildBound())
		    		node.setKthChildBound(kthBound);
	    		if (kthBound < childBound)
	    			childBound = (int) node.getKthChildBound();
	    	}
	    	int counter = 0;

	    	try {
	 		   // #################### begin: exclusive access to childs ####################
	 		   node.getExclusiveAccessToChilds();

	 		  for (final Child child : node.getChildren())
		    	{
		    		visits_RAVE += child.get_visits_move();
		    	}
		    	
		    	for (it = node.getChildren().iterator(); it.hasNext()&& counter < childBound; counter++ )
		    	{
		    		
		    		next = (Child) it.next();
		    		
		    		//found a terminal winning state
		    		if (next.get_child_pointer().isStateTerminal() && next.get_child_pointer().getScore()[0] == 1.0)
		    		{
		    			return next;
		    		}
		    		
		    		// UCT Heuristik
		    		if (next.get_visits_local() == 0)
		    			Q_P_UCT = 10 + Math.random(); 
		    		else
		    		{
		    			Q_P_UCT = next.get_one_Q_UCT(0) + UCTK * Math.sqrt(Math.log(node.getVisits())/next.get_visits_local());
		    			//System.out.println("Move: "+next.get_move()+" Exploration: "+Math.sqrt(Math.log(node.get_visits())/next.get_visits_local()));
		    		}
		    		// RAVE Heuristik
		    		if (next.get_visits_move() == 0)
		    			Q_P_RAVE = 10 + Math.random();
		    		else
		    			Q_P_RAVE = next.get_one_Q_RAVE(0) + RAVEK * Math.sqrt(Math.log(visits_RAVE)/next.get_visits_move());
		    		
		    		// combination of RAVE and UCT
		    		beta = Math.sqrt(betaK/(node.getVisits()+betaK));
		    		double threaddiscount = Math.sqrt(next.get_child_pointer().getNumberOfThreads()+1);
		    		Q_UR = (beta * Q_P_RAVE + (1-beta) * Q_P_UCT)/threaddiscount; 
		    		//System.out.println("Move: "+next.get_move()+" RAVE: "+Q_P_RAVE+" UCT: "+Q_P_UCT+" UR: "+Q_UR+" Threads: "+threaddiscount );
		    		if (Q_UR > max) {
		    			max = Q_UR;
		    			best = next;
		    		}
		    	}
	    	} finally {
	 		   node.giveExclusiveAccessToChildsBack();
	 		   // #################### end: exclusive access to childs ####################
	    	}

	    	return best;
	    }

		/**
	    * Updates the information about a Child 
	    * with a given result of random game,
	    * @param child the child of node the information should be updated for
	    * @param node The node that�s child should be updated
	    * @param result has influence to the update
	    * @param usedActions list of actions are used in the past
	    */
	   protected void update_Child(Child child, Node node, double[] result, ArrayList<Object> usedActions)
	   {
		   // UCT - UPDATE
		   child.inc_visits_local();
		   double Q_UCT = child.get_one_Q_UCT(0) + (result[0] - child.get_one_Q_UCT(0)) / child.get_visits_local();
		   child.set_one_Q_UCT(Q_UCT,0);
		   
		   //RAVE - UPDATE

		   try {
	 		   // #################### begin: exclusive access to childs ####################
	 		   node.getExclusiveAccessToChilds();

			   for (Child RAVEchild : node.getChildren())
			   {
				   if (usedActions.contains(RAVEchild.get_move()))
				   {
					   RAVEchild.inc_visits_move();
					   double Q_RAVE = child.get_one_Q_RAVE(0) + (result[0] - child.get_one_Q_RAVE(0)) / RAVEchild.get_visits_move();
					   child.set_one_Q_RAVE(Q_RAVE,0);
				   }
			   }
	    	} finally {
	    		node.giveExclusiveAccessToChildsBack();
	    		// #################### end: exclusive access to childs ####################
		    }
	   }
	   
	   /**
	    * evaluate the best move from the root
	    * @param centurioID position of centurio in the playerlist
	    * @return best move
	    */
	   static public Object getBestMove(int centurioID)
		{
		   Child bestMove = null;
		   // play static game if there was a randomGame with 100 points
		   if(staticSoloGameReady){
			   if(actualRandomMoveIndex<staticActions.size()){
				   final Node rootTemp = root;
				   try {
					   // #################### begin: exclusive access to childs #################### 
					   rootTemp.getExclusiveAccessToChilds();
					   for (Child child : rootTemp.getChildren()) {
						   if (child.get_move().get(centurioID).equals(staticActions.get(actualRandomMoveIndex).get(centurioID))) {
							   bestMove = child;
							   break;
						   }
					   }
				   } finally {
					   rootTemp.giveExclusiveAccessToChildsBack(); 
					   // #################### end: exclusive access to childs #################### 
				   } 
				   actualRandomMoveIndex++;
			   }else{
				   //there is no more move in staticActions,so there went something wrong. we switch back to normal mode!
				   destroyStaticGame();
				   System.err.println("There is no more move in staticActions, so there went something wrong. We switch back to normal mode!");
			   }   
		   } else {
			   final Object tempHighestGameAction;
			   try {
					readLock.lock();
					tempHighestGameAction = highestGameActions.get(actualHighestGameMoveIndex).get(centurioID);
			   } finally {
					readLock.unlock();
			   }
			   final Node rootTemp = root;
			   try {
				   // #################### begin: exclusive access to childs #################### 
				   rootTemp.getExclusiveAccessToChilds();
				   for (Child child : rootTemp.getChildren()) {
					   if (child.get_move().get(centurioID).equals(tempHighestGameAction)) {
						   bestMove = child;
						   break;
					   }
				   }
			   } finally {
				   rootTemp.giveExclusiveAccessToChildsBack(); 
				   // #################### end: exclusive access to childs #################### 
			   }
		   }
		   bestMove.set_one_Q_UCT(10000.0, centurioID);
		   return bestMove.get_move().get(centurioID);
		}

	   /**
	    * destroys the recording for a static game
	    */
	public static synchronized void destroyStaticGame() {
		try {
			writeLock.lock();
			staticSoloGameReady = false;
			staticSoloGame = false;
			propagate = false;
			staticActions = null;
			actualRandomMoveIndex = 0;	
			globalRootHash = 0;
			highestGame = -1.0;
			actualHighestGameMoveIndex = 0;
			highestGameActions = null;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * returns a boolean which signals if the records of the static game is finished 
	 * @return boolean indicates weather a static game is ready to play
	 */
	public static boolean getStaticSoloGameReady() {
		return staticSoloGameReady;
	}

	final static void incHighestGameMoveIndex() {
		try {
			writeLock.lock();
			actualHighestGameMoveIndex++;
		} finally {
			writeLock.unlock();
		}
	}
		
}
