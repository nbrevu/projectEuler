package com.euler;

import java.util.BitSet;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils.LongPair;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;

// This is crude and slow, but it works.
public class Euler513 {
	private final static long L=100000;
	private final static long N=L*L;
	
	private final static int FIRST_PRIME_SIZE=1_000_000_000;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		PrimeDecomposer primeDecomposer=new StandardPrimeDecomposer(FIRST_PRIME_SIZE);
		long result=0;
		SumSquareDecomposer decomposer=new SumSquareDecomposer();
		BitSet validMods=new BitSet();
		validMods.set(0);
		validMods.set(1);
		validMods.set(2);
		validMods.set(4);
		validMods.set(5);
		for (long i=1;i<=N;++i)	{
			if (!validMods.get((int)(i&7))) continue;
			DivisorHolder decomp=primeDecomposer.decompose(i);
			long factor=1;
			boolean doScramble=false;
			LongIntMap reduced=HashLongIntMaps.newMutableMap();
			boolean isValid=true;
			int expSum=0;
			for (LongIntCursor cursor=decomp.getFactorMap().cursor();cursor.moveNext();)	{
				long prime=cursor.key();
				int exp=cursor.value();
				if (prime==2)	{
					int halfExp=exp>>1;
					factor*=(1<<halfExp);
					doScramble=((exp&1)==1);
				}	else if ((prime&3)==1)	{
					reduced.addValue(prime,exp);
					expSum+=exp;
				}	else if ((exp&1)==1)	{
					isValid=false;
					break;
				}	else factor*=LongMath.pow(prime,exp>>1);
			}
			if ((!isValid)||(expSum<2)) continue;
			BaseSquareDecomposition decompXy=decomposer.getFor(reduced);
			if (doScramble) decompXy=decompXy.scramble();
			BaseSquareDecomposition decompAb=decompXy.scramble();
			for (LongPair ab:decompAb.getBaseCombinations())	{
				long a=ab.x*factor;
				if (a==0) continue;
				long b=ab.y*factor;
				for (LongPair xy:decompXy.getBaseCombinations())	{
					long x=xy.x*factor;
					long y=xy.y*factor;
					long x2=x+x;
					long y2=y+y;
					if ((x2<=L)&&(b<=x2)&&(a+b>x2)) ++result;
					if ((x!=y)&&(y2<=L)&&(b<=y2)&&(a+b>y2)) ++result;
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
