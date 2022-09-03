package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils.Pair;
import com.euler.common.Primes;
import com.koloboke.collect.map.LongObjCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler757_2 {
	private static interface PossibleElement	{
		public boolean isHalfInteger();
		public long integerPart();
	}
	
	private static class IntegerElement implements PossibleElement	{
		private final long value;
		public IntegerElement(long value)	{
			this.value=value;
		}
		@Override
		public boolean isHalfInteger() {
			return false;
		}
		@Override
		public long integerPart() {
			return value;
		}
		@Override
		public String toString()	{
			return Long.toString(value);
		}
	}
	
	private static class HalfIntegerElement implements PossibleElement	{
		private final long truncatedValue;
		public HalfIntegerElement(long truncatedValue)	{
			this.truncatedValue=truncatedValue;
		}
		@Override
		public boolean isHalfInteger() {
			return true;
		}
		@Override
		public long integerPart() {
			return truncatedValue;
		}
		@Override
		public String toString()	{
			return Long.toString(truncatedValue)+".5";
		}
	}
	
	private static PossibleElement difference(PossibleElement e1,PossibleElement e2)	{
		long intDiff=e1.integerPart()-e2.integerPart();
		int signToAdd=0;
		if (e1.isHalfInteger()) ++signToAdd;
		if (e2.isHalfInteger()) --signToAdd;
		switch (signToAdd)	{
			case -1: return new HalfIntegerElement(intDiff-1);
			case 0: return new IntegerElement(intDiff);
			case 1: return new HalfIntegerElement(intDiff);
			default: throw new IllegalStateException("¿Qué le estás haciendo a mi amado cuerpo de marines?");
		}
	}
	
	private static class Addends	{
		public final long f1;
		public final long f2;
		public Addends(long f1,long f2)	{
			this.f1=f1;
			this.f2=f2;
		}
		public PossibleElement getHalfSum()	{
			long s=f1+f2;
			if ((s%2)==1) return new HalfIntegerElement((s-1)/2);
			else return new IntegerElement(s/2);
		}
		public PossibleElement getHalfDiff()	{
			long s=f2-f1;
			if ((s%2)==1) return new HalfIntegerElement((s-1)/2);
			else return new IntegerElement(s/2);
		}
	}
	
	public static void main(String[] args)	{
		int n=1_000_000;
		int[] lastPrimes=Primes.firstPrimeSieve(n);
		int counter=0;
		for (int i=2;i<=n;++i)	{
			DivisorHolder holder=DivisorHolder.getFromFirstPrimes(i,lastPrimes);
			long[] factors=holder.getUnsortedListOfDivisors().toLongArray();
			Arrays.sort(factors);
			LongObjMap<Addends> sums=HashLongObjMaps.newMutableMap();
			for (int j=0;j<factors.length;++j)	{
				long f1=factors[j];
				long f2=factors[factors.length-1-j];
				if (f1>f2) break;
				/*
				 * Note that there can't be two *different* pairs of numbers (a,b) and (c,d) so that a+b==c+d AND a*b==c*d.
				 * So, no need to use a list. There will be at most one element per sum.
				 */
				sums.put(f1+f2,new Addends(f1,f2));
			}
			List<Pair<Addends,Addends>> matches=new ArrayList<>();
			for (LongObjCursor<Addends> cursor=sums.cursor();cursor.moveNext();)	{
				Addends other=sums.get(cursor.key()-1);
				if (other!=null) matches.add(new Pair<>(cursor.value(),other));
			}
			if (!matches.isEmpty())	{
				++counter;
				System.out.println("Found "+i+":");
				for (Pair<Addends,Addends> match:matches)	{
					Addends p1=match.first;
					Addends p2=match.second;
					PossibleElement alpha=p1.getHalfSum();
					PossibleElement beta=p1.getHalfDiff();
					PossibleElement gamma=p2.getHalfSum();
					PossibleElement delta=p2.getHalfDiff();
					PossibleElement diffSum=difference(alpha,gamma);
					PossibleElement diffDiff=difference(beta,delta);
					System.out.println("\t("+p1.f1+","+p1.f2+" -> "+alpha+", "+beta+") und ("+p2.f1+","+p2.f2+" -> "+gamma+", "+delta+"); diffs=("+diffSum+", "+diffDiff+").");
					if (!(diffSum instanceof HalfIntegerElement)) throw new IllegalStateException("Esto no puede pasar nunca.");
					HalfIntegerElement half=(HalfIntegerElement)diffSum;
					if (half.integerPart()!=0) throw new IllegalStateException("Esto no me lo esperaba, caso A.");
				}
			}
		}
		System.out.println("Total: "+counter+".");
	}
}
