package com.euler;

import java.util.Arrays;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.math.LongMath;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler442 {
	private final static long MAX_SIZE=15;
	
	private static class Pattern11 implements Comparable<Pattern11>	{
		private final static int[] EMPTY_ARRAY=new int[0];
		private int[] digits;
		private Pattern11(int[] digits)	{
			this.digits=digits;
		}
		public int size()	{
			return digits.length;
		}
		public static Pattern11 getFromNumber(long in)	{
			int size=(int)Math.ceil(Math.log10(in));
			int[] digits=new int[size];
			for (int i=size-1;i>=0;--i)	{
				digits[i]=(int)in%10;
				in/=10;
			}
			return new Pattern11(digits);
		}
		public static Pattern11 combineIfPossible(Pattern11 left,Pattern11 right)	{
			int maxOverlap=Math.min(left.size(),right.size())-1;
			for (int i=1;i<=maxOverlap;++i) if (doPatternsOverlap(left,right,i)) return combine(left,right,i);
			return null;
		}
		private static boolean doPatternsOverlap(Pattern11 left,Pattern11 right,int overlapSize)	{
			for (int i=0;i<overlapSize;++i) if (left.digits[left.digits.length-1-i]!=right.digits[overlapSize-1-i]) return false;
			return true;
		}
		private static Pattern11 combine(Pattern11 left,Pattern11 right,int overlapSize)	{
			int[] digits=new int[left.digits.length+right.digits.length-overlapSize];
			System.arraycopy(left.digits,0,digits,0,left.digits.length);
			System.arraycopy(right.digits,overlapSize,digits,left.digits.length,right.digits.length-overlapSize);
			return new Pattern11(digits);
		}
		public int[] findSubpattern(Pattern11 subpattern)	{
			int maxToLook=size()-subpattern.size()-1;
			if (maxToLook<0) return EMPTY_ARRAY;
			IntSet solutions=HashIntSets.newMutableSet();
			for (int i=0;i<=maxToLook;++i) if (isSubpattern(subpattern,i)) solutions.add(i);
			if (solutions.isEmpty()) return EMPTY_ARRAY;
			int[] result=solutions.toIntArray();
			Arrays.sort(result);
			return result;
		}
		private boolean isSubpattern(Pattern11 subpattern,int position)	{
			for (int i=0;i<subpattern.digits.length;++i) if (digits[position+i]!=subpattern.digits[i]) return false;
			return true;
		}
		@Override
		public boolean equals(Object other)	{
			return Arrays.equals(digits,((Pattern11)other).digits);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(digits);
		}
		@Override
		public int compareTo(Pattern11 other) {
			int sizeDiff=size()-other.size();
			if (sizeDiff!=0) return sizeDiff;
			for (int i=0;i<digits.length;++i)	{
				int diff=digits[i]-other.digits[i];
				if (diff!=0) return diff;
			}
			return 0;
		}
		@Override
		public String toString()	{
			return Arrays.stream(digits).mapToObj(Integer::toString).collect(Collectors.joining());
		}
	}
	
	public static void main(String[] args)	{
		{
			long p=1;
			long l=LongMath.pow(10l,18);
			for (;;)	{
				p*=11;
				if (p>=l) break;
				System.out.println(p);
			}
		}
		
		long tic=System.nanoTime();
		NavigableSet<Pattern11> pending=new TreeSet<>();
		long p=1;
		for (int i=1;i<=MAX_SIZE;++i)	{
			p*=11;
			pending.add(Pattern11.getFromNumber(p));
		}
		NavigableSet<Pattern11> patterns=new TreeSet<>();
		while (!pending.isEmpty())	{
			Pattern11 toCheck=pending.pollFirst();
			Pattern11 mix=Pattern11.combineIfPossible(toCheck,toCheck);
			if ((mix!=null)&&(mix.size()<=1+MAX_SIZE)&&!patterns.contains(mix)) pending.add(mix);
			for (Pattern11 existing:patterns)	{
				mix=Pattern11.combineIfPossible(toCheck,existing);
				if ((mix!=null)&&(mix.size()<=1+MAX_SIZE)&&!patterns.contains(mix)) pending.add(mix);
				mix=Pattern11.combineIfPossible(existing,toCheck);
				if ((mix!=null)&&(mix.size()<=1+MAX_SIZE)&&!patterns.contains(mix)) pending.add(mix);
			}
			patterns.add(toCheck);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("He tardado "+seconds+" putos segundos sólo en esta mierda.");
		System.out.println("Tengo "+patterns.size()+" patrones. Y ésos son sólo los simples.");
		System.out.println(patterns);
	}
}
