package it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;
import java.util.NoSuchElementException;

public class BooleanBigListIterators
{
  public static final EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new EmptyBigListIterator();
  
  public static BooleanBigListIterator singleton(boolean element)
  {
    return new SingletonBigListIterator(element);
  }
  
  public static BooleanBigListIterator unmodifiable(BooleanBigListIterator local_i)
  {
    return new UnmodifiableBigListIterator(local_i);
  }
  
  public static BooleanBigListIterator asBigListIterator(BooleanListIterator local_i)
  {
    return new BigListIteratorListIterator(local_i);
  }
  
  public static class BigListIteratorListIterator
    extends AbstractBooleanBigListIterator
  {
    protected final BooleanListIterator field_60;
    
    protected BigListIteratorListIterator(BooleanListIterator local_i)
    {
      this.field_60 = local_i;
    }
    
    private int intDisplacement(long local_n)
    {
      if ((local_n < -2147483648L) || (local_n > 2147483647L)) {
        throw new IndexOutOfBoundsException("This big iterator is restricted to 32-bit displacements");
      }
      return (int)local_n;
    }
    
    public void set(boolean local_ok)
    {
      this.field_60.set(local_ok);
    }
    
    public void add(boolean local_ok)
    {
      this.field_60.add(local_ok);
    }
    
    public int back(int local_n)
    {
      return this.field_60.back(local_n);
    }
    
    public long back(long local_n)
    {
      return this.field_60.back(intDisplacement(local_n));
    }
    
    public void remove()
    {
      this.field_60.remove();
    }
    
    public int skip(int local_n)
    {
      return this.field_60.skip(local_n);
    }
    
    public long skip(long local_n)
    {
      return this.field_60.skip(intDisplacement(local_n));
    }
    
    public boolean hasNext()
    {
      return this.field_60.hasNext();
    }
    
    public boolean hasPrevious()
    {
      return this.field_60.hasPrevious();
    }
    
    public boolean nextBoolean()
    {
      return this.field_60.nextBoolean();
    }
    
    public boolean previousBoolean()
    {
      return this.field_60.previousBoolean();
    }
    
    public long nextIndex()
    {
      return this.field_60.nextIndex();
    }
    
    public long previousIndex()
    {
      return this.field_60.previousIndex();
    }
  }
  
  public static class UnmodifiableBigListIterator
    extends AbstractBooleanBigListIterator
  {
    protected final BooleanBigListIterator field_60;
    
    public UnmodifiableBigListIterator(BooleanBigListIterator local_i)
    {
      this.field_60 = local_i;
    }
    
    public boolean hasNext()
    {
      return this.field_60.hasNext();
    }
    
    public boolean hasPrevious()
    {
      return this.field_60.hasPrevious();
    }
    
    public boolean nextBoolean()
    {
      return this.field_60.nextBoolean();
    }
    
    public boolean previousBoolean()
    {
      return this.field_60.previousBoolean();
    }
    
    public long nextIndex()
    {
      return this.field_60.nextIndex();
    }
    
    public long previousIndex()
    {
      return this.field_60.previousIndex();
    }
    
    public Boolean next()
    {
      return (Boolean)this.field_60.next();
    }
    
    public Boolean previous()
    {
      return (Boolean)this.field_60.previous();
    }
  }
  
  private static class SingletonBigListIterator
    extends AbstractBooleanBigListIterator
  {
    private final boolean element;
    private int curr;
    
    public SingletonBigListIterator(boolean element)
    {
      this.element = element;
    }
    
    public boolean hasNext()
    {
      return this.curr == 0;
    }
    
    public boolean hasPrevious()
    {
      return this.curr == 1;
    }
    
    public boolean nextBoolean()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      this.curr = 1;
      return this.element;
    }
    
    public boolean previousBoolean()
    {
      if (!hasPrevious()) {
        throw new NoSuchElementException();
      }
      this.curr = 0;
      return this.element;
    }
    
    public long nextIndex()
    {
      return this.curr;
    }
    
    public long previousIndex()
    {
      return this.curr - 1;
    }
  }
  
  public static class EmptyBigListIterator
    extends AbstractBooleanBigListIterator
    implements Serializable, Cloneable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    
    public boolean hasNext()
    {
      return false;
    }
    
    public boolean hasPrevious()
    {
      return false;
    }
    
    public boolean nextBoolean()
    {
      throw new NoSuchElementException();
    }
    
    public boolean previousBoolean()
    {
      throw new NoSuchElementException();
    }
    
    public long nextIndex()
    {
      return 0L;
    }
    
    public long previousIndex()
    {
      return -1L;
    }
    
    public long skip(long local_n)
    {
      return 0L;
    }
    
    public long back(long local_n)
    {
      return 0L;
    }
    
    public Object clone()
    {
      return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
    }
    
    private Object readResolve()
    {
      return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.booleans.BooleanBigListIterators
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */