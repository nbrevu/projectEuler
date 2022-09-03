package com.euler;

import java.util.Arrays;

import com.koloboke.collect.map.LongLongCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler413_2 {
	private final static int MAX_DIGITS=19;
	private final static int BASE=10;
	/*
	 * I really don't feel like using enums and shit for this. Low level all the way. Memory might be tight, and LongLongMaps are the least bad
	 * option. I expect LongLongMaps to use a lot of memory for the high digit counts.
	 * I'm not even going to use BitSets. Just longs, bit by bit. The way it was intended when the 8086 was born.
	 */
	private static class Encoder	{
		private final static int[][] COUNT_TRANSITIONS=new int[][] {{0,1,2},{1,2,2},{2,2,2}};
		private final int numDigits;
		// Results after multiplying times 10, cached for quick reuse.
		private final int[] digitTransitions;
		// Preallocated for speed. Fuck it, if we're going low level, WE'RE GOING LOW LEVEL.
		private final int[] aux1,aux2,aux3;
		private final long endCounter;
		public Encoder(int numDigits)	{
			this.numDigits=numDigits;
			digitTransitions=new int[numDigits];
			for (int i=0;i<numDigits;++i) digitTransitions[i]=(i*BASE)%numDigits;
			aux1=new int[1+numDigits];
			aux2=new int[1+numDigits];
			aux3=new int[1+numDigits];
			endCounter=1l<<(2*numDigits);
		}
		private LongLongMap getInitialDigits()	{
			LongLongMap result=HashLongLongMaps.newMutableMap();
			for (int i=1;i<BASE;++i)	{
				// No need to calculate the array specifically, it's just all zeroes except for one bit.
				int target=i%numDigits;
				long index=1l<<(2*target);
				if (target==0) index+=endCounter;
				result.addValue(index,1l,0l);
			}
			return result;
		}
		/*
		 * Updated internal state: there are [numDigits+1] "tri-bytes". The first [0,numDigits) count the amount of occurrences in the strings
		 * that end in the current last digit. And then, the "tri-byte" at [numDigits] represents the count of zeroes in the history of this
		 * string.
		 */
		private void decode(long value)	{
			// Always stores the result in aux1.
			for (int i=0;i<=numDigits;++i) if ((value&(1l<<(2*i+1)))!=0) aux1[i]=2;
			else aux1[i]=((value&(1l<<(2*i)))==0)?0:1;
		}
		private void multiply()	{
			// Always moves the result from aux1 into aux2;
			Arrays.fill(aux2,0);
			for (int i=0;i<numDigits;++i)	{
				int target=digitTransitions[i];
				aux2[target]=COUNT_TRANSITIONS[aux2[target]][aux1[i]];
			}
			aux2[numDigits]=aux1[numDigits];
		}
		private void getFinalSummary(int digitToAdd)	{
			Arrays.fill(aux3,0);
			// First store the new digit.
			int target=digitToAdd%numDigits;
			aux3[target]=1;
			// And finally the transformed result from aux2.
			for (int i=0;i<numDigits;++i)	{
				target=(i+digitToAdd)%numDigits;
				aux3[target]=COUNT_TRANSITIONS[aux2[i]][aux3[target]];
			}
			// Update the special counter as well.
			aux3[numDigits]=COUNT_TRANSITIONS[aux2[numDigits]][aux3[0]];
		}
		private boolean isResultFeasible()	{
			return aux3[numDigits]<2;
		}
		private long encode()	{
			// Always uses the result from aux3.
			long result=0l;
			for (int i=numDigits;i>=0;--i)	{
				result<<=2;
				result+=aux3[i];
			}
			return result;
		}
		private void evolveCase(long in,long weight,LongLongMap result)	{
			// Store result in aux1.
			decode(in);
			// Update aux2.
			multiply();
			for (int i=0;i<BASE;++i)	{
				getFinalSummary(i);
				if (isResultFeasible()) result.addValue(encode(),weight,0l);
			}
		}
		private LongLongMap evolveGeneration(LongLongMap currentGen)	{
			LongLongMap result=HashLongLongMaps.newMutableMap();
			for (LongLongCursor cursor=currentGen.cursor();cursor.moveNext();) evolveCase(cursor.key(),cursor.value(),result);
			return result;
		}
		public long countOneChildNumbers()	{
			LongLongMap currentGen=getInitialDigits();
			for (int i=2;i<=numDigits;++i) currentGen=evolveGeneration(currentGen);
			long result=0l;
			for (LongLongCursor cursor=currentGen.cursor();cursor.moveNext();) if ((cursor.key()&endCounter)!=0l) result+=cursor.value();
			return result;
		}
	}
	
	private static long countOneChildNumbers(int numDigits)	{
		return new Encoder(numDigits).countOneChildNumbers();
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0l;
		for (int i=1;i<=MAX_DIGITS;++i) result+=countOneChildNumbers(i);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
