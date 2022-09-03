package com.euler;

import java.util.Locale;

import com.euler.common.Primes;

public class Euler779_2 {
	private final static int LIMIT=100000;	// Yes, this is enough to make it converge :|. I lost a lot of time being VERY stupid with this.
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		double sum=0;
		long[] primes=Primes.listLongPrimesAsArray(LIMIT);
		double h=1;
		for (long p:primes)	{
			double dp=p;
			double p_=dp-1;
			sum+=1/(p_*p_*dp)*h;	// This gets sum(f_K).
			// sum+=1/(p_*dp*dp)*h;	// This gets f_1.
			h*=1-(1/dp);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.12f",sum));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
