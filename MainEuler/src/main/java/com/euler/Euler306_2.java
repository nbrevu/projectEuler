package com.euler;

public class Euler306_2 {
	private final static int LIMIT=1000000;
	public static void main(String[] args)	{
		int fullSeqs=LIMIT/34;
		int lastSeqSize=LIMIT%34;
		int p2=4+5*fullSeqs;
		if (lastSeqSize>=29) p2+=5;
		else if (lastSeqSize>=25) p2+=4;
		else if (lastSeqSize>=21) p2+=3;
		else if (lastSeqSize>=9) p2+=2;
		else if (lastSeqSize>=5) ++p2;
		int result=LIMIT-p2;
		System.out.println(result);
	}
}
