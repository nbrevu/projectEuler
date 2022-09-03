package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RosalindRoot {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_root.txt";
	
	/*
with open('rosalind_root.txt') as f:
    n = int(f.readline().strip())
    t = [0, 1]
    c = map(list, [[0] * (n + 1)] * (n + 1))
    for i in xrange(n + 1):
        for j in xrange(i + 1):
            if j == 0 or i == j:
                c[i][j] = 1
            else:
                c[i][j] = (c[i - 1][j - 1] + c[i - 1][j]) % 10 ** 7


    def getcnt(n, i):
        if i + i == n:
            return c[n][i] / 2
        else:
            return c[n][i]


    for i in xrange(2, n + 1):
        t.append(0)
        for fp in xrange(1, i / 2 + 1):
            t[i] = (t[i] + t[fp] * t[i - fp] * getcnt(i, fp)) % 10 ** 6
	print t[n] 
	 */

	public static void main(String[] args) throws IOException	{
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
		}
	}
}
