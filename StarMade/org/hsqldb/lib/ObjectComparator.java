package org.hsqldb.lib;

import java.util.Comparator;

public abstract interface ObjectComparator
  extends Comparator
{
  public abstract int hashCode(Object paramObject);
  
  public abstract long longKey(Object paramObject);
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.hsqldb.lib.ObjectComparator
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */