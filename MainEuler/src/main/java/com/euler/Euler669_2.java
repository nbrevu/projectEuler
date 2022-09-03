package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler669_2 {
	/*-
	[3, 2, 1]
	[5, 3, 2, 1, 4]
	[8, 5, 3, 2, 6, 7, 1, 4]
	[13, 8, 5, 3, 10, 11, 2, 6, 7, 1, 4, 9, 12]
	[21, 13, 8, 5, 16, 18, 3, 10, 11, 2, 19, 15, 6, 7, 14, 20, 1, 12, 9, 4, 17]
	[34, 21, 13, 8, 26, 29, 5, 16, 18, 3, 31, 24, 10, 11, 23, 32, 2, 19, 15, 6, 28, 27, 7, 14, 20, 1, 33, 22, 12, 9, 25, 30, 4, 17]
	[55, 34, 21, 13, 42, 47, 8, 26, 29, 5, 50, 39, 16, 18, 37, 52, 3, 31, 24, 10, 45, 44, 11, 23, 32, 2, 53, 36, 19, 15, 40, 49, 6, 28, 27, 7, 48, 41, 14, 20, 35, 54, 1, 33, 22, 12, 43, 46, 9, 25, 30, 4, 17, 38, 51]
	[89, 55, 34, 21, 68, 76, 13, 42, 47, 8, 81, 63, 26, 29, 60, 84, 5, 50, 39, 16, 73, 71, 18, 37, 52, 3, 86, 58, 31, 24, 65, 79, 10, 45, 44, 11, 78, 66, 23, 32, 57, 87, 2, 53, 36, 19, 70, 74, 15, 40, 49, 6, 83, 61, 28, 27, 62, 82, 7, 48, 41, 14, 75, 69, 20, 35, 54, 1, 88, 56, 33, 22, 67, 77, 12, 43, 46, 9, 80, 64, 25, 30, 59, 85, 4, 51, 38, 17, 72]
	 */
	private static class FibonacciArrangementFinder	{
		private final int maxValue;
		private final int[] fibNumbers;
		public FibonacciArrangementFinder(int maxValue)	{
			this.maxValue=maxValue;
			fibNumbers=getFibonaccis(2*maxValue-1);
		}
		private static int[] getFibonaccis(int maxValue)	{
			List<Integer> fibValues=new ArrayList<>();
			fibValues.add(1);
			fibValues.add(2);
			int prev2=1;
			int prev=2;
			for (;;)	{
				int curr=prev+prev2;
				if (curr>maxValue) break;
				fibValues.add(curr);
				prev2=prev;
				prev=curr;
			}
			return fibValues.stream().mapToInt(Integer::intValue).toArray();
		}
		private List<List<Integer>> getValidAugends()	{
			List<List<Integer>> result=new ArrayList<>(1+maxValue);
			for (int i=0;i<=maxValue;++i) result.add(new ArrayList<>());
			for (int i=1;i<=maxValue;++i)	{
				List<Integer> thisList=result.get(i);
				for (int fib:fibNumbers)	{
					int diff=fib-i;
					if (diff<=0) continue;
					else if (diff>maxValue) break;
					thisList.add(diff);
				}
			}
			return result;
		}
		private List<Integer> getSingleValuedIndices(List<List<Integer>> validAugends)	{
			List<Integer> result=new ArrayList<>();
			for (int i=1;i<validAugends.size();++i) if (validAugends.get(i).size()==1) result.add(i);
			return result;
		}
		public int[] getArrangement()	{
			List<List<Integer>> validAugends=getValidAugends();
			List<Integer> singleValuedIndices=getSingleValuedIndices(validAugends);
			if (singleValuedIndices.size()>2) throw new IllegalStateException("That's unpossible!");
			else if (singleValuedIndices.size()==0) throw new IllegalStateException("I'm not sure how to work with this.");
			int[] result=new int[maxValue];
			IntSet remainingNumbers=HashIntSets.newMutableSet();
			for (int i=1;i<=maxValue;++i) remainingNumbers.add(i);
			for (int n:singleValuedIndices)	{
				remainingNumbers.removeInt(n);
				result[0]=n;
				if (fillRecursive(result,validAugends,remainingNumbers,1)) return result;
				remainingNumbers.add(n);
			}
			throw new IllegalStateException("Can't find an arrangement.");
		}
		private boolean fillRecursive(int[] result,List<List<Integer>> validAugends,IntSet remainingNumbers,int currentIndex)	{
			int lastValue=result[currentIndex-1];
			for (int augend:validAugends.get(lastValue)) if (remainingNumbers.contains(augend))	{
				remainingNumbers.removeInt(augend);
				result[currentIndex]=augend;
				if (remainingNumbers.isEmpty()) return true;
				if (fillRecursive(result,validAugends,remainingNumbers,1+currentIndex)) return true;
				remainingNumbers.add(augend);
			}
			return false;
		}
	}
	
	public static void main(String[] args)	{
		int[] values=new int[] {3,5,8,13,21,34,55,89,144,233};
		for (int n:values)	{
			FibonacciArrangementFinder finder=new FibonacciArrangementFinder(n);
			int[] arrangement=finder.getArrangement();
			System.out.println(Arrays.toString(arrangement));
		}
	}
}
