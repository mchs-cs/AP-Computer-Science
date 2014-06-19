package battle.pack;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import info.gridworld.gui.AbstractDisplay;

public class BonusActorDisplay extends AbstractDisplay {
	private Class<?> cl;
	private String imageFilename;
	private static final String imageExtension = ".gif";
	private Map<String, Image> tintedVersions = new HashMap<String, Image>();

	public BonusActorDisplay() throws IOException {
		super();
		this.cl=this.getClass();
		this.imageFilename="battle/pack/BonusActor";
		//imageFilename = cl.getName().replace('.', '/');
//		System.out.println("imageFilename=" + this.imageFilename);
		URL url = cl.getClassLoader().getResource(imageFilename + imageExtension);

		if (url == null)
			throw new FileNotFoundException(imageFilename + imageExtension + " not found.");
//		System.out.println("imageFilename=" + this.imageFilename + ", url=" + url.toString());
	}
	public BonusActorDisplay(Class<?> cl) throws IOException
	{
		this.cl = cl;
		imageFilename = cl.getName().replace('.', '/');
		URL url = cl.getClassLoader().getResource(imageFilename + imageExtension);

		if (url == null)
			throw new FileNotFoundException(imageFilename + imageExtension + " not found.");
	}
	public void draw(Object obj, Component comp, Graphics2D g2, Rectangle rect)
	{
		float scaleFactor = Math.min(rect.width, rect.height);
		g2 = (Graphics2D) g2.create();

		// Translate to center of the object
		g2.translate(rect.x + rect.width / 2.0, rect.y + rect.height / 2.0);

		// Rotate drawing surface before drawing to capture object's
		// orientation (direction).
		if (obj != null)
		{
			Integer direction = (Integer) getProperty(obj, "direction");
			int rotationInDegrees = direction == null ? 0 : direction
					.intValue();
			g2.rotate(Math.toRadians(rotationInDegrees));
		}
		// Scale to size of rectangle, adjust stroke back to 1-pixel wide
		g2.scale(scaleFactor, scaleFactor);
		g2.setStroke(new BasicStroke(1.0f / scaleFactor));
		draw(obj, comp, g2);
	}

	public void draw(Object obj, Component comp, Graphics2D g2)
	{
		Color color;
		if (obj == null)
			color = null;
		else
			color = (Color) getProperty(obj, "color");
		String imageSuffix = (String) getProperty(obj, "imageSuffix");
		if (imageSuffix == null)
			imageSuffix = "";
		// Compose image with color using an image filter.
		//	Image tinted = null;
		Image tinted = tintedVersions.get(color + imageSuffix);
		if (tinted == null) // not cached, need new filter for color
		{
			Image untinted = tintedVersions.get(imageSuffix);
			if (untinted == null) // not cached, need to fetch
			{
				try
				{
					URL url = cl.getClassLoader().getResource(
							imageFilename + imageSuffix + imageExtension);
					if (url == null)
						throw new FileNotFoundException(imageFilename
								+ imageSuffix + imageExtension + " not found.");
					untinted = ImageIO.read(url);
					tintedVersions.put(imageSuffix, untinted);
				}
				catch (IOException ex)
				{
					untinted = tintedVersions.get("");
				}
			}
			tinted=untinted;
			int width = tinted.getWidth(null);
			int height = tinted.getHeight(null);
			int size = Math.max(width, height);

			// Scale to shrink or enlarge the image to fit the size 1x1 cell.
			g2.scale(1.0 / size, 1.0 / size);
			g2.clip(new Rectangle(-width / 2, -height / 2, width, height));
			g2.drawImage(tinted, -width / 2, -height / 2, null);
		}
	}
    public static Object getProperty(Object obj, String propertyName)
    {
        if (obj == null)
            return null;
        try
        {
            BeanInfo info = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            for (int i = 0; i < descriptors.length; i++)
            {
 //               System.out.println(descriptors[i].getName());
                if (descriptors[i].getName().equals(propertyName))
                {
                    Method getter = descriptors[i].getReadMethod();
                    if (getter == null)
                        return null;
                    try {
                    return getter.invoke(obj);
                    } catch (Exception ex) {
                        System.out.println(descriptors[i].getName());
                        return null;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
}