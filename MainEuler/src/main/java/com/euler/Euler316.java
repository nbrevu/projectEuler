package com.euler;

import java.util.Arrays;

import com.euler.common.Rational;
import com.google.common.math.LongMath;

public class Euler316 {
	private final static long DIVISOR=LongMath.pow(10l,16);
	private final static int LIMIT=999999;
	
	private final static Rational ONE_TENTH=new Rational(1,10);
	private final static Rational NINE_TENTHS=new Rational(9,10);
	private final static Rational ZERO=new Rational(0);
	private final static Rational ONE=new Rational(1);
	/*
	 * This would have been a perfect job for Matlab. Unfortunately, I need exact precision (no floating point) and Matlab can only do that
	 * with sloooooooow symbolic calculations. So here we go, implementing Gaussian reduction IN JAVA.
	 */
	private static int[] getDigits(long in)	{
		int N=1+(int)Math.floor(Math.log10(in));
		int[] result=new int[N];
		for (int i=N-1;i>=0;--i)	{
			result[i]=(int)(in%10);
			in/=10;
		}
		return result;
	}
	
	private static boolean tryPrefix(int[] digits,int i_1,int additionalDigit,int k)	{
		if (digits[k-1]!=additionalDigit) return false;
		// for (int l=k-2;l>=0;--l) if (digits[l]!=digits[i_1+l+1-k]) return false;
		int base=i_1+1-k;
		for (int l=k-2;l>=0;--l) if (digits[l]!=digits[base+l]) return false;
		return true;
	}
	
	private static Rational[][] getReducibleMatrix(int[] digits)	{
		int N=digits.length;
		Rational[][] result=new Rational[N][N+1];
		for (int i=0;i<N;++i) Arrays.fill(result[i],ZERO);
		result[0][0]=NINE_TENTHS;
		result[1][0]=ONE_TENTH;
		for (int i=2;i<=N;++i) for (int j=0;j<=9;++j)	{
			boolean found=false;
			// This can probably be done in a better way, but this is not the bottleneck so I'm not going to bother.
			for (int k=i;k>=1;--k) if (tryPrefix(digits,i-1,j,k))	{
				found=true;
				if (k<N) result[k][i-1]=result[k][i-1].sum(ONE_TENTH);
				// if k==N we are transitioning to the "absorbing" state.
				break;
			}
			if (!found) result[0][i-1]=result[0][i-1].sum(ONE_TENTH);
		}
		for (int i=0;i<N;++i) for (int j=0;j<=N;++j) if (i==j) result[i][j]=ONE.subtract(result[i][j]);
		else if (result[i][j].isNotZero()) result[i][j]=result[i][j].negate();
		result[0][N]=ONE;
		return result;
	}
	
	private static int getRowToExchange(Rational[][] matrix,int diag)	{
		for (int i=diag+1;i<matrix.length;++i) if (matrix[i][diag].isNotZero()) return i;
		throw new RuntimeException("Singular matrix.");
	}
	
	private static void swapRows(Rational[][] matrix,int i,int j)	{
		// Apparently this is actually never needed in this particular problem!
		Rational[] swap=matrix[i];
		matrix[i]=matrix[j];
		matrix[j]=swap;
	}
	
	private static void reduce(Rational[][] matrix)	{
		int N=matrix.length;
		// For each row, from top to down...
		for (int i=0;i<N;++i)	{
			Rational toDivide=matrix[i][i];
			if (!toDivide.isNotZero())	{
				int row=getRowToExchange(matrix,i);
				swapRows(matrix,i,row);
				toDivide=matrix[i][i];
			}
			matrix[i][i]=ONE;
			// Ensure that there is an one in (i,i).
			for (int k=i+1;k<=N;++k) matrix[i][k]=matrix[i][k].divide(toDivide);
			// Then reduce the remaining rows. It's assumed that (i,0:i-1) is all zeroes because of previous steps.
			for (int j=i+1;j<N;++j)	{
				Rational toMultiply=matrix[j][i];
				if (toMultiply.isNotZero())	{
					matrix[j][i]=ZERO;
					for (int k=i+1;k<=N;++k) matrix[j][k]=matrix[j][k].subtract(matrix[i][k].multiply(toMultiply));
				}
			}
		}
		// Now we have an upper triangular matrix and the diagonal elements are 1, so we can move upwards creating a diagonal one.
		for (int i=N-1;i>0;--i) for (int j=i-1;j>=0;--j)	{
			Rational toSubtract=matrix[j][i];
			// We only need to update the rightmost element.
			if (toSubtract.isNotZero()) matrix[j][N]=matrix[j][N].subtract(matrix[i][N].multiply(toSubtract));
		}
	}

	private static long sumLastColumn(Rational[][] rat)	{
		int N=rat[0].length;
		long result=0;
		for (int i=0;i<rat.length;++i) result+=rat[i][N-1].getIntegerValue();
		return result;
	}
	
	private static long getExpectedValue(long in)	{
		int[] digits=getDigits(in);
		// I only need the first row of the inverse matrix, so I can reduce the amount of calculations done.
		// https://math.stackexchange.com/a/952877
		Rational[][] reducibleMatrix=getReducibleMatrix(digits);
		reduce(reducibleMatrix);
		return sumLastColumn(reducibleMatrix)+1-digits.length;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		for (int i=2;i<=LIMIT;++i)	{
			long value=DIVISOR/i;
			result+=getExpectedValue(value);
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
