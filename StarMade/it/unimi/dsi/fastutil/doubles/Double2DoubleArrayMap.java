package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.AbstractObjectIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

public class Double2DoubleArrayMap
  extends AbstractDouble2DoubleMap
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = 1L;
  private transient double[] key;
  private transient double[] value;
  private int size;
  
  public Double2DoubleArrayMap(double[] key, double[] value)
  {
    this.key = key;
    this.value = value;
    this.size = key.length;
    if (key.length != value.length) {
      throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
    }
  }
  
  public Double2DoubleArrayMap()
  {
    this.key = DoubleArrays.EMPTY_ARRAY;
    this.value = DoubleArrays.EMPTY_ARRAY;
  }
  
  public Double2DoubleArrayMap(int capacity)
  {
    this.key = new double[capacity];
    this.value = new double[capacity];
  }
  
  public Double2DoubleArrayMap(Double2DoubleMap local_m)
  {
    this(local_m.size());
    putAll(local_m);
  }
  
  public Double2DoubleArrayMap(Map<? extends Double, ? extends Double> local_m)
  {
    this(local_m.size());
    putAll(local_m);
  }
  
  public Double2DoubleArrayMap(double[] key, double[] value, int size)
  {
    this.key = key;
    this.value = value;
    this.size = size;
    if (key.length != value.length) {
      throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
    }
    if (size > key.length) {
      throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
    }
  }
  
  public Double2DoubleMap.FastEntrySet double2DoubleEntrySet()
  {
    return new EntrySet(null);
  }
  
  private int findKey(double local_k)
  {
    double[] key = this.key;
    int local_i = this.size;
    while (local_i-- != 0) {
      if (key[local_i] == local_k) {
        return local_i;
      }
    }
    return -1;
  }
  
  public double get(double local_k)
  {
    double[] key = this.key;
    int local_i = this.size;
    while (local_i-- != 0) {
      if (key[local_i] == local_k) {
        return this.value[local_i];
      }
    }
    return this.defRetValue;
  }
  
  public int size()
  {
    return this.size;
  }
  
  public void clear()
  {
    this.size = 0;
  }
  
  public boolean containsKey(double local_k)
  {
    return findKey(local_k) != -1;
  }
  
  public boolean containsValue(double local_v)
  {
    int local_i = this.size;
    while (local_i-- != 0) {
      if (this.value[local_i] == local_v) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isEmpty()
  {
    return this.size == 0;
  }
  
  public double put(double local_k, double local_v)
  {
    int oldKey = findKey(local_k);
    if (oldKey != -1)
    {
      double oldValue = this.value[oldKey];
      this.value[oldKey] = local_v;
      return oldValue;
    }
    if (this.size == this.key.length)
    {
      double[] oldValue = new double[this.size == 0 ? 2 : this.size * 2];
      double[] newValue = new double[this.size == 0 ? 2 : this.size * 2];
      int local_i = this.size;
      while (local_i-- != 0)
      {
        oldValue[local_i] = this.key[local_i];
        newValue[local_i] = this.value[local_i];
      }
      this.key = oldValue;
      this.value = newValue;
    }
    this.key[this.size] = local_k;
    this.value[this.size] = local_v;
    this.size += 1;
    return this.defRetValue;
  }
  
  public double remove(double local_k)
  {
    int oldPos = findKey(local_k);
    if (oldPos == -1) {
      return this.defRetValue;
    }
    double oldValue = this.value[oldPos];
    int tail = this.size - oldPos - 1;
    for (int local_i = 0; local_i < tail; local_i++)
    {
      this.key[(oldPos + local_i)] = this.key[(oldPos + local_i + 1)];
      this.value[(oldPos + local_i)] = this.value[(oldPos + local_i + 1)];
    }
    this.size -= 1;
    return oldValue;
  }
  
  public DoubleSet keySet()
  {
    return new DoubleArraySet(this.key, this.size);
  }
  
  public DoubleCollection values()
  {
    return DoubleCollections.unmodifiable(new DoubleArraySet(this.value, this.size));
  }
  
  public Double2DoubleArrayMap clone()
  {
    Double2DoubleArrayMap local_c;
    try
    {
      local_c = (Double2DoubleArrayMap)super.clone();
    }
    catch (CloneNotSupportedException cantHappen)
    {
      throw new InternalError();
    }
    local_c.key = ((double[])this.key.clone());
    local_c.value = ((double[])this.value.clone());
    return local_c;
  }
  
  private void writeObject(ObjectOutputStream local_s)
    throws IOException
  {
    local_s.defaultWriteObject();
    for (int local_i = 0; local_i < this.size; local_i++)
    {
      local_s.writeDouble(this.key[local_i]);
      local_s.writeDouble(this.value[local_i]);
    }
  }
  
  private void readObject(ObjectInputStream local_s)
    throws IOException, ClassNotFoundException
  {
    local_s.defaultReadObject();
    this.key = new double[this.size];
    this.value = new double[this.size];
    for (int local_i = 0; local_i < this.size; local_i++)
    {
      this.key[local_i] = local_s.readDouble();
      this.value[local_i] = local_s.readDouble();
    }
  }
  
  private final class EntrySet
    extends AbstractObjectSet<Double2DoubleMap.Entry>
    implements Double2DoubleMap.FastEntrySet
  {
    private EntrySet() {}
    
    public ObjectIterator<Double2DoubleMap.Entry> iterator()
    {
      new AbstractObjectIterator()
      {
        int next = 0;
        
        public boolean hasNext()
        {
          return this.next < Double2DoubleArrayMap.this.size;
        }
        
        public Double2DoubleMap.Entry next()
        {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          return new AbstractDouble2DoubleMap.BasicEntry(Double2DoubleArrayMap.this.key[this.next], Double2DoubleArrayMap.this.value[(this.next++)]);
        }
      };
    }
    
    public ObjectIterator<Double2DoubleMap.Entry> fastIterator()
    {
      new AbstractObjectIterator()
      {
        int next = 0;
        final AbstractDouble2DoubleMap.BasicEntry entry = new AbstractDouble2DoubleMap.BasicEntry(0.0D, 0.0D);
        
        public boolean hasNext()
        {
          return this.next < Double2DoubleArrayMap.this.size;
        }
        
        public Double2DoubleMap.Entry next()
        {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          this.entry.key = Double2DoubleArrayMap.this.key[this.next];
          this.entry.value = Double2DoubleArrayMap.this.value[(this.next++)];
          return this.entry;
        }
      };
    }
    
    public int size()
    {
      return Double2DoubleArrayMap.this.size;
    }
    
    public boolean contains(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Double, Double> local_e = (Map.Entry)local_o;
      double local_k = ((Double)local_e.getKey()).doubleValue();
      return (Double2DoubleArrayMap.this.containsKey(local_k)) && (Double2DoubleArrayMap.this.get(local_k) == ((Double)local_e.getValue()).doubleValue());
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.doubles.Double2DoubleArrayMap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */