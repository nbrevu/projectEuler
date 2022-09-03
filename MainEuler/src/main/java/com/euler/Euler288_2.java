package com.euler;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Iterator;

import com.google.common.math.LongMath;

public class Euler288_2 {
	// "Number 629527 appeared in position 1, but also in position 6308949. Therefore, there is a cycle of length 6308948."
	private final static long K1=290797;
	private final static long K2=50515093;
	private final static long PRIME=61;
	private final static long N=10000000;
	private final static long MOD=LongMath.pow(3,20);
	
	private static class NumberGenerator implements Iterator<Long>	{
		private final long k1;
		private final long k2;
		public long n;
		public NumberGenerator(long k1,long k2)	{
			this.k1=k1;
			this.k2=k2;
			n=k1;
		}
		@Override
		public Long next()	{
			long result=n;
			n=(n*n)%k2;
			return result;
		}
		@Override
		public boolean hasNext()	{
			return true;
		}
	}
	
	public static void main(String[] args)	{
		BigInteger factor=BigInteger.ONE;
		BigInteger bigPrime=BigInteger.valueOf(PRIME);
		BigInteger sum=BigInteger.ZERO;
		NumberGenerator gen=new NumberGenerator(K1,K2);
		long tic=System.nanoTime();
		for (int i=0;i<=N;++i)	{
			if ((i%10000)==0) System.out.println(i+"...");
			sum=sum.add(factor.multiply(BigInteger.valueOf(gen.next())));
			factor=factor.multiply(bigPrime);
		}
		long tac=System.nanoTime();
		try (PrintStream ps=new PrintStream("C:\\out288.txt"))	{
			ps.println("N("+PRIME+","+N+")=");
			ps.println(sum.toString());
			long interval=tac-tic;
			double seconds=((double)interval)/1e9;
			System.out.println("ATIENDE QUÉ GAÑANAZO: generating the number took "+seconds+" seconds!");
		}	catch (IOException exc)	{
			System.out.println("ICH KANN DEN ARCHIVE NICHT ÖFFNEN! ICH HABE VIELE ANGST!");
		}
	}
}
