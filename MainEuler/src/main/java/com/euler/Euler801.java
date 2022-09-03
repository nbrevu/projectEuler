package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler801 {
	public static void main(String[] args)	{
		long[] primes=Primes.listLongPrimesAsArray(100l);
		long result=0;
		for (long p:primes)	{
			long thisResult=0;
			long lim=p*(p-1);
			System.out.println(String.format("Pairs for %d:",p));
			for (int x=1;x<=lim;++x)	{
				long g=0;
				for (int y=1;y<=lim;++y)	{
					long p1=EulerUtils.expMod(x,y,p);
					long p2=EulerUtils.expMod(y,x,p);
					if (p1==p2)	{
						System.out.println(String.format("\t(%d,%d)=>%d",x,y,p1));
						++g;
					}
				}
				System.out.println(String.format("\t\tg(%d,%d)=%d.",p,x,g));
				thisResult+=g;
			}
			System.out.println(String.format("f(%d)=%d=%d*%d.",p,thisResult,p-1,thisResult/(p-1)));
			result+=thisResult;
		}
		System.out.println(result);
	}
}
