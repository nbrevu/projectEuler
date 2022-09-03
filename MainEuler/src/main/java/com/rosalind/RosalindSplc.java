package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.rosalind.aminoacids.CodonTranslation;
import com.rosalind.aminoacids.RnaTranscriber;
import com.rosalind.aminoacids.TranslatedCodon;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;
import com.rosalind.utils.ProteinUtils;

public class RosalindSplc {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_splc.txt";

	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()<=1) throw new IllegalArgumentException();
		FastaEntry result=ProteinUtils.removeIntrons(entries.get(0),entries.subList(1,entries.size()));
		RnaTranscriber transcriber=new RnaTranscriber(result.getContent());
		String rna=transcriber.getReverseRnaTranscription();
		List<TranslatedCodon> codons=CodonTranslation.translateUntilStop(rna,false);
		System.out.println(TranslatedCodon.toString(codons,true));
	}
}
