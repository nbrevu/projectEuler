package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RosalindTrie {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_trie.txt";
	
	private static class Trie	{
		private List<Map<Character,Integer>> edges;
		public Trie()	{
			edges=new ArrayList<>();
			edges.add(new HashMap<>());
		}
		public void addString(String in)	{
			int node=0;
			for (char symbol:in.toCharArray()) node=addChar(node,symbol);
		}
		private int addChar(int currentNode,char symbol)	{
			Map<Character,Integer> result=edges.get(currentNode);
			Integer newNode=result.get(symbol);
			if (newNode==null)	{
				int N=edges.size();
				edges.add(new HashMap<>());
				result.put(symbol,N);
				return N;
			}	else return newNode;
		}
		public List<String> getEdgesAsStrings()	{
			List<String> result=new ArrayList<>();
			for (int i=0;i<edges.size();++i) for (Map.Entry<Character,Integer> entry:edges.get(i).entrySet()) result.add(toString(i,entry));
			return result;
		}
		private String toString(int i,Map.Entry<Character,Integer> entry)	{
			return (i+1)+" "+(entry.getValue()+1)+" "+entry.getKey();
		}
	}
	
	public static void main(String[] args) throws IOException	{
		List<String> strings=new ArrayList<>();
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			for (;;)	{
				String line=reader.readLine();
				if (line==null) break;
				else strings.add(line);
			}
		}
		Trie trie=new Trie();
		for (String in:strings) trie.addString(in);
		for (String edge:trie.getEdgesAsStrings()) System.out.println(edge);
	}
}
