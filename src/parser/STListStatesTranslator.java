package parser;

import java.io.PrintStream;
import java.util.LinkedList;

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
 * This class implements the translation from KIF to Prolog, adding a state denoter as
 * additional parameter to each predicate, prefixing (most) names by "ggp_" and variables
 * (except state denoter) by "VAR_". It is from the DTH GGP project.
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
 * STListStatesTranslator.java
 * translates from KIF to Prolog, adding a state denoter as additional parameter to each predicate,
 * prefixing (most) names by "ggp_" and variables (except state denoter) by "VAR_"
 *
 * Created on 12. November 2006, 14:56
 * Author: Holger Troelenberg, David Boehme, Kristine Jetzke
 * Modified by Team Centurio
 */

public class STListStatesTranslator extends STTranslator {
    
    // name of the added parameter that denotes a state
    public String stateDenoter = "S";
    
    // prints a list as a Prolog knowledge base, appending another parameter, <stateDenoter>, to each relation
    public void printKnowledgeBase(LinkedList<ParsedTerm> rules, PrintStream out) {
        // initialize stateDenoter and make first letter upper case
        if (stateDenoter == null || stateDenoter.length() == 0)
            stateDenoter = "S";
        else
            stateDenoter = stateDenoter.substring(0, 1).toUpperCase()+stateDenoter.substring(1);
        
        for (ParsedTerm term: rules) {
            // print one rule per line, followed by a "."
            if (term instanceof ParsedFunction) {
                ParsedFunction rule = (ParsedFunction) term;
                if (rule.head.equals("<=")) {
                    // rule is a real rule, print using ":-"
                    if (rule.parameters.length > 0)
                        printRelation(rule.parameters[0], out);
                    if (rule.parameters.length > 1) {
                        out.print(" :- ");
                        printRelation(rule.parameters[1], out);
                    }
                    for (int i = 2; i < rule.parameters.length; i++) {
                        out.print(", ");
                        printRelation(rule.parameters[i], out);
                    }
                } else
                    // rule is a fact, print as relation
                    printRelation(rule, out);
            } else if (term instanceof ParsedConstant)
                // term is a constant fact
                printConstantPlusState((ParsedConstant) term, out);
            else if (term instanceof ParsedVariable)
                throw new Error("Fact is Variable: "+(ParsedVariable)term+"; expected ParsedFunction or ParsedConstant");
            else
                throw new Error("ParsedTerm has unknown type: "+term.getClass()+"; expected ParsedFunction or ParsedConstant");
            out.println(".");
        }
    }
    
    // prints a relation, appending a state parameter to each relation
    protected void printRelation(ParsedTerm term, PrintStream out) {
        if (term instanceof ParsedFunction) {
            ParsedFunction function = (ParsedFunction) term;
            if ("role".equals(function.head))
                // handle "role" specially to allow not adding the state denoter
                printRole(function, out);
            else if ("init".equals(function.head))
                // handle "init" specially to allow not adding the state denoter
                printInit(function, out);
            else if ("not".equals(function.head))
                // handle "not" specially to allow translation to an efficient implementation
                printNot(function, out);
            else if ("distinct".equals(function.head))
                // handle "distinct" specially to allow translation to an efficient implementation
                printDistinct(function, out);
            else if ("or".equals(function.head))
                // handle "or" specially to allow translation to an efficient implementation
                printOr(function, out);
            else
                // print function including additional parameter <stateDenoter>
                printRelationPlusState(function, out);
        } else if (term instanceof ParsedConstant)
            // print zero-arity relation with one parameter denoting the state
            printConstantPlusState((ParsedConstant) term, out);
        else if (term instanceof ParsedVariable)
            throw new Error("Fact is Variable: "+(ParsedVariable)term+"; expected ParsedFunction or ParsedConstant");
        else
            throw new Error("ParsedTerm has unknown type: "+term.getClass()+"; expected ParsedFunction or ParsedConstant");
    }
    
    // prints a function, adding parameter <stateDenoter>
    protected void printRelationPlusState(ParsedFunction function, PrintStream out) {
        printRelationHead(function.head, out);
        out.print("(");
        for (ParsedTerm t: function.parameters) {
            printTerm(t, out);
            out.print(", ");
        }
        out.print(stateDenoter+")");
    }
    
    // prints a function, not adding another parameter
    protected void printRelationWithoutState(ParsedFunction function, PrintStream out) {
        printRelationHead(function.head, out);
        if (function.parameters.length > 0) {
            out.print("(");
            printTerm(function.parameters[0], out);
            for (int i = 1; i < function.parameters.length; i++) {
                out.print(", ");
                printTerm(function.parameters[i], out);
            }
            if (function.parameters.length > 0)
                out.print(")");
        }
    }
    
    // prints a constant as a relation, adding parameter <stateDenoter>
    protected void printConstantPlusState(ParsedConstant constant, PrintStream out) {
        printRelationHead(constant.name, out);
        out.print("("+stateDenoter+")");
    }
    
    // prints a relation head in Prolog syntax, prepending the ggp_-prefix
    protected void printRelationHead(String head, PrintStream out) {
        out.print("ggp_"+replaceSpecialChars(head));
    }
    
    // prints a variable in Prolog syntax, prepending the VAR_-prefix
    protected void printVariable(ParsedVariable variable, PrintStream out) {
        out.print("VAR_"+replaceSpecialChars(variable.name));
    }
    
    // prints the special role relation as a normal function with no additional state denoter
    protected void printRole(ParsedFunction function, PrintStream out) {
        printRelationWithoutState(function, out);
    }
    
    // prints the special init relation as a normal function with no additional state denoter
    protected void printInit(ParsedFunction function, PrintStream out) {
        printRelationWithoutState(function, out);
    }
    
    // prints the special not relation as the "~"-operator
    protected void printNot(ParsedFunction function, PrintStream out) {
        out.print("~");
        // enclose parameters in parentheses if more or less than one
        out.print(function.parameters.length != 1 ? "(" : " ");
        if (function.parameters.length > 0)
            printRelation(function.parameters[0], out);
        for (int i = 1; i < function.parameters.length; i++) {
            out.print(", ");
            printRelation(function.parameters[i], out);
        }
        if (function.parameters.length > 1)
            out.print(")");
    }
    
    // prints the special distinct relation as the "\="-operator
    protected void printDistinct(ParsedFunction function, PrintStream out) {
        if (function.parameters.length == 2) {
            printTerm(function.parameters[0], out);
            out.print(" \\= ");
            printTerm(function.parameters[1], out);
        } else {
            // enclose parameters in parentheses if more or less than two
            out.print("\\=(");
            if (function.parameters.length > 0)
                printTerm(function.parameters[0], out);
            for (int i = 1; i < function.parameters.length; i++) {
                out.print(", ");
                printTerm(function.parameters[i], out);
            }
            out.print(")");
        }
    }
    
    // prints the special or relation as the ";"-operator
    protected void printOr(ParsedFunction function, PrintStream out) {
        if (function.parameters.length > 1)
            out.print("(");
        if (function.parameters.length > 0)
            printRelation(function.parameters[0], out);
        for (int i = 1; i < function.parameters.length; i++) {
            out.print(" ; ");
            printRelation(function.parameters[i], out);
        }
        if (function.parameters.length > 1)
            out.print(")");
    }
}
