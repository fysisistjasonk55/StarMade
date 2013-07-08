package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;

public abstract class AbstractLong2ShortFunction
  implements Long2ShortFunction, Serializable
{
  public static final long serialVersionUID = -4940583368468432370L;
  protected short defRetValue;
  
  public void defaultReturnValue(short local_rv)
  {
    this.defRetValue = local_rv;
  }
  
  public short defaultReturnValue()
  {
    return this.defRetValue;
  }
  
  public short put(long key, short value)
  {
    throw new UnsupportedOperationException();
  }
  
  public short remove(long key)
  {
    throw new UnsupportedOperationException();
  }
  
  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean containsKey(Object local_ok)
  {
    return containsKey(((Long)local_ok).longValue());
  }
  
  public Short get(Object local_ok)
  {
    long local_k = ((Long)local_ok).longValue();
    return containsKey(local_k) ? Short.valueOf(get(local_k)) : null;
  }
  
  public Short put(Long local_ok, Short local_ov)
  {
    long local_k = local_ok.longValue();
    boolean containsKey = containsKey(local_k);
    short local_v = put(local_k, local_ov.shortValue());
    return containsKey ? Short.valueOf(local_v) : null;
  }
  
  public Short remove(Object local_ok)
  {
    long local_k = ((Long)local_ok).longValue();
    boolean containsKey = containsKey(local_k);
    short local_v = remove(local_k);
    return containsKey ? Short.valueOf(local_v) : null;
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.longs.AbstractLong2ShortFunction
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */