package org.newdawn.slick.svg.inkscape;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Figure;
import org.newdawn.slick.svg.Loader;
import org.newdawn.slick.svg.NonGeometricData;
import org.newdawn.slick.svg.ParsingException;
import org.w3c.dom.Element;

public class PolygonProcessor
  implements ElementProcessor
{
  private static int processPoly(Polygon poly, Element element, StringTokenizer tokens)
    throws ParsingException
  {
    int count = 0;
    ArrayList pts = new ArrayList();
    boolean moved = false;
    boolean closed = false;
    while (tokens.hasMoreTokens())
    {
      String nextToken = tokens.nextToken();
      if (!nextToken.equals("L"))
      {
        if (nextToken.equals("z"))
        {
          closed = true;
          break;
        }
        if (nextToken.equals("M"))
        {
          if (!moved) {
            moved = true;
          } else {
            return 0;
          }
        }
        else
        {
          if (nextToken.equals("C")) {
            return 0;
          }
          String tokenX = nextToken;
          String tokenY = tokens.nextToken();
          try
          {
            float local_x = Float.parseFloat(tokenX);
            float local_y = Float.parseFloat(tokenY);
            poly.addPoint(local_x, local_y);
            count++;
          }
          catch (NumberFormatException local_x)
          {
            throw new ParsingException(element.getAttribute("id"), "Invalid token in points list", local_x);
          }
        }
      }
    }
    poly.setClosed(closed);
    return count;
  }
  
  public void process(Loader loader, Element element, Diagram diagram, Transform local_t)
    throws ParsingException
  {
    Transform transform = Util.getTransform(element);
    transform = new Transform(local_t, transform);
    String points = element.getAttribute("points");
    if (element.getNodeName().equals("path")) {
      points = element.getAttribute("d");
    }
    StringTokenizer tokens = new StringTokenizer(points, ", ");
    Polygon poly = new Polygon();
    int count = processPoly(poly, element, tokens);
    NonGeometricData data = Util.getNonGeometricData(element);
    if (count > 3)
    {
      Shape shape = poly.transform(transform);
      diagram.addFigure(new Figure(5, shape, data, transform));
    }
  }
  
  public boolean handles(Element element)
  {
    if (element.getNodeName().equals("polygon")) {
      return true;
    }
    return (element.getNodeName().equals("path")) && (!"arc".equals(element.getAttributeNS("http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd", "type")));
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.newdawn.slick.svg.inkscape.PolygonProcessor
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */