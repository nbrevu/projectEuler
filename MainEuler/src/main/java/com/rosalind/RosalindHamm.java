package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.rosalind.utils.ProteinUtils;

public class RosalindHamm {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_hamm.txt";
	
	public static void main(String[] args) throws IOException	{
		Path srcFile=Paths.get(FILE);
		try (BufferedReader reader=Files.newBufferedReader(srcFile))	{
			String l1=reader.readLine();
			String l2=reader.readLine();
			int dist=ProteinUtils.computeHammingDistance(l1,l2);
			System.out.println(dist);
		}
	}
}
