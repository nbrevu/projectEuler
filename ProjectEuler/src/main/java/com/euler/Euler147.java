package com.euler;

import com.euler.common.Timing;

public class Euler147 {
	private final static long MAX_A=47;
	private final static long MAX_B=43;
	
	@SuppressWarnings("unused")
	private static long solve()	{
		// Brutal formula thanks to Faulhaber expansions. And Matlab. Very much Matlab.
		// F=symsum(symsum(f(a,b),b,1,B),a,B,A)+symsum(symsum(f(a,b),b,1,a),a,1,B-1)+symsum(symsum(f(b,a),a,1,b-1),b,2,B)+symsum(symsum((a^2+a)*(b^2+b)/4,a,1,A),b,1,B)
		long a,b;
		if (MAX_A>=MAX_B)	{
			a=MAX_A;
			b=MAX_B;
		}	else	{
			// Never happens, but here in case of experiment.
			a=MAX_B;
			b=MAX_A;
		}
		long a2=a*a;
		long a3=a2*a;
		long b2=b*b;
		long b3=b2*b;
		long b4=b2*b2;
		long b5=b3*b2;
		long b6=b3*b3;
		long cA3=5*b3+15*b2+10*b;
		long cA2=30*b4+75*b3+60*b2+15*b;
		long cA=-24*b5-30*b4+40*b3+15*b2-31*b;
		long c0=6*b6+6*b5-15*b4+9*b2-6*b;
		return (cA3*a3+cA2*a2+cA*a+c0)/180;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler147::solve);
	}
}
