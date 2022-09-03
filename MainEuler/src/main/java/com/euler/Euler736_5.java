package com.euler;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Euler736_5 {
	private final static int N=45;
	
	private static class PowerOf2Cache	{
		private final long[] powers;
		private final long[][] powerDiffs;
		public PowerOf2Cache(int maxPower)	{
			/*-
			 * Max Power!
			 * He's the man,
			 * Whose name you'd love to touch!
			 * But you mustn't touch.
			 * His name sounds good in your ear,
			 * but when you say it, you mustn't.
			 */
			powers=new long[1+maxPower];
			powers[0]=1l;
			powerDiffs=new long[1+maxPower][];
			powerDiffs[0]=new long[] {0l};
			for (int i=1;i<=maxPower;++i)	{
				powers[i]=powers[i-1]*2;
				powerDiffs[i]=new long[i];
				for (int j=0;j<i;++j) powerDiffs[i][j]=powers[i]-powers[j];
			}
		}
		public long getPowerOf2(int i)	{
			return powers[i];
		}
		public long getPowerOf2Diff(int i,int j)	{
			return powerDiffs[i][j];
		}
	}
	private final static PowerOf2Cache POWER_OF_2_CACHE=new PowerOf2Cache(63);
	private static class RPowers	{
		public final static Comparator<RPowers> DEFAULT_COMPARATOR=Comparator.comparingLong(RPowers::getFullValue).reversed();
		private final NavigableMap<Integer,Integer> content;
		private final long value;
		public RPowers(int n)	{
			content=new TreeMap<>();
			content.put(n,n);
			// rValue=n*2^n; sValue=n*2^0=n. value=rValue-sValue.
			value=(POWER_OF_2_CACHE.getPowerOf2(n)-1)*n;
		}
		private RPowers(NavigableMap<Integer,Integer> content,long value)	{
			this.content=content;
			this.value=value;
		}
		public long getFullValue()	{
			return value;
		}
		@Override
		public String toString()	{
			StringBuilder result=new StringBuilder();
			for (boolean operation:getSequence()) result.append(operation?'s':'r');
			return result.toString();
		}
		public void getChildren(Set<RPowers> toWrite)	{
			NavigableMap<Integer,Integer> commonCopy=new TreeMap<>(content);
			Map.Entry<Integer,Integer> entryToMove=commonCopy.pollFirstEntry();
			int key=entryToMove.getKey();
			int count=entryToMove.getValue();
			for (int i=0;i<key;++i)	for (int j=1;j<=count;++j)	{
				NavigableMap<Integer,Integer> copiedMap=new TreeMap<>(commonCopy);
				copiedMap.put(i,j);
				if (j<count) copiedMap.put(key,count-j);
				long rDiff=j*POWER_OF_2_CACHE.getPowerOf2Diff(key,i);
				long sDiff=(key-i)*(POWER_OF_2_CACHE.getPowerOf2(j)-1);
				toWrite.add(new RPowers(copiedMap,value-(rDiff+sDiff)));
			}
		}
		public long evaluateX(long in)	{
			long result=in;
			for (boolean operation:getSequence()) result+=operation?result:1;
			return result;
		}
		private int recoverN()	{
			int result=0;
			for (Integer x:content.values()) result+=x.intValue();
			return result;
		}
		private boolean[] getSequence()	{
			int n=recoverN();
			boolean[] result=new boolean[2*n];
			int index=0;
			for (int i=n;i>0;--i)	{
				index+=content.getOrDefault(i,0);
				result[index]=true;
				++index;
			}
			return result;
		}
	}
	
	private static RPowers getConfiguration(int initialValue)	{
		for (int n=1+initialValue;;++n)	{
			long goal=initialValue*POWER_OF_2_CACHE.getPowerOf2(n);
			// Sorted by RValue (descending).
			NavigableSet<RPowers> cases=new TreeSet<>(RPowers.DEFAULT_COMPARATOR);
			cases.add(new RPowers(n));
			while (!cases.isEmpty())	{
				RPowers currentValue=cases.pollFirst();
				long diff=currentValue.getFullValue();
				if (diff==goal) return currentValue;
				else if (diff<goal) break;
				else currentValue.getChildren(cases);
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		RPowers optimalConf=getConfiguration(N);
		long value=optimalConf.evaluateX(N);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(value);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
