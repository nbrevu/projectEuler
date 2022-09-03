package com.euler;

import java.math.RoundingMode;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.Pair;
import com.google.common.math.LongMath;

public class Euler646 {
	// Pues no funciona :(. Pero estoy cerca, casi seguro.
	private final static int FACT_OPERAND=30;
	private final static long L=LongMath.pow(10l,8);
	private final static long H=LongMath.pow(10l,12);
	private final static long MOD=Long.MAX_VALUE;
	
	private static class NumberAndSign	{
		public final long number;
		public final boolean sign;	// True if negative.
		public NumberAndSign()	{
			this.number=0l;
			this.sign=false;
		}
		public NumberAndSign(long number,boolean sign)	{
			this.number=number;
			this.sign=sign;
		}
		public NumberAndSign add(NumberAndSign other,long mod)	{
			if (sign==other.sign)	{
				long newNumber=(number+other.number)%mod;
				return new NumberAndSign(newNumber,sign);
			}	else if (number>=other.number)	{
				long newNumber=number-other.number;
				return new NumberAndSign(newNumber,sign);
			}	else	{
				long newNumber=other.number-number;
				return new NumberAndSign(newNumber,other.sign);
			}
		}
		public NumberAndSign subtract(NumberAndSign other,long mod)	{
			if (sign!=other.sign)	{
				long newNumber=(number+other.number)%mod;
				return new NumberAndSign(newNumber,sign);
			}	else if (number>=other.number)	{
				long newNumber=number-other.number;
				return new NumberAndSign(newNumber,sign);
			}	else	{
				long newNumber=other.number-number;
				return new NumberAndSign(newNumber,other.sign);
			}
		}
		public long getAsPositive(long mod)	{
			return sign?(mod-number):number;
		}
	}
	
	private static class NumberGenerator	{
		private final long mod;
		private final NavigableMap<Double,NumberAndSign> numbers;
		public NumberGenerator(long mod)	{
			this.mod=mod;
			numbers=new TreeMap<>();
			numbers.put(0d,new NumberAndSign(1l,false));
		}
		public void addNumber(long number,int exp)	{
			double log=Math.log(number);
			NavigableMap<Double,NumberAndSign> toAdd=new TreeMap<>();
			for (int i=1;i<=exp;++i)	{
				double thisLog=i*log;
				long thisFactor=LongMath.pow(number,i)%mod;
				for (Map.Entry<Double,NumberAndSign> entry:numbers.entrySet())	{
					double newLog=thisLog+entry.getKey();
					NumberAndSign previous=entry.getValue();
					long newNumber=(thisFactor*previous.number)%mod;
					boolean newSign=((i%2)==1)^previous.sign;
					toAdd.put(newLog,new NumberAndSign(newNumber,newSign));
				}
			}
			numbers.putAll(toAdd);
		}
		public void addAll(Map<Integer,Integer> newNumbers)	{
			for (Map.Entry<Integer,Integer> entry:newNumbers.entrySet()) addNumber(entry.getKey(),entry.getValue());
		}
		public static NavigableMap<Double,NumberAndSign> getForNumbers(Map<Integer,Integer> numbers,long mod)	{
			NumberGenerator result=new NumberGenerator(mod);
			result.addAll(numbers);
			return result.numbers;
		}
	}
	
	private static Pair<NavigableMap<Double,NumberAndSign>,NavigableMap<Double,NumberAndSign>> separate(NavigableMap<Integer,Integer> in,long limit,long mod)	{
		long product=1;
		int maxPrimeForFirstSet=0;
		for (Map.Entry<Integer,Integer> entry:in.entrySet())	{
			product*=1+entry.getValue();
			if (product>=limit)	{
				maxPrimeForFirstSet=entry.getKey();
				break;
			}
		}
		NavigableMap<Integer,Integer> map1=in.headMap(maxPrimeForFirstSet,true);
		NavigableMap<Integer,Integer> map2=in.tailMap(maxPrimeForFirstSet,false);
		NavigableMap<Double,NumberAndSign> result1=NumberGenerator.getForNumbers(map1,mod);
		NavigableMap<Double,NumberAndSign> result2=NumberGenerator.getForNumbers(map2,mod);
		return new Pair<>(result1,result2);
	}
		
	private static Pair<NavigableMap<Double,NumberAndSign>,NavigableMap<Double,NumberAndSign>> separate(NavigableMap<Integer,Integer> in,long mod)	{
		long product=1;
		for (int exp:in.values()) product*=1+exp;
		return separate(in,LongMath.sqrt(product,RoundingMode.DOWN),mod);
	}
	
	private static NavigableMap<Double,NumberAndSign> getCumulativeMap(NavigableMap<Double,NumberAndSign> in,long mod)	{
		NavigableMap<Double,NumberAndSign> result=new TreeMap<>();
		NumberAndSign current=new NumberAndSign();
		result.put(Double.NEGATIVE_INFINITY,current);
		for (Map.Entry<Double,NumberAndSign> entry:in.entrySet())	{
			current=current.add(entry.getValue(),mod);
			result.put(entry.getKey(),current);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		NavigableMap<Integer,Integer> factors=EulerUtils.getPrimeFactorsInFactorial(FACT_OPERAND);
		Pair<NavigableMap<Double,NumberAndSign>,NavigableMap<Double,NumberAndSign>> maps=separate(factors,MOD);
		NavigableMap<Double,NumberAndSign> map1=maps.first;
		NavigableMap<Double,NumberAndSign> map2=maps.second;
		/*
		 * Considering the way we have built the map, the second map is likely to have fewer elements. Therefore the best way to
		 * proceed is to use the cumulative version of the first map, iterating over the second.
		 */
		double minLog=Math.log(L);
		double maxLog=Math.log(H);
		NavigableMap<Double,NumberAndSign> cumulativeMap=getCumulativeMap(map1,MOD);
		NumberAndSign result=new NumberAndSign();
		for (Map.Entry<Double,NumberAndSign> entry:map2.entrySet())	{
			double thisLog=entry.getKey();
			double upperBound=maxLog-thisLog;
			double lowerBound=minLog-thisLog;
			NumberAndSign upperElement=cumulativeMap.floorEntry(upperBound).getValue();
			NumberAndSign lowerElement=cumulativeMap.floorEntry(lowerBound).getValue();
			NumberAndSign diff=upperElement.subtract(lowerElement,MOD);
			result=result.add(diff,MOD);
		}
		long value=result.getAsPositive(MOD);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(value);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
