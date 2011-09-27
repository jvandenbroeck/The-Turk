/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package benchmark.cs227b.teamIago.gameProver;

import benchmark.cs227b.teamIago.parser.Parser;
import benchmark.cs227b.teamIago.resolver.ExpList;
import benchmark.cs227b.teamIago.resolver.Expression;
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

/**
 *
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GameSimulatorTest
{
	public GameSimulatorTest(){}

	// this main reads in an axiom file, a state file, and a moves file, and
	// prints out output according to the format specified in assignment 3
	public static void main(String args[])
	{
		int maxIndex = 0;
		int offset;
		String axiomFile, stateFile, movesFile;
		GameSimulator myGGP;
		boolean wantDebugPrintouts;
		ExpList movesList, roles, legalMoves, goalValues;
		GameState nextState;

		wantDebugPrintouts = false;
		offset = 0;
		if (args.length % 3 == 1)
		{
			wantDebugPrintouts = true;
			offset = 1;
		}

		if (args.length >= 3) maxIndex = args.length / 3;
		else {
			System.err.println("Expected arguments: [debug] axiom file, state file, move file");
			System.exit(-1);
			return;
		}


		for (int testfile = 1; testfile <=  maxIndex ; testfile++)
		{

		    boolean useOptimization = true;
		myGGP = new GameSimulator(wantDebugPrintouts, useOptimization);

		System.out.println("**************** Game " + testfile + " *****************");

		if (args.length > 3*testfile - 1 + offset)
		{
			axiomFile = args[3*(testfile-1) + 0 + offset];
			stateFile = args[3*(testfile-1) + 1 + offset];
			movesFile = args[3*(testfile-1) + 2 + offset];
		}
		else return;

		myGGP.ParseFileIntoTheory(axiomFile);
		myGGP.ParseFileIntoTheory(stateFile);

		movesList = Parser.parseFile(movesFile);


		// Get all the available roles
		roles = myGGP.GetRoles();

		if (roles == null) {
				System.out.println("No playable roles found: aborting.");
			System.exit(-1);
		}

		int i;
		for (i = 0; i < roles.size(); i++)
		{

			Expression player = roles.get(i);

			System.out.println("Player " + player.toString());
			legalMoves =  myGGP.GetLegalMoves(player);
			if (legalMoves == null)
				System.out.println("No legal moves found.");
			else
				System.out.println("Legal moves: " + legalMoves.toString());

			goalValues = myGGP.GetGoalValues(player);
			if (goalValues == null)
				System.out.println("No goal values found.");
			else
				System.out.println("Goal values: " +
				goalValues.toString());
		}

		if (myGGP.IsTerminal()) {
			System.out.println("Game is in terminal state.");
			System.out.println("(Not calculating next state.)");
		}
		else
		{
			System.out.println("Game is in non-terminal state.");

			GameState oldState;
			oldState = myGGP.GetGameState();
			myGGP.SimulateStep(movesList);

			nextState = myGGP.GetGameState();
			if (nextState == null)
				System.out.println("Could not calculate next state.");
			else
			{
				System.out.println("Next state: " + nextState);
				System.out.println("GGP's old state after Simulate:");
				System.out.println(oldState);
			}
		}


		System.out.println("************* End of Game " + testfile + " *************");
		}
	}

}
