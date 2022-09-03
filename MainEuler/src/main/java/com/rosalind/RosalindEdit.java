package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;
import com.rosalind.utils.ProteinUtils;

public class RosalindEdit {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_edit.txt";
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=2) throw new IllegalStateException();
		System.out.println(ProteinUtils.computeLevenshteinDistance(entries.get(0).getContent(),entries.get(1).getContent()));
	}
}
