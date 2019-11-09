package com.euler;

import com.euler.common.Timing;

public class Euler162 {
	private final static int N=16;
	
	private static String solve()	{
		long results[][]=new long[N][8];
		results[0][0]=13;
		results[0][1]=1;
		results[0][2]=1;
		long accumulated=0l;
		for (int i=1;i<N;++i)	{
			for (int j=0;j<8;++j)	{
				long prev=results[i-1][j];
				results[i][j]+=13*prev;
				results[i][j|1]+=prev;
				results[i][j|2]+=prev;
				results[i][j|4]+=prev;
			}
			accumulated+=results[i][7];
		}
		return Long.toHexString(accumulated).toUpperCase();
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler162::solve);
	}
}
