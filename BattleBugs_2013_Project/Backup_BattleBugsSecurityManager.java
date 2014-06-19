package battle.pack;

import java.io.FileDescriptor;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class BattleBugsSecurityManager extends SecurityManager {
	private class PermString {
		private String s;
		public PermString(String permission) {
			s=permission;
		}
		public String toString() {return s;}
		public boolean equals(Object o) {
			if (o instanceof PermString) {
				PermString ob=(PermString)o;
				if (s.indexOf("*")!=-1) {
					return ob.s.startsWith(s.substring(0, s.indexOf("*")-1));
				} else {
					return s.equals(ob.s);
				}
			} else {
				return super.equals(o);
			}
		}
	}
	private class MyPermission {
		private String permClass;
		private String permAction;
		private boolean checkPermName;
		private String permName;
		private String topClass;

		public MyPermission(String permClass, String permAction) {
			this.permAction=permAction;
			this.permClass=permClass;
			checkPermName=false;
		}
		public MyPermission(String permClass, String permAction, String permName, String topClass) {
			this.permAction=permAction;
			this.permClass=permClass;
			this.permName=permName;
			this.checkPermName=true;
			this.topClass=topClass;
		}
		public boolean equals(Object o) {
			if (o instanceof MyPermission) {
				MyPermission ob=(MyPermission)o;
				if (checkPermName) {
					return (ob.permAction.equals(permAction) && ob.permClass.equals(permClass) && ob.permName.equals(permName));
				} else {
					return (ob.permAction.equals(permAction) && ob.permClass.equals(permClass));
				}
			} else {
				return super.equals(o);
			}
		}
		public boolean grant(String permClass, String permAction, String permName, ArrayList<String> stackClasses) {
			if (this.permClass.equals(permClass) && this.permAction.equals(permAction)){
				if (checkPermName) {
					if (topClass.endsWith("*")) {
						String stackClassStartsWith=topClass.substring(0,topClass.indexOf("*")-1);
						for (String stackClass:stackClasses) {
							if (stackClass.startsWith(stackClassStartsWith)) {
								return this.permName.equals(permName);
							}
						}
					} else {
						return (this.permName.equals(permName) && stackClasses.contains(topClass));
					}
				} else {
					return true;
				}
			}
			return false;
		}
		public boolean grant(String permClass, String permAction) {
			if (this.permClass.equals(permClass) && this.permAction.equals(permAction) && !checkPermName) return true;
			return false;
		}
		public boolean grant(String permClass, String permAction, String permName, String topClass) {
			if (this.permClass.equals(permClass) && this.permAction.equals(permAction)){
				if (!checkPermName || (this.permName.equals(permName) && this.topClass.equals(topClass))) return true;
			}
			return false;
		}
		public boolean grant(MyPermission checkPerm) {
			if (checkPerm.equals(this)) return true;
			return false;
		}
	}
	private List<MyPermission> checkPermNameList=asList(
			new MyPermission("ReflectPermission",""));

	private List<MyPermission> whiteList=asList(new MyPermission("FilePermission","read"),
			new MyPermission("PropertyPermission","read"),
			new MyPermission("SocketPermission","connect,resolve"),
			new MyPermission("SocketPermission","resolve"),
			new MyPermission("LoggingPermission",""),
			new MyPermission("SecurityPermission","","getProperty.networkaddress.cache.ttl","xxx"),
			new MyPermission("ReflectPermission","","suppressAccessChecks","java.net.URL"),
			new MyPermission("ReflectPermission","","suppressAccessChecks","java.net.ProxySelector"),
			new MyPermission("ReflectPermission","","suppressAccessChecks","java.net.InetAddress.*"),
			new MyPermission("NetPermission","")
			);
	public void checkDelete(String filename) {}
	public void checkLink(String library) {}
	public void checkRead(FileDescriptor filedescriptor) {}
	public void checkRead(String filename) {}
	public void checkRead(String filename, Object executionContext) {}
	public void checkWrite(FileDescriptor filedescriptor) {}
	public void checkWrite(String filename) {}
	public void checkPermission(Permission perm) {
		String permName=perm.getName();
		String permClass=perm.getClass().getSimpleName();
		String permFull=perm.toString();
		String permActions=perm.getActions().toString();
		int elements=Thread.currentThread().getStackTrace().length;
		for (int elemIndex=0; elemIndex<elements; elemIndex++) {
			StackTraceElement elem=Thread.currentThread().getStackTrace()[elemIndex];
			//		}
			//		for (StackTraceElement elem : Thread.currentThread().getStackTrace()) {
			String requestiongClassName=elem.getClassName();
			if (requestiongClassName.indexOf("battle.game")!=-1) {
				ArrayList<String> stackClasses=new ArrayList<String>();
				MyPermission checkPerm=new MyPermission(permClass,permActions);
				Boolean allowed=false;
				System.out.println("**Found Buried Match: "+elemIndex+"/"+elements);
				System.out.println("permName="+permName);
				System.out.println("permClass="+permClass);
				System.out.println("permActions="+permActions);
				System.out.println("permFull="+permFull);
				System.out.println("requestingClass="+requestiongClassName);
				if (whiteList.contains(checkPerm)) {
					if (checkPermNameList.contains(checkPerm)) {
						System.out.println("Checking special Permission!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
							stackClasses.add(e.getClassName());
						}
						MyPermission specialPerm=new MyPermission(permClass,permActions,permName,"");
						for (MyPermission wPerm:whiteList) {
							if (wPerm.equals(specialPerm)) {
								allowed=wPerm.grant(permClass, permActions, permName, stackClasses);
								if (allowed) break;
							}
						}
					} else {
						allowed=true;
					}
				}
				if (allowed) {
					System.out.println("<---Action Allowed: White List--->");
				} else {
					System.out.println("!!!!!!!! Action Disallowed !!!!!!!!");
					System.out.println("FileName="+elem.getFileName());
					System.out.println("LineNumber="+elem.getLineNumber());
					System.out.println("MethodName="+elem.getMethodName());
					if (stackClasses.size()>0) {
						System.out.println("Stack classes:");
						for (String stackClass:stackClasses) {
							System.out.println(stackClass);
						}
					}
					throw new SecurityException();
				}
				System.out.println();
			}			
		}
	}
}
