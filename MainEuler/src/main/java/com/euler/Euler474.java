package com.euler;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.collect.ImmutableMap;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler474 {
	private final static int FACT_OPERAND=IntMath.pow(10,6);
	private final static int DIGITS=5;
	private final static int REMAINDER=65432;
	private final static long MOD=LongMath.pow(10l,16)+61l;
	
	private static class SortedDivisorsGenerator	{
		private final int numDigits;
		private final int remainder;
		private final BigInteger bigMod;
		private final int mod10;
		private final int totient;
		public SortedDivisorsGenerator(int numDigits,int remainder,long mod)	{
			this.numDigits=numDigits;
			this.remainder=remainder;
			bigMod=BigInteger.valueOf(mod);
			mod10=IntMath.pow(10,numDigits);
			totient=4*IntMath.pow(10,numDigits-1);
		}
		public SortedDivisors getFor2(int power)	{
			return getFor2Or5(2,power);
		}
		public SortedDivisors getFor5(int power)	{
			return getFor2Or5(5,power);
		}
		public SortedDivisors getForPrime(int prime,int power)	{
			Map<Integer,BigInteger> result=generatePowersWithTotient(prime,power);
			return new SortedDivisors(result);
		}
		public SortedDivisors combine(SortedDivisors s1,SortedDivisors s2)	{
			Map<Integer,BigInteger> result=new HashMap<>();
			for (Map.Entry<Integer,BigInteger> e1:s1.digitsMap.entrySet()) for (Map.Entry<Integer,BigInteger> e2:s2.digitsMap.entrySet())	{
				int newKey=(e1.getKey()*e2.getKey())%mod10;
				BigInteger newValue=e1.getValue().multiply(e2.getValue());
				EulerUtils.increaseCounter(result,newKey,newValue,bigMod);
			}
			return new SortedDivisors(result);
		}
		private SortedDivisors getFor2Or5(int prime,int power)	{
			int maxPower=maxPowerOf(prime,remainder);
			Map<Integer,BigInteger> result=(maxPower>=numDigits)?generatePowersFull(prime,power):ImmutableMap.of(IntMath.pow(prime,maxPower),BigInteger.ONE);
			return new SortedDivisors(result);
		}
		private Map<Integer,BigInteger> generatePowersFull(int prime,int power)	{
			Map<Integer,BigInteger> result=new HashMap<>();
			int current=1;
			result.put(current,BigInteger.ONE);
			for (int p=1;p<=power;++p)	{
				current=(current*prime)%mod10;
				EulerUtils.increaseCounter(result,current,BigInteger.ONE);
			}
			return result;
		}
		private Map<Integer,BigInteger> generatePowersWithTotient(int prime,int power)	{
			if (power<=totient) return generatePowersFull(prime,power);
			Map<Integer,BigInteger> result=new HashMap<>();
			int q=power/totient;
			int r=power%totient;
			BigInteger base=BigInteger.valueOf(q);
			BigInteger basePlus=base.add(BigInteger.ONE);
			int current=1;
			result.put(current,basePlus);
			for (int p=1;p<=r;++p)	{
				current=(current*prime)%mod10;
				EulerUtils.increaseCounter(result,current,basePlus);
			}
			if (q>0) for (int p=r+1;p<totient;++p)	{
				current=(current*prime)%mod10;
				EulerUtils.increaseCounter(result,current,base);
			}
			assert (current*prime)%mod10==1;
			return result;
		}
		private static int maxPowerOf(int base,int number)	{
			int result=0;
			while ((number%base)==0)	{
				++result;
				number/=base;
			}
			return result;
		}
		public class SortedDivisors	{
			private Map<Integer,BigInteger> digitsMap;
			private SortedDivisors(Map<Integer,BigInteger> digitsMap)	{
				this.digitsMap=digitsMap;
			}
			public BigInteger get(int key)	{
				return digitsMap.getOrDefault(key,BigInteger.ZERO);
			}
			@Override
			public String toString()	{
				SortedMap<Integer,BigInteger> tmp=new TreeMap<>(digitsMap);
				StringBuilder sb=new StringBuilder();
				boolean first=true;
				for (Map.Entry<Integer,BigInteger> entry:tmp.entrySet())	{
					if (first) first=false;
					else sb.append(", ");
					sb.append("[").append(entry.getKey()).append("->").append(entry.getValue().toString()).append("]");
				}
				return sb.toString();
			}
			// This is here for debugging purposes.
			@SuppressWarnings("unused")
			public BigInteger count()	{
				BigInteger result=BigInteger.ZERO;
				for (BigInteger counter:digitsMap.values()) result=result.add(counter);
				return result;
			}
		}
	}
	
	private static int timesInFactorial(int prime,int factorial)	{
		int result=0;
		while (factorial>=prime)	{
			int div=factorial/prime;
			result+=div;
			factorial=div;
		}
		return result;
	}
	
	private static long countDivisors(int factOperand,int digits,int remainder,long mod)	{
		SortedDivisorsGenerator gen=new SortedDivisorsGenerator(digits,remainder,mod);
		List<Integer> primes=Primes.listIntPrimes(factOperand);
		primes=primes.subList(3,primes.size());	// 2, 3 and 5 are covered separately.
		SortedDivisorsGenerator.SortedDivisors result=gen.getFor2(timesInFactorial(2,factOperand));
		result=gen.combine(result,gen.getForPrime(3,timesInFactorial(3,factOperand)));
		result=gen.combine(result,gen.getFor5(timesInFactorial(5,factOperand)));
		for (int p:primes)	{
			System.out.println(""+p+"...");
			result=gen.combine(result,gen.getForPrime(p,timesInFactorial(p,factOperand)));
		}
		return result.get(remainder).longValue();
	}
	
	public static void main(String[] args)	{
		System.out.println(countDivisors(12,2,12,MOD));
		System.out.println(countDivisors(50,3,123,MOD));
		long tic=System.nanoTime();
		System.out.println(countDivisors(FACT_OPERAND,DIGITS,REMAINDER,MOD));
		long tac=System.nanoTime();
		double seconds=((tac-tic)*1e-9);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
