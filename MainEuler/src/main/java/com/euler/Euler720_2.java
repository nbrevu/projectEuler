package com.euler;

import java.util.Arrays;
import java.util.BitSet;

public class Euler720_2 {
	private final static int SIZE=64;
	
	private static class UnpredictablePermutationGenerator	{
		private final int size;
		private final BitSet pendingNumbers;
		private final int[] result;
		public UnpredictablePermutationGenerator(int size)	{
			this.size=size;
			pendingNumbers=new BitSet(1+size);
			pendingNumbers.set(1,1+size);
			result=new int[size];
		}
		public int[] generate()	{
			return generateRecursive(0);
		}
		private int[] generateRecursive(int currentIndex)	{
			for (int i=pendingNumbers.nextSetBit(0);i>=0;i=pendingNumbers.nextSetBit(i+1))	{
				int i2=2*i;
				boolean isSuitable=true;
				for (int j=0;j<currentIndex;++j)	{
					int toLookFor=i2-result[j];
					if ((toLookFor>0)&&(toLookFor<=size)&&pendingNumbers.get(toLookFor))	{
						isSuitable=false;
						break;
					}
				}
				if (!isSuitable) continue;
				result[currentIndex]=i;
				if (currentIndex==size-1) return result;
				else	{
					pendingNumbers.clear(i);
					int[] recResult=generateRecursive(1+currentIndex);
					if (recResult!=null) return recResult;
					pendingNumbers.set(i);
				}
			}
			return null;
		}
	}
	
	// The run time gets incredibly high for N=7 (from about 2 seconds to *checks notes* 6356.7270432450005 seconds). 
	public static void main(String[] args) 	{
		long tic=System.nanoTime();
		for (int i=4;i<=SIZE;i+=i)	{
			int[] permutation=new UnpredictablePermutationGenerator(i).generate();
			System.out.println(Arrays.toString(permutation));
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
