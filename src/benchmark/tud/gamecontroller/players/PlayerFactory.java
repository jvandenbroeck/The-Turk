package benchmark.tud.gamecontroller.players;

import benchmark.tud.gamecontroller.game.StateInterface;
import benchmark.tud.gamecontroller.game.TermFactoryInterface;
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
 * @author Felix Maximilian Möller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */

/*
 * PlayerFactory.java
 * Modified by Team Centurio
 */

public class PlayerFactory {

	public static <T extends TermInterface, S extends StateInterface<T, S>> Player<T,S> createRemotePlayer(Class<S> s, RemotePlayerInfo info, TermFactoryInterface<T> termfactory) {
		return new RemotePlayer<T,S>(info.getName(), info.getHost(), info.getPort(), termfactory);
	}
	public static <T extends TermInterface, S extends StateInterface<T, S>> Player<T,S> createRandomPlayer(RandomPlayerInfo info) {
		return new RandomPlayer<T,S>(info.getName());
	}
	public static <T extends TermInterface, S extends StateInterface<T, S>> Player<T,S> createLegalPlayer(LegalPlayerInfo info) {
		return new LegalPlayer<T,S>(info.getName());
	}
	public static <T extends TermInterface, S extends StateInterface<T, S>> Player<T,S> createPlayer(PlayerInfo info, TermFactoryInterface<T> termfactory) {
		if(info instanceof RemotePlayerInfo){
			Class<S> workaround = null;
			return createRemotePlayer(workaround, (RemotePlayerInfo)info, termfactory);
		}else if(info instanceof RandomPlayerInfo){
			return createRandomPlayer((RandomPlayerInfo)info);
		}else if(info instanceof LegalPlayerInfo){
			return createLegalPlayer((LegalPlayerInfo)info);
		}
		return null;
	}


}
