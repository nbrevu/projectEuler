package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.rosalind.newick.NewickTree;

import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;

public class RosalindMend {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_mend.txt";
	
	private static class Probabilities	{
		private final double AA,Aa,aa;
		private Probabilities(double AA,double Aa,double aa)	{
			this.AA=AA;
			this.Aa=Aa;
			this.aa=aa;
		}
		public static Probabilities getFromNode(TreeNode node)	{
			if ("AA".equals(node.label)) return new Probabilities(1.0,0.0,0.0);
			else if (("Aa".equals(node.label))||("aA".equals(node.label))) return new Probabilities(0.0,1.0,0.0);
			else if ("aa".equals(node.label)) return new Probabilities(0.0,0.0,1.0);
			else if (node.numberChildren()==2)	{
				Probabilities p1=getFromNode(node.getChild(0));
				Probabilities p2=getFromNode(node.getChild(1));
				return p1.combine(p2);
			}	else throw new RuntimeException(Integer.toString(node.numberChildren())+", "+node.label);
		}
		public Probabilities combine(Probabilities other)	{
			/*
			 * AA*AA -> 100% AA
			 * AA*Aa -> 50% AA, 50% Aa
			 * AA*aa -> 100% Aa
			 * Aa*AA -> 50% AA, 50% Aa
			 * Aa*Aa -> 25% AA, 50% Aa, 25% aa
			 * Aa*aa -> 50% Aa, 50% aa
			 * aa*AA -> 100% Aa
			 * aa*Aa -> 50% Aa, 50% aa
			 * aa*aa -> 100% aa
			 */
			double AAAA=AA*other.AA;
			double AAAa=AA*other.Aa;
			double AAaa=AA*other.aa;
			double AaAA=Aa*other.AA;
			double AaAa=Aa*other.Aa;
			double Aaaa=Aa*other.aa;
			double aaAA=aa*other.AA;
			double aaAa=aa*other.Aa;
			double aaaa=aa*other.aa;
			double newAA=AAAA+0.5*AAAa+0.5*AaAA+0.25*AaAa;
			double newAa=0.5*AAAa+AAaa+0.5*AaAA+0.5*AaAa+0.5*Aaaa+aaAA+0.5*aaAa;
			double newaa=0.25*AaAa+0.5*Aaaa+0.5*aaAa+aaaa;
			return new Probabilities(newAA,newAa,newaa);
		}
	}
	public static void main(String[] args) throws IOException	{
		Tree tree;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			tree=NewickTree.readString(reader.readLine());
		}
		Probabilities p=Probabilities.getFromNode(tree.getRoot());
		System.out.println(p.AA+" "+p.Aa+" "+p.aa);
	}
}
