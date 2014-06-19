package battle.pack;

import java.util.LinkedList;
import java.util.List;

public class SecurityLog {
	private List<SecurityEntry> entries;
	private boolean onlyFirstUniqueEntry;
	private String fileName;
	public SecurityLog(String fileName, boolean onlyFirstUniqueEntry) {
		this.fileName=fileName;
		this.onlyFirstUniqueEntry=onlyFirstUniqueEntry;
		entries=new LinkedList<SecurityEntry>();

	}
	public boolean AddEntry(String permissionName, StackTraceElement stackTraceElement) {
		if (permissionName.indexOf("SecurityLog$SecurityEntry.class")==-1) {
			SecurityEntry entry=new SecurityEntry(permissionName,stackTraceElement);
			if (!onlyFirstUniqueEntry || (onlyFirstUniqueEntry && !entries.contains(stackTraceElement))) {
				entries.add(entry);
				return true;
			}
		}
		return false;
	}
	public String report(boolean toFile, boolean verbose) {
		FileWrite securityReport=null;
		if (toFile) {
			securityReport=new FileWrite(fileName);
		}
		String report="";
		for (SecurityEntry entry:entries) {
			if (verbose) {
				report+=entry.getReport()+"\n";
			} else {
				report+=entry+"\n";
			}
		}
		if (toFile && securityReport!=null) {
			securityReport.write(report, false);
		}
		return report;
	}
	private class SecurityEntry {
		private String permissionName;
		private StackTraceElement stackTraceElement;
		public SecurityEntry(String permissionName, StackTraceElement stackTraceElement) {
			this.permissionName=permissionName;
			this.stackTraceElement=stackTraceElement;
		}
		public String getPermissionName() {return permissionName;}
		public String getRequestingClass() {return stackTraceElement.getClassName();}
		public String getRequestingMethodName() {return stackTraceElement.getMethodName();}
		public String getRequestingFileName() {return stackTraceElement.getFileName();}
		public int getRequestingLineNumber() {return stackTraceElement.getLineNumber();}
		public String getReport() {
			String report=toString()+" in method \""+getRequestingMethodName()+"\" on line #"+getRequestingLineNumber()+"["+getRequestingFileName()+"]";		
			return report;
		}
		public String toString() {
			return getPermissionName()+":"+getRequestingClass();
		}
		public boolean equals(Object o) {
			if (o instanceof SecurityEntry) {
				SecurityEntry other=(SecurityEntry)o;
				return other.toString().equals(toString());
			} else {
				return super.equals(o);
			}
		}
	}

}

