package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.euler.common.BigIntegerUtils.BigFactorialCache;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

public class Euler698 {
	// Another one right at the first try!
	private final static long LIMIT=111_111_111_111_222_333l;
	private final static long MOD=123_123_123l;
	
	private final static int[] FIRST_NUMBERS=new int[] {1,2,3,11,12,13,21,22,23,31,32,33};
	private final static BigFactorialCache FACTORIALS=new BigFactorialCache(99);
	
	private static BigInteger tripleCombinatorial(int a,int b,int c)	{
		return FACTORIALS.get(a+b+c).divide(FACTORIALS.get(a).multiply(FACTORIALS.get(b)).multiply(FACTORIALS.get(c)));
	}
	
	private static class ConcreteCombination	{
		private final int num1;
		private final int num2;
		private final int num3;
		public ConcreteCombination(int num1,int num2,int num3)	{
			this.num1=num1;
			this.num2=num2;
			this.num3=num3;
		}
		public BigInteger getCombinations()	{
			return tripleCombinatorial(num1,num2,num3);
		}
		public ConcreteCombination minus1()	{
			return new ConcreteCombination(num1-1,num2,num3);
		}
		public ConcreteCombination minus2()	{
			return new ConcreteCombination(num1,num2-1,num3);
		}
		public ConcreteCombination minus3()	{
			return new ConcreteCombination(num1,num2,num3-1);
		}
		public boolean has1()	{
			return num1>0;
		}
		public boolean has2()	{
			return num2>0;
		}
		public boolean has3()	{
			return num3>0;
		}
	}
	
	private static abstract class Combination123	{
		protected final static BigInteger THREE=BigInteger.valueOf(3);
		protected final static BigInteger SIX=BigInteger.valueOf(6);
		public abstract int getAmountOfDigits();
		public abstract BigInteger getCombinations();
		public abstract List<ConcreteCombination> getConcreteCombinations();
	}
	
	private static class SingleCombination extends Combination123	{
		private final int numDigits;
		public SingleCombination(int numDigits)	{
			this.numDigits=numDigits;
		}
		@Override
		public int getAmountOfDigits()	{
			return numDigits;
		}
		@Override
		public BigInteger getCombinations()	{
			return THREE;
		}
		@Override
		public List<ConcreteCombination> getConcreteCombinations()	{
			return Arrays.asList(new ConcreteCombination(numDigits,0,0),new ConcreteCombination(0,numDigits,0),new ConcreteCombination(0,0,numDigits));
		}
	}
	
	private static class DoubleCombination extends Combination123	{
		private final int digitsA;
		private final int digitsB;
		public DoubleCombination(int digitsA,int digitsB)	{
			this.digitsA=digitsA;
			this.digitsB=digitsB;
		}
		@Override
		public int getAmountOfDigits()	{
			return digitsA+digitsB;
		}
		@Override
		public BigInteger getCombinations()	{
			BigInteger factor=(digitsA==digitsB)?THREE:SIX;
			return factor.multiply(FACTORIALS.get(digitsA+digitsB).divide(FACTORIALS.get(digitsA).multiply(FACTORIALS.get(digitsB))));
		}
		@Override
		public List<ConcreteCombination> getConcreteCombinations()	{
			if (digitsA==digitsB) return Arrays.asList(new ConcreteCombination(digitsA,digitsA,0),new ConcreteCombination(digitsA,0,digitsA),new ConcreteCombination(0,digitsA,digitsA));
			else return Arrays.asList(new ConcreteCombination(digitsA,digitsB,0),new ConcreteCombination(digitsA,0,digitsB),new ConcreteCombination(0,digitsA,digitsB),new ConcreteCombination(digitsB,digitsA,0),new ConcreteCombination(digitsB,0,digitsA),new ConcreteCombination(0,digitsB,digitsA));
		}
	}
	
	private static class TripleCombination extends Combination123	{
		private final int digitsA;
		private final int digitsB;
		private final int digitsC;
		public TripleCombination(int digitsA,int digitsB,int digitsC)	{
			this.digitsA=digitsA;
			this.digitsB=digitsB;
			this.digitsC=digitsC;
		}
		@Override
		public int getAmountOfDigits()	{
			return digitsA+digitsB+digitsC;
		}
		@Override
		public BigInteger getCombinations()	{
			// ACHTUNG! Because of the way we generate the numbers, A==C if and only if A==B and B==C. We won't have A=C/=B.
			boolean equals1=(digitsA==digitsB);
			boolean equals2=(digitsB==digitsC);
			BigInteger factor;
			if (equals1&&equals2) factor=BigInteger.ONE;
			else if (equals1||equals2) factor=THREE;
			else factor=SIX;
			return factor.multiply(tripleCombinatorial(digitsA,digitsB,digitsC));
		}
		@Override
		public List<ConcreteCombination> getConcreteCombinations()	{
			boolean equals1=(digitsA==digitsB);
			boolean equals2=(digitsB==digitsC);
			if (equals1&&equals2) return Arrays.asList(new ConcreteCombination(digitsA,digitsA,digitsA));
			else if (equals1) return Arrays.asList(new ConcreteCombination(digitsA,digitsA,digitsC),new ConcreteCombination(digitsA,digitsC,digitsA),new ConcreteCombination(digitsC,digitsA,digitsA));
			else if (equals2) return Arrays.asList(new ConcreteCombination(digitsA,digitsB,digitsB),new ConcreteCombination(digitsB,digitsA,digitsB),new ConcreteCombination(digitsB,digitsB,digitsA));
			else return Arrays.asList(new ConcreteCombination(digitsA,digitsB,digitsC),new ConcreteCombination(digitsA,digitsC,digitsB),new ConcreteCombination(digitsB,digitsA,digitsC),new ConcreteCombination(digitsB,digitsC,digitsA),new ConcreteCombination(digitsC,digitsA,digitsB),new ConcreteCombination(digitsC,digitsB,digitsA));
		}
	}
	
