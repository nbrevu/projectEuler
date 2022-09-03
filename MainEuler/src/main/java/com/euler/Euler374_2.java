package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils.FactorialCache;
import com.google.common.math.IntMath;

public class Euler374_2 {
	private final static FactorialCache FACT_CACHE=new FactorialCache(13);
	
	// Maximum Product Over Partitions Into Distinct Parts - EMIS (Došlic - ‎2005)
	private static class FAndM	{
		public final long f;
		public final int m;
		public FAndM(long f,int m)	{
			this.f=f;
			this.m=m;
		}
	}
	
	private static class TriangularDecomposition	{
		public final int m;
		public final int l;
		private TriangularDecomposition(int m,int l)	{
			this.m=m;
			this.l=l;
		}
		private static TriangularDecomposition getFor(int n)	{
			/*
			 * (m^2+m)/2 = n => m^2+m-2n=0
			 * m = (-1\pm\sqrt(8n+1))/2
			 * Esta función es estrictamente creciente así que nos vale.
			 */
			int m=(IntMath.sqrt(8*n+1,RoundingMode.DOWN)-1)/2;
			int tri=(m*(m+1))/2;
			int l=n-tri;
			if ((l<0)||(l>m)) System.out.println("No, hija, no.");
			return new TriangularDecomposition(m,l);
		}
	}
	
	private static FAndM getFAndM(int in)	{
		if (in==1) return new FAndM(1,1);
		TriangularDecomposition decomp=TriangularDecomposition.getFor(in);
		long f;
		int m;
		if (decomp.l==decomp.m)	{
			f=FACT_CACHE.get(decomp.m+1);
			m=decomp.m;
		}	else if (decomp.l==decomp.m-1)	{
			f=(FACT_CACHE.get(decomp.m)*(decomp.m+2))/2;
			m=decomp.m-1;
		}	else	{
			f=FACT_CACHE.get(decomp.m+1)/(decomp.m-decomp.l);
			m=decomp.m-1;
		}
		return new FAndM(f,m);
	}
	
	public static void main(String[] args)	{
		long result=0;
		for (int i=1;i<=100;++i)	{
			FAndM temporary=getFAndM(i);
			System.out.println("f("+i+")="+temporary.f+", m("+i+")="+temporary.m+" => "+temporary.f*temporary.m);
			result+=temporary.f*temporary.m;
		}
		System.out.println(result);
		System.out.println(result%982451653l);
	}
}
