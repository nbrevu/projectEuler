package com.euler;

import java.util.Arrays;

import com.euler.common.EulerUtils;

public class Euler336 {
	// This is admittedly a VERY POOR implementation.
	private final static int LIMIT=11;
	
	private final static int POSITION=2010;
	
	private static int[] getFirstArray(int size)	{
		int[] result=new int[size];
		for (int i=0;i<size;++i) result[i]=i;
		return result;
	}
	
	private static int[] getFactorials(int limit)	{
		int[] result=new int[limit];
		result[0]=1;
		for (int i=1;i<limit;++i) result[i]=result[i-1]*i;
		return result;
	}
	
	private final static int[] FACTORIALS=getFactorials(LIMIT+1);
	
	private static int getLexicographicalPosition(int[] perm)	{
		int result=0;
		for (int j=0;j<perm.length-1;++j)	{
			int k=0;
			for (int i=j+1;i<perm.length;++i) if (perm[i]<perm[j]) ++k;
			result+=k*FACTORIALS[perm.length-1-j];
		}
		return result;
	}
	
	// This is the kind of stupid shit that the lack of Pair forces us to do.
	private static int[] getFirstOutOfOrder(int[] perm)	{
		for (int i=0;i<perm.length;++i) if (perm[i]!=i)	{
			int[] result=new int[2];
			result[0]=i;
			for (int j=i+1;j<perm.length;++j) if (perm[j]==i)	{
				result[1]=j;
				return result;
			}
		}
		return null;
	}
	
	private static int[] copyAndReverse(int[] array,int firstToReverse)	{
		int[] result=new int[array.length];
		for (int i=0;i<firstToReverse;++i) result[i]=array[i];
		for (int j=firstToReverse;j<array.length;++j) result[j]=array[array.length-1+firstToReverse-j];
		return result;
	}
	
	private static int refillCounter(int[] counters,int[] permutation)	{
		int index=getLexicographicalPosition(permutation);
		if (counters[index]==-1)	{
			int[] fooo=getFirstOutOfOrder(permutation);
			if (fooo==null) counters[index]=0;
			else	{
				int[] nextStep;
				if (fooo[1]==permutation.length-1) nextStep=copyAndReverse(permutation,fooo[0]);
				else nextStep=copyAndReverse(permutation,fooo[1]);
				int nextIndex=refillCounter(counters,nextStep);
				counters[index]=1+counters[nextIndex];
			}
		}
		return index;
	}
	
	public static void main(String[] args)	{
		int[] array11=getFirstArray(LIMIT);
		int[] counters=new int[FACTORIALS[LIMIT]];
		Arrays.fill(counters,-1);
		int maximum=0;
		do	{
			int index=refillCounter(counters,array11);
			maximum=Math.max(maximum,counters[index]);
		}	while (EulerUtils.nextPermutation(array11));
		for (int i=0;i<counters.length;++i) if (counters[i]==-1) throw new IllegalArgumentException("Mierda.");
		int counter=0;
		for (int i=0;i<counters.length;++i) if (counters[i]==maximum)	{
			++counter;
			// I'm getting CAGBIHEFDKJ instead of CAGBIHEFJDK and I don't know why...
			if (counter==POSITION)	{
				System.out.print(""+counter+": ");
				int[] newArray=getFirstArray(LIMIT);
				for (int j=1;j<i;++j) EulerUtils.nextPermutation(newArray);
				for (int j=0;j<LIMIT;++j) System.out.print((char)('A'+newArray[j]));
				System.out.println();
			}
		}
	}
}
