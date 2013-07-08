package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

public class Reference2IntOpenHashMap<K>
  extends AbstractReference2IntMap<K>
  implements Serializable, Cloneable, Hash
{
  public static final long serialVersionUID = 0L;
  private static final boolean ASSERTS = false;
  protected transient K[] key;
  protected transient int[] value;
  protected transient boolean[] used;
  protected final float field_48;
  protected transient int field_49;
  protected transient int maxFill;
  protected transient int mask;
  protected int size;
  protected volatile transient Reference2IntMap.FastEntrySet<K> entries;
  protected volatile transient ReferenceSet<K> keys;
  protected volatile transient IntCollection values;
  
  public Reference2IntOpenHashMap(int expected, float local_f)
  {
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
    this.key = ((Object[])new Object[this.field_49]);
    this.value = new int[this.field_49];
    this.used = new boolean[this.field_49];
  }
  
  public Reference2IntOpenHashMap(int expected)
  {
    this(expected, 0.75F);
  }
  
  public Reference2IntOpenHashMap()
  {
    this(16, 0.75F);
  }
  
  public Reference2IntOpenHashMap(Map<? extends K, ? extends Integer> local_m, float local_f)
  {
    this(local_m.size(), local_f);
    putAll(local_m);
  }
  
  public Reference2IntOpenHashMap(Map<? extends K, ? extends Integer> local_m)
  {
    this(local_m, 0.75F);
  }
  
  public Reference2IntOpenHashMap(Reference2IntMap<K> local_m, float local_f)
  {
    this(local_m.size(), local_f);
    putAll(local_m);
  }
  
  public Reference2IntOpenHashMap(Reference2IntMap<K> local_m)
  {
    this(local_m, 0.75F);
  }
  
  public Reference2IntOpenHashMap(K[] local_k, int[] local_v, float local_f)
  {
    this(local_k.length, local_f);
    if (local_k.length != local_v.length) {
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + local_k.length + " and " + local_v.length + ")");
    }
    for (int local_i = 0; local_i < local_k.length; local_i++) {
      put(local_k[local_i], local_v[local_i]);
    }
  }
  
  public Reference2IntOpenHashMap(K[] local_k, int[] local_v)
  {
    this(local_k, local_v, 0.75F);
  }
  
  public int put(K local_k, int local_v)
  {
    for (int pos = (local_k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(local_k))) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        int oldValue = this.value[pos];
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
  
  public Integer put(K local_ok, Integer local_ov)
  {
    int local_v = local_ov.intValue();
    K local_k = local_ok;
    for (int pos = (local_k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(local_k))) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        Integer oldValue = Integer.valueOf(this.value[pos]);
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
  
  public int add(K local_k, int incr)
  {
    for (int pos = (local_k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(local_k))) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        int oldValue = this.value[pos];
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
        int slot = (this.key[pos] == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(this.key[pos]))) & this.mask;
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
    this.key[last] = null;
    return last;
  }
  
  public int removeInt(Object local_k)
  {
    for (int pos = (local_k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(local_k))) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        this.size -= 1;
        int local_v = this.value[pos];
        shiftKeys(pos);
        return local_v;
      }
    }
    return this.defRetValue;
  }
  
  public Integer remove(Object local_ok)
  {
    K local_k = local_ok;
    for (int pos = (local_k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(local_k))) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        this.size -= 1;
        int local_v = this.value[pos];
        shiftKeys(pos);
        return Integer.valueOf(local_v);
      }
    }
    return null;
  }
  
  public int getInt(Object local_k)
  {
    for (int pos = (local_k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(local_k))) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k) {
        return this.value[pos];
      }
    }
    return this.defRetValue;
  }
  
  public boolean containsKey(Object local_k)
  {
    for (int pos = (local_k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(local_k))) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsValue(int local_v)
  {
    int[] value = this.value;
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
    ObjectArrays.fill(this.key, null);
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
  
  public Reference2IntMap.FastEntrySet<K> reference2IntEntrySet()
  {
    if (this.entries == null) {
      this.entries = new MapEntrySet(null);
    }
    return this.entries;
  }
  
  public ReferenceSet<K> keySet()
  {
    if (this.keys == null) {
      this.keys = new KeySet(null);
    }
    return this.keys;
  }
  
  public IntCollection values()
  {
    if (this.values == null) {
      this.values = new AbstractIntCollection()
      {
        public IntIterator iterator()
        {
          return new Reference2IntOpenHashMap.ValueIterator(Reference2IntOpenHashMap.this);
        }
        
        public int size()
        {
          return Reference2IntOpenHashMap.this.size;
        }
        
        public boolean contains(int local_v)
        {
          return Reference2IntOpenHashMap.this.containsValue(local_v);
        }
        
        public void clear()
        {
          Reference2IntOpenHashMap.this.clear();
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
    K[] key = this.key;
    int[] value = this.value;
    int newMask = newN - 1;
    K[] newKey = (Object[])new Object[newN];
    int[] newValue = new int[newN];
    boolean[] newUsed = new boolean[newN];
    int local_j = this.size;
    while (local_j-- != 0)
    {
      while (used[local_i] == 0) {
        local_i++;
      }
      K local_k = key[local_i];
      for (int pos = (local_k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(local_k))) & newMask; newUsed[pos] != 0; pos = pos + 1 & newMask) {}
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
  
  public Reference2IntOpenHashMap<K> clone()
  {
    Reference2IntOpenHashMap<K> local_c;
    try
    {
      local_c = (Reference2IntOpenHashMap)super.clone();
    }
    catch (CloneNotSupportedException cantHappen)
    {
      throw new InternalError();
    }
    local_c.keys = null;
    local_c.values = null;
    local_c.entries = null;
    local_c.key = ((Object[])this.key.clone());
    local_c.value = ((int[])this.value.clone());
    local_c.used = ((boolean[])this.used.clone());
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
      if (this != this.key[local_i]) {
        local_t = this.key[local_i] == null ? 0 : System.identityHashCode(this.key[local_i]);
      }
      local_t ^= this.value[local_i];
      local_h += local_t;
      local_i++;
    }
    return local_h;
  }
  
  private void writeObject(ObjectOutputStream local_s)
    throws IOException
  {
    K[] key = this.key;
    int[] value = this.value;
    Reference2IntOpenHashMap<K>.MapIterator local_i = new MapIterator(null);
    local_s.defaultWriteObject();
    int local_j = this.size;
    while (local_j-- != 0)
    {
      int local_e = local_i.nextEntry();
      local_s.writeObject(key[local_e]);
      local_s.writeInt(value[local_e]);
    }
  }
  
  private void readObject(ObjectInputStream local_s)
    throws IOException, ClassNotFoundException
  {
    local_s.defaultReadObject();
    this.field_49 = HashCommon.arraySize(this.size, this.field_48);
    this.maxFill = HashCommon.maxFill(this.field_49, this.field_48);
    this.mask = (this.field_49 - 1);
    K[] key = this.key = (Object[])new Object[this.field_49];
    int[] value = this.value = new int[this.field_49];
    boolean[] used = this.used = new boolean[this.field_49];
    int local_i = this.size;
    int pos = 0;
    while (local_i-- != 0)
    {
      K local_k = local_s.readObject();
      int local_v = local_s.readInt();
      for (pos = (local_k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(local_k))) & this.mask; used[pos] != 0; pos = pos + 1 & this.mask) {}
      used[pos] = true;
      key[pos] = local_k;
      value[pos] = local_v;
    }
  }
  
  private void checkTable() {}
  
  private final class ValueIterator
    extends Reference2IntOpenHashMap.MapIterator
    implements IntIterator
  {
    public ValueIterator()
    {
      super(null);
    }
    
    public int nextInt()
    {
      return Reference2IntOpenHashMap.this.value[nextEntry()];
    }
    
    public Integer next()
    {
      return Integer.valueOf(Reference2IntOpenHashMap.this.value[nextEntry()]);
    }
  }
  
  private final class KeySet
    extends AbstractReferenceSet<K>
  {
    private KeySet() {}
    
    public ObjectIterator<K> iterator()
    {
      return new Reference2IntOpenHashMap.KeyIterator(Reference2IntOpenHashMap.this);
    }
    
    public int size()
    {
      return Reference2IntOpenHashMap.this.size;
    }
    
    public boolean contains(Object local_k)
    {
      return Reference2IntOpenHashMap.this.containsKey(local_k);
    }
    
    public boolean remove(Object local_k)
    {
      int oldSize = Reference2IntOpenHashMap.this.size;
      Reference2IntOpenHashMap.this.remove(local_k);
      return Reference2IntOpenHashMap.this.size != oldSize;
    }
    
    public void clear()
    {
      Reference2IntOpenHashMap.this.clear();
    }
  }
  
  private final class KeyIterator
    extends Reference2IntOpenHashMap<K>.MapIterator
    implements ObjectIterator<K>
  {
    public KeyIterator()
    {
      super(null);
    }
    
    public K next()
    {
      return Reference2IntOpenHashMap.this.key[nextEntry()];
    }
  }
  
  private final class MapEntrySet
    extends AbstractObjectSet<Reference2IntMap.Entry<K>>
    implements Reference2IntMap.FastEntrySet<K>
  {
    private MapEntrySet() {}
    
    public ObjectIterator<Reference2IntMap.Entry<K>> iterator()
    {
      return new Reference2IntOpenHashMap.EntryIterator(Reference2IntOpenHashMap.this, null);
    }
    
    public ObjectIterator<Reference2IntMap.Entry<K>> fastIterator()
    {
      return new Reference2IntOpenHashMap.FastEntryIterator(Reference2IntOpenHashMap.this, null);
    }
    
    public boolean contains(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<K, Integer> local_e = (Map.Entry)local_o;
      K local_k = local_e.getKey();
      for (int pos = (local_k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(local_k))) & Reference2IntOpenHashMap.this.mask; Reference2IntOpenHashMap.this.used[pos] != 0; pos = pos + 1 & Reference2IntOpenHashMap.this.mask) {
        if (Reference2IntOpenHashMap.this.key[pos] == local_k) {
          return Reference2IntOpenHashMap.this.value[pos] == ((Integer)local_e.getValue()).intValue();
        }
      }
      return false;
    }
    
    public boolean remove(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<K, Integer> local_e = (Map.Entry)local_o;
      K local_k = local_e.getKey();
      for (int pos = (local_k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(local_k))) & Reference2IntOpenHashMap.this.mask; Reference2IntOpenHashMap.this.used[pos] != 0; pos = pos + 1 & Reference2IntOpenHashMap.this.mask) {
        if (Reference2IntOpenHashMap.this.key[pos] == local_k)
        {
          Reference2IntOpenHashMap.this.remove(local_e.getKey());
          return true;
        }
      }
      return false;
    }
    
    public int size()
    {
      return Reference2IntOpenHashMap.this.size;
    }
    
    public void clear()
    {
      Reference2IntOpenHashMap.this.clear();
    }
  }
  
  private class FastEntryIterator
    extends Reference2IntOpenHashMap<K>.MapIterator
    implements ObjectIterator<Reference2IntMap.Entry<K>>
  {
    final AbstractReference2IntMap.BasicEntry<K> entry = new AbstractReference2IntMap.BasicEntry(null, 0);
    
    private FastEntryIterator()
    {
      super(null);
    }
    
    public AbstractReference2IntMap.BasicEntry<K> next()
    {
      int local_e = nextEntry();
      this.entry.key = Reference2IntOpenHashMap.this.key[local_e];
      this.entry.value = Reference2IntOpenHashMap.this.value[local_e];
      return this.entry;
    }
  }
  
  private class EntryIterator
    extends Reference2IntOpenHashMap<K>.MapIterator
    implements ObjectIterator<Reference2IntMap.Entry<K>>
  {
    private Reference2IntOpenHashMap<K>.MapEntry entry;
    
    private EntryIterator()
    {
      super(null);
    }
    
    public Reference2IntMap.Entry<K> next()
    {
      return this.entry = new Reference2IntOpenHashMap.MapEntry(Reference2IntOpenHashMap.this, nextEntry());
    }
    
    public void remove()
    {
      super.remove();
      Reference2IntOpenHashMap.MapEntry.access$102(this.entry, -1);
    }
  }
  
  private class MapIterator
  {
    int pos = Reference2IntOpenHashMap.this.field_49;
    int last = -1;
    int field_1537 = Reference2IntOpenHashMap.this.size;
    ReferenceArrayList<K> wrapped;
    
    private MapIterator()
    {
      boolean[] used = Reference2IntOpenHashMap.this.used;
      while ((this.field_1537 != 0) && (used[(--this.pos)] == 0)) {}
    }
    
    public boolean hasNext()
    {
      return this.field_1537 != 0;
    }
    
    public int nextEntry()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      this.field_1537 -= 1;
      if (this.pos < 0)
      {
        Object local_k = this.wrapped.get(-(this.last = --this.pos) - 2);
        for (int pos = (local_k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(local_k))) & Reference2IntOpenHashMap.this.mask; Reference2IntOpenHashMap.this.used[pos] != 0; pos = pos + 1 & Reference2IntOpenHashMap.this.mask) {
          if (Reference2IntOpenHashMap.this.key[pos] == local_k) {
            return pos;
          }
        }
      }
      this.last = this.pos;
      if (this.field_1537 != 0)
      {
        boolean[] local_k = Reference2IntOpenHashMap.this.used;
        while ((this.pos-- != 0) && (local_k[this.pos] == 0)) {}
      }
      return this.last;
    }
    
    protected final int shiftKeys(int pos)
    {
      int last;
      for (;;)
      {
        for (pos = (last = pos) + 1 & Reference2IntOpenHashMap.this.mask; Reference2IntOpenHashMap.this.used[pos] != 0; pos = pos + 1 & Reference2IntOpenHashMap.this.mask)
        {
          int slot = (Reference2IntOpenHashMap.this.key[pos] == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(Reference2IntOpenHashMap.this.key[pos]))) & Reference2IntOpenHashMap.this.mask;
          if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) {
            break;
          }
        }
        if (Reference2IntOpenHashMap.this.used[pos] == 0) {
          break;
        }
        if (pos < last)
        {
          if (this.wrapped == null) {
            this.wrapped = new ReferenceArrayList();
          }
          this.wrapped.add(Reference2IntOpenHashMap.this.key[pos]);
        }
        Reference2IntOpenHashMap.this.key[last] = Reference2IntOpenHashMap.this.key[pos];
        Reference2IntOpenHashMap.this.value[last] = Reference2IntOpenHashMap.this.value[pos];
      }
      Reference2IntOpenHashMap.this.used[last] = false;
      Reference2IntOpenHashMap.this.key[last] = null;
      return last;
    }
    
    public void remove()
    {
      if (this.last == -1) {
        throw new IllegalStateException();
      }
      if (this.pos < -1)
      {
        Reference2IntOpenHashMap.this.remove(this.wrapped.set(-this.pos - 2, null));
        this.last = -1;
        return;
      }
      Reference2IntOpenHashMap.this.size -= 1;
      if ((shiftKeys(this.last) == this.pos) && (this.field_1537 > 0))
      {
        this.field_1537 += 1;
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
    implements Reference2IntMap.Entry<K>, Map.Entry<K, Integer>
  {
    private int index;
    
    MapEntry(int index)
    {
      this.index = index;
    }
    
    public K getKey()
    {
      return Reference2IntOpenHashMap.this.key[this.index];
    }
    
    public Integer getValue()
    {
      return Integer.valueOf(Reference2IntOpenHashMap.this.value[this.index]);
    }
    
    public int getIntValue()
    {
      return Reference2IntOpenHashMap.this.value[this.index];
    }
    
    public int setValue(int local_v)
    {
      int oldValue = Reference2IntOpenHashMap.this.value[this.index];
      Reference2IntOpenHashMap.this.value[this.index] = local_v;
      return oldValue;
    }
    
    public Integer setValue(Integer local_v)
    {
      return Integer.valueOf(setValue(local_v.intValue()));
    }
    
    public boolean equals(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<K, Integer> local_e = (Map.Entry)local_o;
      return (Reference2IntOpenHashMap.this.key[this.index] == local_e.getKey()) && (Reference2IntOpenHashMap.this.value[this.index] == ((Integer)local_e.getValue()).intValue());
    }
    
    public int hashCode()
    {
      return (Reference2IntOpenHashMap.this.key[this.index] == null ? 0 : System.identityHashCode(Reference2IntOpenHashMap.this.key[this.index])) ^ Reference2IntOpenHashMap.this.value[this.index];
    }
    
    public String toString()
    {
      return Reference2IntOpenHashMap.this.key[this.index] + "=>" + Reference2IntOpenHashMap.this.value[this.index];
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */