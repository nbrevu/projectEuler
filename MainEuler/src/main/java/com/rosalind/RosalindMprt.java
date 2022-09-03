package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rosalind.utils.FastaReader.FastaEntry;
import com.rosalind.utils.UniprotReader;

public class RosalindMprt {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_mprt.txt";
	private final static Pattern N_GLYCOSILATION_MOTIF=Pattern.compile("N[^P][ST][^P]");
	
	private static List<Integer> getNGlycosilationMotifPositions(FastaEntry entry)	{
		List<Integer> positions=new ArrayList<>();
		Matcher matcher=N_GLYCOSILATION_MOTIF.matcher(entry.getContent());
		int pos=0;
		while (matcher.find(pos))	{
			pos=matcher.start()+1;
			positions.add(pos);
		}
		return positions;
	}
	
	private static List<Integer> getNGlycosilationMotifPositions(String accessId) throws IOException,MalformedURLException	{
		List<FastaEntry> entries=UniprotReader.readFromAccessId(accessId);
		if (entries.size()!=1) throw new IllegalStateException();
		return getNGlycosilationMotifPositions(entries.get(0));
	}
	
	public static void main(String[] args) throws IOException,MalformedURLException	{
		List<String> accessIds=new ArrayList<>();
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			for (;;)	{
				String line=reader.readLine();
				if (line==null) break;
				accessIds.add(line);
			}
		}
		for (String accessId:accessIds)	{
			List<Integer> positions=getNGlycosilationMotifPositions(accessId);
			if (!positions.isEmpty())	{
				System.out.println(accessId);
				System.out.print(positions.get(0));
				for (int i=1;i<positions.size();++i) System.out.print(" "+positions.get(i));
				System.out.println();
			}
		}
	}
}
