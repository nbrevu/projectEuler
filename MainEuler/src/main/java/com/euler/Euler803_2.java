package com.euler;

import java.math.BigInteger;
import java.util.Arrays;

import com.google.common.math.LongMath;

public class Euler803_2 {
	private final static BigInteger F=BigInteger.valueOf(25214903917l);
	private final static BigInteger K=BigInteger.valueOf(11);
	private final static BigInteger MOD=BigInteger.valueOf(LongMath.pow(2l,48));
	
	private final static String GOAL="PuzzleOne";
	
	// private final static BigInteger A0=BigInteger.valueOf(123456);
	private final static BigInteger A0=BigInteger.valueOf(78580612777175l);
	
	private static int[] cToB(String in)	{
		char[] cs=in.toCharArray();
		int[] result=new int[cs.length];
		for (int i=0;i<cs.length;++i)	{
			char c=cs[i];
			if ((c>='a')&&(c<='z')) result[i]=c-'a';
			else if ((c>='A')&&(c<='Z')) result[i]=c-'A'+26;
			else throw new IllegalArgumentException("Invalid character: \""+c+"\".");
		}
		return result;
	}
	
	public static void main(String[] args)	{
		System.out.println(Arrays.toString(cToB(GOAL)));
	}
}
