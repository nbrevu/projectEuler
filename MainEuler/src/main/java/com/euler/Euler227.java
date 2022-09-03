package com.euler;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Locale;

public class Euler227 {
	// This is stupidly slow.
	// https://en.wikipedia.org/wiki/Absorbing_Markov_chain#Expected_number_of_steps saves the day (also Matlab).
	private final static int SIZE=50;
	private final static BigDecimal TOL=BigDecimal.valueOf(1e-14);
	
	private static BigDecimal[][] getTransitionMatrix()	{
		MathContext mc=new MathContext(500);
		BigDecimal[][] transitionMatrix=new BigDecimal[1+SIZE][1+SIZE];
		BigDecimal move2=BigDecimal.valueOf(1l).divide(BigDecimal.valueOf(36l),mc);
		BigDecimal move1=BigDecimal.valueOf(2l).divide(BigDecimal.valueOf(9l),mc);
		BigDecimal stay=BigDecimal.valueOf(1l).divide(BigDecimal.valueOf(2l),mc);
		for (int i=2;i<=SIZE-2;++i)	{
			transitionMatrix[i][i-2]=move2;
			transitionMatrix[i][i-1]=move1;
			transitionMatrix[i][i]=stay;
			transitionMatrix[i][i+1]=move1;
			transitionMatrix[i][i+2]=move2;
		}
		transitionMatrix[1][0]=move1;
		transitionMatrix[1][1]=stay.add(move2);
		transitionMatrix[1][2]=move1;
		transitionMatrix[1][3]=move2;
		transitionMatrix[SIZE-1][SIZE-3]=move2;
		transitionMatrix[SIZE-1][SIZE-2]=move1;
		transitionMatrix[SIZE-1][SIZE-1]=stay.add(move2);
		transitionMatrix[SIZE-1][SIZE]=move1;
		transitionMatrix[SIZE][SIZE-2]=move2.add(move2);
		transitionMatrix[SIZE][SIZE-1]=move1.add(move1);
		transitionMatrix[SIZE][SIZE]=stay;
		return transitionMatrix;
	}
	
	private static BigDecimal[] multiply(BigDecimal[] prev,BigDecimal[][] transition)	{
		BigDecimal[] res=new BigDecimal[1+SIZE];
		for (int i=0;i<=SIZE;++i)	{
			BigDecimal thisVal=BigDecimal.ZERO;
			for (int j=0;j<=SIZE;++j) if (transition[j][i]!=null) thisVal=thisVal.add(transition[j][i].multiply(prev[j]));
			res[i]=thisVal;
		}
		return res;
	}
	
	private static BigDecimal[] init()	{
		BigDecimal[] res=new BigDecimal[1+SIZE];
		for (int i=0;i<SIZE;++i) res[i]=BigDecimal.ZERO;
		res[SIZE]=BigDecimal.ONE;
		return res;
	}
	
	public static void main(String[] args)	{
		BigDecimal result=BigDecimal.ZERO;
		BigDecimal[] curr=init();
		BigDecimal[][] matrix=getTransitionMatrix();
		for (int i=1;;++i)	{
			curr=multiply(curr,matrix);
			BigDecimal augend=curr[0].multiply(BigDecimal.valueOf(i));
			if ((i>100)&&(augend.compareTo(TOL)<0)) break;
			result=result.add(augend);
			System.out.println(result.doubleValue());
		}
		double val=result.doubleValue();
		System.out.println(String.format(Locale.UK,"%.10f",val));
	}
}
