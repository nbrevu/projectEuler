package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.rosalind.aminoacids.BaseCounter;

public class RosalindDna {
	public static void main(String[] args) throws IOException	{
		Path srcFile=Paths.get("F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_dna.txt");
		try (BufferedReader reader=Files.newBufferedReader(srcFile))	{
			for (;;)	{
				String line=reader.readLine();
				if (line==null) return;
				BaseCounter counter=new BaseCounter();
				counter.readString(line,false);
				System.out.println(counter.toString());
			}
		}
	}
}
