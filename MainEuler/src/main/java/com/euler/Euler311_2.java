package com.euler;

import java.math.RoundingMode;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.BaseSquareDecomposition.PrimePowerDecompositionFinder;
import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.euler.common.EulerUtils.LongPair;
import com.google.common.math.LongMath;

public class Euler311_2 {
	public static void main(String[] args)	{
		/*-
		for (long n=5;n<=15625;n*=5)	{
			for (long x=0;;++x)	{
				long x2=x*x;
				long y2=n-x*x;
				if (y2<x2) break;
				long y=LongMath.sqrt(y2,RoundingMode.DOWN);
				if (y*y==y2) System.out.println(String.format("%d=%d^2+%d^2.",n,x,y));
			}
			System.out.println();
		}
		*/
		PrimePowerDecompositionFinder finder=new PrimePowerDecompositionFinder(5l,new CombinatorialNumberCache(6));
		for (int i=1;i<=6;++i)	{
			long power=LongMath.pow(5l,i);
			BaseSquareDecomposition decomps=finder.getFor(i);
			for (LongPair decomp:decomps.getBaseCombinations()) System.out.println(String.format("%d=%d^2+%d^2.",power,decomp.x,decomp.y));
			System.out.println();
		}
		BaseSquareDecomposition decomps=finder.getFor(5);
		BaseSquareDecomposition other=new PrimePowerDecompositionFinder(13l,new CombinatorialNumberCache(6)).getFor(2);
		BaseSquareDecomposition combined=decomps.combineWith(other);
		for (LongPair decomp:combined.getBaseCombinations()) System.out.println(String.format("%d=%d^2+%d^2.",3125*169,decomp.x,decomp.y));
		System.out.println("Y ahora el coro de los viejos locos.");
		for (long x=0;;++x)	{
			long x2=x*x;
			long y2=3125*169-x*x;
			if (y2<x2) break;
			long y=LongMath.sqrt(y2,RoundingMode.DOWN);
			if (y*y==y2) System.out.println(String.format("%d=%d^2+%d^2.",3125*169,x,y));
		}
	}
}
