package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.euler.common.Rational;

public class Euler785_2 {
	private static class Tuple	{
		public final int x;
		public final int y;
		public final int z;
		public Tuple(int x,int y,int z)	{
			this.x=x;
			this.y=y;
			this.z=z;
		}
		@Override
		public String toString()	{
			return String.format("(%d,%d,%d)",x,y,z);
		}
	}
	
	// Valid residues modulo 34: [0, 1, 2, 4, 8, 9, 13, 15, 16, 17, 18, 19, 21, 25, 26, 30, 32, 33].
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SortedMap<Integer,List<Tuple>> results=new TreeMap<>();
		int N=10000;
		long sum1=0;
		long sum2=0;
		for (int x=1;x<=N;++x)	{
			long xx=x*x;
			for (int y=x;y<=N;++y)	{
				if (((x%2)==0)&&((y%2)==0)) continue;
				long yy=y*y;
				long sxy=x+y;
				long pxy=x*y;
				int zInitial=y;
				while (((sxy+zInitial)%8)!=0) ++zInitial;
				for (int z=zInitial;z<=N;z+=8) if (EulerUtils.areCoprime(x,y,z))	{
					long zz=z*z;
					long a=xx+yy+zz;
					long b=pxy+z*sxy;
					if (15*a==34*b)	{
						int q=(x+y+z)/8;
						results.computeIfAbsent(q,(Integer unused)->new ArrayList<>()).add(new Tuple(x,y,z));
					}
				}
			}
		}
		int[] primes=Primes.lastPrimeSieve(N);
		for (Map.Entry<Integer,List<Tuple>> entry:results.entrySet())	{
			int q=entry.getKey();
			sum1+=8*q*entry.getValue().size();
			DivisorHolder divs=DivisorHolder.getFromFirstPrimes(q,primes);
			System.out.println(String.format("For %d=%s I have these solutions: %s.",q,divs.toString(),entry.getValue().toString()));
			for (Tuple t:entry.getValue())	{
				sum2+=t.x+t.y+t.z;
				System.out.println(String.format("\t(%d,%d,%d)==(%d,%d,%d) (mod 3).",t.x,t.y,t.z,t.x%3,t.y%3,t.z%3));
				Rational rat=new Rational(t.z,q);
				System.out.println(String.format("\tz/q=%s=%f.",rat.toString(),t.z/((double)q)));
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(sum1);
		System.out.println(sum2);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
