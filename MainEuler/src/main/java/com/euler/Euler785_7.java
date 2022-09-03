package com.euler;

import java.util.List;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.LongConvergents.LongConvergent;
import com.euler.common.LongFiniteContinuedFraction;
import com.euler.common.Primes;
import com.google.common.collect.Iterables;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongObjCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler785_7 {
	private final static int N=IntMath.pow(10,4);
	
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
	
	private static class ModularEquationCache	{
		private final static LongSet NO_SOLUTIONS=HashLongSets.newImmutableSet(new long[0]);
		private final LongObjMap<LongSet> solutions;
		private final long[] tmpPowers;
		public ModularEquationCache()	{
			solutions=HashLongObjMaps.newMutableMap();
			solutions.put(2l,HashLongSets.newImmutableSetOf(1l));
			tmpPowers=new long[64];
			tmpPowers[0]=1l;
		}
		public static long equation(long x)	{
			return x*x+3;
		}
		private static LongSet getSolutions(long prime)	{
			return EulerUtils.squareRootModuloPrime(trueMod(prime-3,prime),prime);
		}
		private static LongSet henselLifting2n(LongSet previous,long powPrev,long pow)	{
			if (previous.isEmpty()) return NO_SOLUTIONS;
			LongSet result=HashLongSets.newMutableSet();
			for (LongCursor cursor=previous.cursor();cursor.moveNext();)	{
				long x=cursor.elem();
				if ((equation(x)%pow)==0) result.add(x);
				long x2=x+powPrev;
				if ((equation(x2)%pow)==0) result.add(x2);
			}
			return result;
		}
		private static void manualLifting(long x,long prime,long powPrev,long pow,LongSet result)	{
			for (int i=0;i<prime;++i)	{
				if ((equation(x)%pow)==0) result.add(x);
				x+=powPrev;
			}
		}
		private static LongSet henselLifting(LongSet previous,long prime,long powPrev,long pow)	{
			if (previous.isEmpty()) return NO_SOLUTIONS;
			LongSet result=HashLongSets.newMutableSet();
			for (LongCursor cursor=previous.cursor();cursor.moveNext();)	{
				long x=cursor.elem();
				long dFx=2*x;
				if (dFx%prime==0) manualLifting(x,prime,powPrev,pow,result);	// I think this doesn't happen any more!
				else	{
					long dFx_1=EulerUtils.modulusInverse(dFx,prime);
					result.add(trueMod(x-equation(x)*dFx_1,pow));
				}
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
						result=(prime==2)?henselLifting2n(result,tmpPowers[j-1],tmpPowers[j]):henselLifting(result,prime,tmpPowers[j-1],tmpPowers[j]);
						solutions.put(tmpPowers[j],result);
					}
					return result;
				}
			}
			result=solutions.computeIfAbsent(prime,ModularEquationCache::getSolutions);
			for (int j=2;j<=power;++j)	{
				result=(prime==2)?henselLifting2n(result,tmpPowers[j-1],tmpPowers[j]):henselLifting(result,prime,tmpPowers[j-1],tmpPowers[j]);
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
				if (otherSols!=null) return EulerUtils.solveChineseRemainder(currentSols,currentFactors,otherSols,remaining);
				prime=cursor.key();
				power=cursor.value();
				otherSols=getSolutions(prime,power);
				long primePower=pow(prime,power);
				currentSols=EulerUtils.solveChineseRemainder(currentSols,currentFactors,otherSols,primePower);
				currentFactors*=primePower;
				remaining/=primePower;
			}
			return currentSols;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] primes=Primes.lastPrimeSieve(N);
		long sum=0;
		boolean add4=true;
		ModularEquationCache equationSolver=new ModularEquationCache();
		List<LongConvergent> BASE_SOLUTION=List.of(new LongConvergent(1l,0l));
		for (int q=3;q<N;q+=add4?4:2,add4=!add4)	{
			DivisorHolder decomp=DivisorHolder.getFromFirstPrimes(q,primes);
			if (!isValid(decomp)) continue;
			//System.out.println(q+"...");
			decomp.addFactor(2l,3);
			long q8=q*8;
			for (LongObjCursor<LongIntMap> cursor=decomp.getUnsortedListOfDivisorsWithDecomposition().cursor();cursor.moveNext();)	{
				// 1) Solve x^2+3=0 mod r for every r such that d^2*r=57*64*q^2 for some integer d. Use Hensel lifting and CRT. CACHE RESULTS!!
				long r=cursor.key();
				long d=q8/r;
				r=r*r*57;
				LongIntMap rDecomp=cursor.value();
				for (LongIntCursor powers=rDecomp.cursor();powers.moveNext();) powers.setValue(powers.value()*2);
				rDecomp.addValue(3l,1);
				rDecomp.addValue(19l,1);
				LongSet sols=equationSolver.getSolutions(r,rDecomp);
				if (sols.isEmpty()) continue;
				for (LongCursor solsCursor=sols.cursor();solsCursor.moveNext();)	{
					// 2) Given a solution n, calculate P=(n^2+3)/r, Q=2*n and R=r. Ignore solutions where gcd(P,Q,R)!=1.
					long n=solsCursor.elem();
					long pp=ModularEquationCache.equation(n)/r;
					long qq=2*n;
					if (!EulerUtils.areCoprime(pp,qq,r)) continue;
					// 3) Calculate convergents of the fraction Q/2P, (p,q).
					for (LongConvergent conv:Iterables.concat(BASE_SOLUTION,LongFiniteContinuedFraction.getFor(qq,pp+pp)))	{
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
								if ((zz<=N)&&EulerUtils.areCoprime(xx,yy,zz))	{
									System.out.println(String.format("ATENCI ÓN, ATENCI ÓN: q=%d, r=%d, d=%d, n=%d, P=%d, Q=%d, R=%d, p=%d, q=%d, \"x\"=%d, \"y\"=%d, u=%d, v=%d, x=%d, y=%d, z=%d.",q,r,d,n,pp,qq,r,conv.p,conv.q,x,y,u,v,xx,yy,zz));
									// System.out.println(String.format("q=%d: x=%d, y=%d, z=%d.",q,xx,yy,zz));
									sum+=xx+yy+zz;
								}
							}
						}
					}
				}
			}
			/*
			 * The following steps are:
			 * 1) Solve 13x^2+14x+4=0 mod r for every r such that d^2*r=57*64*q^2 for some integer d. Use Hensel lifting and CRT. CACHE RESULTS!!
			 * 2) Given a solution n, calculate P=(13n^2+14n+4)/r, Q=26n+14 and R=13*r. Ignore solutions where gcd(P,Q,R)!=1.
			 * 3) Calculate convergents of the fraction Q/2P, (p,q).
			 * 4) We need that Pp^2+Qpq+Rq^2=1. Otherwise, skip and move to the next convergent.
			 * 5) First transformation reversion: x=n*p-r*q, y=p.
			 * 6) Rescaling: u=d*x, v=d*y.
			 * 7) Reversing unimodular transformation: a=2u+v, b=u+v.
			 * 8) Final equation solution: X=(a-96)/(-12), det=b/(-12). Only valid if integer, natürlich. Take abs(det) (or ignore negative det).
			 * 9) Now we go back to our original solution: S=8q-X, P=34q^2-X^2 using the X above. It should be the case that S^2-4P=det^2.
			 * 10) Y and Z are respectively (S-det)/2 and (S+det)/2. These values should be integers by construction.
			 * 11) Accept the solution if gcd(X,Y,Z)=1 and if X<Y. Do some experiments to check that the rescaling factor "d" can be sieved.
			 * 
			 * It's not SO bad, but there are a lot of steps! It's like 777 all over again :).
			 */
			// ZUTUN! TODO! TEHDÄ!!!!!
			/*-
			for (LongCursor cursor=decomp.getUnsortedListOfDivisors().cursor();cursor.moveNext();)	{
				long d=cursor.elem();
				long quotient=q/d;
				long r=57*quotient*quotient;
			}
			*/
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
