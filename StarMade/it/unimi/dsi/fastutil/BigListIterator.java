package it.unimi.dsi.fastutil;

public abstract interface BigListIterator<K>
  extends BidirectionalIterator<K>
{
  public abstract long nextIndex();
  
  public abstract long previousIndex();
  
  public abstract long skip(long paramLong);
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.BigListIterator
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */