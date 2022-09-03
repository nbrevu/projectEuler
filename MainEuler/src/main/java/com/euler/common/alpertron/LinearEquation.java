package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import com.euler.common.EulerUtils;

/**
 * Equation of the form Dx+Ey+F=0. Also used as a subsystem for some second degree equations that are decomposed into simple ones like this.
 * 
 * The solutions provided by this equation are linear solutions of the form x=At+B, y=Ct+D for every t integer.
 */
public class LinearEquation implements Diophantine2dEquation<LinearSolution> {
	private BigInteger d;
	private BigInteger e;
	private BigInteger f;
	public LinearEquation(BigInteger d,BigInteger e,BigInteger f)	{
		this.d=d;
		this.e=e;
		this.f=f;
	}
	@Override
	public List<LinearSolution> solveSpecific() {
		BigInteger dd=d;
		BigInteger ee=e;
		BigInteger ff=f;
		if (dd.signum()==0)	{
			if (ee.signum()==0) throw new ArithmeticException("Malformed equation.");
			BigInteger[] div=ff.divideAndRemainder(ee);
			if (div[1].signum()!=0) return Collections.emptyList();
			else return Collections.singletonList(LinearSolution.fixedY(div[0].negate()));
		}	else if (ee.signum()==0)	{
			BigInteger[] div=ff.divideAndRemainder(dd);
			if (div[1].signum()!=0) return Collections.emptyList();
			else return Collections.singletonList(LinearSolution.fixedX(div[0].negate()));
		}	else	{
			BigInteger g=EulerUtils.gcd(dd,ee);
			BigInteger[] div=ff.divideAndRemainder(g);
			if (div[1].signum()!=0) return Collections.emptyList();
			dd=dd.divide(g);
			ee=ee.divide(g);
			ff=div[0];
			BigInteger[] coeffs=EulerUtils.extendedGcd(dd,ee).coeffs;
			BigInteger baseX=coeffs[0].multiply(ff).negate();
			BigInteger baseY=coeffs[1].multiply(ff).negate();
			return Collections.singletonList(new LinearSolution(baseX,ee,baseY,dd.negate()));
		}
	}
}
