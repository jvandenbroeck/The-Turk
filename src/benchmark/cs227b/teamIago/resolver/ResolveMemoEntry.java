/*
 * Created on May 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package benchmark.cs227b.teamIago.resolver;

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
public class ResolveMemoEntry {
	protected Expression stored;
	protected Expression matcher;
	protected ArrayList subs;
	
	/**
	 * @param stored
	 */
	public ResolveMemoEntry(Expression stored) {
		super();
		this.stored = stored;
		ExpList vars = stored.getVars();
		vars = vars.removeDuplicates();
		Substitution match = new Substitution();
		for (int i = 0; i < vars.size(); ++i) {
			Variable v = (Variable) vars.get(i);
			Variable canon = new Variable(i);
			match.addAssoc(v,canon);
		}
		matcher = stored.apply(match);
		subs = null;
	}
	
	public ResolveMemoEntry(Expression e, ArrayList subs) {
		this(e);
		this.subs = subs;
	}
	/**
	 * @return Returns the stored.
	 */
	public Expression getStored() {
		return stored;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		if (arg0 == null) return false;
		ResolveMemoEntry other = (ResolveMemoEntry) arg0;
		return this.matcher.equals(other.matcher);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return matcher.hashCode();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "<ResolveMemoEntry: [ matcher = " 
			+ matcher.toString() + ", stored = " 
			+ stored.toString() + "]>"; 
	}
	
	public ArrayList getSubs(Expression e) {
		ArrayList comps = new ArrayList();
		Substitution psi = e.mapTo(new Substitution(),stored);
		for (int i = 0; i < subs.size(); ++i) {
			Substitution old = (Substitution) subs.get(i);
			comps.add(psi.apply(old));
		}
		return comps;
	}
}
