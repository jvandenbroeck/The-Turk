package centurio;

import java.util.*;

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
 * Implementation of UCT with RAVE in relation to sequential games.
 *
 * @author Felix Maximilian M�ller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */
public class URSeq extends UCT {

	public URSeq(String matchID, Integer i) {
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
	    	double visits_RAVE = 0;
	    	int activePlayer = 0;
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
		    	
		    	for (it = node.getChildren().iterator(); it.hasNext() && counter < childBound; counter++)
		    	{

		    		next = (Child) it.next();
		    		activePlayer = node.getActivePlayer();
					if (next.get_child_pointer().isStateTerminal() && next.get_child_pointer().getScore()[activePlayer] == 1.0)
					{
						return next;
					}
		    		// UCT Heuristik
					
		    		if (next.get_visits_local() == 0)
		    			Q_P_UCT = 10 + Math.random(); 
		    		else
		    			Q_P_UCT = next.get_one_Q_UCT(activePlayer) + UCTK * Math.sqrt(Math.log(node.getVisits())/next.get_visits_local());
		    		
		    		// RAVE Heuristik
					if (next.get_visits_move() == 0)
					{
						double[] moveScores = raveInitMap.syncGet(next.get_move());
						double randomNr;
						if (moveScores == null)
						{
							randomNr = Math.random();
						}
						else
						{
							randomNr = moveScores[activePlayer];
						}
							
		    			Q_P_RAVE = 10 + randomNr;
					}
		    		else
		    			Q_P_RAVE = next.get_one_Q_RAVE(activePlayer) + RAVEK * Math.sqrt(Math.log(visits_RAVE)/next.get_visits_move());
		    		
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
		   child.inc_visits_local();
		   int i = node.getActivePlayer();
		   double Q_UCT = child.get_one_Q_UCT(i) + (result[i] - child.get_one_Q_UCT(i)) / child.get_visits_local();
		   child.set_one_Q_UCT(Q_UCT,i);

		   //RAVE - UPDATE

		   try {
			   // #################### begin: exclusive access to childs ####################
			   node.getExclusiveAccessToChilds();

			   for (Child RAVEchild : node.getChildren())
			   {
				   if (usedActions.contains(RAVEchild.get_move()))
				   {
					   RAVEchild.inc_visits_move();
					   double Q_RAVE = child.get_one_Q_RAVE(i) + (result[i] - child.get_one_Q_RAVE(i)) / RAVEchild.get_visits_move();
					   child.set_one_Q_RAVE(Q_RAVE,i);
					   
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
		   double bestValue = -1;
		   ArrayList<Child> bestChilds = new ArrayList<Child>();
		   final Node rootTemp = root;
		   System.out.println("visits: " + rootTemp.getVisits());

		   try {
	 		   // #################### begin: exclusive access to childs ####################
			   rootTemp.getExclusiveAccessToChilds();

	 		   for (final Child child : rootTemp.getChildren())
	 		   {

				   if (child.get_child_pointer().isStateTerminal() && child.get_child_pointer().getScore()[centurioID] == 1.0)
				   {
					   bestMove = child;
					   bestValue = Integer.MAX_VALUE;
				   }
				   
				   //print moveproperties for debugging
				   System.out.println("Move :"+STTranslator.printKIF(child.get_move()) + "\tv_local: " + child.get_visits_local() + "\tv_move: " + child.get_visits_move() + "\tQ_UCT: " +  child.get_one_Q_UCT(centurioID));
				    
				   if (child.get_one_Q_UCT(centurioID) > bestValue)
				   {
					   bestMove = child;
					   bestValue = child.get_one_Q_UCT(centurioID);
					   bestChilds.clear();
					   bestChilds.add(bestMove);
				   }else{
					   if(child.get_visits_local() == bestValue){
						   bestChilds.add(bestMove);
					   }
				   }
			   }

		   } finally {
			   rootTemp.giveExclusiveAccessToChildsBack();
	    		// #################### end: exclusive access to childs ####################
	    	}
		  
		   // If there are more than one node with the most visits, choose the one with the best uct-value
	/*
		   if(bestChilds.size()>1){
			   double bestUctValue = -1.0;
			   for (Child bestChild : bestChilds){
				   if(bestChild.get_one_Q_UCT(centurioID) > bestUctValue){
					   bestMove = bestChild;
					   bestUctValue = bestChild.get_one_Q_UCT(centurioID);
				   }
			   }
		   }
		*/	   
			   
		   bestMove.set_one_Q_UCT(10000.0, centurioID);
		   return bestMove.get_move().get(centurioID);
		}
		

}
