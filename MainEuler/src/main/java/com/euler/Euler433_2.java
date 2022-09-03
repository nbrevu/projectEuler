package com.euler;

import java.util.Arrays;
import java.util.stream.IntStream;

import com.euler.common.EulerUtils;

public class Euler433_2 {
	private static class ZeckendorfRepresentation	{
		public final static ZeckendorfRepresentation ZERO=new ZeckendorfRepresentation(new boolean[0]);
		private final boolean[] data;
		private ZeckendorfRepresentation(boolean[] data)	{
			this.data=data;
		}
		public static ZeckendorfRepresentation singleFibonacci(int index)	{
			boolean[] result=new boolean[1+index];
			result[index]=true;
			return new ZeckendorfRepresentation(result);
		}
		public ZeckendorfRepresentation copyWithAddedBit(int index)	{
			boolean[] result=Arrays.copyOf(data,Math.max(1+index,data.length));
			result[index]=true;
			return new ZeckendorfRepresentation(result);
		}
		public boolean isFree(int index)	{
			return (index<0)||(index>=data.length)||!data[index];
		}
		@Override
		public String toString()	{
			if (data.length<=0) return "0";
			StringBuilder sb=new StringBuilder();
			for (int i=data.length-1;i>=0;--i) sb.append(data[i]?'1':'0');
			return sb.toString();
		}
	}
	
	private static ZeckendorfRepresentation[] getZeckendorfRepresentations(int upTo)	{
		IntStream.Builder fiboBuilder=IntStream.builder();
		int prev=1;
		int curr=2;
		fiboBuilder.accept(prev);
		fiboBuilder.accept(curr);
		for (;;)	{
			int next=prev+curr;
			if (next>upTo) break;
			fiboBuilder.accept(next);
			prev=curr;
			curr=next;
		}
		int[] fibos=fiboBuilder.build().toArray();
		ZeckendorfRepresentation[] result=new ZeckendorfRepresentation[1+upTo];
		result[0]=ZeckendorfRepresentation.ZERO;
		for (int n=1;n<=upTo;++n)	{
			for (int i=0;;++i)	{
				int fibo=fibos[i];
				if (n==fibo)	{
					result[n]=ZeckendorfRepresentation.singleFibonacci(i);
					break;
				}	else if (n<fibo) throw new IllegalStateException("Es funktioniert nicht.");
				ZeckendorfRepresentation prevRep=result[n-fibo];
				if (prevRep.isFree(i-1)&&prevRep.isFree(i)&&prevRep.isFree(i+1))	{
					result[n]=prevRep.copyWithAddedBit(i);
					break;
				}
			}
		}
		return result;
	}
	
	private static int countSteps(int a,int b)	{
		int result=0;
		do	{
			int r=a%b;
			a=b;
			b=r;
			++result;
		}	while (b!=0);
		return result;
	}
	
	public static void main(String[] args)	{
		int N=100;
		ZeckendorfRepresentation[] reps=getZeckendorfRepresentations(100);
		// for (int i=0;i<=100;++i) System.out.println(String.format("i=%d: %s.",i,reps[i]));
		for (int x=1;x<=N;++x)	{
			for (int y=1;y<=x;++y)	{
				int steps=countSteps(x,y);
				int gcd=EulerUtils.gcd(x,y);
				System.out.println(String.format("x=%d (Z=%s), y=%d (Z=%s): %d steps. Gcd=%d (Z=%s).",x,reps[x],y,reps[y],steps,gcd,reps[gcd]));
			}
			System.out.println();
		}
	}
}
