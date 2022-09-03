package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Euler806_9 {
	/*
	 * These are used as KEYS. We only consider three states: ABC, BAC, CAB. We use steps of level two so that odd permutations are never needed.
	 */
	private static class Additions	{
		private final int[] data;
		public Additions(int a,int b,int c)	{
			data=new int[] {a,b,c};
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(data);
		}
		@Override
		public boolean equals(Object other)	{
			return Arrays.equals(data,((Additions)other).data);
		}
		public Additions sum(Additions other)	{
			return new Additions(data[0]+other.data[0],data[1]+other.data[1],data[2]+other.data[2]);
		}
		public boolean isLosing()	{
			return (data[0]^data[1]^data[2])==0;
		}
	}
	
	private static class Transition	{
		// This should always have three elements, but Java is a bit stupid about arrays of generic objects.
		private final List<Map<Additions,long[]>> transitions;
		private Transition(List<Map<Additions,long[]>> transitions)	{
			this.transitions=transitions;
		}
		public static Transition getInitial()	{
			long[] oneAbc=new long[] {1,0,0};
			long[] oneBca=new long[] {0,1,0};
			long[] oneCab=new long[] {0,0,1};
			Additions _200=new Additions(2,0,0);
			Additions _020=new Additions(0,2,0);
			Additions _002=new Additions(0,0,2);
			Additions _110=new Additions(1,1,0);
			Additions _101=new Additions(1,0,1);
			Additions _011=new Additions(0,1,1);
			Map<Additions,long[]> forAbc=Map.of(_200,oneAbc,_110,oneCab,_011,oneBca,_002,oneAbc);
			Map<Additions,long[]> forBca=Map.of(_020,oneBca,_011,oneAbc,_101,oneCab,_200,oneBca);
			Map<Additions,long[]> forCab=Map.of(_002,oneCab,_101,oneBca,_110,oneAbc,_020,oneCab);
			return new Transition(List.of(forAbc,forBca,forCab));
		}
		public Transition combine(Transition other)	{
			List<Map<Additions,long[]>> result=new ArrayList<>(3);
			for (int i=0;i<3;++i)	{
				Map<Additions,long[]> t1=transitions.get(i);
				Map<Additions,long[]> thisResult=new HashMap<>();
				for (Map.Entry<Additions,long[]> entry1:t1.entrySet())	{
					Additions add=entry1.getKey();
					long[] counters=entry1.getValue();
					for (int j=0;j<3;++j)	{
						long thisCounter=counters[j];
						if (thisCounter==0) continue;
						Map<Additions,long[]> toCombine=other.transitions.get(j);
						for (Map.Entry<Additions,long[]> entry2:toCombine.entrySet())	{
							Additions next=add.sum(entry2.getKey());
							long[] read=entry2.getValue();
							long[] write=thisResult.computeIfAbsent(next,(Additions unused)->new long[3]);
							for (int k=0;k<3;++k) write[k]+=thisCounter*read[k];
						}
					}
				}
				result.add(thisResult);
			}
			return new Transition(result);
		}
	}
	
	public static void main(String[] args)	{
		Transition t2=Transition.getInitial();
		Transition t4=t2.combine(t2);
		Transition t8=t4.combine(t4);
		Transition t10=t2.combine(t8);
		Map<Additions,long[]> usefulData=t10.transitions.get(0);
		long result=0;
		for (Map.Entry<Additions,long[]> entry:usefulData.entrySet())	{
			Additions keys=entry.getKey();
			if (!keys.isLosing()) continue;
			long[] counters=entry.getValue();
			result+=counters[0]+counters[1]+counters[2];
		}
		result/=2;
		result*=1023;
		System.out.println(result);
	}
}
