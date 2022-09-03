package com.euler;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.euler.common.Convergents.Convergent;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.FiniteContinuedFraction;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.set.LongSet;

public class Euler785_10 {
	private final static int N=IntMath.pow(10,6);
	
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
	
	public static BigInteger[] solveChineseRemainder(BigInteger[] as,BigInteger x,BigInteger[] bs,BigInteger y)	{
		if (as.length==0) return as;
		if (bs.length==0) return bs;
		BigInteger y_x=EulerUtils.modulusInverse(y.mod(x),x);
		BigInteger x_y=EulerUtils.modulusInverse(x.mod(y),y);
		BigInteger prod=x.multiply(y);
		BigInteger m=y.multiply(y_x).mod(prod);
		BigInteger n=x.multiply(x_y).mod(prod);
		BigInteger[] result=new BigInteger[as.length*bs.length];
		for (int i=0;i<as.length;++i) for (int j=0;j<bs.length;++j) result[j*as.length+i]=as[i].multiply(m).add(bs[j].multiply(n)).mod(prod);
		return result;
	}
	
	private static class ModularEquationCache	{
		private final static BigInteger[] NO_SOLUTIONS=new BigInteger[0];
		private final Map<BigInteger,BigInteger[]> solutions;
		private final long[] tmpPowers;
		public ModularEquationCache()	{
			solutions=new HashMap<>();
			solutions.put(BigInteger.TWO,new BigInteger[] {BigInteger.ONE});
			solutions.put(THREE,new BigInteger[] {BigInteger.ZERO});
			solutions.put(BigInteger.valueOf(4l),new BigInteger[] {BigInteger.ONE,THREE});
			solutions.put(BigInteger.valueOf(9),NO_SOLUTIONS);
			tmpPowers=new long[23];	// ceil(63*log(7)/log(2).
			tmpPowers[0]=1l;
		}
		private static BigInteger[] getSolutions(long prime)	{
			// The prime is always below 1e9, so we don't need to resort to BigInteger.
			LongSet longSols=EulerUtils.squareRootModuloPrime(trueMod(prime-3,prime),prime);
			return longSols.stream().map(BigInteger::valueOf).toArray(BigInteger[]::new);
		}
		private static BigInteger[] henselLifting(BigInteger[] previous,long prime,long pow)	{
			if (previous.length==0) return NO_SOLUTIONS;
			BigInteger bigPow=BigInteger.valueOf(pow);
			BigInteger[] result=new BigInteger[previous.length];
			for (int i=0;i<previous.length;++i)	{
				BigInteger x=previous[i];
				BigInteger dFx=x.add(x);
				BigInteger dFx_1=EulerUtils.modulusInverse(dFx,BigInteger.valueOf(prime));
				BigInteger eqn=x.multiply(x).add(THREE);
				BigInteger bigResult=x.subtract(eqn.multiply(dFx_1)).mod(bigPow);
				BigInteger toAdd=bigResult.mod(bigPow);
				if (toAdd.signum()<0) toAdd=toAdd.add(bigPow);
				result[i]=toAdd;
			}
			return result;
		}
		private BigInteger[] getSolutions(long prime,int power)	{
			tmpPowers[1]=prime;
			for (int i=2;i<=power;++i) tmpPowers[i]=prime*tmpPowers[i-1];
			BigInteger[] result=solutions.get(BigInteger.valueOf(tmpPowers[power]));
			if (result!=null) return result;
			for (int i=power-1;i>=2;--i)	{
				result=solutions.get(BigInteger.valueOf(tmpPowers[i]));
				if (result!=null)	{
					for (int j=i+1;j<=power;++j)	{
						result=henselLifting(result,prime,tmpPowers[j]);
						solutions.put(BigInteger.valueOf(tmpPowers[j]),result);
					}
					return result;
				}
			}
			result=solutions.computeIfAbsent(BigInteger.valueOf(prime),(BigInteger unused)->getSolutions(prime));
			for (int j=2;j<=power;++j)	{
				result=henselLifting(result,prime,tmpPowers[j]);
				solutions.put(BigInteger.valueOf(tmpPowers[j]),result);
			}
			return result;
		}
		public BigInteger[] getSolutions(BigInteger n,LongIntMap decomp)	{
			BigInteger[] result=solutions.get(n);
			if (result!=null) return result;
			LongIntCursor cursor=decomp.cursor();
			cursor.moveNext();
			long prime=cursor.key();
			int power=cursor.value();
			BigInteger[] currentSols=getSolutions(prime,power);
			BigInteger currentFactors=BigInteger.valueOf(pow(prime,power));
			if (n.equals(currentFactors)) return currentSols;
			BigInteger remaining=n.divide(currentFactors);
			while (cursor.moveNext())	{
				BigInteger[] otherSols=solutions.get(remaining);
				if (otherSols!=null) return solveChineseRemainder(currentSols,currentFactors,otherSols,remaining);
				prime=cursor.key();
				power=cursor.value();
				otherSols=getSolutions(prime,power);
				BigInteger primePower=BigInteger.valueOf(pow(prime,power));
				currentSols=solveChineseRemainder(currentSols,currentFactors,otherSols,primePower);
				currentFactors=currentFactors.multiply(primePower);
				remaining=remaining.divide(primePower);
			}
			return currentSols;
		}
	}
	
