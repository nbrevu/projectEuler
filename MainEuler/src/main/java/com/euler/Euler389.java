package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class Euler389	{
	private final static MathContext CONTEXT=new MathContext(50);
	private static BigInteger pow(long base,int exp)	{
		BigInteger curProd=BigInteger.valueOf(base);
		BigInteger prod=BigInteger.ONE;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(curProd);
			curProd=curProd.multiply(curProd);
			exp=exp/2;
		}
		return prod;
	}
	private static BigInteger[][] simulate(int dieLength,int rolls)	{
		BigInteger[][] res=new BigInteger[rolls+1][];
		for (int i=0;i<=rolls;++i)	{
			res[i]=new BigInteger[(i*dieLength)+1];
			for (int j=0;j<=i*dieLength;++j) res[i][j]=BigInteger.ZERO;
		}
		res[0][0]=BigInteger.ONE;
		for (int i=1;i<=rolls;++i)	{
			System.out.println("\t"+i+"...");
			int N=res[i].length-1;
			int Nprev=res[i-1].length;
			for (int j=1;j<=N;++j) for (int k=1;k<=dieLength;++k)	{
				int pastIndex=j-k;
				if ((pastIndex>=0)&&(pastIndex<Nprev)) res[i][j]=res[i][j].add(res[i-1][pastIndex]);
			}
		}
		return res;
	}
	private static BigDecimal[] partialSimulation(int dieLength,BigDecimal[] previous)	{
		int N=previous.length-1;
		BigDecimal[] res=new BigDecimal[(N*dieLength)+1];
		for (int i=0;i<res.length;++i) res[i]=BigDecimal.ZERO;
		BigInteger[][] dice=simulate(dieLength,N);
		for (int i=1;i<=N;++i)	{
			BigDecimal denominator=new BigDecimal(pow(dieLength,i));
			for (int j=0;j<dice[i].length;++j)	{
				BigDecimal factor=new BigDecimal(dice[i][j]).divide(denominator, CONTEXT); 
				res[j]=res[j].add(previous[i].multiply(factor));
			}
		}
		return res;
	}
	private static BigDecimal[] fullSimulation()	{
		BigDecimal[] ways4=new BigDecimal[5];
		ways4[0]=BigDecimal.ZERO;
		BigDecimal fourth=BigDecimal.ONE.divide(BigDecimal.valueOf(4),CONTEXT);
		for (int i=1;i<=4;++i) ways4[i]=fourth;
		System.out.println("Tetrahedron ready ("+(ways4.length-1)+" cases)...");
		BigDecimal[] ways6=partialSimulation(6,ways4);
		System.out.println("Hexahedron ready ("+(ways6.length-1)+" cases)...");
		BigDecimal[] ways8=partialSimulation(8,ways6);
		System.out.println("Octahedron ready ("+(ways8.length-1)+" cases)...");
		BigDecimal[] ways12=partialSimulation(12,ways8);
		System.out.println("Dodecahedron ready ("+(ways12.length-1)+" cases)...");
		BigDecimal[] ways20=partialSimulation(20,ways12);
		System.out.println("Icosahedron ready ("+(ways20.length-1)+" cases)...");
		return ways20;
	}
	private static BigDecimal getVariance(BigDecimal[] ways)	{
		BigDecimal sum=BigDecimal.ZERO;
		BigDecimal squareSum=BigDecimal.ZERO;
		for (int i=1;i<ways.length;++i)	{
			BigDecimal biI=BigDecimal.valueOf(i);
			sum=sum.add(ways[i].multiply(biI));
			squareSum=squareSum.add(ways[i].multiply(ways[i]).multiply(biI));
		}
		BigDecimal N=BigDecimal.valueOf(ways.length);
		sum=sum.divide(N,CONTEXT);
		squareSum.divide(N,CONTEXT);
		return squareSum.subtract(sum.multiply(sum));
	}
	public static void main(String[] args)	{
		BigDecimal[] simulation=fullSimulation();
		BigDecimal variance=getVariance(simulation);
		double res=variance.doubleValue();
		System.out.println(res);
	}
}

