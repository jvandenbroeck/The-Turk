/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package benchmark.cs227b.teamIago.resolver;
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
 * @author Nick
 * @author University Dresden
 * @version 1.0
 */

/**
 *
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NotOp extends Connective {
	protected static final Atom notOperator = new Atom("NOT");
	protected static final int NOT_OPERATOR_HASH_SEED = 2147003267;
	protected static final int NOT_OPERANDS_HASH_SEED = 2147003527;
	protected static final int NOT_OPERANDS_HASH_MUL = 2147003759;

	/**
	 * @param e
	 */
	public NotOp(Expression e) {
		super();
		addOperand(e);
		setOperator(notOperator);
		buildVolatile(false);
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#apply(cs227b.teamIago.resolver.Substitution)
	 */
	public Expression apply(Substitution sigma) {
		if (operands.size() < 1) 
		{
			System.err.println("Error: 'Apply' called on 'Not' with undefined operand.");
			return null;
		}
		Expression e = operands.get(0);
		return new NotOp(e.apply(sigma));
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Evaluable#eval(cs227b.teamIago.resolver.Expression, cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public ArrayList eval(Substitution sigma, Theory t)  throws InterruptedException {
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.eval(sigma,t);

		t.enterChain(this);
		if (t.interrupted()) throw new InterruptedException();
		Substitution psi = evalOne(sigma,t);
		if (psi == null) {
			t.exitChain(this,false,null);
			return null;
		}
		ArrayList result = new ArrayList();
		result.add(psi);
		t.exitChain(this,true,result);
		return result;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Evaluable#evalOne(cs227b.teamIago.resolver.Expression, cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public Substitution evalOne(Substitution sigma, Theory t)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.evalOne(sigma,t);

		t.enterChain(this);
		
		Expression e = operands.get(0);
		if (t.interrupted()) throw new InterruptedException();
		Substitution psi = e.evalOne(sigma,t);
		if (psi == null) {
			// couldn't prove subgoal, so "not" is true
			t.exitChain(this,true,sigma);
			return sigma;
		}
		else {
			// proved subgoal, so "not" is false
			t.exitChain(this,false,null);
			return null;
		}
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#chain(cs227b.teamIago.resolver.Expression, cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public ArrayList chain(Substitution sigma, Theory t, boolean cond) throws InterruptedException {
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.chain(sigma,t, cond);

		t.enterChain(this);
		Expression e = operands.get(0);
		if (t.interrupted()) throw new InterruptedException();
		Substitution psi = e.chainOne(sigma,t, cond);
		if (psi == null) {
			ArrayList result = new ArrayList();
			t.exitChain(this,true,sigma);
			result.add(sigma);
			return result;
		}
		else {
			t.exitChain(this,false,null);
			return null;
		}
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#chainOne(cs227b.teamIago.resolver.Expression, cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public Substitution chainOne(Substitution sigma, Theory t, boolean cond)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();		
		if (!s.equals(this)) return s.chainOne(sigma,t, cond);
		t.enterChain(this);
		
		Expression e = operands.get(0);
		if (t.interrupted()) throw new InterruptedException();
		Substitution psi = e.chainOne(sigma,t, cond);
		if (psi == null) {
			t.exitChain(this,true,sigma);
			return sigma;
		}
		else {
			t.exitChain(this,false,null);
			return null;
		}
	}
	
	public boolean buildVolatile(boolean impliedVol) {
		amVolatile = amVolatile || impliedVol;
		// The operator here is NOT,
		// which is not volatile.
		
		// However, the volatility of the NotOp
		// depends on the volatility of the operands.
		amVolatile = operands.buildVolatile(false) || amVolatile;
		return amVolatile;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (operator.hashCode()*NOT_OPERATOR_HASH_SEED + 
				hashProd(operands,NOT_OPERANDS_HASH_MUL,NOT_OPERANDS_HASH_SEED))
				*NOT_OPERATOR_HASH_SEED % HASH_QUAD;

	}

	public Substitution mapTo(Substitution sigma, Expression e) {
		Substitution psi = null;
		if (e instanceof NotOp) {
			NotOp other = (NotOp) e;
			psi = operator.mapTo(sigma,other.operator);
			if (psi != null) psi = operands.mapTo(psi,other.operands);
		}		
		return psi;
	}
	
}
