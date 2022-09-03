package com.euler;

import com.google.common.math.LongMath;

public class Euler312 {
	private final static long MOD=LongMath.pow(13l,8);
	private final static long FIRST_MOD=12*LongMath.pow(13l,7);	// phi(13^8)
	private final static long FIRST_LIMIT=10000l;
	
	// There are better ways to do this, but this is fast enough.
	private static class SierpinskiIterator	{
		private final long b2;
		private final long c2;
		private final long mod;
		public SierpinskiIterator(long mod)	{
			b2=2l;
			c2=3l;
			this.mod=mod;
		}
		public long getCycles(long n)	{
			long bn=b2;	// bn: full paths from one corner to another one.
			long cn=c2;	// cn: paths from one corner to another one, without touching the third one.
			for (long i=3;i<n;++i)	{
				long tmp=(2*bn*cn)%mod;
				bn=(bn*tmp)%mod;
				cn=(cn*tmp)%mod;
			}
			long bn2=(bn*bn)%mod;
			return (bn2*bn)%mod;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SierpinskiIterator initialIterator=new SierpinskiIterator(FIRST_MOD);
		SierpinskiIterator finalIterator=new SierpinskiIterator(MOD);
		long result=FIRST_LIMIT;
		result=initialIterator.getCycles(result);
		result=initialIterator.getCycles(result);
		result=finalIterator.getCycles(result);
		long tac=System.nanoTime();
		double seconds=(tac-tic)/1e9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
