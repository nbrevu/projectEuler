package com.euler;

import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.LongWithModFenwickTree;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler378_4 {
	private final static int UP_TO=60_000_000;
	private final static long MOD=LongMath.pow(10l,18);
	
	// This method is surprisingly slow. I mean, 6e7 is high for an O(n*log(n)) algorithm, but almost 40 seconds??
	private static int[] getTriangularDivisors(int maxSize)	{
		int[] firstPrimes=Primes.firstPrimeSieve(maxSize+1);
		int[] result=new int[1+maxSize];
		result[0]=0;
		DivisorHolder previous=new DivisorHolder();
		for (int i=1;i<=maxSize;++i)	{
			DivisorHolder current=DivisorHolder.getFromFirstPrimes(i+1,firstPrimes);
			DivisorHolder triangular=DivisorHolder.combine(previous,current);
			triangular.removeFactor(2l);
			result[i]=(int)triangular.getAmountOfDivisors();
			previous=current;
		}
		return result;
	}
	
	/*
	 * Ok, I need to sort my thoughts.
	 * - The Fenwick trees are dynamic and get updated with each iteration. Therefore we can talk about "Fenwick trees during (i.e. at the start
	 * of) iteration X".
	 * - The first Fenwick tree stores the cumulative count of triangular numbers whose amount of divisors is X. That is, at each iteration we
	 * can just store the value of dT(i) in this array. So, when we are at iteration j, we have stored all the values 1<=i<j, so we can easily
	 * retrieve the amount of values such that 1<=i<j and dT(i)>dT(j), just by getting the difference between the stored total and the value for
	 * dT(j).
	 * - Now, this difference can be stored in the second level Fenwick tree, in the entry indexed with dT(j). This means that, at every iteration
	 * k, we can calculate the difference between the stored total and the value for dT(k), and it's precisely the amount of triples with
	 * 1<=i<j<k and dT(i)>dT(j)>dT(k).
	 */
	private static class FenwickTrees	{
		private final LongWithModFenwickTree basicFenwickTree;
		private final LongWithModFenwickTree secondLevelFenwickTree;
		public FenwickTrees(int size,long mod)	{
			basicFenwickTree=new LongWithModFenwickTree(size,mod);
			secondLevelFenwickTree=new LongWithModFenwickTree(size,mod);
		}
		public long readValue(int dTK)	{
			return secondLevelFenwickTree.getTotal()-secondLevelFenwickTree.readData(dTK);
		}
		public void storeValue(int dTK)	{
			long currentDiff=basicFenwickTree.getTotal()-basicFenwickTree.readData(dTK);
			basicFenwickTree.putData(dTK,1l);
			secondLevelFenwickTree.putData(dTK,currentDiff);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] triangulars=getTriangularDivisors(UP_TO);
		int maxValue=Arrays.stream(triangulars).max().getAsInt();
		FenwickTrees state=new FenwickTrees(maxValue,MOD);
		long result=0;
		for (int i=1;i<=UP_TO;++i)	{
			int dt=triangulars[i];
			state.storeValue(dt);
			result=(result+state.readValue(dt))%MOD;
		}
		System.out.println(result);
		long tac=System.nanoTime();
		double seconds=(1e-9)*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
