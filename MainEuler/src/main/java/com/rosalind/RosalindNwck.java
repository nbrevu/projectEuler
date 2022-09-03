package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.rosalind.newick.NewickTree;
import com.rosalind.utils.IoUtils;

import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;

public class RosalindNwck {
	// ALELUYA, ESTA LIBRERÍA FUNCIONA. Gracias, señor cjb, propietario de https://github.com/cjb/libnewicktree.
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_nwck.txt";
	
	private static interface Solution	{
		public Solution combine(Solution other);
		public void oneMoreLevel();
		public static Solution combine(Collection<Solution> solutions)	{
			Solution result=NoSolution.INSTANCE;
			for (Solution s:solutions) result=result.combine(s);
			return result;
		}
	}
	
	private enum NoSolution implements Solution	{
		INSTANCE;
		@Override
		public Solution combine(Solution other)	{
			return other;
		}
		@Override
		public void oneMoreLevel()	{}
	}
	
	private static class FullSolution implements Solution	{
		public final int distance;
		public FullSolution(int distance)	{
			this.distance=distance;
		}
		@Override
		public Solution combine(Solution other)	{
			return this;
		}
		@Override
		public void oneMoreLevel()	{}
	}
	
	private static class SingleFound implements Solution	{
		public final String found;
		public int distance;
		public SingleFound(String found)	{
			this.found=found;
			distance=0;
		}
		@Override
		public Solution combine(Solution other)	{
			if (other instanceof NoSolution) return this;
			else if (other instanceof FullSolution) return other;
			SingleFound sf=(SingleFound)other;
			if (sf.found.equals(found)) throw new RuntimeException();
			else return new FullSolution(distance+sf.distance);
		}
		@Override
		public void oneMoreLevel()	{
			++distance;
		}
	}
	
	private static class ProblemInput	{
		public final Tree tree;
		public final String node1;
		public final String node2;
		public ProblemInput(Tree tree,String node1,String node2)	{
			this.tree=tree;
			this.node1=node1;
			this.node2=node2;
		}
		public int solve()	{
			Solution sol=solveRecursive(tree.getRoot());
			if (sol instanceof FullSolution) return ((FullSolution)sol).distance;
			else throw new RuntimeException();
		}
		private Solution solveRecursive(TreeNode node)	{
			List<Solution> solutions=new ArrayList<>();
			if (node1.equals(node.label)) solutions.add(new SingleFound(node1));
			else if (node2.equals(node.label)) solutions.add(new SingleFound(node2));
			for (int i=0;i<node.numberChildren();++i)	{
				Solution partial=solveRecursive(node.getChild(i));
				if (partial instanceof FullSolution) return partial;
				else solutions.add(partial);
			}
			Solution res=Solution.combine(solutions);
			res.oneMoreLevel();
			return res;
		}
	}

	public static void main(String[] args) throws IOException	{
		List<ProblemInput> problems;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			problems=new ArrayList<>();
			for (;;)	{
				String line1=reader.readLine();
				if (line1==null) break;
				Tree tree=NewickTree.readString(line1);
				String line2=reader.readLine();
				String[] nodes=line2.split(" ");
				assert nodes.length==2;
				problems.add(new ProblemInput(tree,nodes[0],nodes[1]));
				if (reader.readLine()==null) break;
			}
		}
		int[] result=new int[problems.size()];
		for (int i=0;i<problems.size();++i) result[i]=problems.get(i).solve();
		System.out.println(IoUtils.toStringWithSpaces(result));
	}
}
