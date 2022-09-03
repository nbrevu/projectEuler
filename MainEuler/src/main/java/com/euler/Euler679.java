package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils.Pair;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Euler679 {
	private final static char[] LETTERS=new char[] {'A','E','F','R'};
	private final static int SIZE=30;
	private final static int TOTAL_STATES=4*4*4*2*2*2*2;
	
	private static class State	{
		private final char letterMinus3;
		private final char letterMinus2;
		private final char letterMinus1;
		private final boolean containsArea;
		private final boolean containsFare;
		private final boolean containsFree;
		private final boolean containsReef;
		public State(char letterMinus3,char letterMinus2,char letterMinus1,boolean containsArea,boolean containsFare,boolean containsFree,boolean containsReef)	{
			this.letterMinus3=letterMinus3;
			this.letterMinus2=letterMinus2;
			this.letterMinus1=letterMinus1;
			this.containsArea=containsArea;
			this.containsFare=containsFare;
			this.containsFree=containsFree;
			this.containsReef=containsReef;
		}
		private int getPosition(char c)	{
			switch (c)	{
				case 'A':return 0;
				case 'E':return 1;
				case 'F':return 2;
				case 'R':return 3;
				default:throw new IllegalArgumentException();
			}
		}
		private int bool2Int(boolean b)	{
			return b?1:0;
		}
		private int getBooleanId()	{
			return bool2Int(containsArea)+2*bool2Int(containsFare)+4*bool2Int(containsFree)+8*bool2Int(containsReef);
		}
		public int getId()	{
			return getPosition(letterMinus3)+4*getPosition(letterMinus2)+16*getPosition(letterMinus1)+64*getBooleanId();
		}
		public static State getStateFromId(int id)	{
			char letterMinus3=LETTERS[id%4];
			id/=4;
			char letterMinus2=LETTERS[id%4];
			id/=4;
			char letterMinus1=LETTERS[id%4];
			id/=4;
			boolean containsArea=(id%2==1);
			id/=2;
			boolean containsFare=(id%2==1);
			id/=2;
			boolean containsFree=(id%2==1);
			id/=2;
			boolean containsReef=(id%2==1);
			return new State(letterMinus3,letterMinus2,letterMinus1,containsArea,containsFare,containsFree,containsReef);
		}
		public State move(char letter)	{
			String lastLetters=new String(new char[] {letterMinus3,letterMinus2,letterMinus1,letter});
			boolean area=containsArea;
			boolean fare=containsFare;
			boolean free=containsFree;
			boolean reef=containsReef;
			if ("AREA".equals(lastLetters))	{
				if (area) return null;
				area=true;
			}	else if ("FARE".equals(lastLetters))	{
				if (fare) return null;
				fare=true;
			}	else if ("FREE".equals(lastLetters))	{
				if (free) return null;
				free=true;
			}	else if ("REEF".equals(lastLetters))	{
				if (reef) return null;
				reef=true;
			}
			return new State(lastLetters.charAt(1),lastLetters.charAt(2),lastLetters.charAt(3),area,fare,free,reef);
		}
		@Override
		public int hashCode()	{
			return getId();
		}
		@Override
		public boolean equals(Object other)	{
			State sOther=(State)other;
			return (letterMinus3==sOther.letterMinus3)&&(letterMinus2==sOther.letterMinus2)&&(letterMinus1==sOther.letterMinus1)&&
					(containsArea==sOther.containsArea)&&(containsFare==sOther.containsFare)&&(containsFree==sOther.containsFree)&&(containsReef==sOther.containsReef);
		}
		public boolean isFinal()	{
			return containsArea&&containsFare&&containsFree&&containsReef;
		}
	}
	
	private static Multimap<Integer,Pair<Integer,Character>> getTransitionMap()	{
		// Because of the way we are going to iterate, the key is the TARGET of the transition determined by each pair present in the values.
		Multimap<Integer,Pair<Integer,Character>> result=HashMultimap.create();
		for (int i=0;i<TOTAL_STATES;++i)	{
			State state=State.getStateFromId(i);
			for (char c:LETTERS)	{
				State transition=state.move(c);
				if (transition!=null) result.put(transition.getId(),new Pair<>(state.getId(),c));
			}
		}
		return result;
	}
	
	private static int[] getFinalStates()	{
		List<Integer> result=new ArrayList<>();
		for (int i=0;i<TOTAL_STATES;++i)	{
			State state=State.getStateFromId(i);
			if (state.isFinal()) result.add(i);
		}
		return result.stream().mapToInt(Integer::intValue).toArray();
	}
	
	private static long[] initForLength3()	{
		long[] result=new long[TOTAL_STATES];
		for (int i=0;i<64;++i) result[i]=1;
		return result;
	}
	
	private static void iterate(long[] previous,long[] current,Multimap<Integer,Pair<Integer,Character>> transitionMap)	{
		Arrays.fill(current,0l);
		for (Map.Entry<Integer,Pair<Integer,Character>> entry:transitionMap.entries()) current[entry.getKey()]+=previous[entry.getValue().first];
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] array1=initForLength3();
		long[] array2=new long[TOTAL_STATES];
		Multimap<Integer,Pair<Integer,Character>> transitions=getTransitionMap();
		for (int i=0;i<SIZE-3;++i)	{
			iterate(array1,array2,transitions);
			// Swap to move "previous" array into "current" one and vice versa.
			long[] swap=array1;
			array1=array2;
			array2=swap;
		}
		long result=0l;
		for (int i:getFinalStates()) result+=array1[i];
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
