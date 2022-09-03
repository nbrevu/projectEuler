package com.euler;

import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.math.LongMath;

public class Euler580_2 {
	private final static long LIMIT=LongMath.pow(10l,7);
	
	public static void main(String[] args)	{
		SortedSet<Long> hilberts=new TreeSet<>();
		for (long i=1;i<=LIMIT;i+=4)	{
			boolean isValid=true;
			for (long l:hilberts)	{
				if (l==1) continue;
				long l2=l*l;
				if (i<l2) break;
				else if ((i%l2)==0)	{
					isValid=false;
					break;
				}
			}
			if (isValid) hilberts.add(i);
		}
		System.out.println(hilberts.size());
	}
}
