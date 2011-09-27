package centurio;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * Centurio a General Game Player
 *
 * Copyright (C) 2009 Felix Maximilian M�ller, Marius Schneider and Martin Wegner
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
 * This class implements a node from the game tree.
 *
 * @author Felix Maximilian M�ller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 2.1
 */

final class Node
{

	private static int divideChildBound = GamePlayer.getConfigMap("divideChildBound").intValue();
	private static SyncHashMap<Object, WeakReference<Node>> transpositionTable = new SyncHashMap<Object, WeakReference<Node>>(GamePlayer.getConfigMap("hashMapSize").intValue());
	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
	private final ReadLock readLock = readWriteLock.readLock();
	private final WriteLock writeLock = readWriteLock.writeLock();
	private final List<Child> childs = Collections.synchronizedList(new ArrayList<Child>()); // a list of edges from the node
	private final Object state; // representation of the state in this node
	private final boolean isStateTerminal; // is the state terminal?
	private final int activePlayer; // ID of the player, its turn is
	private final double[] scoreList; // scores of all players in this state (only filled in terminal states)
	private int visits = 0; // total number of node visits 
	private int numberOfThreads = 0; // actual number of threads passing this node (threads on this path)
	private int kthChildBound = 5; // limitation of the childs which will be explored (progressive widening)

	/**
	 * constructor of a nonterminal state
	 * @param state
	 * @param activePlayer
	 * @param countMoves
	 */
	Node(final Object state, final int activePlayer, final int countMoves)
	{
		this.state = state;
		this.activePlayer = activePlayer;
		this.scoreList = null;
		this.isStateTerminal = false;
		kthChildBound = Math.max(countMoves / divideChildBound, 1);
	}

	/**
	 * constructor of a terminal state
	 * @param state
	 * @param score
	 * @param activePlayer
	 * @param countMoves
	 */
	Node(final Object state, final double[] score, final int activePlayer, final int countMoves)
	{
		this.state = state;
		this.scoreList = score;
		this.activePlayer = activePlayer;
		this.isStateTerminal = true;
		kthChildBound = Math.max(countMoves / divideChildBound, 1);
	}

	/**
	 * returns the state of the node
	 * @return the state of the node
	 */
	final Object getState()
	{
		return state;
	}

	/**
	 * returns if the state of the node is terminal
	 * @return if the state of the node is terminal
	 */
	final boolean isStateTerminal() {
		return isStateTerminal;
	}

