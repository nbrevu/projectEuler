package com.euler;

import com.google.common.math.LongMath;

public class Euler720_4 {
	private final static int MAX_POWER=25;
	private final static long MOD=LongMath.pow(10l,9)+7l;
	
	private final static int[] CASE_FOR_2=new int[] {1,3,2,4};
	
	private static class FactoradicHelper	{
		/*
		 * This class uses a recursive bin structure to count as efficiently as possible the amount of remaining numbers in a given set.
		 * It has two operations: remove a number and count the amount of numbers before a given value. It's like a form a specialised tree.
		 */
		private static interface Bin	{
			public void removeNumber(int n);
			public int countBefore(int value);
			public int countAll();
		}
		
		private static class LeafBin implements Bin	{
			public final int value;
			public boolean isPresent;
			public LeafBin(int value)	{
				this.value=value;
				isPresent=true;
			}
			@Override
			public void removeNumber(int n)	{
				// If we got here, it's assumed that n==value.
				isPresent=false;
			}
			@Override
			public int countBefore(int number)	{
				return (isPresent&&(number>value))?1:0;
			}
			@Override
			public int countAll()	{
				return isPresent?1:0;
			}
		}
		private static class IntermediateBin implements Bin	{
			private final int binLimit;
			private final Bin lowerBin;
			private final Bin upperBin;
			private int currentCount;
			public IntermediateBin(int start,int end)	{
				int halfLength=(end-start)/2;
				binLimit=start+halfLength;
				if (halfLength==1)	{
					lowerBin=new LeafBin(start);
					upperBin=new LeafBin(binLimit);
				}	else	{
					lowerBin=new IntermediateBin(start,binLimit);
					upperBin=new IntermediateBin(binLimit,end);
				}
				currentCount=end-start;
			}
			@Override
			public void removeNumber(int n) {
				// Assuming start<=n<end.
				--currentCount;
				((binLimit>=n)?lowerBin:upperBin).removeNumber(n);
			}
			@Override
			public int countBefore(int value) {
				if (value<binLimit) return lowerBin.countBefore(value);
				else if (value==binLimit) return lowerBin.countAll();
				else return lowerBin.countAll()+upperBin.countBefore(value);
			}
			@Override
			public int countAll()	{
				return currentCount;
			}
		}
		private Bin higherLevelBin;
		public FactoradicHelper(int limit)	{
			higherLevelBin=new IntermediateBin(1,limit);
		}
		public void removeNumber(int n)	{
			higherLevelBin.removeNumber(n);
		}
		public int countBefore(int n)	{
			return higherLevelBin.countBefore(n);
		}
	}
	
	private static int[] getNextCase(int[] previous)	{
		int N=previous.length;
		int[] result=new int[2*N];
		for (int i=0;i<N-1;++i) result[i]=2*previous[i]-1;
		result[N-1]=2;
		int offset1=2*N+1;
		int offset2=2*N-1;
		for (int i=N;i<result.length;++i) result[i]=offset1-result[offset2-i];
		return result;
	}
	
	private static int[] getFactoradicRepresentation(int[] array)	{
		int N=array.length;
		int[] result=new int[N];
		FactoradicHelper remaining=new FactoradicHelper(N);
		for (int i=0;i<array.length;++i)	{
			result[i]=remaining.countBefore(array[i]);
			remaining.removeNumber(array[i]);
		}
		return result;
	}
	
	private static long calculateFactoradicIndex(int[] array,long mod)	{
		int[] factoradic=getFactoradicRepresentation(array);
		long currentValue=1;	// The first index is 1, not 0.
		long currentFactorial=1;
		int N=array.length;
		int offset=N-1;
		for (int i=1;i<N;++i)	{
			currentFactorial*=i;
			currentFactorial%=mod;
			currentValue+=factoradic[offset-i]*currentFactorial;
			currentValue%=mod;
		}
		return currentValue;
	}
	
	public static void main(String[] args) 	{
		long tic=System.nanoTime();
		int currentSize=2;
		int[] currentArray=CASE_FOR_2;
		for (++currentSize;currentSize<=MAX_POWER;++currentSize) currentArray=getNextCase(currentArray);
		long result=calculateFactoradicIndex(currentArray,MOD);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
