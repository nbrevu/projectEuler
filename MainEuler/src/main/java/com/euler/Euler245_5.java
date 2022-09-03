package com.euler;

import java.util.List;

import com.euler.common.Primes;

public class Euler245_5 {
	// Code from the forum, just slightly modified.
	private final static long N=(long)2e11;

	private static long semi(int[] pr)	{
	    long p,q,sum=0;
	    for (int i=1;(p=pr[i])*p<N;i++)	{
	        for (int j=i+1;p*(q=pr[j])<N;j++)	{
	            if (q>p*p) break;
	            if ((p*q-1)%(p+q-1)==0) sum+=p*q;
	        }
	    }
	    return sum;
	}
	    
	private static long multi(int f,int pi,long p,long phi,int[] pr)	{
	    long q,sum=0;
	    if (f>2 && (p-1)%(p-phi)==0) sum=p;
	    for (int i=pi;i<pr.length;i++)	{
	        q=pr[i];
	        if (p*q>N) break;
	        if (f==0 && q*q*q>N) break;
	        if (f==1 && p*q*q>N) break;
	        sum+=multi(f+1,i+1,p*q,phi*(q-1),pr);
	    }
	    return sum;
	}

	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int s=(int)Math.pow(N,2d/3);
		List<Integer> primeList=Primes.listIntPrimes(s);
		int[] pr=new int[primeList.size()];
		for (int i=0;i<primeList.size();++i) pr[i]=primeList.get(i).intValue();
	    long sum=0;
	    sum+=semi(pr);
	    sum+=multi(0,1,1,1,pr);
	    long tac=System.nanoTime();
	    System.out.println(sum);
	    double seconds=(tac-tic)*1e-9;
	    System.out.println("Elapsed "+seconds+" seconds.");
	}
}
