package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.google.common.math.IntMath;

public class Euler348 {
	// I didn't expect to get this at the first try! I assumed that the fifth number would be unbearably high.
	private final static int LIMIT=1000000000;
	
	private static int[] getSquares(int upTo)	{
		int max=IntMath.sqrt(upTo, RoundingMode.FLOOR);
		int[] result=new int[1+max];
		for (int i=1;i<=max;++i) result[i]=i*i;
		return result;
	}
	
	private static int[] getCubes(int upTo)	{
		int max=(int)(Math.floor(Math.cbrt((double)upTo)));
		int[] result=new int[1+max];
		for (int i=1;i<=max;++i) result[i]=i*i*i;
		return result;
	}
	
	public static int[] getSums(int[] source1,int[] source2,int limit)	{
		int[] result=new int[1+limit];
		for (int i=2;i<source1.length;++i) for (int j=2;j<source2.length;++j)	{
			int sum=source1[i]+source2[j];
			if (sum>limit) break;
			++result[sum];
		}
		return result;
	}
	
	public static boolean isPalindrome(int in)	{
		List<Integer> digits=new ArrayList<>();
		while (in>0)	{
			digits.add(in%10);
			in/=10;
		}
		int N=digits.size()-1;
		for (int i=0;i<=N/2;++i) if (digits.get(i)!=digits.get(N-i)) return false;
		return true;
	}
	
	public static void main(String[] args)	{
		int[] squares=getSquares(LIMIT);
		int[] cubes=getCubes(LIMIT);
		int[] sums=getSums(squares,cubes,LIMIT);
		int count=0;
		long sum=0l;
		for (int i=2;i<=LIMIT;++i) if ((sums[i]==4)&&isPalindrome(i))	{
			++count;
			sum+=i;
			if (count==5) break;
		}
		if (count<5) System.out.println("Sólo he encontrado "+count+" coincidencias.");
		else System.out.println(sum);
	}
}
