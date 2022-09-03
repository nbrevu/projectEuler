package com.euler;

import java.util.List;
import java.util.OptionalLong;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler457_2 {
	private static long LIMIT=LongMath.pow(10l,7);

	private static LongSet getBs(long prime)	{
		long x=(prime+1)/2;	// 2^-1 mod prime.
		long x3=(x*3)%prime;
		long possibleSquare=((x3*x3)+1)%prime;
		LongSet sqRoots=EulerUtils.squareRootModuloPrime(possibleSquare,prime);
		LongSet result=HashLongSets.newMutableSet();
		sqRoots.forEach((long root)->result.add((root+x3)%prime));
		return result;
	}
	
	private static OptionalLong getAFromB(long b,long prime)	{
		long poly=b*b-3*b-1;
		long q=poly/prime;
		if (q==0) return OptionalLong.of(0l);
		long inv=(3-2*b)%prime;
		if (inv==0) return OptionalLong.empty();	// Happens only for 13, apparently. Something to do with Hansel's Lemma.
		if (inv<0) inv+=prime;
		long a=q*EulerUtils.modulusInverse(inv,prime);
		a%=prime;
		if (a<0) a+=prime;
		return OptionalLong.of(a);
	}
	
	private static OptionalLong getSmallestN(long prime)	{
		LongSet bs=getBs(prime);
		OptionalLong result=OptionalLong.empty();
		LongCursor cursor=bs.cursor();
		while (cursor.moveNext())	{
			long b=cursor.elem();
			OptionalLong a=getAFromB(b,prime);
			if (a.isPresent())	{
				long n=a.getAsLong()*prime+b;
				if (result.isEmpty()||(n<result.getAsLong())) result=OptionalLong.of(n);
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		List<Long> cases=Primes.listLongPrimes(LIMIT);
		cases.remove(0);
		long sum=0l;
		for (long prime:cases)	{
			OptionalLong result=getSmallestN(prime);
			if (result.isPresent()) sum+=result.getAsLong();
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
