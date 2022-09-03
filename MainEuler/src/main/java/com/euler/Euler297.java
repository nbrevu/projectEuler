package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.google.common.math.LongMath;

public class Euler297 {
	// Well, this was easy thanks to https://en.wikipedia.org/wiki/Zeckendorf's_theorem.
	// First try, and very fast implementation!
	private final static long LIMIT=LongMath.pow(10l,17);
	private final static List<Long> FIBONACCIS=getFibonaccis(2*LIMIT);
	
	private static List<Long> getFibonaccis(long upTo)	{
		long prev2=0;
		long prev=1;
		List<Long> result=new ArrayList<>();
		for (;;)	{
			long curr=prev+prev2;
			if (curr>upTo) return result;
			result.add(curr);
			prev2=prev;
			prev=curr;
		}
	}
	
	private final static NavigableMap<Long,Long> ZECKENDORF_SUM_FIBONACCIS=new TreeMap<>();
	private final static NavigableMap<Long,Long> zeckendorfCache=new TreeMap<>();
	
	private static void initZeckendorfCache()	{
		ZECKENDORF_SUM_FIBONACCIS.put(1l,1l);
		ZECKENDORF_SUM_FIBONACCIS.put(2l,2l);
		ZECKENDORF_SUM_FIBONACCIS.put(3l,3l);
		for (int i=3;i<FIBONACCIS.size();++i)	{
			long thisFibo=FIBONACCIS.get(i);
			long prevFibo=FIBONACCIS.get(i-1);
			long prevPrevFibo=FIBONACCIS.get(i-2);
			long previous=ZECKENDORF_SUM_FIBONACCIS.get(prevFibo);
			long middle=getZeckendorf(prevPrevFibo-1)+prevPrevFibo-1;
			long current=1l;	//Me!
			ZECKENDORF_SUM_FIBONACCIS.put(thisFibo,previous+middle+current);
		}
	}
	
	private static long getZeckendorf(long in)	{
		if (ZECKENDORF_SUM_FIBONACCIS.containsKey(in)) return ZECKENDORF_SUM_FIBONACCIS.get(in);
		if (zeckendorfCache.containsKey(in)) return zeckendorfCache.get(in);
		Map.Entry<Long,Long> previous=ZECKENDORF_SUM_FIBONACCIS.floorEntry(in);
		long lastFibo=previous.getKey();
		long lastSum=previous.getValue();
		long diff=in-lastFibo;
		long result=lastSum+diff+getZeckendorf(diff);
		zeckendorfCache.put(in,result);
		return result;
	}
	
	public static void main(String[] args)	{
		initZeckendorfCache();
		System.out.println(getZeckendorf(LIMIT-1));
	}
}
