package com.euler;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.LongConvergents.LongConvergent;
import com.euler.common.LongFiniteContinuedFraction;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler785_9 {
	private final static int N=IntMath.pow(10,9);
	
	private final static BigInteger THREE=BigInteger.valueOf(3l);
	
	private static boolean isValid(DivisorHolder divs)	{
		for (LongCursor cursor=divs.getFactorMap().keySet().cursor();cursor.moveNext();) if ((cursor.elem()%3)==2) return false;
		return true;
	}
	
	// Faster than LongMath.pow because it skips special cases that are not relevant to this problem.
	private static long pow(long prime,int pow)	{
		long result=prime;
		for (int i=2;i<=pow;++i) result*=prime;
		return result;
		
	}
	
	private static long trueMod(long x,long mod)	{
		return ((x%mod)+mod)%mod;
	}
	
	private static BigInteger modulusInverse(long operand,long bigPrime)	{
		return EulerUtils.modulusInverse(BigInteger.valueOf(operand),BigInteger.valueOf(bigPrime));
	}
	
	public static LongSet solveChineseRemainder(LongSet as,long x,LongSet bs,long y)	{
		if (as.isEmpty()) return as;
		if (bs.isEmpty()) return bs;
		BigInteger bx=BigInteger.valueOf(x);
		BigInteger by=BigInteger.valueOf(y);
		BigInteger y_x=EulerUtils.modulusInverse(BigInteger.valueOf(y%x),bx);
		BigInteger x_y=EulerUtils.modulusInverse(BigInteger.valueOf(x%y),by);
		BigInteger prod=bx.multiply(by);
		BigInteger m=by.multiply(y_x).mod(prod);
		BigInteger n=bx.multiply(x_y).mod(prod);
		LongSet result=HashLongSets.newMutableSet();
		BigInteger[] aa=as.stream().map(BigInteger::valueOf).toArray(BigInteger[]::new);
		BigInteger[] bb=bs.stream().map(BigInteger::valueOf).toArray(BigInteger[]::new);
		for (BigInteger valA:aa) for (BigInteger valB:bb) result.add(valA.multiply(m).add(valB.multiply(n)).mod(prod).longValueExact());
		return result;
	}
	
	private static class ModularEquationCache	{
		private final static LongSet NO_SOLUTIONS=HashLongSets.newImmutableSet(new long[0]);
		private final LongObjMap<LongSet> solutions;
		private final long[] tmpPowers;
		public ModularEquationCache()	{
			solutions=HashLongObjMaps.newMutableMap();
			solutions.put(2l,HashLongSets.newImmutableSetOf(1l));
			solutions.put(3l,HashLongSets.newImmutableSetOf(0l));
			solutions.put(4l,HashLongSets.newImmutableSetOf(1l,3l));
			solutions.put(9l,NO_SOLUTIONS);
			tmpPowers=new long[23];	// ceil(63*log(7)/log(2).
			tmpPowers[0]=1l;
		}
		private static LongSet getSolutions(long prime)	{
			// The prime is always below 1e9, so we don't need to resort to BigInteger.
			return EulerUtils.squareRootModuloPrime(trueMod(prime-3,prime),prime);
		}
		private static LongSet henselLifting(LongSet previous,long prime,long powPrev,long pow)	{
			if (previous.isEmpty()) return NO_SOLUTIONS;
			LongSet result=HashLongSets.newMutableSet();
			BigInteger bigPow=BigInteger.valueOf(pow);
			for (LongCursor cursor=previous.cursor();cursor.moveNext();)	{
				long x=cursor.elem();
				long dFx=2*x;
				BigInteger dFx_1=modulusInverse(dFx,prime);
				BigInteger bx=BigInteger.valueOf(x);
				BigInteger eqn=bx.multiply(bx).add(THREE);
				BigInteger bigResult=bx.subtract(eqn.multiply(dFx_1)).mod(bigPow);
				result.add(trueMod(bigResult.longValueExact(),pow));
			}
			return result;
		}
		private LongSet getSolutions(long prime,int power)	{
			tmpPowers[1]=prime;
			for (int i=2;i<=power;++i) tmpPowers[i]=prime*tmpPowers[i-1];
			LongSet result=solutions.get(tmpPowers[power]);
			if (result!=null) return result;
			for (int i=power-1;i>=2;--i)	{
				result=solutions.get(tmpPowers[i]);
				if (result!=null)	{
					for (int j=i+1;j<=power;++j)	{
						result=henselLifting(result,prime,tmpPowers[j-1],tmpPowers[j]);
						solutions.put(tmpPowers[j],result);
					}
					return result;
				}
			}
			result=solutions.computeIfAbsent(prime,ModularEquationCache::getSolutions);
			for (int j=2;j<=power;++j)	{
				result=henselLifting(result,prime,tmpPowers[j-1],tmpPowers[j]);
				solutions.put(tmpPowers[j],result);
			}
			return result;
		}
		public LongSet getSolutions(long n,LongIntMap decomp)	{
			LongSet result=solutions.get(n);
			if (result!=null) return result;
			LongIntCursor cursor=decomp.cursor();
			cursor.moveNext();
			long prime=cursor.key();
			int power=cursor.value();
			LongSet currentSols=getSolutions(prime,power);
			long currentFactors=pow(prime,power);
			if (n==currentFactors) return currentSols;
			long remaining=n/currentFactors;
			while (cursor.moveNext())	{
				LongSet otherSols=solutions.get(remaining);
				if (otherSols!=null) return solveChineseRemainder(currentSols,currentFactors,otherSols,remaining);
				prime=cursor.key();
				power=cursor.value();
				otherSols=getSolutions(prime,power);
				long primePower=pow(prime,power);
				currentSols=solveChineseRemainder(currentSols,currentFactors,otherSols,primePower);
				currentFactors*=primePower;
				remaining/=primePower;
			}
			return currentSols;
		}
	}
	
	private static class AdHocDecomposition	{
		public final long d;
		public final long r;
		public final LongIntMap rDecomp;
		public AdHocDecomposition(long d,long r,LongIntMap rDecomp)	{
			this.d=d;
			this.r=r;
			this.rDecomp=rDecomp;
		}
	}
	
	/*
	 * If q is a multiple of 9, don't bother, there aren't primitive solutions.
	 * If q is a multiple of 3, we want the decompositions {d=24, r=57*(q/3)^2} and {d=12, r=57*(2q/3)}.
	 * Otherwise, we want {d=8, r=57*q^2} and {d=4, r=228*q^2}.
	 */
	private static Iterable<AdHocDecomposition> getValidDecompositions(long q,LongIntMap qDecomp)	{
		if ((q%9)==0) return List.of();
		for (LongIntCursor cursor=qDecomp.cursor();cursor.moveNext();) cursor.setValue(cursor.value()+cursor.value());
		qDecomp.addValue(3l,1);
		qDecomp.addValue(19l,1);
		long baseD,baseR;
		if ((q%3)==0)	{
			baseD=24;
			baseR=(q/3)*q*19;
			qDecomp.addValue(3l,-2);
		}	else	{
			baseD=8;
			baseR=q*q*57;
		}
		return ()->new Iterator<>()	{
			private int position=0;
			@Override
			public boolean hasNext() {
				return position<2;
			}
			@Override
			public AdHocDecomposition next() {
				++position;
				switch (position)	{
					case 1:
						return new AdHocDecomposition(baseD,baseR,qDecomp);
					case 2:
						qDecomp.addValue(2l,2);
						return new AdHocDecomposition(baseD/2,baseR*4,qDecomp);
					default:throw new NoSuchElementException();
				}
			}
		};
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] primes=Primes.lastPrimeSieve(N);
		long sum=0;
		boolean add4=true;
		ModularEquationCache equationSolver=new ModularEquationCache();
		int lastN=N/4;	// Quite conservative. I believe htat N/5 is enough.
		for (int q=3;q<lastN;q+=add4?4:2,add4=!add4)	{
			DivisorHolder qDecomp=DivisorHolder.getFromFirstPrimes(q,primes);
			if (!isValid(qDecomp)) continue;
			long q8=8*q;
			for (AdHocDecomposition fullDecomp:getValidDecompositions(q,qDecomp.getFactorMap()))	{
				long r=fullDecomp.r;
				BigInteger bigR=BigInteger.valueOf(r);
				long d=fullDecomp.d;
				LongSet sols=equationSolver.getSolutions(r,fullDecomp.rDecomp);
				if (sols.isEmpty()) continue;
				for (LongCursor solsCursor=sols.cursor();solsCursor.moveNext();)	{
					// 2) Given a solution n, calculate P=(n^2+3)/r, Q=2*n and R=r. Ignore solutions where gcd(P,Q,R)!=1.
					long n=solsCursor.elem();
					BigInteger bn=BigInteger.valueOf(n);
					long pp=bn.multiply(bn).add(THREE).divide(bigR).longValueExact();
					long qq=2*n;
					if (!EulerUtils.areCoprime(pp,qq,r)) continue;
					// 3) Calculate convergents of the fraction Q/2P, (p,q).
					for (LongConvergent conv:LongFiniteContinuedFraction.getFor(qq,pp+pp))	{
						// 4) We need that Pp^2-Qpq+Rq^2=1. Otherwise, skip and move to the next convergent.
						long val=pp*conv.p*conv.p-qq*conv.p*conv.q+r*conv.q*conv.q;
						if (val!=1) continue;
						// 5) First transformation reversion: x=n*p-r*q, y=p.
						long x=n*conv.p-r*conv.q;
						long y=conv.p;
						if ((x>=0)||((3*y+x)<=0)) continue;
						// 6) Rescaling: u=d*x, v=d*y. Step 7 can be skipped now!
						long u=d*x;
						long v=d*y;
						if (v-u>24*N-64*q) continue;
						// 8) Final equation solution: det=v/12, X=(32q+u/12). Only valid if integer.
						if ((((32*q-v)%12)==0)&&((u%12)==0))	{
							long xx=(32*q-v)/12;
							long det=-u/12;
							// 9) Now we go back to our original solution: S=8q-X, P=15q^2-8qX+X^2 using the X above.
							long S=q8-xx;
							long yy=(S-det)/2;
							// 10) Y and Z are respectively (S-det)/2 and (S+det)/2. These values should be integers by construction.
							// 11) Accept the solution if gcd(X,Y,Z)=1 and if X<Y.
							if ((0<xx)&&(xx<=yy))	{
								long zz=(S+det)/2;
								if (EulerUtils.areCoprime(xx,yy,zz)) sum+=8*q;
							}
						}
					}
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
