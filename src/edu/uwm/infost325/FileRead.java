package edu.uwm.infost325;
import java.io.*;

public class FileRead {
	public static void main(String args[])throws IOException{
		 File file = new File("this.txt");
	

		file.createNewFile();
		
		FileWriter writer = new FileWriter(file);
		
		writer.write("This is totally a hashing algorithm");
		writer.flush();
		writer.close();
		
		
		FileReader harrumph = new FileReader(file);
		char[] a = new char[50];
		harrumph.read(a);
		
		for(char huh: a)
			System.out.print(huh);
		harrumph.close();
}
}
