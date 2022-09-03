package com.euler.experiments;

public class Palindromes {
	public static void main(String[] args)	{
		/*
		 * Theoretical analysis: the only 5-digit palindromes which are at the same time multiples of 11 and 111 are:
		 * 13431, 26862, 41514 and 54945.
		 */
		for (int a=1;a<=9;++a) for (int b=0;b<=9;++b) for (int c=0;c<=9;++c)	{
			int n=10001*a+1010*b+100*c;
			if (((n%11)==0)&&((n%111)==0)) System.out.println(n);
		}
		// Result: correct :).
	}
}
