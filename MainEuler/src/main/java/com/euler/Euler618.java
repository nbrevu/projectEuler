package com.euler;

import java.util.List;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler618 {
	private final static int FIB_MAX_INDEX=24;
	private final static long MOD=1000000000l;
	
	private static int[] generateFibonacci(int maxIndex)	{
		int[] result=new int[1+maxIndex];
		result[0]=0;
		result[1]=1;
		for (int i=2;i<=maxIndex;++i) result[i]=result[i-1]+result[i-2];
		return result;
	}
	
	private static long[][] fillTable(int maxNumber,long mod)	{
		List<Integer> primes=Primes.listIntPrimes(maxNumber);
		int howManyPrimes=primes.size();
		long[][] sums=new long[howManyPrimes][1+maxNumber];
		for (int i=0;i<howManyPrimes;++i) sums[i][0]=1l;	// All the others are 0.
		// Fill first row.
		int prime=primes.get(0);	// Which is 2, in case you didn't know.
		for (int j=prime;j<=maxNumber;j+=prime) sums[0][j]=EulerUtils.expMod(prime,j/prime,mod);
		// Fill the rest of the rows.
		for (int i=1;i<howManyPrimes;++i)	{
			prime=primes.get(i);
			for (int j=1;j<=maxNumber;++j)	{
				sums[i][j]=sums[i-1][j];
				// And now we go backwards.
				int prevIndex=j-prime;
				long factor=prime;
				while (prevIndex>=0)	{
					long sumAddend=(factor*sums[i-1][prevIndex])%mod;
					sums[i][j]=(sums[i][j]+sumAddend)%mod;
					prevIndex-=prime;
					factor=(factor*prime)%mod;
				}
			}
		}
		return sums;
	}
	
	public static void main(String[] args)	{
		int[] fibos=generateFibonacci(FIB_MAX_INDEX);
		long[][] sums=fillTable(fibos[FIB_MAX_INDEX],MOD);
		long result=0;
		for (int i=2;i<=FIB_MAX_INDEX;++i) result+=sums[sums.length-1][fibos[i]];
		result%=MOD;
		System.out.println(result);
	}
}
