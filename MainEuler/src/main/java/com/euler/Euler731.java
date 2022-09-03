package com.euler;

import com.google.common.math.LongMath;

public class Euler731 {
	public static void main(String[] args)	{
		long initialOffset=LongMath.pow(10l,8)-1;
		for (int i=1;;++i)	{
			long periodDigits=(i==1)?1:(LongMath.pow(3,i-2));
			long myOffset=LongMath.pow(3,i);
			long trueOffset=initialOffset-myOffset;
			if (trueOffset<0)	{
				System.out.println("Bueno, pues con i="+i+" ya me he pasado.");
				break;
			}	else	{
				long baseOffset=trueOffset%periodDigits;
				System.out.println("Para i="+i+" tengo un periodo de "+periodDigits+" y tengo que empezar a contar a partir del "+baseOffset+"º.");
			}
		}
	}
}
