/*
 * Created on May 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package benchmark.cs227b.teamIago.util;

import java.util.ArrayList;

import benchmark.cs227b.teamIago.gameProver.GameSimulator;
import benchmark.cs227b.teamIago.resolver.ExpList;
import benchmark.cs227b.teamIago.resolver.Expression;

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
 * Essentially manages the loop counting variable
 * of a set of nested "for" loops of arbitrary nesting depth.
 * Manages the complete move generation for all or all but one players.
 * 
 */

public class RolePermuter extends ForNest{ 
	protected GameSimulator ggp;
	protected ExpList roles;
	protected ExpList [] moves;
	
/*	public RolePermuter(Atom role, GGP ggp)
	{
		super();
		this.ggp = ggp;
		roles = ggp.GetOtherRoles(role);
		if (roles == null) nestDepth = 0;
		else nestDepth = roles.size();
		moves = new ExpList[nestDepth];
		int[] roleSize = new int[nestDepth];
		for (int i = 0; i < nestDepth; i++)
		{
			ExpList mvs = ggp.GetLegalMoves(roles.get(i));
			moves[i] = mvs;
			roleSize[i] = mvs.size();
		}
		setLimits(roleSize);
	}
*/	
	public RolePermuter(ExpList roles, GameSimulator ggp)
	{
		super();
		this.ggp = ggp;
		this.roles = roles;
		nestDepth = roles.size();
		moves = new ExpList[nestDepth];
		int[] roleSize = new int[nestDepth];
		for (int i = 0; i < nestDepth; i++)
		{
			ExpList mvs = ggp.GetLegalMoves(roles.get(i));
			moves[i] = mvs;
			roleSize[i] = mvs.size();
		}
		setLimits(roleSize);		
	}
	
	public RolePermuter(ExpList roles, ArrayList moveSets, GameSimulator ggp)
	{
		super();
		this.ggp = ggp;
		nestDepth = roles.size();
		moves = new ExpList[moveSets.size()];
		int[] roleSize = new int[nestDepth];
		for (int i = 0; i < nestDepth; ++i) {
			moves[i] = (ExpList) moveSets.get(i);
			roleSize[i] = moves[i].size();
		}
		setLimits(roleSize);
	}
	
	public ExpList getFullMove(Expression myMove) {
		ExpList retMove = new ExpList();
		retMove.add(myMove);
		for (int i = 0; i < nestDepth; ++i)
			retMove.add(moves[i].get(count[i]));
		return retMove;
	}

	public ExpList getFullMove() {
		ExpList retMove = new ExpList();
		for (int i = 0; i < nestDepth; ++i)
			retMove.add(moves[i].get(count[i]));
		return retMove;
	}
	
	public ExpList getFullMove(ExpList partialMove) {
		ExpList retMove = new ExpList(partialMove);
		for (int i = 0; i < nestDepth; ++i) {
			retMove.add(moves[i].get(count[i]));
		}
		return retMove;
	}
	
	public ExpList getMove()
	{
		ExpList retMove = new ExpList();
		for (int i = 0; i < nestDepth; i++)
			retMove.add(moves[i].get(count[i]));
		return retMove;	
	}
	public boolean hasRoles() {
		return (roles != null) && (roles.size() > 0);
	}
}