	private static ListMultimap<Integer,Combination123> getAllCombinations(int[] array)	{
		List<Combination123> allCombinations=new ArrayList<>();
		int N=array.length;
		for (int i=0;i<N;++i)	{
			int numA=array[i];
			allCombinations.add(new SingleCombination(numA));
			for (int j=i;j<N;++j)	{
				int numB=array[j];
				allCombinations.add(new DoubleCombination(numA,numB));
				for (int k=j;k<N;++k)	{
					int numC=array[k];
					allCombinations.add(new TripleCombination(numA,numB,numC));
				}
			}
		}
		// Multimaps.index(allCombinations,Combination123::getAmountOfDigits); // Unsorted, not what I need. I'll just build it manually...
		ListMultimap<Integer,Combination123> result=MultimapBuilder.treeKeys().arrayListValues().build();
		for (Combination123 combination:allCombinations) result.put(combination.getAmountOfDigits(),combination);
		return result;
	}
	
	private static BigInteger tryOneDigit(List<ConcreteCombination> combinations,Predicate<ConcreteCombination> hasDigit,Function<ConcreteCombination,ConcreteCombination> subtractDigit,List<ConcreteCombination> output)	{
		BigInteger result=BigInteger.ZERO;
		for (ConcreteCombination combination:combinations) if (hasDigit.test(combination))	{
			ConcreteCombination child=subtractDigit.apply(combination);
			output.add(child);
			result=result.add(child.getCombinations());
		}
		return result;
	}
	
	private static boolean isZeroCombination(List<ConcreteCombination> combinations)	{
		if (combinations.size()!=1) return false;
		ConcreteCombination comb=combinations.get(0);
		return !(comb.has1()||comb.has2()||comb.has3());
	}
	
	private static void getNthCombination(List<ConcreteCombination> combinations,BigInteger N,StringBuilder accumulator)	{
		if (N.equals(BigInteger.ONE)&&isZeroCombination(combinations)) return;
		{
			List<ConcreteCombination> startingIn1=new ArrayList<>();
			BigInteger amountStartingIn1=tryOneDigit(combinations,ConcreteCombination::has1,ConcreteCombination::minus1,startingIn1);
			if (amountStartingIn1.compareTo(N)>=0)	{
				accumulator.append('1');
				getNthCombination(startingIn1,N,accumulator);
				return;
			}
			N=N.subtract(amountStartingIn1);
		}
		{
			List<ConcreteCombination> startingIn2=new ArrayList<>();
			BigInteger amountStartingIn2=tryOneDigit(combinations,ConcreteCombination::has2,ConcreteCombination::minus2,startingIn2);
			if (amountStartingIn2.compareTo(N)>=0)	{
				accumulator.append('2');
				getNthCombination(startingIn2,N,accumulator);
				return;
			}
			N=N.subtract(amountStartingIn2);
		}
		{
			List<ConcreteCombination> startingIn3=new ArrayList<>();
			BigInteger amountStartingIn3=tryOneDigit(combinations,ConcreteCombination::has3,ConcreteCombination::minus3,startingIn3);
			if (amountStartingIn3.compareTo(N)>=0)	{
				accumulator.append('3');
				getNthCombination(startingIn3,N,accumulator);
				return;
			}
		}
		throw new IllegalStateException("You chose... poorly");
	}
	
	private static BigInteger getNthCombination(List<Combination123> thisSize,BigInteger N)	{
		StringBuilder builder=new StringBuilder();
		List<ConcreteCombination> concreteCombinations=thisSize.stream().map(Combination123::getConcreteCombinations).flatMap(Collection::stream).collect(Collectors.toUnmodifiableList());
		getNthCombination(concreteCombinations,N,builder);
		return new BigInteger(builder.toString());
	}
	
	private static BigInteger getResult()	{
		ListMultimap<Integer,Combination123> sortedCombinations=getAllCombinations(FIRST_NUMBERS);
		BigInteger remaining=BigInteger.valueOf(LIMIT);
		for (Integer key:sortedCombinations.keySet())	{
			List<Combination123> combinations=sortedCombinations.get(key);
			BigInteger thisSize=BigInteger.ZERO;
			for (Combination123 combination:combinations) thisSize=thisSize.add(combination.getCombinations());
			if (thisSize.compareTo(remaining)>=0) return getNthCombination(combinations,remaining);
			else remaining=remaining.subtract(thisSize);
		}
		throw new IllegalStateException();
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger result=getResult();
		BigInteger realResult=result.mod(BigInteger.valueOf(MOD));
		long tac=System.nanoTime();
		System.out.println(result);
		System.out.println(realResult);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
