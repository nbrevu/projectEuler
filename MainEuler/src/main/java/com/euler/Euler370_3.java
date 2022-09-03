package com.euler;

import java.math.RoundingMode;
import java.util.BitSet;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

public class Euler370_3 {
	//private final static long LIMIT=LongMath.pow(10l,6);
	private final static long LIMIT=25*LongMath.pow(10l,12);

	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long sum=LIMIT/3;	// Equilateral triangles.
		/*
		 * Given any denominator d, the smallest possible value comes from n=1. In this case we have triangles of size d^2, d*(d+1), (d+1)^2. So
		 * the total sum is d^2+(d^2+d)+(d^2+2d+1)=3d^2+3d+1. Therefore the maximum denominator D comes from the equation 3D^2+3D+1.
		 * In short, we can just use the limit D=sqrt(L/3), and then calculate the appropriate limit for n given each d.
		 * This limit comes from: d^2+d*(d+n)+(d+n)^2 <= L -> d^2+(d^2+nd)+(d^2+2nd+n^2) <= L -> d^2+3nd+n^2<=L -> n^2+3nd+d^2-L<=0 ->
		 * n<=(-3d\pmsqrt(9d^2-4(d^2-L)))/2 -> n<=(-3d\pmsqrt(4L-5d^2))/2.
		 * The other limit comes from n<=(phi-1)*d. 
		 */
		long limitD=(LongMath.sqrt(12*LIMIT-3,RoundingMode.DOWN)-3)/6;
		//long limitD=LongMath.sqrt(LIMIT/2,RoundingMode.DOWN);
		long[] lastPrimes=Primes.lastPrimeSieve(limitD);
		double phiMinus1=Math.sqrt(1.25)-0.5;
		for (long d=2;;++d)	{
			long limit1=(LongMath.sqrt(4*LIMIT-3*d*d,RoundingMode.DOWN)-3*d)/2l;
			long limit2=(long)Math.floor(d*phiMinus1);
			int limit=(int)Math.min(limit1,limit2);
			if (limit<1) break;
			BitSet validNums=new BitSet(1+limit);
			validNums.set(1,1+limit);
			if (lastPrimes[(int)d]!=0)	{
				LongSet primes=DivisorHolder.getFromFirstPrimes(d,lastPrimes).getFactorMap().keySet();
				for (LongCursor cursor=primes.cursor();cursor.moveNext();)	{
					int p=(int)cursor.elem();
					for (int i=p;i<=limit;i+=p) validNums.clear(i);
				}
			}
			for (int n=validNums.nextSetBit(0);n>=0;n=validNums.nextSetBit(n+1))	{
				long nd=n+d;
				long first=d*d+nd*d+nd*nd;
				sum+=LIMIT/first;
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");	// Elapsed 11892.002204748002 seconds. JAJA SI
	}
}
