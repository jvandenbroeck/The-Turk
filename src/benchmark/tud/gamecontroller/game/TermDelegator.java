package benchmark.tud.gamecontroller.game;

import java.util.List;

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

public abstract class TermDelegator<T extends TermInterface> implements TermInterface {

	private T term;

	public TermDelegator(T term) {
		this.term=term;
	}
	
	public T getTerm(){
		return term;
	}

	public List<TermInterface> getArgs() {
		return term.getArgs();
	}

	public String getKIFForm() {
		return term.getKIFForm();
	}

	public String getName() {
		return term.getName();
	}

	public String getPrefixForm() {
		return term.getPrefixForm();
	}

	public boolean isCompound() {
		return term.isCompound();
	}

	public boolean isConstant() {
		return term.isConstant();
	}

	public boolean isVariable() {
		return term.isVariable();
	}
	
	public boolean isGround() {
		return term.isGround();
	}
	
	public String toString() {
		return term.toString();
	}

	public boolean equals(Object obj) {
		if(obj instanceof TermDelegator){
			return term.equals(((TermDelegator<?>)obj).getTerm());
		}else if(obj instanceof TermInterface){
			return term.equals(obj);
		}else{
			return false;
		}
	}

	public int hashCode() {
		return term.hashCode();
	}
}
