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

public class Euler806_13 {
	private final static int N=300;
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
	
	private static class Element	{
		private final int index;
		public long value;
		public Element(int index,long value)	{
			this.index=index;
			this.value=value;
		}
	}
	
	private static class Transition	{
		// This should always have three elements, but Java is a bit stupid about arrays of generic objects.
		private final List<Map<Additions,Element>> transitions;
		private Transition(List<Map<Additions,Element>> transitions)	{
			this.transitions=transitions;
		}
		public static Transition getInitial()	{
			Element oneAbc=new Element(0,1l);
			Element oneBca=new Element(1,1l);
			Element oneCab=new Element(2,1l);
			Additions _200=new Additions(2,0,0);
			Additions _020=new Additions(0,2,0);
			Additions _002=new Additions(0,0,2);
			Additions _110=new Additions(1,1,0);
			Additions _101=new Additions(1,0,1);
			Additions _011=new Additions(0,1,1);
			Map<Additions,Element> forAbc=Map.of(_200,oneAbc,_110,oneCab,_011,oneBca,_002,oneAbc);
			Map<Additions,Element> forBca=Map.of(_020,oneBca,_011,oneAbc,_101,oneCab,_200,oneBca);
			Map<Additions,Element> forCab=Map.of(_002,oneCab,_101,oneBca,_110,oneAbc,_020,oneCab);
			return new Transition(List.of(forAbc,forBca,forCab));
		}
		public Transition combine(Transition other,Predicate<Additions> validity,boolean generateOnlyAbc)	{
			List<Map<Additions,Element>> result=new ArrayList<>(3);
			int maxI=generateOnlyAbc?1:3;
			for (int i=0;i<maxI;++i)	{
				Map<Additions,Element> t1=transitions.get(i);
				Map<Additions,Element> thisResult=new HashMap<>();
				for (Map.Entry<Additions,Element> entry1:t1.entrySet())	{
					Additions add=entry1.getKey();
					Element thisCounter=entry1.getValue();
					if (thisCounter.value==0) continue;
					Map<Additions,Element> toCombine=other.transitions.get(thisCounter.index);
					for (Map.Entry<Additions,Element> entry2:toCombine.entrySet())	{
						Additions next=add.sum(entry2.getKey());
						if (!validity.test(next)) continue;
						Element read=entry2.getValue();
						Element write=thisResult.computeIfAbsent(next,(Additions unused)->new Element(read.index,0l));
						if (write.index!=read.index) throw new IllegalStateException();
						write.value=(write.value+thisCounter.value*read.value)%MOD;
					}
				}
				result.add(thisResult);
			}
			return new Transition(result);
		}
		public long meetInTheMiddle(Transition other,List<Additions> goals)	{
			long result=0;
			for (Map.Entry<Additions,Element> entry:transitions.get(0).entrySet())	{
				Additions part1=entry.getKey();
				Element multiplier=entry.getValue();
				if (multiplier.value==0) continue;
				for (Additions full:goals)	{
					int x=full.data[0]-part1.data[0];
					int y=full.data[1]-part1.data[1];
					int z=full.data[2]-part1.data[2];
					if ((x<0)||(y<0)||(z<0)) continue;
					Additions part2=new Additions(x,y,z);
					Element otherCases=other.transitions.get(multiplier.index).get(part2);
					if (otherCases!=null)	{
						result+=multiplier.value*otherCases.value;
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
			{
				String[] strs=new String[] {"ABC","BCA","CAB"};
				for (int i=0;i<3;++i)	{
					System.out.println("\tTransitions "+strs[i]+": ");
					for (Map.Entry<Additions,Element> entry:current.transitions.get(i).entrySet())	{
						Element vals=entry.getValue();
						System.out.println(String.format("\t\t%s => {%d}[%d]",entry.getKey().toString(),vals.index,vals.value));
					}
				}
			}
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
