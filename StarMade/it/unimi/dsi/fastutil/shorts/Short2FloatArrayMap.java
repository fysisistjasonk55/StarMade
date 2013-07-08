package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
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

public class Short2FloatArrayMap
  extends AbstractShort2FloatMap
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = 1L;
  private transient short[] key;
  private transient float[] value;
  private int size;
  
  public Short2FloatArrayMap(short[] key, float[] value)
  {
    this.key = key;
    this.value = value;
    this.size = key.length;
    if (key.length != value.length) {
      throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
    }
  }
  
  public Short2FloatArrayMap()
  {
    this.key = ShortArrays.EMPTY_ARRAY;
    this.value = FloatArrays.EMPTY_ARRAY;
  }
  
  public Short2FloatArrayMap(int capacity)
  {
    this.key = new short[capacity];
    this.value = new float[capacity];
  }
  
  public Short2FloatArrayMap(Short2FloatMap local_m)
  {
    this(local_m.size());
    putAll(local_m);
  }
  
  public Short2FloatArrayMap(Map<? extends Short, ? extends Float> local_m)
  {
    this(local_m.size());
    putAll(local_m);
  }
  
  public Short2FloatArrayMap(short[] key, float[] value, int size)
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
  
  public Short2FloatMap.FastEntrySet short2FloatEntrySet()
  {
    return new EntrySet(null);
  }
  
  private int findKey(short local_k)
  {
    short[] key = this.key;
    int local_i = this.size;
    while (local_i-- != 0) {
      if (key[local_i] == local_k) {
        return local_i;
      }
    }
    return -1;
  }
  
  public float get(short local_k)
  {
    short[] key = this.key;
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
  
  public boolean containsKey(short local_k)
  {
    return findKey(local_k) != -1;
  }
  
  public boolean containsValue(float local_v)
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
  
  public float put(short local_k, float local_v)
  {
    int oldKey = findKey(local_k);
    if (oldKey != -1)
    {
      float oldValue = this.value[oldKey];
      this.value[oldKey] = local_v;
      return oldValue;
    }
    if (this.size == this.key.length)
    {
      short[] oldValue = new short[this.size == 0 ? 2 : this.size * 2];
      float[] newValue = new float[this.size == 0 ? 2 : this.size * 2];
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
  
  public float remove(short local_k)
  {
    int oldPos = findKey(local_k);
    if (oldPos == -1) {
      return this.defRetValue;
    }
    float oldValue = this.value[oldPos];
    int tail = this.size - oldPos - 1;
    for (int local_i = 0; local_i < tail; local_i++)
    {
      this.key[(oldPos + local_i)] = this.key[(oldPos + local_i + 1)];
      this.value[(oldPos + local_i)] = this.value[(oldPos + local_i + 1)];
    }
    this.size -= 1;
    return oldValue;
  }
  
  public ShortSet keySet()
  {
    return new ShortArraySet(this.key, this.size);
  }
  
  public FloatCollection values()
  {
    return FloatCollections.unmodifiable(new FloatArraySet(this.value, this.size));
  }
  
  public Short2FloatArrayMap clone()
  {
    Short2FloatArrayMap local_c;
    try
    {
      local_c = (Short2FloatArrayMap)super.clone();
    }
    catch (CloneNotSupportedException cantHappen)
    {
      throw new InternalError();
    }
    local_c.key = ((short[])this.key.clone());
    local_c.value = ((float[])this.value.clone());
    return local_c;
  }
  
  private void writeObject(ObjectOutputStream local_s)
    throws IOException
  {
    local_s.defaultWriteObject();
    for (int local_i = 0; local_i < this.size; local_i++)
    {
      local_s.writeShort(this.key[local_i]);
      local_s.writeFloat(this.value[local_i]);
    }
  }
  
  private void readObject(ObjectInputStream local_s)
    throws IOException, ClassNotFoundException
  {
    local_s.defaultReadObject();
    this.key = new short[this.size];
    this.value = new float[this.size];
    for (int local_i = 0; local_i < this.size; local_i++)
    {
      this.key[local_i] = local_s.readShort();
      this.value[local_i] = local_s.readFloat();
    }
  }
  
  private final class EntrySet
    extends AbstractObjectSet<Short2FloatMap.Entry>
    implements Short2FloatMap.FastEntrySet
  {
    private EntrySet() {}
    
    public ObjectIterator<Short2FloatMap.Entry> iterator()
    {
      new AbstractObjectIterator()
      {
        int next = 0;
        
        public boolean hasNext()
        {
          return this.next < Short2FloatArrayMap.this.size;
        }
        
        public Short2FloatMap.Entry next()
        {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          return new AbstractShort2FloatMap.BasicEntry(Short2FloatArrayMap.this.key[this.next], Short2FloatArrayMap.this.value[(this.next++)]);
        }
      };
    }
    
    public ObjectIterator<Short2FloatMap.Entry> fastIterator()
    {
      new AbstractObjectIterator()
      {
        int next = 0;
        final AbstractShort2FloatMap.BasicEntry entry = new AbstractShort2FloatMap.BasicEntry((short)0, 0.0F);
        
        public boolean hasNext()
        {
          return this.next < Short2FloatArrayMap.this.size;
        }
        
        public Short2FloatMap.Entry next()
        {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          this.entry.key = Short2FloatArrayMap.this.key[this.next];
          this.entry.value = Short2FloatArrayMap.this.value[(this.next++)];
          return this.entry;
        }
      };
    }
    
    public int size()
    {
      return Short2FloatArrayMap.this.size;
    }
    
    public boolean contains(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Short, Float> local_e = (Map.Entry)local_o;
      short local_k = ((Short)local_e.getKey()).shortValue();
      return (Short2FloatArrayMap.this.containsKey(local_k)) && (Short2FloatArrayMap.this.get(local_k) == ((Float)local_e.getValue()).floatValue());
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.shorts.Short2FloatArrayMap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */