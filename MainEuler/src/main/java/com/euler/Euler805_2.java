package com.euler;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.Rational;

public class Euler805_2 {
	private final static int N=5000;
	
	private static class PeriodInfo	{
		private final int origin;
		private final int firstRemainder;
		private final int secondRemainder;
		public PeriodInfo(int origin,int firstRemainder,int secondRemainder)	{
			this.origin=origin;
			this.firstRemainder=firstRemainder;
			this.secondRemainder=secondRemainder;
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			int mod=firstRemainder;
			for (;;)	{
				int prodMod=mod*10;
				int q=prodMod/origin;
				int nextMod=prodMod%origin;
				sb.append(q);
				mod=nextMod;
				if (mod==firstRemainder) break;
			}
			return String.format("[Mod=%d: %d=>%d (n=%s)]",origin,firstRemainder,secondRemainder,sb.toString());
		}
	}
	
	public static void main(String[] args)	{
		SortedMap<Rational,List<PeriodInfo>> data=new TreeMap<>();
		for (int i=3;i<=N;++i)	{
			if (((i%2)==0)||((i%5)==0)) continue;
			BitSet available=new BitSet(i);
			available.set(1,i);
			while (!available.isEmpty())	{
				int firstMod=available.nextSetBit(1);
				int mod=firstMod;
				do	{
					available.clear(mod);
					int modProd=mod*10;
					int nextMod=modProd%i;
					if (nextMod!=modProd)	{
						PeriodInfo info=new PeriodInfo(i,mod,nextMod);
						data.computeIfAbsent(new Rational(nextMod,mod),(Rational unused)->new ArrayList<>()).add(info);
					}
					mod=nextMod;
				}	while (mod!=firstMod);
			}
		}
		for (Map.Entry<Rational,List<PeriodInfo>> entry:data.entrySet())	{
			Rational x=entry.getKey();
			if ((x.num()==1)&&(x.den()==316))	{
				System.out.println(entry.getKey()+":");
				for (PeriodInfo p:entry.getValue()) System.out.println("\t"+p);
			}
		}
	}
}
