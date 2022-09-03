package com.rosalind;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.List;

import com.rosalind.aminoacids.BaseCounter;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindPmch {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_pmch.txt";
	
	private static BigInteger factorial(int in)	{
		BigInteger result=BigInteger.ONE;
		for (int i=2;i<=in;++i) result=result.multiply(BigInteger.valueOf(i));
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=1) throw new IllegalArgumentException();
		BaseCounter counter=new BaseCounter();
		counter.readString(entries.get(0).getContent(),true);
		int a=counter.getA();
		int c=counter.getC();
		int g=counter.getG();
		int u=counter.getTorU();
		if ((c!=g)||(a!=u)) throw new IllegalArgumentException();
		System.out.println(factorial(a).multiply(factorial(c)));
	}
}
