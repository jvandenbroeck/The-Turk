package benchmark.tud.gamecontroller.game.javaprover;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import benchmark.tud.gamecontroller.game.Fluent;
import benchmark.tud.gamecontroller.game.Move;
import benchmark.tud.gamecontroller.game.Role;
import benchmark.tud.gamecontroller.game.StateInterface;


import benchmark.cs227b.teamIago.resolver.Atom;
import benchmark.cs227b.teamIago.resolver.Connective;
import benchmark.cs227b.teamIago.resolver.ExpList;
import benchmark.cs227b.teamIago.resolver.Predicate;
import benchmark.cs227b.teamIago.util.GameState;

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

public class State implements StateInterface<Term, State> {
	private Reasoner reasoner;
	private GameState state;
	
	public State(Reasoner reasoner, GameState state) {
		this.reasoner=reasoner;
		this.state=state;
	}

	public boolean isTerminal() {
		return reasoner.IsTerminal(state);
	}

	public State getSuccessor(List<Move<Term>> moves) {
		ExpList movesList=new ExpList();
		for(int i=0; i<moves.size(); i++){
			ExpList doesArgs=new ExpList();
			doesArgs.add(reasoner.GetRoles().get(i));
			doesArgs.add(moves.get(i).getTerm().getExpr());
			movesList.add(new Predicate(new Atom("DOES"), doesArgs));
		}
		return new State(reasoner, reasoner.SuccessorState(state, movesList));
	}

	public boolean isLegal(Role<Term> role, Move<Term> move) {
		return reasoner.isLegal(role.getTerm().getExpr(), move.getTerm().getExpr(), state);
	}

	public Move<Term> getLegalMove(Role<Term> role) {
		return getLegalMoves(role).iterator().next();
	}

	public int getGoalValue(Role<Term> role) {
		return reasoner.GetGoalValue(role.getTerm().getExpr(), state);
	}

	public Collection<Move<Term>> getLegalMoves(Role<Term> role) {
		ExpList exprlist=reasoner.GetLegalMoves(role.getTerm().getExpr(), state);
		Collection<Move<Term>> moveslist=new LinkedList<Move<Term>>();
		for(int i=0;i<exprlist.size();i++){
			Move<Term> move=new Move<Term>(new Term(((Connective)exprlist.get(i)).getOperands().get(1)));
			moveslist.add(move);
		}
		return moveslist;
	}
	
	public String toString(){
		return state.toString();
	}

	@SuppressWarnings("unchecked")
	public Collection<Fluent<Term>> getFluents() {
		Collection<Fluent<Term>> fluents=new LinkedList<Fluent<Term>>();
		Iterator<ExpList> it=state.getMap().values().iterator();
		while(it.hasNext()){
			ExpList el=it.next();
			for(int i=0;i<el.size();i++){
				Predicate true_expr=(Predicate)el.get(i);
				 fluents.add(new Fluent<Term>(new Term(true_expr.getOperands().get(0))));
			}
		}
		return fluents;
	}

}
