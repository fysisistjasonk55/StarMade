package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

public class Short2DoubleOpenCustomHashMap
  extends AbstractShort2DoubleMap
  implements Serializable, Cloneable, Hash
{
  public static final long serialVersionUID = 0L;
  private static final boolean ASSERTS = false;
  protected transient short[] key;
  protected transient double[] value;
  protected transient boolean[] used;
  protected final float field_48;
  protected transient int field_49;
  protected transient int maxFill;
  protected transient int mask;
  protected int size;
  protected volatile transient Short2DoubleMap.FastEntrySet entries;
  protected volatile transient ShortSet keys;
  protected volatile transient DoubleCollection values;
  protected ShortHash.Strategy strategy;
  
  public Short2DoubleOpenCustomHashMap(int expected, float local_f, ShortHash.Strategy strategy)
  {
    this.strategy = strategy;
    if ((local_f <= 0.0F) || (local_f > 1.0F)) {
      throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
    }
    if (expected < 0) {
      throw new IllegalArgumentException("The expected number of elements must be nonnegative");
    }
    this.field_48 = local_f;
    this.field_49 = HashCommon.arraySize(expected, local_f);
    this.mask = (this.field_49 - 1);
    this.maxFill = HashCommon.maxFill(this.field_49, local_f);
    this.key = new short[this.field_49];
    this.value = new double[this.field_49];
    this.used = new boolean[this.field_49];
  }
  
  public Short2DoubleOpenCustomHashMap(int expected, ShortHash.Strategy strategy)
  {
    this(expected, 0.75F, strategy);
  }
  
  public Short2DoubleOpenCustomHashMap(ShortHash.Strategy strategy)
  {
    this(16, 0.75F, strategy);
  }
  
  public Short2DoubleOpenCustomHashMap(Map<? extends Short, ? extends Double> local_m, float local_f, ShortHash.Strategy strategy)
  {
    this(local_m.size(), local_f, strategy);
    putAll(local_m);
  }
  
  public Short2DoubleOpenCustomHashMap(Map<? extends Short, ? extends Double> local_m, ShortHash.Strategy strategy)
  {
    this(local_m, 0.75F, strategy);
  }
  
  public Short2DoubleOpenCustomHashMap(Short2DoubleMap local_m, float local_f, ShortHash.Strategy strategy)
  {
    this(local_m.size(), local_f, strategy);
    putAll(local_m);
  }
  
  public Short2DoubleOpenCustomHashMap(Short2DoubleMap local_m, ShortHash.Strategy strategy)
  {
    this(local_m, 0.75F, strategy);
  }
  
  public Short2DoubleOpenCustomHashMap(short[] local_k, double[] local_v, float local_f, ShortHash.Strategy strategy)
  {
    this(local_k.length, local_f, strategy);
    if (local_k.length != local_v.length) {
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + local_k.length + " and " + local_v.length + ")");
    }
    for (int local_i = 0; local_i < local_k.length; local_i++) {
      put(local_k[local_i], local_v[local_i]);
    }
  }
  
  public Short2DoubleOpenCustomHashMap(short[] local_k, double[] local_v, ShortHash.Strategy strategy)
  {
    this(local_k, local_v, 0.75F, strategy);
  }
  
  public ShortHash.Strategy strategy()
  {
    return this.strategy;
  }
  
  public double put(short local_k, double local_v)
  {
    for (int pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.strategy.equals(this.key[pos], local_k))
      {
        double oldValue = this.value[pos];
        this.value[pos] = local_v;
        return oldValue;
      }
    }
    this.used[pos] = true;
    this.key[pos] = local_k;
    this.value[pos] = local_v;
    if (++this.size >= this.maxFill) {
      rehash(HashCommon.arraySize(this.size + 1, this.field_48));
    }
    return this.defRetValue;
  }
  
  public Double put(Short local_ok, Double local_ov)
  {
    double local_v = local_ov.doubleValue();
    short local_k = local_ok.shortValue();
    for (int pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.strategy.equals(this.key[pos], local_k))
      {
        Double oldValue = Double.valueOf(this.value[pos]);
        this.value[pos] = local_v;
        return oldValue;
      }
    }
    this.used[pos] = true;
    this.key[pos] = local_k;
    this.value[pos] = local_v;
    if (++this.size >= this.maxFill) {
      rehash(HashCommon.arraySize(this.size + 1, this.field_48));
    }
    return null;
  }
  
  public double add(short local_k, double incr)
  {
    for (int pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.strategy.equals(this.key[pos], local_k))
      {
        double oldValue = this.value[pos];
        this.value[pos] += incr;
        return oldValue;
      }
    }
    this.used[pos] = true;
    this.key[pos] = local_k;
    this.value[pos] = (this.defRetValue + incr);
    if (++this.size >= this.maxFill) {
      rehash(HashCommon.arraySize(this.size + 1, this.field_48));
    }
    return this.defRetValue;
  }
  
  protected final int shiftKeys(int pos)
  {
    int last;
    for (;;)
    {
      for (pos = (last = pos) + 1 & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask)
      {
        int slot = HashCommon.murmurHash3(this.strategy.hashCode(this.key[pos])) & this.mask;
        if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) {
          break;
        }
      }
      if (this.used[pos] == 0) {
        break;
      }
      this.key[last] = this.key[pos];
      this.value[last] = this.value[pos];
    }
    this.used[last] = false;
    return last;
  }
  
  public double remove(short local_k)
  {
    for (int pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.strategy.equals(this.key[pos], local_k))
      {
        this.size -= 1;
        double local_v = this.value[pos];
        shiftKeys(pos);
        return local_v;
      }
    }
    return this.defRetValue;
  }
  
  public Double remove(Object local_ok)
  {
    short local_k = ((Short)local_ok).shortValue();
    for (int pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.strategy.equals(this.key[pos], local_k))
      {
        this.size -= 1;
        double local_v = this.value[pos];
        shiftKeys(pos);
        return Double.valueOf(local_v);
      }
    }
    return null;
  }
  
  public Double get(Short local_ok)
  {
    short local_k = local_ok.shortValue();
    for (int pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.strategy.equals(this.key[pos], local_k)) {
        return Double.valueOf(this.value[pos]);
      }
    }
    return null;
  }
  
  public double get(short local_k)
  {
    for (int pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.strategy.equals(this.key[pos], local_k)) {
        return this.value[pos];
      }
    }
    return this.defRetValue;
  }
  
  public boolean containsKey(short local_k)
  {
    for (int pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.strategy.equals(this.key[pos], local_k)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsValue(double local_v)
  {
    double[] value = this.value;
    boolean[] used = this.used;
    int local_i = this.field_49;
    while (local_i-- != 0) {
      if ((used[local_i] != 0) && (value[local_i] == local_v)) {
        return true;
      }
    }
    return false;
  }
  
  public void clear()
  {
    if (this.size == 0) {
      return;
    }
    this.size = 0;
    BooleanArrays.fill(this.used, false);
  }
  
  public int size()
  {
    return this.size;
  }
  
  public boolean isEmpty()
  {
    return this.size == 0;
  }
  
  @Deprecated
  public void growthFactor(int growthFactor) {}
  
  @Deprecated
  public int growthFactor()
  {
    return 16;
  }
  
  public Short2DoubleMap.FastEntrySet short2DoubleEntrySet()
  {
    if (this.entries == null) {
      this.entries = new MapEntrySet(null);
    }
    return this.entries;
  }
  
  public ShortSet keySet()
  {
    if (this.keys == null) {
      this.keys = new KeySet(null);
    }
    return this.keys;
  }
  
  public DoubleCollection values()
  {
    if (this.values == null) {
      this.values = new AbstractDoubleCollection()
      {
        public DoubleIterator iterator()
        {
          return new Short2DoubleOpenCustomHashMap.ValueIterator(Short2DoubleOpenCustomHashMap.this);
        }
        
        public int size()
        {
          return Short2DoubleOpenCustomHashMap.this.size;
        }
        
        public boolean contains(double local_v)
        {
          return Short2DoubleOpenCustomHashMap.this.containsValue(local_v);
        }
        
        public void clear()
        {
          Short2DoubleOpenCustomHashMap.this.clear();
        }
      };
    }
    return this.values;
  }
  
  @Deprecated
  public boolean rehash()
  {
    return true;
  }
  
  public boolean trim()
  {
    int local_l = HashCommon.arraySize(this.size, this.field_48);
    if (local_l >= this.field_49) {
      return true;
    }
    try
    {
      rehash(local_l);
    }
    catch (OutOfMemoryError cantDoIt)
    {
      return false;
    }
    return true;
  }
  
  public boolean trim(int local_n)
  {
    int local_l = HashCommon.nextPowerOfTwo((int)Math.ceil(local_n / this.field_48));
    if (this.field_49 <= local_l) {
      return true;
    }
    try
    {
      rehash(local_l);
    }
    catch (OutOfMemoryError cantDoIt)
    {
      return false;
    }
    return true;
  }
  
  protected void rehash(int newN)
  {
    int local_i = 0;
    boolean[] used = this.used;
    short[] key = this.key;
    double[] value = this.value;
    int newMask = newN - 1;
    short[] newKey = new short[newN];
    double[] newValue = new double[newN];
    boolean[] newUsed = new boolean[newN];
    int local_j = this.size;
    while (local_j-- != 0)
    {
      while (used[local_i] == 0) {
        local_i++;
      }
      short local_k = key[local_i];
      for (int pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & newMask; newUsed[pos] != 0; pos = pos + 1 & newMask) {}
      newUsed[pos] = true;
      newKey[pos] = local_k;
      newValue[pos] = value[local_i];
      local_i++;
    }
    this.field_49 = newN;
    this.mask = newMask;
    this.maxFill = HashCommon.maxFill(this.field_49, this.field_48);
    this.key = newKey;
    this.value = newValue;
    this.used = newUsed;
  }
  
  public Short2DoubleOpenCustomHashMap clone()
  {
    Short2DoubleOpenCustomHashMap local_c;
    try
    {
      local_c = (Short2DoubleOpenCustomHashMap)super.clone();
    }
    catch (CloneNotSupportedException cantHappen)
    {
      throw new InternalError();
    }
    local_c.keys = null;
    local_c.values = null;
    local_c.entries = null;
    local_c.key = ((short[])this.key.clone());
    local_c.value = ((double[])this.value.clone());
    local_c.used = ((boolean[])this.used.clone());
    local_c.strategy = this.strategy;
    return local_c;
  }
  
  public int hashCode()
  {
    int local_h = 0;
    int local_j = this.size;
    int local_i = 0;
    int local_t = 0;
    while (local_j-- != 0)
    {
      while (this.used[local_i] == 0) {
        local_i++;
      }
      local_t = this.strategy.hashCode(this.key[local_i]);
      local_t ^= HashCommon.double2int(this.value[local_i]);
      local_h += local_t;
      local_i++;
    }
    return local_h;
  }
  
  private void writeObject(ObjectOutputStream local_s)
    throws IOException
  {
    short[] key = this.key;
    double[] value = this.value;
    MapIterator local_i = new MapIterator(null);
    local_s.defaultWriteObject();
    int local_j = this.size;
    while (local_j-- != 0)
    {
      int local_e = local_i.nextEntry();
      local_s.writeShort(key[local_e]);
      local_s.writeDouble(value[local_e]);
    }
  }
  
  private void readObject(ObjectInputStream local_s)
    throws IOException, ClassNotFoundException
  {
    local_s.defaultReadObject();
    this.field_49 = HashCommon.arraySize(this.size, this.field_48);
    this.maxFill = HashCommon.maxFill(this.field_49, this.field_48);
    this.mask = (this.field_49 - 1);
    short[] key = this.key = new short[this.field_49];
    double[] value = this.value = new double[this.field_49];
    boolean[] used = this.used = new boolean[this.field_49];
    int local_i = this.size;
    int pos = 0;
    while (local_i-- != 0)
    {
      short local_k = local_s.readShort();
      double local_v = local_s.readDouble();
      for (pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & this.mask; used[pos] != 0; pos = pos + 1 & this.mask) {}
      used[pos] = true;
      key[pos] = local_k;
      value[pos] = local_v;
    }
  }
  
  private void checkTable() {}
  
  private final class ValueIterator
    extends Short2DoubleOpenCustomHashMap.MapIterator
    implements DoubleIterator
  {
    public ValueIterator()
    {
      super(null);
    }
    
    public double nextDouble()
    {
      return Short2DoubleOpenCustomHashMap.this.value[nextEntry()];
    }
    
    public Double next()
    {
      return Double.valueOf(Short2DoubleOpenCustomHashMap.this.value[nextEntry()]);
    }
  }
  
  private final class KeySet
    extends AbstractShortSet
  {
    private KeySet() {}
    
    public ShortIterator iterator()
    {
      return new Short2DoubleOpenCustomHashMap.KeyIterator(Short2DoubleOpenCustomHashMap.this);
    }
    
    public int size()
    {
      return Short2DoubleOpenCustomHashMap.this.size;
    }
    
    public boolean contains(short local_k)
    {
      return Short2DoubleOpenCustomHashMap.this.containsKey(local_k);
    }
    
    public boolean remove(short local_k)
    {
      int oldSize = Short2DoubleOpenCustomHashMap.this.size;
      Short2DoubleOpenCustomHashMap.this.remove(local_k);
      return Short2DoubleOpenCustomHashMap.this.size != oldSize;
    }
    
    public void clear()
    {
      Short2DoubleOpenCustomHashMap.this.clear();
    }
  }
  
  private final class KeyIterator
    extends Short2DoubleOpenCustomHashMap.MapIterator
    implements ShortIterator
  {
    public KeyIterator()
    {
      super(null);
    }
    
    public short nextShort()
    {
      return Short2DoubleOpenCustomHashMap.this.key[nextEntry()];
    }
    
    public Short next()
    {
      return Short.valueOf(Short2DoubleOpenCustomHashMap.this.key[nextEntry()]);
    }
  }
  
  private final class MapEntrySet
    extends AbstractObjectSet<Short2DoubleMap.Entry>
    implements Short2DoubleMap.FastEntrySet
  {
    private MapEntrySet() {}
    
    public ObjectIterator<Short2DoubleMap.Entry> iterator()
    {
      return new Short2DoubleOpenCustomHashMap.EntryIterator(Short2DoubleOpenCustomHashMap.this, null);
    }
    
    public ObjectIterator<Short2DoubleMap.Entry> fastIterator()
    {
      return new Short2DoubleOpenCustomHashMap.FastEntryIterator(Short2DoubleOpenCustomHashMap.this, null);
    }
    
    public boolean contains(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Short, Double> local_e = (Map.Entry)local_o;
      short local_k = ((Short)local_e.getKey()).shortValue();
      for (int pos = HashCommon.murmurHash3(Short2DoubleOpenCustomHashMap.this.strategy.hashCode(local_k)) & Short2DoubleOpenCustomHashMap.this.mask; Short2DoubleOpenCustomHashMap.this.used[pos] != 0; pos = pos + 1 & Short2DoubleOpenCustomHashMap.this.mask) {
        if (Short2DoubleOpenCustomHashMap.this.strategy.equals(Short2DoubleOpenCustomHashMap.this.key[pos], local_k)) {
          return Short2DoubleOpenCustomHashMap.this.value[pos] == ((Double)local_e.getValue()).doubleValue();
        }
      }
      return false;
    }
    
    public boolean remove(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Short, Double> local_e = (Map.Entry)local_o;
      short local_k = ((Short)local_e.getKey()).shortValue();
      for (int pos = HashCommon.murmurHash3(Short2DoubleOpenCustomHashMap.this.strategy.hashCode(local_k)) & Short2DoubleOpenCustomHashMap.this.mask; Short2DoubleOpenCustomHashMap.this.used[pos] != 0; pos = pos + 1 & Short2DoubleOpenCustomHashMap.this.mask) {
        if (Short2DoubleOpenCustomHashMap.this.strategy.equals(Short2DoubleOpenCustomHashMap.this.key[pos], local_k))
        {
          Short2DoubleOpenCustomHashMap.this.remove(local_e.getKey());
          return true;
        }
      }
      return false;
    }
    
    public int size()
    {
      return Short2DoubleOpenCustomHashMap.this.size;
    }
    
    public void clear()
    {
      Short2DoubleOpenCustomHashMap.this.clear();
    }
  }
  
  private class FastEntryIterator
    extends Short2DoubleOpenCustomHashMap.MapIterator
    implements ObjectIterator<Short2DoubleMap.Entry>
  {
    final AbstractShort2DoubleMap.BasicEntry entry = new AbstractShort2DoubleMap.BasicEntry((short)0, 0.0D);
    
    private FastEntryIterator()
    {
      super(null);
    }
    
    public AbstractShort2DoubleMap.BasicEntry next()
    {
      int local_e = nextEntry();
      this.entry.key = Short2DoubleOpenCustomHashMap.this.key[local_e];
      this.entry.value = Short2DoubleOpenCustomHashMap.this.value[local_e];
      return this.entry;
    }
  }
  
  private class EntryIterator
    extends Short2DoubleOpenCustomHashMap.MapIterator
    implements ObjectIterator<Short2DoubleMap.Entry>
  {
    private Short2DoubleOpenCustomHashMap.MapEntry entry;
    
    private EntryIterator()
    {
      super(null);
    }
    
    public Short2DoubleMap.Entry next()
    {
      return this.entry = new Short2DoubleOpenCustomHashMap.MapEntry(Short2DoubleOpenCustomHashMap.this, nextEntry());
    }
    
    public void remove()
    {
      super.remove();
      Short2DoubleOpenCustomHashMap.MapEntry.access$102(this.entry, -1);
    }
  }
  
  private class MapIterator
  {
    int pos = Short2DoubleOpenCustomHashMap.this.field_49;
    int last = -1;
    int field_485 = Short2DoubleOpenCustomHashMap.this.size;
    ShortArrayList wrapped;
    
    private MapIterator()
    {
      boolean[] used = Short2DoubleOpenCustomHashMap.this.used;
      while ((this.field_485 != 0) && (used[(--this.pos)] == 0)) {}
    }
    
    public boolean hasNext()
    {
      return this.field_485 != 0;
    }
    
    public int nextEntry()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      this.field_485 -= 1;
      if (this.pos < 0)
      {
        short local_k = this.wrapped.getShort(-(this.last = --this.pos) - 2);
        for (int pos = HashCommon.murmurHash3(Short2DoubleOpenCustomHashMap.this.strategy.hashCode(local_k)) & Short2DoubleOpenCustomHashMap.this.mask; Short2DoubleOpenCustomHashMap.this.used[pos] != 0; pos = pos + 1 & Short2DoubleOpenCustomHashMap.this.mask) {
          if (Short2DoubleOpenCustomHashMap.this.strategy.equals(Short2DoubleOpenCustomHashMap.this.key[pos], local_k)) {
            return pos;
          }
        }
      }
      this.last = this.pos;
      if (this.field_485 != 0)
      {
        boolean[] local_k = Short2DoubleOpenCustomHashMap.this.used;
        while ((this.pos-- != 0) && (local_k[this.pos] == 0)) {}
      }
      return this.last;
    }
    
    protected final int shiftKeys(int pos)
    {
      int last;
      for (;;)
      {
        for (pos = (last = pos) + 1 & Short2DoubleOpenCustomHashMap.this.mask; Short2DoubleOpenCustomHashMap.this.used[pos] != 0; pos = pos + 1 & Short2DoubleOpenCustomHashMap.this.mask)
        {
          int slot = HashCommon.murmurHash3(Short2DoubleOpenCustomHashMap.this.strategy.hashCode(Short2DoubleOpenCustomHashMap.this.key[pos])) & Short2DoubleOpenCustomHashMap.this.mask;
          if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) {
            break;
          }
        }
        if (Short2DoubleOpenCustomHashMap.this.used[pos] == 0) {
          break;
        }
        if (pos < last)
        {
          if (this.wrapped == null) {
            this.wrapped = new ShortArrayList();
          }
          this.wrapped.add(Short2DoubleOpenCustomHashMap.this.key[pos]);
        }
        Short2DoubleOpenCustomHashMap.this.key[last] = Short2DoubleOpenCustomHashMap.this.key[pos];
        Short2DoubleOpenCustomHashMap.this.value[last] = Short2DoubleOpenCustomHashMap.this.value[pos];
      }
      Short2DoubleOpenCustomHashMap.this.used[last] = false;
      return last;
    }
    
    public void remove()
    {
      if (this.last == -1) {
        throw new IllegalStateException();
      }
      if (this.pos < -1)
      {
        Short2DoubleOpenCustomHashMap.this.remove(this.wrapped.getShort(-this.pos - 2));
        this.last = -1;
        return;
      }
      Short2DoubleOpenCustomHashMap.this.size -= 1;
      if ((shiftKeys(this.last) == this.pos) && (this.field_485 > 0))
      {
        this.field_485 += 1;
        nextEntry();
      }
      this.last = -1;
    }
    
    public int skip(int local_n)
    {
      int local_i = local_n;
      while ((local_i-- != 0) && (hasNext())) {
        nextEntry();
      }
      return local_n - local_i - 1;
    }
  }
  
  private final class MapEntry
    implements Short2DoubleMap.Entry, Map.Entry<Short, Double>
  {
    private int index;
    
    MapEntry(int index)
    {
      this.index = index;
    }
    
    public Short getKey()
    {
      return Short.valueOf(Short2DoubleOpenCustomHashMap.this.key[this.index]);
    }
    
    public short getShortKey()
    {
      return Short2DoubleOpenCustomHashMap.this.key[this.index];
    }
    
    public Double getValue()
    {
      return Double.valueOf(Short2DoubleOpenCustomHashMap.this.value[this.index]);
    }
    
    public double getDoubleValue()
    {
      return Short2DoubleOpenCustomHashMap.this.value[this.index];
    }
    
    public double setValue(double local_v)
    {
      double oldValue = Short2DoubleOpenCustomHashMap.this.value[this.index];
      Short2DoubleOpenCustomHashMap.this.value[this.index] = local_v;
      return oldValue;
    }
    
    public Double setValue(Double local_v)
    {
      return Double.valueOf(setValue(local_v.doubleValue()));
    }
    
    public boolean equals(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Short, Double> local_e = (Map.Entry)local_o;
      return (Short2DoubleOpenCustomHashMap.this.strategy.equals(Short2DoubleOpenCustomHashMap.this.key[this.index], ((Short)local_e.getKey()).shortValue())) && (Short2DoubleOpenCustomHashMap.this.value[this.index] == ((Double)local_e.getValue()).doubleValue());
    }
    
    public int hashCode()
    {
      return Short2DoubleOpenCustomHashMap.this.strategy.hashCode(Short2DoubleOpenCustomHashMap.this.key[this.index]) ^ HashCommon.double2int(Short2DoubleOpenCustomHashMap.this.value[this.index]);
    }
    
    public String toString()
    {
      return Short2DoubleOpenCustomHashMap.this.key[this.index] + "=>" + Short2DoubleOpenCustomHashMap.this.value[this.index];
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.shorts.Short2DoubleOpenCustomHashMap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */