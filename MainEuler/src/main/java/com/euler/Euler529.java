package com.euler;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Euler529 {
	private static class State implements Comparable<State>	{
		private final static Comparator<NavigableSet<Integer>> SET_COMPARATOR=new Comparator<>()	{
			@Override
			public int compare(NavigableSet<Integer> s1, NavigableSet<Integer> s2) {
				int diff=s1.size()-s2.size();
				if (diff!=0) return diff;
				Iterator<Integer> i1=s1.iterator();
				Iterator<Integer> i2=s2.iterator();
				while (i1.hasNext())	{
					diff=i1.next()-i2.next();
					if (diff!=0) return diff;
				}
				return 0;
			}
		};
		private final NavigableSet<Integer> initialSums;
		private final NavigableSet<Integer> finalSums;
		private final boolean isBeginningFinished;
		private final int unaccountedDigits;
		public State(NavigableSet<Integer> initialSums,NavigableSet<Integer> finalSums,boolean isBeginningFinished,int unaccountedDigits)	{
			this.initialSums=initialSums;
			this.finalSums=finalSums;
			this.isBeginningFinished=isBeginningFinished;
			this.unaccountedDigits=unaccountedDigits;
		}
		public static State getForSingleDigit(int digit)	{
			if (digit<=0||digit>=10) throw new IllegalArgumentException();
			NavigableSet<Integer> sums=new TreeSet<>();
			sums.add(digit);
			return new State(sums,sums,false,1);
		}
		public State addDigit(int digit)	{
			if (digit<0||digit>=10) throw new IllegalArgumentException();
			if (digit==0) return this;
			NavigableSet<Integer> newFinalSums=new TreeSet<>();
			NavigableSet<Integer> newInitialSums;
			int lastSum=finalSums.last()+digit;
			boolean newIsBeginningFinished;
			int newUnaccountedDigits;
			if ((!isBeginningFinished)&&(lastSum<10))	{
				newIsBeginningFinished=false;
				newFinalSums.add(digit);
				for (int i:finalSums) newFinalSums.add(i+digit);
				newInitialSums=new TreeSet<>(initialSums);
				newInitialSums.add(newInitialSums.last()+digit);
				newUnaccountedDigits=1+unaccountedDigits;
			}	else	{
				newIsBeginningFinished=true;
				newFinalSums.add(digit);
				for (int i:finalSums) if (i+digit<10) newFinalSums.add(i+digit);
				newInitialSums=initialSums;
				newUnaccountedDigits=(lastSum==10)?0:(1+unaccountedDigits);
			}
			if (newUnaccountedDigits>newFinalSums.size()) return null;
			return new State(newInitialSums,newFinalSums,newIsBeginningFinished,newUnaccountedDigits);
		}
		@Override
		public boolean equals(Object other)	{
			State sOther=(State)other;
			return initialSums.equals(sOther.initialSums)&&finalSums.equals(sOther.finalSums)&&(isBeginningFinished==sOther.isBeginningFinished)&&(unaccountedDigits==sOther.unaccountedDigits);
		}
		@Override
		public int hashCode()	{
			return Objects.hash(initialSums,finalSums,isBeginningFinished,unaccountedDigits);
		}
		@Override
		public String toString()	{
			return "[START:"+initialSums+"; END:"+finalSums+"; "+((isBeginningFinished)?"finished":"unfinished")+"; "+unaccountedDigits+" unaccounted]";
		}
		@Override
		public int compareTo(State o) {
			int diff=SET_COMPARATOR.compare(initialSums,o.initialSums);
			if (diff!=0) return diff;
			diff=SET_COMPARATOR.compare(finalSums,o.finalSums);
			if (diff!=0) return diff;
			diff=((isBeginningFinished)?1:0)-((o.isBeginningFinished)?1:0);
			if (diff!=0) return diff;
			else return unaccountedDigits-o.unaccountedDigits;
		}
	}
	
	public static void main(String[] args)	{
		// 1434377 different states. What I wanted to do is O(N^2) and therefore not feasible :(.
		long tic=System.nanoTime();
		SortedSet<State> allStates=new TreeSet<>();
		Set<State> pending=new HashSet<>();
		for (int i=1;i<10;++i) pending.add(State.getForSingleDigit(i));
		while (!pending.isEmpty())	{
			State toProcess=pending.iterator().next();
			allStates.add(toProcess);
			pending.remove(toProcess);
			for (int i=1;i<10;++i)	{
				State child=toProcess.addDigit(i);
				if ((child==null)||allStates.contains(child)) continue;
				else pending.add(child);
			}
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
		try (PrintStream ps=new PrintStream("C:\\out529.txt"))	{
			for (State s:allStates) ps.println(s);
		}	catch (IOException exc)	{
			System.out.println("D'OH!");
		}
		System.out.println("ATIENDE QUÉ GAÑANAZO: tengo "+allStates.size()+" estados diferentes.");
	}
}
