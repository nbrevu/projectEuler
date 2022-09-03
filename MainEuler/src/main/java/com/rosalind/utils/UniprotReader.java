package com.rosalind.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.rosalind.utils.FastaReader.FastaEntry;

public class UniprotReader {
	public static URL getUrl(String accessId) throws MalformedURLException	{
		return new URL("https://www.uniprot.org/uniprot/"+accessId+".fasta");
	}
	
	public static List<FastaEntry> readFromAccessId(String accessId) throws IOException,MalformedURLException	{
		FastaReader reader=new FastaReader(getUrl(accessId));
		reader.read();
		return reader.getEntries();
	}
}
