package com.euler;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.OptionalInt;

import com.euler.common.CustomCombinationIterator;
import com.euler.common.EulerUtils;
import com.euler.common.Timing;
import com.euler.common.VariationIterator;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler93 {
	private static class IntRational	{
		public final int n;
		public final int d;
		public IntRational(int x)	{
			n=x;
			d=1;
		}
		public IntRational(int n,int d)	{
			int g=EulerUtils.gcd(n,d);
			this.n=n/g;
			this.d=d/g;
		}
	}
	
	private static enum Operation	{
		ADDITION	{
			@Override
			public IntRational apply(IntRational a,IntRational b)	{
				return new IntRational(a.n*b.d+a.d*b.n,a.d*b.d);
			}
		},	SUBTRACTION	{
			@Override
			public IntRational apply(IntRational a,IntRational b)	{
				return new IntRational(a.n*b.d-a.d*b.n,a.d*b.d);
			}
		},	PRODUCT	{
			@Override
			public IntRational apply(IntRational a,IntRational b)	{
				return new IntRational(a.n*b.n,a.d*b.d);
			}
		},	DIVISION	{
			@Override
			public IntRational apply(IntRational a,IntRational b)	{
				if (b.n==0) return null;
				else return new IntRational(a.n*b.d,a.d*b.n);
			}
		},	REV_SUBTRACTION	{
			@Override
			public IntRational apply(IntRational a,IntRational b)	{
				return new IntRational(a.d*b.n-a.n*b.d,a.d*b.d);
			}
		},	REV_DIVISION	{
			@Override
			public IntRational apply(IntRational a,IntRational b)	{
				if (a.n==0) return null;
				else return new IntRational(b.n*a.d,a.n*b.d);
			}
		};
		public abstract IntRational apply(IntRational a,IntRational b);
	}
	
	private static Operation[][] getAllOperatorCombinations()	{
		Operation[] operations=Operation.values();
		int N=operations.length;
		Operation[][] result=new Operation[N*N*N][3];
		int index=0;
		for (int[] array:new VariationIterator(3,N,false))	{
			Operation[] copying=result[index];
			for (int i=0;i<3;++i) copying[i]=operations[array[i]];
			++index;
		}
		return result;
	}
	
	private static class CalculationGenerator	{
		private final static Operation[][] COMBINATIONS=getAllOperatorCombinations();
		private static int getMaxGenerable(int[] values)	{
			IntSet generated=HashIntSets.newMutableSet();
			do for (Operation[] comb:COMBINATIONS)	{
				OptionalInt tmp=reduce(values,comb);
				if (tmp.isPresent()) generated.add(tmp.getAsInt());
			}	while (EulerUtils.nextPermutation(values));
			for (int i=1;;++i) if (!generated.contains(i)) return i-1;
		}
		private static OptionalInt reduce(int[] values,Operation[] operations)	{
			Deque<IntRational> digits=new ArrayDeque<>();
			for (int v:values) digits.add(new IntRational(v));
			for (int i=0;i<3;++i) {
				IntRational d1=digits.poll();
				IntRational d2=digits.poll();
				IntRational result=operations[i].apply(d1,d2);
				if (result==null) return OptionalInt.empty();
				digits.addFirst(result);
			}
			IntRational ratResult=digits.poll();
			if (ratResult.d!=1) return OptionalInt.empty();
			return OptionalInt.of(ratResult.n);
		}
	}
	
	private static String solve()	{
		int[] bestCombination=null;
		int bestValue=0;
		for (int[] digits:new CustomCombinationIterator(4,new int[] {1,2,3,4,5,6,7,8,9},false,true))	{
			int maxGenerable=CalculationGenerator.getMaxGenerable(digits);
			if (maxGenerable>bestValue)	{
				bestValue=maxGenerable;
				bestCombination=digits;
			}
		}
		Arrays.sort(bestCombination);
		StringBuilder sb=new StringBuilder();
		for (int i:bestCombination) sb.append(i);
		return sb.toString();
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler93::solve);
	}
}
