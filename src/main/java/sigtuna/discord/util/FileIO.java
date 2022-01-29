package sigtuna.discord.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileIO extends File {

	public FileIO(String pathname) {
		super(pathname);
	}
	
	private static final long serialVersionUID = 1L;
	
	public Scanner getScanner() throws FileNotFoundException {
		Scanner cin = new Scanner(this);
		return cin;
	}
	
	public PrintWriter writer() throws FileNotFoundException {
		File file = this;
		PrintWriter writer = new PrintWriter(file);
		return writer;
	}
	
}
