package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.List;

/**
 * Dummy implementation of the UnimodularTransform class. No transformation happens, the equation can be solved as such.
 */
public enum NoUnimodularTransform implements UnimodularTransform {
	INSTANCE;
	@Override
	public FixedSolution revertTransform(FixedSolution solution) {
		return solution;
	}
	@Override
	public BigInteger[] transformCoeffs(BigInteger a,BigInteger b,BigInteger c) {
		return new BigInteger[]{a,b,c};
	}
	@Override
	public List<FixedSolution> revertTransform(List<FixedSolution> solutions)	{
		return solutions;
	}
}
