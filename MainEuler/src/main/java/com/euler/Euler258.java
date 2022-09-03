package com.euler;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import com.google.common.math.LongMath;

public class Euler258 {
	// VERY slow (N^3 with N=2000!!!). Diagonalize?
	// Update: "Elapsed 8146.774719311001 seconds." Good enough :D.
	private final static long MOD=20092010l;
	private final static long N=LongMath.pow(10l, 18);
	
	private static class Matrix2000	{
		private static Matrix2000 LAGGEDFIBO_BASE=null;
		private static Matrix2000 IDENTITY=null;
		private long[][] data;
		public Matrix2000()	{
			data=new long[2000][2000];
		}
		public static Matrix2000 getLaggedFiboBase()	{
			if (LAGGEDFIBO_BASE!=null) return LAGGEDFIBO_BASE;
			LAGGEDFIBO_BASE=new Matrix2000();
			LAGGEDFIBO_BASE.data[0][1998]=1;
			LAGGEDFIBO_BASE.data[0][1999]=1;
			for (int i=1;i<2000;++i) LAGGEDFIBO_BASE.data[i][i-1]=1;
			return LAGGEDFIBO_BASE;
		}
		public static Matrix2000 getIdentity()	{
			if (IDENTITY!=null) return IDENTITY;
			IDENTITY=new Matrix2000();
			for (int i=0;i<2000;++i) IDENTITY.data[i][i]=1;
			return IDENTITY;
		}
		public Matrix2000 multiplyMod(Matrix2000 other,long mod)	{
			Matrix2000 result=new Matrix2000();
			for (int i=0;i<2000;++i) for (int j=0;j<2000;++j)	{
				long res=0l;
				// for (int k=0;k<2000;++k) res=(res+this.data[i][k]*other.data[k][j])%mod;
				for (int k=0;k<2000;++k) res+=this.data[i][k]*other.data[k][j];
				result.data[i][j]=res%mod;
			}
			return result;
		}
		public Matrix2000 expMod(long exp,long mod)	{
			Matrix2000 result=getIdentity();
			Matrix2000 tmpProduct=this;
			while (exp>0)	{
				if ((exp%2)==1l) result=result.multiplyMod(tmpProduct,mod);
				tmpProduct=tmpProduct.multiplyMod(tmpProduct,mod);
				exp/=2;
			}
			return result;
		}
		public long[] multiplyMod(long[] vector,long mod)	{
			assert vector.length==2000;
			long[] result=new long[2000];
			for (int i=0;i<2000;++i)	{
				long res=0;
				// for (int k=0;k<2000;++k) res=(res+this.data[i][k]*vector[k])%mod;
				for (int k=0;k<2000;++k) res+=this.data[i][k]*vector[k];
				result[i]=res%mod;
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] initial=new long[2000];
		Arrays.fill(initial,1l);
		Matrix2000 base=Matrix2000.getLaggedFiboBase();
		Matrix2000 finalMatrix=base.expMod(N-1999l,MOD);
		long[] result=finalMatrix.multiplyMod(initial,MOD);
		long toc=System.nanoTime();
		double seconds=(toc-tic)*1e-9;
		try (PrintStream ps=new PrintStream("C:\\out258.txt"))	{
			for (int i=0;i<result.length;++i) ps.println(""+i+": "+result[i]+".");
		}	catch (IOException exc)	{
			System.out.println("D'OH!!!!!");
		}
		System.out.println(result[0]);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
