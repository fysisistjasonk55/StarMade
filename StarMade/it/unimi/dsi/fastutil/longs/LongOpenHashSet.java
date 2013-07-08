package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LongOpenHashSet
  extends AbstractLongSet
  implements Serializable, Cloneable, Hash
{
  public static final long serialVersionUID = 0L;
  private static final boolean ASSERTS = false;
  protected transient long[] key;
  protected transient boolean[] used;
  protected final float field_156;
  protected transient int field_157;
  protected transient int maxFill;
  protected transient int mask;
  protected int size;
  
  public LongOpenHashSet(int expected, float local_f)
  {
    if ((local_f <= 0.0F) || (local_f > 1.0F)) {
      throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
    }
    if (expected < 0) {
      throw new IllegalArgumentException("The expected number of elements must be nonnegative");
    }
    this.field_156 = local_f;
    this.field_157 = HashCommon.arraySize(expected, local_f);
    this.mask = (this.field_157 - 1);
    this.maxFill = HashCommon.maxFill(this.field_157, local_f);
    this.key = new long[this.field_157];
    this.used = new boolean[this.field_157];
  }
  
  public LongOpenHashSet(int expected)
  {
    this(expected, 0.75F);
  }
  
  public LongOpenHashSet()
  {
    this(16, 0.75F);
  }
  
  public LongOpenHashSet(Collection<? extends Long> local_c, float local_f)
  {
    this(local_c.size(), local_f);
    addAll(local_c);
  }
  
  public LongOpenHashSet(Collection<? extends Long> local_c)
  {
    this(local_c, 0.75F);
  }
  
  public LongOpenHashSet(LongCollection local_c, float local_f)
  {
    this(local_c.size(), local_f);
    addAll(local_c);
  }
  
  public LongOpenHashSet(LongCollection local_c)
  {
    this(local_c, 0.75F);
  }
  
  public LongOpenHashSet(LongIterator local_i, float local_f)
  {
    this(16, local_f);
    while (local_i.hasNext()) {
      add(local_i.nextLong());
    }
  }
  
  public LongOpenHashSet(LongIterator local_i)
  {
    this(local_i, 0.75F);
  }
  
  public LongOpenHashSet(Iterator<?> local_i, float local_f)
  {
    this(LongIterators.asLongIterator(local_i), local_f);
  }
  
  public LongOpenHashSet(Iterator<?> local_i)
  {
    this(LongIterators.asLongIterator(local_i));
  }
  
  public LongOpenHashSet(long[] local_a, int offset, int length, float local_f)
  {
    this(length < 0 ? 0 : length, local_f);
    LongArrays.ensureOffsetLength(local_a, offset, length);
    for (int local_i = 0; local_i < length; local_i++) {
      add(local_a[(offset + local_i)]);
    }
  }
  
  public LongOpenHashSet(long[] local_a, int offset, int length)
  {
    this(local_a, offset, length, 0.75F);
  }
  
  public LongOpenHashSet(long[] local_a, float local_f)
  {
    this(local_a, 0, local_a.length, local_f);
  }
  
  public LongOpenHashSet(long[] local_a)
  {
    this(local_a, 0.75F);
  }
  
  public boolean add(long local_k)
  {
    for (int pos = (int)HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k) {
        return false;
      }
    }
    this.used[pos] = true;
    this.key[pos] = local_k;
    if (++this.size >= this.maxFill) {
      rehash(HashCommon.arraySize(this.size + 1, this.field_156));
    }
    return true;
  }
  
  protected final int shiftKeys(int pos)
  {
    int last;
    for (;;)
    {
      for (pos = (last = pos) + 1 & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask)
      {
        int slot = (int)HashCommon.murmurHash3(this.key[pos]) & this.mask;
        if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) {
          break;
        }
      }
      if (this.used[pos] == 0) {
        break;
      }
      this.key[last] = this.key[pos];
    }
    this.used[last] = false;
    return last;
  }
  
  public boolean remove(long local_k)
  {
    for (int pos = (int)HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k)
      {
        this.size -= 1;
        shiftKeys(pos);
        return true;
      }
    }
    return false;
  }
  
  public boolean contains(long local_k)
  {
    for (int pos = (int)HashCommon.murmurHash3(local_k) & this.mask; this.used[pos] != 0; pos = pos + 1 & this.mask) {
      if (this.key[pos] == local_k) {
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
  
  public LongIterator iterator()
  {
    return new SetIterator(null);
  }
  
  @Deprecated
  public boolean rehash()
  {
    return true;
  }
  
  public boolean trim()
  {
    int local_l = HashCommon.arraySize(this.size, this.field_156);
    if (local_l >= this.field_157) {
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
    int local_l = HashCommon.nextPowerOfTwo((int)Math.ceil(local_n / this.field_156));
    if (this.field_157 <= local_l) {
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
    int newMask = newN - 1;
    long[] newKey = new long[newN];
    boolean[] newUsed = new boolean[newN];
    int local_j = this.size;
    while (local_j-- != 0)
    {
      while (used[local_i] == 0) {
        local_i++;
      }
      long local_k = key[local_i];
      for (int pos = (int)HashCommon.murmurHash3(local_k) & newMask; newUsed[pos] != 0; pos = pos + 1 & newMask) {}
      newUsed[pos] = true;
      newKey[pos] = local_k;
      local_i++;
    }
    this.field_157 = newN;
    this.mask = newMask;
    this.maxFill = HashCommon.maxFill(this.field_157, this.field_156);
    this.key = newKey;
    this.used = newUsed;
  }
  
  public LongOpenHashSet clone()
  {
    LongOpenHashSet local_c;
    try
    {
      local_c = (LongOpenHashSet)super.clone();
    }
    catch (CloneNotSupportedException cantHappen)
    {
      throw new InternalError();
    }
    local_c.key = ((long[])this.key.clone());
    local_c.used = ((boolean[])this.used.clone());
    return local_c;
  }
  
  public int hashCode()
  {
    int local_h = 0;
    int local_i = 0;
    int local_j = this.size;
    while (local_j-- != 0)
    {
      while (this.used[local_i] == 0) {
        local_i++;
      }
      local_h += HashCommon.long2int(this.key[local_i]);
      local_i++;
    }
    return local_h;
  }
  
  private void writeObject(ObjectOutputStream local_s)
    throws IOException
  {
    LongIterator local_i = iterator();
    local_s.defaultWriteObject();
    int local_j = this.size;
    while (local_j-- != 0) {
      local_s.writeLong(local_i.nextLong());
    }
  }
  
  private void readObject(ObjectInputStream local_s)
    throws IOException, ClassNotFoundException
  {
    local_s.defaultReadObject();
    this.field_157 = HashCommon.arraySize(this.size, this.field_156);
    this.maxFill = HashCommon.maxFill(this.field_157, this.field_156);
    this.mask = (this.field_157 - 1);
    long[] key = this.key = new long[this.field_157];
    boolean[] used = this.used = new boolean[this.field_157];
    int local_i = this.size;
    int pos = 0;
    while (local_i-- != 0)
    {
      long local_k = local_s.readLong();
      for (pos = (int)HashCommon.murmurHash3(local_k) & this.mask; used[pos] != 0; pos = pos + 1 & this.mask) {}
      used[pos] = true;
      key[pos] = local_k;
    }
  }
  
  private void checkTable() {}
  
  private class SetIterator
    extends AbstractLongIterator
  {
    int pos = LongOpenHashSet.this.field_157;
    int last = -1;
    int field_386 = LongOpenHashSet.this.size;
    LongArrayList wrapped;
    
    private SetIterator()
    {
      boolean[] used = LongOpenHashSet.this.used;
      while ((this.field_386 != 0) && (used[(--this.pos)] == 0)) {}
    }
    
    public boolean hasNext()
    {
      return this.field_386 != 0;
    }
    
    public long nextLong()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      this.field_386 -= 1;
      if (this.pos < 0) {
        return this.wrapped.getLong(-(this.last = --this.pos) - 2);
      }
      long retVal = LongOpenHashSet.this.key[(this.last = this.pos)];
      if (this.field_386 != 0)
      {
        boolean[] used = LongOpenHashSet.this.used;
        while ((this.pos-- != 0) && (used[this.pos] == 0)) {}
      }
      return retVal;
    }
    
    final int shiftKeys(int pos)
    {
      int last;
      for (;;)
      {
        for (pos = (last = pos) + 1 & LongOpenHashSet.this.mask; LongOpenHashSet.this.used[pos] != 0; pos = pos + 1 & LongOpenHashSet.this.mask)
        {
          int slot = (int)HashCommon.murmurHash3(LongOpenHashSet.this.key[pos]) & LongOpenHashSet.this.mask;
          if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) {
            break;
          }
        }
        if (LongOpenHashSet.this.used[pos] == 0) {
          break;
        }
        if (pos < last)
        {
          if (this.wrapped == null) {
            this.wrapped = new LongArrayList();
          }
          this.wrapped.add(LongOpenHashSet.this.key[pos]);
        }
        LongOpenHashSet.this.key[last] = LongOpenHashSet.this.key[pos];
      }
      LongOpenHashSet.this.used[last] = false;
      return last;
    }
    
    public void remove()
    {
      if (this.last == -1) {
        throw new IllegalStateException();
      }
      if (this.pos < -1)
      {
        LongOpenHashSet.this.remove(this.wrapped.getLong(-this.pos - 2));
        this.last = -1;
        return;
      }
      LongOpenHashSet.this.size -= 1;
      if ((shiftKeys(this.last) == this.pos) && (this.field_386 > 0))
      {
        this.field_386 += 1;
        nextLong();
      }
      this.last = -1;
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.longs.LongOpenHashSet
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */