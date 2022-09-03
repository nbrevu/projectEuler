package com.euler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Locale;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;
import com.google.common.math.LongMath;

public class Euler368_3 {
	private final static int MAX_POWER=10;
	
	private static abstract class KempnerSummationScheme	{
		private final int base;
		private final int maxPower;
		private final int numSets;
		// adjacencyGraph must have dimensions numSets*base. Each element must be either -1 or a value in the range [0..numSets-1].
		private final int[][] adjacencyGraph;
		private final double[][][] a;
		// phiA and phiB are used for iterations, using a rail-switching scheme.
		private final double[][] phiA;
		private final double[][] phiB;
		protected KempnerSummationScheme(int base,int maxPower,int numSets,int[][] adjacencyGraph)	{
			this.base=base;
			this.maxPower=maxPower;
			this.numSets=numSets;
			this.adjacencyGraph=adjacencyGraph;
			BigCombinatorialNumberCache combis=new BigCombinatorialNumberCache(2*maxPower);
			double[][] c=new double[1+maxPower][1+maxPower];
			for (int k=1;k<=maxPower;++k) for (int w=0;w<=maxPower;++w)	{
				BigInteger combi=combis.get(k+w-1,w);
				c[k][w]=(((w%2)==1)?(combi.negate()):combi).doubleValue();
			}
			double[][] powers=new double[base][1+maxPower];
			for (int i=0;i<base;++i)	{
				powers[i][0]=1;
				powers[i][1]=i;
				for (int j=2;j<=maxPower;++j) powers[i][j]=i*powers[i][j-1];
			}
			double[] negBasePowers=new double[2*(1+maxPower)];
			negBasePowers[0]=1;
			double baseInverse=1d/base;
			for (int i=1;i<negBasePowers.length;++i) negBasePowers[i]=negBasePowers[i-1]*baseInverse;
			a=new double[1+maxPower][1+maxPower][base];
			for (int k=1;k<=maxPower;++k) for (int w=0;w<=maxPower;++w) for (int m=0;m<base;++m) a[k][w][m]=negBasePowers[k+w]*c[k][w]*powers[m][w];
			phiA=new double[numSets][1+maxPower];
			phiB=new double[numSets][1+maxPower];
		}
		private void setZero(double[][] array)	{
			for (int i=0;i<array.length;++i) Arrays.fill(array[i],0d);	
		}
		// Returns the set this number belongs to, or maybe -1 if it's not to be counted.
		public abstract int getSet(long number);
		public double getSum(int initialDigits,int maxDigits)	{
			double result=0;
			long firstLimit=LongMath.pow(base,initialDigits-1);
			long secondLimit=base*firstLimit;
			for (long i=1;i<firstLimit;++i) if (getSet(i)>=0) result+=1d/i;
			setZero(phiA);
			for (long i=firstLimit;i<secondLimit;++i)	{
				int set=getSet(i);
				if (set<0) continue;
				double inverse=1d/i;
				result+=inverse;
				phiA[set][1]+=inverse;
				double power=inverse;
				for (int j=2;j<=MAX_POWER;++j)	{
					power*=inverse;
					phiA[set][j]+=power;
				}
			}
			double[][] prev=phiA;
			double[][] next=phiB;
			for (int d=initialDigits;d<maxDigits;++d)	{
				setZero(next);
				for (int l=0;l<numSets;++l) for (int m=0;m<base;++m)	{
					int j=adjacencyGraph[l][m];
					if (j<0) continue;
					for (int k=1;k<=maxPower;++k) for (int w=0;w+k<=maxPower;++w) next[j][k]+=a[k][w][m]*prev[l][k+w];
				}
				for (int j=0;j<numSets;++j) result+=next[j][1];
				double[][] swap=prev;
				prev=next;
				next=swap;
			}
			return result;
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
		public SpecialKempnerSeries(int maxPower) {
			super(10,maxPower,20,GRAPH);
		}
		@Override
		public int getSet(long number)	{
			int result=(int)(number%10);
			if (((number/10)%10)==result) result+=10;
			long secondToLastDigit=-1;
			long lastDigit=-1;
			while (number>0)	{
				long digit=number%10;
				if ((digit==lastDigit)&&(lastDigit==secondToLastDigit)) return -1;
				secondToLastDigit=lastDigit;
				lastDigit=digit;
				number/=10;
			}
			return result;
		}
	}
	
	// ZUTUN! Ahora que tengo las respuesta, puedo hacer DIE EXPERIMENTEN!!!!!
	/*
	 * Sumar (-1)^x(n)/n donde x(n) es la suma de los dígitos de n.
	 * Lo mismo pero para n^2 -> Hostia puta, acabo de caer en que creo que había un problema MUY parecido a esto :O.
	 * Sumar series de Kempner para 1, 2, 3, ... , y también para números de varias cifras.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		double result=new SpecialKempnerSeries(MAX_POWER).getSum(4,5000);
		long tac=System.nanoTime();
		double seconds=(1e-9)*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.10f",result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
