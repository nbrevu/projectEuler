package com.euler;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

public class Euler793 {
	private final static int N=27;
	
	private static class Info implements Comparable<Info>	{
		public final long number;
		public final int index1;
		public final int index2;
		public Info(long number,int index1,int index2)	{
			this.number=number;
			this.index1=index1;
			this.index2=index2;
		}
		@Override
		public int compareTo(Info other)	{
			return Long.compare(number,other.number);
		}
	}
	
	public static void main(String[] args)	{
		long[] original=new long[N];
		original[0]=290797;
		for (int i=1;i<N;++i) original[i]=(original[i-1]*original[i-1])%50515093l;
		for (int k=3;k<=N;k+=4)	{
			long[] numbers=Arrays.copyOf(original,k);
			Arrays.sort(numbers);
			SortedSet<Info> products=new TreeSet<>();
			for (int i=0;i<numbers.length;++i) for (int j=i+1;j<numbers.length;++j) products.add(new Info(numbers[i]*numbers[j],i,j));
			Info[] prodArray=products.toArray(Info[]::new);
			Info result=prodArray[(prodArray.length-1)/2];
			System.out.println(String.format("N=%d: %dx%d -> %d.",k,result.index1,result.index2,result.number));
			if (k==15)	{
				System.out.println("INFORMACIÓN INTERESANTE ACERCA DE LA LLAMA.");
				for (Info i:prodArray) System.out.println(String.format("\t%dx%d -> %d.",i.index1,i.index2,i.number));
			}
		}
	}
}
