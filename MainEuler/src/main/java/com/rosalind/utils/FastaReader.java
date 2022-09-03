package com.rosalind.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class FastaReader {
	public static class FastaEntry	{
		private final String id;
		private final String content;
		public FastaEntry(String id,String content)	{
			this.id=id;
			this.content=content;
		}
		public String getId()	{
			return id;
		}
		public String getContent()	{
			return content;
		}
	}
	
	private Supplier<BufferedReader> inputSource;
	private String currentName=null;
	private StringBuilder currentContent=null;
	private final List<FastaEntry> result=new ArrayList<>();
	
	private void parseLine(String line)	{
		if (line.startsWith(">"))	{
			writeIfNeeded();
			currentName=line.substring(1);
			currentContent=new StringBuilder();
		}	else currentContent.append(line);
	}
	
	private void writeIfNeeded()	{
		if (currentName!=null) write();
	}
	
	private void write()	{
		result.add(new FastaEntry(currentName,currentContent.toString()));
	}
	
	public FastaReader(Path fileIn)	{
		inputSource=new Supplier<BufferedReader>()	{
			@Override
			public BufferedReader get() {
				try {
					return Files.newBufferedReader(fileIn);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	public FastaReader(URL urlIn)	{
		inputSource=new Supplier<BufferedReader>()	{
			@Override
			public BufferedReader get()	{
				try {
					return new BufferedReader(new InputStreamReader(urlIn.openStream()));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	public void read() throws IOException	{
		try (BufferedReader br=inputSource.get())	{
			for (;;)	{
				String line=br.readLine();
				if (line==null) break;
				else parseLine(line);
			}
			write();
		}
	}
	
	public List<FastaEntry> getEntries()	{
		return Collections.unmodifiableList(result);
	}
}
