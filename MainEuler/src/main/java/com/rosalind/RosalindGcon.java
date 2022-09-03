package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.google.common.collect.ImmutableMap;
import com.rosalind.aminoacids.AminoacidData;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindGcon {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_gcon.txt";
	private final static int GAP_PENALTY=-5;
	
	private static class ScoreData	{
		// The key in this map represents whether we are inside a gap or not.
		public final Map<Boolean,Integer> scores;
		private ScoreData(Map<Boolean,Integer> scores)	{
			this.scores=scores;
		}
		private ScoreData(boolean isGap,int score)	{
			this(ImmutableMap.of(isGap,score));
		}
		public ScoreData()	{
			this(false,0);
		}
		public ScoreData addGap(int gapPenalty)	{
			if (scores.size()==1)	{
				if (scores.containsKey(true)) return this;
				else return new ScoreData(true,gapPenalty+scores.get(false));
			}
			int fValue=scores.get(false)+gapPenalty;
			int tValue=scores.get(true);
			return new ScoreData(true,Math.max(fValue,tValue));
		}
		public ScoreData addScore(int added)	{
			return new ScoreData(false,added+maxValue(new ArrayList<>(scores.values())));
		}
		public int getMaxValue()	{
			if (scores.size()==1) return scores.values().iterator().next();
			else return Math.max(scores.get(false),scores.get(true));
		}
		public static ScoreData combine(Collection<ScoreData> scores)	{
			List<Integer> falseValues=new ArrayList<>();
			List<Integer> trueValues=new ArrayList<>();
			for (ScoreData score:scores)	{
				if (score.scores.containsKey(false)) falseValues.add(score.scores.get(false));
				if (score.scores.containsKey(true)) trueValues.add(score.scores.get(true));
			}
			Map<Boolean,Integer> result=new HashMap<>();
			if (!falseValues.isEmpty()) result.put(false,maxValue(falseValues));
			if (!trueValues.isEmpty()) result.put(true,maxValue(trueValues));
			return new ScoreData(result);
		}
		private static Integer maxValue(List<Integer> l)	{
			Integer result=l.get(0);
			for (int i=1;i<l.size();++i) if (l.get(i)>result) result=l.get(i);
			return result;
		}
		@Override
		public String toString()	{
			return scores.toString();
		}
	}
	
	private static ScoreData[][] computeModifiedLevenshteinArray(String s1,String s2,int gapPenalty,BiFunction<Character,Character,Integer> fun)	{
		int N=s1.length();
		int M=s2.length();
		ScoreData[][] d=new ScoreData[N+1][M+1];
		d[0][0]=new ScoreData();
		for (int i=1;i<=N;++i) d[i][0]=d[i-1][0].addGap(gapPenalty);
		for (int j=1;j<=M;++j) d[0][j]=d[0][j-1].addGap(gapPenalty);
		for (int i=1;i<=N;++i) for (int j=1;j<=M;++j)	{
			ScoreData sc1=d[i-1][j].addGap(gapPenalty);
			ScoreData sc2=d[i][j-1].addGap(gapPenalty);
			ScoreData sc3=d[i-1][j-1].addScore(fun.apply(s1.charAt(i-1),s2.charAt(j-1)));
			d[i][j]=ScoreData.combine(Arrays.asList(sc1,sc2,sc3));
		}
		return d;
	}
	
	private static int getModifiedLevenshteinDistance(String s1,String s2,int gapPenalty,BiFunction<Character,Character,Integer> fun)	{
		ScoreData[][] array=computeModifiedLevenshteinArray(s1,s2,gapPenalty,fun);
		return array[s1.length()][s2.length()].getMaxValue();
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		assert entries.size()==2;
		int result=getModifiedLevenshteinDistance(entries.get(0).getContent(),entries.get(1).getContent(),GAP_PENALTY,(Character c1,Character c2)->AminoacidData.getBlosumScore(c1,c2));
		System.out.println(result);
	}
}
