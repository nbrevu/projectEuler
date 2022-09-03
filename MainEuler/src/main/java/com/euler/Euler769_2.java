package com.euler;

import java.math.RoundingMode;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;

public class Euler769_2 {
	private static long trueMod(long x,long mod)	{
		return ((x%mod)+mod)%mod;
	}
	
	/*
	 * Initial solution: x=6, y=5, z=6.
	 * We perform some calculations on all the divisors of 4852224 = 2^9 * 3^6 * 13.
	 */
	public static void main(String[] args)	{
		long[] lastPrimes=Primes.lastPrimeSieve(4852224l);
		DivisorHolder holder=new DivisorHolder();
		holder.addFactor(2l,9);
		holder.addFactor(3l,6);
		holder.addFactor(13l,1);
		SortedSet<Long> decompositions=new TreeSet<>();
		decompositions.addAll(holder.getUnsortedListOfDivisors());
		for (long g:decompositions)	{
			/*
			for (long q=0;q<g;++q)	{
				long qq=q*q;
				if (((1872*qq)%g)!=0) continue;
				for (long p=0;p<g;++p)	{
					if (((72*p*q+1092*qq)%g)!=0) continue;
					if (((36*p*p+12*p*q+1417*qq)%g)==0) System.out.println(String.format("Solución encontrada para g=%d: p=%d, q=%d.",g,p,q));
				}
			}
			/*/
			long gmq=g/EulerUtils.gcd(1872,g);
			long a=1;
			long b=1;
			for (LongIntCursor cursor=DivisorHolder.getFromFirstPrimes(gmq,lastPrimes).getFactorMap().cursor();cursor.moveNext();)	{
				long prime=cursor.key();
				int pow=cursor.value();
				b*=LongMath.pow(prime,IntMath.divide(pow,2,RoundingMode.DOWN));
				if ((pow%2)==1) a*=prime;
			}
			long ab=a*b;
			for (long i=0;i<b;++i)	{
				long q=i*ab;
				long qq=q*q;
				if (((72l*q)%g)==0)	{
					if (((1092l*qq)%g)==0)	{	// Else, no solution. This doesn't actually happen.
						// We're better off iterating :|.
						for (long p=0;p<g;++p)	{
							long pp=p*p;
							if (((36*pp+12*p*q+1417*qq)%g)==0)	{
								System.out.println(String.format("Solución encontrada para g=%d: p=%d, q=%d.",g,p,q));
							}
						}
					}
				}	else	{
					long aa=(72*q)%g;
					long bb=trueMod(-1092*qq,g);
					long m=EulerUtils.gcd(aa,g);
					if ((bb%m)!=0) continue;	// No solutions.
					long gm=g/m;
					long p=(bb/m*EulerUtils.modulusInverse(aa/m,gm))%gm;
					if (((36*p*p+12*p*q+1417*qq)%g)==0) System.out.println(String.format("Solución encontrada para g=%d: p=%d, q=%d.",g,p,q));
				}
			}
			//*/
		}
	}
}
