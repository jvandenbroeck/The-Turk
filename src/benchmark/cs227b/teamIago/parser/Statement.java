/*
 * Statement.java
 * Created on Apr 7, 2005
 *
 */


package benchmark.cs227b.teamIago.parser;
import java.util.ArrayList;
//import java.util.StringTokenizer;

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
 * @author mike
 * @author University Dresden
 * @version 1.0
 */

/**
 * STATEMENT ::== LPAREN OPERATOR (LISTOF STATEMENTS or LITERALS) RPAREN
 */
public class Statement {
String operator;
ArrayList members = new ArrayList();


/**
 * Recursively/Polymorphically print out the statement
 */
 static void expand(Statement s) {
	System.err.println(s.operator);
	for (int i = 0; i < s.members.size(); i++)
		System.err.println("\t" + s.members.get(i));
	System.err.println("");
 }

 public String toString() {
	 StringBuffer sb = new StringBuffer();
	 sb.append("(").append(operator);
	 for (int i = 0; i < members.size(); i++)
	 {
		 Object oi = members.get(i);
		 String si = oi.toString();
		 sb.append(" ").append(si);
 	 }
 	 sb.append(")");
 	 return sb.toString();
 }



/**
 * Fill the statement instance by recursively parsing token stream
 * @param tk
 * @return
 */

boolean parse(Tokenizer tk){
	int start = tk.x;
	String tok = tk.next();
	if(tok==null) return false;
	boolean parseSuccess;
	if (!(tok.equals("("))){
		tk.x = start;
		return false;
	}
	operator = tk.next(); // operator
	if(operator==null) return false;
	if (operator.equals("(")){
		// Syntax error in source
//		System.err.println("Error: List as function name at line " + tk.sourceLine());
//		System.exit(-1);
		throw new RuntimeException("Syntax Error: List as function name at line " + tk.sourceLine());
	}
	if (operator.equals(")")){
		// Syntax error in source
//		System.err.println("Error: No operator in list at line " + tk.sourceLine());
//		System.exit(-1);
		throw new RuntimeException("Syntax Error: No operator in list at line " + tk.sourceLine());
	}
	Statement s = new Statement();

	while((parseSuccess=s.parse(tk)) || isLiteral(tk)){
		if(parseSuccess)
			members.add(s);
		else{
			tok=tk.next();
			if(tok==null) return false;
			members.add(tok);
		}
		s = new Statement();
	}

	tok=tk.next();
	if(tok==null) return false;
	if (!(tok.equals(")"))){
		tk.x = start;
		return false;
	}
	return true;
}

	boolean isLiteral(Tokenizer tk) {
		int start=tk.x;
		String tok = tk.next();
		if (tok !=null && !tok.equals("(") && !tok.equals(")")){
			tk.x=start;
			return true;
		}
		else{
			tk.x=start;
			return false;
		}
	}
}