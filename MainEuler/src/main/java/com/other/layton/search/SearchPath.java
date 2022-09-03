package com.other.layton.search;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchPath<T,U extends ProblemStatus<T,U>> {
	public static class NextMove<T,U extends ProblemStatus<T,U>>	{
		public final T move;
		public final U status;
		public NextMove(T move,U status)	{
			this.move=move;
			this.status=status;
		}
	}
	private final U initial;
	private final List<NextMove<T,U>> moves;
	public SearchPath(U initial)	{
		this.initial=initial;
		moves=Collections.emptyList();
	}
	private SearchPath(U initial,List<NextMove<T,U>> moves)	{
		this.initial=initial;
		this.moves=moves;
	}
	public U getCurrentStatus()	{
		if (moves.isEmpty()) return initial;
		else return moves.get(moves.size()-1).status;
	}
	public SearchPath<T,U> move(T move)	{
		U status=getCurrentStatus();
		U newStatus=status.move(move);
		List<NextMove<T,U>> newList=new ArrayList<>(moves);
		newList.add(new NextMove<>(move,newStatus));
		return new SearchPath<>(initial,newList);
	}
	public void print(PrintStream ps)	{
		ps.println(initial.toString());
		for (NextMove<T,U> move:moves)	{
			ps.print("\t (");
			ps.print(move.move.toString());
			ps.print(") => ");
			ps.println(move.status.toString());
		}
	}
	@Override
	public String toString()	{
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try (PrintStream ps=new PrintStream(baos,true,"utf-8"))	{
			print(ps);
		}	catch (UnsupportedEncodingException uoe)	{
			throw new RuntimeException(uoe);
		}
		String content=new String(baos.toByteArray(),StandardCharsets.UTF_8);
		return content;
	}
}
