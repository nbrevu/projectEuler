package com.euler;

import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.euler.common.BigIntegerUtils.Fraction;

public class Euler610 {
	private final static boolean ONLY_MINIMAL_FORM=false;
	
	private enum RomanDigit	{
		M(7),D(7),C(7),L(7),X(7),V(7),I(7),end(1);
		public final long probability;
		
		RomanDigit(long probability)	{
			this.probability=probability;
		}
	}
	
	private static class PositionSequencer	{
		private final List<List<List<RomanDigit>>> storage;
		private PositionSequencer(RomanDigit value1,RomanDigit value5,RomanDigit value10)	{
			storage=new ArrayList<>(10);
			for (int i=0;i<10;++i) storage.add(new ArrayList<>());
			storage.get(0).add(Collections.emptyList());
			if (ONLY_MINIMAL_FORM)	{
				for (int i=1;i<4;++i) storage.get(i).add(Collections.nCopies(i,value1));
				storage.get(4).add(Arrays.asList(value1,value5));
				storage.get(5).add(Arrays.asList(value5));
				for (int i=6;i<9;++i)	{
					List<RomanDigit> compoundNumber=new ArrayList<>();
					compoundNumber.add(value5);
					compoundNumber.addAll(Collections.nCopies(i-5,value1));
					storage.get(i).add(compoundNumber);
				}
				storage.get(9).add(Arrays.asList(value1,value10));
			}	else	{
				for (int i=1;i<10;++i) storage.get(i).add(Collections.nCopies(i,value1));
				storage.get(4).add(Arrays.asList(value1,value5));
				for (int i=5;i<10;++i)	{
					List<RomanDigit> compoundNumber=new ArrayList<>();
					compoundNumber.add(value5);
					compoundNumber.addAll(Collections.nCopies(i-5,value1));
					storage.get(i).add(compoundNumber);
				}
				storage.get(9).add(Arrays.asList(value1,value10));
			}
		}
		public static PositionSequencer POSITIONS_ONES=new PositionSequencer(RomanDigit.I,RomanDigit.V,RomanDigit.X);
		public static PositionSequencer POSITIONS_TENS=new PositionSequencer(RomanDigit.X,RomanDigit.L,RomanDigit.C);
		public static PositionSequencer POSITIONS_HUNDREDS=new PositionSequencer(RomanDigit.C,RomanDigit.D,RomanDigit.M);
		public static List<List<RomanDigit>> getListsForHundreds(int hundreds)	{
			return POSITIONS_HUNDREDS.storage.get(hundreds);
		}
		public static List<List<RomanDigit>> getListsForTens(int tens)	{
			return POSITIONS_TENS.storage.get(tens);
		}
		public static List<List<RomanDigit>> getListsForOnes(int ones)	{
			return POSITIONS_ONES.storage.get(ones);
		}
	}
	
	private static class RomanNumeralTree	{
		private final Map<RomanDigit,RomanNumeralTree> children;
		public RomanNumeralTree()	{
			children=new EnumMap<>(RomanDigit.class);
		}
		public void add(List<RomanDigit> sequence)	{
			if (sequence.isEmpty()) return;
			RomanDigit digit=sequence.get(0);
			List<RomanDigit> remaining=sequence.subList(1,sequence.size());
			RomanNumeralTree child=children.get(digit);
			if (child==null)	{
				child=new RomanNumeralTree();
				children.put(digit,child);
			}
			child.add(remaining);
		}
	}
	
	private static class RomanNumeral	{
		public final List<RomanDigit> sequence;
		private RomanNumeral(List<RomanDigit> sequence)	{
			this.sequence=sequence;
		}
		public static List<RomanNumeral> getFromNumber(int n)	{
			if (n>=1000) throw new IllegalArgumentException("Only <=999 values.");
			List<RomanNumeral> result=new ArrayList<>();
			int hundreds=n/100;
			n-=100*hundreds;
			int tens=n/10;
			int ones=n-10*tens;
			List<List<RomanDigit>> hundredsList=PositionSequencer.getListsForHundreds(hundreds);
			List<List<RomanDigit>> tensList=PositionSequencer.getListsForTens(tens);
			List<List<RomanDigit>> onesList=PositionSequencer.getListsForOnes(ones);
			for (List<RomanDigit> l1:hundredsList) for (List<RomanDigit> l2:tensList) for (List<RomanDigit> l3:onesList)	{
				List<RomanDigit> sequence=new ArrayList<>();
				sequence.addAll(l1);
				sequence.addAll(l2);
				sequence.addAll(l3);
				sequence.add(RomanDigit.end);
				result.add(new RomanNumeral(sequence));
			}
			return result;
		}
	}
	
	private static class ProbabilityCalculator	{
		private final BigInteger[] denominators;
		private final RomanNumeralTree baseTree;
		public ProbabilityCalculator(RomanNumeralTree baseTree)	{
			denominators=new BigInteger[1+RomanDigit.values().length];
			for (int i=1;i<denominators.length;++i) denominators[i]=BigInteger.valueOf((7*i)-6);
			this.baseTree=baseTree;
		}
		public Fraction calculateProbability(RomanNumeral digits)	{
			return calculateProbability(baseTree,digits.sequence);
		}
		private Fraction calculateProbability(RomanNumeralTree tree,List<RomanDigit> digits)	{
			if (digits.isEmpty()) return new Fraction(BigInteger.ONE,BigInteger.ONE);
			RomanDigit digit=digits.get(0);
			List<RomanDigit> remaining=digits.subList(1,digits.size());
			Fraction me=new Fraction(BigInteger.valueOf(digit.probability),denominators[tree.children.size()]);
			return me.multiply(calculateProbability(tree.children.get(digit),remaining));
		}
	}
	
	public static void main(String[] args)	{
		RomanNumeralTree tree=new RomanNumeralTree();
		for (int i=0;i<=999;++i) for (RomanNumeral numeral:RomanNumeral.getFromNumber(i)) tree.add(numeral.sequence);
		tree.add(Arrays.asList(RomanDigit.M,RomanDigit.end));
		ProbabilityCalculator calculator=new ProbabilityCalculator(tree);
		Fraction result=new Fraction();
		Fraction constant1=new Fraction(BigInteger.valueOf(350000),BigInteger.valueOf(1849));
		Fraction constant2=new Fraction(BigInteger.valueOf(50),BigInteger.valueOf(43));
		for (int i=0;i<=999;++i) for (RomanNumeral numeral:RomanNumeral.getFromNumber(i))	{
			Fraction baseProb=calculator.calculateProbability(numeral);
			Fraction factor=constant2.multiply(BigInteger.valueOf(i));
			factor=factor.add(constant1);
			result=result.add(factor.multiply(baseProb));
		}
		System.out.println(result);
		MathContext mc=new MathContext(11,RoundingMode.HALF_UP);
		System.out.println(result.asBigDecimal(mc).toString());
	}
}
