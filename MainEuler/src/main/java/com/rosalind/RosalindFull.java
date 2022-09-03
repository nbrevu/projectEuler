package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.rosalind.aminoacids.Aminoacid;
import com.rosalind.aminoacids.AminoacidData;

public class RosalindFull {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_full.txt";
	
	private static class Edge	{
		public final int source;
		public final int target;
		public final Aminoacid acid;
		public Edge(int source,int target,Aminoacid acid)	{
			this.source=source;
			this.target=target;
			this.acid=acid;
		}
		public Edge reverse()	{
			return new Edge(target,source,acid);
		}
	}
	
	private static class GraphPath	{
		private final List<Integer> nodes;
		private final List<Aminoacid> aminoacids;
		public GraphPath(Edge edge)	{
			nodes=Arrays.asList(edge.source,edge.target);
			aminoacids=Collections.singletonList(edge.acid);
		}
		private GraphPath(List<Integer> nodes,List<Aminoacid> aminoacids)	{
			this.nodes=nodes;
			this.aminoacids=aminoacids;
		}
		public GraphPath add(Edge edge)	{
			assert edge.source==nodes.get(nodes.size()-1);
			List<Integer> newNodes=new ArrayList<>(nodes);
			newNodes.add(edge.target);
			List<Aminoacid> newAminoacids=new ArrayList<>(aminoacids);
			newAminoacids.add(edge.acid);
			return new GraphPath(newNodes,newAminoacids);
		}
		public boolean isCompatible(GraphPath other)	{
			if (!aminoacids.equals(other.aminoacids)) return false;
			Set<Integer> edges=new HashSet<>(nodes);
			edges.retainAll(other.nodes);
			return edges.isEmpty();	// No common nodes between the two graphs.
		}
		public String getAminoacidsAsString()	{
			StringBuilder result=new StringBuilder();
			for (Aminoacid acid:aminoacids) result.append(acid.symbol);
			return result.toString();
		}
	}
	
	private static List<GraphPath> getAllPaths(GraphPath base,int target,int howManyMore,ListMultimap<Integer,Edge> graph)	{
		List<GraphPath> result=new ArrayList<>();
		for (Edge edge:graph.get(target))	{
			GraphPath extended=base.add(edge);
			if (howManyMore==1) result.add(extended);
			else result.addAll(getAllPaths(extended,edge.target,howManyMore-1,graph));
		}
		return result;
	}
	
	private static List<Edge> getAllEdges(List<Double> weights)	{
		List<Edge> result=new ArrayList<>();
		for (int i=0;i<weights.size()-1;++i) for (int j=i+1;j<weights.size();++j)	{
			Aminoacid acid=AminoacidData.getNearestAminoacid(weights.get(j)-weights.get(i),1e-4);
			if (acid!=null) result.add(new Edge(i,j,acid));
		}
		return result;
	}
	
	private static ListMultimap<Integer,Edge> createFullGraph(List<Edge> edges,boolean direct)	{
		ListMultimap<Integer,Edge> result=ArrayListMultimap.create();
		for (Edge edge:edges) if (direct) result.put(edge.source,edge);
		else result.put(edge.target,edge.reverse());
		return result;
	}
	
	private static List<GraphPath> getAllPaths(boolean direct,List<Edge> edges,int length)	{
		List<GraphPath> result=new ArrayList<>();
		ListMultimap<Integer,Edge> graph=createFullGraph(edges,direct);
		for (Edge edge:edges)	{
			Edge actingEdge=direct?edge:edge.reverse();
			result.addAll(getAllPaths(new GraphPath(actingEdge),actingEdge.target,length-1,graph));
		}
		return result;
	}
	
	private static String getResult(List<Double> weights)	{
		List<Edge> allEdges=getAllEdges(weights);
		int N=(weights.size()/2)-1;
		List<GraphPath> directGraph=getAllPaths(true,allEdges,N);
		List<GraphPath> reverseGraph=getAllPaths(false,allEdges,N);
		for (GraphPath p1:directGraph) for (GraphPath p2:reverseGraph) if (p1.isCompatible(p2)) return p1.getAminoacidsAsString();
		throw new IllegalArgumentException();
	}
	
	public static void main(String[] args) throws IOException	{
		// double totalWeight;	// Not actually used.
		List<Double> weights;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			// totalWeight=Double.parseDouble(reader.readLine());
			reader.readLine();
			weights=new ArrayList<>();
			for (;;)	{
				String line=reader.readLine();
				if (line==null) break;
				weights.add(Double.parseDouble(line));
			}
		}
		System.out.println(getResult(weights));
	}
}
