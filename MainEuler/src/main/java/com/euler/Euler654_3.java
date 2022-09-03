package com.euler;

import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.math.LongMath;

public class Euler654_3 {
	/*
	 * Multiplying a 4999x4999 matrix by itself.
	 * Basic method: elapsed 1435.930549652 seconds.
	 * Constant method (making MOD a constant): elapsed 1448.902148652 seconds (!!!).
	 * No mod: elapsed 1458.505763936 seconds. This is a joke :|.
	 * 
	 * Conclusion: be smarter. Use symmetry.
	 */
	private final static int N=5000;
	private final static long M=LongMath.pow(10l,12);
	private final static long MOD=1000000007l;
	
	private static int COUNTER=0;
	
	private static class SymmetricMatrix	{
		private final static Deque<SymmetricMatrix> POOL=new ArrayDeque<>();
		// Only the lower triangle is stored. The lower is preferred because the index match the standard case.
		public final long[][] data;
		private SymmetricMatrix(int N)	{
			data=new long[N][];
			for (int i=0;i<N;++i) data[i]=new long[i+1];
		}
		private static SymmetricMatrix eye(int N)	{
			SymmetricMatrix result=getMatrix(N);
			for (int i=0;i<N;++i) result.data[i][i]=1l;
			return result;
		}
		private void dispose()	{
			POOL.push(this);
		}
		public static SymmetricMatrix getMatrix(int N)	{
			if (POOL.isEmpty()) return new SymmetricMatrix(N);
			else return POOL.pop();
		}
		public SymmetricMatrix pow(long exp,long mod)	{
			int size=data.length;
			SymmetricMatrix prod=eye(size);
			SymmetricMatrix curProd=this;
			while (exp>0)	{
				if ((exp%2)==1)	{
					SymmetricMatrix swap=prod;
					prod=prod.multiply(curProd,mod);
					swap.dispose();
				}
				SymmetricMatrix swap=curProd;
				curProd=curProd.multiply(curProd,mod);
				swap.dispose();
				exp/=2;
			}
			return prod;
		}
		public long getElementSum(long mod)	{
			int size=data.length;
			long result=0;
			for (int i=0;i<size;++i)	{
				for (int j=0;j<i;++j) result+=data[i][j]*2;
				result+=data[i][i];
			}
			return result%mod;
		}
		public SymmetricMatrix multiply(SymmetricMatrix in,long mod)	{
			++COUNTER;
			System.out.println(""+COUNTER+" multiplication(s)...");
			int size=in.data.length;
			SymmetricMatrix result=getMatrix(size);
			for (int i=0;i<size;++i)	{
				// First the normal elements.
				for (int j=0;j<i;++j)	{
					long num=0;
					// for (int k=0;k<N;++k) num+=data[i][k]*data[k][j];
					/*
					 * Three sets:
					 * k<=j<i
					 * j<k<=i
					 * j<i<k
					 */
					int k=0;
					for (;k<=j;++k) num=(num+data[i][k]*in.data[j][k])%mod;
					for (;k<=i;++k) num=(num+data[i][k]*in.data[k][j])%mod;
					for (;k<size;++k) num=(num+data[k][i]*in.data[k][j])%mod;
					result.data[i][j]=num;
				}
				// Finally the diagonal element (i==j).
				long num=0;
				for (int k=0;k<=i;++k) num=(num+data[i][k]*in.data[i][k])%mod;
				for (int k=i+1;k<size;++k) num=(num+data[k][i]*in.data[k][i])%mod;
				result.data[i][i]=num%mod;
			}
			return result;
		}
	}
	
	private static SymmetricMatrix createBaseMatrix(int n)	{
		SymmetricMatrix result=SymmetricMatrix.getMatrix(n-1);
		for (int i=0;i<n-1;++i)	{
			int maxJ=Math.min(i,n-2-i);
			for (int j=0;j<=maxJ;++j) result.data[i][j]=1l;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SymmetricMatrix matrix=createBaseMatrix(N);
		SymmetricMatrix finalMatrix=matrix.pow(M-1,MOD);
		System.out.println(finalMatrix.getElementSum(MOD));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
