package com.euler;

public class Euler441_2 {
	public static void main(String[] args)	{
		int M=100;
		double[] mBruteForce=new double[1+M];
		for (int i=1;i<=M;++i) for (int k=1;k<i;++k) mBruteForce[i]+=1d/(k*(i-k));
		double[] mFinesse=new double[1+M];
		mFinesse[1]=0;
		mFinesse[2]=1;
		for (int i=3;i<=M;++i)	{
			double num1=2*i-3;
			double den1=i;
			double num2=(i-2)*(i-2);
			double den2=(i-1)*i;
			mFinesse[i]=num1/den1*mFinesse[i-1]-num2/den2*mFinesse[i-2];
		}
		for (int i=1;i<=M;++i) System.out.println(String.format("f(%d)=%f or %f (diff=%f).",i,mBruteForce[i],mFinesse[i],Math.abs(mBruteForce[i]-mFinesse[i])));
	}
}
