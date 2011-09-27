package benchmark.tud.gamecontroller.game.javaprover;

import java.util.List;
import java.util.logging.Logger;

import benchmark.cs227b.teamIago.gameProver.GameSimulator;
import benchmark.cs227b.teamIago.parser.PublicAxiomsWrapper;
import benchmark.cs227b.teamIago.parser.Statement;
import benchmark.cs227b.teamIago.resolver.Atom;
import benchmark.cs227b.teamIago.resolver.ExpList;
import benchmark.cs227b.teamIago.resolver.Expression;
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
 * @author Felix Maximilian Möller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */

/*
 * Reasoner.java
 * Modified by Team Centurio
 */

public class Reasoner {

	private GameSimulator gameSim;
	private String gameDescription; 
	
	public Reasoner(String gameDescription) {
		this.gameDescription=gameDescription;
		gameSim=new GameSimulator(false, true);
		gameSim.ParseDescIntoTheory(gameDescription);
	}

	public boolean IsTerminal(GameState state) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			return gameSim.IsTerminal();
		}
	}

	public ExpList GetRoles() {
		synchronized (gameSim) {
			return gameSim.GetRoles();
		}
	}

	public GameState SuccessorState(GameState state, ExpList movesList) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			gameSim.SimulateStep(movesList);
			return gameSim.GetGameState();
		}
	}

	public boolean isLegal(Expression role, Expression move, GameState state) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			ExpList roleVar=new ExpList();
			roleVar.add(role);
			roleVar.add(move);
			try {
				return gameSim.getTheory().findp(new Predicate(new Atom("LEGAL"),roleVar));
			} catch (InterruptedException e) {
				Logger logger=Logger.getLogger("tud.gamecontroller");
				System.err.println("reasoner was interrupted during findp("+new Predicate(new Atom("LEGAL"),roleVar)+"):");
				System.err.println(e.getMessage());
				return false;
			}
		}
	}

	public int GetGoalValue(Expression role, GameState state) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			return gameSim.GetGoalValue(role);
		}
	}

	public ExpList GetLegalMoves(Expression role, GameState state) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			return gameSim.GetLegalMoves(role);
		}
	}

	public GameState getInitialState() {
		synchronized (gameSim) {
			gameSim.SimulateStart();
			return gameSim.getTheory().getState();
		}
	}

	public String getKIFGameDescription() {
		PublicAxiomsWrapper a=new PublicAxiomsWrapper();
		a.parseFromString(gameDescription);
		List<Statement> statements=a.getStatements();
		StringBuilder stringBuilder=new StringBuilder();
		for(Statement statement:statements){
			stringBuilder.append(statement.toString()).append(' ');
		}
		return stringBuilder.toString().toUpperCase();
	}

}
