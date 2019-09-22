package com.euler.common;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

public final class Timing {
	public static void time(LongSupplier function)	{
		long tic=System.nanoTime();
		long result=function.getAsLong();
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}

	public static void time(Supplier<String> function)	{
		long tic=System.nanoTime();
		String result=function.get();
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