	/**
	 * returns the size of the childs array
	 * @return the size of the childs array
	 */
	final int getChildsSize() {
		try {
			readLock.lock();
			return childs.size();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * exclusive access to the childs array
	 */
	final void getExclusiveAccessToChilds() {
		writeLock.lock();
	}

	/**
	 * gives the exclusive access to the childs array back
	 */
	final void giveExclusiveAccessToChildsBack() {
		writeLock.unlock();
	}

	/**
	 * sorting the childs array
	 * @param comparator
	 */
	final void sortChilds(final Comparator<Child> comparator) {
		try {
			writeLock.lock();
			Collections.sort(childs, comparator);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * returns the childs array
	 * you need to invoke the getExclusiveAccessToChilds methode before
	 * @return the childs array
	 */
	final List<Child> getChildren() {
		return childs;
	}

	/**
	 * returns the number of runs through this node
	 * @return the number of runs through this node
	 */
	final int getVisits()
	{
		try {
			readLock.lock();
			return visits;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * increase the number of runs through this node
	 */
	final void incVisits()
	{
		try {
			writeLock.lock();
			visits++;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * returns the number of threads which has actual this node in their sequence
	 * @return the number of threads which has actual this node in their sequence
	 */
	final int getNumberOfThreads()
	{
		try {
			readLock.lock();
			return numberOfThreads;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * increase the number of threads which has actual this node in their sequence
	 */
	final void incNumberOfThreads()
	{
		try {
			writeLock.lock();
			numberOfThreads++;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * decrease the number of threads which has actual this node in their sequence
	 */
	final void decNumberOfThreads()
	{
		try {
			writeLock.lock();
			numberOfThreads--;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * returns the score of this state of this node<br>
	 * this method is only valid if this is a terminal state !!!
	 * @return the score of the state of this node
	 */
	final double[] getScore()
	{
		return scoreList.clone();
	}

	/**
	 * returns the player, which has an option between several moves
	 * this method is only valid in sequential games !!!
	 * @return the player, which has an option between several moves
	 */
	final int getActivePlayer()
	{
		return activePlayer;
	}

	/**
	 * returns the k-th childbound of progressive widening
	 * @return the k-th childbound of progressive widening
	 */
	final int getKthChildBound()
	{
		try {
			readLock.lock();
			return kthChildBound;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * sets the k-th childbound of progressive widening
	 * @param k
	 */
	final void setKthChildBound(final int k)
	{
		try {
			writeLock.lock();
			kthChildBound = k;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * resets the transposition table
	 */
	final static void resetTranspositionTable()
	{
		transpositionTable.syncClear();
	}

    /**
     * Creates the children for a node. 
     * Whether it will be a new Node created 
     * and insert into the game tree, or 
     * the child is a existing node in the game tree 
     * (which will be find out with the help of the transposition table)
     * and the reference will be pointed at this node.
	 * @param gameLogic
     */
	final void createChildren(final GameLogic gameLogic) throws PanicModeException
	{
		try {
			writeLock.lock();
			if(this.childs.size() == 0 )
			{
				Object nextState = null;
				boolean terminal = false;
				int activePlayer = -1;
				double[] normScores = null;
				Node newNode = null;
				WeakReference<Node> refNewNode, tempRef = null;
				final LinkedList<LinkedList<Object>>  legalMoves = gameLogic.getAllLegalMoves(this.state);
				final int countLegalMoves = legalMoves.size();
				for(final LinkedList<Object> move : legalMoves)
				{
					nextState = gameLogic.getNextState(this.state, move);
					if (gameLogic.isInPanicMode(nextState)) {
						URPar.setPanicMode(gameLogic.getRandomMove(UCT.root.getState()));
						throw new PanicModeException();
					}
					//Eintrag existiert && Verweis auf Knoten existiert noch && Kollision abfangen
					if ( (tempRef = transpositionTable.syncGet(nextState)) != null && tempRef.get() != null && ((Node) tempRef.get()).getState().equals(nextState))
					{
						//state found in transposition tabel
						newNode = (Node) tempRef.get();
					}
					else
					{
						//state not found
						if (tempRef != null && tempRef.get() == null)
						{
							//remove outdated reference 
							transpositionTable.syncRemove(tempRef);
						}
						
						terminal = gameLogic.isTerminal(nextState);
						activePlayer = gameLogic.activePlayer(nextState);					
						if (terminal)
						{
							normScores = gameLogic.scoresState(nextState);
							newNode = new Node(nextState,normScores,activePlayer,countLegalMoves); // moves possible
						}
						else
							newNode = new Node(nextState,activePlayer,countLegalMoves);
							
						refNewNode = new WeakReference<Node>(newNode);
						transpositionTable.syncPutMaxSizeControl(nextState, refNewNode); 		//collision will be overwrite 
					}
					
					Child newChild = new Child(newNode,move,gameLogic.getNumberOfPlayers());
					this.childs.add(newChild);
					
				}
			}
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * sets the divideChildBound of progressive Widening with the configMap
	 */
	final static void setDivideChildBound()
	{
		Node.divideChildBound = GamePlayer.getConfigMap("divideChildBound").intValue();
	}

	/**
	 * returns a successor node from the current node
	 * @param gameLogic
	 * @param nextState
	 * @return a successor node from the current node
	 */
	final Node createSuccessorNode(final GameLogic gameLogic, final Object nextState) {
		final boolean terminal;
		final int activePlayer;
		final WeakReference<Node> refNewNode, tempRef;
		final Node newNode;
		final double[] normScores;

		//Eintrag existiert && Verweis auf Knoten existiert noch && Kollision abfangen
		if ( (tempRef = transpositionTable.syncGet(nextState)) != null && tempRef.get()!= null && ((Node) tempRef.get()).getState().equals(nextState))
		{
			//state found in transposition tabel
			newNode = (Node) tempRef.get();
		}
		else
		{
			//state not found
			if (tempRef != null && tempRef.get() == null)
			{
				//remove outdated reference 
				transpositionTable.syncRemove(tempRef);
			}
			
			terminal = gameLogic.isTerminal(nextState);
			activePlayer = gameLogic.activePlayer(nextState);					
			if (terminal)
			{
				normScores = gameLogic.scoresState(nextState);
				newNode = new Node(nextState,normScores,activePlayer,1);
			}
			else
				newNode = new Node(nextState,activePlayer,1);
				
			refNewNode = new WeakReference<Node>(newNode);
			transpositionTable.syncPutMaxSizeControl(nextState, refNewNode); 		//collision will be overwrite 
		}
		return newNode;
	}

}
