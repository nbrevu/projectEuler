package com.euler;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler455 {
	private final static long LIMIT=LongMath.pow(10l,6);
	private final static int MAX_DIGITS=9;
	private final static long MOD=LongMath.pow(10l,MAX_DIGITS);
	
	private static class FinderStep	{
		public final int increment;
		public final int limit;
		private FinderStep(int increment,int limit)	{
			this.increment=increment;
			this.limit=limit;
		}
		public final static FinderStep INCREMENT_OF_2=new FinderStep(2,100);
		public final static FinderStep INCREMENT_OF_4=new FinderStep(4,100);
		public final static FinderStep INCREMENT_OF_50=new FinderStep(50,100);
		public final static List<FinderStep> POWERS_OF_TEN=createPowersOfTen();
		private static List<FinderStep> createPowersOfTen()	{
			int N=MAX_DIGITS-1;
			FinderStep[] result=new FinderStep[N];
			result[0]=new FinderStep(10,100);
			for (int i=1;i<N;++i)	{
				int newLimit=10*result[i-1].limit;
				result[i]=new FinderStep(result[i-1].limit,newLimit);
			}
			return Arrays.asList(result).subList(1,result.length);
		}
	}

	private static class FinderSchematic	{
		public final int initialPower;
		public final FinderStep initialSearch;
		public final int initialDigits;
		public List<FinderStep> subsequentSteps;
		private FinderSchematic(int initialPower,FinderStep initialSearch,int initialDigits,List<FinderStep> subsequentSteps)	{
			this.initialPower=initialPower;
			this.initialSearch=initialSearch;
			this.initialDigits=initialDigits;
			this.subsequentSteps=Collections.unmodifiableList(subsequentSteps);
		}
		private final static FinderSchematic FOR_ODD_NUMBERS=new FinderSchematic(1,FinderStep.INCREMENT_OF_2,2,FinderStep.POWERS_OF_TEN);
		private final static FinderSchematic FOR_EVEN_NUMBERS=new FinderSchematic(4,FinderStep.INCREMENT_OF_4,2,FinderStep.POWERS_OF_TEN);
		private final static FinderSchematic FOR_5=new FinderSchematic(25,FinderStep.INCREMENT_OF_50,2,FinderStep.POWERS_OF_TEN);
		public static FinderSchematic getForNumber(long n)	{
			if ((n%10)==0) return null;
			else if ((n%5)==0) return FOR_5;
			else return ((n%2)==1)?FOR_ODD_NUMBERS:FOR_EVEN_NUMBERS;
		}
	}
	
	private static class EndDigitsChecker	{
		private final long[] powersOfTen;
		public EndDigitsChecker(int maxPower)	{
			powersOfTen=new long[1+maxPower];
			powersOfTen[0]=1;
			for (int i=1;i<=maxPower;++i) powersOfTen[i]=10*powersOfTen[i-1];
		}
		public boolean checkMatch(long n1,long n2,int digits)	{
			long mod=powersOfTen[digits];
			n1%=mod;
			n2%=mod;
			return n1==n2;
		}
	}
	
	private static class PowerFinder	{
		private static EndDigitsChecker checker;
		public static void setDigitsChecker(EndDigitsChecker theChecker)	{
			checker=theChecker;
		}
		private final long number;
		private final long mod;
		private final FinderSchematic schematic;
		private final Map<Integer,Long> powers;
		public PowerFinder(long number,long mod)	{
			this.number=number;
			this.mod=mod;
			schematic=FinderSchematic.getForNumber(number);
			powers=new HashMap<>();
		}
		private long getPower(int exponent)	{
			Long power=powers.get(exponent);
			if (power==null)	{
				power=EulerUtils.expMod(number,exponent,mod);
				powers.put(exponent,power);
			}
			return power;
		}
		public Optional<Integer> getBestPower()	{
			if (schematic==null) return Optional.empty();
			NavigableSet<Integer> currentSet=evaluateFirstStep();
			int digits=schematic.initialDigits;
			for (FinderStep step:schematic.subsequentSteps)	{
				if (currentSet.isEmpty()) return Optional.empty();
				++digits;
				currentSet=evaluateStep(currentSet,step,digits);
			}
			if (currentSet.isEmpty()) return Optional.empty();
			return Optional.of(currentSet.last());
		}
		private NavigableSet<Integer> evaluateFirstStep()	{
			NavigableSet<Integer> result=new TreeSet<>();
			evaluateStep(schematic.initialPower,schematic.initialSearch,schematic.initialDigits,result);
			return result;
		}
		private NavigableSet<Integer> evaluateStep(NavigableSet<Integer> current,FinderStep step,int digits)	{
			NavigableSet<Integer> result=new TreeSet<>();
			for (int value:current) evaluateStep(value,step,digits,result);
			return result;
		}
		private void evaluateStep(int value,FinderStep step,int digits,NavigableSet<Integer> result)	{
			long currentNumber=getPower(value);
			long increment=getPower(step.increment);
			for (int i=value;i<step.limit;i+=step.increment)	{
				if (checker.checkMatch(i,currentNumber,digits))	{
					powers.put(i,currentNumber);
					result.add(i);
				}
				currentNumber=(currentNumber*increment)%mod;
			}
		}
	}
	
	public static void main(String[] args)	{
		PowerFinder.setDigitsChecker(new EndDigitsChecker(MAX_DIGITS));
		long sum=0;
		for (int i=2;i<=LIMIT;++i)	{
			PowerFinder pf=new PowerFinder(i,MOD);
			Optional<Integer> result=pf.getBestPower();
			if (result.isPresent()) sum+=result.get();
		}
		System.out.println(sum);
	}
}
