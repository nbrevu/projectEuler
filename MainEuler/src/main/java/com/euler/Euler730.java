package com.euler;

import java.math.BigInteger;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.LongPair;
import com.euler.common.EulerUtils.Pair;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler730 {
	private final static long N=LongMath.pow(10l,6);
	private final static long M=10l;
	
	private static long pow(long b,int e)	{
		long result=b;
		for (int i=2;i<=e;++i) result*=b;
		return result;
	}
	
	private static LongPair hermiteAlgorithmWithoutOverflow(long p)	{
		if (p<Integer.MAX_VALUE) return EulerUtils.hermiteAlgorithm(p);
		Pair<BigInteger,BigInteger> bigResult=EulerUtils.hermiteAlgorithm(BigInteger.valueOf(p));
		return LongPair.sorted(bigResult.first.longValueExact(),bigResult.second.longValueExact());
	}
	private static BaseSquareDecomposition hermiteAlgorithm(long p)	{
		return new BaseSquareDecomposition(hermiteAlgorithmWithoutOverflow(p));
	}
	
	// This is not the same as in problem 264! In this case we might "miss" intermediate values.
	private static class SquareSumFinder	{
		private final static BaseSquareDecomposition[] POINTER=new BaseSquareDecomposition[1];	// Sometimes I hate Java.
		private final PrimeDecomposer decomposer;
		private final LongObjMap<BaseSquareDecomposition> decompositions;
		public SquareSumFinder(int N)	{
			decomposer=new StandardPrimeDecomposer(N);
			decompositions=HashLongObjMaps.newMutableMap();
		}
		private BaseSquareDecomposition getForPrime(long prime)	{
			return decompositions.computeIfAbsent(prime,Euler730::hermiteAlgorithm);
		}
		private BaseSquareDecomposition getForPrimePower(long prime,int exp)	{
			long n=prime;
			BaseSquareDecomposition base=getForPrime(n);
			POINTER[0]=base;
			for (int i=2;i<=exp;++i)	{
				n*=prime;
				POINTER[0]=decompositions.computeIfAbsent(n,(long unused)->POINTER[0].combineWith(base));
			}
			return POINTER[0];
		}
		public BaseSquareDecomposition getFor(long n)	{
			BaseSquareDecomposition result=decompositions.get(n);
			if (result!=null) return result;
			LongIntMap decomp=decomposer.decompose(n).getFactorMap();
			long additionalFactor=1l;
			boolean scramble=false;
			for (LongIntCursor cursor=decomp.cursor();cursor.moveNext();) if (cursor.key()==2l)	{
				int exp=cursor.value();
				scramble=((exp&1)!=0);
				additionalFactor*=(1<<(exp>>1));
			}	else if ((cursor.key()%4)==3)	{
				int exp=cursor.value();
				if ((exp&1)!=0) return BaseSquareDecomposition.EMPTY;
				else additionalFactor*=pow(cursor.key(),exp>>1);
			}	else	{
				BaseSquareDecomposition tmpResult=getForPrimePower(cursor.key(),cursor.value());
				if (result==null) result=tmpResult;
				else result=result.combineWith(tmpResult);
			}
			if (result==null) return scramble?new BaseSquareDecomposition(additionalFactor,additionalFactor):BaseSquareDecomposition.EMPTY;
			if (additionalFactor>1) result=result.scale(additionalFactor);
			if (scramble) result=result.scramble();
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SquareSumFinder sumDecomposer=new SquareSumFinder((int)N);
		long maxR=N/2;
		long result=0;
		for (long r=2;r<=maxR;++r)	{
			System.out.println(r+"...");
			long r2=r*r;
			for (long k=0;k<=M;++k)	{
				long sum=r2-k;
				if (sum<2) break;
				if (((sum&3)==3)) continue;
				BaseSquareDecomposition squareSums=sumDecomposer.getFor(sum);
				for (LongPair pair:squareSums.getBaseCombinations())	{
					if (pair.x==0) continue;
					long n=pair.x+pair.y+r;
					if (n<=N)	{
						long g=EulerUtils.gcd(pair.x,pair.y);
						if ((g==1)||(EulerUtils.gcd(g,r)==1)) ++result;
					}
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
