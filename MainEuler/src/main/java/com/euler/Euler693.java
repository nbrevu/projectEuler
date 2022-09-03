package com.euler;

import java.util.ArrayList;
import java.util.List;

public class Euler693 {
	private static class Sequencer	{
		private final long x;
		private final long y;
		public Sequencer(long x,long y)	{
			this.x=x;
			this.y=y;
		}
		public List<Long> getSequence()	{
			List<Long> result=new ArrayList<>();
			long n=y;
			long mod=x;
			for (;;)	{
				result.add(n);
				if (n<=1) break;
				n=(n*n)%mod;
				++mod;
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		int result=0;
		for (int x=3;x<=100;++x)	{
			for (int y=2;y<x;++y)	{
				List<Long> seq=new Sequencer(x,y).getSequence();
				int len=seq.size();
				int stopper=x+len-2;
				System.out.println(String.format("f(%d,%d)=%d [stopper=%d] -> %s.",x,y,len,stopper,seq.toString()));
				result=Math.max(result,len);
			}
			System.out.println();
		}
		System.out.println(result);
	}
}
