package battle.pack;

import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

public class BattleBugsSecurityManager extends SecurityManager {
	private List<BBPerm> betterWhiteList=asList(
			//			new BBP(new PString("permName"),new PString("permClass"),new PString("permAction"),new PString("callingClass")),
			new BBPerm(new PString("*"),new PString("PropertyPermission"),new PString("read")),
			new BBPerm(new PString("*"),new PString("FilePermission"),new PString("read")),
			new BBPerm(new PString("*"),new PString("SocketPermission"),new PString("connect,resolve")),
			new BBPerm(new PString("*"),new PString("LoggingPermission"),new PString("*")),
			new BBPerm(new PString("getProperty.*"),new PString("SecurityPermission"),new PString("*"),new PString("java.net.*")),
			new BBPerm(new PString("modifyThreadGroup"),new PString("RuntimePermission"),new PString("*"),new PString("sun.net.*")),			
			new BBPerm(new PString("modifyThread"),new PString("RuntimePermission"),new PString("*"),new PString("sun.net.*")),			
			new BBPerm(new PString("suppressAccessChecks"),new PString("ReflectPermission"),new PString("*"),new PString("java.net.*")),			
			new BBPerm(new PString("*"),new PString("NetPermission"),new PString("*"))
			);
	private boolean verbose=false;
	private String logFilename=null;
	private Boolean liveLog=false;
	public void setVerbose(boolean verbose) {
		this.verbose=verbose;
	}
	public void activeLog(String logFileName) {
		if (!liveLog) {
			liveLog=true;
			this.logFilename=logFileName;
			try{
				// Create file 
				FileWriter fstream = new FileWriter(logFilename,false);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write("Security Log Created: "+new Date()+"\n");
				//Close the output stream
				out.close();
			} catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
	}
	private class Caller {
		private String filename;
		private int lineNumber;
		private String methodName;
		public Caller(String filename, int lineNumber, String methodName) {
			this.filename=filename;
			this.lineNumber=lineNumber;
			this.methodName=methodName;
		}
		public String toString() {
			String s="FileName="+filename+"\n";
			s+="LineNumber="+lineNumber+"\n";
			s+="MethodName="+methodName+"\n";
			return s;
		}
		public boolean equals(Object obj) {
			if (obj instanceof Caller) {
				Caller otherCaller=(Caller)obj;
				return filename.equals(otherCaller.filename) && methodName.equals(otherCaller.methodName);
			} else {
				return super.equals(obj);
			}
		}
	}
	private class LogEntry {
		private BBPerm perm;
		private Caller caller;
		public LogEntry(BBPerm perm, Caller caller) {
			this.perm=perm;
			this.caller=caller;
		}
		public boolean equals(Object obj) {
			if (obj instanceof LogEntry) {
				LogEntry otherLogEntry=(LogEntry)obj;
				if (caller==null) return perm.equals(otherLogEntry.perm);
				return perm.equals(otherLogEntry.perm) && caller.equals(otherLogEntry.caller);
			} else {
				return super.equals(obj);
			}
		}
		public String toString() {
			return perm.fullDump()+"\n"+caller;
		}
	}
	private class PString {
		private String s;
		public PString(String permission) {
			s=permission;
		}
		public String toString() {return s;}
		public boolean equals(Object o) {
			if (o instanceof PString) {
				PString ob=(PString)o;
				String[] myTokens= s.split(",");
				String[] obTokens=ob.s.split(",");
				for (String token:myTokens) {
					for (String obToken:obTokens) {
						int tIndex=token.indexOf("*");
						int oIndex=obToken.indexOf("*");
						if ((tIndex==0 && oIndex==-1) || (oIndex==0 && tIndex==-1)) return true;
						if (tIndex>0 && oIndex==-1) {
							if (obToken.startsWith(token.substring(0, tIndex))) return true;
						} else if (oIndex>0 && tIndex==-1) {
							if (token.startsWith(obToken.substring(0, oIndex))) return true;
						} else if (oIndex==-1 && tIndex==-1) {
							if (token.equals(obToken)) return true;
						}						
					}
				}
				return false;
			} else {
				return super.equals(o);
			}
		}
	}
	private class BBPerm {
		private PString permName=null;
		private PString permClass=null;
		private PString permAction=null;
		private PString callingClass=null;
		private ArrayList<PString> callingClassStack=null;
		public BBPerm(PString permName, PString permClass, PString permAction) {
			this.permName=permName;
			this.permClass=permClass;
			this.permAction=permAction;
		}
		public BBPerm(PString permName, PString permClass, PString permAction, PString callingClass) {
			this.permName=permName;
			this.permClass=permClass;
			this.permAction=permAction;
			this.callingClass=callingClass;
		}
		public BBPerm(PString permName, PString permClass, PString permAction, ArrayList<PString> callingClassStack) {
			this.permName=permName;
			this.permClass=permClass;
			this.permAction=permAction;
			this.callingClassStack=callingClassStack;
		}
		public boolean equals(Object o) {
			if (o instanceof BBPerm) {
				BBPerm ob=(BBPerm)o;
				if (this.permName==null || (ob.permName!=null && this.permName.equals(ob.permName))) {
					if (this.permClass==null || (ob.permClass!=null && this.permClass.equals(ob.permClass))) {
						if (this.permAction==null || (ob.permAction!=null && this.permAction.equals(ob.permAction))) {
							if (this.callingClass!=null) {
								if (ob.callingClass!=null) {
									return (this.callingClass.equals(ob.callingClass));
								} else {
									if (ob.callingClassStack!=null) {
										return ob.callingClassStack.contains(this.callingClass);
									} else {
										return false;
									}
								}
							} else {
								return true;
							}
						}
					}
				}
				return false;
			} else {
				return super.equals(o);
			}
		}
		public String toString() {
			String s="";
			if (this.permName!=null) s+="Permission Name="+this.permName+"\n";
			if (this.permClass!=null) s+="Permission Class="+this.permClass+"\n";
			if (this.permAction!=null) s+="Permission Action="+this.permAction+"\n";
			if (this.callingClass!=null) s+="Calling Class="+this.callingClass+"\n";
			if (s.endsWith("\n")) {
				s=s.substring(0, s.length()-1);
			}
			return s;
		}
		public String fullDump() {
			String s=this.toString()+"\n";
			if (this.callingClassStack!=null) {
				s+="Calling Class Stack:\n";
				for (PString callingClass:callingClassStack) {
					s+=callingClass+"\n";
				}
			}
			if (s.endsWith("\n")) {
				s=s.substring(0, s.length()-1);
			}
			return s;

		}
	}
	public void checkDelete(String filename) {}
	public void checkLink(String library) {}
	public void checkRead(FileDescriptor filedescriptor) {}
	public void checkRead(String filename) {}
	public void checkRead(String filename, Object executionContext) {}
	public void checkWrite(FileDescriptor filedescriptor) {}
	public void checkWrite(String filename) {}
	public void checkPermission(Permission perm) throws SecurityException {
		String permName=perm.getName();
		String permClass=perm.getClass().getSimpleName();
		String permActions=perm.getActions().toString();
		int elements=Thread.currentThread().getStackTrace().length;
		for (int elemIndex=0; elemIndex<elements; elemIndex++) {
			StackTraceElement elem=Thread.currentThread().getStackTrace()[elemIndex];
			String requestiongClassName=elem.getClassName();
			if (requestiongClassName.indexOf("battle.game")!=-1) {
				ArrayList<PString> stackClasses=new ArrayList<PString>();
				String prior="";
				boolean ignore=false;
				for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
					String current=e.getClassName();
					if (current.equals("battle.pack.BattleBugsSecurityManager") && prior.equals(current)) ignore=true;
					stackClasses.add(new PString(e.getClassName()));
				}
				if (!ignore) {
					BBPerm checkPerm=new BBPerm(new PString(permName),new PString(permClass),new PString(permActions),stackClasses);
					Caller caller=new Caller(elem.getFileName(),elem.getLineNumber(),elem.getMethodName());
					LogEntry logEntry=new LogEntry(checkPerm,caller);
					for (BBPerm listPerm:betterWhiteList) {
						if (listPerm.equals(checkPerm)) {
							if (verbose) System.out.println(checkPerm);
							if (verbose) System.out.println("<---Action Allowed: White List--->");
							checkPerm.callingClassStack=null;
							if (!approvedRequests.contains(logEntry)) {
								approvedRequests.add(logEntry);
								if (liveLog) {
									activeLog("<---Action Allowed: White List--->\n",logEntry);
								}
							}
							return;
						}
					}
					if (verbose) System.out.println(checkPerm.fullDump());
					if (verbose) System.out.println("!!!!!!!! Action Disallowed !!!!!!!!");
					if (verbose) System.out.println(perm+"\n");
					if (verbose) System.out.println(caller);
					if (verbose) System.out.println("------------------------------------------------");
					if (!deniedRequests.contains(logEntry)) {
						deniedRequests.add(logEntry);
						if (liveLog) {
							activeLog("!!!!!!!! Action Disallowed !!!!!!!!\n",logEntry);
						}
					}
					throw new SecurityException(checkPerm.toString());
				}
			}
		}
	}

	private ArrayList<LogEntry> approvedRequests=new ArrayList<LogEntry>();
	private ArrayList<LogEntry> deniedRequests=new ArrayList<LogEntry>();
	public String getSecurityLog(boolean getApprovedRequests, boolean getDeniedRequests) {
		String log="";
		if (getApprovedRequests) {
			log+="-Approved Security Requests:\n";
			for (LogEntry logEntry:approvedRequests) {
				log+=logEntry+"-------------------------\n";
			}
		}
		if (getDeniedRequests) {
			log+="-Denied Security Requests:\n";
			for (LogEntry logEntry:deniedRequests) {
				log+=logEntry+"-------------------------\n";
			}
		}
		return log;
	}
	private void activeLog(String label, LogEntry logEntry) {
		try{
			// Create file 
			FileWriter fstream = new FileWriter(logFilename,true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(label+logEntry);
			//Close the output stream
			out.close();
		} catch (SecurityException e) {
			System.out.println("Oops -" + e.getMessage());
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}
}
