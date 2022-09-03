package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Euler66 {
	private final static int LIMIT=1000;
	
	private static int getRoot(int base)	{
		return (int)(Math.floor(Math.sqrt((double)base)));
	}
	
	private static List<Integer> getPeriod(int in)	{
		List<Integer> res=new ArrayList<>();
		double dsq=Math.sqrt((double)in);
		int sq=(int)(Math.floor(dsq));
		if (sq*sq==in) throw new IllegalArgumentException();
		int firstA,firstB,a,b;
		a=firstA=1;
		b=firstB=sq;
		do	{
			int diff=(in-b*b)/a;
			double X=(dsq+b)/diff;
			int residue=(int)Math.floor(X);
			res.add(residue);
			a=diff;
			b=diff*residue-b;
		}	while ((a!=firstA)||(b!=firstB));
		return res;
	}
	
	private static BigInteger[] getAConvergent(BigInteger sq,List<Integer> period,int i)	{
		int N=period.size();
		BigInteger outN=BigInteger.valueOf(period.get(i%N));
		BigInteger outD=BigInteger.valueOf(1);
		BigInteger a,tmp;
		for (--i;i>=0;--i)	{
			a=BigInteger.valueOf(period.get(i%N));
			tmp=outN;
			outN=a.multiply(outN).add(outD);
			outD=tmp;
		}
		tmp=outN;
		BigInteger[] res=new BigInteger[2];
		res[0]=sq.multiply(outN).add(outD);
		res[1]=tmp;
		return res;
	}
	
	private static BigInteger[] getConvergents(BigInteger val,int sq,List<Integer> period)	{
		BigInteger bsq=BigInteger.valueOf(sq);
		for (int i=0;;++i)	{
			BigInteger[] res=getAConvergent(bsq,period,i);
			BigInteger lh=res[0].multiply(res[0]);
			BigInteger rh=BigInteger.valueOf(1).add(val.multiply(res[1]).multiply(res[1]));
			if (lh.equals(rh)) return res;
		}
	}
	
	public static void main(String[] args)	{
		int maxI=0;
		BigInteger maxB=BigInteger.valueOf(1);
		for (int i=1;i<=LIMIT;++i)	{
			int sq=getRoot(i);
			if (sq*sq==i) continue;
			List<Integer> period=getPeriod(i);
			BigInteger[] frac=getConvergents(BigInteger.valueOf(i),sq,period);
			System.out.println("Solución para i="+i+": x="+frac[0]+"; y="+frac[1]+".");
			if (frac[0].compareTo(maxB)>0)	{
				maxB=frac[0];
				maxI=i;
			}
		}
		System.out.println(maxI);
	}
}
