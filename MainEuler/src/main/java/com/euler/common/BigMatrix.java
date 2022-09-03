package com.euler.common;

import java.math.BigInteger;

public class BigMatrix {
	private BigInteger[][] data;
	private BigMatrix(int size,boolean doInitialize)	{
		data=new BigInteger[size][size];
		if (doInitialize) for (int i=0;i<size;++i) for (int j=0;j<size;++j) data[i][j]=BigInteger.ZERO;
	}
	public static BigMatrix eye(int size)	{
		BigMatrix res=new BigMatrix(size);
		for (int i=0;i<size;++i) res.data[i][i]=BigInteger.ONE;
		return res;
	}
	public BigMatrix(int size)	{
		data=new BigInteger[size][size];
		for (int i=0;i<size;++i) for (int j=0;j<size;++j) data[i][j]=BigInteger.ZERO;
	}
	public BigInteger get(int i,int j)	{
		return data[i][j];
	}
	public void assign(int i,int j,BigInteger N)	{
		data[i][j]=N;
	}
	public BigMatrix add(BigMatrix other)	{
		int L=data.length;
		if (L!=other.data.length) throw new IllegalArgumentException();
		BigMatrix res=new BigMatrix(L,false);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) res.data[i][j]=get(i,j).add(other.get(i,j));
		return res;
	}
	public BigMatrix multiply(BigMatrix other)	{
		int L=data.length;
		if (L!=other.data.length) throw new IllegalArgumentException();
		BigMatrix res=new BigMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) for (int k=0;k<L;++k) res.data[i][j]=res.data[i][j].add(get(i,k).multiply(other.get(k,j)));
		return res;
	}
	public BigMatrix multiply(BigMatrix other,BigInteger mod)	{
		int L=data.length;
		if (L!=other.data.length) throw new IllegalArgumentException();
		BigMatrix res=new BigMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j)	{
			for (int k=0;k<L;++k) res.data[i][j]=res.data[i][j].add(get(i,k).multiply(other.get(k,j)));
			res.data[i][j]=res.data[i][j].mod(mod);
		}
		return res;
	}
	public BigMatrix pow(int exp)	{
		int L=data.length;
		BigMatrix prod=eye(L);
		BigMatrix curProd=this;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(curProd);
			curProd=curProd.multiply(curProd);
			exp/=2;
		}
		return prod;
	}
	public BigMatrix pow(long exp,BigInteger mod)	{
		int L=data.length;
		BigMatrix prod=eye(L);
		BigMatrix curProd=this;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(curProd,mod);
			curProd=curProd.multiply(curProd,mod);
			exp/=2;
		}
		return prod;
	}
	public int size()	{
		return data.length;
	}
}
