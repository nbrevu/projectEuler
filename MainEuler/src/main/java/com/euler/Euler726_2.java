package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.collect.Lists;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Euler726_2 {
	private static class Node	{
		private Node parentLeft;
		private Node parentRight;
		private Node childLeft;
		private Node childRight;
		public Node()	{}
		public boolean isTop()	{
			return (parentLeft==null)&&(parentRight==null);
		}
		public ObjIntMap<Node> countPaths()	{
			if (isTop()) return HashObjIntMaps.newImmutableMap(new Node[] {this},new int[] {1});
			else if (parentLeft==null) return parentRight.countPaths();
			else if (parentRight==null) return parentLeft.countPaths();
			else return combine(parentLeft.countPaths(),parentRight.countPaths());
		}
		private static ObjIntMap<Node> combine(ObjIntMap<Node> left,ObjIntMap<Node> right)	{
			ObjIntMap<Node> result=HashObjIntMaps.newMutableMap(left);
			for (ObjIntCursor<Node> cursor=right.cursor();cursor.moveNext();) result.addValue(cursor.key(),cursor.value());
			return result;
		}
		public static ObjIntMap<Node> combine(List<ObjIntMap<Node>> partialMaps)	{
			if (partialMaps.isEmpty()) return HashObjIntMaps.newMutableMap();
			else if (partialMaps.size()==1) return partialMaps.get(0);
			ObjIntMap<Node> result=HashObjIntMaps.newMutableMap(partialMaps.get(0));
			for (int i=1;i<partialMaps.size();++i)	{
				ObjIntMap<Node> partialMap=partialMaps.get(i);
				for (ObjIntCursor<Node> cursor=partialMap.cursor();cursor.moveNext();) result.addValue(cursor.key(),cursor.value());
			}
			return result;
		}
		public void remove()	{
			if (childLeft!=null) childLeft.parentRight=null;
			if (childRight!=null) childRight.parentLeft=null;
		}
	}
	
	public static List<Node> buildStack(int layers)	{
		int size=(layers*(layers+1))/2;
		List<Node> result=new ArrayList<>(size);
		int prevStart=0;
		Node root=new Node();
		result.add(root);
		int prevEnd=1;
		for (int i=1;i<layers;++i)	{
			Node prevLeftmost=result.get(prevStart);
			Node leftmost=new Node();
			result.add(leftmost);
			leftmost.parentRight=prevLeftmost;
			prevLeftmost.childLeft=leftmost;
			for (int j=prevStart;j<prevEnd-1;++j)	{
				Node leftParent=result.get(j);
				Node rightParent=result.get(j+1);
				Node newNode=new Node();
				result.add(newNode);
				newNode.parentLeft=leftParent;
				newNode.parentRight=rightParent;
				leftParent.childRight=newNode;
				rightParent.childLeft=newNode;
			}
			Node prevRightmost=result.get(prevEnd-1);
			Node rightmost=new Node();
			result.add(rightmost);
			rightmost.parentLeft=prevRightmost;
			prevRightmost.childRight=rightmost;
			prevStart=prevEnd;
			prevEnd=result.size();
		}
		return result;
	}
	
	public static List<Node> duplicateAndRemove(List<Node> stack,Node toRemove)	{
		int index=stack.indexOf(toRemove);
		if (index==-1) throw new NoSuchElementException("JAJA SI.");
		int newSize=stack.size()-1;
		List<Node> result=new ArrayList<>(newSize);
		for (int i=0;i<newSize;++i) result.add(new Node());
		ObjIntMap<Node> objToIndex=HashObjIntMaps.newMutableMap();
		for (int i=0;i<index;++i) objToIndex.put(stack.get(i),i);
		for (int i=index+1;i<stack.size();++i) objToIndex.put(stack.get(i),i-1);
		for (int i=0;i<stack.size();++i) if (i!=index)	{
			Node original=stack.get(i);
			Node copied=result.get((i>index)?(i-1):i);
			{
				int newIndex=objToIndex.getOrDefault(original.childLeft,-1);
				if (newIndex!=-1) copied.childLeft=result.get(newIndex);
			}
			{
				int newIndex=objToIndex.getOrDefault(original.childRight,-1);
				if (newIndex!=-1) copied.childRight=result.get(newIndex);
			}
			{
				int newIndex=objToIndex.getOrDefault(original.parentLeft,-1);
				if (newIndex!=-1) copied.parentLeft=result.get(newIndex);
			}
			{
				int newIndex=objToIndex.getOrDefault(original.parentRight,-1);
				if (newIndex!=-1) copied.parentRight=result.get(newIndex);
			}
		}
		return result;
	}
	
	private static long fact(long in)	{
		return (in<=1)?in:(in*fact(in-1));
	}
	
	private static boolean isAllOnes(ObjIntMap<Node> map)	{
		for (ObjIntCursor<Node> cursor=map.cursor();cursor.moveNext();) if (cursor.value()!=1) return false;
		return true;
	}
	
	public static BigInteger simulate(List<Node> stack)	{
		ObjIntMap<Node> combinations=Node.combine(Lists.transform(stack,Node::countPaths));
		if (isAllOnes(combinations)) return BigInteger.valueOf(fact(combinations.size()));
		BigInteger result=BigInteger.ZERO;
		for (ObjIntCursor<Node> cursor=combinations.cursor();cursor.moveNext();) result=result.add(BigInteger.valueOf(cursor.value()).multiply(simulate(duplicateAndRemove(stack,cursor.key()))));
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		// f(4)=15240960=15*14*(2^7 * 3^4 * 7).
		// f(5)=56753067571200=31*30*(2^10 * 3^5 * 5 * 7^3 * 11 * 13)
		// f(6)=131226620504925044736000=2^18 * 3^10 * 5^3 * 7^5 * 13 * 17 * 19 * 31^2.
		System.out.println(simulate(buildStack(6)));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
