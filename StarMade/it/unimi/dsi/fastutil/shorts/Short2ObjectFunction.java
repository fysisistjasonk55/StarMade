package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;

public abstract interface Short2ObjectFunction<V>
  extends Function<Short, V>
{
  public abstract V put(short paramShort, V paramV);
  
  public abstract V get(short paramShort);
  
  public abstract V remove(short paramShort);
  
  public abstract boolean containsKey(short paramShort);
  
  public abstract void defaultReturnValue(V paramV);
  
  public abstract V defaultReturnValue();
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.shorts.Short2ObjectFunction
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */