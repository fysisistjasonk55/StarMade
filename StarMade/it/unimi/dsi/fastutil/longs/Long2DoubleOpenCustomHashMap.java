package it.unimi.dsi.fastutil.longs;

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

public class Long2DoubleOpenCustomHashMap
  extends AbstractLong2DoubleMap
  implements Serializable, Cloneable, Hash
{
  public static final long serialVersionUID = 0L;
  private static final boolean ASSERTS = false;
  protected transient long[] key;
  protected transient double[] value;
  protected transient boolean[] used;
  protected final float field_48;
  protected transient int field_49;
  protected transient int maxFill;
  protected transient int mask;
  protected int size;
  protected volatile transient Long2DoubleMap.FastEntrySet entries;
  protected volatile transient LongSet keys;
  protected volatile transient DoubleCollection values;
  protected LongHash.Strategy strategy;
  
  public Long2DoubleOpenCustomHashMap(int expected, float local_f, LongHash.Strategy strategy)
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
    this.key = new long[this.field_49];
    this.value = new double[this.field_49];
    this.used = new boolean[this.field_49];
  }
  
  public Long2DoubleOpenCustomHashMap(int expected, LongHash.Strategy strategy)
  {
    this(expected, 0.75F, strategy);
  }
  
  public Long2DoubleOpenCustomHashMap(LongHash.Strategy strategy)
  {
    this(16, 0.75F, strategy);
  }
  
  public Long2DoubleOpenCustomHashMap(Map<? extends Long, ? extends Double> local_m, float local_f, LongHash.Strategy strategy)
  {
    this(local_m.size(), local_f, strategy);
    putAll(local_m);
  }
  
  public Long2DoubleOpenCustomHashMap(Map<? extends Long, ? extends Double> local_m, LongHash.Strategy strategy)
  {
    this(local_m, 0.75F, strategy);
  }
  
  public Long2DoubleOpenCustomHashMap(Long2DoubleMap local_m, float local_f, LongHash.Strategy strategy)
  {
    this(local_m.size(), local_f, strategy);
    putAll(local_m);
  }
  
  public Long2DoubleOpenCustomHashMap(Long2DoubleMap local_m, LongHash.Strategy strategy)
  {
    this(local_m, 0.75F, strategy);
  }
  
  public Long2DoubleOpenCustomHashMap(long[] local_k, double[] local_v, float local_f, LongHash.Strategy strategy)
  {
    this(local_k.length, local_f, strategy);
    if (local_k.length != local_v.length) {
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + local_k.length + " and " + local_v.length + ")");
    }
    for (int local_i = 0; local_i < local_k.length; local_i++) {
      put(local_k[local_i], local_v[local_i]);
    }
  }
  
  public Long2DoubleOpenCustomHashMap(long[] local_k, double[] local_v, LongHash.Strategy strategy)
  {
    this(local_k, local_v, 0.75F, strategy);
  }
  
  public LongHash.Strategy strategy()
  {
    return this.strategy;
  }
  
  public double put(long local_k, double local_v)
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
  
  public Double put(Long local_ok, Double local_ov)
  {
    double local_v = local_ov.doubleValue();
    long local_k = local_ok.longValue();
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
  
  public double add(long local_k, double incr)
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
  
  public double remove(long local_k)
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
    long local_k = ((Long)local_ok).longValue();
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
  
  public Double get(Long local_ok)
  {
    long local_k = local_ok.longValue();
    for (int pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.strategy.equals(this.key[pos], local_k)) {
        return Double.valueOf(this.value[pos]);
      }
    }
    return null;
  }
  
  public double get(long local_k)
  {
    for (int pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.strategy.equals(this.key[pos], local_k)) {
        return this.value[pos];
      }
    }
    return this.defRetValue;
  }
  
  public boolean containsKey(long local_k)
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
  
  public Long2DoubleMap.FastEntrySet long2DoubleEntrySet()
  {
    if (this.entries == null) {
      this.entries = new MapEntrySet(null);
    }
    return this.entries;
  }
  
  public LongSet keySet()
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
          return new Long2DoubleOpenCustomHashMap.ValueIterator(Long2DoubleOpenCustomHashMap.this);
        }
        
        public int size()
        {
          return Long2DoubleOpenCustomHashMap.this.size;
        }
        
        public boolean contains(double local_v)
        {
          return Long2DoubleOpenCustomHashMap.this.containsValue(local_v);
        }
        
        public void clear()
        {
          Long2DoubleOpenCustomHashMap.this.clear();
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
    long[] key = this.key;
    double[] value = this.value;
    int newMask = newN - 1;
    long[] newKey = new long[newN];
    double[] newValue = new double[newN];
    boolean[] newUsed = new boolean[newN];
    int local_j = this.size;
    while (local_j-- != 0)
    {
      while (used[local_i] == 0) {
        local_i++;
      }
      long local_k = key[local_i];
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
  
  public Long2DoubleOpenCustomHashMap clone()
  {
    Long2DoubleOpenCustomHashMap local_c;
    try
    {
      local_c = (Long2DoubleOpenCustomHashMap)super.clone();
    }
    catch (CloneNotSupportedException cantHappen)
    {
      throw new InternalError();
    }
    local_c.keys = null;
    local_c.values = null;
    local_c.entries = null;
    local_c.key = ((long[])this.key.clone());
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
    long[] key = this.key;
    double[] value = this.value;
    MapIterator local_i = new MapIterator(null);
    local_s.defaultWriteObject();
    int local_j = this.size;
    while (local_j-- != 0)
    {
      int local_e = local_i.nextEntry();
      local_s.writeLong(key[local_e]);
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
    long[] key = this.key = new long[this.field_49];
    double[] value = this.value = new double[this.field_49];
    boolean[] used = this.used = new boolean[this.field_49];
    int local_i = this.size;
    int pos = 0;
    while (local_i-- != 0)
    {
      long local_k = local_s.readLong();
      double local_v = local_s.readDouble();
      for (pos = HashCommon.murmurHash3(this.strategy.hashCode(local_k)) & this.mask; used[pos] != 0; pos = pos + 1 & this.mask) {}
      used[pos] = true;
      key[pos] = local_k;
      value[pos] = local_v;
    }
  }
  
  private void checkTable() {}
  
  private final class ValueIterator
    extends Long2DoubleOpenCustomHashMap.MapIterator
    implements DoubleIterator
  {
    public ValueIterator()
    {
      super(null);
    }
    
    public double nextDouble()
    {
      return Long2DoubleOpenCustomHashMap.this.value[nextEntry()];
    }
    
    public Double next()
    {
      return Double.valueOf(Long2DoubleOpenCustomHashMap.this.value[nextEntry()]);
    }
  }
  
  private final class KeySet
    extends AbstractLongSet
  {
    private KeySet() {}
    
    public LongIterator iterator()
    {
      return new Long2DoubleOpenCustomHashMap.KeyIterator(Long2DoubleOpenCustomHashMap.this);
    }
    
    public int size()
    {
      return Long2DoubleOpenCustomHashMap.this.size;
    }
    
    public boolean contains(long local_k)
    {
      return Long2DoubleOpenCustomHashMap.this.containsKey(local_k);
    }
    
    public boolean remove(long local_k)
    {
      int oldSize = Long2DoubleOpenCustomHashMap.this.size;
      Long2DoubleOpenCustomHashMap.this.remove(local_k);
      return Long2DoubleOpenCustomHashMap.this.size != oldSize;
    }
    
    public void clear()
    {
      Long2DoubleOpenCustomHashMap.this.clear();
    }
  }
  
  private final class KeyIterator
    extends Long2DoubleOpenCustomHashMap.MapIterator
    implements LongIterator
  {
    public KeyIterator()
    {
      super(null);
    }
    
    public long nextLong()
    {
      return Long2DoubleOpenCustomHashMap.this.key[nextEntry()];
    }
    
    public Long next()
    {
      return Long.valueOf(Long2DoubleOpenCustomHashMap.this.key[nextEntry()]);
    }
  }
  
  private final class MapEntrySet
    extends AbstractObjectSet<Long2DoubleMap.Entry>
    implements Long2DoubleMap.FastEntrySet
  {
    private MapEntrySet() {}
    
    public ObjectIterator<Long2DoubleMap.Entry> iterator()
    {
      return new Long2DoubleOpenCustomHashMap.EntryIterator(Long2DoubleOpenCustomHashMap.this, null);
    }
    
    public ObjectIterator<Long2DoubleMap.Entry> fastIterator()
    {
      return new Long2DoubleOpenCustomHashMap.FastEntryIterator(Long2DoubleOpenCustomHashMap.this, null);
    }
    
    public boolean contains(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Long, Double> local_e = (Map.Entry)local_o;
      long local_k = ((Long)local_e.getKey()).longValue();
      for (int pos = HashCommon.murmurHash3(Long2DoubleOpenCustomHashMap.this.strategy.hashCode(local_k)) & Long2DoubleOpenCustomHashMap.this.mask; Long2DoubleOpenCustomHashMap.this.used[pos] != 0; pos = pos + 1 & Long2DoubleOpenCustomHashMap.this.mask) {
        if (Long2DoubleOpenCustomHashMap.this.strategy.equals(Long2DoubleOpenCustomHashMap.this.key[pos], local_k)) {
          return Long2DoubleOpenCustomHashMap.this.value[pos] == ((Double)local_e.getValue()).doubleValue();
        }
      }
      return false;
    }
    
    public boolean remove(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Long, Double> local_e = (Map.Entry)local_o;
      long local_k = ((Long)local_e.getKey()).longValue();
      for (int pos = HashCommon.murmurHash3(Long2DoubleOpenCustomHashMap.this.strategy.hashCode(local_k)) & Long2DoubleOpenCustomHashMap.this.mask; Long2DoubleOpenCustomHashMap.this.used[pos] != 0; pos = pos + 1 & Long2DoubleOpenCustomHashMap.this.mask) {
        if (Long2DoubleOpenCustomHashMap.this.strategy.equals(Long2DoubleOpenCustomHashMap.this.key[pos], local_k))
        {
          Long2DoubleOpenCustomHashMap.this.remove(local_e.getKey());
          return true;
        }
      }
      return false;
    }
    
    public int size()
    {
      return Long2DoubleOpenCustomHashMap.this.size;
    }
    
    public void clear()
    {
      Long2DoubleOpenCustomHashMap.this.clear();
    }
  }
  
  private class FastEntryIterator
    extends Long2DoubleOpenCustomHashMap.MapIterator
    implements ObjectIterator<Long2DoubleMap.Entry>
  {
    final AbstractLong2DoubleMap.BasicEntry entry = new AbstractLong2DoubleMap.BasicEntry(0L, 0.0D);
    
    private FastEntryIterator()
    {
      super(null);
    }
    
    public AbstractLong2DoubleMap.BasicEntry next()
    {
      int local_e = nextEntry();
      this.entry.key = Long2DoubleOpenCustomHashMap.this.key[local_e];
      this.entry.value = Long2DoubleOpenCustomHashMap.this.value[local_e];
      return this.entry;
    }
  }
  
  private class EntryIterator
    extends Long2DoubleOpenCustomHashMap.MapIterator
    implements ObjectIterator<Long2DoubleMap.Entry>
  {
    private Long2DoubleOpenCustomHashMap.MapEntry entry;
    
    private EntryIterator()
    {
      super(null);
    }
    
    public Long2DoubleMap.Entry next()
    {
      return this.entry = new Long2DoubleOpenCustomHashMap.MapEntry(Long2DoubleOpenCustomHashMap.this, nextEntry());
    }
    
    public void remove()
    {
      super.remove();
      Long2DoubleOpenCustomHashMap.MapEntry.access$102(this.entry, -1);
    }
  }
  
  private class MapIterator
  {
    int pos = Long2DoubleOpenCustomHashMap.this.field_49;
    int last = -1;
    int field_1760 = Long2DoubleOpenCustomHashMap.this.size;
    LongArrayList wrapped;
    
    private MapIterator()
    {
      boolean[] used = Long2DoubleOpenCustomHashMap.this.used;
      while ((this.field_1760 != 0) && (used[(--this.pos)] == 0)) {}
    }
    
    public boolean hasNext()
    {
      return this.field_1760 != 0;
    }
    
    public int nextEntry()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      this.field_1760 -= 1;
      if (this.pos < 0)
      {
        long local_k = this.wrapped.getLong(-(this.last = --this.pos) - 2);
        for (int pos = HashCommon.murmurHash3(Long2DoubleOpenCustomHashMap.this.strategy.hashCode(local_k)) & Long2DoubleOpenCustomHashMap.this.mask; Long2DoubleOpenCustomHashMap.this.used[pos] != 0; pos = pos + 1 & Long2DoubleOpenCustomHashMap.this.mask) {
          if (Long2DoubleOpenCustomHashMap.this.strategy.equals(Long2DoubleOpenCustomHashMap.this.key[pos], local_k)) {
            return pos;
          }
        }
      }
      this.last = this.pos;
      if (this.field_1760 != 0)
      {
        boolean[] local_k = Long2DoubleOpenCustomHashMap.this.used;
        while ((this.pos-- != 0) && (local_k[this.pos] == 0)) {}
      }
      return this.last;
    }
    
    protected final int shiftKeys(int pos)
    {
      int last;
      for (;;)
      {
        for (pos = (last = pos) + 1 & Long2DoubleOpenCustomHashMap.this.mask; Long2DoubleOpenCustomHashMap.this.used[pos] != 0; pos = pos + 1 & Long2DoubleOpenCustomHashMap.this.mask)
        {
          int slot = HashCommon.murmurHash3(Long2DoubleOpenCustomHashMap.this.strategy.hashCode(Long2DoubleOpenCustomHashMap.this.key[pos])) & Long2DoubleOpenCustomHashMap.this.mask;
          if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) {
            break;
          }
        }
        if (Long2DoubleOpenCustomHashMap.this.used[pos] == 0) {
          break;
        }
        if (pos < last)
        {
          if (this.wrapped == null) {
            this.wrapped = new LongArrayList();
          }
          this.wrapped.add(Long2DoubleOpenCustomHashMap.this.key[pos]);
        }
        Long2DoubleOpenCustomHashMap.this.key[last] = Long2DoubleOpenCustomHashMap.this.key[pos];
        Long2DoubleOpenCustomHashMap.this.value[last] = Long2DoubleOpenCustomHashMap.this.value[pos];
      }
      Long2DoubleOpenCustomHashMap.this.used[last] = false;
      return last;
    }
    
    public void remove()
    {
      if (this.last == -1) {
        throw new IllegalStateException();
      }
      if (this.pos < -1)
      {
        Long2DoubleOpenCustomHashMap.this.remove(this.wrapped.getLong(-this.pos - 2));
        this.last = -1;
        return;
      }
      Long2DoubleOpenCustomHashMap.this.size -= 1;
      if ((shiftKeys(this.last) == this.pos) && (this.field_1760 > 0))
      {
        this.field_1760 += 1;
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
    implements Long2DoubleMap.Entry, Map.Entry<Long, Double>
  {
    private int index;
    
    MapEntry(int index)
    {
      this.index = index;
    }
    
    public Long getKey()
    {
      return Long.valueOf(Long2DoubleOpenCustomHashMap.this.key[this.index]);
    }
    
    public long getLongKey()
    {
      return Long2DoubleOpenCustomHashMap.this.key[this.index];
    }
    
    public Double getValue()
    {
      return Double.valueOf(Long2DoubleOpenCustomHashMap.this.value[this.index]);
    }
    
    public double getDoubleValue()
    {
      return Long2DoubleOpenCustomHashMap.this.value[this.index];
    }
    
    public double setValue(double local_v)
    {
      double oldValue = Long2DoubleOpenCustomHashMap.this.value[this.index];
      Long2DoubleOpenCustomHashMap.this.value[this.index] = local_v;
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
      Map.Entry<Long, Double> local_e = (Map.Entry)local_o;
      return (Long2DoubleOpenCustomHashMap.this.strategy.equals(Long2DoubleOpenCustomHashMap.this.key[this.index], ((Long)local_e.getKey()).longValue())) && (Long2DoubleOpenCustomHashMap.this.value[this.index] == ((Double)local_e.getValue()).doubleValue());
    }
    
    public int hashCode()
    {
      return Long2DoubleOpenCustomHashMap.this.strategy.hashCode(Long2DoubleOpenCustomHashMap.this.key[this.index]) ^ HashCommon.double2int(Long2DoubleOpenCustomHashMap.this.value[this.index]);
    }
    
    public String toString()
    {
      return Long2DoubleOpenCustomHashMap.this.key[this.index] + "=>" + Long2DoubleOpenCustomHashMap.this.value[this.index];
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.longs.Long2DoubleOpenCustomHashMap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */