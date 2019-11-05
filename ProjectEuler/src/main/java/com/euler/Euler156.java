package com.euler;

import com.euler.common.Timing;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler156 {
	private final static long LIMIT=LongMath.pow(10,11);
	
	private static long f(long n,long digit)	{
		long count=0;
		for (long factor=1;;factor*=10)	{
			long n_factor=n/factor;
			if (n_factor==0) return count;
			long current=n_factor%10;
			long higher=n/(factor*10);
			if (current<digit) count+=higher*factor;
			else if (current==digit)	{
				long lower=n-(n_factor*factor);
				count+=higher*factor+lower+1;
			}	else count+=(higher+1)*factor;
		}
	}
	
	private static void binarySearch(long digit,long lower,long upper,LongSet result)	{
		if (lower+1>=upper)	{
			if (f(lower,digit)==lower) result.add(lower);
			return;
		}
		long mid=(upper+lower)/2;
		long lowVal=f(lower,digit);
		long midVal=f(mid,digit);
		long upVal=f(upper,digit);
		if ((midVal>=lower)&&(mid>=lowVal)) binarySearch(digit,lower,mid,result);
		if ((upVal>=mid)&&(upper>=midVal)) binarySearch(digit,mid,upper,result);
	}
	
	private static long solve()	{
		long result=0;
		for (long d=1;d<=9;++d)	{
			LongSet elements=HashLongSets.newMutableSet();
			binarySearch(d,1,LIMIT,elements);
			for (LongCursor cursor=elements.cursor();cursor.moveNext();) result+=cursor.elem();
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler156::solve);
	}
}