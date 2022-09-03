package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.math.LongMath;

public class Euler632 {
	private final static long BASE=10l;
	private final static int MAX_EXP=16;
	private final static long MOD=1000000007l;
	
	private static int getPrimeFactors(long in,long[] primeSieve)	{
		Set<Long> factors=new HashSet<>();
		while (in>1)	{
			long p=primeSieve[(int)in];
			if (p==0) p=in;
			factors.add(p);
			in/=p;
		}
		return factors.size();
	}
	
	private static long[] getPowers(long base,int exp)	{
		long[] result=new long[1+exp];
		result[0]=1l;
		for (int i=1;i<=exp;++i) result[i]=result[i-1]*base;
		return result;
	}
	
	private static <R,C> void increaseCounter(Table<R,C,Long> table,R rowValue,C columnValue,long tally)	{
		Long current=table.get(rowValue,columnValue);
		if (current==null) current=tally;
		else current+=tally;
		table.put(rowValue,columnValue,current);
	}
	
	private static Table<Integer,Integer,Long> getSquareFactorsTable(long base,int maxExp)	{
		Table<Integer,Integer,Long> result=HashBasedTable.create();
		long[] powers=getPowers(base,maxExp);
		long maxNumber=LongMath.pow(base,maxExp);
		long sieveMax=LongMath.sqrt(maxNumber,RoundingMode.DOWN);
		long[] primeSieve=Primes.firstPrimeSieve(sieveMax);
		for (long i=1;i<=sieveMax;++i)	{
			int primeFactors=getPrimeFactors(i,primeSieve);
			if (i==-1) continue;
			long sq=i*i;
			for (int j=1;j<=maxExp;++j)	{
				long pow=powers[j];
				if (pow<sq) continue;
				long counter=EulerUtils.getSquareFreeNumbers(pow/sq);
				increaseCounter(result,j,primeFactors,counter);
			}
		}
		return result;
	}
	
	private static BigInteger getProduct(Table<Integer,Integer,Long> table)	{
		BigInteger result=BigInteger.ONE;
		for (Long value:table.values())	{
			BigInteger factor=BigInteger.valueOf(value);
			result=result.multiply(factor);
		}
		return result;
	}
	
	private static void printTable(Table<Integer,Integer,Long> table)	{
		for (Map.Entry<Integer,Map<Integer,Long>> row:table.rowMap().entrySet())	{
			System.out.print(row.getKey()+": ");
			for (Map.Entry<Integer,Long> value:row.getValue().entrySet()) System.out.print(value.getValue()+" ");
			System.out.println();
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Table<Integer,Integer,Long> squareFactorsTable=getSquareFactorsTable(BASE,MAX_EXP);
		printTable(squareFactorsTable);
		BigInteger bigResult=getProduct(squareFactorsTable);
		BigInteger result=bigResult.mod(BigInteger.valueOf(MOD));
		long tac=System.nanoTime();
		System.out.println("Big result: "+bigResult);
		System.out.println("Result: "+result);
		double seconds=(tac-tic)/1e9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