	private static class AdHocDecomposition	{
		public final long d;
		public final BigInteger r;
		public final LongIntMap rDecomp;
		public AdHocDecomposition(long d,BigInteger r,LongIntMap rDecomp)	{
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
		long baseD;
		BigInteger baseR;
		if ((q%3)==0)	{
			baseD=24;
			baseR=BigInteger.valueOf(19*q/3).multiply(BigInteger.valueOf(q));
			qDecomp.addValue(3l,-2);
		}	else	{
			baseD=8;
			baseR=BigInteger.valueOf(q).multiply(BigInteger.valueOf(q*57));
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
					case 1:return new AdHocDecomposition(baseD,baseR,qDecomp);
					case 2:	{
						qDecomp.addValue(2l,2);
						return new AdHocDecomposition(baseD/2,baseR.shiftLeft(2),qDecomp);
					}	default:throw new NoSuchElementException();
				}
			}
		};
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] primes=Primes.lastPrimeSieve(N);
		ModularEquationCache equationSolver=new ModularEquationCache();
		long sum=0;
		long lastGoodQ=1;
		long lastN=N/5;
		boolean add4=true;
		for (long q=3;q<lastN;q+=add4?4:2,add4=!add4)	{
			DivisorHolder qDecomp=DivisorHolder.getFromFirstPrimes((int)q,primes);
			if (!isValid(qDecomp)) continue;
			long q8=8*q;
			for (AdHocDecomposition fullDecomp:getValidDecompositions(q,qDecomp.getFactorMap()))	{
				BigInteger r=fullDecomp.r;
				long d=fullDecomp.d;
				BigInteger[] sols=equationSolver.getSolutions(r,fullDecomp.rDecomp);
				if (sols.length==0) continue;
				for (BigInteger n:sols)	{
					BigInteger pp=n.multiply(n).add(THREE).divide(r);
					BigInteger qq=n.shiftLeft(1);
					if (!EulerUtils.areCoprime(pp,qq,r)) continue;
					for (Convergent conv:FiniteContinuedFraction.getFor(qq,pp.shiftLeft(1)))	{
						BigInteger val=pp.multiply(conv.p.multiply(conv.p)).subtract(qq.multiply(conv.p.multiply(conv.q))).add(r.multiply(conv.q.multiply(conv.q)));
						if (!val.equals(BigInteger.ONE)) continue;
						BigInteger x=n.multiply(conv.p).subtract(r.multiply(conv.q));
						BigInteger y=conv.p;
						if ((x.signum()>=0)||(THREE.multiply(y).add(x).signum()<=0)) continue;
						long u=d*x.longValueExact();
						long v=d*y.longValueExact();
						if ((((32l*q-v)%12)==0)&&((u%12)==0))	{
							long xx=(32l*q-v)/12;
							long det=-u/12;
							long S=q8-xx;
							long yy=(S-det)/2;
							if ((0<xx)&&(xx<=yy))	{
								long zz=(S+det)/2;
								if ((zz<=N)&&EulerUtils.areCoprime(xx,yy,zz))	{
									lastGoodQ=q;
									sum+=8*q;
								}
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
		System.out.println("Last good q="+lastGoodQ+".");
	}
}
