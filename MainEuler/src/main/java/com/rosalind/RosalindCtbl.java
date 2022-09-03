package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;

import com.google.common.base.Strings;
import com.rosalind.newick.NewickTree;
import com.rosalind.newick.NewickUtils;

import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;

public class RosalindCtbl {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_ctbl.txt";
	
	private static class PhysicalCharacter	{
		private final Set<String> taxa;
		public PhysicalCharacter()	{
			taxa=new HashSet<>();
		}
		public void addTaxon(String taxon)	{
			taxa.add(taxon);
		}
		public boolean isTrivial(Collection<String> allLabels)	{
			int N=allLabels.size();
			int s=taxa.size();
			return (s==1)||(s==N-1);
		}
		public boolean isComplementary(PhysicalCharacter other,Collection<String> allLabels)	{
			if (taxa.size()+other.taxa.size()!=allLabels.size()) return false;
			else return areDisjoint(taxa,other.taxa);
		}
		private static boolean areDisjoint(Set<String> s1,Set<String> s2)	{
			Set<String> tmp=new HashSet<>(s1);
			tmp.retainAll(s2);
			return tmp.isEmpty();
		}
		@Override
		public String toString()	{
			return taxa.toString();
		}
		public String toString(NavigableSet<String> labels)	{
			StringBuilder sb=new StringBuilder();
			for (String s:labels) sb.append(taxa.contains(s)?'1':'0');
			return sb.toString();
		}
	}
	
	private static void getCharactersRecursive(TreeNode node,List<PhysicalCharacter> currentCharacters,List<PhysicalCharacter> allCharacters)	{
		if (!Strings.isNullOrEmpty(node.label)) for (PhysicalCharacter c:currentCharacters) c.addTaxon(node.label);
		for (int i=0;i<node.numberChildren();++i)	{
			List<PhysicalCharacter> newCharacters=new ArrayList<>(currentCharacters);
			PhysicalCharacter newCharacter=new PhysicalCharacter();
			newCharacters.add(newCharacter);
			allCharacters.add(newCharacter);
			getCharactersRecursive(node.getChild(i),newCharacters,allCharacters);
		}
	}
	
	private static List<PhysicalCharacter> getCharacters(Tree tree)	{
		List<PhysicalCharacter> result=new ArrayList<>();
		getCharactersRecursive(tree.getRoot(),Collections.emptyList(),result);
		return result;
	}
	
	private static void removeTrivial(List<PhysicalCharacter> in,Set<String> labels)	{
		List<PhysicalCharacter> toRemove=new ArrayList<>();
		for (PhysicalCharacter c:in) if (c.isTrivial(labels)) toRemove.add(c);
		in.removeAll(toRemove);
	}
	
	private static void removeComplementary(List<PhysicalCharacter> in,Set<String> labels)	{
		List<PhysicalCharacter> toRemove=new ArrayList<>();
		int N=in.size();
		for (int i=0;i<N-1;++i) for (int j=i+1;j<N;++j) if (in.get(i).isComplementary(in.get(j),labels)) toRemove.add(in.get(j));
		in.removeAll(toRemove);
	}
	
	private static List<PhysicalCharacter> getNonTrivialCharacters(Tree tree)	{
		List<PhysicalCharacter> result=getCharacters(tree);
		Set<String> labels=NewickUtils.getAllLabels(tree);
		removeTrivial(result,labels);
		removeComplementary(result,labels);
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		Tree tree;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			tree=NewickTree.readString(reader.readLine());
		}
		NavigableSet<String> labels=NewickUtils.getAllLabels(tree);
		List<PhysicalCharacter> nonTrivialCharacters=getNonTrivialCharacters(tree);
		for (PhysicalCharacter c:nonTrivialCharacters) System.out.println(c.toString(labels));
	}
}
