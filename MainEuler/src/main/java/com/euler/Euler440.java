package com.euler;

import java.math.BigInteger;

import com.euler.common.EulerUtils;

public class Euler440 {
	public static void main(String[] args)	{
		/*
		 * At a first glance, it would seem that gcd(T(i),T(j)) = T(gcd(i+1,j-1)-1).
		 * In other words, gcd(T(i+1),T(j+1))=T(gcd(i+1,j+1)). And now it's time to... WRITE A PROBLEM TO CHECK IT!! For smaller numbers, of course.
		 */
		BigInteger[] terms=new BigInteger[28];
		terms[0]=BigInteger.ONE;
		terms[1]=BigInteger.TEN;
		for (int i=2;i<=27;++i) terms[i]=BigInteger.TEN.multiply(terms[i-1]).add(terms[i-2]);
		for (int i=1;i<=27;++i)	{
			System.out.println("T("+i+")="+terms[i]+".");
			for (int j=1;j<i;++j)	{
				boolean coprimeN=EulerUtils.areCoprime(i,j);
				BigInteger gcd=EulerUtils.gcd(terms[i],terms[j]);
				boolean coprimeTN=gcd.equals(BigInteger.ONE);
				System.out.println("i="+i+", j="+j+"; coprime(i,j)="+coprimeN+"; coprime(T(i),T(j))="+coprimeTN+" (gcd="+gcd+").");
			}
		}
	}
}
