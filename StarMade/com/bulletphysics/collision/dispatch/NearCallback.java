package com.bulletphysics.collision.dispatch;

import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.broadphase.DispatcherInfo;

public abstract class NearCallback
{
  public abstract void handleCollision(BroadphasePair paramBroadphasePair, CollisionDispatcher paramCollisionDispatcher, DispatcherInfo paramDispatcherInfo);
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     com.bulletphysics.collision.dispatch.NearCallback
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */