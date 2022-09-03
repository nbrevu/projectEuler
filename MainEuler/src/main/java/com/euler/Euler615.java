package com.euler;

import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler615 {
	private final static int MIN_FACTORS=1000000;
	private final static int ORDINAL=1000000;
	private final static long MOD=123454321;
	private final static int HOW_MANY_PRIMES=70000;	// Use 20000 for 1e6 or 70000 for 5e6.
	
	private static class NumberAndLog implements Comparable<NumberAndLog>	{
		public final long number;
		public final double log;
		public NumberAndLog(long number)	{
			this.number=number;
			log=Math.log(number);
		}
		@Override
		public boolean equals(Object other)	{
			// I'm not even going to try to catch ClassCastException.
			NumberAndLog n=(NumberAndLog)other;
			return n.number==number;
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(number);
		}
		@Override
		public int compareTo(NumberAndLog other)	{
			return Long.compare(number,other.number);
		}
	}
	
	private final static NumberAndLog TWO=new NumberAndLog(2l);

	private static class FactoredNumber implements Comparable<FactoredNumber>	{
		private final NavigableMap<NumberAndLog,Integer> factors;
		public final double log;
		public FactoredNumber(int minFactors)	{
			factors=new TreeMap<>();
			factors.put(TWO,minFactors);
			log=calculateLog(factors);
		}
		private FactoredNumber(NavigableMap<NumberAndLog,Integer> factors)	{
			this.factors=factors;
			log=calculateLog(factors);
		}
		private static double calculateLog(Map<NumberAndLog,Integer> factors)	{
			double result=0;
			for (Map.Entry<NumberAndLog,Integer> factor:factors.entrySet())	{
				if (factor.getKey().number==2)	{
					result+=factor.getKey().log*(factor.getValue()-MIN_FACTORS);
				}	else	{
					result+=factor.getKey().log*factor.getValue();
				}
			}
			return result;
		}
		@Override
		public boolean equals(Object other)	{
			FactoredNumber f=(FactoredNumber)other;
			return f.factors.equals(factors);
		}
		@Override
		public int hashCode()	{
			return factors.hashCode();
		}
		@Override
		public int compareTo(FactoredNumber other)	{
			return Double.compare(log,other.log);
		}
		public NavigableSet<FactoredNumber> getChildren(NavigableSet<NumberAndLog> primeFactors,double limit)	{
			NumberAndLog smallestFactor=factors.firstKey();
			NumberAndLog biggestFactor=factors.lastKey();
			NavigableSet<NumberAndLog> primesToAdd=primeFactors.tailSet(biggestFactor,true);
			NavigableSet<NumberAndLog> primesToExchange=primeFactors.tailSet(smallestFactor,false);
			NavigableSet<FactoredNumber> result=new TreeSet<>();
			for (NumberAndLog toAdd:primesToAdd)	{
				double expectedLog=log+toAdd.log;
				if (expectedLog>limit) break;
				else result.add(addFactor(toAdd));
			}
			double minusLog=log-smallestFactor.log;
			for (NumberAndLog toExchange:primesToExchange)	{
				double expectedLog=minusLog+toExchange.log;
				if (expectedLog>limit) break;
				else result.add(exchangeFactor(smallestFactor,toExchange));
			}
			return result;
		}
		private FactoredNumber exchangeFactor(NumberAndLog oldFactor,NumberAndLog newFactor)	{
			NavigableMap<NumberAndLog,Integer> newNumber=new TreeMap<>(factors);
			EulerUtils.decreaseCounter(newNumber,oldFactor);
			EulerUtils.increaseCounter(newNumber,newFactor);
			return new FactoredNumber(newNumber);
		}
		private FactoredNumber addFactor(NumberAndLog newFactor)	{
			NavigableMap<NumberAndLog,Integer> newNumber=new TreeMap<>(factors);
			EulerUtils.increaseCounter(newNumber,newFactor);
			return new FactoredNumber(newNumber);
		}
		private long getNumber(long mod)	{
			long result=1l;
			for (Map.Entry<NumberAndLog,Integer> entry:factors.entrySet())	{
				long thisFactor=EulerUtils.expMod(entry.getKey().number,entry.getValue(),mod);
				result=(result*thisFactor)%mod;
			}
			return result;
		}
		private NumberAndLog getBiggestFactorUsed()	{
			return factors.lastKey();
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			boolean first=true;
			for (Map.Entry<NumberAndLog,Integer> entry:factors.entrySet())	{
				if (first) first=false;
				else sb.append('*');
				sb.append('(').append(entry.getKey().number).append('^').append(entry.getValue()).append(')');
			}
			return sb.toString();
		}
	}
	
	private static class FactoredNumberFinder	{
		private final NavigableSet<NumberAndLog> primeFactors;
		private NumberAndLog biggestFactorUsed=null;
		private NavigableSet<FactoredNumber> currentSet;
		public FactoredNumberFinder(FactoredNumber initial,NavigableSet<NumberAndLog> primeFactors)	{
			this.primeFactors=primeFactors;
			currentSet=new TreeSet<>();
			currentSet.add(initial);
		}
		public FactoredNumber getNth(int n)	{
			biggestFactorUsed=new NumberAndLog(1);
			for (int i=0;i<n-1;++i)	{
				FactoredNumber next=currentSet.pollFirst();
				double limit=(currentSet.size()>=(n-2-i))?(currentSet.last().log):Double.MAX_VALUE;
				Set<FactoredNumber> children=next.getChildren(primeFactors,limit);
				currentSet.addAll(children);
				int desiredSize=n-1-i;
				if (currentSet.size()>desiredSize) EulerUtils.trimToSize(currentSet,desiredSize);
				biggestFactorUsed=EulerUtils.max(biggestFactorUsed,next.getBiggestFactorUsed());
			}
			return currentSet.pollFirst();
		}
		public NumberAndLog getBiggestFactorUsed()	{
			return biggestFactorUsed;
		}
	}
	
	private static NavigableSet<NumberAndLog> getFirstNPrimes(int n)	{
		boolean[] composites=Primes.sieve(100*n);	// Quick and dirty, but it does the job.
		NavigableSet<NumberAndLog> result=new TreeSet<>();
		result.add(TWO);
		for (int i=3;i<composites.length;i+=2) if (!composites[i])	{
			result.add(new NumberAndLog(i));
			if (result.size()==n) break;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		FactoredNumber initial=new FactoredNumber(MIN_FACTORS);
		NavigableSet<NumberAndLog> firstPrimes=getFirstNPrimes(HOW_MANY_PRIMES);
		FactoredNumberFinder finder=new FactoredNumberFinder(initial,firstPrimes);
		FactoredNumber resultNumber=finder.getNth(ORDINAL);
		long result=resultNumber.getNumber(MOD);
		long tac=System.nanoTime();
		System.out.println(resultNumber.toString());
		System.out.println(result);
		NumberAndLog biggestPrimeNeeded=finder.getBiggestFactorUsed();
		int order=1+firstPrimes.headSet(biggestPrimeNeeded).size();
		System.out.println("Biggest prime used: "+biggestPrimeNeeded.number+" ("+order+"th prime).");
		double seconds=((double)(tac-tic))*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
