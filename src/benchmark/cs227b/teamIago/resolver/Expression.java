/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package benchmark.cs227b.teamIago.resolver;
import java.io.Serializable;
import java.util.ArrayList;

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
public abstract class Expression implements Serializable {
	public abstract Substitution mgu(Expression target, Substitution in, Theory t);
	public abstract Expression apply(Substitution sigma);
	protected final int HASH_RES_1 = 46147;
	protected final int HASH_RES_2 = 46511; // product gives hash_quad
	public static final int HASH_QUAD = 2146343117; //MAX_VALUE = 2147483647
	/* occurs
	 * @param var 
	 * 		The variable to check for occurrences
	 * @param sigma
	 * 		The current substitution
	 * 
	 * returns true if the variable appears in the
	 * current expression; false otherwise.
	 */
	public abstract boolean occurs(Variable var);
	public abstract Term firstOp();
	public abstract Term secondOp();
	public abstract ArrayList chain(Substitution sigma, Theory t, boolean cond) throws InterruptedException;
	public abstract Substitution chainOne(Substitution sigma, Theory t, boolean cond) throws InterruptedException;
	public abstract ArrayList eval(Substitution sigma, Theory t) throws InterruptedException;
	public abstract Substitution evalOne(Substitution sigma, Theory t) throws InterruptedException;
	public abstract long getMaxVarNum();
	public abstract ExpList getVars();
	public abstract boolean  buildVolatile(boolean impliedVol);
	public abstract boolean isVolatile();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public abstract boolean equals(Object obj);
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	//public abstract int hashCode();
	public int hashCode() {
		return toString().hashCode();
	}
	
	public abstract Substitution mapTo(Substitution sigma, Expression e);
	
	protected Expression ground(Theory t, Substitution sigma) {
		Expression s = apply(sigma);
		Expression s2 = this;
		while (!s.equals(s2)) {
			if (t.interrupted()) return null;
			s2 = s;
			s = s2.apply(sigma);
		}
		if (t.interrupted()) return null;
		else return s;
	}
	
}
