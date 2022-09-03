package com.euler;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class Euler371 {
	private final static int SIZE=1000;
	
	public static void main(String[] args)	{
		/*
		double without500=0.0;
		double with500=0.0;
		for (int i=SIZE/4;i>=0;--i)	{
			double factor=SIZE-2*(i+1);
			double den=SIZE-(i+1);
			with500=(SIZE+factor*with500)/den;
			without500=(SIZE+factor*without500+with500)/den;
		}
		System.out.println(String.format(Locale.UK,"%.8f",without500));
		*/
		System.out.println(String.format(Locale.UK,"%.8f",simulate(100000000l)));
	}
	
	private static long simulate(Random rand)	{
		Set<Integer> present=new HashSet<>();
		long count=1;
		for (;;)	{
			int draw=rand.nextInt(SIZE);
			if (present.contains(SIZE-draw)) return count;
			present.add(draw);
			++count;
		}
	}
	
	private static double simulate(long simulations)	{
		Random rand=new Random();
		long sum=0;
		for (long i=0;i<simulations;++i) sum+=simulate(rand);
		return ((double)sum)/((double)simulations);
	}
}
