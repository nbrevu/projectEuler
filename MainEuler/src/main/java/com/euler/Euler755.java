package com.euler;

public class Euler755 {
	public static void main(String[] args)	{
		long fPrev=0;
		long fCurr=1;
		long maxSum=0l;
		int count=1;
		do	{
			long fNext=fPrev+fCurr;
			System.out.println(fNext);
			maxSum+=fNext;
			System.out.println("Sum of the first "+count+" members: "+maxSum+".");
			fPrev=fCurr;
			fCurr=fNext;
			++count;
		}	while (fCurr<10_000_000_000_000l);
		System.out.println(count);
	}
}
