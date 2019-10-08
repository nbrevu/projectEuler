package com.euler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

import com.euler.common.Timing;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler107 {
	private static class Edge implements Comparable<Edge>	{
		public final int source;
		public final int sink;
		public final int cost;
		public Edge(int source,int sink,int cost)	{
			this.source=source;
			this.sink=sink;
			this.cost=cost;
		}
		@Override
		public int compareTo(Edge o) {
			int result=cost-o.cost;
			if (result!=0) return result;
			result=source-o.source;
			return (result!=0)?result:(sink-o.sink);
		}
	}
	
	private static class Graph	{
		private final IntObjMap<IntSet> connectedComponents;
		public Graph(int size)	{
			connectedComponents=HashIntObjMaps.newMutableMap();
			for (int i=0;i<size;++i) connectedComponents.put(i,HashIntSets.newImmutableSetOf(i));
		}
		public boolean isConnected(int source,int sink)	{
			return connectedComponents.get(source).contains(sink);
		}
		public void connect(int source,int sink)	{
			IntSet newSet=HashIntSets.newMutableSet();
			connectedComponents.get(source).forEach((IntConsumer)newSet::add);
			connectedComponents.get(sink).forEach((IntConsumer)newSet::add);
			for (IntCursor cursor=newSet.cursor();cursor.moveNext();) connectedComponents.put(cursor.elem(),newSet);
		}
	}
	
	private static NavigableSet<Edge> getOrderedEdges(List<String> lines)	{
		int N=lines.size();
		NavigableSet<Edge> result=new TreeSet<>();
		for (int i=1;i<N;++i)	{
			String[] split=lines.get(i).split(",");
			for (int j=0;j<i;++j) if (!split[j].equals("-")) result.add(new Edge(j,i,Integer.parseInt(split[j])));
		}
		return result;
	}
	
	private static long solve()	{
		try	{
			URL resource=Euler107.class.getResource("in107.txt");
			List<String> lines=Files.lines(Paths.get(resource.toURI())).collect(Collectors.toList());
			NavigableSet<Edge> edges=getOrderedEdges(lines);
			int reducedCost=0;
			Graph graph=new Graph(lines.size());
			for (Edge edge:edges) if (!graph.isConnected(edge.source,edge.sink)) graph.connect(edge.source,edge.sink);
			else reducedCost+=edge.cost;
			return reducedCost;
		}	catch (IOException|URISyntaxException exc)	{
			throw new RuntimeException(exc);
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler107::solve);
	}
}
