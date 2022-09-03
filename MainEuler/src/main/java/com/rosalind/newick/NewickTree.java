package com.rosalind.newick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;

public class NewickTree {
	// Esto no funciona :(. Usar otra librer�a...	
	public static Tree readFile(Path file) throws IOException	{
		try (BufferedReader reader=Files.newBufferedReader(file))	{
			return readBufferedReader(reader);
		}
	}
	
	public static Tree readBufferedReader(BufferedReader reader)	{
		TreeParser tp=new TreeParser(reader);
		return tp.tokenize("");
		//return tp.tokenize(1, "", null);
	}
	
	public static Tree readString(String in)	{
		return readBufferedReader(new BufferedReader(new StringReader(in)));
	}
}
