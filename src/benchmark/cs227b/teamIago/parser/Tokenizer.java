package benchmark.cs227b.teamIago.parser;
import java.util.ArrayList;

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
 * @author mike
 * @author University Dresden
 * @version 1.0
 */

/*
 * Tokenizer.java
 * Created on Apr 10, 2005
 *
 */


public class Tokenizer {
	ArrayList tokens;
	ArrayList lineNums;
	int x;
	int numTokens(){
		return tokens.size();
	}
	Tokenizer(String gdl){
//		tokenize into a String[]
		int lineNum = 1;
		ArrayList _lineNums = new ArrayList();
		ArrayList _tokens = new ArrayList();
		int len = gdl.length();
		int start=0;
		for (int i=0;i<len;i++){
			char ch = gdl.charAt(i);
			if (ch=='(' || ch==')'){
				if (start !=i) {
					_tokens.add(gdl.substring(start,i));
					_lineNums.add(new Integer(lineNum));
				}
				_tokens.add(""+ch);  //ch to String
				_lineNums.add(new Integer(lineNum));
				start=i+1;
			} else if (Character.isWhitespace(ch)) {
				if (start !=i)	{
					_tokens.add(gdl.substring(start,i));
					_lineNums.add(new Integer(lineNum));
				}
				if (ch == '\n') lineNum++;
			  	start = i + 1;
			} else if (ch == ';') {
				// Skip comment lines
				if (start != i) {
					_tokens.add(gdl.substring(start,i));
					_lineNums.add(new Integer(lineNum));
				}
				// Jump to next line
				start = gdl.indexOf('\n',i);
				if (start == -1) i = len; // if last line, skip remainder of string
				lineNum++;
				i = start;
				start++;
			}
		}
		tokens = _tokens;
		lineNums = _lineNums;
		x = 0;
	}

	String next(){
		if (x >= tokens.size()) {
			return null;
//			System.err.println("Error: No more tokens in file");
//			System.exit(-1);
		}
		x++;
		return (String)tokens.get(x-1);
	}

	Integer sourceLine() {
		if (x > tokens.size()) {
			return null;
//			System.err.println("Error: Source line not defined for token past EOF");
//			System.exit(-1);
		}
		return (Integer)lineNums.get(x - 1);
	}
}
