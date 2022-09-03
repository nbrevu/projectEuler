package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.rosalind.utils.ProteinUtils;

public class RosalindRevc {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_revc.txt";
	
	public static void main(String[] args) throws IOException	{
		Path srcFile=Paths.get(FILE);
		try (BufferedReader reader=Files.newBufferedReader(srcFile))	{
			for (;;)	{
				String line=reader.readLine();
				if (line==null) return;
				String converted=ProteinUtils.getComplement(line);
				System.out.println(converted);
			}
		}
	}
}
