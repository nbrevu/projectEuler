package com.euler;

import java.util.Arrays;
import java.util.List;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.ObjLongCursor;
import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashObjLongMaps;

public class Euler269_3 {
	private final static int DIGITS=8;
	
	private static class CounterResult	{
		public final long totalSolution;
		public final long solutionWith0;
		public CounterResult(long totalSolution,long solutionWith0)	{
			this.totalSolution=totalSolution;
			this.solutionWith0=solutionWith0;
		}
	}
	
	private static class IntArray	{
		public final int[] data;
		private final int hashCode;
		public IntArray(int[] data)	{
			this.data=data;
			hashCode=Arrays.hashCode(data);
		}
		@Override
		public boolean equals(Object other)	{
			return Arrays.equals(data,((IntArray)other).data);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
	}
	
	private static class CaseCounter	{
		private final int[] roots;
		private final long[] maxValues;
		private final int digits;
		private final IntArray allZeroes;
		public CaseCounter(int[] roots,int digits)	{
			this.roots=roots;
			maxValues=new long[roots.length];
			for (int i=0;i<roots.length;++i)	{
				int posRoot=-roots[i];
				maxValues[i]=LongMath.pow(posRoot,digits/2);
				if (posRoot<=3) maxValues[i]*=100;
			}
			this.digits=digits;
			allZeroes=new IntArray(new int[roots.length]);
		}
		private ObjLongMap<IntArray> oneDigitCases()	{
			ObjLongMap<IntArray> result=HashObjLongMaps.newMutableMap();
			for (int i=1;i<=9;++i)	{
				int[] array=new int[roots.length];
				Arrays.fill(array,i);
				result.put(new IntArray(array),1l);
			}
			return result;
		}
		private ObjLongMap<IntArray> evolve(ObjLongMap<IntArray> previous)	{
			ObjLongMap<IntArray> result=HashObjLongMaps.newMutableMap();
			for (ObjLongCursor<IntArray> cursor=previous.cursor();cursor.moveNext();)	{
				int[] previousArray=cursor.key().data;
				long count=cursor.value();
				boolean isValid=true;
				for (int i=0;i<=9;++i)	{
					int[] nextArray=new int[previousArray.length];
					for (int j=0;j<nextArray.length;++j)	{
						nextArray[j]=previousArray[j]*roots[j]+i;
						if (Math.abs(nextArray[j])>maxValues[j])	{
							isValid=false;
							break;
						}
					}
					if (!isValid) break;
					result.addValue(new IntArray(nextArray),count);
				}
			}
			return result;
		}
		public CounterResult count()	{
			ObjLongMap<IntArray> cases=oneDigitCases();
			long prevCount=0;
			long curCount=0;
			for (int i=2;i<=digits;++i)	{
				cases=evolve(cases);
				System.out.println("Vaya vaya, tengo "+cases.size()+" cosas...");
				prevCount=curCount;
				curCount+=cases.getLong(allZeroes);
			}
			return new CounterResult(curCount,prevCount);
		}
	}
	
	public static void main(String[] args)	{
		for (int i=-1;i>=-10;--i)	{
			CaseCounter counter=new CaseCounter(new int[] {i},DIGITS);
			CounterResult result=counter.count();
			System.out.println(String.format("For root %d, the total is %d, with %d cases with 0",i,result.totalSolution,result.solutionWith0));
		}
		// Ridiculously slow even for N=9, but WHAT IF, hear me out, WHAT IF LIMITS WERE FOUND DYNAMICALLY?
		int[] array=new int[2];
		for (array[0]=-1;array[0]>=-10;--array[0]) for (array[1]=array[0]-1;array[1]*array[0]<10;--array[1])	{
			CaseCounter counter=new CaseCounter(array,DIGITS);
			CounterResult result=counter.count();
			System.out.println(String.format("For roots %s, the total is %d, with %d cases with 0",Arrays.toString(array),result.totalSolution,result.solutionWith0));
		}
		List<int[]> threeNCases=Arrays.asList(new int[] {-1,-2,-3},new int[] {-1,-2,-4});
		for (int[] array3:threeNCases)	{
			CaseCounter counter=new CaseCounter(array3,DIGITS);
			CounterResult result=counter.count();
			System.out.println(String.format("For roots %s, the total is %d, with %d cases with 0",Arrays.toString(array3),result.totalSolution,result.solutionWith0));
		}
	}
}
