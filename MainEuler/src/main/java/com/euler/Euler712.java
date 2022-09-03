package com.euler;

import com.euler.common.SumOfTotientCalculator;

public class Euler712 {
	private final static long MOD=1_000_000_007l;
	
	// Esto no funciona. Quiero una suma y esto hace un conteo. Hay que echarle más refinamiento.
	public static void main(String[] args)	{
		/*
		SumOfTotientCalculator calculator=SumOfTotientCalculator.getWithMod(MOD);
		long tic=System.nanoTime();
		long result=calculator.getTotientSum(LongMath.pow(10l,12));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Pues he tardado "+seconds+" segundos en calcular la mierda ésta, que es igual a "+result+".");
		// Pues he tardado 194.78332495400002 segundos en calcular la mierda ésta, que es igual a 962430712.
		*/
		SumOfTotientCalculator calculator=SumOfTotientCalculator.getWithMod(MOD);
		long result=0;
		for (int i=1;i<=10;++i) result+=calculator.getTotientSum(10/i);
		result*=2;
		System.out.println(result);
		for (int i=1;i<=10;++i) System.out.println("SumTotient("+i+")="+calculator.getTotientSum(i)+".");
	}
}
