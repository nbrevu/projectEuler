package com.euler.common.alpertron;

import java.math.BigInteger;

import com.euler.common.EulerUtils.Pair;

/**
 * The most basic interface for solutions of diophantine equations. Note that solutions come in four flavours:
 * - Single solutions.
 * - Linear solutions: x=At+B, y=Ct+D.
 * - Quadratic solutions: x=At^2+Bt+C, y=Dt^2+Et+F.
 * - Recursive solutions, where each solution is built on top of another one using a linear operation, X=A*X+B where X=(x,y), A is a 2*2 matrix and B is a vector.
 */
public interface DiophantineSolution extends Iterable<Pair<BigInteger,BigInteger>> {}
