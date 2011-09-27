package centurio;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

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
 * This class implements a struct for the combination between a state, a move to this
 * state and its heuristics.
 *
 * @author Felix Maximilian Möller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */
public class Child {

	/**
	 * 
	 */
	private final Node child_pointer;	//pointer to the node, which will be reached with the move
	/**
	 * a representation of a move
	 */
	private final LinkedList<Object> move;			
	/**
	 * heuristic UCT value
	 */
	private double[] Q_UCT = null;		
	/**
	 * visits local n(s,a)
	 */
	private int visits_local = 0;		
	/**
	 * heuristic RAVE value
	 */
	private double[] Q_RAVE = null;		
	/**
	 * m(s,a)
	 */
	private int visits_move = 0;

	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
	private final ReadLock readLock = readWriteLock.readLock();
	private final WriteLock writeLock = readWriteLock.writeLock();
	
	/**
	 * Constructor
	 * @param child_pointer reference to child object
	 * @param move	a available move in its state
	 * @param NrOfPlayers number of players who play the actual game
	 */
	public Child(Node child_pointer, LinkedList<Object> move, int NrOfPlayers)
	{
		this.child_pointer = child_pointer;
		this.move = move;
		// TODO Q_Werte höher initialisieren
		Q_UCT = new double[NrOfPlayers];
		Q_RAVE = new double[NrOfPlayers];
	}
	
	/**
	 * returns a reference in regard to representing move of this object
	 * @return a reference in regard to representing move of this object
	 */
	public Node get_child_pointer()
	{
		return child_pointer;
	}
	
	/**
	 * returns the representing move of this object
	 * @return the representing move of this object
	 */
	public LinkedList<Object> get_move()
	{
		return move;
	}
	
	/**
	 * set a UCT-value for one player
	 * @param Q_UCT
	 * @param player_index
	 */
	public void set_one_Q_UCT (double Q_UCT, int player_index)
	{
		try {
			writeLock.lock();
			this.Q_UCT[player_index] = Q_UCT;
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 * get one UCT-value for one player
	 * @param player_index
	 * @return one UCT-value for one player
	 */
	public double get_one_Q_UCT (int player_index)
	{
		try {
			readLock.lock();
			return Q_UCT[player_index];
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * set all UCT-values new
	 * @param Q_UCT double[]
	 */
	public void set_all_Q_UCT (double[] Q_UCT)
	{
		try {
			writeLock.lock();
			this.Q_UCT = Q_UCT;
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 * get all UCT-values
	 * @return all UCT-values
	 */
	public double[] get_all_Q_UCT ()
	{
		try {
			readLock.lock();
			return Q_UCT.clone();
		} finally {
			readLock.unlock();
		}	
	}
	
	/**
	 * set a RAVE-value for one player
	 * @param Q_RAVE double
	 * @param player_index
	 */
	public void set_one_Q_RAVE (double Q_RAVE, int player_index)
	{
		try {
			writeLock.lock();
			this.Q_RAVE[player_index] = Q_RAVE;
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 * get one RAVE-value for one player
	 * @param player_index
	 * @return one RAVE-value for one player
	 */
	public double get_one_Q_RAVE (int player_index)
	{
		try {
			readLock.lock();
			return Q_RAVE[player_index];
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * set new all RAVE-values 
	 * @param Q_RAVE double[]
	 */
	public void set_all_Q_RAVE (double[] Q_RAVE)
	{
		try {
			writeLock.lock();
			this.Q_RAVE = Q_RAVE;
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 * get all RAVE-values
	 * @return all RAVE-values
	 */ 
	public double [] get_all_Q_RAVE ()
	{
		try {
			readLock.lock();
			return Q_RAVE.clone();
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * set new the number of runs through this child 
	 * @param visits
	 */
	public void set_visits_local(int visits)
	{
		try {
			writeLock.lock();
			visits_local = visits;
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 * get the number of runs through this child 
	 * @return the number of runs through this child
	 */
	public int get_visits_local()
	{
		try {
			readLock.lock();
			return visits_local;
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * increase the number of runs through this child
	 */
	public void inc_visits_local()
	{
		try {
			writeLock.lock();
			visits_local++;
		} finally {
			writeLock.unlock();
		}
		
	}
	
	/**
	 * set new the number of selection of this move in regard to RAVE 
	 * @param visits
	 */
	public void set_visits_move(int visits)
	{
		try {
			writeLock.lock();
			visits_move = visits;
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 * get the number of selection of this move in regard to RAVE
	 * @return the number of selection of this move in regard to RAVE
	 */
	public int get_visits_move()
	{
		try {
			readLock.lock();
			return visits_move;
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * increase the number of selection of this move in regard to RAVE
	 */
	public void inc_visits_move()
	{
		try {
			writeLock.lock();
			visits_move++;
		} finally {
			writeLock.unlock();
		}	
	}

}
