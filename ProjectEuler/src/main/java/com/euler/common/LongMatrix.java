package com.euler.common;

public class LongMatrix {
	private long[][] data;
	public static LongMatrix eye(int size)	{
		LongMatrix res=new LongMatrix(size);
		for (int i=0;i<size;++i) res.data[i][i]=1l;
		return res;
	}
	public LongMatrix(int size)	{
		data=new long[size][size];
	}
	public LongMatrix(LongMatrix copy)	{
		int size=copy.data.length;
		data=new long[size][size];
		for (int i=0;i<size;++i) for (int j=0;j<size;++j) data[i][j]=copy.data[i][j];
	}
	public long get(int i,int j)	{
		return data[i][j];
	}
	public void assign(int i,int j,long N)	{
		data[i][j]=N;
	}
	public LongMatrix add(LongMatrix other)	{
		int L=data.length;
		if (L!=other.data.length) throw new IllegalArgumentException();
		LongMatrix res=new LongMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) res.data[i][j]=get(i,j)+(other.get(i,j));
		return res;
	}
	public LongMatrix multiply(LongMatrix other)	{
		int L=data.length;
		if (L!=other.data.length) throw new IllegalArgumentException();
		LongMatrix res=new LongMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j) for (int k=0;k<L;++k) res.data[i][j]+=(get(i,k)*other.get(k,j));
		return res;
	}
	public LongMatrix multiply(LongMatrix other,long mod)	{
		int L=data.length;
		if (L!=other.data.length) throw new IllegalArgumentException();
		LongMatrix res=new LongMatrix(L);
		for (int i=0;i<L;++i) for (int j=0;j<L;++j)	{
			for (int k=0;k<L;++k) res.data[i][j]+=(get(i,k)*other.get(k,j));
			res.data[i][j]%=mod;
		}
		return res;
	}
	public LongMatrix pow(long exp)	{
		int L=data.length;
		LongMatrix prod=eye(L);
		LongMatrix curProd=this;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(curProd);
			curProd=curProd.multiply(curProd);
			exp/=2;
		}
		return prod;
	}
	public LongMatrix pow(long exp,long mod)	{
		int L=data.length;
		LongMatrix prod=eye(L);
		LongMatrix curProd=this;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(curProd,mod);
			curProd=curProd.multiply(curProd,mod);
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
				else sb.append(',');
				sb.append(data[i][j]);
			}
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
}
