package benchmark.tud.gamecontroller.game.javaprover;

import java.io.File;

import benchmark.tud.gamecontroller.game.GameInterface;
import benchmark.tud.gamecontroller.game.Role;
import benchmark.cs227b.teamIago.parser.Axioms;

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

public class Game implements GameInterface<Term, State> {

	private Reasoner reasoner;
	private String gameDescription;
	private String name;
		
	private Game(String gameDescription, String name) {
		this.gameDescription=gameDescription;
		this.name=name;
		reasoner=new Reasoner(gameDescription);
		
	}

	public static Game readFromFile(String filename) {
		String gameDescription=Axioms.loadStringFromFile(filename);
		return new Game(gameDescription, (new File(filename)).getName());
	}

	public int getNumberOfRoles() {
		return reasoner.GetRoles().size();
	}

	public State getInitialState() {
		return new State(reasoner, reasoner.getInitialState());
	}

	public Role<Term> getRole(int roleindex) {
		return new Role<Term>(new Term(reasoner.GetRoles().get(roleindex-1)));
	}

	public String getGameDescription() {
		return gameDescription;
	}

	public String getName() {
		return name;
	}

	public String getKIFGameDescription() {
		return reasoner.getKIFGameDescription();
	}


}
