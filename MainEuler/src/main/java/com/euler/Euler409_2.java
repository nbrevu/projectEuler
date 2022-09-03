package com.euler;

import java.util.Arrays;

public class Euler409_2 {
	public final static int N=4;
	
	private static boolean next(int[] arr,int max)	{
		for (int i=arr.length-1;i>=0;--i)	{
			++arr[i];
			if (arr[i]>=max) arr[i]=0;
			else return true;
		}
		return false;
	}
	
	private static boolean isValid(int[] arr)	{
		for (int i=0;i<arr.length;++i) for (int j=i+1;j<arr.length;++j) if (arr[i]==arr[j]) return false;
		return true;
	}
	
	private static boolean isWinning(int[] arr)	{
		int result=0;
		for (int v:arr) result^=v;
		return (result!=0);
	}
	
	private static boolean hasZero(int[] arr)	{
		for (int v:arr) if (v==0) return true;
		return false;
	}
	
	/*-
	N=2: 6 valid positions, 6 winning ones (0 losing).
	N=3: 210 valid positions, 168 winning ones (42 losing).
	N=4: 32760 valid positions, 30240 winning ones (2520 losing).
	N=5: 20389320 valid positions, 19764360 winning ones (624960 losing).
	N=6: 48920775120 valid positions, 48159573840 winning ones (761201280 losing).
	
	Maybe it would be easier to count the losing positions. Can I do it linearly? I have a O(n^2) scheme in mind.
	 */
	public static void main(String[] args)	{
		for (int i=2;i<=N;++i)	{
			int max=1<<i;
			int[] arr=new int[i];
			Arrays.fill(arr,1);
			long valid=0;
			long winning=0;
			long zero=0;
			long zeroWinning=0;
			while (next(arr,max)) if (isValid(arr))	{
				++valid;
				boolean hasZero=hasZero(arr);
				boolean isWinning=isWinning(arr);
				if (isWinning) ++winning;
				if (hasZero) ++zero;
				if (isWinning&&hasZero) ++zeroWinning;
				if (!hasZero&&!isWinning) System.out.println("\t"+Arrays.toString(arr)+"...");
			}
			System.out.println(String.format("Nonzero cases for N=%d: %d valid positions, %d winning ones (%d losing).",i,valid-zero,winning-zeroWinning,valid-winning-zero+zeroWinning));
			System.out.println(String.format("Zero cases for N=%d: %d valid positions, %d winning ones (%d losing).",i,zero,zeroWinning,zero-zeroWinning));
			System.out.println(String.format("All cases for N=%d: %d valid positions, %d winning ones (%d losing).",i,valid,winning,valid-winning));
		}
	}
}
