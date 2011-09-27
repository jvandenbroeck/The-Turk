package centurio;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

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
 * Implementation of UCT with RAVE in relation to parallel games.
 *
 * @author Felix Maximilian M�ller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */
public class URPar extends UCT {

	private static boolean panicMode = false;
	private static Object randomMove = null;

	public URPar(String matchID, Integer i) {
		// solo & par --> check int v slavebag -->
		super(matchID, i);
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
	    	double[] All_UCT = null;
	    	double[] All_RAVE = null;
	    	double Q_All_UCT = 0;
	    	double Q_All_RAVE = 0;
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
		    			    		
		    		All_UCT = next.get_all_Q_UCT();
		    		All_RAVE = next.get_all_Q_RAVE();
		    		for (int i = 0; i < All_UCT.length; i++)
		    		{
		    			Q_All_UCT += All_UCT[i];
		    			Q_All_RAVE += All_RAVE[i];
		    		}
		    		Q_All_UCT = Q_All_UCT / All_UCT.length;
		    		Q_All_RAVE = Q_All_RAVE / All_RAVE.length;	    		
		    		
		    		// UCT Heuristik
		    		if (next.get_visits_local() == 0)
		    			Q_P_UCT = 10 + Math.random();
		    		else
		    			Q_P_UCT = Q_All_UCT + UCTK * Math.sqrt(Math.log(node.getVisits())/next.get_visits_local());
		    		
		    		// RAVE Heuristik
		    		if (next.get_visits_move() == 0)
					{
						double[] moveScores = raveInitMap.syncGet(next.get_move());
						double randomNr = 0;
						if (moveScores == null)
						{
							randomNr = Math.random();
						}
						else
						{	
							for (double moveScore : moveScores)
							{
								randomNr += moveScore;
							}
							randomNr /= moveScores.length;
						}
							
		    			Q_P_RAVE = 10 + randomNr;
					}
		    		else
		    			Q_P_RAVE = Q_All_RAVE + RAVEK * Math.sqrt(Math.log(visits_RAVE)/next.get_visits_move());
		    		
		    		// combination of RAVE and UCT
		    		beta = Math.sqrt(betaK/(node.getVisits()+betaK));
		    		Q_UR = (beta * Q_P_RAVE + (1-beta) * Q_P_UCT)/Math.sqrt(next.get_child_pointer().getNumberOfThreads()+1);
		    		
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
		   int visits_local = child.get_visits_local();
		   visits_local++;
		   child.set_visits_local(visits_local);
		   for (int i = 0; i < result.length; i++)
		   {
			   double Q_UCT = child.get_one_Q_UCT(i) + (result[i] - child.get_one_Q_UCT(i)) / visits_local;
			   child.set_one_Q_UCT(Q_UCT,i);
		   }
		   
		   //RAVE - UPDATE

		   try {
	 		   // #################### begin: exclusive access to childs ####################
	 		   node.getExclusiveAccessToChilds();

			   for (Child RAVEchild : node.getChildren())
			   {
				   if (usedActions.contains(RAVEchild.get_move()))
				   {
					   RAVEchild.inc_visits_move();
					   for (int j = 0; j < result.length; j++)
					   {
						   double Q_RAVE = child.get_one_Q_RAVE(j) + (result[j] - child.get_one_Q_RAVE(j)) / RAVEchild.get_visits_move();
						   child.set_one_Q_RAVE(Q_RAVE,j);
					   }
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
		   if (panicMode) {
			   return randomMove;
		   } else {
			   Object bestMove = null;
			   Object centurioMove = null;
			   Hashtable<Object,Double> mapValue = new Hashtable<Object,Double>();
			   Hashtable<Object,Integer> mapTimes = new Hashtable<Object,Integer>();
			   double bestValue = -1;

			   final Node rootTemp = root;
			   try {
		 		   // #################### begin: exclusive access to childs ####################
				   rootTemp.getExclusiveAccessToChilds();

		 		   for (final Child child : rootTemp.getChildren())
		 		   {
		   
					   //print moveproperties for debugging
					  // System.out.println("Move :"+STTranslator.printKIF(child.get_move()) + "\tv_local: " + child.get_visits_local() + "\tv_move: " + child.get_visits_move() + "\tQ_UCT: " +  child.get_one_Q_UCT(centurioID));
					   
					   centurioMove = child.get_move().get(centurioID);
					   // TODO Alternative mit Visits durchdenken
					   // unsch�n, aber was sch�neres f�llt mir gerade nicht (manju)
					   if (mapValue.containsKey(centurioMove))
					   {
						   mapValue.put(centurioMove,mapValue.get(centurioMove)+child.get_one_Q_UCT(centurioID));
						   mapTimes.put(centurioMove,mapTimes.get(centurioMove)+1);
					   }
					   else 
					   {
						   mapValue.put(centurioMove,child.get_one_Q_UCT(centurioID));
						   mapTimes.put(centurioMove,1);
					   }
				   }

			   } finally {
				   rootTemp.giveExclusiveAccessToChildsBack();
		    		// #################### end: exclusive access to childs ####################
		    	}

			   double value = 0;
			   System.out.println("#possible Moves: "+rootTemp.getChildsSize());
			   System.out.println("#possible Moves for Centurio: "+mapValue.size());
			   for (Object centurio : mapValue.keySet())
			   {
				  value = mapValue.get(centurio) / mapTimes.get(centurio);
				  System.out.println("move: "+STTranslator.printKIF(centurio)+" heuristic: "+value);
				  if (value > bestValue){
					  bestMove = centurio;
					  bestValue = value;
				  }
					  
			   }

			   try {
				   // #################### begin: exclusive access to childs #################### 
				   rootTemp.getExclusiveAccessToChilds();
				   for (Child child : rootTemp.getChildren()) {
					   if (child.get_move().get(centurioID).equals(bestMove)) {
						   child.set_one_Q_UCT(child.get_one_Q_UCT(centurioID) + 10000.0, centurioID);
					   }
				   }
			   } finally {
				   rootTemp.giveExclusiveAccessToChildsBack(); 
				   // #################### end: exclusive access to childs #################### 
			   }

			   // Der durchschnittlich beste Werte wird gew�hlt
			   return bestMove;
		   }		  
		}

	   final static boolean isInPanicMode() {
		   return panicMode;
	   }

	   final static void setPanicMode(final Object randomMove) {
		   panicMode = true;
		   URPar.randomMove = randomMove;
		   System.out.println("########## PANIC MODE ##########");
	   }

	   final static void reset() {
		   panicMode = false;
		   randomMove = null;
	   }
		
}
