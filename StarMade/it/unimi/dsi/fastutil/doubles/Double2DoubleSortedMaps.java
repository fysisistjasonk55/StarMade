package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

public class Double2DoubleSortedMaps
{
  public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();
  
  public static Comparator<? super Map.Entry<Double, ?>> entryComparator(DoubleComparator comparator)
  {
    new Comparator()
    {
      public int compare(Map.Entry<Double, ?> local_x, Map.Entry<Double, ?> local_y)
      {
        return this.val$comparator.compare(local_x.getKey(), local_y.getKey());
      }
    };
  }
  
  public static Double2DoubleSortedMap singleton(Double key, Double value)
  {
    return new Singleton(key.doubleValue(), value.doubleValue());
  }
  
  public static Double2DoubleSortedMap singleton(Double key, Double value, DoubleComparator comparator)
  {
    return new Singleton(key.doubleValue(), value.doubleValue(), comparator);
  }
  
  public static Double2DoubleSortedMap singleton(double key, double value)
  {
    return new Singleton(key, value);
  }
  
  public static Double2DoubleSortedMap singleton(double key, double value, DoubleComparator comparator)
  {
    return new Singleton(key, value, comparator);
  }
  
  public static Double2DoubleSortedMap synchronize(Double2DoubleSortedMap local_m)
  {
    return new SynchronizedSortedMap(local_m);
  }
  
  public static Double2DoubleSortedMap synchronize(Double2DoubleSortedMap local_m, Object sync)
  {
    return new SynchronizedSortedMap(local_m, sync);
  }
  
  public static Double2DoubleSortedMap unmodifiable(Double2DoubleSortedMap local_m)
  {
    return new UnmodifiableSortedMap(local_m);
  }
  
  public static class UnmodifiableSortedMap
    extends Double2DoubleMaps.UnmodifiableMap
    implements Double2DoubleSortedMap, Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final Double2DoubleSortedMap sortedMap;
    
    protected UnmodifiableSortedMap(Double2DoubleSortedMap local_m)
    {
      super();
      this.sortedMap = local_m;
    }
    
    public DoubleComparator comparator()
    {
      return this.sortedMap.comparator();
    }
    
