package com.euler;

import java.util.Arrays;

import com.euler.common.EulerUtils;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler758 {
	private final static int SHIFT=30;
	private final static int MASK=(1<<SHIFT)-1;
	
	private static long intPairToLong(long a,long b)	{
		return (a<<SHIFT)+b;
	}
	
	private static void longToIntPair(long id,int[] result)	{
		result[0]=(int)(id>>SHIFT);
		result[1]=(int)(id&MASK);
	}
	
	private static class StepCounterManager	{
		private final int s;
		private final int m;
		private final int[] intPlaceholder;
		private final long[] placeholder;
		public StepCounterManager(int s,int m)	{
			this.s=s;
			this.m=m;
			intPlaceholder=new int[2];
			placeholder=new long[6];	// I believe that in the actual problem, we never get to 6; 5 is the maximum, maybe 4. But better be safe.
		}
		public boolean isFinal(long config)	{
			longToIntPair(config,intPlaceholder);
			return (intPlaceholder[0]==1)||(intPlaceholder[1]==1);
		}
		public long[] children(long config)	{
			longToIntPair(config,intPlaceholder);
			int a=intPlaceholder[0];
			int b=intPlaceholder[1];
			Arrays.fill(placeholder,-1l);
			int currentIndex=0;
			if (a>0)	{
				if (b<m)	{
					// Pouring S into M.
					int maxToPour=Math.min(a,m-b);
					long child=intPairToLong(a-maxToPour,b+maxToPour);
					placeholder[currentIndex]=child;
					++currentIndex;
				}
				// Pouring S into L.
				long child=intPairToLong(0,b);
				placeholder[currentIndex]=child;
				++currentIndex;
			}
			if (b>0)	{
				if (a<s)	{
					// Pouring M into S.
					int maxToPour=Math.min(s-a,b);
					long child=intPairToLong(a+maxToPour,b-maxToPour);
					placeholder[currentIndex]=child;
					++currentIndex;
				}
				// Pouring M into L.
				long child=intPairToLong(a,0);
				placeholder[currentIndex]=child;
				++currentIndex;
			}
			if (a<s)	{
				// Pouring L into S.
				long child=intPairToLong(s,b);
				placeholder[currentIndex]=child;
				++currentIndex;
			}
			if (b<m)	{
				// Pouring L into M.
				long child=intPairToLong(a,m);
				placeholder[currentIndex]=child;
				++currentIndex;
			}
			return placeholder;
		}
	}
	
	private static int countSteps(int s,int m)	{
		int currentStep=0;
		LongSet currentGen=HashLongSets.newMutableSet();
		StepCounterManager manager=new StepCounterManager(s,m);
		currentGen.add(intPairToLong(s,m));
		LongSet visited=HashLongSets.newMutableSet();
		for (;;)	{
			if (currentGen.isEmpty()) throw new IllegalStateException("KHÉ?");
			++currentStep;
			LongSet nextGen=HashLongSets.newMutableSet();
			for (LongCursor cursor=currentGen.cursor();cursor.moveNext();)	{
				long config=cursor.elem();
				long[] children=manager.children(config);
				for (long c:children) if (c<0) break;
				else if (visited.contains(c)) continue;
				else if (manager.isFinal(c)) return currentStep;
				else	{
					visited.add(c);
					nextGen.add(c);
				}
			}
			currentGen=nextGen;
		}
	}
	
	private static void printFullInfo(int a,int b)	{
		long[] coeffs=EulerUtils.extendedGcd(a,b).coeffs;
		int coef11,coef12,coef21,coef22;
		if (coeffs[0]<0)	{
			/*
			 * For example: (31,7) -> (-2,9) -> -2*31+9*7=1.
			 * The first sum is (7-2)*31=(31-9)*7+1
			 * The second sum is 9*7=2*31+1
			 */
			coef11=b+(int)coeffs[0];
			coef12=a-(int)coeffs[1];
			coef21=(int)coeffs[1];
			coef22=(int)-coeffs[0];
		}	else	{
			/*
			 * For example: (7,31) -> (9,-2) -> 9*7+(-2)*31=1.
			 * The first sum is 9*7=2*31+1.
			 * The second sum is (7-2)*31=(31-9)*7+1;
			 */
			coef11=(int)coeffs[0];
			coef12=(int)-coeffs[1];
			coef21=a+(int)coeffs[1];
			coef22=b-(int)coeffs[0];
		}
		String sum1=String.format("%d*%d=%d*%d+1",coef11,a,coef12,b);
		String sum2=String.format("%d*%d=%d*%d+1",coef21,b,coef22,a);
		int minSum=Math.min(coef11+coef12,coef21+coef22);
		int expectedResult=2*(minSum-1);
		int realResult=countSteps(a,b);
		System.out.println("For ("+a+","+b+"):");
		System.out.println("\t"+Arrays.toString(coeffs));
		System.out.println("\t"+sum1);
		System.out.println("\t"+sum2);
		System.out.println("\tThe amount of steps is "+realResult+" (expected="+expectedResult+").");	// YEP, confirmed :).
		boolean[] conditions=new boolean[2];
		conditions[0]=coeffs[0]<0;
		conditions[1]=(coef11+coef12)>(coef21+coef22);
		System.out.println("\tConditions: "+Arrays.toString(conditions));
		if (realResult!=expectedResult) System.out.println("Scheiße!!!!!");
		else if (conditions[0]!=conditions[1]) System.out.println("OH NEIN!!!!!");
	}
	
	public static void main(String[] args)	{
		for (int i=3;i<100;++i) for (int j=2;j<i;++j) if (EulerUtils.gcd(i,j)==1) printFullInfo(j,i);
	}
}
