package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;
import com.google.common.math.LongMath;

public class Euler368_2 {
	private final static int MAX_POWER=1000;
	private final static int MAX_PRECISION=30;
	
	private static abstract class KempnerSummationScheme	{
		private final int base;
		private final int maxPower;
		private final int numSets;
		// adjacencyGraph must have dimensions numSets*base. Each element must be either -1 or a value in the range [0..numSets-1].
		private final int[][] adjacencyGraph;
		private final MathContext context;
		private final BigDecimal[][][] a;
		// phiA and phiB are used for iterations, using a rail-switching scheme.
		private final BigDecimal[][] phiA;
		private final BigDecimal[][] phiB;
		protected KempnerSummationScheme(int base,int maxPower,int numSets,int precision,int[][] adjacencyGraph)	{
			this.base=base;
			this.maxPower=maxPower;
			this.numSets=numSets;
			this.adjacencyGraph=adjacencyGraph;
			context=new MathContext(precision,RoundingMode.HALF_UP);
			BigCombinatorialNumberCache combis=new BigCombinatorialNumberCache(2*maxPower);
			BigDecimal[][] c=new BigDecimal[1+maxPower][1+maxPower];
			for (int k=1;k<=maxPower;++k) for (int w=0;w<=maxPower;++w)	{
				BigInteger combi=combis.get(k+w-1,w);
				c[k][w]=new BigDecimal(((w%2)==1)?(combi.negate()):combi);
			}
			BigDecimal[][] powers=new BigDecimal[base][1+maxPower];
			for (int i=0;i<base;++i)	{
				BigDecimal n=BigDecimal.valueOf(i);
				powers[i][0]=BigDecimal.ONE;
				powers[i][1]=n;
				for (int j=2;j<=maxPower;++j) powers[i][j]=n.multiply(powers[i][j-1]);
			}
			BigDecimal[] negBasePowers=new BigDecimal[2*(1+maxPower)];
			negBasePowers[0]=BigDecimal.ONE;
			BigDecimal baseInverse=BigDecimal.ONE.divide(BigDecimal.valueOf(base),context);
			for (int i=1;i<negBasePowers.length;++i) negBasePowers[i]=baseInverse.multiply(negBasePowers[i-1],context);
			a=new BigDecimal[1+maxPower][1+maxPower][base];
			for (int k=1;k<=maxPower;++k) for (int w=0;w<=maxPower;++w) for (int m=0;m<base;++m) a[k][w][m]=negBasePowers[k+w].multiply(c[k][w],context).multiply(powers[m][w]);
			phiA=new BigDecimal[numSets][1+maxPower];
			phiB=new BigDecimal[numSets][1+maxPower];
		}
		private void setZero(BigDecimal[][] array)	{
			for (int i=0;i<array.length;++i) Arrays.fill(array[i],BigDecimal.ZERO);	
		}
		// Returns the set this number belongs to, or maybe -1 if it's not to be counted.
		public abstract int getSet(long number);
		public BigDecimal getSum(int initialDigits,int maxDigits)	{
			BigDecimal result=BigDecimal.ZERO;
			long firstLimit=LongMath.pow(base,initialDigits-1);
			long secondLimit=base*firstLimit;
			for (long i=1;i<firstLimit;++i) if (getSet(i)>=0) result=result.add(BigDecimal.ONE.divide(BigDecimal.valueOf(i),context));
			setZero(phiA);
			for (long i=firstLimit;i<secondLimit;++i)	{
				int set=getSet(i);
				if (set<0) continue;
				BigDecimal inverse=BigDecimal.ONE.divide(BigDecimal.valueOf(i),context);
				result=result.add(inverse);
				phiA[set][1]=phiA[set][1].add(inverse);
				BigDecimal power=inverse;
				for (int j=2;j<=MAX_POWER;++j)	{
					power=power.multiply(inverse,context);
					phiA[set][j]=phiA[set][j].add(power,context);
				}
			}
			BigDecimal[][] prev=phiA;
			BigDecimal[][] next=phiB;
			for (int d=initialDigits;d<maxDigits;++d)	{
				System.out.println(d+"... "+result);
				setZero(next);
				for (int l=0;l<numSets;++l) for (int m=0;m<base;++m)	{
					int j=adjacencyGraph[l][m];
					if (j<0) continue;
					for (int k=1;k<=maxPower;++k) for (int w=0;w+k<=maxPower;++w) next[j][k]=next[j][k].add(a[k][w][m].multiply(prev[l][k+w],context),context);
				}
				for (int j=0;j<numSets;++j) result=result.add(next[j][1],context);
				BigDecimal[][] swap=prev;
				prev=next;
				next=swap;
			}
			return result;
		}
	}
	
	// Sample for the standard Kempner series. It works!
	private static class StandardKempnerSeries extends KempnerSummationScheme	{
		private final static int[][] GRAPH=new int[][] {{0,0,0,0,0,0,0,0,0,-1}};
		public StandardKempnerSeries(int maxPower,int precision)	{
			super(10,maxPower,1,precision,GRAPH);
		}
		@Override
		public int getSet(long number) {
			while (number>0)	{
				long digit=number%10;
				if (digit==9) return -1;
				number/=10;
			}
			return 0;
		}
	}
	
	/*
	 * We will have 20 sets. Sets 0 to 9 (included) count the cases where the last digit is m and the second to last digit is not.
	 * Sets 10 to 19 (included) count the cases where the last digit is m-10, and so is the second to last digit.
	 * If we are in any set and we encounter a different digit, we stay on the first sets. If we are in one of the first sets and encounter the
	 * same digit, we move to the second set. If we are in one of the second sets and encounter the same digit, we discard.
	 */
	private static class SpecialKempnerSeries extends KempnerSummationScheme	{
		private final static int[][] GRAPH=generateGraph();
		private static int[][] generateGraph()	{
			int[][] result=new int[20][10];
			for (int i=0;i<10;++i) for (int m=0;m<10;++m)	{
				if (i==m)	{
					result[i][m]=10+m;
					result[10+i][m]=-1;
				}	else	{
					result[i][m]=m;
					result[10+i][m]=m;
				}
			}
			return result;
		}
		public SpecialKempnerSeries(int maxPower,int precision) {
			super(10,maxPower,20,precision,GRAPH);
		}
		@Override
		public int getSet(long number)	{
			long secondToLastDigit=-1;
			long lastDigit=-1;
			while (number>0)	{
				long digit=number%10;
				if ((digit==lastDigit)&&(lastDigit==secondToLastDigit)) return -1;
				secondToLastDigit=lastDigit;
				lastDigit=digit;
				number/=10;
			}
			int result=(int)lastDigit;
			if (secondToLastDigit==lastDigit) result+=10;
			return result;
		}
	}
	
	// Wrong again, but very close. The integer part is right, at least.
	public static void main(String[] args)	{
		System.out.println(new StandardKempnerSeries(MAX_POWER,MAX_PRECISION).getSum(2,10000));
		System.out.println(new SpecialKempnerSeries(MAX_POWER,MAX_PRECISION).getSum(6,10000));
	}
}
