package com.euler.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public interface ProgramState {
	public void saveToFile(BufferedWriter writer) throws IOException;
	
	public void readStateFromFile(BufferedReader reader) throws IOException;
}
