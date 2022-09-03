package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.math.IntMath;

public class Euler806_8 {
	private static class HanoiCombination	{
		public final int left;
		public final int middle;
		public final int right;
		public HanoiCombination(int left,int middle,int right)	{
			this.left=left;
			this.middle=middle;
			this.right=right;
		}
		public boolean isLosing()	{
			return (left^middle^right)==0;
		}
	}
	
	private static class HanoiCombinationCache	{
		private final List<List<List<HanoiCombination>>> combis;
		public HanoiCombinationCache()	{
			combis=new ArrayList<>();
			HanoiCombination combi0=new HanoiCombination(0,0,0);	// Not actually used, but it "takes space", so to speak.
			List<HanoiCombination> list00=new ArrayList<>();
			list00.add(combi0);
			List<List<HanoiCombination>> list0=new ArrayList<>();
			list0.add(list00);
			combis.add(list0);
		}
		public HanoiCombination get(int left,int middle,int right)	{
			while (left>=combis.size()) combis.add(new ArrayList<>());
			List<List<HanoiCombination>> listA=combis.get(left);
			while (middle>=listA.size()) listA.add(new ArrayList<>());
			List<HanoiCombination> listB=listA.get(middle);
			while (right>=listB.size()) listB.add(new HanoiCombination(left,middle,listB.size()));
			return listB.get(right);
		}
	}
	
	private static enum Peg	{
		A	{
			@Override
			public HanoiCombination nextCombination(HanoiCombination current, HanoiCombinationCache cache) {
				return cache.get(current.left+1,current.middle,current.right);
			}
		},	B	{
			@Override
			public HanoiCombination nextCombination(HanoiCombination current, HanoiCombinationCache cache) {
				return cache.get(current.left,current.middle+1,current.right);
			}
		},	C	{
			@Override
			public HanoiCombination nextCombination(HanoiCombination current, HanoiCombinationCache cache) {
				return cache.get(current.left,current.middle,current.right+1);
			}
		};
		public abstract HanoiCombination nextCombination(HanoiCombination current,HanoiCombinationCache cache);
	}
	
	private static enum PegOrder	{
		ABC(Peg.A,Peg.C),ACB(Peg.A,Peg.B),BAC(Peg.B,Peg.C),BCA(Peg.B,Peg.A),CAB(Peg.C,Peg.B),CBA(Peg.C,Peg.A);
		private final static Map<PegOrder,PegOrder> AFTER_0=getMapAfter0();
		private final static Map<PegOrder,PegOrder> AFTER_1=getMapAfter1();
		private static Map<PegOrder,PegOrder> getMapAfter0()	{
			Map<PegOrder,PegOrder> result=new EnumMap<>(PegOrder.class);
			result.put(ABC,ACB);
			result.put(ACB,ABC);
			result.put(BAC,BCA);
			result.put(BCA,BAC);
			result.put(CAB,CBA);
			result.put(CBA,CAB);
			return result;
		}
		private static Map<PegOrder,PegOrder> getMapAfter1()	{
			Map<PegOrder,PegOrder> result=new EnumMap<>(PegOrder.class);
			result.put(ABC,BAC);
			result.put(ACB,CAB);
			result.put(BAC,ABC);
			result.put(BCA,CBA);
			result.put(CAB,ACB);
			result.put(CBA,BCA);
			return result;
		}
		public PegOrder stateAfter0()	{
			return AFTER_0.get(this);
		}
		public PegOrder stateAfter1()	{
			return AFTER_1.get(this);
		}
		private final Peg peg0;
		private final Peg peg1;
		private PegOrder(Peg peg0,Peg peg1)	{
			this.peg0=peg0;
			this.peg1=peg1;
		}
	}
	
	private static List<int[]> getLosingTriples(int n)	{
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
		List<int[]> result=new ArrayList<>(size);
		for (int i=0;i<size;++i)	{
			int[] thisCombination=new int[] {n2,n2,n2};
			int x=i;
			for (int j=0;j<bitCount;++j)	{
				thisCombination[x%3]-=bits[j];
				x/=3;
			}
			result.add(thisCombination);
		}
		return result;
	}
	
	// This will be useful for pruning.
	private static class ValidCasesSummary	{
		private final int maxValue;
		private final int[][] maxValues2;
		private final List<int[]> losingTriples;
		public ValidCasesSummary(int n)	{
			if ((n&1)==1) throw new IllegalArgumentException("Das gefällt mir ÜBERHAUPT NICHT!!!!!");
			maxValue=n>>1;
			int x=maxValue;
			int bitCount=Integer.bitCount(x);
			int[] bits=new int[bitCount];
			for (int i=0;i<bitCount;++i)	{
				bits[i]=Integer.lowestOneBit(x);
				x-=bits[i];
			}
			int size=IntMath.pow(3,bitCount);
			losingTriples=new ArrayList<>(size);
			for (int i=0;i<size;++i)	{
				int[] thisCombination=new int[] {maxValue,maxValue,maxValue};
				int y=i;
				for (int j=0;j<bitCount;++j)	{
					thisCombination[y%3]-=bits[j];
					y/=3;
				}
				losingTriples.add(thisCombination);
			}
			SortedMap<Integer,SortedMap<Integer,Integer>> rangesMap=new TreeMap<>();	// ZUTUN! Do this carefully...
			// What I wanted to do doesn't quite make the cut.
			maxValues2=new int[1+maxValue][1+maxValue];
		}
	}
	
	public static void main(String[] args)	{
		List<int[]> triples=getLosingTriples(100_000);
		for (int[] x:triples) System.out.println(Arrays.toString(x));
	}
}
