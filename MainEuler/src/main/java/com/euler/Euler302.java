package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

// Code recycled from 634!
public class Euler302 {
	private final static long LIMIT=LongMath.pow(10l,18);
	
	private final static int MAX_LENGTH=10;	// Hackish, yeah.
	
	private static class PrimePowerCache	{
		private final long[] primes;
		private final LongObjMap<long[]> powers;
		public PrimePowerCache(long limit)	{
			long maxPrime=(long)Math.ceil(Math.cbrt(limit));
			primes=Primes.listLongPrimesAsArray(maxPrime);
			powers=HashLongObjMaps.newMutableMap();
			for (long p:primes)	{
				long[] powArray=calculatePrimePowers(p,limit);
				if (powArray==null) break;
				else powers.put(p,powArray);
			}
		}
		private static long[] calculatePrimePowers(long prime,long limit)	{
			int maxPower=(int)Math.floor(Math.log(limit)/Math.log(prime));
			if (maxPower<=2) return null;
			long[] result=new long[maxPower-2];
			result[0]=prime*prime*prime;
			for (int i=1;i<result.length;++i) result[i]=prime*result[i-1];
			return result;
		}
		public long getPower(long prime,int power)	{
			// Assumes power>=2.
			if (power==2) return prime*prime;
			else return powers.get(prime)[power-3];
		}
	}
	
