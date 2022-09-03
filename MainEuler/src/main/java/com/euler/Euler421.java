package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler421 {
	public static void main(String[] args)	{
		long limit1=100l;
		long limit2=1000l;
		for (long p:Primes.listLongPrimes(limit1))	{
			List<Long> casesFound=new ArrayList<>();
			for (long i=1;i<=limit2;++i) if (EulerUtils.expMod(i,15,p)==p-1) casesFound.add(i);
			String numberList=casesFound.stream().map(Object::toString).collect(Collectors.joining(","));
			System.out.println("Cases for p="+p+": "+numberList+".");
		}
	}
}
