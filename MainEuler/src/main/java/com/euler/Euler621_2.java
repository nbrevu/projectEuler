package com.euler;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler621_2 {
	public final static long LIMIT=17526l*LongMath.pow(10l,9);
	
	private static long get3TrianglePartitions(long n)	{
		return get3SquarePartitions(8*n+3);
	}
	
	private static long get3SquarePartitions(long n)	{
		long result=0;
		long s=LongMath.sqrt(n,RoundingMode.DOWN);
		List<Long> primes=Primes.listLongPrimes(s);
		for (long i=1;i<=s;++i)	{
			long operand=n-i*i;
			if ((i%100000)==0) System.out.println("i="+i+": extracting "+operand+"...");
			result+=get2SquarePartitions(operand,primes);
		}
		return result;
	}
	
	private static long extractPrime(long in,long factor,Map<Long,Integer> primes)	{
		if ((in%factor)==0)	{
			do	{
				in/=factor;
				EulerUtils.increaseCounter(primes,factor);
			}	while ((in%factor)==0);
			return in;
		}	else return in;
	}
	
	private static Map<Long,Integer> getPrimeFactors(long in,List<Long> primes)	{
		Map<Long,Integer> result=new HashMap<>();
		for (long p:primes)	{
			if (in<(p*p)) in=extractPrime(in,in,result);
			else in=extractPrime(in,p,result);
			if (in==1) return result;
		}
		if (in>1) in=extractPrime(in,in,result);
		return result;
	}
	
	private static long get2SquarePartitions(long n,List<Long> primes)	{
		Map<Long,Integer> primeFactors=getPrimeFactors(n,primes);
		long result=1;
		for (Map.Entry<Long,Integer> entry:primeFactors.entrySet())	{
			long p=entry.getKey();
			if (p==2l) continue;
			long m=p%4;
			if (m==1) result*=1+entry.getValue();
			else if ((entry.getValue()%2)==1) return 0l;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=get3TrianglePartitions(LIMIT);
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
