package com.euler;

import com.euler.common.LongMatrix;

public class Euler588_6 {
	private final static int N=18;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		LongMatrix d0=new LongMatrix(4);
		d0.assign(0,0,1l);
		d0.assign(0,1,1l);
		d0.assign(0,2,2l);
		d0.assign(2,1,1l);
		d0.assign(2,3,2l);
		LongMatrix d1=new LongMatrix(4);
		d1.assign(0,1,1l);
		d1.assign(0,2,2l);
		d1.assign(1,0,1l);
		d1.assign(2,0,1l);
		d1.assign(2,3,2l);
		d1.assign(3,1,1l);
		long result=0l;
		long pow=1l;
		for (int i=1;i<=N;++i)	{
			pow*=10;
			long[] vector=new long[] {1,0,0,0};
			long n=pow;
			while (n>0)	{
				vector=(((n&1)==0)?d0:d1).multiply(vector);
				n>>=1;
			}
			result+=vector[0]+3*vector[1]+2*vector[2]+4*vector[3];
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
