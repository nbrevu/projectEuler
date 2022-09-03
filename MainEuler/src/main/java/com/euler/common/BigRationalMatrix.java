package com.euler.common;

import java.util.Arrays;

public class BigRationalMatrix {
	private BigRational[][] data;
	private final int L;
	public static BigRationalMatrix eye(int size)	{
		BigRationalMatrix res=new BigRationalMatrix(size);
		for (int i=0;i<size;++i) res.data[i][i]=BigRational.ONE;
		return res;
	}
	public BigRationalMatrix(int size)	{
		data=new BigRational[size][size];
		for (BigRational[] row:data) Arrays.fill(row,BigRational.ZERO);
		L=size;
	}
	public int size()	{
		return data.length;
	}
	public BigRational get(int i,int j)	{
		return data[i][j];
	}
	public void assign(int i,int j,BigRational x)	{
		data[i][j]=x;
	}
	public BigRationalMatrix add(BigRationalMatrix other)	{
		if (L!=other.data.length) throw new IllegalArgumentException();
		BigRationalMatrix res=new BigRationalMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) res.data[i][j]=data[i][j].sum(other.data[i][j]);
		return res;
	}
	public BigRationalMatrix subtract(BigRationalMatrix other)	{
		if (L!=other.data.length) throw new IllegalArgumentException();
		BigRationalMatrix res=new BigRationalMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) res.data[i][j]=data[i][j].subtract(other.data[i][j]);
		return res;
	}
	public BigRationalMatrix multiply(BigRationalMatrix other)	{
		if (L!=other.data.length) throw new IllegalArgumentException();
		BigRationalMatrix res=new BigRationalMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) for (int k=0;k<L;++k) res.data[i][j]=res.data[i][j].sum(data[i][k].multiply(other.data[k][j]));
		return res;
	}
	public BigRationalMatrix pow(long exp)	{
		BigRationalMatrix prod=eye(L);
		BigRationalMatrix curProd=this;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(curProd);
			curProd=curProd.multiply(curProd);
			exp/=2;
		}
		return prod;
	}
	private static int findPivot(BigRationalMatrix in,int row)	{
		int L=in.L;
		for (int i=row;i<L;++i) if (!in.data[i][row].equals(BigRational.ZERO)) return i;
		throw new RuntimeException("Singular matrix.");
	}
	private static void exchange(BigRationalMatrix m1,int row1,int row2)	{
		BigRational[] swap=m1.data[row1];
		m1.data[row1]=m1.data[row2];
		m1.data[row2]=swap;
	}
	public static BigRationalMatrix destructiveInverse(BigRationalMatrix in)	{
		BigRationalMatrix result=new BigRationalMatrix(in.size());
		destructiveInverseWithStorage(in,result);
		return result;
	}
	public static void destructiveInverseWithStorage(BigRationalMatrix in,BigRationalMatrix result)	{
		int L=in.L;
		for (int i=0;i<L;++i)	{
			Arrays.fill(result.data[i],BigRational.ZERO);
			result.data[i][i]=BigRational.ONE;
		}
		for (int i=0;i<L;++i)	{
			int pivot=findPivot(in,i);
			if (i!=pivot)	{
				exchange(in,i,pivot);
				exchange(result,i,pivot);
			}
			BigRational p=in.data[i][i];
			if (!p.equals(BigRational.ONE))	{
				in.data[i][i]=BigRational.ONE;
				for (int j=i+1;j<L;++j) in.data[i][j]=in.data[i][j].divide(p);
				for (int j=0;j<L;++j) result.data[i][j]=result.data[i][j].divide(p);
			}
			for (int k=i+1;k<L;++k)	{
				p=in.data[k][i];
				if (!p.equals(BigRational.ZERO))	{
					in.data[k][i]=BigRational.ZERO;
					for (int j=i+1;j<L;++j) in.data[k][j]=in.data[k][j].subtract(p.multiply(in.data[i][j]));
					for (int j=0;j<L;++j) result.data[k][j]=result.data[k][j].subtract(p.multiply(result.data[i][j]));
				}
			}
		}
		for (int i=L-1;i>0;--i) for (int k=i-1;k>=0;--k)	{
			BigRational p=in.data[k][i];
			if (!p.equals(BigRational.ZERO))	{
				in.data[k][i]=BigRational.ZERO;
				for (int j=0;j<L;++j) result.data[k][j]=result.data[k][j].subtract(p.multiply(result.data[i][j]));
			}
		}
	}
	public void multiplyWithStorage(BigRational[] vector,BigRational[] result)	{
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) result[i]=result[i].sum(data[i][j].multiply(vector[j]));
	}
	public BigRational[] multiply(BigRational[] vector)	{
		BigRational[] result=new BigRational[L];
		Arrays.fill(result,BigRational.ZERO);
		multiplyWithStorage(vector,result);
		return result;
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
