package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
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

public class Short2ByteLinkedOpenHashMap
  extends AbstractShort2ByteSortedMap
  implements Serializable, Cloneable, Hash
{
  public static final long serialVersionUID = 0L;
  private static final boolean ASSERTS = false;
  protected transient short[] key;
  protected transient byte[] value;
  protected transient boolean[] used;
  protected final float field_48;
  protected transient int field_49;
  protected transient int maxFill;
  protected transient int mask;
  protected int size;
  protected volatile transient Short2ByteSortedMap.FastSortedEntrySet entries;
  protected volatile transient ShortSortedSet keys;
  protected volatile transient ByteCollection values;
  protected transient int first = -1;
  protected transient int last = -1;
  protected transient long[] link;
  
  public Short2ByteLinkedOpenHashMap(int expected, float local_f)
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
    this.key = new short[this.field_49];
    this.value = new byte[this.field_49];
    this.used = new boolean[this.field_49];
    this.link = new long[this.field_49];
  }
  
  public Short2ByteLinkedOpenHashMap(int expected)
  {
    this(expected, 0.75F);
  }
  
  public Short2ByteLinkedOpenHashMap()
  {
    this(16, 0.75F);
  }
  
  public Short2ByteLinkedOpenHashMap(Map<? extends Short, ? extends Byte> local_m, float local_f)
  {
    this(local_m.size(), local_f);
    putAll(local_m);
  }
  
  public Short2ByteLinkedOpenHashMap(Map<? extends Short, ? extends Byte> local_m)
  {
    this(local_m, 0.75F);
  }
  
  public Short2ByteLinkedOpenHashMap(Short2ByteMap local_m, float local_f)
  {
    this(local_m.size(), local_f);
    putAll(local_m);
  }
  
  public Short2ByteLinkedOpenHashMap(Short2ByteMap local_m)
  {
    this(local_m, 0.75F);
  }
  
  public Short2ByteLinkedOpenHashMap(short[] local_k, byte[] local_v, float local_f)
  {
    this(local_k.length, local_f);
    if (local_k.length != local_v.length) {
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + local_k.length + " and " + local_v.length + ")");
    }
    for (int local_i = 0; local_i < local_k.length; local_i++) {
      put(local_k[local_i], local_v[local_i]);
    }
  }
  
  public Short2ByteLinkedOpenHashMap(short[] local_k, byte[] local_v)
  {
    this(local_k, local_v, 0.75F);
  }
  
  public byte put(short local_k, byte local_v)
  {
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        byte oldValue = this.value[pos];
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
  
  public Byte put(Short local_ok, Byte local_ov)
  {
    byte local_v = local_ov.byteValue();
    short local_k = local_ok.shortValue();
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        Byte oldValue = Byte.valueOf(this.value[pos]);
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
  
  public byte add(short local_k, byte incr)
  {
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        byte oldValue = this.value[pos];
        int tmp42_41 = pos;
        byte[] tmp42_38 = this.value;
        tmp42_38[tmp42_41] = ((byte)(tmp42_38[tmp42_41] + incr));
        return oldValue;
      }
    }
    this.used[pos] = true;
    this.key[pos] = local_k;
    this.value[pos] = ((byte)(this.defRetValue + incr));
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
  
  public byte remove(short local_k)
  {
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        this.size -= 1;
        fixPointers(pos);
        byte local_v = this.value[pos];
        shiftKeys(pos);
        return local_v;
      }
    }
    return this.defRetValue;
  }
  
  public Byte remove(Object local_ok)
  {
    short local_k = ((Short)local_ok).shortValue();
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        this.size -= 1;
        fixPointers(pos);
        byte local_v = this.value[pos];
        shiftKeys(pos);
        return Byte.valueOf(local_v);
      }
    }
    return null;
  }
  
  public byte removeFirstByte()
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
    byte local_v = this.value[pos];
    shiftKeys(pos);
    return local_v;
  }
  
  public byte removeLastByte()
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
    byte local_v = this.value[pos];
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
  
  public byte getAndMoveToFirst(short local_k)
  {
    short[] key = this.key;
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
  
  public byte getAndMoveToLast(short local_k)
  {
    short[] key = this.key;
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
  
  public byte putAndMoveToFirst(short local_k, byte local_v)
  {
    short[] key = this.key;
    boolean[] used = this.used;
    int mask = this.mask;
    for (int pos = HashCommon.murmurHash3(local_k) & mask; used[pos] != 0; pos = pos + 1 & mask) {
      if (local_k == key[pos])
      {
        byte oldValue = this.value[pos];
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
  
  public byte putAndMoveToLast(short local_k, byte local_v)
  {
    short[] key = this.key;
    boolean[] used = this.used;
    int mask = this.mask;
    for (int pos = HashCommon.murmurHash3(local_k) & mask; used[pos] != 0; pos = pos + 1 & mask) {
      if (local_k == key[pos])
      {
        byte oldValue = this.value[pos];
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
  
  public Byte get(Short local_ok)
  {
    short local_k = local_ok.shortValue();
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k) {
        return Byte.valueOf(this.value[pos]);
      }
    }
    return null;
  }
  
  public byte get(short local_k)
  {
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k) {
        return this.value[pos];
      }
    }
    return this.defRetValue;
  }
  
  public boolean containsKey(short local_k)
  {
    for (int pos = HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsValue(byte local_v)
  {
    byte[] value = this.value;
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
  
  public short firstShortKey()
  {
    if (this.size == 0) {
      throw new NoSuchElementException();
    }
    return this.key[this.first];
  }
  
  public short lastShortKey()
  {
    if (this.size == 0) {
      throw new NoSuchElementException();
    }
    return this.key[this.last];
  }
  
  public ShortComparator comparator()
  {
    return null;
  }
  
  public Short2ByteSortedMap tailMap(short from)
  {
    throw new UnsupportedOperationException();
  }
  
  public Short2ByteSortedMap headMap(short local_to)
  {
    throw new UnsupportedOperationException();
  }
  
  public Short2ByteSortedMap subMap(short from, short local_to)
  {
    throw new UnsupportedOperationException();
  }
  
  public Short2ByteSortedMap.FastSortedEntrySet short2ByteEntrySet()
  {
    if (this.entries == null) {
      this.entries = new MapEntrySet(null);
    }
    return this.entries;
  }
  
  public ShortSortedSet keySet()
  {
    if (this.keys == null) {
      this.keys = new KeySet(null);
    }
    return this.keys;
  }
  
  public ByteCollection values()
  {
    if (this.values == null) {
      this.values = new AbstractByteCollection()
      {
        public ByteIterator iterator()
        {
          return new Short2ByteLinkedOpenHashMap.ValueIterator(Short2ByteLinkedOpenHashMap.this);
        }
        
        public int size()
        {
          return Short2ByteLinkedOpenHashMap.this.size;
        }
        
        public boolean contains(byte local_v)
        {
          return Short2ByteLinkedOpenHashMap.this.containsValue(local_v);
        }
        
        public void clear()
        {
          Short2ByteLinkedOpenHashMap.this.clear();
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
    short[] key = this.key;
    byte[] value = this.value;
    int newMask = newN - 1;
    short[] newKey = new short[newN];
    byte[] newValue = new byte[newN];
    boolean[] newUsed = new boolean[newN];
    long[] link = this.link;
    long[] newLink = new long[newN];
    this.first = -1;
    int local_j = this.size;
    while (local_j-- != 0)
    {
      short local_k = key[local_i];
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
  
  public Short2ByteLinkedOpenHashMap clone()
  {
    Short2ByteLinkedOpenHashMap local_c;
    try
    {
      local_c = (Short2ByteLinkedOpenHashMap)super.clone();
    }
    catch (CloneNotSupportedException cantHappen)
    {
      throw new InternalError();
    }
    local_c.keys = null;
    local_c.values = null;
    local_c.entries = null;
    local_c.key = ((short[])this.key.clone());
    local_c.value = ((byte[])this.value.clone());
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
      local_t ^= this.value[local_i];
      local_h += local_t;
      local_i++;
    }
    return local_h;
  }
  
  private void writeObject(ObjectOutputStream local_s)
    throws IOException
  {
    short[] key = this.key;
    byte[] value = this.value;
    MapIterator local_i = new MapIterator(null);
    local_s.defaultWriteObject();
    int local_j = this.size;
    while (local_j-- != 0)
    {
      int local_e = local_i.nextEntry();
      local_s.writeShort(key[local_e]);
      local_s.writeByte(value[local_e]);
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
    byte[] value = this.value = new byte[this.field_49];
    boolean[] used = this.used = new boolean[this.field_49];
    long[] link = this.link = new long[this.field_49];
    int prev = -1;
    this.first = (this.last = -1);
    int local_i = this.size;
    int pos = 0;
    while (local_i-- != 0)
    {
      short local_k = local_s.readShort();
      byte local_v = local_s.readByte();
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
    extends Short2ByteLinkedOpenHashMap.MapIterator
    implements ByteListIterator
  {
    public byte previousByte()
    {
      return Short2ByteLinkedOpenHashMap.this.value[previousEntry()];
    }
    
    public Byte previous()
    {
      return Byte.valueOf(Short2ByteLinkedOpenHashMap.this.value[previousEntry()]);
    }
    
    public void set(Byte local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Byte local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void set(byte local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(byte local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public ValueIterator()
    {
      super(null);
    }
    
    public byte nextByte()
    {
      return Short2ByteLinkedOpenHashMap.this.value[nextEntry()];
    }
    
    public Byte next()
    {
      return Byte.valueOf(Short2ByteLinkedOpenHashMap.this.value[nextEntry()]);
    }
  }
  
  private final class KeySet
    extends AbstractShortSortedSet
  {
    private KeySet() {}
    
    public ShortListIterator iterator(short from)
    {
      return new Short2ByteLinkedOpenHashMap.KeyIterator(Short2ByteLinkedOpenHashMap.this, from);
    }
    
    public ShortListIterator iterator()
    {
      return new Short2ByteLinkedOpenHashMap.KeyIterator(Short2ByteLinkedOpenHashMap.this);
    }
    
    public int size()
    {
      return Short2ByteLinkedOpenHashMap.this.size;
    }
    
    public boolean contains(short local_k)
    {
      return Short2ByteLinkedOpenHashMap.this.containsKey(local_k);
    }
    
    public boolean remove(short local_k)
    {
      int oldSize = Short2ByteLinkedOpenHashMap.this.size;
      Short2ByteLinkedOpenHashMap.this.remove(local_k);
      return Short2ByteLinkedOpenHashMap.this.size != oldSize;
    }
    
    public void clear()
    {
      Short2ByteLinkedOpenHashMap.this.clear();
    }
    
    public short firstShort()
    {
      if (Short2ByteLinkedOpenHashMap.this.size == 0) {
        throw new NoSuchElementException();
      }
      return Short2ByteLinkedOpenHashMap.this.key[Short2ByteLinkedOpenHashMap.this.first];
    }
    
    public short lastShort()
    {
      if (Short2ByteLinkedOpenHashMap.this.size == 0) {
        throw new NoSuchElementException();
      }
      return Short2ByteLinkedOpenHashMap.this.key[Short2ByteLinkedOpenHashMap.this.last];
    }
    
    public ShortComparator comparator()
    {
      return null;
    }
    
    public final ShortSortedSet tailSet(short from)
    {
      throw new UnsupportedOperationException();
    }
    
    public final ShortSortedSet headSet(short local_to)
    {
      throw new UnsupportedOperationException();
    }
    
    public final ShortSortedSet subSet(short from, short local_to)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private final class KeyIterator
    extends Short2ByteLinkedOpenHashMap.MapIterator
    implements ShortListIterator
  {
    public KeyIterator(short local_k)
    {
      super(local_k, null);
    }
    
    public short previousShort()
    {
      return Short2ByteLinkedOpenHashMap.this.key[previousEntry()];
    }
    
    public void set(short local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(short local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public Short previous()
    {
      return Short.valueOf(Short2ByteLinkedOpenHashMap.this.key[previousEntry()]);
    }
    
    public void set(Short local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Short local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public KeyIterator()
    {
      super(null);
    }
    
    public short nextShort()
    {
      return Short2ByteLinkedOpenHashMap.this.key[nextEntry()];
    }
    
    public Short next()
    {
      return Short.valueOf(Short2ByteLinkedOpenHashMap.this.key[nextEntry()]);
    }
  }
  
  private final class MapEntrySet
    extends AbstractObjectSortedSet<Short2ByteMap.Entry>
    implements Short2ByteSortedMap.FastSortedEntrySet
  {
    private MapEntrySet() {}
    
    public ObjectBidirectionalIterator<Short2ByteMap.Entry> iterator()
    {
      return new Short2ByteLinkedOpenHashMap.EntryIterator(Short2ByteLinkedOpenHashMap.this);
    }
    
    public Comparator<? super Short2ByteMap.Entry> comparator()
    {
      return null;
    }
    
    public ObjectSortedSet<Short2ByteMap.Entry> subSet(Short2ByteMap.Entry fromElement, Short2ByteMap.Entry toElement)
    {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSortedSet<Short2ByteMap.Entry> headSet(Short2ByteMap.Entry toElement)
    {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSortedSet<Short2ByteMap.Entry> tailSet(Short2ByteMap.Entry fromElement)
    {
      throw new UnsupportedOperationException();
    }
    
    public Short2ByteMap.Entry first()
    {
      if (Short2ByteLinkedOpenHashMap.this.size == 0) {
        throw new NoSuchElementException();
      }
      return new Short2ByteLinkedOpenHashMap.MapEntry(Short2ByteLinkedOpenHashMap.this, Short2ByteLinkedOpenHashMap.this.first);
    }
    
    public Short2ByteMap.Entry last()
    {
      if (Short2ByteLinkedOpenHashMap.this.size == 0) {
        throw new NoSuchElementException();
      }
      return new Short2ByteLinkedOpenHashMap.MapEntry(Short2ByteLinkedOpenHashMap.this, Short2ByteLinkedOpenHashMap.this.last);
    }
    
    public boolean contains(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Short, Byte> local_e = (Map.Entry)local_o;
      short local_k = ((Short)local_e.getKey()).shortValue();
      for (int pos = HashCommon.murmurHash3(local_k) & Short2ByteLinkedOpenHashMap.this.mask; Short2ByteLinkedOpenHashMap.this.used[pos] != 0; pos = pos + 1 & Short2ByteLinkedOpenHashMap.this.mask) {
        if (Short2ByteLinkedOpenHashMap.this.key[pos] == local_k) {
          return Short2ByteLinkedOpenHashMap.this.value[pos] == ((Byte)local_e.getValue()).byteValue();
        }
      }
      return false;
    }
    
    public boolean remove(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Short, Byte> local_e = (Map.Entry)local_o;
      short local_k = ((Short)local_e.getKey()).shortValue();
      for (int pos = HashCommon.murmurHash3(local_k) & Short2ByteLinkedOpenHashMap.this.mask; Short2ByteLinkedOpenHashMap.this.used[pos] != 0; pos = pos + 1 & Short2ByteLinkedOpenHashMap.this.mask) {
        if (Short2ByteLinkedOpenHashMap.this.key[pos] == local_k)
        {
          Short2ByteLinkedOpenHashMap.this.remove(local_e.getKey());
          return true;
        }
      }
      return false;
    }
    
    public int size()
    {
      return Short2ByteLinkedOpenHashMap.this.size;
    }
    
    public void clear()
    {
      Short2ByteLinkedOpenHashMap.this.clear();
    }
    
    public ObjectBidirectionalIterator<Short2ByteMap.Entry> iterator(Short2ByteMap.Entry from)
    {
      return new Short2ByteLinkedOpenHashMap.EntryIterator(Short2ByteLinkedOpenHashMap.this, ((Short)from.getKey()).shortValue());
    }
    
    public ObjectBidirectionalIterator<Short2ByteMap.Entry> fastIterator()
    {
      return new Short2ByteLinkedOpenHashMap.FastEntryIterator(Short2ByteLinkedOpenHashMap.this);
    }
    
    public ObjectBidirectionalIterator<Short2ByteMap.Entry> fastIterator(Short2ByteMap.Entry from)
    {
      return new Short2ByteLinkedOpenHashMap.FastEntryIterator(Short2ByteLinkedOpenHashMap.this, ((Short)from.getKey()).shortValue());
    }
  }
  
  private class FastEntryIterator
    extends Short2ByteLinkedOpenHashMap.MapIterator
    implements ObjectListIterator<Short2ByteMap.Entry>
  {
    final AbstractShort2ByteMap.BasicEntry entry = new AbstractShort2ByteMap.BasicEntry((short)0, (byte)0);
    
    public FastEntryIterator()
    {
      super(null);
    }
    
    public FastEntryIterator(short from)
    {
      super(from, null);
    }
    
    public AbstractShort2ByteMap.BasicEntry next()
    {
      int local_e = nextEntry();
      this.entry.key = Short2ByteLinkedOpenHashMap.this.key[local_e];
      this.entry.value = Short2ByteLinkedOpenHashMap.this.value[local_e];
      return this.entry;
    }
    
    public AbstractShort2ByteMap.BasicEntry previous()
    {
      int local_e = previousEntry();
      this.entry.key = Short2ByteLinkedOpenHashMap.this.key[local_e];
      this.entry.value = Short2ByteLinkedOpenHashMap.this.value[local_e];
      return this.entry;
    }
    
    public void set(Short2ByteMap.Entry local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Short2ByteMap.Entry local_ok)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class EntryIterator
    extends Short2ByteLinkedOpenHashMap.MapIterator
    implements ObjectListIterator<Short2ByteMap.Entry>
  {
    private Short2ByteLinkedOpenHashMap.MapEntry entry;
    
    public EntryIterator()
    {
      super(null);
    }
    
    public EntryIterator(short from)
    {
      super(from, null);
    }
    
    public Short2ByteLinkedOpenHashMap.MapEntry next()
    {
      return this.entry = new Short2ByteLinkedOpenHashMap.MapEntry(Short2ByteLinkedOpenHashMap.this, nextEntry());
    }
    
    public Short2ByteLinkedOpenHashMap.MapEntry previous()
    {
      return this.entry = new Short2ByteLinkedOpenHashMap.MapEntry(Short2ByteLinkedOpenHashMap.this, previousEntry());
    }
    
    public void remove()
    {
      super.remove();
      Short2ByteLinkedOpenHashMap.MapEntry.access$202(this.entry, -1);
    }
    
    public void set(Short2ByteMap.Entry local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Short2ByteMap.Entry local_ok)
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
      this.next = Short2ByteLinkedOpenHashMap.this.first;
      this.index = 0;
    }
    
    private MapIterator(short from)
    {
      if (Short2ByteLinkedOpenHashMap.this.key[Short2ByteLinkedOpenHashMap.this.last] == from)
      {
        this.prev = Short2ByteLinkedOpenHashMap.this.last;
        this.index = Short2ByteLinkedOpenHashMap.this.size;
      }
      else
      {
        for (int pos = HashCommon.murmurHash3(from) & Short2ByteLinkedOpenHashMap.this.mask; Short2ByteLinkedOpenHashMap.this.used[pos] != 0; pos = pos + 1 & Short2ByteLinkedOpenHashMap.this.mask) {
          if (Short2ByteLinkedOpenHashMap.this.key[pos] == from)
          {
            this.next = ((int)Short2ByteLinkedOpenHashMap.this.link[pos]);
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
        this.index = Short2ByteLinkedOpenHashMap.this.size;
        return;
      }
      int pos = Short2ByteLinkedOpenHashMap.this.first;
      for (this.index = 1; pos != this.prev; this.index += 1) {
        pos = (int)Short2ByteLinkedOpenHashMap.this.link[pos];
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
        return Short2ByteLinkedOpenHashMap.this.size();
      }
      this.curr = this.next;
      this.next = ((int)Short2ByteLinkedOpenHashMap.this.link[this.curr]);
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
      this.prev = ((int)(Short2ByteLinkedOpenHashMap.this.link[this.curr] >>> 32));
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
        this.prev = ((int)(Short2ByteLinkedOpenHashMap.this.link[this.curr] >>> 32));
      }
      else
      {
        this.next = ((int)Short2ByteLinkedOpenHashMap.this.link[this.curr]);
      }
      Short2ByteLinkedOpenHashMap.this.size -= 1;
      if (this.prev == -1) {
        Short2ByteLinkedOpenHashMap.this.first = this.next;
      } else {
        Short2ByteLinkedOpenHashMap.this.link[this.prev] ^= (Short2ByteLinkedOpenHashMap.this.link[this.prev] ^ this.next & 0xFFFFFFFF) & 0xFFFFFFFF;
      }
      if (this.next == -1) {
        Short2ByteLinkedOpenHashMap.this.last = this.prev;
      } else {
        Short2ByteLinkedOpenHashMap.this.link[this.next] ^= (Short2ByteLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 0xFFFFFFFF) << 32) & 0x0;
      }
      int pos = this.curr;
      int last;
      for (;;)
      {
        for (pos = (last = pos) + 1 & Short2ByteLinkedOpenHashMap.this.mask; Short2ByteLinkedOpenHashMap.this.used[pos] != 0; pos = pos + 1 & Short2ByteLinkedOpenHashMap.this.mask)
        {
          int slot = HashCommon.murmurHash3(Short2ByteLinkedOpenHashMap.this.key[pos]) & Short2ByteLinkedOpenHashMap.this.mask;
          if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) {
            break;
          }
        }
        if (Short2ByteLinkedOpenHashMap.this.used[pos] == 0) {
          break;
        }
        Short2ByteLinkedOpenHashMap.this.key[last] = Short2ByteLinkedOpenHashMap.this.key[pos];
        Short2ByteLinkedOpenHashMap.this.value[last] = Short2ByteLinkedOpenHashMap.this.value[pos];
        if (this.next == pos) {
          this.next = last;
        }
        if (this.prev == pos) {
          this.prev = last;
        }
        Short2ByteLinkedOpenHashMap.this.fixPointers(pos, last);
      }
      Short2ByteLinkedOpenHashMap.this.used[last] = false;
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
    implements Short2ByteMap.Entry, Map.Entry<Short, Byte>
  {
    private int index;
    
    MapEntry(int index)
    {
      this.index = index;
    }
    
    public Short getKey()
    {
      return Short.valueOf(Short2ByteLinkedOpenHashMap.this.key[this.index]);
    }
    
    public short getShortKey()
    {
      return Short2ByteLinkedOpenHashMap.this.key[this.index];
    }
    
    public Byte getValue()
    {
      return Byte.valueOf(Short2ByteLinkedOpenHashMap.this.value[this.index]);
    }
    
    public byte getByteValue()
    {
      return Short2ByteLinkedOpenHashMap.this.value[this.index];
    }
    
    public byte setValue(byte local_v)
    {
      byte oldValue = Short2ByteLinkedOpenHashMap.this.value[this.index];
      Short2ByteLinkedOpenHashMap.this.value[this.index] = local_v;
      return oldValue;
    }
    
    public Byte setValue(Byte local_v)
    {
      return Byte.valueOf(setValue(local_v.byteValue()));
    }
    
    public boolean equals(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Short, Byte> local_e = (Map.Entry)local_o;
      return (Short2ByteLinkedOpenHashMap.this.key[this.index] == ((Short)local_e.getKey()).shortValue()) && (Short2ByteLinkedOpenHashMap.this.value[this.index] == ((Byte)local_e.getValue()).byteValue());
    }
    
    public int hashCode()
    {
      return Short2ByteLinkedOpenHashMap.this.key[this.index] ^ Short2ByteLinkedOpenHashMap.this.value[this.index];
    }
    
    public String toString()
    {
      return Short2ByteLinkedOpenHashMap.this.key[this.index] + "=>" + Short2ByteLinkedOpenHashMap.this.value[this.index];
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.shorts.Short2ByteLinkedOpenHashMap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */