package com.euler;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.koloboke.collect.IntCursor;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler714 {
	private final static int N=50000;
	private final static NumberFormat FORMATTER=new DecimalFormat("0.000000000000E0",new DecimalFormatSymbols(Locale.UK));
	
	private static class NumberAndMod	{
		public final BigInteger number;
		public final int mod;
		public NumberAndMod(BigInteger number,int mod)	{
			this.number=number;
			this.mod=mod;
		}
		public NumberAndMod add(NumberAndMod toAdd,int divisor)	{
			BigInteger nextNumber=number.add(toAdd.number);
			int nextMod=(mod+toAdd.mod)%divisor;
			return new NumberAndMod(nextNumber,nextMod);
		}
		public NumberAndMod multiply(NumberAndMod toMultiply,int divisor)	{
			BigInteger nextNumber=number.multiply(toMultiply.number);
			int nextMod=(mod*toMultiply.mod)%divisor;
			return new NumberAndMod(nextNumber,nextMod);
		}
	}
	
	private static class SingleDuodigitFinder	{
		private final int digitA;
		private final int digitB;
		private final int num;
		private final boolean hasZero;
		private BigInteger[] currentState;
		private BigInteger[] stateSwapper;
		private IntSet currentMods;
		private IntSet modsSwapper;
		public SingleDuodigitFinder(int digitA,int digitB,int num)	{
			this.digitA=digitA;
			this.digitB=digitB;
			this.num=num;
			hasZero=(digitA==0);	// No need to check digitB, it will never be 0.
			currentState=new BigInteger[num];
			currentMods=HashIntSets.newMutableSet();
			if (isSuitableStart(digitA,num))	{
				currentState[digitA]=BigInteger.valueOf(digitA);
				currentMods.add(digitA);
			}
			if (isSuitableStart(digitB,num))	{
				currentState[digitB]=BigInteger.valueOf(digitB);
				currentMods.add(digitB);
			}
			stateSwapper=new BigInteger[num];
			modsSwapper=HashIntSets.newMutableSet();
		}
		private static boolean isSuitableStart(int digit,int num)	{
			if ((digit==0)||((num%10)==0)) return false;
			else return ((digit%2)==0)||((num%2)==1);
		}
		public BigInteger getCurrentFinding()	{
			// Possibly null.
			return currentState[0];
		}
		public void nextGeneration(NumberAndMod[] nextNumbers)	{
			NumberAndMod toAddA=nextNumbers[digitA];
			NumberAndMod toAddB=nextNumbers[digitB];
			Arrays.fill(stateSwapper,null);
			modsSwapper.clear();
			if (hasZero)	{
				stateSwapper[toAddB.mod]=toAddB.number;
				modsSwapper.add(toAddB.mod);
			}
			for (IntCursor cursor=currentMods.cursor();cursor.moveNext();)	{
				int currentMod=cursor.elem();
				BigInteger currentNumber=currentState[currentMod];
				tryToAdd(currentNumber,currentMod,toAddA);
				tryToAdd(currentNumber,currentMod,toAddB);
			}
			BigInteger[] swap1=stateSwapper;
			stateSwapper=currentState;
			currentState=swap1;
			IntSet swap2=modsSwapper;
			modsSwapper=currentMods;
			currentMods=swap2;
		}
		private void tryToAdd(BigInteger currentNumber,int currentMod,NumberAndMod toAdd)	{
			BigInteger nextNumber=currentNumber.add(toAdd.number);
			int nextMod=(currentMod+toAdd.mod)%num;
			BigInteger existing=stateSwapper[nextMod];
			if ((existing==null)||(nextNumber.compareTo(existing)<0))	{
				stateSwapper[nextMod]=nextNumber;
				modsSwapper.add(nextMod);
			}
		}
	}
	
	private static class DuodigitFinder	{
		private final static NumberAndMod TEN=new NumberAndMod(BigInteger.TEN,10);
		private final int number;
		private final List<SingleDuodigitFinder> separateCases;
		private DuodigitFinder(int number,List<SingleDuodigitFinder> separateCases)	{
			this.number=number;
			this.separateCases=separateCases;
		}
		public static DuodigitFinder getGeneralCase(int in)	{
			List<SingleDuodigitFinder> result=new ArrayList<>(45);
			for (int i=0;i<10;++i) for (int j=i+1;j<10;++j) result.add(new SingleDuodigitFinder(i,j,in));
			return new DuodigitFinder(in,result);
		}
		public static DuodigitFinder getEven(int in)	{
			List<SingleDuodigitFinder> result=new ArrayList<>(35);
			for (int i=0;i<10;++i) for (int j=i+1;j<10;++j) if (((i%2)==0)||((j%2)==0)) result.add(new SingleDuodigitFinder(i,j,in));
			return new DuodigitFinder(in,result);
		}
		public static DuodigitFinder get0(int in)	{
			List<SingleDuodigitFinder> result=new ArrayList<>(45);
			for (int j=1;j<10;++j) result.add(new SingleDuodigitFinder(0,j,in));
			return new DuodigitFinder(in,result);
		}
		public BigInteger findFirstDuodigit()	{
			NumberAndMod[] generationSteps=new NumberAndMod[10];
			generationSteps[0]=new NumberAndMod(BigInteger.ZERO,0);
			generationSteps[1]=TEN;
			fillGenerationSteps(generationSteps);
			for (;;)	{
				BigInteger result=null;
				for (SingleDuodigitFinder singleCase:separateCases)	{
					singleCase.nextGeneration(generationSteps);
					BigInteger tempResult=singleCase.getCurrentFinding();
					if (tempResult==null) continue;
					else if ((result==null)||(tempResult.compareTo(result)<0)) result=tempResult;
				}
				if (result!=null) return result;
				generationSteps[1]=generationSteps[1].multiply(TEN,number);
				fillGenerationSteps(generationSteps);
			}
		}
		private void fillGenerationSteps(NumberAndMod[] generationSteps)	{
			for (int i=2;i<10;++i) generationSteps[i]=generationSteps[i-1].add(generationSteps[1],number);
		}
	}
	
	private static boolean isDuodigit(int in)	{
		IntSet digits=HashIntSets.newMutableSet();
		while (in>0)	{
			digits.add(in%10);
			in/=10;
		}
		return digits.size()<=2;
	}
	
	private static BigInteger getFirstDuodigit(int in)	{
		if (isDuodigit(in)) return BigInteger.valueOf(in);
		DuodigitFinder finder;
		if ((in%10)==0) finder=DuodigitFinder.get0(in);
		else finder=((in%2)==0)?DuodigitFinder.getEven(in):DuodigitFinder.getGeneralCase(in);
		return finder.findFirstDuodigit();
	}
	
	public static void main(String[] args)	{
		BigInteger result=BigInteger.ZERO;
		long tic=System.nanoTime();
		for (int i=1;i<=N;++i) result=result.add(getFirstDuodigit(i));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(FORMATTER.format(result).toLowerCase());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