/*
public class Euler389 {
	// This causes an Out Of Memory.
	// Solution: use 1000000-digit BigDecimals instead of Fractions.  
	private static BigInteger pow(long base,int exp)	{
		BigInteger curProd=BigInteger.valueOf(base);
		BigInteger prod=BigInteger.ONE;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(curProd);
			curProd=curProd.multiply(curProd);
			exp=exp/2;
		}
		return prod;
	}
	private static BigInteger[][] simulate(int dieLength,int rolls)	{
		BigInteger[][] res=new BigInteger[rolls+1][];
		for (int i=0;i<=rolls;++i)	{
			res[i]=new BigInteger[(i*dieLength)+1];
			for (int j=0;j<=i*dieLength;++j) res[i][j]=BigInteger.ZERO;
		}
		res[0][0]=BigInteger.ONE;
		for (int i=1;i<=rolls;++i)	{
			int N=res[i].length-1;
			int Nprev=res[i-1].length;
			for (int j=1;j<=N;++j) for (int k=1;k<=dieLength;++k)	{
				int pastIndex=j-k;
				if ((pastIndex>=0)&&(pastIndex<Nprev)) res[i][j]=res[i][j].add(res[i-1][pastIndex]);
			}
		}
		return res;
	}
	
	private static Fraction[] partialSimulation(int dieLength,Fraction[] previous)	{
		int N=previous.length-1;
		Fraction[] res=new Fraction[(N*dieLength)+1];
		for (int i=0;i<res.length;++i) res[i]=new Fraction();
		BigInteger[][] dice=simulate(dieLength,N);
		for (int i=1;i<=N;++i)	{
			BigInteger denominator=pow(dieLength,i);
			for (int j=0;j<dice[i].length;++j) res[j]=res[j].add(previous[i].multiply(new Fraction(dice[i][j],denominator)));
		}
		return res;
	}
	
	private static Fraction[] fullSimulation()	{
		Fraction[] ways4=new Fraction[5];
		ways4[0]=new Fraction();
		Fraction fourth=new Fraction(BigInteger.ONE,BigInteger.valueOf(4));
		for (int i=1;i<=4;++i) ways4[i]=fourth;
		System.out.println("Tetrahedron ready ("+(ways4.length-1)+" cases)...");
		Fraction[] ways6=partialSimulation(6,ways4);
		System.out.println("Hexahedron ready ("+(ways6.length-1)+" cases)...");
		Fraction[] ways8=partialSimulation(8,ways6);
		System.out.println("Octahedron ready ("+(ways8.length-1)+" cases)...");
		Fraction[] ways12=partialSimulation(12,ways8);
		System.out.println("Dodecahedron ready ("+(ways12.length-1)+" cases)...");
		Fraction[] ways20=partialSimulation(20,ways12);
		System.out.println("Icosahedron ready ("+(ways20.length-1)+" cases)...");
		return ways20;
	}
	
	private static Fraction getVariance(Fraction[] ways)	{
		Fraction sum=new Fraction();
		Fraction squareSum=new Fraction();
		for (int i=1;i<ways.length;++i)	{
			BigInteger biI=BigInteger.valueOf(i);
			sum=sum.add(ways[i].multiply(biI));
			squareSum=squareSum.add(ways[i].multiply(ways[i]).multiply(biI));
		}
		BigInteger N=BigInteger.valueOf(ways.length);
		sum=sum.divide(N);
		squareSum.divide(N);
		return squareSum.subtract(sum.multiply(sum));
	}
	
	public static void main(String[] args)	{
		Fraction[] simulation=fullSimulation();
		Fraction variance=getVariance(simulation);
		BigDecimal bdN=new BigDecimal(variance.numerator);
		BigDecimal bdD=new BigDecimal(variance.denominator);
		double res=bdN.divide(bdD,new MathContext(100)).doubleValue();
		System.out.println(res);
	}
}
*/