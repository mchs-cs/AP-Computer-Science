package battle.pack;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class FileCopy {
	public enum FileOperation{COPY,DELETE}
	public static void ExternalBugs(String textFile,FileOperation fileOp) {
		try {
			String bugName,dir;
			FileReader fr = new FileReader(textFile);
			BufferedReader fd = new BufferedReader(fr);
			while (true) {
				bugName = fd.readLine();				
				dir = fd.readLine();
				if (bugName==null || dir == null) break;
				if (dir.indexOf("//")==0) {
					dir=FileCopy.baseDir()+dir.substring(2);
				}
				if (fileOp==FileOperation.COPY) {
					FileCopy.copyFilenameFromTo(dir+"bin/battle/game/",FileCopy.baseDir()+"bin/battle/game/", bugName+"*.class", true, true);
					FileCopy.copyFilenameFromTo(dir+"src/battle/game/",FileCopy.homeDir()+"battleBug_src/", bugName+"*.java", true, true);
					FileCopy.copyFilenameFromTo(dir+"bin/battle/pack/",FileCopy.baseDir()+"bin/battle/pack/", "Bactor_"+bugName+".gif", true, true);
				} else if (fileOp==FileOperation.DELETE) {
					FileCopy.deleteFiles(getFiles(FileCopy.baseDir()+"bin/battle/game/",bugName+"*.class"), true);
				}
			}		
		} catch (FileNotFoundException fileNotFound) {
			System.out.println("\""+textFile+"\":File Not Found!");
		} catch (EOFException eof) {
		} catch (IOException ioe) {
			System.out.println("IO error: " + ioe);
		}
	}

	public static void copyExternalBugs() {
		try {
			String bugName,dir;
			FileReader fr = new FileReader("externalBugsDirectories.txt");
			BufferedReader fd = new BufferedReader(fr);
			while (true) {
				bugName = fd.readLine();				
				dir = fd.readLine();
				if (bugName==null || dir == null) break;
				FileCopy.copyFilenameFromTo(dir+"bin/battle/game/",FileCopy.baseDir()+"bin/battle/game/", bugName+"*.class", true, true);
				FileCopy.copyFilenameFromTo(dir+"bin/battle/pack/",FileCopy.baseDir()+"bin/battle/pack/", "Bactor_"+bugName+".gif", true, true);
			}		
		} catch (FileNotFoundException fileNotFound) {
			System.out.println("\"externalBugsDirectories.txt\":File Not Found!");
		} catch (EOFException eof) {
		} catch (IOException ioe) {
			System.out.println("IO error: " + ioe);
		}
	}

	public static String baseDir() {
		final String dir = System.getProperty("user.dir")+"/";
		return dir;
	}
	public static String homeDir() {
		final String dir = System.getProperty("user.home")+"/";
		return dir;
		
	}
	public static void copyFilenameFromTo(String sourceDir, String destDir, String fileName, boolean verbose, boolean overWrite) {
		IOFileFilter fileFilter = new WildcardFileFilter(fileName);
		File fileSourceDir=new File(sourceDir);
		if (fileSourceDir.isDirectory()) {
			Collection<File> files=FileUtils.listFiles(fileSourceDir, fileFilter, null);
			if (files.size()>0) {
				for (File sourceFile:files) {
					String name = sourceFile.getName();

					File targetFile = new File(destDir+name);
					if (!overWrite && targetFile.exists()) {
						if (verbose) System.out.println(targetFile + " Already Exists!");
					} else {
						if (verbose) System.out.print("copying "+sourceFile.getName()+" from "+sourceDir+" to "+destDir+"...");
						try {
							FileUtils.copyFile(sourceFile, targetFile);
							if (verbose) System.out.println("completed!");
						} catch (IOException e) {
							if (verbose) System.out.println("failed!");
							e.printStackTrace();
						}
					}
				}
			} else {
				if (verbose) System.out.println(fileName+" not found in "+fileSourceDir);
			}
		} else {
			if (verbose) System.out.println(fileSourceDir+" isn't a directory or does not exist!");
		}
	}
	public static void deleteFiles(ArrayList<File> files,boolean verbose) {
		for (File file:files) {
			if (file.delete()) {
				if (verbose) System.out.println(file + " deleted!");
			} else {
				if (verbose) System.out.println(file + " could not be deleted!");
			}
		}
	}
	public static ArrayList<File> getFiles(String dir, String filename) {
		ArrayList<File> files=new ArrayList<File>();
		IOFileFilter fileFilter = new WildcardFileFilter(filename);
		File fileSourceDir=new File(dir);
		if (fileSourceDir.isDirectory()) {
			files.addAll(FileUtils.listFiles(fileSourceDir, fileFilter, null));
		}
		return files;
	}
	public static ArrayList<File> copyFileTypeFromTo(String sourceDir, String destDir, String fileType, boolean verbose, boolean overWrite) {
		String target =destDir;
		ArrayList<File> copiedFiles=new ArrayList<File>();
		String[] fileTypes=new String[1];
		fileTypes[0]=fileType;
		File fileSourceDir=new File(sourceDir);
		Collection<File> files=FileUtils.listFiles(fileSourceDir, fileTypes, false);
		for (File sourceFile:files) {
			String name = sourceFile.getName();

			File targetFile = new File(target+name);
			copiedFiles.add(targetFile);
			if (!overWrite && targetFile.exists()) {
				if (verbose) System.out.println(targetFile + " Already Exists!");
			} else {
				if (verbose) System.out.println("Copying file : " + sourceFile.getName());
				try {
					FileUtils.copyFile(sourceFile, targetFile);
					if (verbose) System.out.println("copying "+fileType+" from "+sourceDir+" to "+destDir+" completed!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return copiedFiles;
	}
}
