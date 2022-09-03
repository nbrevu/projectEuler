package com.other.layton.search;

import java.util.List;

public interface ProblemStatus<T,U extends ProblemStatus<T,U>> {
	public boolean isFinal();
	
	public List<T> availableMoves();
	
	public U move(T move);
}
