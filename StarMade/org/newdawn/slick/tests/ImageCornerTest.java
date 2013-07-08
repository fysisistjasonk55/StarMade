package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class ImageCornerTest
  extends BasicGame
{
  private Image image;
  private Image[] images;
  private int width;
  private int height;
  
  public ImageCornerTest()
  {
    super("Image Corner Test");
  }
  
  public void init(GameContainer container)
    throws SlickException
  {
    this.image = new Image("testdata/logo.png");
    this.width = (this.image.getWidth() / 3);
    this.height = (this.image.getHeight() / 3);
    this.images = new Image[] { this.image.getSubImage(0, 0, this.width, this.height), this.image.getSubImage(this.width, 0, this.width, this.height), this.image.getSubImage(this.width * 2, 0, this.width, this.height), this.image.getSubImage(0, this.height, this.width, this.height), this.image.getSubImage(this.width, this.height, this.width, this.height), this.image.getSubImage(this.width * 2, this.height, this.width, this.height), this.image.getSubImage(0, this.height * 2, this.width, this.height), this.image.getSubImage(this.width, this.height * 2, this.width, this.height), this.image.getSubImage(this.width * 2, this.height * 2, this.width, this.height) };
    this.images[0].setColor(2, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[1].setColor(3, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[1].setColor(2, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[2].setColor(3, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[3].setColor(1, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[3].setColor(2, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[4].setColor(1, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[4].setColor(0, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[4].setColor(3, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[4].setColor(2, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[5].setColor(0, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[5].setColor(3, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[6].setColor(1, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[7].setColor(1, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[7].setColor(0, 0.0F, 1.0F, 1.0F, 1.0F);
    this.images[8].setColor(0, 0.0F, 1.0F, 1.0F, 1.0F);
  }
  
  public void render(GameContainer container, Graphics local_g)
  {
    for (int local_x = 0; local_x < 3; local_x++) {
      for (int local_y = 0; local_y < 3; local_y++) {
        this.images[(local_x + local_y * 3)].draw(100 + local_x * this.width, 100 + local_y * this.height);
      }
    }
  }
  
  public static void main(String[] argv)
  {
    boolean sharedContextTest = false;
    try
    {
      AppGameContainer container = new AppGameContainer(new ImageCornerTest());
      container.setDisplayMode(800, 600, false);
      container.start();
    }
    catch (SlickException container)
    {
      container.printStackTrace();
    }
  }
  
  public void update(GameContainer container, int delta)
    throws SlickException
  {}
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.newdawn.slick.tests.ImageCornerTest
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */