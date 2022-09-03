package com.euler.common;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongUnaryOperator;
import java.util.function.ToLongFunction;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;
import com.koloboke.function.LongIntConsumer;

public class PowerfulNumberGenerator	{
	public static class MultiplicativeFunctionScheme	{
		private final LongUnaryOperator sumG;
		private final ToLongFunction<PowerfulNumber> h;
		public MultiplicativeFunctionScheme(LongUnaryOperator sumG,ToLongFunction<PowerfulNumber> h)	{
			this.sumG=sumG;
			this.h=h;
		}
	}
	public static class PowerfulNumber	{
		public final long n;
		public final LongIntMap primes;
		// Generates the square.
		private PowerfulNumber(long prime)	{
			n=prime*prime;
			primes=HashLongIntMaps.newMutableMap();
			primes.put(prime,2);
		}
		// Multiples by a prime.
		private PowerfulNumber(PowerfulNumber n1,long prime)	{
			n=n1.n*prime;
			primes=HashLongIntMaps.newMutableMap(n1.primes);
			primes.addValue(prime,1);
		}
		private PowerfulNumber(PowerfulNumber n1,PowerfulNumber n2)	{
			// Assumes n1 and n2 coprime.
			n=n1.n*n2.n;
			primes=HashLongIntMaps.newMutableMap();
			LongIntConsumer put=(long p,int pow)->primes.put(p,pow);
			n1.primes.forEach(put);
			n2.primes.forEach(put);
		}
		public static PowerfulNumber combine(PowerfulNumber n1,PowerfulNumber n2)	{
			return (n1==null)?n2:new PowerfulNumber(n1,n2);
		}
	}
	private final List<List<PowerfulNumber>> primePowers;
	public PowerfulNumberGenerator(long limit)	{
		long[] primes=Primes.listLongPrimesAsArray(LongMath.sqrt(limit,RoundingMode.DOWN));
		primePowers=new ArrayList<>(primes.length);
		for (int i=0;i<primes.length;++i)	{
			long p=primes[i];
			List<PowerfulNumber> thisPowers=new ArrayList<>();
			PowerfulNumber current=new PowerfulNumber(p);
			long thisLimit=limit/p;
			for (;;)	{
				thisPowers.add(current);
				if (current.n>thisLimit) break;
				current=new PowerfulNumber(current,p);
			}
			if (thisPowers.isEmpty()) throw new RuntimeException("Something has gone very wrong!");
			primePowers.add(thisPowers);
		}
	}
	/*
	 * Take into account that this will NOT generate the number 1, which is normally needed in multiplicative function summations, but it's a
	 * special case because it doesn't have any prime factor.
	 */
	public void generatePowerfulNumbers(long limit,Consumer<PowerfulNumber> action)	{
		generatePowerfulNumbersRecursive(limit,action,0,null);
	}
	private void generatePowerfulNumbersRecursive(long limit,Consumer<PowerfulNumber> action,int startIndex,PowerfulNumber current)	{
		long thisLimit=(current==null)?limit:limit/current.n;
		for (int i=startIndex;i<primePowers.size();++i)	{
			List<PowerfulNumber> currentList=primePowers.get(i);
			if (currentList.get(0).n>thisLimit) break;
			for (PowerfulNumber p:currentList)	{
				if (p.n>thisLimit) break;
				PowerfulNumber next=PowerfulNumber.combine(current,p);
				action.accept(next);
				generatePowerfulNumbersRecursive(limit,action,i+1,next);
			}
		}
	}
	public long sumMultiplicativeFunction(LongUnaryOperator sumG,ToLongFunction<PowerfulNumber> h,long limit,long mod)	{
		/*
		 * Java doesn't handle closures very well, so an array (used like a pointer) is needed.
		 * This happens because an array is final (the pointer itself doesn't change), but a long is not.
		 */
		long[] result=new long[] {sumG.applyAsLong(limit)};	// Initialised as the value for 1 (special case).
		Consumer<PowerfulNumber> accumulator=(PowerfulNumber num)->	{
			long toAdd=(sumG.applyAsLong(limit/num.n)*h.applyAsLong(num))%mod;
			result[0]=(result[0]+toAdd)%mod;
		};
		generatePowerfulNumbers(limit,accumulator);
		return result[0];
	}
	public long sumMultiplicativeFunctions(Collection<MultiplicativeFunctionScheme> schemes,long limit,long mod)	{
		long[] result=new long[] {0l};
		for (MultiplicativeFunctionScheme scheme:schemes) result[0]+=scheme.sumG.applyAsLong(limit);
		result[0]%=mod;
		Consumer<PowerfulNumber> accumulator=(PowerfulNumber num)->	{
			long thisLimit=limit/num.n;
			for (MultiplicativeFunctionScheme scheme:schemes)	{
				long toAdd=(scheme.sumG.applyAsLong(thisLimit)*scheme.h.applyAsLong(num))%mod;
				result[0]+=toAdd;
			}
			result[0]%=mod;
		};
		generatePowerfulNumbers(limit,accumulator);
		return result[0];
	}
	
	public static void main(String[] args)	{
		long limit=LongMath.pow(10l,16);
		int[] counter=new int[] {0};
		long tic=System.nanoTime();
		PowerfulNumberGenerator gen=new PowerfulNumberGenerator(limit);
		gen.generatePowerfulNumbers(limit,(PowerfulNumber x)->++counter[0]);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("There are "+counter[0]+" powerful numbers up to "+limit+". The generation took "+seconds+" seconds.");
	}
}