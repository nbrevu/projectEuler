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
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import com.euler.common.EulerUtils.Pair;

public class RosalindCstr {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_cstr.txt";
	
	private static abstract class CharacterTree	{
		public abstract int howManyChildren();
		public abstract List<Integer> getIndices();
		public abstract List<CharacterTree> getChildren();
		protected abstract Pair<List<Integer>,List<List<Integer>>> getIndicesForAllChildren();
		public final List<String> getAllStrings(int size)	{
			List<List<Integer>> lists=getIndicesForAllChildren().second;
			removeTrivial(lists,size);
			removeComplementary(lists,size);
			List<String> result=new ArrayList<>();
			for (List<Integer> list:lists)	{
				if ((list.size()<=1)||(list.size()>=size-1)) continue;
				result.add(getString(list,size));
			}
			return result;
		}
		private void removeTrivial(List<List<Integer>> lists,int size)	{
			lists.removeIf(new Predicate<List<Integer>>()	{
				@Override
				public boolean test(List<Integer> list) {
					return (list.size()<=1)||(list.size()>=size-1);
				}
			});
		}
		private boolean areComplementary(List<Integer> l1,List<Integer> l2,int size)	{
			if (l1.size()+l2.size()!=size) return false;
			Set<Integer> s1=new HashSet<>(l1);
			s1.retainAll(l2);
			return s1.isEmpty();
		}
		private void removeComplementary(List<List<Integer>> lists,int size)	{
			for (int i=0;i<lists.size()-1;++i)	{
				List<Integer> l1=lists.get(i);
				for (int j=i+1;j<lists.size();++j)	{
					List<Integer> l2=lists.get(j);
					if (areComplementary(l1,l2,size))	{
						lists.remove(j);
						break;
					}
				}
			}
		}
		private String getString(List<Integer> list,int size)	{
			// "list" is ordered so we can take shortcuts and avoid calls to contains().
			StringBuilder result=new StringBuilder();
			int nextToWrite=0;
			int index=0;
			while (index<list.size())	{
				int value=list.get(index);
				for (int i=nextToWrite;i<value;++i) result.append('0');
				result.append('1');
				++index;
				nextToWrite=value+1;
			}
			for (int i=nextToWrite;i<size;++i) result.append('0');
			return result.toString();
		}
	}
	
	private static class IntermediateNode extends CharacterTree	{
		private final CharacterTree child1;
		private final CharacterTree child2;
		private final List<CharacterTree> children;
		public IntermediateNode(CharacterTree child1,CharacterTree child2)	{
			this.child1=child1;
			this.child2=child2;
			children=Arrays.asList(child1,child2);
		}
		@Override
		public int howManyChildren()	{
			int result=0;
			for (CharacterTree c:children) result+=c.howManyChildren();
			return result;
		}
		@Override
		public List<Integer> getIndices()	{
			List<Integer> result=new ArrayList<>();
			for (CharacterTree c:children) result.addAll(c.getIndices());
			return result;
		}
		@Override
		public List<CharacterTree> getChildren()	{
			return children;
		}
		@Override
		protected Pair<List<Integer>, List<List<Integer>>> getIndicesForAllChildren() {
			Pair<List<Integer>, List<List<Integer>>> list1=child1.getIndicesForAllChildren();
			Pair<List<Integer>, List<List<Integer>>> list2=child2.getIndicesForAllChildren();
			List<Integer> myList=new ArrayList<>();
			myList.addAll(list1.first);
			myList.addAll(list2.first);
			myList.sort(null);
			List<List<Integer>> myBigList=new ArrayList<>();
			myBigList.addAll(list1.second);
			myBigList.addAll(list2.second);
			myBigList.add(myList);
			return new Pair<>(myList,myBigList);
		}
	}
	
	private static class LeafNode extends CharacterTree	{
		private final List<Integer> indices;
		public LeafNode(List<Integer> indices)	{
			this.indices=indices;
		}
		@Override
		public int howManyChildren()	{
			return indices.size();
		}
		@Override
		public List<Integer> getIndices()	{
			return indices;
		}
		@Override
		public List<CharacterTree> getChildren()	{
			return Collections.emptyList();
		}
		@Override
		protected Pair<List<Integer>, List<List<Integer>>> getIndicesForAllChildren() {
			return new Pair<>(indices,Collections.singletonList(indices));
		}
	}
	
	private static NavigableMap<Integer,String> createIndexMap(List<String> strings)	{
		NavigableMap<Integer,String> result=new TreeMap<>();
		for (int i=0;i<strings.size();++i) result.put(i,strings.get(i));
		return result;
	}
	
	private static CharacterTree traverseStrings(NavigableMap<Integer,String> stringMap,int charPosition)	{
		if (stringMap.size()==1) return new LeafNode(Collections.singletonList(stringMap.firstKey()));
		else if (charPosition>=stringMap.firstEntry().getValue().length()) return new LeafNode(new ArrayList<>(stringMap.keySet()));
		int char1=0x0;
		int char2=0x0;
		NavigableMap<Integer,String> strs1=new TreeMap<>();
		NavigableMap<Integer,String> strs2=new TreeMap<>();
		for (Map.Entry<Integer,String> entry:stringMap.entrySet())	{
			String str=entry.getValue();
			char c=str.charAt(charPosition);
			int position;
			if (char1==0x0)	{
				char1=c;
				position=1;
			}	else if (char1==c) position=1;
			else if (char2==0x0)	{
				char2=c;
				position=2;
			}	else if (char2==c) position=2;
			else throw new IllegalArgumentException();
			((position==1)?strs1:strs2).put(entry.getKey(),str);
		}
		if (strs2.isEmpty()) return traverseStrings(stringMap,1+charPosition);
		else	{
			CharacterTree t1=traverseStrings(strs1,1+charPosition);
			CharacterTree t2=traverseStrings(strs2,1+charPosition);
			return new IntermediateNode(t1,t2);
		}
	}

	private static CharacterTree traverseStrings(List<String> strings)	{
		return traverseStrings(createIndexMap(strings),0);
	}
	
	// Mierda, está mal, 200 líneas para nada :(.
	public static void main(String[] args) throws IOException	{
		List<String> strings;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			strings=new ArrayList<>();
			for (;;)	{
				String line=reader.readLine();
				if (line==null) break;
				else strings.add(line);
			}
		}	catch (IOException exc)	{
			strings=Arrays.asList("ATGCTACC","CGTTTACC","ATTCGACC","AGTCTCCC","CGTCTATC");
		}
		CharacterTree tree=traverseStrings(strings);
		List<String> result=tree.getAllStrings(strings.size());
		System.out.println(result.size());
		for (String s:result) System.out.println(s);
	}
}
