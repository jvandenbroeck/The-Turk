package parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.LinkedList;
import java.util.Collection;

import com.parctechnologies.eclipse.Atom;
import com.parctechnologies.eclipse.CompoundTerm;
import com.parctechnologies.eclipse.CompoundTermImpl;

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
 * This class implements the translation from KIF to eclipse CompoundTerms. It is from
 * the DTH GGP project.
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
 * STCompoundTermTranslator.java
 * translates from KIF to eclipse CompoundTerms
 *
 * Created on 14. November 2006, 13:58
 * Author: Holger Troelenberg, David Boehme, Kristine Jetzke
 * Modified by Team Centurio
 */

public class STCompoundTermTranslator extends STTranslator {
    
    // translates Knowledge Interchange Format to Collection<CompoundTerm>
    public Collection translate(Reader in) throws IOException, ParseException {
        return translate(in, true);
    }
    public Collection translate(Reader in, boolean sort) throws IOException, ParseException {
        // initialize tokenizer
        StreamTokenizer tokenizer = initTokenizer(in);
        // parse game description into list of functions
        LinkedList<ParsedTerm> list = parseList(tokenizer);
        // sort the list of clauses by ECLiPSe procedure names
        if (sort)
            sortParsedList(list);
        // parse list of functions to list of CompoundTerms
        return translateList(list);
    }
    
    // translates a list of terms to a collection of CompoundTerms
    @SuppressWarnings({"unchecked"})
    protected Collection translateList(LinkedList<ParsedTerm> elements) {
        Collection result = new LinkedList();
        for (ParsedTerm term: elements)
            result.add(translateTerm(term));
        return result;
    }
    
    // translates a term to a CompoundTerm
    protected Object translateTerm(ParsedTerm term) {
        if (term instanceof ParsedFunction)
            return translateFunction((ParsedFunction) term);
        else if (term instanceof ParsedConstant)
            return translateConstant((ParsedConstant) term);
        else if (term instanceof ParsedVariable)
            return translateVariable((ParsedVariable) term);
        else
            throw new Error("ParsedTerm has unknown type: "+term.getClass()+"; expected ParsedFunction, ParsedConstant, or ParsedVariable");
    }
    
    // translates a function to a CompoundTerm
    protected CompoundTerm translateFunction(ParsedFunction function) {
        Object[] parameters = new Object[function.parameters.length];
        for (int i = 0; i < parameters.length; i++)
            parameters[i] = translateTerm(function.parameters[i]);
        return new CompoundTermImpl(translateFunctionHead(function.head), parameters);
    }
    
    // translates a function head
    protected String translateFunctionHead(String head) {
        return replaceSpecialChars(head);
    }
    
    // translates a constant
    protected Object translateConstant(ParsedConstant constant) {
        try {
            return Integer.parseInt(constant.name);
        } catch (NumberFormatException e) {
            return new Atom(replaceSpecialChars(constant.name));
        }
    }
    
    // translates a variable as null the eclipse representation
    protected Object translateVariable(ParsedVariable variable) {
        return null;
    }
}
