package benchmark.tud.gamecontroller.game.javaprover;

import benchmark.tud.gamecontroller.game.InvalidKIFException;

import benchmark.cs227b.teamIago.parser.Parser;
import benchmark.cs227b.teamIago.resolver.Connective;
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
 * @version 1.0
 */

public class TermFactory implements benchmark.tud.gamecontroller.game.TermFactoryInterface<Term> {

	public Term getTermFromKIF(String kif) throws InvalidKIFException {
		Term term=null;
		try{
			ExpList list=Parser.parseDesc("(bla "+kif+")");
			if(list.size()>0){
				ExpList list2=((Connective)list.get(0)).getOperands();
				term=new Term(list2.get(0));
			}
		}catch(Exception ex){
			throw new InvalidKIFException("Exception while parsing \""+kif+"\":"+ex.getMessage());
		}
		if(term==null){
			throw new InvalidKIFException("not a valid kif term:"+kif);
		}
		return term;
	}

}
