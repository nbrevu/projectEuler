package com.euler;

import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.IntStream;

public class Euler669_3 {
	/*
	 * Ok, the "reflection" is the trick here. There is a certain recursion available.
	 * Let F(n,pos) be the element at position "pos" in the nth fib-number.
	 * Then, F(n,pos)=F(n-1,pos)+F(n-2,pos).
	 * BUT! If "pos" is above the maximum, we REFLECT.
	 * So, if F(4) has 13 elements, F(4,14)=F(4,13); F(4,15)=F(4,12); and so on.
	 * At the end we can put a 0, i.e., F(4,26)=F(4,1) and F(4,27)=F(4,0)=0.
	 * 
	 * This might not be enough, but we can STILL extend once more, it seems. The 0 replaces the next fib, but aside from that, the sequence restarts.
	 * Since fib(n+2) < 3*fib(n), this SHOULD be enough.
	 * It might be a good idea to generate the 20th list or so, so that the recursion ends a little sooner.
	 * I need to work a bit on zero-based indices as well.
	 */
	private static class FibonacciArrangementFinder	{
		private final int maxValue;
		private final int[] fibNumbers;
		public FibonacciArrangementFinder(int maxValue)	{
			this.maxValue=maxValue;
			fibNumbers=getFibonaccis(2*maxValue);
		}
		private static int[] getFibonaccis(int maxValue)	{
			IntStream.Builder fibValues=IntStream.builder();
			fibValues.accept(1);
			fibValues.accept(2);
			int prev2=1;
			int prev=2;
			for (;;)	{
				int curr=prev+prev2;
				if (curr>maxValue) break;
				fibValues.accept(curr);
				prev2=prev;
				prev=curr;
			}
			return fibValues.build().toArray();
		}
		public int[] getArrangement()	{
			int[] validSums=Arrays.copyOfRange(fibNumbers,fibNumbers.length-3,fibNumbers.length);
			int[] result=new int[maxValue];
			BitSet available=new BitSet(maxValue);
			available.set(1,maxValue);
			result[0]=maxValue;
			result[1]=validSums[0];	// I.e. fib(n-1).
			int lastNum=result[1];
			int lastIndex=2;
			for (int i=2;i<result.length;++i) switch (lastIndex)	{
				case 0:
				case 2:	{// Yep, exact same code.
					int next=validSums[1]-lastNum;
					if (!available.get(next)) throw new IllegalStateException("Es funktioniert nicht.");
					result[i]=next;
					lastNum=next;
					lastIndex=1;
					break;
				}	case 1:	{
					int next=validSums[2]-lastNum;
					lastIndex=2;
					if (!available.get(next))	{
						next=validSums[0]-lastNum;
						if (!available.get(next)) throw new IllegalStateException("Es funktioniert nicht.");
						lastIndex=0;
					}
					result[i]=next;
					lastNum=next;
					break;
				}
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		int[] values=new int[] {3,5,8,13,21,34,55,89,144,233,377,610};
		for (int n:values)	{
			FibonacciArrangementFinder finder=new FibonacciArrangementFinder(n);
			int[] arrangement=finder.getArrangement();
			System.out.println(Arrays.toString(arrangement));
		}
	}
}
