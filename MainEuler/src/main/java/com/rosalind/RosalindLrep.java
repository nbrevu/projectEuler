package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class RosalindLrep {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_lrep.txt";
	
	// Algo estoy haciendo mal, y no sé qué es :(.
	private static class NodeInfo	{
		public final String sourceNode;
		public final String targetNode;
		public final int strOrigin;
		public final int strLen;
		public NodeInfo(String sourceNode,String targetNode,int strOrigin,int strLen)	{
			this.sourceNode=sourceNode;
			this.targetNode=targetNode;
			this.strOrigin=strOrigin;
			this.strLen=strLen;
		}
	}
	
	private static class Graph	{
		private static class Node	{
			public final String name;
			public final Map<String,Node> children;
			public Node(String name)	{
				this.name=name;
				children=new HashMap<>();
			}
			public void addChild(String str,Node node)	{
				children.put(str,node);
			}
			@Override
			public String toString()	{
				return "["+name+", "+children+"]";
			}
		}
		private final String idStr;
		private final Map<String,Node> nodes;
		private final ListMultimap<String,String> strings;
		public Graph(String idStr)	{
			this.idStr=idStr;
			nodes=new HashMap<>();
			strings=ArrayListMultimap.create();
		}
		public void addNode(NodeInfo info)	{
			int origin=info.strOrigin-1;
			int end=origin+info.strLen;
			String substring=idStr.substring(origin,end);
			Node source=getNode(info.sourceNode);
			Node target=getNode(info.targetNode);
			source.addChild(substring,target);
		}
		private Node getNode(String name)	{
			Node result=nodes.get(name);
			if (result==null)	{
				result=new Node(name);
				nodes.put(name,result);
			}
			return result;
		}
		public Map<String,Integer> getCuratedStringsFor(String node)	{
			if (strings.isEmpty()) fillMultimap();
			Map<String,Integer> result=new HashMap<>();
			for (String str:strings.get(node)) if (str.endsWith("$")) EulerUtils.increaseCounter(result,str.substring(0,str.length()-1));
			return result;
		}
		private void fillMultimap()	{
			for (Node node:nodes.values()) if (!strings.containsKey(node.name)) fillMultimapFor(node.name);
		}
		private void fillMultimapFor(String node)	{
			List<String> newStrings=new ArrayList<>();
			newStrings.add("");
			Node theNode=nodes.get(node);
			for (Map.Entry<String,Node> entry:theNode.children.entrySet())	{
				String prefix=entry.getKey();
				Node childNode=entry.getValue();
				Collection<String> childStrings=getStringsFor(childNode.name);
				for (String str:childStrings) newStrings.add(prefix+str);
			}
			strings.putAll(node,newStrings);
		}
		private Collection<String> getStringsFor(String name)	{
			if (!strings.containsKey(name)) fillMultimapFor(name);
			return strings.get(name);
		}
	}
	
	private static String getFirstNode(List<NodeInfo> nodes)	{
		String result=null;
		for (NodeInfo node:nodes) if (result==null) result=node.sourceNode;
		else if (node.sourceNode.compareTo(result)<0) result=node.sourceNode;
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		String baseString;
		int minRepeat;
		List<NodeInfo> nodes=new ArrayList<>();
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			baseString=reader.readLine();
			minRepeat=Integer.parseInt(reader.readLine());
			for (;;)	{
				String line=reader.readLine();
				if (line==null) break;
				String[] split=line.split(" ");
				nodes.add(new NodeInfo(split[0],split[1],Integer.parseInt(split[2]),Integer.parseInt(split[3])));
			}
		}
		Graph graph=new Graph(baseString);
		for (NodeInfo node:nodes) graph.addNode(node);
		NavigableMap<Integer,String> repeatedStringsByLength=new TreeMap<>();
		for (Map.Entry<String,Integer> repeatedString:graph.getCuratedStringsFor(getFirstNode(nodes)).entrySet())	{
			String str=repeatedString.getKey();
			int times=repeatedString.getValue();
			if (times>=minRepeat) repeatedStringsByLength.put(str.length(),str);
		}
		System.out.println(repeatedStringsByLength.lastEntry().getValue());
	}
}
