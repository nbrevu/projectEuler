package com.rosalind.utils;

import com.euler.common.EulerUtils.CombinatorialNumberCache;

public class Generation	{
	private final static CombinatorialNumberCache COMBINATORIALS=new CombinatorialNumberCache(1);
	/*
	 *  probs[i]=p means that there is a probability "p" that there are exactly "i" copies of the *dominant* allele.
	 */
	private final double[] probs;
	private Generation(double[] probs)	{
		this.probs=probs;
	}
	public static Generation getFixedGeneration(int N,int m)	{
		double[] probs=new double[2*N+1];
		probs[m]=1.0;
		return new Generation(probs);
	}
	public Generation nextGeneration()	{
		int size=probs.length;
		int N=size-1;
		double[] result=new double[size];
		for (int i=0;i<size;++i)	{
			double weight=probs[i];
			if (weight==0.0) continue;
			double p=((double)i)/((double)N);
			double[] gen=getGeneration(N,p);
			for (int j=0;j<size;++j) result[j]+=weight*gen[j];
		}
		return new Generation(result);
	}
	public Generation nextGenerations(int n)	{
		Generation result=this;
		for (int i=0;i<n;++i) result=result.nextGeneration();
		return result;
	}
	private static double[] getGeneration(int N,double p)	{
		double[] result=new double[N+1];
		for (int i=0;i<=N;++i) result[i]=COMBINATORIALS.get(N,i)*Math.pow(p,i)*Math.pow(1-p,N-i);
		return result;
	}
	@Override
	public String toString()	{
		return IoUtils.toStringWithSpaces(probs);
	}
	public double getProb(int k)	{
		return probs[k];
	}
	public double getProbOfAtLeastKRecessive(int k)	{
		double result=0;
		int maxIndex=probs.length-k;
		for (int i=0;i<maxIndex;++i) result+=probs[i];
		return result;
	}
}