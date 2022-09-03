package com.euler.experiments;

public class ReverseMultiples {
	private final static long LIMIT=9999999l;
	private final static long MAX_MULTIPLE=99999999l;
	
	private static long reverse(long in)	{
		long result=0;
		while (in>0)	{
			result*=10;
			result+=(in%10);
			in/=10;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		for (long i=1;i<=LIMIT;++i)	{
			boolean isOk=true;
			for (long j=1;j<=MAX_MULTIPLE;++j)	{
				long x=i*j;
				long rx=reverse(x);
				if ((rx%i)!=0)	{
					isOk=false;
					break;
				}
			}
			if (isOk) System.out.println(i);
		}
	}
}
