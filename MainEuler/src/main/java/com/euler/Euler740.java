package com.euler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.Pair;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Euler740 {
	// ZUTUN! No da lo que tiene que dar. Usar racionales.
	private static class PaperList	{
		private final int[] state;
		public PaperList(int nPlayers)	{
			state=new int[nPlayers];
			for (int i=0;i<nPlayers;++i) state[i]=2;
		}
		private PaperList(int[] state)	{
			this.state=state;
		}
		public ObjIntMap<PaperList> getChildren(int player)	{
			ObjIntMap<PaperList> result=HashObjIntMaps.newMutableMap();
			for (ObjIntCursor<PaperList> cursor=getChildren1(player).cursor();cursor.moveNext();) for (ObjIntCursor<PaperList> cursor2=cursor.key().getChildren1(player).cursor();cursor2.moveNext();) result.addValue(cursor2.key(),cursor.value()*cursor2.value());
			return result;
		}
		private ObjIntMap<PaperList> getChildren1(int player)	{
			ObjIntMap<PaperList> result=HashObjIntMaps.newMutableMap();
			for (int i=0;i<state.length;++i) if (i!=player)	{
				int current=state[i];
				if (current!=0) result.put(getChild(i),current);
			}
			return result;
		}
		private PaperList getChild(int toRemove)	{
			int[] newState=Arrays.copyOf(state,state.length);
			--newState[toRemove];
			return new PaperList(newState);
		}
		public boolean contains(int player)	{
			return state[player]>0;
		}
		@Override
		public String toString()	{
			return Arrays.toString(state);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(state);
		}
		@Override
		public boolean equals(Object other)	{
			PaperList plOther=(PaperList)other;
			return Arrays.equals(state,plOther.state);
		}
	}
	
	private static Pair<BigInteger,BigInteger> simulate(int nPlayers)	{
		Map<PaperList,BigInteger> currentGen=new HashMap<>();
		currentGen.put(new PaperList(nPlayers),BigInteger.ONE);
		for (int i=0;i<nPlayers-1;++i)	{
			Map<PaperList,BigInteger> nextGen=new HashMap<>();
			for (Map.Entry<PaperList,BigInteger> entry:currentGen.entrySet()) for (ObjIntCursor<PaperList> cursor=entry.getKey().getChildren(i).cursor();cursor.moveNext();) EulerUtils.increaseCounter(nextGen,cursor.key(),entry.getValue().multiply(BigInteger.valueOf(cursor.value())));
			currentGen=nextGen;
			System.out.println("After player "+i+": "+nextGen);
		}
		BigInteger num=BigInteger.ZERO;
		BigInteger den=BigInteger.ZERO;
		for (Map.Entry<PaperList,BigInteger> entry:currentGen.entrySet())	{
			BigInteger value=entry.getValue();
			if (entry.getKey().contains(nPlayers-1)) num=num.add(value);
			den=den.add(value);
		}
		return new Pair<>(num,den);
	}
	
	public static void main(String[] args)	{
		Pair<BigInteger,BigInteger> result=simulate(3);
		BigInteger num=result.first;
		BigInteger den=result.second;
		System.out.println(""+num+'/'+den);
	}
}
