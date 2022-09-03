package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.rosalind.utils.IoUtils;

public class RosalindAfrq {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_afrq.txt";

	private static double getSingleAlleleProb(double homocygProb)	{
		/*
		 * Let P be the probability of gene X having the recessive allele.
		 * Then h = P^2. That is, P=sqrt(h)
		 * The probability of having a completely sane individual is (1-P)^2.
		 * So the result is 1-(1-P)^2 = 2P-P^2 = 2sqrt(h)-h 
		 */
		return 2*Math.sqrt(homocygProb)-homocygProb;
	}
	
	public static void main(String[] args) throws IOException	{
		double[] probs;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			probs=IoUtils.parseStringAsArrayOfDoubles(reader.readLine(),-1);
		}
		double[] result=new double[probs.length];
		for (int i=0;i<probs.length;++i) result[i]=getSingleAlleleProb(probs[i]);
		System.out.println(IoUtils.toStringWithSpaces(result));
	}
}
