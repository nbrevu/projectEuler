package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.MeisselLehmerPrimeCounter;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler634_5 {
	private final static long LIMIT=3000000;
	
	private final static int MAX_LENGTH=10;	// Kind of a hack, but works.
	
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
	
	private static long[] getFactorials(int max)	{
		long[] result=new long[1+max];
		result[0]=1l;
		result[1]=1l;
		for (int i=2;i<=max;++i) result[i]=i*result[i-1];
		return result;
	}
	
	private static class PrimePowerCombinationCalculator	{
		private final static long[] FACTORIALS=getFactorials(MAX_LENGTH);
		private long pow(long x,int exp)	{
			long result=x*x;
			for (int i=3;i<=exp;++i) result*=x;
			return result;
		}
		private long root(long x,int index)	{
			long result=(long)Math.floor(Math.pow(x,1d/index));
			// Horrible and kind of slow, but safe.
			while (pow(result,index)>x) --result;
			while (pow(result+1,index)<=x) ++result;
			return result;
		}
		private static long getMultiplicityfactor(int[] exponents)	{
			long result=1l;
			int current=1;
			int previous=-1;
			for (int exp:exponents)	{
				if (exp==previous) ++current;
				else	{
					result=FACTORIALS[current];
					current=1;
				}
				previous=exp;
			}
			return result*FACTORIALS[current];
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
		private final MeisselLehmerPrimeCounter primeCounter;
		private final PrimePowerCache primePowers;
		private final long[] primes;
		private final long limit;
		public PrimePowerCombinationCalculator(long limit)	{
			primeCounter=new MeisselLehmerPrimeCounter(LongMath.sqrt(LongMath.sqrt(limit/8,RoundingMode.UP),RoundingMode.UP));
			primePowers=new PrimePowerCache(limit);
			primes=primePowers.primeNumbers();
			this.limit=limit;
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
				for (long p:primes) if (p>maxPrime) break;
				else if (!usedPrimes.contains(p))	{
					long power=primePowers.getPower(p,exponents[currentIndex]);
					usedPrimes.add(p);
					long myResult=countForExponentsRecursive(exponents,1+currentIndex,usedPrimes,currentLimit/power);
					result+=myResult;
					usedPrimes.removeLong(p);
					if (myResult!=0)	{
						if (currentIndex==0) System.out.println(p+"=>"+myResult/2+".");
						else if (currentIndex==1) System.out.println(usedPrimes+","+p+"=>"+myResult/2);
					}
				}
				return result;
			}
		}
		private long countRecursive(int[] exponents)	{
			long result=0l;
			if (isExponentSetValid(exponents))	{
				long thisResult=countForExponents(exponents);
				long factor=getMultiplicityfactor(exponents);
				if (thisResult==0l) return 0l;
				if ((thisResult%factor)!=0) throw new RuntimeException("Was passiert??");
				result=thisResult/factor;
				System.out.println(Arrays.toString(exponents)+": "+result+".");
			}
			//for (int[] child:getChildren(exponents)) if (child.length<=MAX_LENGTH) result+=countRecursive(child);
			return result;
		}
	}

	private static boolean isExponentSetValid(int[] exponents)	{
		int any2=0;
		int any3=0;
		int any6=0;
		boolean anyComplete=false;
		for (int e:exponents) if (e==1) return false;
		else if ((e==2)||(e==4)) any2=1;
		else if (e==3) any3=1;
		else if (e==6) ++any6;	// Two sixes are acceptable.
		else anyComplete=true;	// If there is any exponent equal to 5 or >=7, and all the other ones are >=2, the combination is always valid.
		return anyComplete||(any2+any3+any6>=2);	// Kind of weird, but it works.
	}
	
	private static class Array implements Comparable<Array>	{
		public final int[] data;
		public final int hashCode;
		public Array(int[] data)	{
			Arrays.sort(data);
			for (int i=0;i<data.length/2;++i)	{
				int swap=data[data.length-1-i];
				data[data.length-1-i]=data[i];
				data[i]=swap;
			}
			this.data=data;
			hashCode=Arrays.hashCode(data);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Array aOther=(Array)other;
			return Arrays.equals(data,aOther.data);
		}
		@Override
		public String toString()	{
			return Arrays.toString(data);
		}
		@Override
		public int compareTo(Array other)	{
			for (int i=0;;++i)	{
				boolean exceededThis=i>=data.length;
				boolean exceededOther=i>=other.data.length;
				if (exceededThis&&exceededOther) return 0;
				else if (exceededThis) return -1;
				else if (exceededOther) return 1;
				int comparison=Integer.compare(data[i],other.data[i]);
				if (comparison!=0) return comparison;
			}
		}
	}

	public static void main(String[] args)	{
		// I think I get it, maybe I don't need to subtract in the "common exponent" cases, to allow actual double counting.
		// I definitely would do better if I grouped same exponent cases.
		LongIntMap bruteForceCase=HashLongIntMaps.newMutableMap();
		LongObjMap<LongIntMap> bruteForceSubcases=HashLongObjMaps.newMutableMap();
		Array ourCase=new Array(new int[] {3,2,2});
		long[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
		SortedMap<Array,Integer> counters=new TreeMap<>();
		for (int i=2;i<=LIMIT;++i)	{
			DivisorHolder holder=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
			int[] exponents=holder.getFactorMap().values().toIntArray();
			Array myExponents=new Array(exponents);
			if (isExponentSetValid(exponents)) EulerUtils.increaseCounter(counters,new Array(exponents));
			if (myExponents.equals(ourCase))	{
				long mainPrime=-1;
				for (LongIntCursor cursor=holder.getFactorMap().cursor();cursor.moveNext();) if (cursor.value()==3)	{
					mainPrime=cursor.key();
					break;
				}
				bruteForceCase.addValue(mainPrime,1,0);
				LongIntMap subMap=bruteForceSubcases.computeIfAbsent(mainPrime,(long unused)->HashLongIntMaps.newMutableMap());
				for (LongIntCursor cursor=holder.getFactorMap().cursor();cursor.moveNext();) if (cursor.value()!=3) subMap.addValue(cursor.key(),1,0);
			}
		}
		System.out.println(bruteForceCase);
		System.out.println(bruteForceSubcases);
		PrimePowerCombinationCalculator counter=new PrimePowerCombinationCalculator(LIMIT);
		counter.countRecursive(ourCase.data);
	}
}