	private static class PrimePowerCombinationCalculator	{
		private long root(long x,int index)	{
			long result=(long)Math.floor(Math.pow(x,1d/index));
			// Horrible and kind of slow, but safe.
			while (Math.pow(result,index)>x) --result;
			while (Math.pow(result+1,index)<=x) ++result;
			return result;
		}
		private static boolean isAchilles(int[] exponents)	{
			if ((exponents.length==1)||(exponents[0]==1)) return false;
			int gcd=exponents[0];
			for (int i=1;i<exponents.length;++i)	{
				if (exponents[i]==1) return false;
				gcd=EulerUtils.gcd(gcd,exponents[i]);
			}
			return gcd==1;
		}
		private static boolean isAchilles(DivisorHolder decomp)	{
			return isAchilles(decomp.getFactorMap().values().toIntArray());
		}
		private static List<int[]> getChildren(int[] base)	{
			int n=base.length;
			int[] copy1=Arrays.copyOf(base,n+1);
			copy1[n]=2;
			if ((base.length==1)||(base[base.length-1]<base[base.length-2]))	{
				int[] copy2=Arrays.copyOf(base,n);
				++copy2[n-1];
				return Arrays.asList(copy1,copy2);
			}	else return Arrays.asList(copy1);
		}
		private long[] extractPrimes(int[] firstPrimes)	{
			List<Long> primes=new ArrayList<>(firstPrimes.length/10);
			primes.add(2l);
			primes.add(3l);
			boolean add4=false;
			for (int p=5;p<firstPrimes.length;p+=(add4?4:2),add4=!add4) if (firstPrimes[p]==0) primes.add(Long.valueOf(p));
			return primes.stream().mapToLong(Long::longValue).toArray();
		}
		private final int[] firstPrimeCache;
		private final long[] primes;
		private final PrimePowerCache primePowers;
		private final long limit;
		private final double logLimit;
		private final double[] logPrimes;
		public PrimePowerCombinationCalculator(long limit)	{
			firstPrimeCache=Primes.firstPrimeSieve((int)LongMath.sqrt(limit/8,RoundingMode.DOWN));
			primes=extractPrimes(firstPrimeCache);
			primePowers=new PrimePowerCache(limit);
			this.limit=limit;
			logLimit=Math.log(limit);
			logPrimes=new double[MAX_LENGTH];
			for (int i=0;i<logPrimes.length;++i) logPrimes[i]=Math.log(primes[i]);
		}
		private long countForExponents(int[] exponents)	{
			LongSet usedPrimes=HashLongSets.newMutableSet();
			DivisorHolder totientHolder=new DivisorHolder();
			return countForExponentsRecursive(exponents,0,usedPrimes,limit,totientHolder);
		}
		private long countForExponentsRecursive(int[] exponents,int currentIndex,LongSet usedPrimes,long currentLimit,DivisorHolder totientHolder)	{
			long result=0l;
			long maxPrime=root(currentLimit,exponents[currentIndex]);
			for (int i=0;i<primes.length;++i)	{
				long p=primes[i];
				if (p>maxPrime) break;
				else if (usedPrimes.contains(p)) continue;
				DivisorHolder thisTotient=DivisorHolder.getFromFirstPrimes((int)p-1,firstPrimeCache);
				thisTotient.addFactor(p,exponents[currentIndex]-1);
				totientHolder.combineDestructive(thisTotient);
				if (currentIndex==exponents.length-1)	{
					if (isAchilles(totientHolder)) ++result;
				}	else	{
					long power=primePowers.getPower(p,exponents[currentIndex]);
					long newLimit=currentLimit/power;
					usedPrimes.add(p);
					long toAdd;
					if (exponents[currentIndex+1]==exponents[currentIndex]) toAdd=countForExponentsRecursiveSameExponent(exponents,1+currentIndex,usedPrimes,newLimit,1+i,totientHolder);
					else toAdd=countForExponentsRecursive(exponents,1+currentIndex,usedPrimes,newLimit,totientHolder);
					result+=toAdd;
					usedPrimes.removeLong(p);
				}
				totientHolder.divideDestructive(thisTotient);
			}
			return result;
		}
		private long countForExponentsRecursiveSameExponent(int[] exponents,int currentIndex,LongSet usedPrimes,long currentLimit,int currentPrimeIndex,DivisorHolder totientHolder)	{
			if (currentPrimeIndex>=primes.length) return 0l;
			long result=0l;
			long maxPrime=root(currentLimit,exponents[currentIndex]);
			for (int i=currentPrimeIndex;i<primes.length;++i)	{
				long p=primes[i];
				if (p>maxPrime) break;
				else if (usedPrimes.contains(p)) continue;
				DivisorHolder thisTotient=DivisorHolder.getFromFirstPrimes((int)p-1,firstPrimeCache);
				thisTotient.addFactor(p,exponents[currentIndex]-1);
				totientHolder.combineDestructive(thisTotient);
				if (currentIndex==exponents.length-1)	{
					if (isAchilles(totientHolder)) ++result;
				}	else	{
					long power=primePowers.getPower(p,exponents[currentIndex]);
					long newLimit=currentLimit/power;
					usedPrimes.add(p);
					long toAdd;
					if (exponents[currentIndex+1]==exponents[currentIndex]) toAdd=countForExponentsRecursiveSameExponent(exponents,1+currentIndex,usedPrimes,newLimit,1+i,totientHolder);
					else toAdd=countForExponentsRecursive(exponents,1+currentIndex,usedPrimes,newLimit,totientHolder);
					result+=toAdd;
					usedPrimes.removeLong(p);
				}
				totientHolder.divideDestructive(thisTotient);
			}
			return result;
		}
		private long countRecursive(int[] exponents)	{
			if (!isFeasible(exponents)) return 0l;
			System.out.println(Arrays.toString(exponents)+"...");
			long result=0l;
			if (isAchilles(exponents)) result=countForExponents(exponents);
			for (int[] child:getChildren(exponents)) result+=countRecursive(child);
			return result;
		}
		private boolean isFeasible(int[] exponents)	{
			double log=logLimit;
			for (int i=0;i<exponents.length;++i) log-=exponents[i]*logPrimes[i];
			return log>=0;
		}
		public long count()	{
			return countRecursive(new int[] {3});
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		PrimePowerCombinationCalculator counter=new PrimePowerCombinationCalculator(LIMIT);
		long result=counter.count();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
