/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package benchmark.cs227b.teamIago.resolver;

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
public class Association {
	Variable var;
	Expression sub;
	
	/**
	 * @param v
	 * @param sub
	 */
	public Association(Variable v, Expression sub) {
		this.var = v;
		this.sub = sub;
	}
	
	
	/**
	 * @return Returns the sub.
	 */
	public Expression getSub() {
		return sub;
	}
	/**
	 * @param sub The sub to set.
	 */
	public void setSub(Expression sub) {
		this.sub = sub;
	}
	/**
	 * @return Returns the v.
	 */
	public Variable getVar() {
		return var;
	}
	/**
	 * @param v The v to set.
	 */
	public void setVar(Variable v) {
		this.var = v;
	}
	
	public boolean assigns(Variable v) {
		return this.var.equals(v);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "{" + var.toString() + " -> " + sub.toString() + "}";
	}
}
