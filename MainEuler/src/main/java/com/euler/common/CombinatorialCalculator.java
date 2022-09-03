package com.euler.common;

public class CombinatorialCalculator	{
	private final long mod;
	private final long[] factorials;
	private final long[] factorialInverses;
	public CombinatorialCalculator(int size,long mod)	{
		this.mod=mod;
		factorials=new long[1+size];
		factorialInverses=new long[1+size];
		factorials[0]=1l;
		factorialInverses[0]=1l;
		for (int i=1;i<=size;++i)	{
			factorials[i]=(factorials[i-1]*i)%mod;
			factorialInverses[i]=EulerUtils.modulusInverse(factorials[i],mod);
		}
	}
	public long nChooseK(int n,int k)	{
		long num=factorials[n];
		long den=(factorialInverses[k]*factorialInverses[n-k])%mod;
		return (num*den)%mod;
	}
	public long nChooseKWithRepetition(int n,int k)	{
		long result=(n==0)?0:nChooseK(n+k-1,k);
		System.out.println(String.format("Combinaciones con repetición de %d elementos tomados de %d en %d: %d.",n,k,k,result));
		return result;
	}
}