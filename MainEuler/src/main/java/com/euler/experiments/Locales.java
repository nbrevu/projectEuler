package com.euler.experiments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Locales {
	// From https://www.ssec.wisc.edu/~tomw/java/unicode.html.
	private final static String FILE="F:\\Unicode Chart.html";
	private final static List<String> MARKERS=List.of("DIGIT","NUMBER","NUMERAL");
	private final static Set<Boolean> ALL_TRUE=Set.of(Boolean.TRUE);
	private final static Set<Boolean> ALL_FALSE=Set.of(Boolean.FALSE);
	
	private static class CharData	{
		public final String id;
		public final String character;
		public CharData(String id,String character) {
			this.id=id;
			this.character=character;
		}
	}
	
	private static List<CharData> scrape(String pageContents)	{
		String patternString="<tr><td>.*</td><td>.*</td><td>(.*)</td><td><font .*>(.*)</font></td></tr>";
		Matcher matcher=Pattern.compile(patternString).matcher(pageContents);
		List<CharData> result=new ArrayList<>();
		while (matcher.find()) result.add(new CharData(matcher.group(1),matcher.group(2)));
		return result;
	}
	
	private static boolean isPossiblyNumber(String id)	{
		for (String m:MARKERS) if (id.contains(m)) return true;
		return false;
	}
	
	private static boolean canParse(CharData data)	{
		try	{
			Integer.parseInt(data.character);
			return true;
		}	catch (NumberFormatException nfe)	{
			return false;
		}
	}

	private static Table<Locale,CharData,Boolean> tryParseAll(Locale[] allLocales,List<CharData> filteredCharacters) {
		Table<Locale,CharData,Boolean> result=HashBasedTable.create();
		for (Locale l:allLocales)	{
			Locale.setDefault(l);
			for (CharData c:filteredCharacters) result.put(l,c,canParse(c));
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		Locale[] allLocales=Locale.getAvailableLocales();
		System.out.println("Found "+allLocales.length+" locales.");
		String fileContents=Files.readString(Paths.get(FILE));
		List<CharData> allCharacters=scrape(fileContents);
		List<CharData> filteredCharacters=new ArrayList<>();
		for (CharData c:scrape(fileContents)) if (isPossiblyNumber(c.id)) filteredCharacters.add(c);
		System.out.println("Found "+filteredCharacters.size()+" possible numbers out of "+allCharacters.size()+" unicode characters.");
		Table<Locale,CharData,Boolean> parseResults=tryParseAll(allLocales,filteredCharacters);
		List<CharData> parseableEverywhere=new ArrayList<>();
		List<CharData> parseableNowhere=new ArrayList<>();
		List<Map.Entry<CharData,Map<Locale,Boolean>>> parseableSomewhere=new ArrayList<>();
		for (Map.Entry<CharData,Map<Locale,Boolean>> entry:parseResults.columnMap().entrySet())	{
			Set<Boolean> validResults=new HashSet<>(entry.getValue().values());
			if (ALL_TRUE.equals(validResults)) parseableEverywhere.add(entry.getKey());
			else if (ALL_FALSE.equals(validResults)) parseableNowhere.add(entry.getKey());
			else parseableSomewhere.add(entry);
		}
		System.out.println("Found "+parseableEverywhere.size()+" parseable characters.");
		System.out.println("Found "+parseableNowhere.size()+" non-parseable characters.");
		System.out.println("Found "+parseableSomewhere.size()+" interesting cases.");
		for (CharData c:parseableEverywhere)	{
			int parsed=Integer.parseInt(c.character);
			System.out.println(String.format("Character %s (%s) is parsed as number %d.",c.id,c.character,parsed));
		}
		System.out.println("List of characters that could have been parseable, but no:");
		for (CharData c:parseableNowhere) System.out.println(String.format("%s (%s)",c.id,c.character));
	}
}
