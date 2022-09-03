package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.rosalind.utils.ProteinUtils;

public class RosalindRstr {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_rstr.txt";

	public static void main(String[] args) throws IOException	{
		int N;
		double gcContent;
		String baseString;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			String line1=reader.readLine();
			String[] split=line1.split(" ");
			N=Integer.parseInt(split[0]);
			gcContent=Double.parseDouble(split[1]);
			baseString=reader.readLine();
		}	catch (IOException exc)	{
			N=90000;
			gcContent=0.6;
			baseString="ATAGCCGA";
		}
		double baseProb=ProteinUtils.getProbability(baseString,gcContent);
		double probNot=1.0-baseProb;
		double probNone=Math.pow(probNot,N);
		double result=1.0-probNone;
		System.out.println(result);
	}
}
