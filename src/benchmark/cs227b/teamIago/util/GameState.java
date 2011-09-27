/*
 * Created on May 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package benchmark.cs227b.teamIago.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import benchmark.cs227b.teamIago.resolver.ExpList;

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
 * @author Nick
 * @author University Dresden
 * @version 1.0
 */

/**
 *
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GameState implements Serializable{
	HashMap objState;
	HashMap objProven;
	HashSet objDisproven;
	
	public GameState(HashMap objState) {
		this.objState = objState;
		this.objProven = null;
		this.objDisproven = null;
	}
	
	public GameState(HashMap objState, 
			HashMap provenTrans, HashSet disprovenTrans) {
		this.objState = objState;
		this.objProven = provenTrans;
		this.objDisproven = disprovenTrans;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		Collection myLists = this.objState.values();
		Iterator myIt = myLists.iterator();
		long curHash = 0;

		for (; myIt.hasNext();)
		{
			ExpList myList = (ExpList) myIt.next();
			curHash += myList.hashCode();
		}
//		System.err.print("Hash: " + curHash + "   ");
		return (int)curHash;
		
	}
	public HashMap getMap() {
		return objState;
	}
	
	public HashMap getProven() {
		return objProven;
	}

	public HashSet getDisproven() {
		return objDisproven;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "< GameState: " + objState.toString() + " >";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		boolean old = false;
		if (arg0 == null) return false;
		
		if (old) {
			GameState hisObj = (GameState) arg0;
			boolean hashes = (hisObj.hashCode() == hashCode());
//			System.err.println("Hash codes equal? " + hashes);
			boolean ret = this.objState.equals(hisObj.objState);
//			System.err.println("Objects equal? " + ret);
			return ret;
		}
		else {
			GameState hisObj = (GameState) arg0;
			int hisHash = hisObj.hashCode();
			int myHash = hashCode();
			boolean hashes = (hisHash == myHash);
//			System.err.println("Hash codes equal? " + hashes);

			boolean ret = (this.objState.size() == hisObj.objState.size());
			// short-circuit to skip ContainsAll if sizes unequal
			if (ret == false) {
//				System.err.println("Objects equal? " + ret);
				return ret;
			}

			Collection hisLists = hisObj.objState.values();
			Collection myLists = this.objState.values();
			Iterator myIt = myLists.iterator();
			Iterator hisIt = hisLists.iterator();

			for (; myIt.hasNext();)
			{
				ExpList myList = (ExpList) myIt.next();
				ExpList hisList = (ExpList) hisIt.next();
				if (hisList.size() != myList.size()) {
					ret = false;
					break;
				}
				if (!hisList.containsAll(myList)) {
					ret = false;
					break;
				}
			}
//			System.err.println("Objects equal? " + ret);
			return ret;
		}
	}
}
