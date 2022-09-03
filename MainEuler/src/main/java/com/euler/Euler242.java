package com.euler;

import java.io.IOException;
import java.io.PrintStream;

public class Euler242 {
	private final static int LIMIT=10000;
	
	private static boolean combinatorialParity(int n,int k)	{
		// Returns whether C(n,k) is odd. See http://math.stackexchange.com/a/11009 for an explanation
		// of the black magic involved.
		return (k&(n-k))==0;
	}
	
	private static boolean isFOdd(int n,int m)	{
		int odds=(n+1)/2;
		int evens=n-odds;
		boolean result=false;
		int minE=Math.max(0,m-odds);
		if (((minE)%2)==1) ++minE;
		int maxE=Math.min(evens,m);
		for (int e=minE;e<=maxE;e+=2)	{
			int o=m-e;
			boolean termParity=combinatorialParity(evens,e)&&combinatorialParity(odds,o);
			result^=termParity;
		}
		return result;
	}
	
	private static int getWeight(int n)	{
		int res=0;
		while (n>0)	{
			if ((n&1)==1) ++res;
			n>>=1;
		}
		return res;
	}

	// This is a demonstration.
	public static void main(String[] args)	{
		try (PrintStream out=new PrintStream("C:\\out242.txt"))	{
			for (int i=1;i<=LIMIT;i+=4)	{
				int n=(i-1)/4;
				int w=getWeight(n);
				int comp1=1<<w;
				int comp2=0;
				out.print(n+": ");
				for (int j=1;j<=i;j+=4) if (isFOdd(i,j))	{
					out.print("("+i+","+j+") ");
					++comp2;
				}
				out.println();
				if (comp1!=comp2) System.out.println("Ooooh. Esto falla para i="+i+" (n="+n+").");
			}
		}	catch (IOException exc)	{
			System.out.println("Pues vaya una mierda.");
		}
	}
}
