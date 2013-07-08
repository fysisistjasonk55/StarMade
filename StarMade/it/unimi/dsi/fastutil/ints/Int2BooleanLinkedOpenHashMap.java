package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

public class Int2BooleanLinkedOpenHashMap
  extends AbstractInt2BooleanSortedMap
  implements Serializable, Cloneable, Hash
{
  public static final long serialVersionUID = 0L;
  private static final boolean ASSERTS = false;
  protected transient int[] key;
  protected transient boolean[] value;
  protected transient boolean[] used;
  protected final float field_48;
  protected transient int field_49;
  protected transient int maxFill;
  protected transient int mask;
  protected int size;
  protected volatile transient Int2BooleanSortedMap.FastSortedEntrySet entries;
  protected volatile transient IntSortedSet keys;
  protected volatile transient BooleanCollection values;
  protected transient int first = -1;
  protected transient int last = -1;
  protected transient long[] link;
  
  public Int2BooleanLinkedOpenHashMap(int expected, float local_f)
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
    this.key = new int[this.field_49];
    this.value = new boolean[this.field_49];
    this.used = new boolean[this.field_49];
    this.link = new long[this.field_49];
  }
  
  public Int2BooleanLinkedOpenHashMap(int expected)
  {
    this(expected, 0.75F);
  }
  
  public Int2BooleanLinkedOpenHashMap()
  {
    this(16, 0.75F);
  }
  
  public Int2BooleanLinkedOpenHashMap(Map<? extends Integer, ? extends Boolean> local_m, float local_f)
  {
    this(local_m.size(), local_f);
    putAll(local_m);
  }
  
  public Int2BooleanLinkedOpenHashMap(Map<? extends Integer, ? extends Boolean> local_m)
  {
    this(local_m, 0.75F);
  }
  
  public Int2BooleanLinkedOpenHashMap(Int2BooleanMap local_m, float local_f)
  {
    this(local_m.size(), local_f);
    putAll(local_m);
  }
  
  public Int2BooleanLinkedOpenHashMap(Int2BooleanMap local_m)
  {
    this(local_m, 0.75F);
  }
  
  public Int2BooleanLinkedOpenHashMap(int[] local_k, boolean[] local_v, float local_f)
  {
    this(local_k.length, local_f);
    if (local_k.length != local_v.length) {
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + local_k.length + " and " + local_v.length + ")");
    }
    for (int local_i = 0; local_i < local_k.length; local_i++) {
      put(local_k[local_i], local_v[local_i]);
    }
  }
  
  public Int2BooleanLinkedOpenHashMap(int[] local_k, boolean[] local_v)
  {
    this(local_k, local_v, 0.75F);
  }
  
  public boolean put(int local_k, boolean local_v)
  {
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        boolean oldValue = this.value[pos];
        this.value[pos] = local_v;
        return oldValue;
      }
    }
    this.used[pos] = true;
    this.key[pos] = local_k;
    this.value[pos] = local_v;
    if (this.size == 0)
    {
      this.first = (this.last = pos);
      this.link[pos] = -1L;
    }
    else
    {
      this.link[this.last] ^= (this.link[this.last] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
      this.link[pos] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
      this.last = pos;
    }
    if (++this.size >= this.maxFill) {
      rehash(HashCommon.arraySize(this.size + 1, this.field_48));
    }
    return this.defRetValue;
  }
  
  public Boolean put(Integer local_ok, Boolean local_ov)
  {
    boolean local_v = local_ov.booleanValue();
    int local_k = local_ok.intValue();
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        Boolean oldValue = Boolean.valueOf(this.value[pos]);
        this.value[pos] = local_v;
        return oldValue;
      }
    }
    this.used[pos] = true;
    this.key[pos] = local_k;
    this.value[pos] = local_v;
    if (this.size == 0)
    {
      this.first = (this.last = pos);
      this.link[pos] = -1L;
    }
    else
    {
      this.link[this.last] ^= (this.link[this.last] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
      this.link[pos] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
      this.last = pos;
    }
    if (++this.size >= this.maxFill) {
      rehash(HashCommon.arraySize(this.size + 1, this.field_48));
    }
    return null;
  }
  
  protected final int shiftKeys(int pos)
  {
    int last;
    for (;;)
    {
      for (pos = (last = pos) + 1 & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask)
      {
        int slot = HashCommon.murmurHash3(this.key[pos]) & this.mask;
        if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) {
          break;
        }
      }
      if (this.used[pos] == 0) {
        break;
      }
      this.key[last] = this.key[pos];
      this.value[last] = this.value[pos];
      fixPointers(pos, last);
    }
    this.used[last] = false;
    return last;
  }
  
  public boolean remove(int local_k)
  {
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        this.size -= 1;
        fixPointers(pos);
        boolean local_v = this.value[pos];
        shiftKeys(pos);
        return local_v;
      }
    }
    return this.defRetValue;
  }
  
  public Boolean remove(Object local_ok)
  {
    int local_k = ((Integer)local_ok).intValue();
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        this.size -= 1;
        fixPointers(pos);
        boolean local_v = this.value[pos];
        shiftKeys(pos);
        return Boolean.valueOf(local_v);
      }
    }
    return null;
  }
  
  public boolean removeFirstBoolean()
  {
    if (this.size == 0) {
      throw new NoSuchElementException();
    }
    this.size -= 1;
    int pos = this.first;
    this.first = ((int)this.link[pos]);
    if (0 <= this.first) {
      this.link[this.first] |= -4294967296L;
    }
    boolean local_v = this.value[pos];
    shiftKeys(pos);
    return local_v;
  }
  
  public boolean removeLastBoolean()
  {
    if (this.size == 0) {
      throw new NoSuchElementException();
    }
    this.size -= 1;
    int pos = this.last;
    this.last = ((int)(this.link[pos] >>> 32));
    if (0 <= this.last) {
      this.link[this.last] |= 4294967295L;
    }
    boolean local_v = this.value[pos];
    shiftKeys(pos);
    return local_v;
  }
  
  private void moveIndexToFirst(int local_i)
  {
    if ((this.size == 1) || (this.first == local_i)) {
      return;
    }
    if (this.last == local_i)
    {
      this.last = ((int)(this.link[local_i] >>> 32));
      this.link[this.last] |= 4294967295L;
    }
    else
    {
      long linki = this.link[local_i];
      int prev = (int)(linki >>> 32);
      int next = (int)linki;
      this.link[prev] ^= (this.link[prev] ^ linki & 0xFFFFFFFF) & 0xFFFFFFFF;
      this.link[next] ^= (this.link[next] ^ linki & 0x0) & 0x0;
    }
    this.link[this.first] ^= (this.link[this.first] ^ (local_i & 0xFFFFFFFF) << 32) & 0x0;
    this.link[local_i] = (0x0 | this.first & 0xFFFFFFFF);
    this.first = local_i;
  }
  
  private void moveIndexToLast(int local_i)
  {
    if ((this.size == 1) || (this.last == local_i)) {
      return;
    }
    if (this.first == local_i)
    {
      this.first = ((int)this.link[local_i]);
      this.link[this.first] |= -4294967296L;
    }
    else
    {
      long linki = this.link[local_i];
      int prev = (int)(linki >>> 32);
      int next = (int)linki;
      this.link[prev] ^= (this.link[prev] ^ linki & 0xFFFFFFFF) & 0xFFFFFFFF;
      this.link[next] ^= (this.link[next] ^ linki & 0x0) & 0x0;
    }
    this.link[this.last] ^= (this.link[this.last] ^ local_i & 0xFFFFFFFF) & 0xFFFFFFFF;
    this.link[local_i] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
    this.last = local_i;
  }
  
  public boolean getAndMoveToFirst(int local_k)
  {
    int[] key = this.key;
    boolean[] used = this.used;
    int mask = this.mask;
    for (int pos = HashCommon.murmurHash3(local_k) & mask; used[pos] != 0; pos = pos + 1 & mask) {
      if (local_k == key[pos])
      {
        moveIndexToFirst(pos);
        return this.value[pos];
      }
    }
    return this.defRetValue;
  }
  
  public boolean getAndMoveToLast(int local_k)
  {
    int[] key = this.key;
    boolean[] used = this.used;
    int mask = this.mask;
    for (int pos = HashCommon.murmurHash3(local_k) & mask; used[pos] != 0; pos = pos + 1 & mask) {
      if (local_k == key[pos])
      {
        moveIndexToLast(pos);
        return this.value[pos];
      }
    }
    return this.defRetValue;
  }
  
  public boolean putAndMoveToFirst(int local_k, boolean local_v)
  {
    int[] key = this.key;
    boolean[] used = this.used;
    int mask = this.mask;
    for (int pos = HashCommon.murmurHash3(local_k) & mask; used[pos] != 0; pos = pos + 1 & mask) {
      if (local_k == key[pos])
      {
        boolean oldValue = this.value[pos];
        this.value[pos] = local_v;
        moveIndexToFirst(pos);
        return oldValue;
      }
    }
    used[pos] = true;
    key[pos] = local_k;
    this.value[pos] = local_v;
    if (this.size == 0)
    {
      this.first = (this.last = pos);
      this.link[pos] = -1L;
    }
    else
    {
      this.link[this.first] ^= (this.link[this.first] ^ (pos & 0xFFFFFFFF) << 32) & 0x0;
      this.link[pos] = (0x0 | this.first & 0xFFFFFFFF);
      this.first = pos;
    }
    if (++this.size >= this.maxFill) {
      rehash(HashCommon.arraySize(this.size, this.field_48));
    }
    return this.defRetValue;
  }
  
  public boolean putAndMoveToLast(int local_k, boolean local_v)
  {
    int[] key = this.key;
    boolean[] used = this.used;
    int mask = this.mask;
    for (int pos = HashCommon.murmurHash3(local_k) & mask; used[pos] != 0; pos = pos + 1 & mask) {
      if (local_k == key[pos])
      {
        boolean oldValue = this.value[pos];
        this.value[pos] = local_v;
        moveIndexToLast(pos);
        return oldValue;
      }
    }
    used[pos] = true;
    key[pos] = local_k;
    this.value[pos] = local_v;
    if (this.size == 0)
    {
      this.first = (this.last = pos);
      this.link[pos] = -1L;
    }
    else
    {
      this.link[this.last] ^= (this.link[this.last] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
      this.link[pos] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
      this.last = pos;
    }
    if (++this.size >= this.maxFill) {
      rehash(HashCommon.arraySize(this.size, this.field_48));
    }
    return this.defRetValue;
  }
  
  public Boolean get(Integer local_ok)
  {
    int local_k = local_ok.intValue();
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k) {
        return Boolean.valueOf(this.value[pos]);
      }
    }
    return null;
  }
  
  public boolean get(int local_k)
  {
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k) {
        return this.value[pos];
      }
    }
    return this.defRetValue;
  }
  
  public boolean containsKey(int local_k)
  {
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsValue(boolean local_v)
  {
    boolean[] value = this.value;
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
    this.first = (this.last = -1);
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
  
  protected void fixPointers(int local_i)
  {
    if (this.size == 0)
    {
      this.first = (this.last = -1);
      return;
    }
    if (this.first == local_i)
    {
      this.first = ((int)this.link[local_i]);
      if (0 <= this.first) {
        this.link[this.first] |= -4294967296L;
      }
      return;
    }
    if (this.last == local_i)
    {
      this.last = ((int)(this.link[local_i] >>> 32));
      if (0 <= this.last) {
        this.link[this.last] |= 4294967295L;
      }
      return;
    }
    long linki = this.link[local_i];
    int prev = (int)(linki >>> 32);
    int next = (int)linki;
    this.link[prev] ^= (this.link[prev] ^ linki & 0xFFFFFFFF) & 0xFFFFFFFF;
    this.link[next] ^= (this.link[next] ^ linki & 0x0) & 0x0;
  }
  
  protected void fixPointers(int local_s, int local_d)
  {
    if (this.size == 1)
    {
      this.first = (this.last = local_d);
      this.link[local_d] = -1L;
      return;
    }
    if (this.first == local_s)
    {
      this.first = local_d;
      this.link[((int)this.link[local_s])] ^= (this.link[((int)this.link[local_s])] ^ (local_d & 0xFFFFFFFF) << 32) & 0x0;
      this.link[local_d] = this.link[local_s];
      return;
    }
    if (this.last == local_s)
    {
      this.last = local_d;
      this.link[((int)(this.link[local_s] >>> 32))] ^= (this.link[((int)(this.link[local_s] >>> 32))] ^ local_d & 0xFFFFFFFF) & 0xFFFFFFFF;
      this.link[local_d] = this.link[local_s];
      return;
    }
    long links = this.link[local_s];
    int prev = (int)(links >>> 32);
    int next = (int)links;
    this.link[prev] ^= (this.link[prev] ^ local_d & 0xFFFFFFFF) & 0xFFFFFFFF;
    this.link[next] ^= (this.link[next] ^ (local_d & 0xFFFFFFFF) << 32) & 0x0;
    this.link[local_d] = links;
  }
  
  public int firstIntKey()
  {
    if (this.size == 0) {
      throw new NoSuchElementException();
    }
    return this.key[this.first];
  }
  
  public int lastIntKey()
  {
    if (this.size == 0) {
      throw new NoSuchElementException();
    }
    return this.key[this.last];
  }
  
  public IntComparator comparator()
  {
    return null;
  }
  
  public Int2BooleanSortedMap tailMap(int from)
  {
    throw new UnsupportedOperationException();
  }
  
  public Int2BooleanSortedMap headMap(int local_to)
  {
    throw new UnsupportedOperationException();
  }
  
  public Int2BooleanSortedMap subMap(int from, int local_to)
  {
    throw new UnsupportedOperationException();
  }
  
  public Int2BooleanSortedMap.FastSortedEntrySet int2BooleanEntrySet()
  {
    if (this.entries == null) {
      this.entries = new MapEntrySet(null);
    }
    return this.entries;
  }
  
  public IntSortedSet keySet()
  {
    if (this.keys == null) {
      this.keys = new KeySet(null);
    }
    return this.keys;
  }
  
  public BooleanCollection values()
  {
    if (this.values == null) {
      this.values = new AbstractBooleanCollection()
      {
        public BooleanIterator iterator()
        {
          return new Int2BooleanLinkedOpenHashMap.ValueIterator(Int2BooleanLinkedOpenHashMap.this);
        }
        
        public int size()
        {
          return Int2BooleanLinkedOpenHashMap.this.size;
        }
        
        public boolean contains(boolean local_v)
        {
          return Int2BooleanLinkedOpenHashMap.this.containsValue(local_v);
        }
        
        public void clear()
        {
          Int2BooleanLinkedOpenHashMap.this.clear();
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
    int local_i = this.first;
    int prev = -1;
    int newPrev = -1;
    int[] key = this.key;
    boolean[] value = this.value;
    int newMask = newN - 1;
    int[] newKey = new int[newN];
    boolean[] newValue = new boolean[newN];
    boolean[] newUsed = new boolean[newN];
    long[] link = this.link;
    long[] newLink = new long[newN];
    this.first = -1;
    int local_j = this.size;
    while (local_j-- != 0)
    {
      int local_k = key[local_i];
      for (int pos = HashCommon.murmurHash3(local_k) & newMask; newUsed[pos] != 0; pos = pos + 1 & newMask) {}
      newUsed[pos] = true;
      newKey[pos] = local_k;
      newValue[pos] = value[local_i];
      if (prev != -1)
      {
        newLink[newPrev] ^= (newLink[newPrev] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
        newLink[pos] ^= (newLink[pos] ^ (newPrev & 0xFFFFFFFF) << 32) & 0x0;
        newPrev = pos;
      }
      else
      {
        newPrev = this.first = pos;
        newLink[pos] = -1L;
      }
      int local_t = local_i;
      local_i = (int)link[local_i];
      prev = local_t;
    }
    this.field_49 = newN;
    this.mask = newMask;
    this.maxFill = HashCommon.maxFill(this.field_49, this.field_48);
    this.key = newKey;
    this.value = newValue;
    this.used = newUsed;
    this.link = newLink;
    this.last = newPrev;
    if (newPrev != -1) {
      newLink[newPrev] |= 4294967295L;
    }
  }
  
  public Int2BooleanLinkedOpenHashMap clone()
  {
    Int2BooleanLinkedOpenHashMap local_c;
    try
    {
      local_c = (Int2BooleanLinkedOpenHashMap)super.clone();
    }
    catch (CloneNotSupportedException cantHappen)
    {
      throw new InternalError();
    }
    local_c.keys = null;
    local_c.values = null;
    local_c.entries = null;
    local_c.key = ((int[])this.key.clone());
    local_c.value = ((boolean[])this.value.clone());
    local_c.used = ((boolean[])this.used.clone());
    local_c.link = ((long[])this.link.clone());
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
      local_t = this.key[local_i];
      local_t ^= (this.value[local_i] != 0 ? 1231 : 1237);
      local_h += local_t;
      local_i++;
    }
    return local_h;
  }
  
  private void writeObject(ObjectOutputStream local_s)
    throws IOException
  {
    int[] key = this.key;
    boolean[] value = this.value;
    MapIterator local_i = new MapIterator(null);
    local_s.defaultWriteObject();
    int local_j = this.size;
    while (local_j-- != 0)
    {
      int local_e = local_i.nextEntry();
      local_s.writeInt(key[local_e]);
      local_s.writeBoolean(value[local_e]);
    }
  }
  
  private void readObject(ObjectInputStream local_s)
    throws IOException, ClassNotFoundException
  {
    local_s.defaultReadObject();
    this.field_49 = HashCommon.arraySize(this.size, this.field_48);
    this.maxFill = HashCommon.maxFill(this.field_49, this.field_48);
    this.mask = (this.field_49 - 1);
    int[] key = this.key = new int[this.field_49];
    boolean[] value = this.value = new boolean[this.field_49];
    boolean[] used = this.used = new boolean[this.field_49];
    long[] link = this.link = new long[this.field_49];
    int prev = -1;
    this.first = (this.last = -1);
    int local_i = this.size;
    int pos = 0;
    while (local_i-- != 0)
    {
      int local_k = local_s.readInt();
      boolean local_v = local_s.readBoolean();
      for (pos = HashCommon.murmurHash3(local_k) & this.mask; used[pos] != 0; pos = pos + 1 & this.mask) {}
      used[pos] = true;
      key[pos] = local_k;
      value[pos] = local_v;
      if (this.first != -1)
      {
        link[prev] ^= (link[prev] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
        link[pos] ^= (link[pos] ^ (prev & 0xFFFFFFFF) << 32) & 0x0;
        prev = pos;
      }
      else
      {
        prev = this.first = pos;
        link[pos] |= -4294967296L;
      }
    }
    this.last = prev;
    if (prev != -1) {
      link[prev] |= 4294967295L;
    }
  }
  
  private void checkTable() {}
  
  private final class ValueIterator
    extends Int2BooleanLinkedOpenHashMap.MapIterator
    implements BooleanListIterator
  {
    public boolean previousBoolean()
    {
      return Int2BooleanLinkedOpenHashMap.this.value[previousEntry()];
    }
    
    public Boolean previous()
    {
      return Boolean.valueOf(Int2BooleanLinkedOpenHashMap.this.value[previousEntry()]);
    }
    
    public void set(Boolean local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Boolean local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void set(boolean local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(boolean local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public ValueIterator()
    {
      super(null);
    }
    
    public boolean nextBoolean()
    {
      return Int2BooleanLinkedOpenHashMap.this.value[nextEntry()];
    }
    
    public Boolean next()
    {
      return Boolean.valueOf(Int2BooleanLinkedOpenHashMap.this.value[nextEntry()]);
    }
  }
  
  private final class KeySet
    extends AbstractIntSortedSet
  {
    private KeySet() {}
    
    public IntListIterator iterator(int from)
    {
      return new Int2BooleanLinkedOpenHashMap.KeyIterator(Int2BooleanLinkedOpenHashMap.this, from);
    }
    
    public IntListIterator iterator()
    {
      return new Int2BooleanLinkedOpenHashMap.KeyIterator(Int2BooleanLinkedOpenHashMap.this);
    }
    
    public int size()
    {
      return Int2BooleanLinkedOpenHashMap.this.size;
    }
    
    public boolean contains(int local_k)
    {
      return Int2BooleanLinkedOpenHashMap.this.containsKey(local_k);
    }
    
    public boolean remove(int local_k)
    {
      int oldSize = Int2BooleanLinkedOpenHashMap.this.size;
      Int2BooleanLinkedOpenHashMap.this.remove(local_k);
      return Int2BooleanLinkedOpenHashMap.this.size != oldSize;
    }
    
    public void clear()
    {
      Int2BooleanLinkedOpenHashMap.this.clear();
    }
    
    public int firstInt()
    {
      if (Int2BooleanLinkedOpenHashMap.this.size == 0) {
        throw new NoSuchElementException();
      }
      return Int2BooleanLinkedOpenHashMap.this.key[Int2BooleanLinkedOpenHashMap.this.first];
    }
    
    public int lastInt()
    {
      if (Int2BooleanLinkedOpenHashMap.this.size == 0) {
        throw new NoSuchElementException();
      }
      return Int2BooleanLinkedOpenHashMap.this.key[Int2BooleanLinkedOpenHashMap.this.last];
    }
    
    public IntComparator comparator()
    {
      return null;
    }
    
    public final IntSortedSet tailSet(int from)
    {
      throw new UnsupportedOperationException();
    }
    
    public final IntSortedSet headSet(int local_to)
    {
      throw new UnsupportedOperationException();
    }
    
    public final IntSortedSet subSet(int from, int local_to)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private final class KeyIterator
    extends Int2BooleanLinkedOpenHashMap.MapIterator
    implements IntListIterator
  {
    public KeyIterator(int local_k)
    {
      super(local_k, null);
    }
    
    public int previousInt()
    {
      return Int2BooleanLinkedOpenHashMap.this.key[previousEntry()];
    }
    
    public void set(int local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(int local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public Integer previous()
    {
      return Integer.valueOf(Int2BooleanLinkedOpenHashMap.this.key[previousEntry()]);
    }
    
    public void set(Integer local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Integer local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public KeyIterator()
    {
      super(null);
    }
    
    public int nextInt()
    {
      return Int2BooleanLinkedOpenHashMap.this.key[nextEntry()];
    }
    
    public Integer next()
    {
      return Integer.valueOf(Int2BooleanLinkedOpenHashMap.this.key[nextEntry()]);
    }
  }
  
  private final class MapEntrySet
    extends AbstractObjectSortedSet<Int2BooleanMap.Entry>
    implements Int2BooleanSortedMap.FastSortedEntrySet
  {
    private MapEntrySet() {}
    
    public ObjectBidirectionalIterator<Int2BooleanMap.Entry> iterator()
    {
      return new Int2BooleanLinkedOpenHashMap.EntryIterator(Int2BooleanLinkedOpenHashMap.this);
    }
    
    public Comparator<? super Int2BooleanMap.Entry> comparator()
    {
      return null;
    }
    
    public ObjectSortedSet<Int2BooleanMap.Entry> subSet(Int2BooleanMap.Entry fromElement, Int2BooleanMap.Entry toElement)
    {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSortedSet<Int2BooleanMap.Entry> headSet(Int2BooleanMap.Entry toElement)
    {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSortedSet<Int2BooleanMap.Entry> tailSet(Int2BooleanMap.Entry fromElement)
    {
      throw new UnsupportedOperationException();
    }
    
    public Int2BooleanMap.Entry first()
    {
      if (Int2BooleanLinkedOpenHashMap.this.size == 0) {
        throw new NoSuchElementException();
      }
      return new Int2BooleanLinkedOpenHashMap.MapEntry(Int2BooleanLinkedOpenHashMap.this, Int2BooleanLinkedOpenHashMap.this.first);
    }
    
    public Int2BooleanMap.Entry last()
    {
      if (Int2BooleanLinkedOpenHashMap.this.size == 0) {
        throw new NoSuchElementException();
      }
      return new Int2BooleanLinkedOpenHashMap.MapEntry(Int2BooleanLinkedOpenHashMap.this, Int2BooleanLinkedOpenHashMap.this.last);
    }
    
    public boolean contains(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Integer, Boolean> local_e = (Map.Entry)local_o;
      int local_k = ((Integer)local_e.getKey()).intValue();
      for (int pos = HashCommon.murmurHash3(local_k) & Int2BooleanLinkedOpenHashMap.this.mask; Int2BooleanLinkedOpenHashMap.this.used[pos] != 0; pos = pos + 1 & Int2BooleanLinkedOpenHashMap.this.mask) {
        if (Int2BooleanLinkedOpenHashMap.this.key[pos] == local_k) {
          return Int2BooleanLinkedOpenHashMap.this.value[pos] == ((Boolean)local_e.getValue()).booleanValue();
        }
      }
      return false;
    }
    
    public boolean remove(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Integer, Boolean> local_e = (Map.Entry)local_o;
      int local_k = ((Integer)local_e.getKey()).intValue();
      for (int pos = HashCommon.murmurHash3(local_k) & Int2BooleanLinkedOpenHashMap.this.mask; Int2BooleanLinkedOpenHashMap.this.used[pos] != 0; pos = pos + 1 & Int2BooleanLinkedOpenHashMap.this.mask) {
        if (Int2BooleanLinkedOpenHashMap.this.key[pos] == local_k)
        {
          Int2BooleanLinkedOpenHashMap.this.remove(local_e.getKey());
          return true;
        }
      }
      return false;
    }
    
    public int size()
    {
      return Int2BooleanLinkedOpenHashMap.this.size;
    }
    
    public void clear()
    {
      Int2BooleanLinkedOpenHashMap.this.clear();
    }
    
    public ObjectBidirectionalIterator<Int2BooleanMap.Entry> iterator(Int2BooleanMap.Entry from)
    {
      return new Int2BooleanLinkedOpenHashMap.EntryIterator(Int2BooleanLinkedOpenHashMap.this, ((Integer)from.getKey()).intValue());
    }
    
    public ObjectBidirectionalIterator<Int2BooleanMap.Entry> fastIterator()
    {
      return new Int2BooleanLinkedOpenHashMap.FastEntryIterator(Int2BooleanLinkedOpenHashMap.this);
    }
    
    public ObjectBidirectionalIterator<Int2BooleanMap.Entry> fastIterator(Int2BooleanMap.Entry from)
    {
      return new Int2BooleanLinkedOpenHashMap.FastEntryIterator(Int2BooleanLinkedOpenHashMap.this, ((Integer)from.getKey()).intValue());
    }
  }
  
  private class FastEntryIterator
    extends Int2BooleanLinkedOpenHashMap.MapIterator
    implements ObjectListIterator<Int2BooleanMap.Entry>
  {
    final AbstractInt2BooleanMap.BasicEntry entry = new AbstractInt2BooleanMap.BasicEntry(0, false);
    
    public FastEntryIterator()
    {
      super(null);
    }
    
    public FastEntryIterator(int from)
    {
      super(from, null);
    }
    
    public AbstractInt2BooleanMap.BasicEntry next()
    {
      int local_e = nextEntry();
      this.entry.key = Int2BooleanLinkedOpenHashMap.this.key[local_e];
      this.entry.value = Int2BooleanLinkedOpenHashMap.this.value[local_e];
      return this.entry;
    }
    
    public AbstractInt2BooleanMap.BasicEntry previous()
    {
      int local_e = previousEntry();
      this.entry.key = Int2BooleanLinkedOpenHashMap.this.key[local_e];
      this.entry.value = Int2BooleanLinkedOpenHashMap.this.value[local_e];
      return this.entry;
    }
    
    public void set(Int2BooleanMap.Entry local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Int2BooleanMap.Entry local_ok)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class EntryIterator
    extends Int2BooleanLinkedOpenHashMap.MapIterator
    implements ObjectListIterator<Int2BooleanMap.Entry>
  {
    private Int2BooleanLinkedOpenHashMap.MapEntry entry;
    
    public EntryIterator()
    {
      super(null);
    }
    
    public EntryIterator(int from)
    {
      super(from, null);
    }
    
    public Int2BooleanLinkedOpenHashMap.MapEntry next()
    {
      return this.entry = new Int2BooleanLinkedOpenHashMap.MapEntry(Int2BooleanLinkedOpenHashMap.this, nextEntry());
    }
    
    public Int2BooleanLinkedOpenHashMap.MapEntry previous()
    {
      return this.entry = new Int2BooleanLinkedOpenHashMap.MapEntry(Int2BooleanLinkedOpenHashMap.this, previousEntry());
    }
    
    public void remove()
    {
      super.remove();
      Int2BooleanLinkedOpenHashMap.MapEntry.access$202(this.entry, -1);
    }
    
    public void set(Int2BooleanMap.Entry local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Int2BooleanMap.Entry local_ok)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class MapIterator
  {
    int prev = -1;
    int next = -1;
    int curr = -1;
    int index = -1;
    
    private MapIterator()
    {
      this.next = Int2BooleanLinkedOpenHashMap.this.first;
      this.index = 0;
    }
    
    private MapIterator(int from)
    {
      if (Int2BooleanLinkedOpenHashMap.this.key[Int2BooleanLinkedOpenHashMap.this.last] == from)
      {
        this.prev = Int2BooleanLinkedOpenHashMap.this.last;
        this.index = Int2BooleanLinkedOpenHashMap.this.size;
      }
      else
      {
        for (int pos = HashCommon.murmurHash3(from) & Int2BooleanLinkedOpenHashMap.this.mask; Int2BooleanLinkedOpenHashMap.this.used[pos] != 0; pos = pos + 1 & Int2BooleanLinkedOpenHashMap.this.mask) {
          if (Int2BooleanLinkedOpenHashMap.this.key[pos] == from)
          {
            this.next = ((int)Int2BooleanLinkedOpenHashMap.this.link[pos]);
            this.prev = pos;
            return;
          }
        }
        throw new NoSuchElementException("The key " + from + " does not belong to this map.");
      }
    }
    
    public boolean hasNext()
    {
      return this.next != -1;
    }
    
    public boolean hasPrevious()
    {
      return this.prev != -1;
    }
    
    private final void ensureIndexKnown()
    {
      if (this.index >= 0) {
        return;
      }
      if (this.prev == -1)
      {
        this.index = 0;
        return;
      }
      if (this.next == -1)
      {
        this.index = Int2BooleanLinkedOpenHashMap.this.size;
        return;
      }
      int pos = Int2BooleanLinkedOpenHashMap.this.first;
      for (this.index = 1; pos != this.prev; this.index += 1) {
        pos = (int)Int2BooleanLinkedOpenHashMap.this.link[pos];
      }
    }
    
    public int nextIndex()
    {
      ensureIndexKnown();
      return this.index;
    }
    
    public int previousIndex()
    {
      ensureIndexKnown();
      return this.index - 1;
    }
    
    public int nextEntry()
    {
      if (!hasNext()) {
        return Int2BooleanLinkedOpenHashMap.this.size();
      }
      this.curr = this.next;
      this.next = ((int)Int2BooleanLinkedOpenHashMap.this.link[this.curr]);
      this.prev = this.curr;
      if (this.index >= 0) {
        this.index += 1;
      }
      return this.curr;
    }
    
    public int previousEntry()
    {
      if (!hasPrevious()) {
        return -1;
      }
      this.curr = this.prev;
      this.prev = ((int)(Int2BooleanLinkedOpenHashMap.this.link[this.curr] >>> 32));
      this.next = this.curr;
      if (this.index >= 0) {
        this.index -= 1;
      }
      return this.curr;
    }
    
    public void remove()
    {
      ensureIndexKnown();
      if (this.curr == -1) {
        throw new IllegalStateException();
      }
      if (this.curr == this.prev)
      {
        this.index -= 1;
        this.prev = ((int)(Int2BooleanLinkedOpenHashMap.this.link[this.curr] >>> 32));
      }
      else
      {
        this.next = ((int)Int2BooleanLinkedOpenHashMap.this.link[this.curr]);
      }
      Int2BooleanLinkedOpenHashMap.this.size -= 1;
      if (this.prev == -1) {
        Int2BooleanLinkedOpenHashMap.this.first = this.next;
      } else {
        Int2BooleanLinkedOpenHashMap.this.link[this.prev] ^= (Int2BooleanLinkedOpenHashMap.this.link[this.prev] ^ this.next & 0xFFFFFFFF) & 0xFFFFFFFF;
      }
      if (this.next == -1) {
        Int2BooleanLinkedOpenHashMap.this.last = this.prev;
      } else {
        Int2BooleanLinkedOpenHashMap.this.link[this.next] ^= (Int2BooleanLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 0xFFFFFFFF) << 32) & 0x0;
      }
      int pos = this.curr;
      int last;
      for (;;)
      {
        for (pos = (last = pos) + 1 & Int2BooleanLinkedOpenHashMap.this.mask; Int2BooleanLinkedOpenHashMap.this.used[pos] != 0; pos = pos + 1 & Int2BooleanLinkedOpenHashMap.this.mask)
        {
          int slot = HashCommon.murmurHash3(Int2BooleanLinkedOpenHashMap.this.key[pos]) & Int2BooleanLinkedOpenHashMap.this.mask;
          if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) {
            break;
          }
        }
        if (Int2BooleanLinkedOpenHashMap.this.used[pos] == 0) {
          break;
        }
        Int2BooleanLinkedOpenHashMap.this.key[last] = Int2BooleanLinkedOpenHashMap.this.key[pos];
        Int2BooleanLinkedOpenHashMap.this.value[last] = Int2BooleanLinkedOpenHashMap.this.value[pos];
        if (this.next == pos) {
          this.next = last;
        }
        if (this.prev == pos) {
          this.prev = last;
        }
        Int2BooleanLinkedOpenHashMap.this.fixPointers(pos, last);
      }
      Int2BooleanLinkedOpenHashMap.this.used[last] = false;
      this.curr = -1;
    }
    
    public int skip(int local_n)
    {
      int local_i = local_n;
      while ((local_i-- != 0) && (hasNext())) {
        nextEntry();
      }
      return local_n - local_i - 1;
    }
    
    public int back(int local_n)
    {
      int local_i = local_n;
      while ((local_i-- != 0) && (hasPrevious())) {
        previousEntry();
      }
      return local_n - local_i - 1;
    }
  }
  
  private final class MapEntry
    implements Int2BooleanMap.Entry, Map.Entry<Integer, Boolean>
  {
    private int index;
    
    MapEntry(int index)
    {
      this.index = index;
    }
    
    public Integer getKey()
    {
      return Integer.valueOf(Int2BooleanLinkedOpenHashMap.this.key[this.index]);
    }
    
    public int getIntKey()
    {
      return Int2BooleanLinkedOpenHashMap.this.key[this.index];
    }
    
    public Boolean getValue()
    {
      return Boolean.valueOf(Int2BooleanLinkedOpenHashMap.this.value[this.index]);
    }
    
    public boolean getBooleanValue()
    {
      return Int2BooleanLinkedOpenHashMap.this.value[this.index];
    }
    
    public boolean setValue(boolean local_v)
    {
      boolean oldValue = Int2BooleanLinkedOpenHashMap.this.value[this.index];
      Int2BooleanLinkedOpenHashMap.this.value[this.index] = local_v;
      return oldValue;
    }
    
    public Boolean setValue(Boolean local_v)
    {
      return Boolean.valueOf(setValue(local_v.booleanValue()));
    }
    
    public boolean equals(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Integer, Boolean> local_e = (Map.Entry)local_o;
      return (Int2BooleanLinkedOpenHashMap.this.key[this.index] == ((Integer)local_e.getKey()).intValue()) && (Int2BooleanLinkedOpenHashMap.this.value[this.index] == ((Boolean)local_e.getValue()).booleanValue());
    }
    
    public int hashCode()
    {
      return Int2BooleanLinkedOpenHashMap.this.key[this.index] ^ (Int2BooleanLinkedOpenHashMap.this.value[this.index] != 0 ? 1231 : 1237);
    }
    
    public String toString()
    {
      return Int2BooleanLinkedOpenHashMap.this.key[this.index] + "=>" + Int2BooleanLinkedOpenHashMap.this.value[this.index];
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.ints.Int2BooleanLinkedOpenHashMap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */