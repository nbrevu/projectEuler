package com.euler;

import java.util.Locale;

import com.euler.common.EulerUtils;
import com.koloboke.collect.map.LongDoubleCursor;
import com.koloboke.collect.map.LongDoubleMap;
import com.koloboke.collect.map.hash.HashLongDoubleMaps;

public class Euler483_5 {
	private final static int LIMIT=350;
	
	private static class PartitionInfoSummary	{
		private LongDoubleMap[][][] divisorSums;
		public PartitionInfoSummary(int limit)	{
			divisorSums=new LongDoubleMap[1+limit][][];
			for (int N=1;N<=limit;++N)	{
				divisorSums[N]=new LongDoubleMap[1+N][];
				for (int n=1;n<=N;++n)	{
					int maxM=N/n;
					divisorSums[N][n]=new LongDoubleMap[1+maxM];
					for (int m=1;m<=maxM;++m) divisorSums[N][n][m]=HashLongDoubleMaps.newMutableMap();
				}
			}
		}
		public void calculate()	{
			divisorSums[1][1][1].put(1l,1d);
			int max=divisorSums.length-1;
			for (int i=2;i<max;++i)	{
				calculateFor(i,max+1-i);
				System.out.println(i+"...");
			}
			calculateFor(max,max+1);
		}
		private void calculateFor(int n,int maxDeltaNeeded)	{
			LongDoubleMap[][] baseTarget=divisorSums[n];
			for (int b=1;b<n;++b)	{
				int delta=n-b;
				if (delta>maxDeltaNeeded)	{
					continue;
				}
				LongDoubleMap[][] source=divisorSums[b];
				LongDoubleMap[] target=baseTarget[delta];
				accumulate(source,target,delta);
			}
			baseTarget[n][1].addValue(n,1d/n);
		}
		private void accumulate(LongDoubleMap[][] baseMap,LongDoubleMap[] nDeltaMap,int delta)	{
			LongDoubleMap standardMap=nDeltaMap[1];
			if (baseMap.length<=delta)	{
				for (int m=1;m<baseMap.length;++m) accumulateNew(baseMap[m],standardMap,delta);
			}	else	{
				for (int m=1;m<delta;++m) accumulateNew(baseMap[m],standardMap,delta);
				accumulateAdding(baseMap[delta],nDeltaMap,delta);
			}
		}
		private void accumulateNew(LongDoubleMap[] smallerMap,LongDoubleMap targetMap,int delta)	{
			// Accumulate an element "delta" into partitions whose maximum element is smaller than delta.
			for (int i=1;i<smallerMap.length;++i) for (LongDoubleCursor cursor=smallerMap[i].cursor();cursor.moveNext();)	{
				long newValue=EulerUtils.lcm(cursor.key(),delta);
				double newDivisor=cursor.value()/delta;
				targetMap.addValue(newValue,newDivisor);
			}
		}
		private void accumulateAdding(LongDoubleMap[] sourceMap,LongDoubleMap[] targetMap,int delta)	{
			// Accumulate an element "delta" into partitions which already have it.
			for (int i=1;i<sourceMap.length;++i) for (LongDoubleCursor cursor=sourceMap[i].cursor();cursor.moveNext();)	{
				long newValue=EulerUtils.lcm(cursor.key(),delta);
				double newDivisor=cursor.value()/(delta*(i+1));
				targetMap[i+1].addValue(newValue,newDivisor);
			}
		}
		public double getValue(int elem)	{
			double result=0;
			for (int n=1;n<=elem;++n)	{
				LongDoubleMap[] fixedN=divisorSums[elem][n];
				for (int m=1;m<fixedN.length;++m) for (LongDoubleCursor cursor=fixedN[m].cursor();cursor.moveNext();)	{
					if (cursor.key()<=0) throw new RuntimeException("Overflow :O.");
					double partitionValue=cursor.key();
					double divisor=cursor.value();
					result+=partitionValue*partitionValue*divisor;
				}
			}
			return result;
		}
	}
	
	/*
	4.992018067e+22
	Elapsed 217.3708637 seconds.
	Not correct :(.
 	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		PartitionInfoSummary data=new PartitionInfoSummary(LIMIT);
		data.calculate();
		double result=data.getValue(LIMIT);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.9e",result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
