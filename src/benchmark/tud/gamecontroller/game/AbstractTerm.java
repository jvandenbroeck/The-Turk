package benchmark.tud.gamecontroller.game;

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

public abstract class AbstractTerm implements TermInterface {

	public boolean isCompound() {
		return !isConstant() && !isVariable();
	}
	
	public String getKIFForm() {
		String s=null;
		if(!isCompound()){
			s=getName().toUpperCase();
		}else{
			s="("+getName().toUpperCase();
			for(TermInterface arg:getArgs()){
				s+=" "+arg.getKIFForm();
			}
			s+=")";
		}
		return s;
	}

	public String getPrefixForm() {
		String s=null;
		if(!isCompound()){
			s=getName().toLowerCase();
		}else{
			s=getName().toUpperCase()+"(";
			boolean first=true;
			for(TermInterface arg:getArgs()){
				if(!first){
					s+=",";
				}else{
					first=false;
				}
				s+=arg.getPrefixForm();
			}
			s+=")";
		}
		return s;
	}

}
