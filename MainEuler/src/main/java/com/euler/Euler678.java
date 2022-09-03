package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.stream.IntStream;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;

public class Euler678 {
	private final static long N=LongMath.pow(10l,18);
	
	private static long countSquareSums(DivisorHolder decomposition,int maxPower)	{
		IntStream.Builder builder=IntStream.builder();
		int start=3;
		int increment=1;
		for (LongIntCursor cursor=decomposition.getFactorMap().cursor();cursor.moveNext();)	{
			long prime=cursor.key();
			int power=cursor.value();
			long mod=prime%4;
			if (mod==3)	{
				if ((power%2)!=0)	{
					start=4;
					increment=2;
				}
			}	else if (mod==1) builder.accept(power);
		}
		if (start>maxPower) return 0;
		int[] exponents=builder.build().toArray();
		long result=0;
		for (int i=start;i<=maxPower;i+=increment)	{
			long b=1;
			for (int exp:exponents) b*=i*exp+1;
			/*
			 * If B is odd we always want to subtract one. If the power of 2 was even, there is a special case a^2+0^2. If it's odd, there is a 
			 * special case a^2+a^2. None of these must be considered.
			 */
			if ((b%2)==0) result+=b/2;
			else result+=(b-1)/2;
		}
		return result;
	}
	
	private static boolean isPerfectSquare(long in)	{
		long sq=LongMath.sqrt(in,RoundingMode.DOWN);
		return sq*sq==in;
	}
	
	private static long countCubeSums(long n,DivisorHolder decomposition)	{
		long minM=1+(long)Math.floor(Math.cbrt(n));
		long maxM=(long)Math.floor(Math.cbrt(4*n));
		long[] divisors=decomposition.getUnsortedListOfDivisors().toLongArray();
		Arrays.sort(divisors);
		int index1=Arrays.binarySearch(divisors,minM);
		if (index1<0) index1=-1-index1;
		int index2=Arrays.binarySearch(divisors,maxM);
		if (index2<0) index2=-1-index2;
		else ++index2;
		long result=0;
		for (int i=index1;i<index2;++i)	{
			long m=divisors[i];
			long mm=m*m;
			long l3=mm-n/m;
			if ((l3%3)!=0) continue;
			long l=l3/3;
			if ((mm>4*l)&&isPerfectSquare(mm-4*l)) ++result;
		}
		return result;
	}
	
	private static long countCubeSums(long n,DivisorHolder decomposition,int maxPower)	{
		long result=0;
		for (int i=4;i<=maxPower;++i) result+=countCubeSums(LongMath.pow(n,i),decomposition.pow(i));
		return result;
	}
	
	// This is very dirty, but sometimes the precision issues result in things like 5^(1/3)=4.99999... and so on.
	private static long root(long n,int index)	{
		long result=(long)Math.floor(Math.pow(n,1d/index));
		BigInteger bigResult=BigInteger.valueOf(result);
		BigInteger bigN=BigInteger.valueOf(n);
		if (bigResult.add(BigInteger.ONE).pow(index).compareTo(bigN)<=0) ++result;
		else if (bigResult.pow(index).compareTo(bigN)>0) --result;	// I don't think this happens but I want to be sure.
		return result;
	}
	
	// OLRAIT, right at the first try :).
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long maxPrime=(long)Math.ceil(Math.cbrt(N));
		int maxPower=LongMath.log2(N,RoundingMode.DOWN);
		long[] maxRoots=new long[1+maxPower];
		long[][] powersByExponent=new long[1+maxPower][];
		LongIntMap allPowers=HashLongIntMaps.newMutableMap();
		for (int i=3;i<=maxPower;++i)	{
			long maxRoot=root(N,i);
			maxRoots[i]=maxRoot;
			if (i>3) powersByExponent[i]=new long[(int)(1+maxRoot)];
		}
		long[] firstPrimes=Primes.firstPrimeSieve(maxPrime);
		long result=0;
		for (long i=1;i<=maxPrime;++i)	{
			long power=i*i;
			int maxExponent=maxRoots.length-1;
			for (int j=3;j<maxRoots.length;++j)	{
				if (i>maxRoots[j])	{
					maxExponent=j-1;
					break;
				}
				power*=i;
				allPowers.addValue(power,1);
				if (j>3) powersByExponent[j][(int)i]=power;
			}
			DivisorHolder decomposition=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
			result+=countSquareSums(decomposition,maxExponent)+countCubeSums(i,decomposition,maxExponent);
		}
		for (int i=4;i<powersByExponent.length;++i) for (int j=2;j<powersByExponent[i].length;++j)	{
			long p1=powersByExponent[i][j];
			if (2*p1>N) break;
			for (int k=j+1;k<powersByExponent[i].length;++k)	{
				long sum=p1+powersByExponent[i][k];
				if (sum>N) break;
				result+=allPowers.getOrDefault(sum,0);
			}
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
