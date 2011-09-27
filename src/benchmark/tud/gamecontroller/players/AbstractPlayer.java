package benchmark.tud.gamecontroller.players;

import java.util.List;

import benchmark.tud.gamecontroller.MessageSentNotifier;
import benchmark.tud.gamecontroller.game.Match;
import benchmark.tud.gamecontroller.game.Move;
import benchmark.tud.gamecontroller.game.Role;
import benchmark.tud.gamecontroller.game.StateInterface;
import benchmark.tud.gamecontroller.game.TermInterface;

/**
 * Centurio a General Game Player
 *
 * Copyright (C) 2008 Felix Maximilian M�ller, Marius Schneider and Martin Wegner
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

public abstract class AbstractPlayer<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> implements Player<T,S> {

	protected Match<T,S> match=null;
	protected Role<T> role=null;
	private String name;

	public AbstractPlayer(String name){
		this.name=name;
	}
	
	public void gameStart(Match<T,S> match, Role<T> role, MessageSentNotifier notifier) {
		this.match=match;
		this.role=role;
	}

	public void gameStop(List<Move<T>> priormoves, MessageSentNotifier notifier) { }

	public String getName() {
		return name;
	}

	public String toString() {
		return "player("+name+")";
	}

}