package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler748 {
	private static class IntPair	{
		public final int a;
		public final int b;
		public IntPair(int a,int b)	{
			this.a=a;
			this.b=b;
		}
	}
	private static class SquareSumFinder	{
		private final long[] squares;
		public SquareSumFinder(long limit)	{
			squares=new long[(int)LongMath.sqrt(limit,RoundingMode.UP)];
			for (int i=0;i<squares.length;++i) squares[i]=i*(long)i;
		}
		public List<IntPair> getSquareSums(long limit)	{
			List<IntPair> result=new ArrayList<>();
			for (int i=1;i<squares.length;++i)	{
				long sq=squares[i];
				long diff=limit-sq;
				if (sq>diff) break;
				int pos=Arrays.binarySearch(squares,diff);
				if (pos>0) result.add(new IntPair(i,pos));
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		/*-
		 * Caso manual: (6,17)->5
		 * 6^2 + 17^2 = 13*5^2
		 * "divido" por 510^2:
		 * (1/85)^2 + (1/30)^2 = 13*(1/102)^2.
		 * 
		 * Y efectivamente queda fuera del límite de 10^2 por poco.
		 * Pues esto es factible, pero hay que tener cuidadín generando las sumas.
		 */
		SquareSumFinder finder=new SquareSumFinder(13_000_000);
		for (int i=1;i<=1000;++i)	{
			List<IntPair> sums=finder.getSquareSums(13l*i*i);
			System.out.println("Para 13*"+i+"^2 tengo:");
			for (IntPair p:sums)	{
				String prefix=EulerUtils.areCoprime(p.a,p.b)?"!":"";
				System.out.println(prefix+"\t("+p.a+","+p.b+").");
			}
		}
	}
}
