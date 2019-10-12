package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.Timing;

public class Euler115 {
	private final static int M=50;
	private final static long LIMIT=1000000l;
	private static long solve()	{
		List<long[]> combinations=new ArrayList<>();
		combinations.add(new long[] {1l,0l});
		for (int i=1;;++i)	{
			long[] newArray=new long[2];
			long[] prev=combinations.get(i-1);
			newArray[0]=prev[0]+prev[1];
			for (int j=0;j<=i-M;++j) newArray[1]+=combinations.get(j)[0];
			if (newArray[0]+newArray[1]>=LIMIT) return i;
			combinations.add(newArray);
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler115::solve);
	}
}
