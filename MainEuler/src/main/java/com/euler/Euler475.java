package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.ObjLongCursor;
import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashObjLongMaps;

public class Euler475 {
	private final static long MOD=LongMath.pow(10l,9)+7;
	private final static int SIZE=600;
	private final static int QUARTETS=SIZE/4;
	private final static int TRIOS=SIZE/3;
	
	private static class CombinationCalculator	{
		private final static int[] POWERS2=new int[] {1,2,4,8};
		private final static int[] POWERS3=new int[] {1,3,9,27};
		private final static int[] POWERS4=new int[] {1,4,16,64};
		private static class IndexCalculator	{
			/*
			 * Let [a,b,c,d,e] be the array.
			 * We have a+b+c+d+e=maxGroups, by construction. We need to generate a different index per array, so that all consecutive ones are used.
			 * We can start from the end. Clearly, given (a,b,c) there are maxGroups-(a+b+c)+1=d+e+1 possible cases, so we can use index 0 for
			 * (0,maxGroups-(a+b+c)), index 1 for (1,maxGroups-(a+b+c)-1) and so on, up until the index maxGroups(-a+b+c) for the case d=0.
			 * If we move one position leftwards, let's say that we have fixed (a,b). Then the remainder is maxGroups-(a+b)=c+d+e. If c=0, we have
			 * (maxGroups-(a+b)+1) cases; if c=1, we have (maxGroups-(a+b)+0), and so on. Clearly the total amount is triangular (maxGroups-(a+b)+1),
			 * which is (X+1)*(X+2)/2 with X=maxGroups-(a+b).
			 * If we have fixed (a) only, there are (X+1)*(X+2)*(X+3)/6 cases, where X=maxGroups-a.
			 * Finally, the total amount of combinations is (X+1)*(X+2)*(X+3)*(X+4)/24.
			 * It's going to be really convenient to have a precalculated object with all the offsets.
			 */
			private final int totalSize;
			private final int[][][] indices;
			public IndexCalculator(int maxGroups)	{
				indices=new int[1+maxGroups][][];
				int accumulatedValue=0;
				// There are elegant but complex ways to do this using recursion and what not. But this is simple and fast, and works.
				for (int i=0;i<=maxGroups;++i)	{
					int remainingAfterI=1+maxGroups-i;
					indices[i]=new int[remainingAfterI][];
					for (int j=0;j<remainingAfterI;++j)	{
						int remainingAfterJ=remainingAfterI-j;
						indices[i][j]=new int[remainingAfterJ];
						for (int k=0;k<remainingAfterJ;++k)	{
							indices[i][j][k]=accumulatedValue;
							accumulatedValue+=remainingAfterJ-k;
						}
					}
				}
				totalSize=accumulatedValue;	// Must be equal to (maxGroups+1)*(maxGroups+2)*(maxGroups+3)*(maxGroups+4)/24.
			}
			public int getTotalSize()	{
				return totalSize;
			}
			public int getIndex(int[] array)	{
				return indices[array[0]][array[1]][array[2]]+array[3];
			}
		}
		private final int maxGroups;
		private final IndexCalculator indexCalculator;
		private final CombinatorialNumberCache combinatorials;
		private final long mod;
		public CombinationCalculator(int maxGroups,long mod)	{
			this.maxGroups=maxGroups;
			indexCalculator=new IndexCalculator(maxGroups);
			combinatorials=new CombinatorialNumberCache(maxGroups);
			this.mod=mod;
		}
		private ObjLongMap<int[]> getChildren(int[] array)	{
			ObjLongMap<int[]> result=HashObjLongMaps.newMutableMap();
			int remaining1=3;
			int max1=Math.min(array[1],remaining1);
			/*
			 * Again, there are "better" ways to do this, but since the size is fixed, this works and saves me the trouble of adding yet another
			 * structure on top. As a bonus, no additional method calls are used.
			 */
			for (int i1=0;i1<=max1;++i1)	{
				int remaining2=remaining1-i1;
				int max2=Math.min(array[2],remaining2);
				long combi1=combinatorials.get(array[1],i1)%mod;
				for (int i2=0;i2<=max2;++i2)	{
					int remaining3=remaining2-i2;
					int max3=Math.min(array[3],remaining3);
					long combi2=(combi1*combinatorials.get(array[2],i2))%mod;
					for (int i3=0;i3<=max3;++i3)	{
						int i4=remaining3-i3;
						if (i4<=array[4])	{
							// OK, we have found a valid case.
							long combi3=(combi2*combinatorials.get(array[3],i3))%mod;
							long combi4=(combi3*combinatorials.get(array[4],i4))%mod;
							/*
							 * This "extraFactor" appears because we have 4 ways to choose a musician from a group of 4, and so on. The
							 * "combi1/2/3/4" variables only account for the previous quartets remains from which we choose musicians, but leaves
							 * out the actual musicians we can choose from every group!
							 */
							long extraFactor=POWERS2[i2]*POWERS3[i3]*POWERS4[i4];
							long finalValue=(combi4*extraFactor)%mod;
							int[] newArray=new int[5];
							newArray[0]=array[0]+i1;
							newArray[1]=array[1]+i2-i1;
							newArray[2]=array[2]+i3-i2;
							newArray[3]=array[3]+i4-i3;
							newArray[4]=array[4]-i4;
							result.put(newArray,finalValue);
						}
					}
				}
			}
			return result;
		}
		public long calculateCases()	{
			int[] array=new int[] {0,0,0,0,maxGroups};
			Long[] cache=new Long[indexCalculator.getTotalSize()];
			return calculateCasesRecursive(array,cache);
		}
		private long calculateCasesRecursive(int[] array,Long[] cache)	{
			if (isFinalState(array)) return 1l;
			int index=indexCalculator.getIndex(array);
			Long cachedResult=cache[index];
			if (cachedResult!=null) return cachedResult.longValue();
			long result=0l;
			for (ObjLongCursor<int[]> cursor=getChildren(array).cursor();cursor.moveNext();)	{
				long prevResult=calculateCasesRecursive(cursor.key(),cache);
				result+=(prevResult*cursor.value())%mod;
			}
			result%=mod;
			cache[index]=result;
			return result;
		}
		private boolean isFinalState(int[] array)	{
			for (int i=1;i<array.length;++i) if (array[i]>0) return false;
			return true;
		}
	}
	
	private static long getFactorialInverse(int operand,long mod)	{
		long factorial=1l;
		for (int i=2;i<=operand;++i) factorial=(factorial*i)%mod;
		return EulerUtils.modulusInverse(factorial,mod);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		CombinationCalculator combinations=new CombinationCalculator(QUARTETS,MOD);
		long sortedResult=combinations.calculateCases();
		long result=(sortedResult*getFactorialInverse(TRIOS,MOD))%MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
