package benchmark.tud.gamecontroller.game.javaprover;

import java.util.AbstractList;
import java.util.List;

import benchmark.cs227b.teamIago.resolver.Atom;
import benchmark.cs227b.teamIago.resolver.Connective;
import benchmark.cs227b.teamIago.resolver.ExpList;
import benchmark.cs227b.teamIago.resolver.Expression;
import benchmark.cs227b.teamIago.resolver.Variable;

import benchmark.tud.gamecontroller.game.AbstractTerm;
import benchmark.tud.gamecontroller.game.TermInterface;

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
 * @author University Dresden
 * @version 1.0
 */

public class Term extends AbstractTerm{
	
	private Expression expr;
	
	public Term(Expression expr){
		this.expr=expr;
	}

	public String getName() {
		return expr.firstOp().toString();
	}

	public Expression getExpr(){
		return expr;
	}
	
	public boolean isConstant() {
		return expr instanceof Atom;
	}

	public boolean isVariable() {
		return expr instanceof Variable;
	}
	
	public boolean isGround() {
		return expr.getVars().size()==0;
	}

	public List<TermInterface> getArgs() {
		if(expr instanceof Connective){
			return new TermList(((Connective)expr).getOperands());
		}else{
			return new TermList(new ExpList());
		}
	}
	
	private class TermList extends AbstractList<TermInterface>{
		private ExpList expList;

		public TermList(ExpList l){
			this.expList=l;
		}
		
		public TermInterface get(int index) {
			return new Term(expList.get(index));
		}

		public int size() {
			return expList.size();
		}
		
	}
	
	public String toString(){
		return expr.toString();
	}

}
