package com.euler;

import java.math.RoundingMode;
import java.util.BitSet;
import java.util.function.IntConsumer;

import com.euler.common.BitSetCursor;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntLongMap;
import com.koloboke.collect.map.hash.HashIntLongMaps;

public class Euler734 {
	private final static int N=IntMath.pow(10,6);
	private final static int K=999983;
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	/*
	 * Iterates over the bits subsets of a given number.
	 * For example, for 1011, it should return 0000, 0001, 0010, 0011, 1000, 1001, 1010 and 1011 sequentially.
	 */
	private static class BitsIterator implements IntCursor	{
		private int[] bits;
		private boolean initial;
		private int result;
		public BitsIterator(int number)	{
			BitSet bitset=BitSet.valueOf(new long[] {number});
			bits=new int[bitset.cardinality()];
			int index=0;
			for (BitSetCursor cursor=new BitSetCursor(bitset,true);cursor.moveNext();)	{
				bits[index]=1<<cursor.elem();
				++index;
			}
			initial=true;
			result=0;
		}
		@Override
		public boolean moveNext() {
			if (initial)	{
				initial=false;
				return true;
			}
			for (int bit:bits) if ((result&bit)==0)	{
				result+=bit;
				return true;
			}	else result-=bit;
			return false;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		@Override
		public int elem() {
			return result;
		}
		@Override
		public void forEachForward(IntConsumer arg0) {
			while (moveNext()) arg0.accept(elem());
		}
	}
	
	private static class TupleCombinationBitCalculator	{
		private final int k;
		private final long mod;
		private final int minBitsNeeded;
		private final int[] primes;
		private final BitSet[] primesByBit;
		private final IntLongMap combinationCache;
		private final long[] combinations;
		public TupleCombinationBitCalculator(int n,int k,long mod)	{
			this.k=k;
			this.mod=mod;
			minBitsNeeded=IntMath.log2(n+1,RoundingMode.UP);
			primes=Primes.listIntPrimesAsArray(n);
			primesByBit=organisePrimes(primes);
			combinationCache=HashIntLongMaps.newMutableMap();
			int lastPrime=primes[primes.length-1];
			combinations=new long[1+lastPrime];
			for (int i=0;i<=lastPrime;++i) combinations[i]=combine(i);
		}
		/*
		 * Returns an array of BitSets, one per needed bit.
		 * BitSet[i].get(j) == 1 if and only if prime j has its "i" bit set to 1.
		 */
		private BitSet[] organisePrimes(int[] primes)	{
			BitSet[] result=new BitSet[minBitsNeeded];
			for (int i=0;i<minBitsNeeded;++i) result[i]=new BitSet(primes.length);
			for (int i=0;i<primes.length;++i) for (BitSetCursor cursor=new BitSetCursor(primes[i],true);cursor.moveNext();) result[cursor.elem()].set(i);
			return result;
		}
		private long combine(int number)	{
			// Special cases for even numbers.
			if ((number&1)==0) return ((number&2)==0)?0:1;
			BitSet validPrimes=new BitSet(primes.length);
			validPrimes.set(0,primes.length,true);
			for (BitSetCursor cursor=new BitSetCursor(number,false);cursor.moveNext()&&cursor.elem()<primesByBit.length;) validPrimes.andNot(primesByBit[cursor.elem()]);
			return combinationCache.computeIfAbsent(validPrimes.cardinality(),(int validPrimeCount)->EulerUtils.expMod(validPrimeCount,k,mod));
		}
		private long countCombinations(int prime)	{
			long result=0;
			int bits=Integer.bitCount(prime);
			for (BitsIterator cursor=new BitsIterator(prime);cursor.moveNext();)	{
				long thisResult=combinations[cursor.elem()];
				if (((bits-Integer.bitCount(cursor.elem()))&1)!=0) thisResult=-thisResult;
				result+=thisResult;
			}
			// Avoid negative results!
			return ((result%mod)+mod)%mod;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		TupleCombinationBitCalculator calculator=new TupleCombinationBitCalculator(N,K,MOD);
		long result=0l;
		for (int prime:calculator.primes) result+=calculator.countCombinations(prime);
		result%=calculator.mod;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
