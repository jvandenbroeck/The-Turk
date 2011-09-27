package ASP;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.parctechnologies.eclipse.CompoundTerm;

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
 * This class implements the translation from KIF to Prolog. It is from the DTH GGP project.
 *
 * @author Holger Troelenberg
 * @author David Boehme
 * @author Kristine Jetzke
 * @author Felix Maximilian Möller
 * @author Marius Schneider
 * @author Martin Wegner
 * @version 1.0
 */

/*
 * STTranslator.java
 * translates from KIF to Prolog
 *
 * Created on 12. November 2006, 14:16
 * Author: Holger Troelenberg, David Boehme, Kristine Jetzke
 * Modified by Team Centurio
 */


public class ASPTranslator {
    
    // when set to true, translate() prints parsed tokens to stdout
    public static boolean debug = false;
    
    // translates Knowledge Interchange Format to Prolog
    public void translate(Reader in, PrintStream out) throws IOException, ParseException {
        // initialize tokenizer
        StreamTokenizer tokenizer = initTokenizer(in);
        // parse game description into list of functions
        LinkedList<ParsedTerm> rules = parseList(tokenizer);
        // sort the list of clauses by ECLiPSe procedure names
      //  sortParsedList(rules);
        // parse list of functions to prolog-like text
        printKnowledgeBase(rules, out);
    }
    
    // initializes a StreamTokenizer for the translation
	protected StreamTokenizer initTokenizer(Reader in) {

		StreamTokenizer tokenizer = new StreamTokenizer(in);
		tokenizer.resetSyntax();
		tokenizer.commentChar(';');
		tokenizer.eolIsSignificant(false);
		tokenizer.lowerCaseMode(true);
		tokenizer.slashSlashComments(false);
		tokenizer.slashStarComments(false);
		tokenizer.ordinaryChar('(');
		tokenizer.ordinaryChar(')');
		tokenizer.ordinaryChar('?');	// precedes a variable
		tokenizer.wordChars('<', '<');
		tokenizer.wordChars('=', '=');
		tokenizer.wordChars('a', 'z');
		tokenizer.wordChars('A', 'Z');
		tokenizer.wordChars('0', '9');
		tokenizer.wordChars('_', '_');
		tokenizer.wordChars('-', '-');
		tokenizer.wordChars('+', '+');
		tokenizer.wordChars('[', '[');
		tokenizer.wordChars(']', ']');
		tokenizer.wordChars('{', '{');
		tokenizer.wordChars('}', '}');
		tokenizer.wordChars('>', '>');
		tokenizer.wordChars('*', '*');
		tokenizer.wordChars('/', '/');
		tokenizer.wordChars('\\', '\\');
		tokenizer.wordChars('&', '&');
		tokenizer.wordChars('^', '^');
		tokenizer.wordChars('~', '~');
		tokenizer.wordChars('\'', '\'');
		tokenizer.wordChars('`', '`');
		tokenizer.wordChars('"', '"');
		tokenizer.wordChars('@', '@');
		tokenizer.wordChars('#', '#');
		tokenizer.wordChars('$', '$');
		tokenizer.wordChars('%', '%');
		tokenizer.wordChars(':', ':');
		tokenizer.wordChars(',', ',');
		tokenizer.wordChars('.', '.');
		tokenizer.wordChars('!', '!');
		tokenizer.wordChars('|', '|');
		tokenizer.whitespaceChars(' ', ' ');
		tokenizer.whitespaceChars('\t', '\t');
		tokenizer.whitespaceChars('\n', '\n');
		tokenizer.whitespaceChars('\013', '\013');
		tokenizer.whitespaceChars('\f', '\f');
		tokenizer.whitespaceChars('\r', '\r');

		return tokenizer;

	}
    
    // parses a list of terms, i.e. a flat list or a knowledge base
    // the input list may also end with 'EOF' instead of ')'
    // in case of list: expects that the leading "(" has already been read from the tokenizer
    // the input list must not contain any other lists at any point
    protected LinkedList<ParsedTerm> parseList(StreamTokenizer tokenizer) throws IOException, ParseException {
        LinkedList<ParsedTerm> elements = new LinkedList<ParsedTerm>();
        // finish if EOF or ')' encountered
        while (Arrays.binarySearch(new int[]{StreamTokenizer.TT_EOF, ')'}, tokenizer.nextToken()) < 0) {
            if (debug)
                System.out.println("STTranslator.parseList(): "+tokenizer);
            switch (tokenizer.ttype) {
                case '(': // parentheses enclose functions, since lists are not supported
                    elements.add(ParsedFunction.parse(tokenizer));
                    break;
                case StreamTokenizer.TT_WORD: // a single list element
                    elements.add(new ParsedConstant(tokenizer.sval));
                    break;
                case '?': // a single variable
                    elements.add(ParsedVariable.parse(tokenizer));
                    break;
                default:
                    throw new ParseException(tokenizer.lineno(), ')', tokenizer.ttype);
            }
        }
        if (debug)
            System.out.println("STTranslator.parseList(): "+tokenizer);
        return elements;
    }
    
