package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.rosalind.aminoacids.RnaTranscriber;
import com.rosalind.aminoacids.TranslatedCodon;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;
import com.rosalind.utils.ProteinUtils;

public class RosalindOrf {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_orf.txt";
	
	private static List<String> getAllProteinCandidates(FastaEntry entry)	{
		RnaTranscriber transcriber=new RnaTranscriber(entry.getContent());
		List<String> result=new ArrayList<>();
		for (List<TranslatedCodon> openFrame:transcriber.translateAllOpenFrames(true)) result.addAll(ProteinUtils.getProteinCandidatesAsStrings(openFrame));
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=1) throw new IllegalArgumentException();
		for (String protein:new HashSet<>(getAllProteinCandidates(entries.get(0)))) System.out.println(protein);
	}
}
