package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.euler.common.EulerUtils;
import com.google.common.base.Predicates;
import com.google.common.math.IntMath;

public class Euler806_12 {
	private final static int N=1100;
	private final static long MOD=1_000_000_007l;
	/*
	 * These are used as KEYS. We only consider three states: ABC, BAC, CAB. We use steps of level two so that odd permutations are never needed.
	 */
	private static class Additions	{
		private final int[] data;
		public Additions(int a,int b,int c)	{
			data=new int[] {a,b,c};
		}
		public Additions(int[] data)	{
			this.data=data;
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
		@Override
		public String toString()	{
			return Arrays.toString(data);
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
		public Transition combine(Transition other,Predicate<Additions> validity,boolean generateOnlyAbc)	{
			List<Map<Additions,long[]>> result=new ArrayList<>(3);
			int maxI=generateOnlyAbc?1:3;
			for (int i=0;i<maxI;++i)	{
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
							if (!validity.test(next)) continue;
							long[] read=entry2.getValue();
							long[] write=thisResult.computeIfAbsent(next,(Additions unused)->new long[3]);
							for (int k=0;k<3;++k) write[k]=(write[k]+thisCounter*read[k])%MOD;
						}
					}
				}
				result.add(thisResult);
			}
			return new Transition(result);
		}
		public long meetInTheMiddle(Transition other,List<Additions> goals)	{
			long result=0;
			for (Map.Entry<Additions,long[]> entry:transitions.get(0).entrySet())	{
				Additions part1=entry.getKey();
				long[] multipliers=entry.getValue();
				for (Additions full:goals)	{
					int x=full.data[0]-part1.data[0];
					int y=full.data[1]-part1.data[1];
					int z=full.data[2]-part1.data[2];
					if ((x<0)||(y<0)||(z<0)) continue;
					Additions part2=new Additions(x,y,z);
					for (int i=0;i<3;++i)	{
						long multiplier=multipliers[i];
						if (multiplier==0) continue;
						long[] otherCases=other.transitions.get(i).get(part2);
						if (otherCases==null) continue;
						result+=multiplier*(otherCases[0]+otherCases[1]+otherCases[2]);
						result%=MOD;
					}
				}
			}
			return result;
		}
	}
	
	private static List<Additions> getLosingTriples(int n)	{
		int n2=n>>1;
		if ((n&1)==1) return List.of();
		n=n2;
		int bitCount=Integer.bitCount(n);
		int[] bits=new int[bitCount];
		for (int i=0;i<bitCount;++i)	{
			bits[i]=Integer.lowestOneBit(n);
			n-=bits[i];
		}
		int size=IntMath.pow(3,bitCount);
		List<Additions> result=new ArrayList<>(size);
		for (int i=0;i<size;++i)	{
			int[] thisCombination=new int[] {n2,n2,n2};
			int x=i;
			for (int j=0;j<bitCount;++j)	{
				thisCombination[x%3]-=bits[j];
				x/=3;
			}
			result.add(new Additions(thisCombination));
		}
		return result;
	}
	
	private static long combinationCalculator(int n)	{
		if ((n%2)!=0) throw new IllegalArgumentException();
		List<Additions> losingCases=getLosingTriples(n);
		int half=n/2;
		Predicate<Additions> noPred=Predicates.alwaysTrue();
		Predicate<Additions> boundPred=(Additions a)->	{
			for (int x:a.data) if (x>half) return false;
			return true;
		};
		int x=half;
		Transition transResult=null;
		Transition current=Transition.getInitial();
		// Standard iterations: full data.
		while (x>1)	{
			System.out.println("x="+x+"...");
			if ((x&1)==1)	{
				if (transResult==null) transResult=current;
				else transResult=transResult.combine(current,noPred,true);
			}
			System.out.println("And the big combination...");
			Predicate<Additions> actingPred=(x>3)?noPred:boundPred;
			current=current.combine(current,actingPred,false);
			System.out.println(String.format("The map sizes are %d, %d, %d.",current.transitions.get(0).size(),current.transitions.get(1).size(),current.transitions.get(2).size()));
			x>>=1;
		}
		long result=transResult.meetInTheMiddle(current,losingCases);
		long modInverse=(MOD+1)/2;
		result=(result*modInverse)%MOD;
		long pow2=EulerUtils.expMod(2l,n,MOD)-1;
		return (result*pow2)%MOD;
	}
	
	/*
	 * Elapsed 211.09372180000003 seconds.
	 * Very nice improvement, but still FAAAAAR too slow.
	 * 
	 * For 1100 (which has an additional 2^n element):
	 * 313237594
	 * Elapsed 2752.626255 seconds.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=combinationCalculator(N);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
