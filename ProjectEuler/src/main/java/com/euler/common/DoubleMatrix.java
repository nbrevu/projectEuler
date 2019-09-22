package com.euler.common;

public class DoubleMatrix {
	private double[][] data;
	public static DoubleMatrix eye(int size)	{
		DoubleMatrix res=new DoubleMatrix(size);
		for (int i=0;i<size;++i) res.data[i][i]=1.0;
		return res;
	}
	public DoubleMatrix(int size)	{
		data=new double[size][size];
	}
	public double get(int i,int j)	{
		return data[i][j];
	}
	public void assign(int i,int j,double N)	{
		data[i][j]=N;
	}
	public DoubleMatrix add(DoubleMatrix other)	{
		int L=data.length;
		if (L!=other.data.length) throw new IllegalArgumentException();
		DoubleMatrix res=new DoubleMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) res.data[i][j]=get(i,j)+(other.get(i,j));
		return res;
	}
	public DoubleMatrix multiply(DoubleMatrix other)	{
		int L=data.length;
		if (L!=other.data.length) throw new IllegalArgumentException();
		DoubleMatrix res=new DoubleMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) for (int k=0;k<L;++k) res.data[i][j]+=(get(i,k)*other.get(k,j));
		return res;
	}
	public DoubleMatrix pow(long exp)	{
		int L=data.length;
		DoubleMatrix prod=eye(L);
		DoubleMatrix curProd=this;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(curProd);
			curProd=curProd.multiply(curProd);
			exp/=2;
		}
		return prod;
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
