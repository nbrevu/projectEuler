package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import com.euler.common.MeisselLehmerPrimeCounter;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler634_6 {
	private final static long LIMIT=9*LongMath.pow(10l,18);
	
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
		public long[] primeNumbers()	{
			return primes;
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
		private static boolean isExponentSetValid(int[] exponents)	{
			// Assumes that "exponents" only contains numbers >=2.
			int any2=0;
			int any3=0;
			int any6=0;
			for (int e:exponents) if ((e==2)||(e==4)) any2=1;
			else if (e==3) any3=1;
			else if (e==6) ++any6;
			else return true;	// If there is any exponent equal to 5 or >=7, and all the other ones are >=2, the combination is always valid.
			return any2+any3+any6>=2;	// Kind of weird, but it works.
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
		private final MeisselLehmerPrimeCounter primeCounter;
		private final PrimePowerCache primePowers;
		private final long[] primes;
		private final long limit;
		private final double logLimit;
		private final double[] logPrimes;
		public PrimePowerCombinationCalculator(long limit)	{
			primeCounter=new MeisselLehmerPrimeCounter(LongMath.sqrt(LongMath.sqrt(limit/8,RoundingMode.UP),RoundingMode.UP));
			primePowers=new PrimePowerCache(limit);
			primes=primePowers.primeNumbers();
			this.limit=limit;
			logLimit=Math.log(limit);
			logPrimes=new double[MAX_LENGTH];
			for (int i=0;i<logPrimes.length;++i) logPrimes[i]=Math.log(primes[i]);
		}
		private long countForExponents(int[] exponents)	{
			LongSet usedPrimes=HashLongSets.newMutableSet();
			return countForExponentsRecursive(exponents,0,usedPrimes,limit);
		}
		private long countForExponentsRecursive(int[] exponents,int currentIndex,LongSet usedPrimes,long currentLimit)	{
			if (currentIndex==exponents.length-1)	{
				long primeLimit=root(currentLimit,exponents[currentIndex]);
				long result=primeCounter.pi(primeLimit);
				for (LongCursor cursor=usedPrimes.cursor();cursor.moveNext();) if (cursor.elem()<=primeLimit) --result;
				return result;
			}	else	{
				long result=0l;
				long maxPrime=root(currentLimit,exponents[currentIndex]);
				for (int i=0;i<primes.length;++i)	{
					long p=primes[i];
					if (p>maxPrime) break;
					else if (usedPrimes.contains(p)) continue;
					long power=primePowers.getPower(p,exponents[currentIndex]);
					long newLimit=currentLimit/power;
					usedPrimes.add(p);
					long toAdd;
					if (exponents[currentIndex+1]==exponents[currentIndex]) toAdd=countForExponentsRecursiveSameExponent(exponents,1+currentIndex,usedPrimes,newLimit,1+i);
					else toAdd=countForExponentsRecursive(exponents,1+currentIndex,usedPrimes,newLimit);
					result+=toAdd;
					usedPrimes.removeLong(p);
				}
				return result;
			}
		}
		private long countForExponentsRecursiveSameExponent(int[] exponents,int currentIndex,LongSet usedPrimes,long currentLimit,int currentPrimeIndex)	{
			if (currentPrimeIndex>=primes.length) return 0l;
			if (currentIndex==exponents.length-1)	{
				long maxPrime=root(currentLimit,exponents[currentIndex]);
				long alreadyCounted=primes[currentPrimeIndex]-1;
				if (maxPrime<=alreadyCounted) return 0l;
				long result=primeCounter.pi(maxPrime)-primeCounter.pi(alreadyCounted);
				for (LongCursor cursor=usedPrimes.cursor();cursor.moveNext();)	{
					long prime=cursor.elem();
					if ((prime<=maxPrime)&&(prime>alreadyCounted)) --result;
				}
				if (result<0) throw new ArithmeticException("JAJA SI");
				return result;
			}	else	{
				long result=0l;
				long maxPrime=root(currentLimit,exponents[currentIndex]);
				for (int i=currentPrimeIndex;i<primes.length;++i)	{
					long p=primes[i];
					if (p>maxPrime) break;
					else if (usedPrimes.contains(p)) continue;
					long power=primePowers.getPower(p,exponents[currentIndex]);
					long newLimit=currentLimit/power;
					usedPrimes.add(p);
					long toAdd;
					if (exponents[currentIndex+1]==exponents[currentIndex]) toAdd=countForExponentsRecursiveSameExponent(exponents,1+currentIndex,usedPrimes,newLimit,1+i);
					else toAdd=countForExponentsRecursive(exponents,1+currentIndex,usedPrimes,newLimit);
					result+=toAdd;
					usedPrimes.removeLong(p);
				}
				return result;
			}
		}
		private long countRecursive(int[] exponents)	{
			if (!isFeasible(exponents)) return 0l;
			System.out.println(Arrays.toString(exponents)+"...");
			long result=0l;
			if (isExponentSetValid(exponents)) result=countForExponents(exponents);
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
