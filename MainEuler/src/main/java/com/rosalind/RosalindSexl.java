package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.rosalind.utils.IoUtils;

public class RosalindSexl {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_sexl.txt";

	private static double getFemaleCarrierProb(double maleProb)	{
		// Prob that male has allele A = prob that a random X chromosome has allele A = P.
		// Prob that a female is homocygotic: P^2.
		// Prob that a female doesn't carry the allele: (1-P)^2.
		// Prob we're looking for: 1-P^2-(1-P^2)^2 = 2P-2P^2 = 2P(1-P)
		return 2*maleProb*(1-maleProb);
	}
	
	public static void main(String[] args) throws IOException	{
		double[] maleProbs;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			maleProbs=IoUtils.parseStringAsArrayOfDoubles(reader.readLine(),-1);
		}
		int N=maleProbs.length;
		double[] result=new double[N];
		for (int i=0;i<N;++i) result[i]=getFemaleCarrierProb(maleProbs[i]);
		System.out.println(IoUtils.toStringWithSpaces(result));
	}
}
