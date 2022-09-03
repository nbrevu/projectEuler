package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import com.rosalind.utils.ProteinUtils;

public class RosalindDbru {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_dbru.txt";
	
	private final static String OUT_FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\OutFiles\\rosalind_dbru.txt";
	
	private static void addStringToGraph(SetMultimap<String,String> graph,String in)	{
		graph.put(in.substring(0,in.length()-1),in.substring(1));
	}
	
	private static void addStringAndComplementToGraph(SetMultimap<String,String> graph,String in)	{
		addStringToGraph(graph,in);
		addStringToGraph(graph,ProteinUtils.getComplement(in));
	}
	
	public static void main(String[] args) throws IOException	{
		List<String> strings=new ArrayList<>();
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			for (;;)	{
				String str=reader.readLine();
				if (str==null) break;
				else strings.add(str);
			}
		}
		SortedSetMultimap<String,String> graph=TreeMultimap.create();
		for (String in:strings) addStringAndComplementToGraph(graph,in);
		try (PrintStream out=new PrintStream(OUT_FILE))	{
			for (Map.Entry<String,String> entry:graph.entries())	{
				String edge="("+entry.getKey()+", "+entry.getValue()+")";
				out.println(edge);
			}
		}
	}
}
