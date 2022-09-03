package com.euler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.LongPair;

public class Euler787_2 {
	private final static int N=1000;
	
	private static class GameDeterminator	{
		private static LongPair[] getValidTurns(LongPair state)	{
			LongPair[] result=new LongPair[2];
			int index=0;
			for (int i=0;i<=state.x;++i) for (int j=((i==0)?1:0);j<=state.y;++j)	{
				long diff=state.x*j-state.y*i;
				if (Math.abs(diff)==1)	{
					result[index]=new LongPair(i,j);
					++index;
				}
			}
			if (index!=2) throw new IllegalStateException();
			return result;
		}
		private final Map<LongPair,LongPair[]> nextTurns;
		public GameDeterminator(int n)	{
			nextTurns=new HashMap<>();
			for (int i=1;i<n;++i) for (int j=1;j+i<=n;++j) if (EulerUtils.areCoprime(i,j))	{
				LongPair pair=new LongPair(i,j);
				nextTurns.put(pair,getValidTurns(pair));
			}
		}
		public Map<LongPair,Boolean> getWinningPositions()	{
			Map<LongPair,Boolean> result=new HashMap<>();
			for (Map.Entry<LongPair,LongPair[]> entry:nextTurns.entrySet()) if (!result.containsKey(entry.getKey())) result.put(entry.getKey(),isWinning(entry.getValue(),result));
			return result;
		}
		private boolean isWinning(LongPair[] children,Map<LongPair,Boolean> knownValues)	{
			for (LongPair child:children)	{
				if ((child.x==0)||(child.y==0)) return true;
				Boolean status=knownValues.get(child);
				if (status!=null)	{
					status=isWinning(nextTurns.get(child),knownValues);
					knownValues.put(child,status);
				}
				if (!status.booleanValue()) return true;
			}
			return false;
		}
	}
	
	/*
	 * Is the pattern REALLY this simple??
	 * - Let z=min(a,b). If z is ODD, the position wins. If z is EVEN, the position loses. Really?
	 */
	public static void main(String[] args)	{
		Comparator<LongPair> sorter=Comparator.comparingLong((LongPair a)->a.x).thenComparingLong((LongPair a)->a.y);
		GameDeterminator calculator=new GameDeterminator(N);
		SortedSet<LongPair> keys=new TreeSet<>(sorter);
		Map<LongPair,LongPair[]> allChildren=calculator.nextTurns;
		Map<LongPair,Boolean> fullStatus=calculator.getWinningPositions();
		keys.addAll(fullStatus.keySet());
		int counter=0;
		int otherCounter=0;
		for (LongPair p:keys)	{
			boolean status=fullStatus.get(p);
			if (status) ++counter;
			else ++otherCounter;
			LongPair[] children=allChildren.get(p);
			System.out.println(String.format("If a=%d and b=%d (%s) we can do:",p.x,p.y,status?"WINNING":"LOSING"));
			for (LongPair c:children)	{
				Boolean cStatus=fullStatus.get(c);
				String statusString=(cStatus==null)?"ALREADY WON":(cStatus.booleanValue()?"WINNING":"LOSING");
				System.out.println(String.format("\ta'=%d, b'=%d (%s).",c.x,c.y,statusString));
			}
		}
		System.out.println(counter);
		System.out.println(otherCounter);
	}
}
