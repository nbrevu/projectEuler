package com.euler;

import java.util.HashSet;
import java.util.Set;

import com.euler.common.EulerUtils;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class Euler635_2 {
	/*-
	 * This might not be correct... review.
	   N=2:
		Valid: 2.
		Total: 6.
		[[0 x 2], [1 x 2]]
		[[2] x 2]
	   
	   N=3:
	    Valid: 36.
		Total: 90.
		[[0, 1, 2]]
		[[1 x 3]]

	   N=5:
		Valid: 23400.
		Total: 113400.
		[[0 x 2, 1, 2 x 2], [0, 2 x 2, 3 x 2], [0 x 2, 1 x 2, 3], [1 x 2, 2, 3 x 2], [1, 3 x 2, 4 x 2], [0 x 2, 3 x 2, 4], [1 x 2, 2 x 2, 4], [2 x 2, 3, 4 x 2], [0, 1 x 2, 4 x 2], [0 x 2, 2, 4 x 2], [0, 1, 2, 3, 4]]
		[[1, 2 x 2] x 10, [1 x 5]]
		
	   N=7:
		Valid: 97637400.
		Total: 681080400.
		[[0 x 2, 2 x 2, 3 x 2, 4], [1, 2 x 2, 3 x 2, 5 x 2], [0, 1, 2, 3 x 2, 6 x 2], [1 x 2, 2 x 2, 3, 6 x 2], [0 x 2, 1, 2, 3 x 2, 5], [0 x 2, 2 x 2, 5, 6 x 2], [0, 1 x 2, 2 x 2, 3, 5], [0, 1 x 2, 2 x 2, 4 x 2], [0, 1, 2 x 2, 4, 6 x 2], [0 x 2, 1, 2 x 2, 3, 6], [0 x 2, 1, 2 x 2, 4, 5], [0 x 2, 1, 3, 5, 6 x 2], [0 x 2, 1, 4 x 2, 6 x 2], [1, 2 x 2, 3, 4 x 2, 5], [0 x 2, 1 x 2, 3 x 2, 6], [0 x 2, 1, 2, 3, 4 x 2], [0, 1 x 2, 2, 3 x 2, 4], [0, 2 x 2, 3 x 2, 5, 6], [0, 3 x 2, 5 x 2, 6 x 2], [1, 2 x 2, 3 x 2, 4, 6], [0, 1, 3 x 2, 4, 5 x 2], [1 x 2, 3 x 2, 4 x 2, 5], [1, 3 x 2, 4, 5, 6 x 2], [2, 3 x 2, 4 x 2, 6 x 2], [0 x 2, 1 x 2, 2, 5 x 2], [0, 1 x 2, 2, 5, 6 x 2], [0, 1 x 2, 3, 4, 6 x 2], [0, 1, 2 x 2, 5 x 2, 6], [0, 1, 3 x 2, 4 x 2, 6], [0, 2 x 2, 3, 4, 5 x 2], [0, 2, 3 x 2, 4 x 2, 5], [2 x 2, 3, 4, 5, 6 x 2], [0 x 2, 1 x 2, 3, 4, 5], [0 x 2, 2, 3, 4, 6 x 2], [0, 2 x 2, 3, 4 x 2, 6], [1, 2, 3, 5 x 2, 6 x 2], [0 x 2, 3, 4 x 2, 5 x 2], [0, 1, 2, 4 x 2, 5 x 2], [0, 3, 4 x 2, 5, 6 x 2], [1, 2, 4 x 2, 5, 6 x 2], [1 x 2, 2, 3 x 2, 5, 6], [0 x 2, 3 x 2, 4, 5, 6], [1 x 2, 2 x 2, 4, 5, 6], [1 x 2, 4, 5 x 2, 6 x 2], [0 x 2, 1 x 2, 2, 4, 6], [0, 1 x 2, 3, 5 x 2, 6], [0, 2, 4, 5 x 2, 6 x 2], [1 x 2, 2, 3, 4, 5 x 2], [2 x 2, 4 x 2, 5 x 2, 6], [0 x 2, 1, 4, 5 x 2, 6], [0 x 2, 2, 3, 5 x 2, 6], [0, 1 x 2, 4 x 2, 5, 6], [1 x 2, 2, 3, 4 x 2, 6], [0 x 2, 2, 4 x 2, 5, 6], [0, 1, 2, 3, 4, 5, 6], [1, 3, 4 x 2, 5 x 2, 6], [2, 3 x 2, 4, 5 x 2, 6]]
		[[1, 2 x 3] x 14, [1 x 3, 2 x 2] x 42, [1 x 7]]
	 */
	public static void main(String[] args)	{
		int N=7;
		int[] values=new int[2*N];
		for (int i=0;i<N;++i)	{
			values[2*i]=i;
			values[2*i+1]=i;
		}
		long permCount=0;
		long validPermCount=0;
		Set<Multiset<Integer>> sets=new HashSet<>();
		do	{
			int sum=0;
			for (int i=0;i<N;++i) sum+=values[i];
			if ((sum%N)==0)	{
				Multiset<Integer> multiset=HashMultiset.create();
				for (int i=0;i<N;++i) multiset.add(values[i]);
				sets.add(multiset);
				++validPermCount;
			}
			++permCount;
		}	while (EulerUtils.nextPermutation(values));
		System.out.println("Valid: "+validPermCount+".");
		System.out.println("Total: "+permCount+".");
		System.out.println(sets);
		Multiset<Multiset<Integer>> cardinalities=HashMultiset.create();
		for (Multiset<Integer> set:sets)	{
			Multiset<Integer> card=HashMultiset.create();
			set.forEachEntry((Integer obj,int count)->card.add(count));
			cardinalities.add(card);
		}
		System.out.println(cardinalities);
	}
}
