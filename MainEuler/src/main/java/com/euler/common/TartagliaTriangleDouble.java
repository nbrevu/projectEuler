package com.euler.common;

import java.util.ArrayList;

public class TartagliaTriangleDouble {
	private ArrayList<double[]> rowCache;
	public TartagliaTriangleDouble()	{
		this(15);
	}
	public TartagliaTriangleDouble(int precalculate)	{
		rowCache=new ArrayList<>(1+precalculate);
		rowCache.add(new double[] {1});
		computeRows(precalculate);
	}
	private void computeRows(int howMany)	{
		rowCache.ensureCapacity(rowCache.size()+howMany);
		for (int i=0;i<howMany;++i) rowCache.add(getNextRow());
	}
	private double[] getNextRow()	{
		double[] lastRow=rowCache.get(rowCache.size()-1);
		double[] newRow=new double[1+lastRow.length];
		newRow[0]=1;
		for (int i=1;i<lastRow.length;++i)	{
			newRow[i]=lastRow[i]+lastRow[i-1];
			if (Double.isInfinite(newRow[i])) throw new IllegalStateException("Error computing row "+rowCache.size()+". Value is too big.");
		}
		newRow[lastRow.length]=1;
		return newRow;
	}
	private void ensureRowExists(int rowNum)	{
		int diff=rowNum+1-rowCache.size();
		if (diff>0) computeRows(diff);
	}
	public double getValue(int row,int column)	{
		ensureRowExists(row);
		return rowCache.get(row)[column];
	}
}
