package com.euler.experiments;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils;

public class PowerMods {
	// Confirmed result: this a^(totient(n)+1)==a mod n only if n doesn't have repeated prime factors.
	private final static int LIMIT=100;
	
	private static int totient(int in)	{
		// Horrible way to do it, but this is a quick and dirty experiment.
		int result=0;
		for (int i=1;i<in;++i) if (EulerUtils.gcd(in,i)==1) ++result;
		return result;
	}
	
	private static List<Integer> counterExamples(int in)	{
		int t=totient(in);
		List<Integer> result=new ArrayList<>();
		for (int i=0;i<in;++i)	{
			int mod=(int)EulerUtils.expMod(i,1+t,in);
			if (mod!=i) result.add(i);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		for (int i=2;i<=LIMIT;++i)	{
			List<Integer> result=counterExamples(i);
			if (!result.isEmpty()) System.out.println("Counterexample found for "+i+": "+result+".");
		}
	}
}
