package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.AbstractShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Map.Entry;

public abstract class AbstractByte2ShortSortedMap
  extends AbstractByte2ShortMap
  implements Byte2ShortSortedMap
{
  public static final long serialVersionUID = -1773560792952436569L;
  
  public Byte2ShortSortedMap headMap(Byte local_to)
  {
    return headMap(local_to.byteValue());
  }
  
  public Byte2ShortSortedMap tailMap(Byte from)
  {
    return tailMap(from.byteValue());
  }
  
  public Byte2ShortSortedMap subMap(Byte from, Byte local_to)
  {
    return subMap(from.byteValue(), local_to.byteValue());
  }
  
  public Byte firstKey()
  {
    return Byte.valueOf(firstByteKey());
  }
  
  public Byte lastKey()
  {
    return Byte.valueOf(lastByteKey());
  }
  
  public ByteSortedSet keySet()
  {
    return new KeySet();
  }
  
  public ShortCollection values()
  {
    return new ValuesCollection();
  }
  
  public ObjectSortedSet<Map.Entry<Byte, Short>> entrySet()
  {
    return byte2ShortEntrySet();
  }
  
  protected static class ValuesIterator
    extends AbstractShortIterator
  {
    protected final ObjectBidirectionalIterator<Map.Entry<Byte, Short>> field_53;
    
    public ValuesIterator(ObjectBidirectionalIterator<Map.Entry<Byte, Short>> local_i)
    {
      this.field_53 = local_i;
    }
    
    public short nextShort()
    {
      return ((Short)((Map.Entry)this.field_53.next()).getValue()).shortValue();
    }
    
    public boolean hasNext()
    {
      return this.field_53.hasNext();
    }
  }
  
  protected class ValuesCollection
    extends AbstractShortCollection
  {
    protected ValuesCollection() {}
    
    public ShortIterator iterator()
    {
      return new AbstractByte2ShortSortedMap.ValuesIterator(AbstractByte2ShortSortedMap.this.entrySet().iterator());
    }
    
    public boolean contains(short local_k)
    {
      return AbstractByte2ShortSortedMap.this.containsValue(local_k);
    }
    
    public int size()
    {
      return AbstractByte2ShortSortedMap.this.size();
    }
    
    public void clear()
    {
      AbstractByte2ShortSortedMap.this.clear();
    }
  }
  
  protected static class KeySetIterator
    extends AbstractByteBidirectionalIterator
  {
    protected final ObjectBidirectionalIterator<Map.Entry<Byte, Short>> field_58;
    
    public KeySetIterator(ObjectBidirectionalIterator<Map.Entry<Byte, Short>> local_i)
    {
      this.field_58 = local_i;
    }
    
    public byte nextByte()
    {
      return ((Byte)((Map.Entry)this.field_58.next()).getKey()).byteValue();
    }
    
    public byte previousByte()
    {
      return ((Byte)((Map.Entry)this.field_58.previous()).getKey()).byteValue();
    }
    
    public boolean hasNext()
    {
      return this.field_58.hasNext();
    }
    
    public boolean hasPrevious()
    {
      return this.field_58.hasPrevious();
    }
  }
  
  protected class KeySet
    extends AbstractByteSortedSet
  {
    protected KeySet() {}
    
    public boolean contains(byte local_k)
    {
      return AbstractByte2ShortSortedMap.this.containsKey(local_k);
    }
    
    public int size()
    {
      return AbstractByte2ShortSortedMap.this.size();
    }
    
    public void clear()
    {
      AbstractByte2ShortSortedMap.this.clear();
    }
    
    public ByteComparator comparator()
    {
      return AbstractByte2ShortSortedMap.this.comparator();
    }
    
    public byte firstByte()
    {
      return AbstractByte2ShortSortedMap.this.firstByteKey();
    }
    
    public byte lastByte()
    {
      return AbstractByte2ShortSortedMap.this.lastByteKey();
    }
    
    public ByteSortedSet headSet(byte local_to)
    {
      return AbstractByte2ShortSortedMap.this.headMap(local_to).keySet();
    }
    
    public ByteSortedSet tailSet(byte from)
    {
      return AbstractByte2ShortSortedMap.this.tailMap(from).keySet();
    }
    
    public ByteSortedSet subSet(byte from, byte local_to)
    {
      return AbstractByte2ShortSortedMap.this.subMap(from, local_to).keySet();
    }
    
    public ByteBidirectionalIterator iterator(byte from)
    {
      return new AbstractByte2ShortSortedMap.KeySetIterator(AbstractByte2ShortSortedMap.this.entrySet().iterator(new AbstractByte2ShortMap.BasicEntry(from, (short)0)));
    }
    
    public ByteBidirectionalIterator iterator()
    {
      return new AbstractByte2ShortSortedMap.KeySetIterator(AbstractByte2ShortSortedMap.this.entrySet().iterator());
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.bytes.AbstractByte2ShortSortedMap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */