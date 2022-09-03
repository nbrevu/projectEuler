package com.euler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Euler621 {
	/*
	 * Referencias que hay que mirarse:
	 * https://pdfs.semanticscholar.org/5fec/ad110f477e7a6ab06b189890f973c3ecd765.pdf
	 * http://www.mathcs.emory.edu/~ono/publications-cv/pdfs/006.pdf
	 * https://oeis.org/A000164
	 * http://web.maths.unsw.edu.au/~mikeh/webpapers/paper73.pdf
	 * matwbn.icm.edu.pl/ksiazki/aa/aa77/aa7732.pdf
	 */
	
	/*
	 * t(81n+3) = 4t(9n).
	 * Por tanto, t(1752600000000) = t(81*21637037037+3) = 4t(9*21637037037) = 4t(194733333333)
	 * 
	 * Se debe de poder seguir... el último PDF tiene identidades por el estilo.
	 * 
	 * De momento, 194733333333 = (3^3)*7*11*19*4929833
	 */
	
	private final static long LIMIT=1000000;
	
	private static long[] getTriangulars(long upTo)	{
		List<Long> list=new ArrayList<>();
		list.add(0l);
		long prev=0;
		for (long i=1;;++i)	{
			long curr=prev+i;
			if (curr>upTo) break;
			list.add(curr);
			prev=curr;
		}
		long[] result=new long[list.size()];
		for (int i=0;i<list.size();++i) result[i]=list.get(i).longValue();
		return result;
	}
	
	private static long[] sumThreeTriangleCombinations(long[] triangulars,long limit)	{
		long[] result=new long[(int)(1+limit)];
		int N=triangulars.length;
		for (int i=0;i<N;++i)	{
			long li=triangulars[i];
			for (int j=0;j<N;++j)	{
				long lij=li+triangulars[j];
				for (int k=0;k<N;++k)	{
					long sum=lij+triangulars[k];
					if (sum<=limit) ++result[(int)sum];
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long[] triangulars=getTriangulars(LIMIT);
		long[] combinations=sumThreeTriangleCombinations(triangulars,LIMIT);
		try (PrintStream ps=new PrintStream(new File("C:\\output621.txt")))	{
			for (int i=0;i<combinations.length;++i) ps.println(""+i+": "+combinations[i]+".");
		}	catch (IOException exc)	{
			System.out.println("Pues va a tener que ser que no.");
		}
	}
}
