package com.euler;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

public class Euler703_3 {
	private static List<BitSet> getOpenCycles(int size)	{
		if (size==1)	{
			BitSet zero=new BitSet(1);
			BitSet one=new BitSet(1);
			one.set(0);
			return List.of(zero,one);
		}
		List<BitSet> result=new ArrayList<>();
		for (BitSet prev:getOpenCycles(size-1))	{
			result.add(prev);
			if (!prev.get(size-2))	{
				BitSet copy=new BitSet(size);
				copy.or(prev);
				copy.set(size-1);
				result.add(copy);
			}
		}
		return result;
	}
	
	private static List<BitSet> getCycles(int size)	{
		return getOpenCycles(size).stream().filter((BitSet b)->!(b.get(0)&&b.get(size-1))).collect(Collectors.toUnmodifiableList());		
	}
	
	public static void main(String[] args)	{
		/*
		 * This is getting closer to the result. The amount of cycles is VERY reasonable.
		 * Next steps:
		 * 1) Generate a reversed map. From the cycles, and from all the entry points, generate a tree where each element points to the "previous"
		 * elements in the function (i.e. the reverse image of f).
		 * 2) Iterate over all the cycle assignments found with the "getCycles" method above. For each one,
		 *  2.1) Assign a 0 or 1 to each of the entry points.
		 *  2.2) Start moving "upwards".
		 *   2.2.1) To move "upwards", first we consider whether the current node has a 0 or a 1.
		 *   2.2.2) We also need to consider the "parents" of this node.
		 *   2.2.3) If the current node is a LEAF, then the return value is 1.
		 *   2.2.4) Otherwise there are either one or two parents.
		 *   2.2.5) If the current node is a 1, each parent must be assigned zero (there is just one assignment).
		 *   2.2.6) If the current node is a 0, each parent can be zero or 1. There will be up to 4 sub-assignments.
		 *   2.2.7) For each parent and each possible value (0 or 1), move upwards recursively and store the result.
		 *   2.2.8) If there are two parents, the result of each sub-assignment is the product of each parent's result.
		 *   2.2.9) Then, at the end, the return value of this node is the sum of all the sub-assignments.
		 * 3) This gets a return value for each separate cycle. The product of all the numbers is the final result.
		 * 4) Use modding when appropriate. The full result is a huge number, probably on the order of 2^100000 or higher.
		 * 5) Be careful about recursion. It's likely to be deep.
		 */
		int N=20;
		List<BitSet> cycles=getCycles(N);
		System.out.println("Hay "+cycles.size()+" ciclos.");
		for (BitSet b:cycles)	{
			long data=b.cardinality()>0?b.toLongArray()[0]:0;
			String binaryString=Long.toBinaryString(data);
			String pad="0".repeat(N-binaryString.length());
			System.out.println(pad+binaryString);
		}
	}
}
