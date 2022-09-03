package com.euler.experiments;

public class Abcba {
	public static void main(String[] args)	{
		int verified=0;
		int total=0;
		for (int a=1;a<10;++a)	{
			int a000a=a*10001;
			for (int b=0;b<10;++b)	{
				int ab0ba=a000a+1010*b;
				for (int c=0;c<10;++c)	{
					int abcba=ab0ba+100*c;
					boolean cond1=((a+b)==c);
					boolean cond2=((abcba%37)==0);
					++total;
					if (cond1&&cond2) ++verified;
					else if (cond1) System.out.println("ACHTUNG! Contraejemplo, A & !B.");
					else if (cond2) System.out.println("ACHTUNG! Contraejemplo, B & !A.");
				}
			}
		}
		System.out.println("Encontrados "+total+" casos, de los cuales "+verified+" verifican las condiciones.");
	}
}
