package com.euler;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler646_2 {
	private final static int FACT_OPERAND=70;
	private final static double LOG_L=20*Math.log(10);
	private final static double LOG_H=60*Math.log(10);
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private static class DivisorGenerator	{
		private final NavigableMap<Double,Long> positive;
		private final NavigableMap<Double,Long> negative;
		private final double maxLog;
		private final long mod;
		public DivisorGenerator(double maxLog,long mod)	{
			positive=new TreeMap<>();
			positive.put(0d,1l);
			negative=new TreeMap<>();
			this.maxLog=maxLog;
			this.mod=mod;
		}
		private int addDivisors(Map.Entry<Double,Long> current,int prime,int power,double primeLog,boolean firstNegative,NavigableMap<Double,Long> toAddPositive,NavigableMap<Double,Long> toAddNegative)	{
			double currentLog=current.getKey();
			long currentNumber=current.getValue();
			boolean currentTarget=firstNegative;
			for (int i=0;i<power;++i)	{
				currentLog+=primeLog;
				if (currentLog>maxLog) return i;
				currentNumber*=prime;
				currentNumber%=mod;
				NavigableMap<Double,Long> target=currentTarget?toAddNegative:toAddPositive;
				target.put(currentLog,currentNumber);
				currentTarget=!currentTarget;
			}
			return power;
		}
		private void addFactor(int prime,int power)	{
			NavigableMap<Double,Long> toAddPositive=new TreeMap<>();
			NavigableMap<Double,Long> toAddNegative=new TreeMap<>();
			double primeLog=Math.log(prime);
			for (Map.Entry<Double,Long> entry:positive.entrySet()) if (addDivisors(entry,prime,power,primeLog,true,toAddPositive,toAddNegative)==0) break;
			for (Map.Entry<Double,Long> entry:negative.entrySet()) if (addDivisors(entry,prime,power,primeLog,false,toAddPositive,toAddNegative)==0) break;
			positive.putAll(toAddPositive);
			negative.putAll(toAddNegative);
			System.out.println("Después de añadir el "+prime+", tengo ("+positive.size()+", "+negative.size()+") cosas.");
		}
		public void addFactors(NavigableMap<Integer,Integer> factors)	{
			for (Map.Entry<Integer,Integer> entry:factors.entrySet()) addFactor(entry.getKey(),entry.getValue());
		}
		public long sum(double minLog)	{
			long result=0l;
			for (long toAdd:positive.tailMap(minLog).values())	{
				result+=toAdd;
				result%=mod;
			}
			for (long toAdd:negative.tailMap(minLog).values())	{
				result+=mod-toAdd;
				result%=mod;
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		DivisorGenerator generator=new DivisorGenerator(LOG_H,MOD);
		generator.addFactors(EulerUtils.getPrimeFactorsInFactorial(FACT_OPERAND));
		long result=generator.sum(LOG_L);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}