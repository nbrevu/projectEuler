package com.euler.common;

import java.util.Arrays;

public class DoubleMatrix {
	private final static double EPS=1e-7;
	
	private double[][] data;
	private final int L;
	public static DoubleMatrix eye(int size)	{
		DoubleMatrix res=new DoubleMatrix(size);
		for (int i=0;i<size;++i) res.data[i][i]=1.0;
		return res;
	}
	public DoubleMatrix(int size)	{
		data=new double[size][size];
		L=size;
	}
	public int size()	{
		return data.length;
	}
	public double get(int i,int j)	{
		return data[i][j];
	}
	public void assign(int i,int j,double N)	{
		data[i][j]=N;
	}
	public void add(int i,int j,double N)	{
		data[i][j]+=N;
	}
	public DoubleMatrix add(DoubleMatrix other)	{
		if (L!=other.data.length) throw new IllegalArgumentException();
		DoubleMatrix res=new DoubleMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) res.data[i][j]=get(i,j)+(other.get(i,j));
		return res;
	}
	public DoubleMatrix subtract(DoubleMatrix other)	{
		if (L!=other.data.length) throw new IllegalArgumentException();
		DoubleMatrix res=new DoubleMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) res.data[i][j]=get(i,j)-(other.get(i,j));
		return res;
	}
	public DoubleMatrix multiply(DoubleMatrix other)	{
		if (L!=other.data.length) throw new IllegalArgumentException();
		DoubleMatrix res=new DoubleMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) for (int k=0;k<L;++k) res.data[i][j]+=(get(i,k)*other.get(k,j));
		return res;
	}
	public DoubleMatrix pow(long exp)	{
		DoubleMatrix prod=eye(L);
		DoubleMatrix curProd=this;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(curProd);
			curProd=curProd.multiply(curProd);
			exp/=2;
		}
		return prod;
	}
	private static int findPivot(DoubleMatrix in,int row)	{
		int L=in.L;
		for (int i=row;i<L;++i) if (Math.abs(in.data[i][row])>EPS) return i;
		throw new RuntimeException("Singular matrix.");
	}
	private static void exchange(DoubleMatrix m1,int row1,int row2)	{
		double[] swap=m1.data[row1];
		m1.data[row1]=m1.data[row2];
		m1.data[row2]=swap;
	}
	public static DoubleMatrix destructiveInverse(DoubleMatrix in)	{
		DoubleMatrix result=new DoubleMatrix(in.size());
		destructiveInverseWithStorage(in,result);
		return result;
	}
	public static void destructiveInverseWithStorage(DoubleMatrix in,DoubleMatrix result)	{
		int L=in.L;
		for (int i=0;i<L;++i)	{
			Arrays.fill(result.data[i],0d);
			result.data[i][i]=1d;
		}
		for (int i=0;i<L;++i)	{
			int pivot=findPivot(in,i);
			if (i!=pivot)	{
				exchange(in,i,pivot);
				exchange(result,i,pivot);
			}
			double p=in.data[i][i];
			if (Math.abs(p-1)>EPS)	{
				in.data[i][i]=1d;
				for (int j=i+1;j<L;++j) in.data[i][j]/=p;
				for (int j=0;j<L;++j) result.data[i][j]/=p;
			}
			for (int k=i+1;k<L;++k)	{
				p=in.data[k][i];
				if (Math.abs(p)>EPS)	{
					in.data[k][i]=0d;
					for (int j=i+1;j<L;++j) in.data[k][j]-=p*in.data[i][j];
					for (int j=0;j<L;++j) result.data[k][j]-=p*result.data[i][j];
				}
			}
		}
		for (int i=L-1;i>0;--i) for (int k=i-1;k>=0;--k)	{
			double p=in.data[k][i];
			if (Math.abs(p)>EPS)	{
				in.data[k][i]=0;
				for (int j=0;j<L;++j) result.data[k][j]-=p*result.data[i][j];
			}
		}
	}
	public void multiplyWithStorage(double[] vector,double[] result)	{
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) result[i]+=data[i][j]*vector[j];
	}
	public double[] multiply(double[] vector)	{
		double[] result=new double[L];
		multiplyWithStorage(vector,result);
		return result;
	}
	public double[] row(int i)	{
		return data[i];	// Use at your own risk.
	}
	@Override
	public String toString()	{
		StringBuilder sb=new StringBuilder();
		for (int i=0;i<data.length;++i)	{
			boolean first=true;
			for (int j=0;j<data[i].length;++j)	{
				if (first) first=false;
				else sb.append(' ');
				sb.append(data[i][j]);
			}
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
}