    // interprets list of ParsedTerms as list of Prolog clauses and sorts them by their ECLiPSe procedure name
    protected void sortParsedList(LinkedList<ParsedTerm> rules) {
        Collections.sort(rules, new ParsedTermComparator());
    }
    
    // prints a list as a Prolog knowledge base
    protected void printKnowledgeBase(LinkedList<ParsedTerm> rules, PrintStream out) {
        for (ParsedTerm term: rules) {
            // print one rule per line, followed by a "."
            if (term instanceof ParsedFunction){
                ParsedFunction rule = (ParsedFunction) term;
                if (rule.head.equals("<=")) {
                    // rule is a real rule
                    if (rule.parameters.size() > 0)
                        printTerm(rule.parameters.get(0), out);
                    out.print(" :- ");
                    if (rule.parameters.size() > 1)
                        printTerm(rule.parameters.get(1), out);
                    for (int i = 2; i < rule.parameters.size(); i++) {
                        out.print(", ");
                        printTerm(rule.parameters.get(i), out);
                    }
                } else
                    // rule is a fact
                    printFunction(rule, out);
            } else if (term instanceof ParsedConstant)
                // term is a constant fact
                printConstant((ParsedConstant) term, out);
            else if (term instanceof ParsedVariable)
                throw new Error("Fact is Variable: "+(ParsedVariable)term+"; expected ParsedFunction or ParsedConstant");
            else
                throw new Error("ParsedTerm has unknown type: "+term.getClass()+"; expected ParsedFunction or ParsedConstant");
            out.println(".");
        }
    }
    
    // prints a term in Prolog syntax
    protected void printTerm(ParsedTerm term, PrintStream out) {
        if (term instanceof ParsedFunction)
            printFunction((ParsedFunction) term, out);
        else if (term instanceof ParsedConstant)
            printConstant((ParsedConstant) term, out);
        else if (term instanceof ParsedVariable)
            printVariable((ParsedVariable) term, out);
        else
            throw new Error("ParsedTerm has unknown type: "+term.getClass()+"; expected ParsedFunction, ParsedConstant, or ParsedVariable");
    }
    
    // prints a function in Prolog syntax
    protected void printFunction(ParsedFunction function, PrintStream out) {
        printFunctionHead(function.head, out);
        if (function.parameters.size() > 0) {
            out.print("(");
            printTerm(function.parameters.get(0), out);
            for (int i = 1; i < function.parameters.size(); i++) {
                out.print(", ");
                printTerm(function.parameters.get(i), out);
            }
            if (function.parameters.size() > 0)
                out.print(")");
        }
    }
    
	// replaces special characters in a string
	protected String replaceSpecialChars(String in) {
		return in.replace("_", "unterstrich_").replace("-", "minus_").replace("+", "plus_").replace("[", "eckigeklammerauf_").replace("]", "eckigeklammerzu_").replace("{", "geschweifteklammerauf_").replace("}", "geschweifteklammerzu_").replace(">", "spitzeklammerzu_").replace("*", "stern_").replace("/", "slash_").replace("\\", "backslash_").replace("&", "und_").replace("^", "dach_").replace("~", "tilde_").replace("'", "hochkomma_").replace("`", "hochkomma2_").replace("\"", "anfuehrungszeichen_").replace("@", "at_").replace("#", "raute_").replace("$", "dollar_").replace("%", "prozent_").replace(":", "doppelpunkt_").replace(".", "punkt_").replace("!", "ausrufezeichen_").replace("|", "strich_").replace("<", "spitzeklammerauf_").replace("=", "gleich_").replace(";", "semikolon_").replace("?", "fragezeichen_").replace("count", "countReplic");
	}

	// rereplaces the special characters in a string
	public static String resetSpecialChars(String in) {
		return in.replace("unterstrich_", "_").replace("minus_", "-").replace("plus_", "+").replace("eckigeklammerauf_", "[").replace("eckigeklammerzu_", "]").replace("geschweifteklammerauf_", "{").replace("geschweifteklammerzu_", "}").replace("spitzeklammerzu_", ">").replace("stern_", "*").replace("slash_", "/").replace("backslash_", "\\").replace("und_", "&").replace("dach_", "^").replace("tilde_", "~").replace("hochkomma_", "'").replace("hochkomma2_", "`").replace("anfuehrungszeichen_", "\"").replace("at_", "@").replace("raute_", "#").replace("dollar_", "$").replace("prozent_", "%").replace("doppelpunkt_", ":").replace("punkt_", ".").replace("ausrufezeichen_", "!").replace("strich_", "|").replace("spitzeklammerauf_", "<").replace("gleich_", "=").replace("semikolon_", ";").replace("fragezeichen_", "?").replace("countReplic","count");
	}
    
