package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.AbstractShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Comparator;
import java.util.Map.Entry;

public abstract class AbstractReference2ShortSortedMap<K>
  extends AbstractReference2ShortMap<K>
  implements Reference2ShortSortedMap<K>
{
  public static final long serialVersionUID = -1773560792952436569L;
  
  public ReferenceSortedSet<K> keySet()
  {
    return new KeySet();
  }
  
  public ShortCollection values()
  {
    return new ValuesCollection();
  }
  
  public ObjectSortedSet<Map.Entry<K, Short>> entrySet()
  {
    return reference2ShortEntrySet();
  }
  
  protected static class ValuesIterator<K>
    extends AbstractShortIterator
  {
    protected final ObjectBidirectionalIterator<Map.Entry<K, Short>> field_53;
    
    public ValuesIterator(ObjectBidirectionalIterator<Map.Entry<K, Short>> local_i)
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
      return new AbstractReference2ShortSortedMap.ValuesIterator(AbstractReference2ShortSortedMap.this.entrySet().iterator());
    }
    
    public boolean contains(short local_k)
    {
      return AbstractReference2ShortSortedMap.this.containsValue(local_k);
    }
    
    public int size()
    {
      return AbstractReference2ShortSortedMap.this.size();
    }
    
    public void clear()
    {
      AbstractReference2ShortSortedMap.this.clear();
    }
  }
  
  protected static class KeySetIterator<K>
    extends AbstractObjectBidirectionalIterator<K>
  {
    protected final ObjectBidirectionalIterator<Map.Entry<K, Short>> field_3;
    
    public KeySetIterator(ObjectBidirectionalIterator<Map.Entry<K, Short>> local_i)
    {
      this.field_3 = local_i;
    }
    
    public K next()
    {
      return ((Map.Entry)this.field_3.next()).getKey();
    }
    
    public K previous()
    {
      return ((Map.Entry)this.field_3.previous()).getKey();
    }
    
    public boolean hasNext()
    {
      return this.field_3.hasNext();
    }
    
    public boolean hasPrevious()
    {
      return this.field_3.hasPrevious();
    }
  }
  
  protected class KeySet
    extends AbstractReferenceSortedSet<K>
  {
    protected KeySet() {}
    
    public boolean contains(Object local_k)
    {
      return AbstractReference2ShortSortedMap.this.containsKey(local_k);
    }
    
    public int size()
    {
      return AbstractReference2ShortSortedMap.this.size();
    }
    
    public void clear()
    {
      AbstractReference2ShortSortedMap.this.clear();
    }
    
    public Comparator<? super K> comparator()
    {
      return AbstractReference2ShortSortedMap.this.comparator();
    }
    
    public K first()
    {
      return AbstractReference2ShortSortedMap.this.firstKey();
    }
    
    public K last()
    {
      return AbstractReference2ShortSortedMap.this.lastKey();
    }
    
    public ReferenceSortedSet<K> headSet(K local_to)
    {
      return AbstractReference2ShortSortedMap.this.headMap(local_to).keySet();
    }
    
    public ReferenceSortedSet<K> tailSet(K from)
    {
      return AbstractReference2ShortSortedMap.this.tailMap(from).keySet();
    }
    
    public ReferenceSortedSet<K> subSet(K from, K local_to)
    {
      return AbstractReference2ShortSortedMap.this.subMap(from, local_to).keySet();
    }
    
    public ObjectBidirectionalIterator<K> iterator(K from)
    {
      return new AbstractReference2ShortSortedMap.KeySetIterator(AbstractReference2ShortSortedMap.this.entrySet().iterator(new AbstractReference2ShortMap.BasicEntry(from, (short)0)));
    }
    
    public ObjectBidirectionalIterator<K> iterator()
    {
      return new AbstractReference2ShortSortedMap.KeySetIterator(AbstractReference2ShortSortedMap.this.entrySet().iterator());
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.objects.AbstractReference2ShortSortedMap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */