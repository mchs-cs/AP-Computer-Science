package StaticLibrary;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import battle.pack.BattleBug;
import battle.pack.FileCopy;

public class StaticLibrary {
	public enum VERBOSE {NONE,VERBOSE,ALL}
	public static ArrayList<BattleBug> getBattleBugs(VERBOSE verbose) {
		ArrayList<BattleBug> battlebugs = new ArrayList<BattleBug>();
		int cnt=0;
		try {
			Class<?>[] classList = StaticLibrary.getClasses("battle.game");
			for (int i=0; i<classList.length; i++) {
				Class<?> aClass = classList[i];
				if (verbose==VERBOSE.ALL) System.out.println("Class: " + aClass.getName() + " is extended from " + aClass.getSuperclass());
				if (aClass.getSuperclass().toString().equals("class battle.pack.AbstractBattleBug")) {
					String err=" instantiating "+aClass.getName();
					try {
						BattleBug b = (BattleBug) aClass.newInstance();
						battlebugs.add(b);
					} catch (InstantiationException e) {
						if (verbose==VERBOSE.VERBOSE) System.out.println("Catch:InstantiationException"+err);
						if (verbose==VERBOSE.ALL) e.printStackTrace();
					} catch (IllegalAccessException e) {
						if (verbose==VERBOSE.VERBOSE) System.out.println("Catch:IllegalAccessException"+err);
						if (verbose==VERBOSE.ALL) e.printStackTrace();
					} catch (UnsupportedClassVersionError e) {
						if (verbose==VERBOSE.VERBOSE) System.out.println("Catch:UnsupportedClassVersionError"+err);
						if (verbose==VERBOSE.ALL) e.printStackTrace();						
					} catch (NullPointerException e) {
						if (verbose==VERBOSE.VERBOSE) System.out.println("Catch:NullPointerError"+err);
						if (verbose==VERBOSE.ALL) e.printStackTrace();		
					} catch (Error e) {
						if (verbose==VERBOSE.VERBOSE) System.out.println("Unknow error"+err);
						if (verbose==VERBOSE.ALL) e.printStackTrace();
						String fileClass=err.substring(err.lastIndexOf(".")+1);
						FileCopy.deleteFiles(FileCopy.getFiles(FileCopy.baseDir()+"bin/battle/game/", fileClass+".*"), true);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Catch:ClassNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Catch:IOException");
			e.printStackTrace();
		}
		return battlebugs;
	}
	
    @SuppressWarnings("rawtypes")
	public static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        ArrayList<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile().replace("%20"," ")));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("rawtypes")
	private static ArrayList<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        ArrayList<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
            		try {
            			classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            		} catch(UnsupportedClassVersionError e) {
            			System.out.println("Error trying to load "+file.getName()+":"+e.getMessage());
            			ArrayList<File> filesToDelete=new ArrayList<File>();
            			filesToDelete.add(file);
            			FileCopy.deleteFiles(filesToDelete, true);
            		}
            }
        }
        return classes;
    }
	public static int stepsToFaceRelDirection(int relativeDirection) { // Steps ALWAYS represents a positive integer of the number for 45 degree turns and/or forward moves
		return Math.abs(relativeDirection / 45 );
	}
	public static int stepToFaceDirection(int fromDirection, int toDirection) {
		return StaticLibrary.stepsToFaceRelDirection(StaticLibrary.degreesRel(fromDirection, toDirection));
	}
	public static int degreesRel(int fromDirection, int toDirection) {
		int degreesRel=0;
		int myDirectiionMinustargetDirectiion= fromDirection-toDirection;
		if (myDirectiionMinustargetDirectiion<0) {
			if (Math.abs(myDirectiionMinustargetDirectiion)<180) {
				degreesRel=-myDirectiionMinustargetDirectiion; // Turn CW
			} else {
				degreesRel = - (360-Math.abs(myDirectiionMinustargetDirectiion)); // Turn CCW
			}
		} else {
			if (Math.abs(myDirectiionMinustargetDirectiion)<180) {
				degreesRel=-myDirectiionMinustargetDirectiion; // Turn CCW
			} else {
				degreesRel=360-myDirectiionMinustargetDirectiion; // Turn CW
			}
		}
		return degreesRel;
	}
	public static boolean headToHead(int directionA, int directionB) {
		return (directionA + directionB) % 180 == 0;
	}
	public static String padSpaces(String theString,int theLength) {
		return StaticLibrary.padString(theString, theLength, " ");
	}
	public static  String padString(String theString, int padLength, String padString) {
		// If the string is shorter than padLength, leave it alone, otherwise trim it to padLength
		theString = theString.substring(0, Math.min(padLength, theString.length()));
		// Next we add the padString to our string until theString exceeds padLength
		while (theString.length()<padLength) {
			theString+=padString;
		}
		// And finally, since adding padString might result in a string longer than PadLength, we trim it again
		theString = theString.substring(0, Math.min(padLength, theString.length()));
		// And we are done, so return the newly padded theString
		return theString;
	}
	public static int randomInt(int highVal, int lowVal){
		int myRandomNumber;
			myRandomNumber = (int) (  (Math.random()*(highVal-lowVal)) + lowVal);
		return myRandomNumber;
	}
	public static int next2PowerN(int number) {
		double log=Math.log(number-1)/Math.log(2);
		int num=(int)Math.pow(2, (int)log+1);
		return num;
				//2^(int(log(x-1)/log(2))+1)
	}
	public static String stackTraceToString(Throwable e) {
	    StringBuilder sb = new StringBuilder();
	    for (StackTraceElement element : e.getStackTrace()) {
	        sb.append(element.toString());
	        sb.append("\n");
	    }
	    return sb.toString();
	}
}