    // prints a function head in Prolog syntax
    protected void printFunctionHead(String head, PrintStream out) {
        out.print(replaceSpecialChars(head));
    }
    
    // prints a constant in Prolog syntax
    protected void printConstant(ParsedConstant constant, PrintStream out) {
    	   out.print(replaceSpecialChars(constant.name));
    }
    
    // prints a variable in Prolog syntax
    protected void printVariable(ParsedVariable variable, PrintStream out) {
        out.print(replaceSpecialChars(variable.name.substring(0, 1).toUpperCase()+variable.name.substring(1)));
    }
    
    // super class for the game description parse tree
    protected static abstract class ParsedTerm {
    }
    
    // super class for constants and variables, not for functions
    protected static abstract class ParsedObject extends ParsedTerm {
        
        // textual name of the object
        public final String name;
        
        private ParsedObject(String name) {
            this.name = name;
        }
        
        // string representation consists of the name
        public String toString() {
            return name;
        }
    }
    
    // represents a constant or zero-arity predicate in the game description parse tree
    protected static class ParsedConstant extends ParsedObject {
        public ParsedConstant(String name) {
            super(name);
        }
    }
    
    // represents a variable in the game description parse tree
    protected static class ParsedVariable extends ParsedObject {
        // should be constructed by calling parse, hence private
        public ParsedVariable(String name) {
            super(name.toUpperCase());
        }
        
        // parses a variable from kif
        // expects, that the leading "?" has already been read from the tokenizer
        public static ParsedVariable parse(StreamTokenizer tokenizer) throws IOException, ParseException {
            // expecting a word denoting the variable name
            if (tokenizer.nextToken() != StreamTokenizer.TT_WORD)
                throw new ParseException(tokenizer.lineno(), StreamTokenizer.TT_WORD, tokenizer.ttype);
            if (debug)
                System.out.println("ParsedVariable.parse(): "+tokenizer);
            return new ParsedVariable(tokenizer.sval);
        }
    }
    
    // represents a function in the game description parse tree
    protected static class ParsedFunction extends ParsedTerm {
        
        // textual name of the function
        public String head;
        // the parameters' parse trees
        public LinkedList<ParsedTerm>  parameters;
        
        // should be constructed by calling parse, hence private
        ParsedFunction(String head, LinkedList<ParsedTerm>  parameters) {
            this.head = head;
            this.parameters = parameters;
        }
        
        // parses a function from kif
        // expects that the leading "(" has already been read from the tokenizer
        public static ParsedFunction parse(StreamTokenizer tokenizer) throws IOException, ParseException {
            // expecting a word denoting function head
            if (tokenizer.nextToken() != StreamTokenizer.TT_WORD)
                throw new ParseException(tokenizer.lineno(), StreamTokenizer.TT_WORD, tokenizer.ttype);
            if (debug)
                System.out.println("ParsedFunction.parse(): "+tokenizer);
            String head = tokenizer.sval;
            // parse other parameters
            LinkedList<ParsedTerm> parameterList = new LinkedList<ParsedTerm>();
            while (tokenizer.nextToken() != ')') {
                if (debug)
                    System.out.println("ParsedFunction.parse(): "+tokenizer);
                switch (tokenizer.ttype) {
                    case '(':
                        parameterList.add(parse(tokenizer));
                        break;
                    case '?':
                        parameterList.add(ParsedVariable.parse(tokenizer));
                        break;
                    case StreamTokenizer.TT_WORD:
                        parameterList.add(new ParsedConstant(tokenizer.sval));
                        break;
                    default:
                        throw new ParseException(tokenizer.lineno(), ')', tokenizer.ttype);
                }
            }
            if (debug)
                System.out.println("ParsedFunction.parse(): "+tokenizer);
            return new ParsedFunction(head, parameterList);
        }
        
        // string representation looks like head(param1, param2, [...])
        public String toString() {
            String parameters = "";
            for (ParsedTerm term: this.parameters)
                parameters += ", "+term;
            if (parameters.equals(""))
            	return head;
            else
            	return head+"("+parameters.substring(2)+")"; // skip parameters' leading ", "
        }
    }
    
