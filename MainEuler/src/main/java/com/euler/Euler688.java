package com.euler;

public class Euler688 {
	public static void main(String[] args)	{
		long limit=100;
		long result=0;
		for (long k=1;;++k)	{
			long t=k*(k-1)/2;
			if (t>limit) break;
			boolean finished=false;
			for (int l=1;!finished;++l)	{
				long limInf=k*l+t;
				long limSup=k*(l+1)+t;
				if (limInf>limit) finished=true;
				else if (limSup>limit)	{
					finished=true;
					long count=limit+1-limInf;
					result+=l*count;
				}	else result+=(limSup-limInf)*l;
			}
		}
		System.out.println(result);
	}
}
