package com.euler;

import static java.lang.Math.exp;
import static java.lang.Math.log;

import java.util.SortedSet;
import java.util.TreeSet;

public class Euler568 {
	/*
	 * J_A(n)=sum(binom(n,k)/k,k,1,n)/2^n
	 * J_B(n)=sum(1/(k*binom(n,k)),k,1,n)
	 */
	private final static int N=123456789;
	
	private static double[] getLogFactorials(int n)	{
		double[] result=new double[1+n];
		result[0]=0.0;
		result[1]=0.0;
		for (int i=2;i<=n;++i) result[i]=result[i-1]+log((double)i);
		return result;
	}
	
	private static double[] getAllBinomials(double[] logFactorials)	{
		int n=logFactorials.length-1;
		double[] result=new double[n+1];
		double last=logFactorials[n];
		for (int i=0;i<=n;++i) result[i]=last-logFactorials[i]-logFactorials[n-i];
		return result;
	}
	
	private static class LogWithSign implements Comparable<LogWithSign>	{
		public final double logarithm;
		public final boolean isNegative;
		public LogWithSign(double logarithm,boolean isNegative)	{
			this.logarithm=logarithm;
			this.isNegative=isNegative;
		}
		@Override
		public int compareTo(LogWithSign o) {
			if (logarithm<o.logarithm) return -1;
			else if (logarithm>o.logarithm) return 1;
			else return -1;
		}
		@Override
		public boolean equals(Object o)	{
			return (Object)this==o;
		}
		@Override
		public int hashCode()	{
			return super.hashCode();
		}
		@Override
		public String toString()	{
			return (isNegative?"(-)":"(+)")+logarithm;
		}
	}
	
	private static void addAllJas(SortedSet<LogWithSign> container,int N,double[] binomials)	{
		/*
		 * J_A(n)=sum(binom(n,k)/k,k,1,n)/2^n
		 */
		double log2MinusN=(double)(-N)*log(2.0);
		for (int i=1;i<=N;++i)	{
			if ((i%1000000)==0) System.out.println("(A) "+i+"...");
			double log=binomials[i]-log((double)i)+log2MinusN;
			container.add(new LogWithSign(log,true));
		}
	}
	
	private static void addAllJbs(SortedSet<LogWithSign> container,int N,double[] binomials)	{
		/*
		 * J_B(n)=sum(1/(k*binom(n,k)),k,1,n)
		 */
		for (int i=1;i<=N;++i)	{
			if ((i%1000000)==0) System.out.println("(B) "+i+"...");
			double log=-log((double)i)-binomials[i];
			container.add(new LogWithSign(log,false));
		}
	}
	
	private static double addAll(SortedSet<LogWithSign> container)	{
		double res=0.0;
		for (LogWithSign l:container)	{
			double val=exp(l.logarithm);
			if (l.isNegative) res-=val;
			else res+=val;
		}
		return res;
	}
	
	public static void main(String[] args)	{
		double[] factorials=getLogFactorials(N);
		System.out.println("Factoriales calculados...");
		double[] binomials=getAllBinomials(factorials);
		System.out.println("Binomiales calculados...");
		SortedSet<LogWithSign> augends=new TreeSet<>();
		addAllJas(augends,N,binomials);
		System.out.println("JAs añadidos...");
		addAllJbs(augends,N,binomials);
		System.out.println("JBs añadidos...");
		System.out.println(augends.size());
		System.out.println(addAll(augends));
	}
}
