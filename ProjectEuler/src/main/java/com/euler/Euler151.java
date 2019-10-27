package com.euler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.euler.common.BigRational;
import com.euler.common.Timing;

public class Euler151 {
	private final static int MAX_LEVEL=4;
	private static class State	{
		private final int[] sheets;
		public final int singleSheetEncounters;
		public State()	{
			sheets=new int[MAX_LEVEL];
			Arrays.fill(sheets,1);
			singleSheetEncounters=0;
		}
		private State(int[] papers,int singleSheetEncounters)	{
			this.sheets=papers;
			this.singleSheetEncounters=singleSheetEncounters;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(sheets);
		}
		@Override
		public boolean equals(Object other)	{
			State sOther=(State)other;
			return (singleSheetEncounters==sOther.singleSheetEncounters)&&Arrays.equals(sheets,sOther.sheets);
		}
		@Override
		public String toString()	{
			return "<"+Arrays.toString(sheets)+", +"+singleSheetEncounters+">";
		}
		public Map<State,BigRational> getChildren()	{
			Map<State,BigRational> result=new HashMap<>();
			long denominator=sum(sheets);
			for (int i=0;i<sheets.length;++i) if (sheets[i]>0)	{
				int[] copy=Arrays.copyOf(sheets,sheets.length);
				--copy[i];
				for (int j=0;j<i;++j) ++copy[j];
				int encounters=singleSheetEncounters;
				if (isSingleSheet(copy)) ++encounters;
				BigRational fraction=new BigRational(sheets[i],denominator);
				result.put(new State(copy,encounters),fraction);
			}
			return result;
		}
		private static boolean isSingleSheet(int[] sheets)	{
			int sum=0;
			for (int s:sheets)	{
				sum+=s;
				if (sum>1) return false;
			}
			return true;
		}
		private static int sum(int[] array)	{
			int result=0;
			for (int n:array) result+=n;
			return result;
		}
	}

	private static String solve()	{
		Map<State,BigRational> currentDistribution=new HashMap<>();
		currentDistribution.put(new State(),BigRational.ONE);
		int iterations=(1<<MAX_LEVEL)-3;
		for (int i=0;i<iterations;++i)	{
			Map<State,BigRational> newDistribution=new HashMap<>();
			for (Map.Entry<State,BigRational> entry:currentDistribution.entrySet()) for (Map.Entry<State,BigRational> child:entry.getKey().getChildren().entrySet()) newDistribution.compute(child.getKey(),(State unusedKey,BigRational previousValue)->	{
				BigRational toAdd=entry.getValue().multiply(child.getValue());
				return (previousValue==null)?toAdd:toAdd.add(previousValue);
			});
			currentDistribution=newDistribution;
		}
		BigRational result=BigRational.ZERO;
		for (Map.Entry<State,BigRational> entry:currentDistribution.entrySet()) if (entry.getKey().singleSheetEncounters>0) result=result.add(entry.getValue().multiply(entry.getKey().singleSheetEncounters));
		return String.format(Locale.UK,"%.6f",result.toDouble());
	}

	public static void main(String[] args)	{
		Timing.time(Euler151::solve);
	}
}
