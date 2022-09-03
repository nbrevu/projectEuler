package com.euler;

import java.util.Arrays;

import com.koloboke.collect.map.LongLongCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler413 {
	private final static int BASE=10;
	/*
	 * I really don't feel like using enums and shit for this. Low level all the way. Memory might be tight, and LongLongMaps are the least bad
	 * option. I expect LongLongMaps to use a lot of memory for the very high cases.
	 * Fuck it, I'm not even going to use BitSets. Just longs, bit by bit. The way it was intended when the 8086 was born.
	 */
	private static class Encoder	{
		private final static int[][] COUNT_TRANSITIONS=new int[][] {{0,1,2},{1,2,2},{2,2,2}};
		private final int numDigits;
		// Results after multiplying times 10, cached for quick reuse.
		private final int[] digitTransitions;
		// Preallocated for speed. Fuck it, if we're going low level, WE'RE GOING LOW LEVEL.
		private final int[] aux1,aux2,aux3;
		public Encoder(int numDigits)	{
			this.numDigits=numDigits;
			digitTransitions=new int[numDigits];
			for (int i=0;i<numDigits;++i) digitTransitions[i]=(i*BASE)%numDigits;
			aux1=new int[numDigits];
			aux2=new int[numDigits];
			aux3=new int[numDigits];
		}
		private LongLongMap getInitialDigits()	{
			LongLongMap result=HashLongLongMaps.newMutableMap();
			for (int i=1;i<BASE;++i)	{
				// No need to calculate the array specifically, it's just all zeroes except for one bit.
				int target=i%numDigits;
				long index=1l<<(2*target);
				result.addValue(index,1l,0l);
			}
			return result;
		}
		private void decode(long value)	{
			// Always stores the result in aux1.
			for (int i=0;i<numDigits;++i) if ((value&(1l<<(2*i+1)))!=0) aux1[i]=2;
			else aux1[i]=((value&(1l<<(2*i)))==0)?0:1;
		}
		private void multiply()	{
			// Always moves the result from aux1 into aux2;
			Arrays.fill(aux2,0);
			for (int i=0;i<numDigits;++i)	{
				int target=digitTransitions[i];
				aux2[target]=COUNT_TRANSITIONS[aux2[target]][aux1[i]];
			}
		}
		private void getFinalSummary(int digitToAdd)	{
			/*
			 * This is the method that does the "blending". It adds the value from aux1 (just decoded from the previous generation), plus the
			 * current digit, plus the result of adding the current digit to aux2. That is, if we are studying the number 41, and we are going to add
			 * the digit 3, we need to include all the previous substrings from 41 (stored in aux), plus the number 3, plus the substrings that end
			 * in 3, which are found by shifting in 3 units the array of aux2. aux2 corresponds to the number 410 and should not be counted!
			 * 
			 * The result of this method is always stored in aux3.
			 */
			// Include aux0...
			System.arraycopy(aux1,0,aux3,0,numDigits);
			// Then the new digit...
			int target=digitToAdd%numDigits;
			aux3[target]=COUNT_TRANSITIONS[1][aux3[target]];
			// And finally the transformed result from aux2.
			for (int i=0;i<numDigits;++i)	{
				target=(i+digitToAdd)%numDigits;
				aux3[target]=COUNT_TRANSITIONS[aux2[i]][aux3[target]];
			}
		}
		private boolean isResultFeasible()	{
			return aux3[0]<2;
		}
		private long encode()	{
			// Always uses the result from aux3.
			long result=0l;
			for (int i=numDigits-1;i>=0;--i)	{
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
			for (LongLongCursor cursor=currentGen.cursor();cursor.moveNext();)	{
				evolveCase(cursor.key(),cursor.value(),result);
			}
			return result;
		}
		public long countOneChildNumbers()	{
			LongLongMap currentGen=getInitialDigits();
			for (int i=2;i<=numDigits;++i) currentGen=evolveGeneration(currentGen);
			long result=0l;
			for (LongLongCursor cursor=currentGen.cursor();cursor.moveNext();) if ((cursor.key()&3l)==1l) result+=cursor.value();
			return result;
		}
	}
	
	private static long countOneChildNumbers(int numDigits)	{
		return new Encoder(numDigits).countOneChildNumbers();
	}
	
	public static void main(String[] args)	{
		/*
		 * OK, I see the problem now. When updating, I need to use only the strings that END in the previous character. I'm using all of them,
		 * which is obviously wrong since 101 doesn't have a "11" substring.
		 * Not back to square one, but a lot has to be redone. Mostly in the internal state.
		 */
		System.out.println(countOneChildNumbers(3));
	}
}
