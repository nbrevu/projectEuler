package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.euler.common.EulerUtils;
import com.google.common.base.Predicates;
import com.google.common.math.IntMath;
import com.koloboke.collect.map.ObjLongCursor;
import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashObjLongMaps;

public class Euler806_14 {
	private final static int N=44;
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
	
	private static class StateIdentifier	{
		private final int vigil1;
		private final int vigil2;
		private final int destination1;
		private final int destination2;
		private final int destinationDefault;
		public StateIdentifier(int vigil1,int vigil2,int destination1,int destination2,int destinationDefault)	{
			this.vigil1=vigil1;
			this.vigil2=vigil2;
			this.destination1=destination1;
			this.destination2=destination2;
			this.destinationDefault=destinationDefault;
		}
		public int nextState(Additions indices)	{
			if ((indices.data[vigil1]&1)==1) return destination1;
			else if ((indices.data[vigil2]&1)==1) return destination2;
			else return destinationDefault;
		}
	}
	
	private final static StateIdentifier ID_ABC=new StateIdentifier(0,2,2,1,0);
	private final static StateIdentifier ID_BCA=new StateIdentifier(1,0,0,2,1);
	private final static StateIdentifier ID_CAB=new StateIdentifier(2,1,1,0,2);
	private final static StateIdentifier[] IDENTIFIERS=new StateIdentifier[] {ID_ABC,ID_BCA,ID_CAB};
	
	private static boolean mierdis(Additions x)	{
		int a=x.data[0];
		int b=x.data[1];
		int c=x.data[2];
		/*-
		int swap;
		if (a>b)	{
			swap=a;
			a=b;
			b=swap;
		}
		if (b>c)	{
			swap=c;
			c=b;
			b=swap;
		}
		if (a>b)	{
			swap=a;
			a=b;
			b=swap;
		}
		return (a==2)&&(b==2)&&(c==4);
		*/
		return (a==b)&&(a==c);
	}
	
	private static class Transition	{
		// This should always have three elements, but Java is a bit stupid about arrays of generic objects.
		private final List<ObjLongMap<Additions>> transitions;
		private Transition(List<ObjLongMap<Additions>> transitions)	{
			this.transitions=transitions;
		}
		public static Transition getInitial()	{
			Additions _200=new Additions(2,0,0);
			Additions _020=new Additions(0,2,0);
			Additions _002=new Additions(0,0,2);
			Additions _110=new Additions(1,1,0);
			Additions _101=new Additions(1,0,1);
			Additions _011=new Additions(0,1,1);
			ObjLongMap<Additions> forAbc=HashObjLongMaps.newImmutableMapOf(_200,1l,_110,1l,_011,1l,_002,1l);
			ObjLongMap<Additions> forBca=HashObjLongMaps.newImmutableMapOf(_020,1l,_011,1l,_101,1l,_200,1l);
			ObjLongMap<Additions> forCab=HashObjLongMaps.newImmutableMapOf(_002,1l,_101,1l,_110,1l,_020,1l);
			return new Transition(List.of(forAbc,forBca,forCab));
		}
		public Transition combine(Transition other,Predicate<Additions> validity,boolean generateOnlyAbc)	{
			List<ObjLongMap<Additions>> result=new ArrayList<>(3);
			int maxI=generateOnlyAbc?1:3;
			for (int i=0;i<maxI;++i)	{
				ObjLongMap<Additions> t1=transitions.get(i);
				ObjLongMap<Additions> thisResult=HashObjLongMaps.newMutableMap(4*other.transitions.get(0).size());
				StateIdentifier id=IDENTIFIERS[i];
				for (ObjLongCursor<Additions> cursor1=t1.cursor();cursor1.moveNext();)	{
					Additions add=cursor1.key();
					int nextState=id.nextState(add);
					long thisCounter=cursor1.value();
					if (thisCounter==0) continue;
					ObjLongMap<Additions> toCombine=other.transitions.get(nextState);
					for (ObjLongCursor<Additions> cursor2=toCombine.cursor();cursor2.moveNext();)	{
						long read=cursor2.value();
						Additions next=add.sum(cursor2.key());
						if (!validity.test(next)) continue;
						if (mierdis(next))	{
							System.out.println("DAS DEBUG!!!!!");
							System.out.println(String.format("\ti=%d, A1=%s, c1=%d, A2=%s, c2=%d, R=%s",i,add.toString(),thisCounter,cursor2.key().toString(),read,next.toString()));
						}
						thisResult.compute(next,(Additions unused,long write)->(write+thisCounter*read)%MOD);
					}
				}
				result.add(thisResult);
			}
			return new Transition(result);
		}
		public long meetInTheMiddle(Transition other,List<Additions> goals)	{
			long result=0;
			for (ObjLongCursor<Additions> cursor=transitions.get(0).cursor();cursor.moveNext();)	{
				long multiplier=cursor.value();
				if (multiplier==0) continue;
				Additions part1=cursor.key();
				for (Additions full:goals)	{
					int nextState=ID_ABC.nextState(part1);
					int x=full.data[0]-part1.data[0];
					int y=full.data[1]-part1.data[1];
					int z=full.data[2]-part1.data[2];
					if ((x<0)||(y<0)||(z<0)) continue;
					Additions part2=new Additions(x,y,z);
					long otherCases=other.transitions.get(nextState).getOrDefault(part2,0l);
					if (otherCases!=0)	{
						result+=multiplier*otherCases;
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
			/*-
			{
				String[] strs=new String[] {"ABC","BCA","CAB"};
				for (int i=0;i<3;++i)	{
					System.out.println("\tTransitions "+strs[i]+": ");
					for (ObjLongCursor<Additions> cursor=current.transitions.get(i).cursor();cursor.moveNext();)	{
						Additions key=cursor.key();
						long value=cursor.value();
						System.out.println(String.format("\t\t%s => %d",key.toString(),value));
					}
				}
			}
			*/
			x>>=1;
		}
		long result=transResult.meetInTheMiddle(current,losingCases);
		long modInverse=(MOD+1)/2;
		result=(result*modInverse)%MOD;
		long pow2=EulerUtils.expMod(2l,n,MOD)-1;
		return (result*pow2)%MOD;
	}
	
	/*
	 * Elapsed 92.3288528 seconds.
	 * Wow!
	 * 
	 * For 1100:
	 * 313237594
	 * Elapsed 1018.3720936000001 seconds.
	 * 
	 * For 2200:
	 * 24155441
	 * Elapsed 28985.4969391 seconds.
	 * 
	 * :(
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
