package com.rosalind.newick;

import java.util.NavigableSet;
import java.util.TreeSet;

import com.google.common.base.Strings;

import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;

public class NewickUtils {
	public static NavigableSet<String> getAllLabels(Tree in)	{
		NavigableSet<String> result=new TreeSet<>();
		getAllLabelsRecursive(result,in.getRoot());
		return result;
	}
	
	private static void getAllLabelsRecursive(NavigableSet<String> result,TreeNode node)	{
		if (!Strings.isNullOrEmpty(node.label)) result.add(node.label);
		for (int i=0;i<node.numberChildren();++i) getAllLabelsRecursive(result,node.getChild(i));
	}
}
