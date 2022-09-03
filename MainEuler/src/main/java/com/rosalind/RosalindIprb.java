package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RosalindIprb {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_iprb.txt";
	
	private static long triangular(long in)	{
		return (in*(in-1))/2;
	}
	
	public static void main(String[] args) throws IOException	{
		long k,m,n;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			String line=reader.readLine();
			String[] split=line.split(" ");
			k=Integer.parseInt(split[0]);
			m=Integer.parseInt(split[1]);
			n=Integer.parseInt(split[2]);
		}
		long total=k+m+n;
		long cases=triangular(total);
		// A+A: always has both dominant.
		// A+B: always has at least one dominant.
		// A+C: always has exactly one dominant.
		// B+B: 25% of having two, 50% of having one, 25% of having none: 75% of having at least one.
		// B+C: 50% of having one dominant, 50% of having none.
		// C+C: never has any dominant.
		double favourable=(1.0*(triangular(k)+(k*m)+(k*n)))+(0.75*triangular(m))+(0.5*m*n);
		double probability=favourable/cases;
		System.out.println(probability);
	}
}
