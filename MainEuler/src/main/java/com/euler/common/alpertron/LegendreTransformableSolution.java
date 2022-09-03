package com.euler.common.alpertron;

import java.util.Optional;

/**
 * Common interface for solutions of the fixed and linear form, which can revert Legendre transformations.
 * 
 * Note that the revertLegendreTransform method will return objects of this same type, so that type information is not lost and no castings
 * are needed to continue in subsequent steps of the algorithm that might require solutions of a specific type (most likely FixedSolution).
 */
public interface LegendreTransformableSolution<T extends LegendreTransformableSolution<T>> extends DiophantineSolution	{
	public Optional<T> revertLegendreTransform(LegendreTransformation transform);
}
