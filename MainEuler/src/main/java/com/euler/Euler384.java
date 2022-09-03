package com.euler;

public class Euler384 {
	private static int calculateB(int n)	{
		int result=1;
		int lastBit=0;
		while (n>0)	{
			int currentBit=n&1;
			n>>=1;
			if ((currentBit*lastBit)==1) result=-result;
			lastBit=currentBit;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		/*
		 * EVEN VALUES pattern (actually belonging to ODD numbers):
		 * 	2,2*; 4,4; 6,6; 4,4*; 6,6; 8,8; 6,6*; 8,8; 10,10; 12,12; 14,14; 12,12; 10,10; 8,8; 10,10; 8,8*; 10,10; 12,12; 14,14; 12,12;
		 * BALLA BALLA. Esto es justo el doble de los valores de la sucesión:
		 * 1*,2,3,2*,3,4,3*,4,5,6,7,6,5,4,5,4*,5,6,7,6.
		 * 
		 * OLD VALUES pattern (actually belonging to EVEN numbers):
		 * 1*,3,3,3*,5,7,5,5,5,7,7,7,7,5*,7,7*,9,11,11,11,13,
		 * The pattern is not so easy, BUT! we can calculate it from the even number, then adding one or not depending on the value of the
		 * previous even value.
		 * 
		 * However... this allows us to calculate any random given term, which is good, but we need the OPPOSITE function: given some N and M,
		 * calculate the Mth time c(x)=N.
		 * 
		 * The relationship between the even values and the whole sequence is this: if c(x)=a, then c(4x+1)=c(4x+3)=2a.
		 * 
		 * So: in order to look for the Mth occurrence of an even number 2A, we look for the (M/2)th occurrence of A, then we use either 4x+1
		 * (if M was even) or 4x+3 (if M was odd).
		 * 
		 * Odd numbers are *very* tricky, because for 2M+1 we might need to look for the occurrences of 2M AND 2M+2.
		 */
		int c=0;
		for (int i=0;i<=100;++i)	{
			c+=calculateB(i);
			System.out.println("c("+i+")="+c+".");
		}
		long prevFibo=1;
		long curFibo=2;
		int curIndex=3;
		do	{
			long nextFibo=curFibo+prevFibo;
			prevFibo=curFibo;
			curFibo=nextFibo;
			++curIndex;
			System.out.println("Fibo("+curIndex+")="+curFibo+".");
		}	while (curIndex<45);
	}
}
