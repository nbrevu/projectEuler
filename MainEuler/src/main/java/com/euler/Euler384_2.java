package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.google.common.collect.Lists;

public class Euler384_2 {
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
		int s=0;
		NavigableMap<Integer,List<Integer>> grouped=new TreeMap<>();
		for (int i=0;i<=1000;++i)	{
			s+=calculateB(i);
			//System.out.println("s("+i+")="+s+".");
			grouped.computeIfAbsent(s,(Integer unused)->new ArrayList<>()).add(i);
		}
		for (Map.Entry<Integer,List<Integer>> entry:grouped.entrySet())	{
			int value=entry.getKey();
			List<Integer> numbers=entry.getValue();
			System.out.println("For value="+value+":");
			System.out.println("\tNumbers: "+numbers+".");
			System.out.println("\tBase 2 numbers: "+Lists.transform(numbers,(Integer x)->Integer.toString(x,2))+".");
		}
	}
}
