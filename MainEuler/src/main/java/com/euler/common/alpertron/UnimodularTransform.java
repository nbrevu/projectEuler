package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Unimodular transformation, used auxiliary in the StandardHomogeneousEquation class to guarantee that A and F are coprime. This could either not
 * represent a transformation (as represented by the dummy NoUnimodularTransformation object) or a simple one as represented by the "type 1" and
 * "type 2" object, where there is a biunivocal relationship between the original equation and the transformed one.
 */
public interface UnimodularTransform {
	public FixedSolution revertTransform(FixedSolution solution);
	public BigInteger[] transformCoeffs(BigInteger a,BigInteger b,BigInteger c);
	public default List<FixedSolution> revertTransform(List<FixedSolution> solutions)	{
		return solutions.stream().map(this::revertTransform).collect(Collectors.toUnmodifiableList());
	}
}