    protected static class ParsedTermComparator implements Comparator<ParsedTerm> {
        public int compare(ParsedTerm term1, ParsedTerm term2) {
            // retrieve the terms' procedure names, significant for sorting
            String name1, name2;
            if (term1 instanceof ParsedObject)
                name1 = ((ParsedObject)term1).name;
            else if (term1 instanceof ParsedFunction) {
                ParsedFunction function = (ParsedFunction)term1;
                if ("<=".equals(function.head)) {
                    ParsedTerm parameter = function.parameters.get(0);
                    if (parameter instanceof ParsedObject)
                        name1 = ((ParsedObject)parameter).name;
                    else if (parameter instanceof ParsedFunction)
                        name1 = ((ParsedFunction)parameter).head;
                    else
                        throw new ClassCastException("((ParsedFunction)term1).parameters[0] is of type "+parameter.getClass());
                } else
                    name1 = function.head;
            } else
                throw new ClassCastException("term1 is of type "+term1.getClass());
            if (term2 instanceof ParsedObject)
                name2 = ((ParsedObject)term2).name;
            else if (term2 instanceof ParsedFunction) {
                ParsedFunction function = (ParsedFunction)term2;
                if ("<=".equals(function.head)) {
                    ParsedTerm parameter = function.parameters.get(0);
                    if (parameter instanceof ParsedObject)
                        name2 = ((ParsedObject)parameter).name;
                    else if (parameter instanceof ParsedFunction)
                        name2 = ((ParsedFunction)parameter).head;
                    else
                        throw new ClassCastException("((ParsedFunction)term2).parameters[0] is of type "+parameter.getClass());
                } else
                    name2 = function.head;
            } else
                throw new ClassCastException("term2 is of type "+term1.getClass());
            // return if the terms belong to different procedures
            int result = name1.compareTo(name2);
            if (result != 0)
                return result;
            // same procedure:
            if (term1 instanceof ParsedObject)
                if (term2 instanceof ParsedObject)
                    return 0; // object object
                else
                    return -1; // object function
            else
                if (term2 instanceof ParsedObject)
                    return 1; // function object
                else { // function function
                    ParsedFunction function1 = (ParsedFunction)term1, function2 = (ParsedFunction)term2;
                    if ("<=".equals(function1.head))
                        if ("<=".equals(function2.head))
                            return 0; // rule rule
                        else
                            return 1; // rule fact
                    else
                        if ("<=".equals(function2.head))
                            return -1; // fact rule
                        else
                            return 0; // fact fact
                }
        }
    }
    
    // parse exception carries information about the line, where the error is encountered, and the token types found and expected
    @SuppressWarnings({"serial"})
    public static class ParseException extends Exception {
        
        public final int lineno;
        // a char or a TT_-constant from StreamTokenizer
        public final int expected;
        // a char or a TT_-constant from StreamTokenizer
        public final int found;
        
        public ParseException(int lineno, int expected, int found) {
            this.lineno = lineno;
            this.expected = expected;
            this.found = found;
        }
        
        // returns "Parsing error in line <lineno>: expected <expected>, found <found>"
        public String getMessage() {
            int[] chars = new int[]{expected, found};
            String[] strings = new String[2];
            for (int i = 0; i < 2; i++) {
                switch (chars[i]) {
                    case StreamTokenizer.TT_EOF:
                        strings[i] = "EndOfFile";
                        break;
                    case StreamTokenizer.TT_EOL:
                        strings[i] = "EnOfLine";
                        break;
                    case StreamTokenizer.TT_NUMBER:
                        strings[i] = "Number";
                        break;
                    case StreamTokenizer.TT_WORD:
                        strings[i] = "Word";
                        break;
                    default:
                        strings[i] = "'"+(char)chars[i]+"'";
                }
            }
            return "Parsing Error in line "+lineno+": expected "+strings[0]+", found "+strings[1];
        }
    }

    public static String printKIF(Object object) {

		if(object == null)
			return "null";

		else if(object instanceof CompoundTerm) {

			CompoundTerm term = (CompoundTerm) object;
			String kif = "";
			String functor;

			if(term.functor().startsWith("ggp_"))
				functor = term.functor().substring(4);

			else
				functor = term.functor();

			functor = ASPTranslator.resetSpecialChars(functor);

			if(term.arity() > 0)
				kif += "(";

			kif += functor;

			for(int i = 1; i <= term.arity(); i++)
				kif += " " + printKIF(term.arg(i));

			if(term.arity() > 0)
				kif += ")";

			return kif;

		}

		else if(object instanceof Collection) {

			Collection coll = (Collection) object;

			if(coll.isEmpty())
				return "NIL";

			Iterator i = coll.iterator();
			String kif = "(" + printKIF(i.next());

			while(i.hasNext())
				kif += " " + printKIF(i.next());

			return kif + ")";

		}

		else
			return object.toString();

	}
    
    public static CompoundTerm MovesToMove(LinkedList<CompoundTerm> Moves, Integer roleIndex) {    	
    	return Moves.get(roleIndex);
    }

}
