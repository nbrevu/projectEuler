package com.euler;

import java.math.BigInteger;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;
import com.euler.common.Primes;

public class Euler635_4 {
	// Es funktioniert :).
	public static void main(String[] args)	{
		int[] primes=Primes.listIntPrimesAsArray(100);
		BigCombinatorialNumberCache combis=new BigCombinatorialNumberCache(300);
		{
			BigInteger result=BigInteger.TWO;	// Result for n=2.
			for (int i=1;i<primes.length;++i)	{
				int p=primes[i];
				BigInteger n=combis.get(2*p,p);
				n=n.add(BigInteger.valueOf(2*p-2));
				n=n.divide(BigInteger.valueOf(p));
				result=result.add(n);
			}
			System.out.println(result);
			System.out.println(result.mod(BigInteger.valueOf(1_000_000_009l)));
		}
		{
			BigInteger result=BigInteger.valueOf(6l);	// Result for n=2.
			for (int i=1;i<primes.length;++i)	{
				int p=primes[i];
				BigInteger n=combis.get(3*p,p);
				n=n.add(BigInteger.valueOf(3*p-3));
				n=n.divide(BigInteger.valueOf(p));
				result=result.add(n);
			}
			System.out.println(result);
			System.out.println(result.mod(BigInteger.valueOf(1_000_000_009l)));
		}
	}
}
