package com.euler;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Euler592 {
	private final static class Generator	{
		private BigInteger f;
		private final static BigInteger SIXTEEN=BigInteger.valueOf(16);
		private final static BigInteger MOD=BigInteger.valueOf(1l<<48);
		public Generator()	{
			f=BigInteger.ONE;
		}
		public long getNext(long l)	{
			BigInteger bl=BigInteger.valueOf(l);
			f=f.multiply(bl);
			for (;;)	{
				BigInteger[] results=f.divideAndRemainder(SIXTEEN);
				if (results[1].equals(BigInteger.ZERO)) f=results[0];
				else break;
			}
			f=f.remainder(MOD);
			return f.longValue();
		}
	}
	
	// This only looks for collisions. Very, very ugly.
	public static void main(String[] args)	{
		Map<Long,Long> cache=new HashMap<>();
		Generator gen=new Generator();
		for (long l=1;;++l)	{
			long f=gen.getNext(l);
			if (cache.containsKey(f)) System.out.println("Match found: "+cache.get(f)+" <=> "+l);
			else cache.put(f,l);
			if ((l%1000000)==0) System.out.println("\t..."+l);
		}
	}
}
