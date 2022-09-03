package com.euler;

import java.util.Arrays;
import java.util.BitSet;

import com.euler.common.BitSetCursor;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

public class Euler797_4 {
	private final static int N=20;
	private final static long MOD=1_000_000_007l;
	
	/*
	562650172
	Elapsed 26.5029584 seconds.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] primes=Primes.firstPrimeSieve((long)N);
		long[] values=new long[1+N];
		long[] primitiveInverses=new long[1+N];
		values[1]=2l;
		primitiveInverses[1]=1l;
		long lastPower=1l;
		long[] addends=new long[1+N];
		BitSet present=new BitSet(N);
		for (int i=2;i<=N;++i)	{
			DivisorHolder decomposition=DivisorHolder.getFromFirstPrimes(i,primes);
			lastPower=(2*lastPower+1)%MOD;
			long primitive=lastPower;
			LongSet divisors=decomposition.getUnsortedListOfDivisors();
			for (LongCursor cursor=divisors.cursor();cursor.moveNext();)	{
				int div=(int)cursor.elem();
				if (div!=i) primitive=(primitive*primitiveInverses[div])%MOD;
			}
			System.out.println("P("+i+")="+primitive+".");
			primitiveInverses[i]=EulerUtils.modulusInverse(primitive,MOD);
			for (LongCursor cursor=divisors.cursor();cursor.moveNext();)	{
				long div=cursor.elem();
				/*-
				 * We have a number x and a divisor d. The limit is N.
				 * Let A be a number such that gcd(x,A)=d.
				 * Then the lcm of x and A is A*x/d.
				 * Surely we would need A*x/d<=N.
				 * Therefore A<=N*d/x.
				 */
				long maxN=(N*div)/i;
				maxN-=maxN%div;
				// I believe that this is right but the "wrong order" is fucking with me.
				for (long j=(int)maxN;j>=div;j-=div)	{
					long g=EulerUtils.gcd(i,j);
					if (g!=div) continue;
					int k=(int)(i*j/g);
					System.out.println(String.format("Añado f(%d)*P(%d) a f(%d)...",j,i,k));
					addends[k]=(addends[k]+values[(int)j]*primitive)%MOD;
					present.set(k);
				}
			}
			for (BitSetCursor c=new BitSetCursor(present,true);c.moveNext();)	{
				int k=c.elem();
				values[k]=(values[k]+addends[k])%MOD;
				addends[k]=0l;
			}
			System.out.println("Después del paso "+i+", éste es el array: ");
			System.out.println(Arrays.toString(values));
			/*-
			for (int j=N;j>=1;--j)	{
				long prod=i*(long)j;
				long k=prod/EulerUtils.gcd(i,j);
				if (k<=N) values[(int)k]=(values[(int)k]+values[j]*primitive)%MOD;
			}
			*/
		}
		long result=0l;
		for (int i=1;i<=N;++i) result+=values[i];
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
