package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.euler.common.Timing;
import com.google.common.math.LongMath;

public class Euler141 {
	private final static long LIMIT=LongMath.pow(10l,12);
	
	private static boolean isSquare(long n)	{
		long sq=LongMath.sqrt(n,RoundingMode.DOWN);
		return sq*sq==n;
	}
	
	private static long solve()	{
		long result=0;
		long s3Limit=(long)(Math.floor(Math.pow(LIMIT,1d/3d)));
		for (long num=2;num<=s3Limit;++num)	{
			long n3=num*num*num;
			for (long den=1;den<num;++den)	{
				long d2=den*den;
				if (n3*d2>=LIMIT) break;
				else if (!EulerUtils.areCoprime(num,den)) continue;
				for (long c=1;;++c)	{
					long n=(n3*den*c*c)+(c*d2);
					if (n>=LIMIT) break;
					else if (isSquare(n)) result+=n;
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler141::solve);
	}
}
