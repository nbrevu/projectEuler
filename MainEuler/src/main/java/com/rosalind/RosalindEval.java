package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.rosalind.utils.IoUtils;
import com.rosalind.utils.ProteinUtils;

public class RosalindEval {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_eval.txt";

	public static void main(String[] args) throws IOException	{
		int length;
		String s;
		double[] probs;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			length=Integer.parseInt(reader.readLine());
			s=reader.readLine();
			probs=IoUtils.parseStringAsArrayOfDoubles(reader.readLine(),-1);
		}
		int gaps=length-1;
		double[] result=new double[probs.length];
		for (int i=0;i<probs.length;++i)	{
			double baseProb=ProteinUtils.getProbability(s,probs[i]);
			result[i]=baseProb*gaps;
		}
		System.out.println(IoUtils.toStringWithSpaces(result));
	}
}
