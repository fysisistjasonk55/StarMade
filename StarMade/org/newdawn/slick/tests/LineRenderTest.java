package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Path;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.opengl.renderer.LineStripRenderer;
import org.newdawn.slick.opengl.renderer.Renderer;

public class LineRenderTest
  extends BasicGame
{
  private Polygon polygon = new Polygon();
  private Path path = new Path(100.0F, 100.0F);
  private float width = 10.0F;
  private boolean antialias = true;
  
  public LineRenderTest()
  {
    super("LineRenderTest");
  }
  
  public void init(GameContainer container)
    throws SlickException
  {
    this.polygon.addPoint(100.0F, 100.0F);
    this.polygon.addPoint(200.0F, 80.0F);
    this.polygon.addPoint(320.0F, 150.0F);
    this.polygon.addPoint(230.0F, 210.0F);
    this.polygon.addPoint(170.0F, 260.0F);
    this.path.curveTo(200.0F, 200.0F, 200.0F, 100.0F, 100.0F, 200.0F);
    this.path.curveTo(400.0F, 100.0F, 400.0F, 200.0F, 200.0F, 100.0F);
    this.path.curveTo(500.0F, 500.0F, 400.0F, 200.0F, 200.0F, 100.0F);
  }
  
  public void update(GameContainer container, int delta)
    throws SlickException
  {
    if (container.getInput().isKeyPressed(57)) {
      this.antialias = (!this.antialias);
    }
  }
  
  public void render(GameContainer container, Graphics local_g)
    throws SlickException
  {
    local_g.setAntiAlias(this.antialias);
    local_g.setLineWidth(50.0F);
    local_g.setColor(Color.red);
    local_g.draw(this.path);
  }
  
  public static void main(String[] argv)
  {
    try
    {
      Renderer.setLineStripRenderer(4);
      Renderer.getLineStripRenderer().setLineCaps(true);
      AppGameContainer container = new AppGameContainer(new LineRenderTest());
      container.setDisplayMode(800, 600, false);
      container.start();
    }
    catch (SlickException container)
    {
      container.printStackTrace();
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.newdawn.slick.tests.LineRenderTest
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */