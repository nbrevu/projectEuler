package com.euler;

import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler463 {
	// Algo está mal. Incluso el resultado para N=100 es incorrecto.
	// (Also, where the fuck did I get this? IIRC I copied this from somewhere. It's about time I did this MYSELF).
	private final static long LIMIT=100;//LongMath.pow(3l,37);
	private final static long MOD=1000000000l;
	
	private static LongLongMap funCache=HashLongLongMaps.newMutableMap();
	static	{
		funCache.put(0l,0l);
		funCache.put(1l,1l);
		funCache.put(3l,3l);
	}
	
	private static long calculateFun(long value)	{
		long result=funCache.getOrDefault(value,-1l);
		if (result==-1)	{
			if ((value%2)==0) result=calculateFun(value/2);
			else	{
				long n=value/4;
				if ((value%4)==1) result=2*calculateFun(2*n+1)-calculateFun(n);
				else result=3*calculateFun(2*n+1)-calculateFun(n);
			}
			result%=MOD;
			funCache.put(value,result);
		}
		return result;
	}
	
	private static LongLongMap sumCache=HashLongLongMaps.newMutableMap();
	static	{
		sumCache.put(0l,0l);
	}
	
	private static long calculateSum(long value)	{
		long result=sumCache.getOrDefault(value,-1l);
		if (result==-1)	{
			long mod=value%4;
			long q=value/4;
			if (mod!=3) result=calculateFun(value)+calculateSum(value-1);
			else result=6*calculateSum(2*q+1)-8*calculateSum(q)-1;
			result%=MOD;
			sumCache.put(value,result);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long result=calculateSum(LIMIT);
		System.out.println(result);
	}
}