package com.euler;

public class Euler382 {
	public static void main(String[] args)	{
		int[] fMinus2=new int[] {2,0,0,0,2};
		int[] fMinus1=new int[] {4,1,0,2,7};
		int[] f=new int[] {8,3,1,7,19};
		int[] fNext=new int[5];
		for (int i=7;i<=25;++i)	{
			fNext[0]=2*f[0];
			fNext[1]=f[0]+fMinus2[0]+fMinus2[1]+fMinus2[2]-1;
			fNext[2]=f[1]+fMinus2[2];
			fNext[3]=f[4];
			fNext[4]=fNext[0]+fNext[1]+fNext[2]+fNext[3];
			System.out.println(String.format("f(%d)=%d.",i,fNext[4]));
			int[] swap=fMinus2;
			fMinus2=fMinus1;
			fMinus1=f;
			f=fNext;
			fNext=swap;
		}
	}
}
