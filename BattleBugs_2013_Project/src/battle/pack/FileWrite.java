package battle.pack;

import java.io.*;
public class FileWrite 
{
	private String filename;
	public FileWrite() {
		this.filename="out.txt";
	}
	public FileWrite(String filename) {
		this.filename=filename;
	}
	public void write(String s, boolean append) {
		try{
			// Create file 
			FileWriter fstream = new FileWriter(filename,append);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(s);
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}