package benchmark.tud.gamecontroller.game;

import java.util.Collection;
import java.util.List;

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

public interface StateInterface<T extends TermInterface, S extends StateInterface<T,S>> {

	boolean isTerminal();

	S getSuccessor(List<Move<T>> moves);

	boolean isLegal(Role<T> role, Move<T> move);

	Move<T> getLegalMove(Role<T> role);

	int getGoalValue(Role<T> role);

	Collection<Move<T>> getLegalMoves(Role<T> role);

	Collection<Fluent<T>> getFluents();
}