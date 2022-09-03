package com.euler;

import java.util.TreeSet;

import com.google.common.math.LongMath;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler718_3 {
	private final static int P=4;
	
	private final static long P1=LongMath.pow(17,P);
	private final static long P2=LongMath.pow(19,P);
	private final static long P3=LongMath.pow(23,P);
	// Frobenius numbers calculated using Mathematica.
	private final static long[] FROBENIUS_ARRAY=new long[] {176l, 18422l, 1376626l, 110608186l, 9774616654l, 869836853153l};
	private final static long FROBENIUS_NUMBER=FROBENIUS_ARRAY[P-1];
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		LongSet found=HashLongSets.newMutableSet();
		LongSet repeated=HashLongSets.newMutableSet();
		int reRepeated=0;	// Not 0 :'(.
		for (long p=P1;p<=FROBENIUS_NUMBER;p+=P1) for (long q=p+P2;q<=FROBENIUS_NUMBER;q+=P2) for (long r=q+P3;r<=FROBENIUS_NUMBER;r+=P3)	{
			if (!found.add(r)) if (!repeated.add(r)) ++reRepeated;
			if (r==4789l) System.out.println(p+"->"+q+"->"+r+".");
		}
		long sum=0l;
		for (long l=1;l<=FROBENIUS_NUMBER;++l) if (!found.contains(l)) sum+=l;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(sum);
		System.out.println("Found: "+found.size()+".");
		System.out.println("Repeated: "+repeated.size()+".");
		System.out.println("Repeated more than once: "+reRepeated+".");
		System.out.println("Smallest repeated number: "+(new TreeSet<>(repeated).first())+".");
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
