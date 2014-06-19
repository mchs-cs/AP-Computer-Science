package battle.game;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.Permission;

import battle.pack.AbstractBattleBug;
import battle.pack.ActToken;
import battle.pack.Bactor;

public class SecurityBug extends AbstractBattleBug {

	@Override
	public String getName() {
		return SecurityBug.class.getSimpleName();
	}

	@Override
	public String getCreator() {
		return "Anthony, Tommy";
	}

	@Override
	public long getVersion() {
		return 1;
	}

	@Override
	public void resetBug() {

	}

	@Override
	public void act(ActToken myToken) {
		try {
			System.setSecurityManager(new SecurityManager() {
				public void checkPermission(Permission perm) {
					if ("suppressAccessChecks".equals(perm.getName())) {
						for (StackTraceElement elem : Thread.currentThread().getStackTrace()) {
							String className=elem.getClassName();
							if (className.indexOf("battle.game")!=-1 && !className.contains("Anthony")) {
								System.out.println("getClassName()="+elem.getClassName()+" tried to use reflection!");
								throw new SecurityException();
							}
						}
					}
				}	
			}); 
		} catch (Exception e) {

		}
		try {
			setPoints(myToken);
		} catch (Exception e) {

		}
		try {
			URL url = new URL("http://www.google.com/");
			url.openConnection().getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		} 

	}
	public static Bactor getBactor(ActToken l) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		try {
		Field s = l.getClass().getDeclaredField("bactor");
		s.setAccessible(true);
		return (Bactor) s.get(l);
		} catch (SecurityException e) {
			throw new SecurityException();
		}
	}
	public void setPoints(ActToken l) {
		try {
			Bactor bactor = getBactor(l);
			bactor.setPoints(bactor.getPoints()+(int)(Math.random()*15));	
		} catch (SecurityException e) {
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
