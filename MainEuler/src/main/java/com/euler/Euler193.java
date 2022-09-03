package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler193 {
	private final static long N=LongMath.pow(2l,50);
	
	public static void main(String[] args)	{
		System.out.println(EulerUtils.getSquareFreeNumbers(N));
	}
}
