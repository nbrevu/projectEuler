package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.euler.common.Primes.RabinMiller;

public class Euler196 {
	private final static Collection<Integer> WITNESSES=Arrays.asList(2,3,5,7,11,13,17);
	private final static RabinMiller RM=new RabinMiller();
	
	private static Collection<int[]> findNeighbours(boolean[][] rows,int r,int c)	{
		Collection<int[]> res=new ArrayList<>();
		for (int i=r-1;i<r+2;i+=2) for (int j=c-1;j<c+2;++j) if ((j>=0)&&(j<rows[i].length)) if (rows[i][j])	{
			int[] coords=new int[2];
			coords[0]=i;
			coords[1]=j;
			res.add(coords);
		}
		return res;
	}
	
	private final static long getSumOfPrimes(int row)	{
		boolean[][] neededRows=new boolean[5][];
		neededRows[0]=new boolean[row-2];
		neededRows[1]=new boolean[row-1];
		neededRows[2]=new boolean[row];
		neededRows[3]=new boolean[row+1];
		neededRows[4]=new boolean[row+2];
		long lRow=(long)row;
		long number=((lRow-2)*(lRow-3))/2;
		for (int k=0;k<5;++k)	{
			int maxIndex=row-2+k;
			for (int i=0;i<maxIndex;++i)	{
				++number;
				if ((number%2)==1) if (RM.isPrime(BigInteger.valueOf(number),WITNESSES)) neededRows[k][i]=true;
			}
		}
		long sum=0;
		long shift=(((lRow-1l)*lRow)/2l)+1l;
		for (int i=0;i<row;++i) if (neededRows[2][i])	{
			boolean isTriplet=false;
			Collection<int[]> neighbours=findNeighbours(neededRows,2,i);
			if (neighbours.size()>=2) isTriplet=true;
			else if (neighbours.size()==1)	{
				int[] neighbour=neighbours.iterator().next();
				Collection<int[]> otherNeighbours=findNeighbours(neededRows,neighbour[0],neighbour[1]);
				if (otherNeighbours.size()>=2) isTriplet=true;
			}
			if (isTriplet) sum+=shift+(long)i;
		}
		return sum;
	}
	
	public static void main(String[] args)	{
		long res=getSumOfPrimes(5678027)+getSumOfPrimes(7208785);
		System.out.println(res);
	}
}
