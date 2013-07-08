package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Function;

public abstract interface Byte2ReferenceFunction<V>
  extends Function<Byte, V>
{
  public abstract V put(byte paramByte, V paramV);
  
  public abstract V get(byte paramByte);
  
  public abstract V remove(byte paramByte);
  
  public abstract boolean containsKey(byte paramByte);
  
  public abstract void defaultReturnValue(V paramV);
  
  public abstract V defaultReturnValue();
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */