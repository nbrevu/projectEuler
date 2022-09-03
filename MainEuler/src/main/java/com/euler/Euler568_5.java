package com.euler;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

public class Euler568_5 {
	/*
	 * J_A(n)=sum(binom(n,k)/k,k,1,n)/2^n
	 * J_B(n)=sum(1/(k*binom(n,k)),k,1,n)
	 */
	private final static int N=123456789;
	
	private final static int DIGITS=20;
	
	private final static Apfloat[] LOGS=getIntLogs(N);
	
	private static Apfloat[] getIntLogs(int n)	{
		Apfloat[] result=new Apfloat[1+n];
		result[1]=Apfloat.ZERO;
		for (int i=2;i<=n;++i) result[i]=ApfloatMath.log(new Apfloat(i,DIGITS));
		return result;
	}
	
	private static Apfloat[] getLogFactorials(int n)	{
		Apfloat[] result=new Apfloat[1+n];
		result[0]=Apfloat.ZERO;
		result[1]=Apfloat.ZERO;
		for (int i=2;i<=n;++i) result[i]=result[i-1].add(LOGS[i]);
		return result;
	}
	
	private static Apfloat[] getAllBinomials(Apfloat[] logFactorials)	{
		int n=logFactorials.length-1;
		Apfloat[] result=new Apfloat[n+1];
		Apfloat last=logFactorials[n];
		for (int i=0;i<=n;++i) result[i]=last.subtract(logFactorials[i]).subtract(logFactorials[n-i]);
		return result;
	}
	
	private static Apfloat[] getAllJas(int N,Apfloat[] binomials)	{
		/*
		 * J_A(n)=sum(binom(n,k)/k,k,1,n)/2^n
		 */
		Apfloat[] result=new Apfloat[N];
		Apfloat log2MinusN=new Apfloat(-N).multiply(ApfloatMath.log(new Apfloat(2,DIGITS)));
		for (int i=1;i<=N;++i)	{
			if ((i%1000000)==0) System.out.println("(A) "+i+"...");
			Apfloat log=binomials[i].subtract(LOGS[i]).add(log2MinusN);
			result[i-1]=log;
		}
		return result;
	}
	
	private static Apfloat[] getAllJbs(int N,Apfloat[] binomials)	{
		/*
		 * J_B(n)=sum(1/(k*binom(n,k)),k,1,n)
		 */
		Apfloat[] result=new Apfloat[N];
		for (int i=1;i<=N;++i)	{
			if ((i%1000000)==0) System.out.println("(B) "+i+"...");
			Apfloat log=LOGS[i].negate().subtract(binomials[i]);
			result[i-1]=log;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Apfloat[] factorials=getLogFactorials(N);
		System.out.println("Factoriales calculados...");
		Apfloat[] binomials=getAllBinomials(factorials);
		System.out.println("Binomiales calculados...");
		Apfloat[] allJas=getAllJas(N,binomials);
		System.out.println("JAs añadidos...");
		Apfloat[] allJbs=getAllJbs(N,binomials);
		System.out.println("JBs añadidos...");
		Apfloat res=Apfloat.ZERO;
		for (int i=0;i<allJas.length;++i)	{
			if ((i%1000000)==0) System.out.println("(S) "+i+"...");
			Apfloat expB=ApfloatMath.exp(allJbs[i]);
			Apfloat expA=ApfloatMath.exp(allJas[i]);
			res=res.add(expB.subtract(expA));
		}
		System.out.println(res);
	}
}
