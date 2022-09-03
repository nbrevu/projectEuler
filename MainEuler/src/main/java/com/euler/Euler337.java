package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.LongWithModFenwickTree;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler337 {
	//*
	private final static int LIMIT=20_000_000;
	/*/
	private final static int LIMIT=10000;
	//*/
	private final static long MOD=LongMath.pow(10l,8);
	private final static int INITIAL_VALUE=6;
	
	private static int[] calculateTotients(int upTo)	{
		int[] firstPrimes=Primes.firstPrimeSieve(upTo);
		int[] result=new int[1+upTo];
		result[1]=1;
		for (int i=2;i<=upTo;++i) result[i]=(int)DivisorHolder.getFromFirstPrimes(i,firstPrimes).getTotient();
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] totients=calculateTotients(LIMIT);
		/*
		 * I THINK that this can be done using Fenwick trees, but I might be wrong.
		 * Let's assume that a Fenwick tree contains the cumulative sums of the sequence where the index X represents the sequences where the
		 * last totient is X.
		 * 
		 * OK, not so easy.
		 * - I need to "clamp" the totient between tot(ai) and ai. BUT at the time of reading I only have a_{i+1}, and ai can be many numbers.
		 * - The iteration scheme guarantees that ai < a_{i+1}. Depending on how I build the tree, I could guarantee phi(ai) < phi(a_{i+1}) OR
		 * phi(a_{i+1}) < ai, but not both. Maybe I could "add" a negative value in "ai" during the update? Would that work? Probably not?
		 * 
		 * The thing is, IF the "add negative" works, it would be as easy as:
		 * - Iterate for ai.
		 * - Let T be the Fenwick tree cumulative value for tot(ai).
		 * - Add T to the result.
		 * - Add T to the Fenwick tree at position tot(ai).
		 * - Add minus T to the Fenwick tree at position ai.
		 * 
		 * NARRATOR: it didn't work. At least the run time is a little above 40s, which is nice.
		 * Maybe an off-by-one error is fucking with me? I don't think so but it's worth checking.
		 */
		LongWithModFenwickTree fenwickTree=new LongWithModFenwickTree(LIMIT,MOD);
		fenwickTree.putData(totients[INITIAL_VALUE],1l);
		fenwickTree.putData(INITIAL_VALUE,MOD-1l);
		long result=1l;	// The starting sequence, {6}.
		for (int i=1+INITIAL_VALUE;i<=LIMIT;++i)	{
			int totient=totients[i];
			long thisValue=fenwickTree.readData(totient);
			if (thisValue==0l) continue;
			result+=thisValue;
			// Yep, it WAS an off-by-one error :|.
			fenwickTree.putData(totient+1,thisValue);
			fenwickTree.putData(i,MOD-thisValue);
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