    public ObjectSortedSet<Double2DoubleMap.Entry> double2DoubleEntrySet()
    {
      if (this.entries == null) {
        this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.double2DoubleEntrySet());
      }
      return (ObjectSortedSet)this.entries;
    }
    
    public ObjectSortedSet<Map.Entry<Double, Double>> entrySet()
    {
      return double2DoubleEntrySet();
    }
    
    public DoubleSortedSet keySet()
    {
      if (this.keys == null) {
        this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet());
      }
      return (DoubleSortedSet)this.keys;
    }
    
    public Double2DoubleSortedMap subMap(double from, double local_to)
    {
      return new UnmodifiableSortedMap(this.sortedMap.subMap(from, local_to));
    }
    
    public Double2DoubleSortedMap headMap(double local_to)
    {
      return new UnmodifiableSortedMap(this.sortedMap.headMap(local_to));
    }
    
    public Double2DoubleSortedMap tailMap(double from)
    {
      return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
    }
    
    public double firstDoubleKey()
    {
      return this.sortedMap.firstDoubleKey();
    }
    
    public double lastDoubleKey()
    {
      return this.sortedMap.lastDoubleKey();
    }
    
    public Double firstKey()
    {
      return (Double)this.sortedMap.firstKey();
    }
    
    public Double lastKey()
    {
      return (Double)this.sortedMap.lastKey();
    }
    
    public Double2DoubleSortedMap subMap(Double from, Double local_to)
    {
      return new UnmodifiableSortedMap(this.sortedMap.subMap(from, local_to));
    }
    
    public Double2DoubleSortedMap headMap(Double local_to)
    {
      return new UnmodifiableSortedMap(this.sortedMap.headMap(local_to));
    }
    
    public Double2DoubleSortedMap tailMap(Double from)
    {
      return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
    }
  }
  
  public static class SynchronizedSortedMap
    extends Double2DoubleMaps.SynchronizedMap
    implements Double2DoubleSortedMap, Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final Double2DoubleSortedMap sortedMap;
    
    protected SynchronizedSortedMap(Double2DoubleSortedMap local_m, Object sync)
    {
      super(sync);
      this.sortedMap = local_m;
    }
    
    protected SynchronizedSortedMap(Double2DoubleSortedMap local_m)
    {
      super();
      this.sortedMap = local_m;
    }
    
    public DoubleComparator comparator()
    {
      synchronized (this.sync)
      {
        return this.sortedMap.comparator();
      }
    }
    
    public ObjectSortedSet<Double2DoubleMap.Entry> double2DoubleEntrySet()
    {
      if (this.entries == null) {
        this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2DoubleEntrySet(), this.sync);
      }
      return (ObjectSortedSet)this.entries;
    }
    
    public ObjectSortedSet<Map.Entry<Double, Double>> entrySet()
    {
      return double2DoubleEntrySet();
    }
    
    public DoubleSortedSet keySet()
    {
      if (this.keys == null) {
        this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
      }
      return (DoubleSortedSet)this.keys;
    }
    
    public Double2DoubleSortedMap subMap(double from, double local_to)
    {
      return new SynchronizedSortedMap(this.sortedMap.subMap(from, local_to), this.sync);
    }
    
    public Double2DoubleSortedMap headMap(double local_to)
    {
      return new SynchronizedSortedMap(this.sortedMap.headMap(local_to), this.sync);
    }
    
    public Double2DoubleSortedMap tailMap(double from)
    {
      return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
    }
    
    public double firstDoubleKey()
    {
      synchronized (this.sync)
      {
        return this.sortedMap.firstDoubleKey();
      }
    }
    
    public double lastDoubleKey()
    {
      synchronized (this.sync)
      {
        return this.sortedMap.lastDoubleKey();
      }
    }
    
    public Double firstKey()
    {
      synchronized (this.sync)
      {
        return (Double)this.sortedMap.firstKey();
      }
    }
    
    public Double lastKey()
    {
      synchronized (this.sync)
      {
        return (Double)this.sortedMap.lastKey();
      }
    }
    
    public Double2DoubleSortedMap subMap(Double from, Double local_to)
    {
      return new SynchronizedSortedMap(this.sortedMap.subMap(from, local_to), this.sync);
    }
    
    public Double2DoubleSortedMap headMap(Double local_to)
    {
      return new SynchronizedSortedMap(this.sortedMap.headMap(local_to), this.sync);
    }
    
    public Double2DoubleSortedMap tailMap(Double from)
    {
      return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
    }
  }
  
  public static class Singleton
    extends Double2DoubleMaps.Singleton
    implements Double2DoubleSortedMap, Serializable, Cloneable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    protected final DoubleComparator comparator;
    
    protected Singleton(double key, double value, DoubleComparator comparator)
    {
      super(value);
      this.comparator = comparator;
    }
    
    protected Singleton(double key, double value)
    {
      this(key, value, null);
    }
    
    final int compare(double local_k1, double local_k2)
    {
      return this.comparator == null ? 1 : local_k1 == local_k2 ? 0 : local_k1 < local_k2 ? -1 : this.comparator.compare(local_k1, local_k2);
    }
    
    public DoubleComparator comparator()
    {
      return this.comparator;
    }
    
    public ObjectSortedSet<Double2DoubleMap.Entry> double2DoubleEntrySet()
    {
      if (this.entries == null) {
        this.entries = ObjectSortedSets.singleton(new Double2DoubleMaps.Singleton.SingletonEntry(this), Double2DoubleSortedMaps.entryComparator(this.comparator));
      }
      return (ObjectSortedSet)this.entries;
    }
    
    public ObjectSortedSet<Map.Entry<Double, Double>> entrySet()
    {
      return double2DoubleEntrySet();
    }
    
    public DoubleSortedSet keySet()
    {
      if (this.keys == null) {
        this.keys = DoubleSortedSets.singleton(this.key, this.comparator);
      }
      return (DoubleSortedSet)this.keys;
    }
    
    public Double2DoubleSortedMap subMap(double from, double local_to)
    {
      if ((compare(from, this.key) <= 0) && (compare(this.key, local_to) < 0)) {
        return this;
      }
      return Double2DoubleSortedMaps.EMPTY_MAP;
    }
    
    public Double2DoubleSortedMap headMap(double local_to)
    {
      if (compare(this.key, local_to) < 0) {
        return this;
      }
      return Double2DoubleSortedMaps.EMPTY_MAP;
    }
    
    public Double2DoubleSortedMap tailMap(double from)
    {
      if (compare(from, this.key) <= 0) {
        return this;
      }
      return Double2DoubleSortedMaps.EMPTY_MAP;
    }
    
    public double firstDoubleKey()
    {
      return this.key;
    }
    
    public double lastDoubleKey()
    {
      return this.key;
    }
    
    public Double2DoubleSortedMap headMap(Double oto)
    {
      return headMap(oto.doubleValue());
    }
    
    public Double2DoubleSortedMap tailMap(Double ofrom)
    {
      return tailMap(ofrom.doubleValue());
    }
    
    public Double2DoubleSortedMap subMap(Double ofrom, Double oto)
    {
      return subMap(ofrom.doubleValue(), oto.doubleValue());
    }
    
    public Double firstKey()
    {
      return Double.valueOf(firstDoubleKey());
    }
    
    public Double lastKey()
    {
      return Double.valueOf(lastDoubleKey());
    }
  }
  
  public static class EmptySortedMap
    extends Double2DoubleMaps.EmptyMap
    implements Double2DoubleSortedMap, Serializable, Cloneable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    
    public DoubleComparator comparator()
    {
      return null;
    }
    
    public ObjectSortedSet<Double2DoubleMap.Entry> double2DoubleEntrySet()
    {
      return ObjectSortedSets.EMPTY_SET;
    }
    
    public ObjectSortedSet<Map.Entry<Double, Double>> entrySet()
    {
      return ObjectSortedSets.EMPTY_SET;
    }
    
    public DoubleSortedSet keySet()
    {
      return DoubleSortedSets.EMPTY_SET;
    }
    
    public Double2DoubleSortedMap subMap(double from, double local_to)
    {
      return Double2DoubleSortedMaps.EMPTY_MAP;
    }
    
    public Double2DoubleSortedMap headMap(double local_to)
    {
      return Double2DoubleSortedMaps.EMPTY_MAP;
    }
    
    public Double2DoubleSortedMap tailMap(double from)
    {
      return Double2DoubleSortedMaps.EMPTY_MAP;
    }
    
    public double firstDoubleKey()
    {
      throw new NoSuchElementException();
    }
    
    public double lastDoubleKey()
    {
      throw new NoSuchElementException();
    }
    
    public Double2DoubleSortedMap headMap(Double oto)
    {
      return headMap(oto.doubleValue());
    }
    
    public Double2DoubleSortedMap tailMap(Double ofrom)
    {
      return tailMap(ofrom.doubleValue());
    }
    
    public Double2DoubleSortedMap subMap(Double ofrom, Double oto)
    {
      return subMap(ofrom.doubleValue(), oto.doubleValue());
    }
    
    public Double firstKey()
    {
      return Double.valueOf(firstDoubleKey());
    }
    
    public Double lastKey()
    {
      return Double.valueOf(lastDoubleKey());
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.doubles.Double2DoubleSortedMaps
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */