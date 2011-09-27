/*
 * Axioms.java
 * Created on Apr 7, 2005
 *
 */

package benchmark.cs227b.teamIago.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import benchmark.cs227b.teamIago.resolver.ExpList;

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
 * Axioms.java
 * Modified by Team Centurio
 */

public class Axioms {
	ArrayList Statements = new ArrayList();


	boolean parseFromString(String gdl){
		boolean parseSuccess = true;
		// gdl = gdl.trim();
		Tokenizer tk = new Tokenizer(gdl);
		while (tk.x < tk.numTokens()){
			Statement s = new Statement();
			if (s.parse(tk))
				Statements.add(s);
			else {
				System.err.println("Error parsing token: " + tk.next()
					+ " at line " + tk.sourceLine());
				parseSuccess = false;
			}
		}
		return parseSuccess;
	}

	void display() {
		for (int i = 0; i < Statements.size(); i++)
		{
			Statement s = (Statement) Statements.get(i);
			Statement.expand(s);
		}
	}

public static String loadStringFromFile(String filename){
	StringBuffer sb = new StringBuffer();
	try{
	BufferedReader br = new BufferedReader(new FileReader(filename));
	String line;

	while((line=br.readLine())!=null){
		line = line.trim();
		sb.append(line + "\n"); // artificial EOLN marker
	}
	br.close();
	} catch (IOException e){
		System.out.println(e);
		System.exit(-1);
	}
	return sb.toString();
}


public static void main(String[] args){
//	String filename;
//	if (args.length > 0)
//		filename = args[0];
//	else filename = "../files/tictactoe.gdl";
//
//	System.err.print("Loading axioms...");
//	String str = loadStringFromFile(filename);
//	Axioms a = new Axioms();
//	boolean parsed = a.parseFromString(str);
//	if (!parsed) {
//		System.err.println("Errors encountered during parsing--aborting.");
//		System.exit(-1);
//	}
//	System.err.println("done.");
//	System.err.println("<List of axioms>");
//	a.display();
//	System.err.println("<End of axioms>");
Axioms a = new Axioms();
String str = "((mark 3 2) noop )";
ExpList e= Parser.parseExpList(str);

}
}
