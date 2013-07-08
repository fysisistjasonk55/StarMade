package org.schema.game.common.controller.elements;

import javax.vecmath.Vector3f;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.shield.ShieldCollectionManager;

public abstract interface ShieldContainerInterface
  extends PowerManagerInterface
{
  public abstract ShieldCollectionManager getShieldManager();
  
  public abstract double handleShieldHit(Vector3f paramVector3f, SegmentController paramSegmentController, float paramFloat);
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.schema.game.common.controller.elements.ShieldContainerInterface
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */