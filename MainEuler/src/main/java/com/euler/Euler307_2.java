package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import com.euler.common.BigIntegerUtils;
import com.euler.common.BigIntegerUtils.BigFactorialCache;

public class Euler307_2 {
	private final static int DEFECTS=3;
	private final static int CHIPS=7;
	
	// Total amount of distributions: CHIPS^DEFECTS.
	/* Total amount of distributions with no more than one defect per chip:
	 * 	In this case we just have a distribution of 20000 chips with one defect, and 980000 with no defect.
	 *  So we use a combinatorial number, (CHIPS over DEFECTS).
	 */
	/* Now let's suppose that we have N chips with exactly two defects.
	 *  So there are 2N defects in these N chips.
	 *  And then there are 20000-2N defects to be distributed among 1000000-N chips, no more than one per chip.
	 *  Therefore, there will be (20000-2N) chips with one defect, and the amount of chips with no defect is:
	 *  (1000000-N)-(20000-2N)=980000+N.
	 * This means that in this case we have [1000000 over (N,20000-2N,980000+N)] defects.
	 * This case can in fact be integrated in the previous one, with N=0.
	 */
	public static void main(String[] args)	{
		//*
		BigInteger totalCases=BigIntegerUtils.pow(CHIPS,DEFECTS);
		BigFactorialCache cache=new BigFactorialCache(CHIPS);
		BigInteger toSubtract=BigInteger.ZERO;
		for (int i=0;i<=DEFECTS/2;++i)	{
			BigInteger num=cache.get(CHIPS);
			BigInteger den=cache.get(i).multiply(cache.get(DEFECTS-2*i)).multiply(cache.get(CHIPS-DEFECTS+i));
			num=num.multiply(cache.get(DEFECTS));
			System.out.println(num.toString()+'/'+den.toString());
			for (int j=0;j<i;++j) den=den.multiply(cache.get(2));
			toSubtract=toSubtract.add(num.divide(den));
			if (toSubtract.compareTo(totalCases)>0) System.out.println("Pues no.");
		}
		BigInteger numerator=totalCases.subtract(toSubtract);
		MathContext mc=new MathContext(50,RoundingMode.HALF_UP);
		BigDecimal result=new BigDecimal(numerator).divide(new BigDecimal(totalCases),mc);
		System.out.println(result);
		/*/
		BigInteger cosaTocha=BigInteger.ONE;
		for (int i=2;i<=1000000;++i) cosaTocha=cosaTocha.multiply(BigInteger.valueOf(i));
		System.out.println("La cosa tocha tiene "+cosaTocha.toString().length()+" d�gitos.");
		//*/
	}
}
