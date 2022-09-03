package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Euler303 {
	//And another one at the first try :).
	private final static int LIMIT=10000;
	
	private static class ValueSummary	{
		public final long quotient;
		public final long mod;
		public ValueSummary(long number)	{
			quotient=0;
			mod=number;
		}
		private ValueSummary(long quotient,long mod)	{
			this.quotient=quotient;
			this.mod=mod;
		}
		public List<ValueSummary> getChildren(long modulo)	{
			long nextQuotient=10*quotient;
			long nextMod=10*mod;
			nextQuotient+=nextMod/modulo;
			nextMod%=modulo;
			List<ValueSummary> result=new ArrayList<>();
			result.add(new ValueSummary(nextQuotient,nextMod));
			if (nextMod==(modulo-2))	{
				result.add(new ValueSummary(nextQuotient,1+nextMod));
				result.add(new ValueSummary(nextQuotient+1,0));
			}	else if (nextMod==(modulo-1))	{
				result.add(new ValueSummary(nextQuotient+1,0));
				result.add(new ValueSummary(nextQuotient+1,1));
			}	else	{
				result.add(new ValueSummary(nextQuotient,1+nextMod));
				result.add(new ValueSummary(nextQuotient,2+nextMod));
			}
			return result;
		}
	}
	
	private final static List<ValueSummary> INITIAL_LIST=Arrays.asList(new ValueSummary(1l),new ValueSummary(2l));
	
	private static long getBestQuotient(long in)	{
		Set<Long> knownModuli=new HashSet<>();
		List<ValueSummary> curGen=INITIAL_LIST;
		for (;;)	{
			List<ValueSummary> nextGen=new ArrayList<>();
			for (ValueSummary sum:curGen)	{
				if (sum.mod==0) return sum.quotient;
				if (knownModuli.contains(sum.mod)) continue;
				knownModuli.add(sum.mod);
				nextGen.addAll(sum.getChildren(in));
			}
			curGen=nextGen;
		}
	}
	
	public static void main(String[] args)	{
		long sum=2l;	// Already including 1 and 2.
		for (int i=3;i<=LIMIT;++i) sum+=getBestQuotient((long)i);
		System.out.println(sum);
	}
}
