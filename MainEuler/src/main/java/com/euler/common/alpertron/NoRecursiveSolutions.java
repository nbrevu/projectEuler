package com.euler.common.alpertron;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Dummy implementation of the RecursiveSolutionSource interface. No transformation happens because there aren't recursive solutions.
 */
public enum NoRecursiveSolutions implements RecursiveSolutionSource	{
	INSTANCE;
	@Override
	public List<DiophantineSolution> getSolutions(List<FixedSolution> baseSolutions) {
		return Lists.transform(baseSolutions,DiophantineSolution.class::cast);
	}
}
