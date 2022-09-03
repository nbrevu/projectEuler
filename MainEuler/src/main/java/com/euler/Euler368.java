package com.euler;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import com.euler.common.EulerUtils.CombinatorialNumberCache;

public class Euler368 {
	private final static int MAX_POWER=50;
	private final static int MAX_PRECISION=100;
	private final static MathContext CONTEXT=new MathContext(MAX_PRECISION,RoundingMode.HALF_UP);
	
	public static void main(String[] args)	{
		BigDecimal[][] c=new BigDecimal[1+MAX_POWER][1+MAX_POWER];
		CombinatorialNumberCache combis=new CombinatorialNumberCache(2*MAX_POWER);
		for (int k=1;k<=MAX_POWER;++k) for (int w=0;w<=MAX_POWER;++w)	{
			long combi=combis.get(k+w-1,w);
			c[k][w]=BigDecimal.valueOf(((w%2)==1)?(-combi):combi);
		}
		BigDecimal[][] powers=new BigDecimal[10][1+MAX_POWER];
		for (int i=0;i<10;++i)	{
			BigDecimal n=BigDecimal.valueOf(i);
			powers[i][0]=BigDecimal.ONE;
			powers[i][1]=n;
			for (int j=2;j<=MAX_POWER;++j) powers[i][j]=n.multiply(powers[i][j-1]);
		}
		BigDecimal[] negTenPowers=new BigDecimal[2*(1+MAX_POWER)];
		negTenPowers[0]=BigDecimal.ONE;
		BigDecimal tenth=BigDecimal.ONE.divide(BigDecimal.TEN,CONTEXT);
		for (int i=1;i<negTenPowers.length;++i) negTenPowers[i]=tenth.multiply(negTenPowers[i-1],CONTEXT);
		BigDecimal[][][] a=new BigDecimal[1+MAX_POWER][1+MAX_POWER][10];
		for (int k=1;k<=MAX_POWER;++k) for (int w=0;w<=MAX_POWER;++w) for (int m=0;m<10;++m)	{
			a[k][w][m]=negTenPowers[k+w].multiply(c[k][w],CONTEXT).multiply(powers[m][w]);
			System.out.println(String.format("a[%d][%d][%d]=%s.",k,w,m,a[k][w][m].toString()));
		}
	}
}
