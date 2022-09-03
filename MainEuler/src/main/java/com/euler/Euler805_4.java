package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.Rational;
import com.google.common.collect.Lists;

public class Euler805_4 {
	private final static int N=500;
	
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
			return String.format("[Mod=%d: %d=>%d (n=%s)]",origin,firstRemainder,secondRemainder,getFullNumber().toString());
		}
		public BigInteger getFullNumber()	{
			BigInteger result=BigInteger.ZERO;
			int mod=firstRemainder;
			for (;;)	{
				int prodMod=mod*10;
				int q=prodMod/origin;
				int nextMod=prodMod%origin;
				result=result.multiply(BigInteger.TEN).add(BigInteger.valueOf(q));
				mod=nextMod;
				if (mod==firstRemainder) break;
			}
			return result;
		}
	}
	
	private static BigInteger getFineValue(Rational rat)	{
		long num=rat.num();
		long den=rat.den();
		long diff=10*den-num;
		if (diff<=0) throw new IllegalArgumentException("This rational value seems to be out of range :(.");
		long q=diff;
		while ((q%2)==0) q/=2;
		while ((q%5)==0) q/=5;
		if (q<=den) throw new IllegalArgumentException("This rational value seems to be off limits (too large quotient after removing 2 and 5).");
		BigInteger result=BigInteger.ZERO;
		long n=den;
		// while ((q>3*den)&&((q%3)==0)) q/=3;
		do	{
			long n10=n*10;
			long digit=n10/q;
			n=n10%q;
			result=result.multiply(BigInteger.TEN).add(BigInteger.valueOf(digit));
		}	while (n!=den);
		return result;
	}
	
	/*
	 * "ACHTUNG. For rational 1/316 I get 100031655587211142766698322253877809433364988920544476, but the real value is 900284."
	 * W T F.
	 * My algorithm says: q=3160-1=3159, first mod=316.
	 * However there are results for q=351, first mod=316.
	 * Can I divide times 3 as many times as available, and get the real result?
	 * Certainly not as much as I would like.
	 * Sometimes I can, sometimes the result is bogus.
	 */
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
		int valid=0;
		for (Map.Entry<Rational,List<PeriodInfo>> entry:data.entrySet())	{
			BigInteger bruteForceValue=Collections.min(Lists.transform(entry.getValue(),PeriodInfo::getFullNumber));
			BigInteger fineValue=getFineValue(entry.getKey());
			if (!bruteForceValue.equals(fineValue)) System.out.println(String.format("ACHTUNG. For rational %s I get %s, but the real value is %s.",entry.getKey().toString(),fineValue.toString(),bruteForceValue.toString()));
			else ++valid;
		}
		System.out.println("Checked "+valid+" correct values.");
	}
}
