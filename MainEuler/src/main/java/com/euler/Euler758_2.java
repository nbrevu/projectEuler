package com.euler;

import java.util.Arrays;

import com.euler.common.EulerUtils;

public class Euler758_2 {
	private static void printFullInfo(int i,int j)	{
		long a=(1l<<j)-1;
		long b=(1l<<i)-1;
		long[] coeffs=EulerUtils.extendedGcd(a,b).coeffs;
		long coef11,coef12,coef21,coef22;
		if (coeffs[0]<0)	{
			/*
			 * For example: (31,7) -> (-2,9) -> -2*31+9*7=1.
			 * The first sum is (7-2)*31=(31-9)*7+1
			 * The second sum is 9*7=2*31+1
			 */
			coef11=b+coeffs[0];
			coef12=a-coeffs[1];
			coef21=coeffs[1];
			coef22=-coeffs[0];
		}	else	{
			/*
			 * For example: (7,31) -> (9,-2) -> 9*7+(-2)*31=1.
			 * The first sum is 9*7=2*31+1.
			 * The second sum is (7-2)*31=(31-9)*7+1;
			 */
			coef11=coeffs[0];
			coef12=-coeffs[1];
			coef21=a+coeffs[1];
			coef22=b-coeffs[0];
		}
		String sum1=String.format("%d*%d=%d*%d+1",coef11,a,coef12,b);
		String sum2=String.format("%d*%d=%d*%d+1",coef21,a,coef22,b);
		long minSum=Math.min(coef11+coef12,coef21+coef22);
		long expectedResult=2*(minSum-1);
		System.out.println("("+j+","+i+"):");
		System.out.println("\tNumbers: ("+a+","+b+"):");
		System.out.println("\tCoeffs: "+Arrays.toString(coeffs)+".");
		System.out.println("\t"+sum1);
		System.out.println("\t"+sum2);
		System.out.println("\tMinsum="+minSum+".");
		System.out.println("\tExpected result="+expectedResult+".");
	}
	
	public static void main(String[] args)	{
		for (int i=3;i<50;++i) for (int j=2;j<i;++j) if (EulerUtils.gcd(i,j)==1) printFullInfo(i,j);
	}
}
