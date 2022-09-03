package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Euler366_3 {
	private final static long N=LongMath.pow(10l,18);
	private final static BigInteger MOD=BigInteger.valueOf(LongMath.pow(10l,8));
	
	private static class ConsecutiveInterval	{
		private final long startingValue;
		private final long endingValue;
		public ConsecutiveInterval(long startingValue,long endingValue)	{
			this.startingValue=startingValue;
			this.endingValue=endingValue;
		}
		public long getLength()	{
			return endingValue+1-startingValue;
		}
		public BigInteger sum()	{
			return BigInteger.valueOf(endingValue+startingValue).multiply(BigInteger.valueOf(getLength())).divide(BigInteger.TWO);
		}
		public ConsecutiveInterval keepLastValues(long n)	{
			return new ConsecutiveInterval(endingValue+1-n,endingValue);
		}
		public ConsecutiveInterval keepFirstValues(long n)	{
			return new ConsecutiveInterval(startingValue,startingValue+n-1);
		}
	}
	private static class FibonacciBlock	{
		private final List<ConsecutiveInterval> subIntervals;
		public FibonacciBlock(List<ConsecutiveInterval> subIntervals)	{
			this.subIntervals=subIntervals;
		}
		public List<ConsecutiveInterval> keepLastValues(long toKeep)	{
			Stack<ConsecutiveInterval> invertedResult=new Stack<>();
			int currentIndex=subIntervals.size()-1;
			while (toKeep>0)	{
				ConsecutiveInterval thisInterval=subIntervals.get(currentIndex);
				long thisLength=thisInterval.getLength();
				if (thisLength>=toKeep)	{
					invertedResult.push(thisInterval.keepLastValues(toKeep));
					toKeep=0;
				}	else	{
					invertedResult.push(thisInterval);
					toKeep-=thisLength;
				}
				--currentIndex;
			}
			List<ConsecutiveInterval> result=new ArrayList<>(invertedResult.size());
			while (!invertedResult.isEmpty()) result.add(invertedResult.pop());
			return result;
		}
		public FibonacciBlock keepFirstValues(long toKeep)	{
			List<ConsecutiveInterval> result=new ArrayList<>();
			for (ConsecutiveInterval interval:subIntervals)	{
				long length=interval.getLength();
				if (length<=toKeep)	{
					result.add(interval);
					toKeep-=length;
					if (toKeep==0) break;
				}	else	{
					result.add(interval.keepFirstValues(toKeep));
					break;
				}
			}
			return new FibonacciBlock(result);
		}
		private void storeIn(ObjIntMap<ConsecutiveInterval> intervalMap)	{
			for (ConsecutiveInterval interval:subIntervals) intervalMap.addValue(interval,1);
		}
	}
	private static List<FibonacciBlock> buildFibonacciBlocks(long upTo)	{
		List<FibonacciBlock> result=new ArrayList<>();
		long prevFibo=2l;
		long currentFibo=3l;
		for (;;)	{
			List<ConsecutiveInterval> thisInterval=new ArrayList<>();
			long seqLength=prevFibo;
			long halfLength=LongMath.divide(currentFibo,2l,RoundingMode.UP);
			ConsecutiveInterval initialInterval=new ConsecutiveInterval(0l,halfLength-1);
			thisInterval.add(initialInterval);
			long remaining=seqLength-halfLength;
			if (remaining>0)	{
				FibonacciBlock baseBlock=result.get(result.size()-2);
				thisInterval.addAll(baseBlock.keepLastValues(remaining));
			}
			FibonacciBlock thisBlock=new FibonacciBlock(thisInterval);
			long nextFibo=prevFibo+currentFibo;
			if (nextFibo>upTo)	{
				long toKeep=upTo+1-currentFibo;
				result.add(thisBlock.keepFirstValues(toKeep));
				return result;
			}	else result.add(thisBlock);
			prevFibo=currentFibo;
			currentFibo=nextFibo;
		}
	}
	
	private static BigInteger sum(List<FibonacciBlock> blocks)	{
		ObjIntMap<ConsecutiveInterval> intervals=HashObjIntMaps.newMutableMap();
		for (FibonacciBlock block:blocks) block.storeIn(intervals);
		BigInteger result=BigInteger.ZERO;
		for (ObjIntCursor<ConsecutiveInterval> cursor=intervals.cursor();cursor.moveNext();)	{
			BigInteger partialSum=cursor.key().sum();
			result=result.add(partialSum.multiply(BigInteger.valueOf(cursor.value())));
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		List<FibonacciBlock> buildingBlocks=buildFibonacciBlocks(N);
		BigInteger bigResult=sum(buildingBlocks);
		long result=bigResult.mod(MOD).longValue();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
