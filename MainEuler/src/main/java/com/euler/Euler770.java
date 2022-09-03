package com.euler;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Euler770 {
	private static class PseudoDouble implements Comparable<PseudoDouble>	{
		private final double mainValue;
		private final boolean minusEpsilon;
		public PseudoDouble(double mainValue,boolean minusEpsilon)	{
			this.mainValue=mainValue;
			this.minusEpsilon=minusEpsilon;
		}
		@Override
		public int compareTo(PseudoDouble other) {
			int result=Double.compare(mainValue,other.mainValue);
			if (result==0) return Boolean.compare(other.minusEpsilon,minusEpsilon);
			else return result;
		}
	}
	
	private static enum Sign	{
		NEGATIVE {
			@Override
			public boolean isCorrect(int value) {
				return value<0;
			}
		},
		ZERO {
			@Override
			public boolean isCorrect(int value) {
				return value==0;
			}
		},
		POSITIVE {
			@Override
			public boolean isCorrect(int value) {
				return value>0;
			}
		};
		public abstract boolean isCorrect(int value);
	}
	
	public static void main(String[] args)	{
		PseudoDouble one=new PseudoDouble(1.0,false);
		PseudoDouble two=new PseudoDouble(2.0,false);
		PseudoDouble oneMinus=new PseudoDouble(1.0,true);
		PseudoDouble half=new PseudoDouble(0.5,false);
		Table<PseudoDouble,PseudoDouble,Sign> expecteds=HashBasedTable.create();
		expecteds.put(one,one,Sign.ZERO);
		expecteds.put(one,two,Sign.NEGATIVE);
		expecteds.put(one,oneMinus,Sign.POSITIVE);
		expecteds.put(one,half,Sign.POSITIVE);
		expecteds.put(two,one,Sign.POSITIVE);
		expecteds.put(two,two,Sign.ZERO);
		expecteds.put(two,oneMinus,Sign.POSITIVE);
		expecteds.put(two,half,Sign.POSITIVE);
		expecteds.put(oneMinus,one,Sign.NEGATIVE);
		expecteds.put(oneMinus,two,Sign.NEGATIVE);
		expecteds.put(oneMinus,oneMinus,Sign.ZERO);
		expecteds.put(oneMinus,half,Sign.POSITIVE);
		expecteds.put(half,one,Sign.NEGATIVE);
		expecteds.put(half,two,Sign.NEGATIVE);
		expecteds.put(half,oneMinus,Sign.NEGATIVE);
		expecteds.put(half,half,Sign.ZERO);
		for (Table.Cell<PseudoDouble,PseudoDouble,Sign> cell:expecteds.cellSet())	{
			PseudoDouble minuend=cell.getRowKey();
			PseudoDouble subtrahend=cell.getColumnKey();
			Sign expected=cell.getValue();
			int compare=minuend.compareTo(subtrahend);
			if (!expected.isCorrect(compare)) System.out.println("OH NEIN!!!!!");
		}
	}
}
