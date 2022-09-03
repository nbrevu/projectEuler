package com.euler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import com.euler.common.EulerUtils.Pair;
import com.google.common.math.LongMath;

public class Euler654_4 {
	private final static int N=5000;
	private final static long M=LongMath.pow(10l,12);
	private final static long MOD=1000000007l;
	private final static String FILE="C:\\tmp654.txt";
	
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
		public SymmetricMatrix pow(long exp,long mod) throws IOException	{
			int size=data.length;
			return continuePow(eye(size),this,exp,mod);
		}
		public static SymmetricMatrix continuePow(SymmetricMatrix prod,SymmetricMatrix curProd,long exp,long mod) throws IOException	{
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
				writeStatus(FILE,prod,curProd,exp);
			}
			return prod;
		}
		private static void writeStatus(String file,SymmetricMatrix prod,SymmetricMatrix curProd,long exp) throws IOException	{
			System.out.println("Writing... remaining exp="+exp+".");
			int size=prod.data.length;
			try (PrintWriter writer=new PrintWriter(FILE))	{
				for (int i=0;i<size;++i) writer.println(Arrays.toString(prod.data[i]));
				for (int i=0;i<size;++i) writer.println(Arrays.toString(curProd.data[i]));
				writer.println(exp);
			}
		}
		private static Pair<Long,Pair<SymmetricMatrix,SymmetricMatrix>> readStatus(int size) throws IOException	{
			try (BufferedReader reader=new BufferedReader(new FileReader(FILE)))	{
				SymmetricMatrix prod=getMatrix(size);
				for (int i=0;i<size;++i) arrayFromString(reader.readLine(),prod.data[i]);
				SymmetricMatrix curProd=getMatrix(size);
				for (int i=0;i<size;++i) arrayFromString(reader.readLine(),curProd.data[i]);
				long exp=Long.parseLong(reader.readLine());
				return new Pair<>(exp,new Pair<>(prod,curProd));
			}
		}
		private static void arrayFromString(String str,long[] target)	{
			String[] components=str.substring(1,str.length()-1).split(", ");
			if (components.length!=target.length) throw new IllegalArgumentException();
			for (int i=0;i<target.length;++i) target[i]=Long.parseLong(components[i]);
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
	
	public static void main(String[] args) throws IOException	{
		SymmetricMatrix finalMatrix;
		try	{
			Pair<Long,Pair<SymmetricMatrix,SymmetricMatrix>> status=SymmetricMatrix.readStatus(N-1);
			long exp=status.first;
			System.out.println("Data read successfully from file! exp="+exp+".");
			SymmetricMatrix prod=status.second.first;
			SymmetricMatrix curProd=status.second.second;
			finalMatrix=SymmetricMatrix.continuePow(prod,curProd,exp,MOD);
		}	catch (IOException exc)	{
			SymmetricMatrix matrix=createBaseMatrix(N);
			finalMatrix=matrix.pow(M-1,MOD);
		}
		System.out.println(finalMatrix.getElementSum(MOD));
	}
}
