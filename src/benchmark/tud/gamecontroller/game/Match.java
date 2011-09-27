package benchmark.tud.gamecontroller.game;

import benchmark.tud.gamecontroller.scrambling.GameScramblerInterface;
import benchmark.tud.gamecontroller.scrambling.IdentityGameScrambler;

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

public class Match<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> {
	private String matchID;
	private GameInterface<T, S> game;
	private int startclock;
	private int playclock;
	private GameScramblerInterface scrambler;
	
	public Match(String matchID, GameInterface<T, S> game, int startclock, int playclock){
		this(matchID, game, startclock, playclock, null);
	}
	public Match(String matchID, GameInterface<T, S> game, int startclock, int playclock, GameScramblerInterface scrambler){
		this.matchID=matchID;
		this.game=game;
		this.startclock=startclock;
		this.playclock=playclock;
		if(scrambler!=null){
			this.scrambler=scrambler;
		}else{
			this.scrambler=new IdentityGameScrambler();
		}
	}

	public String getMatchID() {
		return matchID;
	}

	public GameInterface<T, S> getGame() {
		return game;
	}

	public int getStartclock() {
		return startclock;
	}

	public int getPlayclock() {
		return playclock;
	}

	public GameScramblerInterface getScrambler() {
		return scrambler;
	}
}
