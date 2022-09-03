package com.euler;

import java.util.Map;
import java.util.TreeMap;

import com.euler.common.EulerUtils.CombinatorialNumberCache;

public class Euler242_2 {
	private final static long LIMIT=1000000000000l;
	
	private static class Divider	{
		private int firstBitPosition;
		private long remainder;
		public Divider(long in)	{
			if (in==0) throw new IllegalArgumentException();
			firstBitPosition=62;	// Not 63. Negative sign will bite you.
			long power=1l<<firstBitPosition;
			while (power>in)	{
				--firstBitPosition;
				power>>=1l;
			}
			remainder=in-power;
		}
		public int getFirstBitPosition()	{
			return firstBitPosition;
		}
		public long getRemainder()	{
			return remainder;
		}
	}
	
	private final static CombinatorialNumberCache COMBINATORIALS=new CombinatorialNumberCache(63);
	
	private static Map<Integer,Long> getMapByWeight(int bits)	{
		Map<Integer,Long> result=new TreeMap<>();
		for (int i=0;i<=bits;++i) result.put(i,COMBINATORIALS.get(bits,i));
		return result;
	}
	
	private static long structuredCalculation(long in)	{
		final long N=(in-1)/4;
		long n=N;
		Map<Integer,Long> countersByWeight=new TreeMap<>();
		for (int a=0;;++a)	{
			if (n==0)	{
				Long oldValue=countersByWeight.get(a);
				Long newValue=1+((oldValue==null)?0l:oldValue.longValue());
				countersByWeight.put(a,newValue);
				break;
			}
			Divider divider=new Divider(n);
			Map<Integer,Long> baseMap=getMapByWeight(divider.getFirstBitPosition());
			for (Map.Entry<Integer,Long> entry:baseMap.entrySet())	{
				int weight=entry.getKey()+a;
				Long oldValue=countersByWeight.get(weight);
				Long newValue=entry.getValue()+((oldValue==null)?0l:oldValue.longValue());
				countersByWeight.put(weight,newValue);
			}
			n=divider.getRemainder();
		}
		long result=0;
		for (Map.Entry<Integer,Long> entry:countersByWeight.entrySet())	{
			long pow=1l<<(long)(entry.getKey());
			result+=pow*entry.getValue();
		}
		return result;
	}

	public static void main(String[] args)	{
		System.out.println(structuredCalculation(LIMIT)+".");
	}
}
