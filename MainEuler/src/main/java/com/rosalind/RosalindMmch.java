package com.rosalind;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.List;

import com.rosalind.aminoacids.BaseCounter;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindMmch {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_mmch.txt";
	
	private static BigInteger getVariations(long a,long b)	{
		if (a<b) return getVariations(b,a);
		BigInteger result=BigInteger.ONE;
		while (b>0)	{
			result=result.multiply(BigInteger.valueOf(a));
			--b;
			--a;
		}
		return result;
	}

	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=1) throw new IllegalArgumentException();
		BaseCounter counter=new BaseCounter();
		counter.readString(entries.get(0).getContent(),true);
		BigInteger varsAU=getVariations(counter.getA(),counter.getTorU());
		BigInteger varsCG=getVariations(counter.getC(),counter.getG());
		System.out.println(varsAU.multiply(varsCG));
	}
}
