package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.math.LongMath;

public class Euler617 {
	private static class Sequencer	{
		private final long n;
		private final int e;
		public Sequencer(long n,int e)	{
			this.n=n;
			this.e=e;
		}
		private long next(long current)	{
			long a1=LongMath.pow(current,e);
			return Math.min(a1,n-a1);
		}
		private List<Long> getSequence(long a1)	{
			List<Long> result=new ArrayList<>();
			result.add(a1);
			long prev=a1;
			for (;;)	{
				long curr=next(prev);
				if (curr<=1) return null;
				else if (result.contains(curr)) return result;
				else	{
					result.add(curr);
					prev=curr;
				}
			}
		}
	}
	
	private static <T> String toString(List<T> list)	{
		return '['+Joiner.on(',').join(Lists.transform(list,((Object obj)->obj.toString())))+']';
	}
	
	public static void main(String[] args)	{
		/*
		int counter=0;
		for (long n=4098;n<=4200;++n) for (int e=2;e<=12;++e)	{
			Sequencer sequencer=new Sequencer(n,e);
			for (long a1=2;a1<=n;++a1)	{
				List<Long> sequence=sequencer.getSequence(a1);
				if (sequence!=null)	{
					System.out.println("Das Ding!!!!! n="+n+", e="+e+", a1="+a1+": "+toString(sequence)+".");
					++counter;
				}
			}
		}
		System.out.println("Ich habe "+counter+" Dinge gefunden!!!!!");
		*/
		int k=36;
		for (int m=1;m<k;++m)	{
			long n=LongMath.pow(2l,k)+LongMath.pow(2l,m);
			int counter=0;
			for (int e=2;e<=k;++e)	{
				Sequencer sequencer=new Sequencer(n,e);
				for (int r=1;r<k;++r)	{
					long a1=LongMath.pow(2l,r);
					if (sequencer.getSequence(a1)!=null)	{
						// System.out.println("Das Ding!!!!! n="+n+", e="+e+", a1="+a1+".");
						++counter;
					}
				}
			}
			if (counter!=0) System.out.println("k="+k+", m="+m+": "+counter+" cosas.");
		}
	}
}
