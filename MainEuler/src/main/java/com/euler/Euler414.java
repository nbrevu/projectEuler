package com.euler;

import java.util.Arrays;

public class Euler414 {
	private static int[] getKaprekarConstant(int base,int n)	{
		int[] p1=new int[n];
		int[] p2=new int[n];
		int[] buffer=new int[n];
		for (int i=0;i<n;++i) p1[i]=i;
		int[] current=p1;
		int[] next=p2;
		for (;;)	{
			System.arraycopy(current,0,buffer,0,n);
			Arrays.sort(buffer);
			int carry=0;
			for (int i=0;i<n;++i)	{
				int below=buffer[n-1-i];
				int above=buffer[i];
				int diff=above-below-carry;
				if (diff<0)	{
					diff+=base;
					carry=1;
				}	else carry=0;
				next[n-1-i]=diff;
			}
			if (carry>0) throw new RuntimeException("JAJA SI");
			if (Arrays.equals(next,current)) return next;
			int[] swap=current;
			current=next;
			next=swap;
		}
		
	}
	
	public static void main(String[] args)	{
		for (int i=15;i<=111;i+=6) System.out.println("i="+i+": "+Arrays.toString(getKaprekarConstant(i,5))+".");
	}
}
