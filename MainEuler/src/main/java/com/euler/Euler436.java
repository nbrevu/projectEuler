package com.euler;

public class Euler436 {
	/*
	 * f1=x*exp(2-x)*(1-exp(-y))
	 * f2=exp(2-x-y)*(x*exp(y)-1+y)
	 * 
	 * Joint probability function of x and y, both between 0 and 1: f(x,y)=f1(x,y) if (x+y<1); f2(x,y) if (x+y>1). 
	 * 
	 * It can be checked that the joint distribution is correctly defined: int(int(f1,y,0,1-x),x,0,1)+int(int(f2,y,1-x,1),x,0,1) = 1.
	 * 
	 * The result we want is the probability that y>x, which is A+B+C:
	 * 
	 * A = int(int(f1,y,x,1-x),x,0,1/2) = 5/8e + 5e^(3/2) - 13/4e^2.
	 * B = int(int(f2,y,1-x,1),x,0,1/2) = e^(1/2) - 5/8e - 13/4e^(3/2) + 2e^2.
	 * C = int(int(f2,y,x,1),x,1/2,1) = 1/4 - e^(1/2) + 7/2e - 7/4e^(3/2)
	 * 
	 * Total = 1/4 + 7/2e - 5/4e^2.
	 * 
	 * ACHTUNG! Someone in the comments thread mentions the following:
	 * "I'm curious whether continuous time Markov chains could be useful here, as they were in problem 394".
	 * So, ZUTUN! TODO! TEHDÄ!!!!! Investigate what "continuous time Markov chains" are and whether I can learn to use them and solve 394.
	 * 
	 * (In general, the comment thread includes a bunch of discussions about statistics which are at just about my level. Nice).
	 * 
	 * See also: https://en.wikipedia.org/wiki/Irwin%E2%80%93Hall_distribution
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		double result=0.25+3.5*Math.exp(1.0)-1.25*Math.exp(2.0);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.printf("%.11f.\n",result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
