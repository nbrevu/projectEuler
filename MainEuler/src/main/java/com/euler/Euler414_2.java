package com.euler;

import java.math.BigInteger;
import java.util.Arrays;

import com.google.common.math.LongMath;

public class Euler414_2 {
	public static class KaprekarCounter	{
		private final static int[] BUFFER=new int[5];
		private final int b;
		private final int[][] counters;
		public KaprekarCounter(int b)	{
			this.b=b;
			counters=new int[b][];
			for (int y=0;y<b;++y) counters[y]=new int[y+1];
		}
		public long getKaprekarSum()	{
			/*
			 * We need to remove one because of the specific Kaprekar constant, where the result is 0 despite belonging to a group where
			 * the rest of the elements would have 1. So we start with -1 instead of 0.
			 * 
			 * It so happens that every single sum for a given base DOES fit in a long. The total sum has 21 digits, so it does not, though.
			 */
			long result=-1;
			for (int y=1;y<b;++y) for (int x=0;x<=y;++x) result+=counters[y][x]*countCases(x,y);
			return result;
		}
		public void fillKaprekarCounters()	{
			counters[2*b/3][b/3]=1;	// Kaprekar constant!
			for (int y=1;y<b;++y) for (int x=0;x<=y;++x) fillKaprekarCounterRecursive(x,y);
		}
		private void fillKaprekarCounterRecursive(int x,int y)	{
			if (counters[y][x]!=0) return;
			if (x==0)	{
				BUFFER[0]=y-1;
				BUFFER[1]=b-1;
			}	else	{
				BUFFER[0]=y;
				BUFFER[1]=x-1;
			}
			BUFFER[2]=b-1;
			BUFFER[3]=b-x-1;
			BUFFER[4]=b-y;
			Arrays.sort(BUFFER);
			int newY=BUFFER[4]-BUFFER[0];
			int newX=BUFFER[3]-BUFFER[1];
			fillKaprekarCounterRecursive(newX,newY);
			counters[y][x]=1+counters[newY][newX];
		}
		public long countCases(int x,int y)	{
			if ((x<0)||(y<0)||(y<x)) throw new RuntimeException("No.");
			if (x==0)	{
				if (y==0) return b;	// Special case 0: A=B=C=D=E, every digit equal. Doesn't actually count.
				else return count0X(y);
			}	else if (x==y) return countXX(x);
			else return countXY(x,y);
		}
		private long count0X(int x)	{
			/*
			 * Case 1: A=B=C=D<E: (b-x)*5.
			 * Case 2: A<B=C=D=E: (b-x)*5.
			 * Case 3: A<B=C=D<E: (b-x)*(x-1)*20.
			 */
			long baseComb=b-x;
			long centerComb=x-1;
			return baseComb*(10+20*centerComb);
		}
		private long countXX(int x)	{
			/*
			 * Case 13: A=B=C<D=E: (b-x)*10.
			 * Case 14: A=B<C=D=E: (b-x)*10.
			 * Case 15: A=B<C<D=E: (b-x)*(x-1)*30.
			 */
			long baseComb=b-x;
			long centerComb=x-1;
			return baseComb*(20+30*centerComb);
		}
		private long countXY(int x,int y)	{
			/*
			 * Case 4: A=B=C<D<E: (b-y)*20.
			 * Case 5: A=B<C<D<E: (b-y)*(x-1)*60.
			 * Case 6: A=B<C=D<E: (b-y)*30.
			 * Case 7: A<B=C<D<E: (b-y)*(y-x-1)*60.
			 * Case 8: A<B<C=D<E: (b-y)*(y-x-1)*60.
			 * Case 9: A<B<C<D=E: (b-y)*(x-1)*60.
			 * Case 10: A<B<C=D=E: (b-y)*20.
			 * Case 11: A<B=C<D=E: (b-y)*30.
			 * Case 12: A<B<C<D<E: (b-y)*(y-x-1)*(x-1)*120
			 */
			long baseComb=b-y;
			long diffComb=y-x-1;
			long centerComb=x-1;
			return baseComb*(100+120*(centerComb+diffComb+diffComb*centerComb));
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger result=BigInteger.ZERO;
		for (int b=15;b<=1803;b+=6)	{
			KaprekarCounter counter=new KaprekarCounter(b);
			counter.fillKaprekarCounters();
			result=result.add(BigInteger.valueOf(counter.getKaprekarSum()));
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println(result.mod(BigInteger.valueOf(LongMath.pow(10l,18))));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
