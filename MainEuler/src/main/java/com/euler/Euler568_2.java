package com.euler;

import static java.lang.Math.exp;
import static java.lang.Math.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Euler568_2 {
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
	
	private static double[] getAllJas(int N,double[] binomials)	{
		/*
		 * J_A(n)=sum(binom(n,k)/k,k,1,n)/2^n
		 */
		double[] result=new double[N];
		double log2MinusN=(double)(-N)*log(2.0);
		for (int i=1;i<=N;++i)	{
			if ((i%1000000)==0) System.out.println("(A) "+i+"...");
			double log=binomials[i]-log((double)i)+log2MinusN;
			result[i-1]=log;
		}
		return result;
	}
	
	private static double[] getAllJbs(int N,double[] binomials)	{
		/*
		 * J_B(n)=sum(1/(k*binom(n,k)),k,1,n)
		 */
		double[] result=new double[N];
		for (int i=1;i<=N;++i)	{
			if ((i%1000000)==0) System.out.println("(B) "+i+"...");
			double log=-log((double)i)-binomials[i];
			result[i-1]=log;
		}
		return result;
	}
	
	private static double addOrdered(Iterator<Double> toAdd,Iterator<Double> toRemove)	{
		double res=0.0;
		double nextToAdd=toAdd.next();
		double nextToRemove=toRemove.next();
		for (;;)	{
			if (Double.isNaN(nextToAdd))	{
				res-=exp(nextToRemove);
				if (toRemove.hasNext()) nextToRemove=toRemove.next();
				else return res;
			}	else if (Double.isNaN(nextToRemove))	{
				res+=exp(nextToAdd);
				if (toAdd.hasNext()) nextToAdd=toAdd.next();
				else return res;
			}	else if (nextToAdd>nextToRemove)	{
				res-=exp(nextToRemove);
				if (toRemove.hasNext()) nextToRemove=toRemove.next();
				else nextToRemove=Double.NaN;
			}	else	{
				res+=exp(nextToAdd);
				if (toAdd.hasNext()) nextToAdd=toAdd.next();
				else nextToAdd=Double.NaN;
			}
		}
	}
	
	public static void main(String[] args)	{
		double[] factorials=getLogFactorials(N);
		System.out.println("Factoriales calculados...");
		double[] binomials=getAllBinomials(factorials);
		System.out.println("Binomiales calculados...");
		double[] allJas=getAllJas(N,binomials);
		System.out.println("JAs añadidos...");
		double[] allJbs=getAllJbs(N,binomials);
		System.out.println("JBs añadidos...");
		List<Double> jaList=new ArrayList<>(N);
		List<Double> jbList=new ArrayList<>(N);
		for (int i=0;i<N;++i)	{
			jaList.add(allJas[i]);
			jbList.add(allJbs[i]);
		}
		Collections.sort(jaList);
		Collections.sort(jbList);
		/*
		double res=0.0;
		for (int i=0;i<jaList.size();++i)	{
			res+=exp(jbList.get(i));
			res-=exp(jaList.get(i));
		}
		*/
		double res=addOrdered(jbList.iterator(),jaList.iterator());
		System.out.println(res);
	}
}
