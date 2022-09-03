package com.euler;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.collect.ImmutableSet;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler474_2 {
	// Vale, esto no funciona porque hay minolles de repeticiones :(. La táctica de Euler474 parece la correcta, pero algo está mal y no sé qué es.
	private final static int FACT_OPERAND=IntMath.pow(10,6);
	private final static int DIGITS=5;
	private final static int REMAINDER=65432;
	private final static long MOD=LongMath.pow(10l,16)+61l;
	
	private static class SortedDivisors	{
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
	}
	
	private static interface Sanitizer	{
		public void sanitize(Map<Integer,Integer> divisorList,Integer prime);
	}
	private static enum NoSanitizer implements Sanitizer	{
		INSTANCE;
		@Override
		public void sanitize(Map<Integer,Integer> divisorList,Integer prime)	{}
	}
	private static enum ZeroSanitizer implements Sanitizer	{
		INSTANCE;
		@Override
		public void sanitize(Map<Integer,Integer> divisorList,Integer prime)	{
			divisorList.remove(prime);
		}
	}
	private static class FixedSanitizer implements Sanitizer	{
		private final int maxAllowed;
		public FixedSanitizer(int maxAllowed)	{
			this.maxAllowed=maxAllowed;
		}
		@Override
		public void sanitize(Map<Integer,Integer> divisorList,Integer prime)	{
			Integer value=divisorList.get(prime);
			if (((value)!=null)&&(value.intValue()>maxAllowed)) divisorList.put(prime,maxAllowed);
		}
	}
	
	private static class SortedDivisorsGenerator	{
		private final Sanitizer sanitizer2;
		private final Sanitizer sanitizer5;
		private final int[] firstPrimes;
		private final BigInteger bigMod;
		private final int mod10;
		private final Set<Integer> forbiddenDivisors;
		public SortedDivisorsGenerator(int numDigits,int lastDigits,long mod,int maxValue)	{
			forbiddenDivisors=new HashSet<>();
			int maxPower2=maxPowerOf(2,lastDigits);
			if (maxPower2>=numDigits) sanitizer2=NoSanitizer.INSTANCE;
			else if (maxPower2==0) sanitizer2=ZeroSanitizer.INSTANCE;
			else	{
				sanitizer2=new FixedSanitizer(maxPower2);
				forbiddenDivisors.add(IntMath.pow(2,1+maxPower2));
			}
			int maxPower5=maxPowerOf(5,lastDigits);
			if (maxPower5>=numDigits) sanitizer5=NoSanitizer.INSTANCE;
			else if (maxPower5==0) sanitizer5=ZeroSanitizer.INSTANCE;
			else	{
				sanitizer5=new FixedSanitizer(maxPower5);
				forbiddenDivisors.add(IntMath.pow(5,1+maxPower5));
			}
			firstPrimes=Primes.firstPrimeSieve(1+maxValue);
			bigMod=BigInteger.valueOf(mod);
			mod10=IntMath.pow(10,numDigits);
		}
		public SortedDivisors getForNumber(int in)	{
			Map<Integer,Integer> allFactors=getFactorList(in);
			sanitizeDivisorList(allFactors);
			Set<Integer> divisors=getAllDivisors(allFactors);
			return new SortedDivisors(translateSet(divisors));
		}
		private Map<Integer,BigInteger> translateSet(Set<Integer> set)	{
			Map<Integer,BigInteger> result=new HashMap<>();
			for (Integer i:set) EulerUtils.increaseCounter(result,i%mod10,BigInteger.ONE);
			return result;
		}
		private Map<Integer,Integer> getFactorList(int in)	{
			Map<Integer,Integer> result=new HashMap<>();
			for (;;)	{
				int prime=firstPrimes[in];
				if (prime==0)	{
					EulerUtils.increaseCounter(result,in);
					return result;
				}	else	{
					EulerUtils.increaseCounter(result,prime);
					in/=prime;
				}
			}
		}
		private void sanitizeDivisorList(Map<Integer,Integer> list)	{
			sanitizer2.sanitize(list,2);
			sanitizer5.sanitize(list,5);
		}
		private Set<Integer> getAllDivisors(Map<Integer,Integer> sanitizedList)	{
			Set<Integer> current=ImmutableSet.of(1);
			for (Map.Entry<Integer,Integer> entry:sanitizedList.entrySet())	{
				Set<Integer> newSet=new HashSet<>(current);
				int prime=entry.getKey();
				int pow=entry.getValue();
				for (int div:current)	{
					int toAdd=div;
					for (int i=1;i<=pow;++i)	{
						toAdd*=prime;
						newSet.add(toAdd);
					}
				}
				current=newSet;
			}
			return current;
		}
		public SortedDivisors combine(SortedDivisors s1,SortedDivisors s2)	{
			Map<Integer,BigInteger> result=new HashMap<>();
			for (Map.Entry<Integer,BigInteger> e1:s1.digitsMap.entrySet()) for (Map.Entry<Integer,BigInteger> e2:s2.digitsMap.entrySet())	{
				int newKey=(e1.getKey()*e2.getKey())%mod10;
				if (isForbidden(newKey)) continue;
				BigInteger newValue=e1.getValue().multiply(e2.getValue());
				EulerUtils.increaseCounter(result,newKey,newValue,bigMod);
			}
			return new SortedDivisors(result);
		}
		private boolean isForbidden(int in)	{
			for (int forbidden:forbiddenDivisors) if ((in%forbidden)==0) return true;
			return false;
		}
		private static int maxPowerOf(int base,int number)	{
			int result=0;
			while ((number%base)==0)	{
				++result;
				number/=base;
			}
			return result;
		}
	}
	
	private static long countDivisors(int factOperand,int digits,int remainder,long mod)	{
		SortedDivisorsGenerator gen=new SortedDivisorsGenerator(digits,remainder,mod,factOperand);
		SortedDivisors result=gen.getForNumber(2);
		for (int i=3;i<=factOperand;++i)	{
			SortedDivisors result2=gen.getForNumber(i);
			result=gen.combine(result,gen.getForNumber(i));
		}
		return result.get(remainder).longValue();
	}
	
	public static void main(String[] args)	{
		System.out.println(countDivisors(12,2,12,MOD));
		System.out.println(countDivisors(50,3,123,MOD));
		/*
		long tic=System.nanoTime();
		System.out.println(countDivisors(FACT_OPERAND,DIGITS,REMAINDER,MOD));
		long tac=System.nanoTime();
		double seconds=((tac-tic)*1e-9);
		System.out.println("Elapsed "+seconds+" seconds.");
		*/
	}
}
