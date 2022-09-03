package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing an equation of the form Ax^2+Bxy+Cy^2=0 where B^2-4AC=k^2 for some integer k.
 * 
 * After multiplying times 4A, we get
 * 	4A^2x^2 + 4ABxy + 4ACy^2 = 0.
 * Since 4AC=B^2-k^2, we have
 * 	4A^2x^2 + 2A[(B+k)+(B-k)]xy + (B+k)(B-k)y^2,
 * which is equivalent to
 * 	(2Ax+(B+k)y) * (2Ax+(B-k)y).
 * This will be equal to 0 whenever any of the solutions is 0, so we can just solve separately these two equations:
 *  2Ax+(B+k)y = 0,
 *  2Ax+(B-k)y = 0.
 * Any (x,y) pair which is a solution of any of these equations will be a solution of the original one.
 */
public class HomogeneousEquationWithSquareDet implements Diophantine2dEquation<LinearSolution> {
	private final BigInteger a;
	private final BigInteger b;
	private final BigInteger k;
	public HomogeneousEquationWithSquareDet(BigInteger a,BigInteger b,BigInteger k) {
		this.a=a;
		this.b=b;
		this.k=k;
	}
	@Override
	public List<LinearSolution> solveSpecific() {
		BigInteger aa=a.add(a);
		List<LinearSolution> result=new ArrayList<>();
		result.addAll(new LinearEquation(aa,b.add(k),BigInteger.ZERO).solveSpecific());
		result.addAll(new LinearEquation(aa,b.subtract(k),BigInteger.ZERO).solveSpecific());
		return result;
	}
}
