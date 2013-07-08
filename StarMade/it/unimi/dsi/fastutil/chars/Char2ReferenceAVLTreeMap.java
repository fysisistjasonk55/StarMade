package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Char2ReferenceAVLTreeMap<V>
  extends AbstractChar2ReferenceSortedMap<V>
  implements Serializable, Cloneable
{
  protected transient Entry<V> tree;
  protected int count;
  protected transient Entry<V> firstEntry;
  protected transient Entry<V> lastEntry;
  protected volatile transient ObjectSortedSet<Char2ReferenceMap.Entry<V>> entries;
  protected volatile transient CharSortedSet keys;
  protected volatile transient ReferenceCollection<V> values;
  protected transient boolean modified;
  protected Comparator<? super Character> storedComparator;
  protected transient CharComparator actualComparator;
  public static final long serialVersionUID = -7046029254386353129L;
  private static final boolean ASSERTS = false;
  private transient boolean[] dirPath;
  
  public Char2ReferenceAVLTreeMap()
  {
    allocatePaths();
    this.tree = null;
    this.count = 0;
  }
  
  private void setActualComparator()
  {
    if ((this.storedComparator == null) || ((this.storedComparator instanceof CharComparator))) {
      this.actualComparator = ((CharComparator)this.storedComparator);
    } else {
      this.actualComparator = new CharComparator()
      {
        public int compare(char local_k1, char local_k2)
        {
          return Char2ReferenceAVLTreeMap.this.storedComparator.compare(Character.valueOf(local_k1), Character.valueOf(local_k2));
        }
        
        public int compare(Character ok1, Character ok2)
        {
          return Char2ReferenceAVLTreeMap.this.storedComparator.compare(ok1, ok2);
        }
      };
    }
  }
  
  public Char2ReferenceAVLTreeMap(Comparator<? super Character> local_c)
  {
    this();
    this.storedComparator = local_c;
    setActualComparator();
  }
  
  public Char2ReferenceAVLTreeMap(Map<? extends Character, ? extends V> local_m)
  {
    this();
    putAll(local_m);
  }
  
  public Char2ReferenceAVLTreeMap(SortedMap<Character, V> local_m)
  {
    this(local_m.comparator());
    putAll(local_m);
  }
  
  public Char2ReferenceAVLTreeMap(Char2ReferenceMap<? extends V> local_m)
  {
    this();
    putAll(local_m);
  }
  
  public Char2ReferenceAVLTreeMap(Char2ReferenceSortedMap<V> local_m)
  {
    this(local_m.comparator());
    putAll(local_m);
  }
  
  public Char2ReferenceAVLTreeMap(char[] local_k, V[] local_v, Comparator<? super Character> local_c)
  {
    this(local_c);
    if (local_k.length != local_v.length) {
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + local_k.length + " and " + local_v.length + ")");
    }
    for (int local_i = 0; local_i < local_k.length; local_i++) {
      put(local_k[local_i], local_v[local_i]);
    }
  }
  
  public Char2ReferenceAVLTreeMap(char[] local_k, V[] local_v)
  {
    this(local_k, local_v, null);
  }
  
  final int compare(char local_k1, char local_k2)
  {
    return this.actualComparator == null ? 1 : local_k1 == local_k2 ? 0 : local_k1 < local_k2 ? -1 : this.actualComparator.compare(local_k1, local_k2);
  }
  
  final Entry<V> findKey(char local_k)
  {
    int cmp;
    for (Entry<V> local_e = this.tree; (local_e != null) && ((cmp = compare(local_k, local_e.key)) != 0); local_e = cmp < 0 ? local_e.left() : local_e.right()) {}
    return local_e;
  }
  
  final Entry<V> locateKey(char local_k)
  {
    Entry<V> local_e = this.tree;
    Entry<V> last = this.tree;
    int cmp = 0;
    while ((local_e != null) && ((cmp = compare(local_k, local_e.key)) != 0))
    {
      last = local_e;
      local_e = cmp < 0 ? local_e.left() : local_e.right();
    }
    return cmp == 0 ? local_e : last;
  }
  
  private void allocatePaths()
  {
    this.dirPath = new boolean[48];
  }
  
  public V put(char local_k, V local_v)
  {
    this.modified = false;
    if (this.tree == null)
    {
      this.count += 1;
      this.tree = (this.lastEntry = this.firstEntry = new Entry(local_k, local_v));
      this.modified = true;
    }
    else
    {
      Entry<V> local_p = this.tree;
      Entry<V> local_q = null;
      Entry<V> local_y = this.tree;
      Entry<V> local_z = null;
      Entry<V> local_e = null;
      Entry<V> local_w = null;
      int local_i = 0;
      for (;;)
      {
        int cmp;
        if ((cmp = compare(local_k, local_p.key)) == 0)
        {
          V oldValue = local_p.value;
          local_p.value = local_v;
          return oldValue;
        }
        if (local_p.balance() != 0)
        {
          local_i = 0;
          local_z = local_q;
          local_y = local_p;
        }
        if ((this.dirPath[(local_i++)] = cmp > 0 ? 1 : 0) != 0)
        {
          if (local_p.succ())
          {
            this.count += 1;
            local_e = new Entry(local_k, local_v);
            this.modified = true;
            if (local_p.right == null) {
              this.lastEntry = local_e;
            }
            local_e.left = local_p;
            local_e.right = local_p.right;
            local_p.right(local_e);
            break;
          }
          local_q = local_p;
          local_p = local_p.right;
        }
        else
        {
          if (local_p.pred())
          {
            this.count += 1;
            local_e = new Entry(local_k, local_v);
            this.modified = true;
            if (local_p.left == null) {
              this.firstEntry = local_e;
            }
            local_e.right = local_p;
            local_e.left = local_p.left;
            local_p.left(local_e);
            break;
          }
          local_q = local_p;
          local_p = local_p.left;
        }
      }
      local_p = local_y;
      local_i = 0;
      while (local_p != local_e)
      {
        if (this.dirPath[local_i] != 0) {
          local_p.incBalance();
        } else {
          local_p.decBalance();
        }
        local_p = this.dirPath[(local_i++)] != 0 ? local_p.right : local_p.left;
      }
      if (local_y.balance() == -2)
      {
        Entry<V> oldValue = local_y.left;
        if (oldValue.balance() == -1)
        {
          local_w = oldValue;
          if (oldValue.succ())
          {
            oldValue.succ(false);
            local_y.pred(oldValue);
          }
          else
          {
            local_y.left = oldValue.right;
          }
          oldValue.right = local_y;
          oldValue.balance(0);
          local_y.balance(0);
        }
        else
        {
          local_w = oldValue.right;
          oldValue.right = local_w.left;
          local_w.left = oldValue;
          local_y.left = local_w.right;
          local_w.right = local_y;
          if (local_w.balance() == -1)
          {
            oldValue.balance(0);
            local_y.balance(1);
          }
          else if (local_w.balance() == 0)
          {
            oldValue.balance(0);
            local_y.balance(0);
          }
          else
          {
            oldValue.balance(-1);
            local_y.balance(0);
          }
          local_w.balance(0);
          if (local_w.pred())
          {
            oldValue.succ(local_w);
            local_w.pred(false);
          }
          if (local_w.succ())
          {
            local_y.pred(local_w);
            local_w.succ(false);
          }
        }
      }
      else if (local_y.balance() == 2)
      {
        Entry<V> oldValue = local_y.right;
        if (oldValue.balance() == 1)
        {
          local_w = oldValue;
          if (oldValue.pred())
          {
            oldValue.pred(false);
            local_y.succ(oldValue);
          }
          else
          {
            local_y.right = oldValue.left;
          }
          oldValue.left = local_y;
          oldValue.balance(0);
          local_y.balance(0);
        }
        else
        {
          local_w = oldValue.left;
          oldValue.left = local_w.right;
          local_w.right = oldValue;
          local_y.right = local_w.left;
          local_w.left = local_y;
          if (local_w.balance() == 1)
          {
            oldValue.balance(0);
            local_y.balance(-1);
          }
          else if (local_w.balance() == 0)
          {
            oldValue.balance(0);
            local_y.balance(0);
          }
          else
          {
            oldValue.balance(1);
            local_y.balance(0);
          }
          local_w.balance(0);
          if (local_w.pred())
          {
            local_y.succ(local_w);
            local_w.pred(false);
          }
          if (local_w.succ())
          {
            oldValue.pred(local_w);
            local_w.succ(false);
          }
        }
      }
      else
      {
        return this.defRetValue;
      }
      if (local_z == null) {
        this.tree = local_w;
      } else if (local_z.left == local_y) {
        local_z.left = local_w;
      } else {
        local_z.right = local_w;
      }
    }
    return this.defRetValue;
  }
  
  private Entry<V> parent(Entry<V> local_e)
  {
    if (local_e == this.tree) {
      return null;
    }
    Entry<V> local_y;
    Entry<V> local_x = local_y = local_e;
    for (;;)
    {
      if (local_y.succ())
      {
        Entry<V> local_p = local_y.right;
        if ((local_p == null) || (local_p.left != local_e))
        {
          while (!local_x.pred()) {
            local_x = local_x.left;
          }
          local_p = local_x.left;
        }
        return local_p;
      }
      if (local_x.pred())
      {
        Entry<V> local_p = local_x.left;
        if ((local_p == null) || (local_p.right != local_e))
        {
          while (!local_y.succ()) {
            local_y = local_y.right;
          }
          local_p = local_y.right;
        }
        return local_p;
      }
      local_x = local_x.left;
      local_y = local_y.right;
    }
  }
  
  public V remove(char local_k)
  {
    this.modified = false;
    if (this.tree == null) {
      return this.defRetValue;
    }
    Entry<V> local_p = this.tree;
    Entry<V> local_q = null;
    boolean dir = false;
    char local_kk = local_k;
    int cmp;
    while ((cmp = compare(local_kk, local_p.key)) != 0) {
      if ((dir = cmp > 0 ? 1 : 0) != 0)
      {
        local_q = local_p;
        if ((local_p = local_p.right()) == null) {
          return this.defRetValue;
        }
      }
      else
      {
        local_q = local_p;
        if ((local_p = local_p.left()) == null) {
          return this.defRetValue;
        }
      }
    }
    if (local_p.left == null) {
      this.firstEntry = local_p.next();
    }
    if (local_p.right == null) {
      this.lastEntry = local_p.prev();
    }
    if (local_p.succ())
    {
      if (local_p.pred())
      {
        if (local_q != null)
        {
          if (dir) {
            local_q.succ(local_p.right);
          } else {
            local_q.pred(local_p.left);
          }
        }
        else {
          this.tree = (dir ? local_p.right : local_p.left);
        }
      }
      else
      {
        local_p.prev().right = local_p.right;
        if (local_q != null)
        {
          if (dir) {
            local_q.right = local_p.left;
          } else {
            local_q.left = local_p.left;
          }
        }
        else {
          this.tree = local_p.left;
        }
      }
    }
    else
    {
      Entry<V> local_r = local_p.right;
      if (local_r.pred())
      {
        local_r.left = local_p.left;
        local_r.pred(local_p.pred());
        if (!local_r.pred()) {
          local_r.prev().right = local_r;
        }
        if (local_q != null)
        {
          if (dir) {
            local_q.right = local_r;
          } else {
            local_q.left = local_r;
          }
        }
        else {
          this.tree = local_r;
        }
        local_r.balance(local_p.balance());
        local_q = local_r;
        dir = true;
      }
      else
      {
        Entry<V> local_s;
        for (;;)
        {
          local_s = local_r.left;
          if (local_s.pred()) {
            break;
          }
          local_r = local_s;
        }
        if (local_s.succ()) {
          local_r.pred(local_s);
        } else {
          local_r.left = local_s.right;
        }
        local_s.left = local_p.left;
        if (!local_p.pred())
        {
          local_p.prev().right = local_s;
          local_s.pred(false);
        }
        local_s.right = local_p.right;
        local_s.succ(false);
        if (local_q != null)
        {
          if (dir) {
            local_q.right = local_s;
          } else {
            local_q.left = local_s;
          }
        }
        else {
          this.tree = local_s;
        }
        local_s.balance(local_p.balance());
        local_q = local_r;
        dir = false;
      }
    }
    while (local_q != null)
    {
      Entry<V> local_r = local_q;
      local_q = parent(local_r);
      if (!dir)
      {
        dir = (local_q != null) && (local_q.left != local_r);
        local_r.incBalance();
        if (local_r.balance() == 1) {
          break;
        }
        if (local_r.balance() == 2)
        {
          Entry<V> local_s = local_r.right;
          if (local_s.balance() == -1)
          {
            Entry<V> local_w = local_s.left;
            local_s.left = local_w.right;
            local_w.right = local_s;
            local_r.right = local_w.left;
            local_w.left = local_r;
            if (local_w.balance() == 1)
            {
              local_s.balance(0);
              local_r.balance(-1);
            }
            else if (local_w.balance() == 0)
            {
              local_s.balance(0);
              local_r.balance(0);
            }
            else
            {
              local_s.balance(1);
              local_r.balance(0);
            }
            local_w.balance(0);
            if (local_w.pred())
            {
              local_r.succ(local_w);
              local_w.pred(false);
            }
            if (local_w.succ())
            {
              local_s.pred(local_w);
              local_w.succ(false);
            }
            if (local_q != null)
            {
              if (dir) {
                local_q.right = local_w;
              } else {
                local_q.left = local_w;
              }
            }
            else {
              this.tree = local_w;
            }
          }
          else
          {
            if (local_q != null)
            {
              if (dir) {
                local_q.right = local_s;
              } else {
                local_q.left = local_s;
              }
            }
            else {
              this.tree = local_s;
            }
            if (local_s.balance() == 0)
            {
              local_r.right = local_s.left;
              local_s.left = local_r;
              local_s.balance(-1);
              local_r.balance(1);
              break;
            }
            if (local_s.pred())
            {
              local_r.succ(true);
              local_s.pred(false);
            }
            else
            {
              local_r.right = local_s.left;
            }
            local_s.left = local_r;
            local_r.balance(0);
            local_s.balance(0);
          }
        }
      }
      else
      {
        dir = (local_q != null) && (local_q.left != local_r);
        local_r.decBalance();
        if (local_r.balance() == -1) {
          break;
        }
        if (local_r.balance() == -2)
        {
          Entry<V> local_s = local_r.left;
          if (local_s.balance() == 1)
          {
            Entry<V> local_w = local_s.right;
            local_s.right = local_w.left;
            local_w.left = local_s;
            local_r.left = local_w.right;
            local_w.right = local_r;
            if (local_w.balance() == -1)
            {
              local_s.balance(0);
              local_r.balance(1);
            }
            else if (local_w.balance() == 0)
            {
              local_s.balance(0);
              local_r.balance(0);
            }
            else
            {
              local_s.balance(-1);
              local_r.balance(0);
            }
            local_w.balance(0);
            if (local_w.pred())
            {
              local_s.succ(local_w);
              local_w.pred(false);
            }
            if (local_w.succ())
            {
              local_r.pred(local_w);
              local_w.succ(false);
            }
            if (local_q != null)
            {
              if (dir) {
                local_q.right = local_w;
              } else {
                local_q.left = local_w;
              }
            }
            else {
              this.tree = local_w;
            }
          }
          else
          {
            if (local_q != null)
            {
              if (dir) {
                local_q.right = local_s;
              } else {
                local_q.left = local_s;
              }
            }
            else {
              this.tree = local_s;
            }
            if (local_s.balance() == 0)
            {
              local_r.left = local_s.right;
              local_s.right = local_r;
              local_s.balance(1);
              local_r.balance(-1);
              break;
            }
            if (local_s.succ())
            {
              local_r.pred(true);
              local_s.succ(false);
            }
            else
            {
              local_r.left = local_s.right;
            }
            local_s.right = local_r;
            local_r.balance(0);
            local_s.balance(0);
          }
        }
      }
    }
    this.modified = true;
    this.count -= 1;
    return local_p.value;
  }
  
  public V put(Character local_ok, V local_ov)
  {
    V oldValue = put(local_ok.charValue(), local_ov);
    return this.modified ? this.defRetValue : oldValue;
  }
  
  public V remove(Object local_ok)
  {
    V oldValue = remove(((Character)local_ok).charValue());
    return this.modified ? oldValue : this.defRetValue;
  }
  
  public boolean containsValue(Object local_v)
  {
    Char2ReferenceAVLTreeMap<V>.ValueIterator local_i = new ValueIterator(null);
    int local_j = this.count;
    while (local_j-- != 0)
    {
      V local_ev = local_i.next();
      if (local_ev == local_v) {
        return true;
      }
    }
    return false;
  }
  
  public void clear()
  {
    this.count = 0;
    this.tree = null;
    this.entries = null;
    this.values = null;
    this.keys = null;
    this.firstEntry = (this.lastEntry = null);
  }
  
  public boolean containsKey(char local_k)
  {
    return findKey(local_k) != null;
  }
  
  public int size()
  {
    return this.count;
  }
  
  public boolean isEmpty()
  {
    return this.count == 0;
  }
  
  public V get(char local_k)
  {
    Entry<V> local_e = findKey(local_k);
    return local_e == null ? this.defRetValue : local_e.value;
  }
  
  public char firstCharKey()
  {
    if (this.tree == null) {
      throw new NoSuchElementException();
    }
    return this.firstEntry.key;
  }
  
  public char lastCharKey()
  {
    if (this.tree == null) {
      throw new NoSuchElementException();
    }
    return this.lastEntry.key;
  }
  
  public ObjectSortedSet<Char2ReferenceMap.Entry<V>> char2ReferenceEntrySet()
  {
    if (this.entries == null) {
      this.entries = new AbstractObjectSortedSet()
      {
        final Comparator<? super Char2ReferenceMap.Entry<V>> comparator = new Comparator()
        {
          public int compare(Char2ReferenceMap.Entry<V> local_x, Char2ReferenceMap.Entry<V> local_y)
          {
            return Char2ReferenceAVLTreeMap.this.storedComparator.compare(local_x.getKey(), local_y.getKey());
          }
        };
        
        public Comparator<? super Char2ReferenceMap.Entry<V>> comparator()
        {
          return this.comparator;
        }
        
        public ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> iterator()
        {
          return new Char2ReferenceAVLTreeMap.EntryIterator(Char2ReferenceAVLTreeMap.this);
        }
        
        public ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> iterator(Char2ReferenceMap.Entry<V> from)
        {
          return new Char2ReferenceAVLTreeMap.EntryIterator(Char2ReferenceAVLTreeMap.this, ((Character)from.getKey()).charValue());
        }
        
        public boolean contains(Object local_o)
        {
          if (!(local_o instanceof Map.Entry)) {
            return false;
          }
          Map.Entry<Character, V> local_e = (Map.Entry)local_o;
          Char2ReferenceAVLTreeMap.Entry<V> local_f = Char2ReferenceAVLTreeMap.this.findKey(((Character)local_e.getKey()).charValue());
          return local_e.equals(local_f);
        }
        
        public boolean remove(Object local_o)
        {
          if (!(local_o instanceof Map.Entry)) {
            return false;
          }
          Map.Entry<Character, V> local_e = (Map.Entry)local_o;
          Char2ReferenceAVLTreeMap.Entry<V> local_f = Char2ReferenceAVLTreeMap.this.findKey(((Character)local_e.getKey()).charValue());
          if (local_f != null) {
            Char2ReferenceAVLTreeMap.this.remove(local_f.key);
          }
          return local_f != null;
        }
        
        public int size()
        {
          return Char2ReferenceAVLTreeMap.this.count;
        }
        
        public void clear()
        {
          Char2ReferenceAVLTreeMap.this.clear();
        }
        
        public Char2ReferenceMap.Entry<V> first()
        {
          return Char2ReferenceAVLTreeMap.this.firstEntry;
        }
        
        public Char2ReferenceMap.Entry<V> last()
        {
          return Char2ReferenceAVLTreeMap.this.lastEntry;
        }
        
        public ObjectSortedSet<Char2ReferenceMap.Entry<V>> subSet(Char2ReferenceMap.Entry<V> from, Char2ReferenceMap.Entry<V> local_to)
        {
          return Char2ReferenceAVLTreeMap.this.subMap((Character)from.getKey(), (Character)local_to.getKey()).char2ReferenceEntrySet();
        }
        
        public ObjectSortedSet<Char2ReferenceMap.Entry<V>> headSet(Char2ReferenceMap.Entry<V> local_to)
        {
          return Char2ReferenceAVLTreeMap.this.headMap((Character)local_to.getKey()).char2ReferenceEntrySet();
        }
        
        public ObjectSortedSet<Char2ReferenceMap.Entry<V>> tailSet(Char2ReferenceMap.Entry<V> from)
        {
          return Char2ReferenceAVLTreeMap.this.tailMap((Character)from.getKey()).char2ReferenceEntrySet();
        }
      };
    }
    return this.entries;
  }
  
  public CharSortedSet keySet()
  {
    if (this.keys == null) {
      this.keys = new KeySet(null);
    }
    return this.keys;
  }
  
  public ReferenceCollection<V> values()
  {
    if (this.values == null) {
      this.values = new AbstractReferenceCollection()
      {
        public ObjectIterator<V> iterator()
        {
          return new Char2ReferenceAVLTreeMap.ValueIterator(Char2ReferenceAVLTreeMap.this, null);
        }
        
        public boolean contains(Object local_k)
        {
          return Char2ReferenceAVLTreeMap.this.containsValue(local_k);
        }
        
        public int size()
        {
          return Char2ReferenceAVLTreeMap.this.count;
        }
        
        public void clear()
        {
          Char2ReferenceAVLTreeMap.this.clear();
        }
      };
    }
    return this.values;
  }
  
  public CharComparator comparator()
  {
    return this.actualComparator;
  }
  
  public Char2ReferenceSortedMap<V> headMap(char local_to)
  {
    return new Submap('\000', true, local_to, false);
  }
  
  public Char2ReferenceSortedMap<V> tailMap(char from)
  {
    return new Submap(from, false, '\000', true);
  }
  
  public Char2ReferenceSortedMap<V> subMap(char from, char local_to)
  {
    return new Submap(from, false, local_to, false);
  }
  
  public Char2ReferenceAVLTreeMap<V> clone()
  {
    Char2ReferenceAVLTreeMap<V> local_c;
    try
    {
      local_c = (Char2ReferenceAVLTreeMap)super.clone();
    }
    catch (CloneNotSupportedException cantHappen)
    {
      throw new InternalError();
    }
    local_c.keys = null;
    local_c.values = null;
    local_c.entries = null;
    local_c.allocatePaths();
    if (this.count != 0)
    {
      Entry<V> local_rp = new Entry();
      Entry<V> local_rq = new Entry();
      Entry<V> local_p = local_rp;
      local_rp.left(this.tree);
      Entry<V> local_q = local_rq;
      local_rq.pred(null);
      for (;;)
      {
        if (!local_p.pred())
        {
          Entry<V> cantHappen = local_p.left.clone();
          cantHappen.pred(local_q.left);
          cantHappen.succ(local_q);
          local_q.left(cantHappen);
          local_p = local_p.left;
          local_q = local_q.left;
        }
        else
        {
          while (local_p.succ())
          {
            local_p = local_p.right;
            if (local_p == null)
            {
              local_q.right = null;
              local_c.tree = local_rq.left;
              for (local_c.firstEntry = local_c.tree; local_c.firstEntry.left != null; local_c.firstEntry = local_c.firstEntry.left) {}
              for (local_c.lastEntry = local_c.tree; local_c.lastEntry.right != null; local_c.lastEntry = local_c.lastEntry.right) {}
              return local_c;
            }
            local_q = local_q.right;
          }
          local_p = local_p.right;
          local_q = local_q.right;
        }
        if (!local_p.succ())
        {
          Entry<V> cantHappen = local_p.right.clone();
          cantHappen.succ(local_q.right);
          cantHappen.pred(local_q);
          local_q.right(cantHappen);
        }
      }
    }
    return local_c;
  }
  
  private void writeObject(ObjectOutputStream local_s)
    throws IOException
  {
    int local_n = this.count;
    Char2ReferenceAVLTreeMap<V>.EntryIterator local_i = new EntryIterator();
    local_s.defaultWriteObject();
    while (local_n-- != 0)
    {
      Entry<V> local_e = local_i.nextEntry();
      local_s.writeChar(local_e.key);
      local_s.writeObject(local_e.value);
    }
  }
  
  private Entry<V> readTree(ObjectInputStream local_s, int local_n, Entry<V> pred, Entry<V> succ)
    throws IOException, ClassNotFoundException
  {
    if (local_n == 1)
    {
      Entry<V> top = new Entry(local_s.readChar(), local_s.readObject());
      top.pred(pred);
      top.succ(succ);
      return top;
    }
    if (local_n == 2)
    {
      Entry<V> top = new Entry(local_s.readChar(), local_s.readObject());
      top.right(new Entry(local_s.readChar(), local_s.readObject()));
      top.right.pred(top);
      top.balance(1);
      top.pred(pred);
      top.right.succ(succ);
      return top;
    }
    int top = local_n / 2;
    int leftN = local_n - top - 1;
    Entry<V> top = new Entry();
    top.left(readTree(local_s, leftN, pred, top));
    top.key = local_s.readChar();
    top.value = local_s.readObject();
    top.right(readTree(local_s, top, top, succ));
    if (local_n == (local_n & -local_n)) {
      top.balance(1);
    }
    return top;
  }
  
  private void readObject(ObjectInputStream local_s)
    throws IOException, ClassNotFoundException
  {
    local_s.defaultReadObject();
    setActualComparator();
    allocatePaths();
    if (this.count != 0)
    {
      this.tree = readTree(local_s, this.count, null, null);
      for (Entry<V> local_e = this.tree; local_e.left() != null; local_e = local_e.left()) {}
      this.firstEntry = local_e;
      for (local_e = this.tree; local_e.right() != null; local_e = local_e.right()) {}
      this.lastEntry = local_e;
    }
  }
  
  private static int checkTree(Entry local_e)
  {
    return 0;
  }
  
  private final class Submap
    extends AbstractChar2ReferenceSortedMap<V>
    implements Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    char from;
    char field_47;
    boolean bottom;
    boolean top;
    protected volatile transient ObjectSortedSet<Char2ReferenceMap.Entry<V>> entries;
    protected volatile transient CharSortedSet keys;
    protected volatile transient ReferenceCollection<V> values;
    
    public Submap(char from, boolean bottom, char local_to, boolean top)
    {
      if ((!bottom) && (!top) && (Char2ReferenceAVLTreeMap.this.compare(from, local_to) > 0)) {
        throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + local_to + ")");
      }
      this.from = from;
      this.bottom = bottom;
      this.field_47 = local_to;
      this.top = top;
      this.defRetValue = Char2ReferenceAVLTreeMap.this.defRetValue;
    }
    
    public void clear()
    {
      Char2ReferenceAVLTreeMap<V>.Submap.SubmapIterator local_i = new SubmapIterator();
      while (local_i.hasNext())
      {
        local_i.nextEntry();
        local_i.remove();
      }
    }
    
    final boolean in2(char local_k)
    {
      return ((this.bottom) || (Char2ReferenceAVLTreeMap.this.compare(local_k, this.from) >= 0)) && ((this.top) || (Char2ReferenceAVLTreeMap.this.compare(local_k, this.field_47) < 0));
    }
    
    public ObjectSortedSet<Char2ReferenceMap.Entry<V>> char2ReferenceEntrySet()
    {
      if (this.entries == null) {
        this.entries = new AbstractObjectSortedSet()
        {
          public ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> iterator()
          {
            return new Char2ReferenceAVLTreeMap.Submap.SubmapEntryIterator(Char2ReferenceAVLTreeMap.Submap.this);
          }
          
          public ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> iterator(Char2ReferenceMap.Entry<V> from)
          {
            return new Char2ReferenceAVLTreeMap.Submap.SubmapEntryIterator(Char2ReferenceAVLTreeMap.Submap.this, ((Character)from.getKey()).charValue());
          }
          
          public Comparator<? super Char2ReferenceMap.Entry<V>> comparator()
          {
            return Char2ReferenceAVLTreeMap.this.entrySet().comparator();
          }
          
          public boolean contains(Object local_o)
          {
            if (!(local_o instanceof Map.Entry)) {
              return false;
            }
            Map.Entry<Character, V> local_e = (Map.Entry)local_o;
            Char2ReferenceAVLTreeMap.Entry<V> local_f = Char2ReferenceAVLTreeMap.this.findKey(((Character)local_e.getKey()).charValue());
            return (local_f != null) && (Char2ReferenceAVLTreeMap.Submap.this.in2(local_f.key)) && (local_e.equals(local_f));
          }
          
          public boolean remove(Object local_o)
          {
            if (!(local_o instanceof Map.Entry)) {
              return false;
            }
            Map.Entry<Character, V> local_e = (Map.Entry)local_o;
            Char2ReferenceAVLTreeMap.Entry<V> local_f = Char2ReferenceAVLTreeMap.this.findKey(((Character)local_e.getKey()).charValue());
            if ((local_f != null) && (Char2ReferenceAVLTreeMap.Submap.this.in2(local_f.key))) {
              Char2ReferenceAVLTreeMap.Submap.this.remove(local_f.key);
            }
            return local_f != null;
          }
          
          public int size()
          {
            int local_c = 0;
            Iterator<?> local_i = iterator();
            while (local_i.hasNext())
            {
              local_c++;
              local_i.next();
            }
            return local_c;
          }
          
          public boolean isEmpty()
          {
            return !new Char2ReferenceAVLTreeMap.Submap.SubmapIterator(Char2ReferenceAVLTreeMap.Submap.this).hasNext();
          }
          
          public void clear()
          {
            Char2ReferenceAVLTreeMap.Submap.this.clear();
          }
          
          public Char2ReferenceMap.Entry<V> first()
          {
            return Char2ReferenceAVLTreeMap.Submap.this.firstEntry();
          }
          
          public Char2ReferenceMap.Entry<V> last()
          {
            return Char2ReferenceAVLTreeMap.Submap.this.lastEntry();
          }
          
          public ObjectSortedSet<Char2ReferenceMap.Entry<V>> subSet(Char2ReferenceMap.Entry<V> from, Char2ReferenceMap.Entry<V> local_to)
          {
            return Char2ReferenceAVLTreeMap.Submap.this.subMap((Character)from.getKey(), (Character)local_to.getKey()).char2ReferenceEntrySet();
          }
          
          public ObjectSortedSet<Char2ReferenceMap.Entry<V>> headSet(Char2ReferenceMap.Entry<V> local_to)
          {
            return Char2ReferenceAVLTreeMap.Submap.this.headMap((Character)local_to.getKey()).char2ReferenceEntrySet();
          }
          
          public ObjectSortedSet<Char2ReferenceMap.Entry<V>> tailSet(Char2ReferenceMap.Entry<V> from)
          {
            return Char2ReferenceAVLTreeMap.Submap.this.tailMap((Character)from.getKey()).char2ReferenceEntrySet();
          }
        };
      }
      return this.entries;
    }
    
    public CharSortedSet keySet()
    {
      if (this.keys == null) {
        this.keys = new KeySet(null);
      }
      return this.keys;
    }
    
    public ReferenceCollection<V> values()
    {
      if (this.values == null) {
        this.values = new AbstractReferenceCollection()
        {
          public ObjectIterator<V> iterator()
          {
            return new Char2ReferenceAVLTreeMap.Submap.SubmapValueIterator(Char2ReferenceAVLTreeMap.Submap.this, null);
          }
          
          public boolean contains(Object local_k)
          {
            return Char2ReferenceAVLTreeMap.Submap.this.containsValue(local_k);
          }
          
          public int size()
          {
            return Char2ReferenceAVLTreeMap.Submap.this.size();
          }
          
          public void clear()
          {
            Char2ReferenceAVLTreeMap.Submap.this.clear();
          }
        };
      }
      return this.values;
    }
    
    public boolean containsKey(char local_k)
    {
      return (in2(local_k)) && (Char2ReferenceAVLTreeMap.this.containsKey(local_k));
    }
    
    public boolean containsValue(Object local_v)
    {
      Char2ReferenceAVLTreeMap<V>.Submap.SubmapIterator local_i = new SubmapIterator();
      while (local_i.hasNext())
      {
        Object local_ev = local_i.nextEntry().value;
        if (local_ev == local_v) {
          return true;
        }
      }
      return false;
    }
    
    public V get(char local_k)
    {
      char local_kk = local_k;
      Char2ReferenceAVLTreeMap.Entry<V> local_e;
      return (in2(local_kk)) && ((local_e = Char2ReferenceAVLTreeMap.this.findKey(local_kk)) != null) ? local_e.value : this.defRetValue;
    }
    
    public V put(char local_k, V local_v)
    {
      Char2ReferenceAVLTreeMap.this.modified = false;
      if (!in2(local_k)) {
        throw new IllegalArgumentException("Key (" + local_k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.field_47)) + ")");
      }
      V oldValue = Char2ReferenceAVLTreeMap.this.put(local_k, local_v);
      return Char2ReferenceAVLTreeMap.this.modified ? this.defRetValue : oldValue;
    }
    
    public V put(Character local_ok, V local_ov)
    {
      V oldValue = put(local_ok.charValue(), local_ov);
      return Char2ReferenceAVLTreeMap.this.modified ? this.defRetValue : oldValue;
    }
    
    public V remove(char local_k)
    {
      Char2ReferenceAVLTreeMap.this.modified = false;
      if (!in2(local_k)) {
        return this.defRetValue;
      }
      V oldValue = Char2ReferenceAVLTreeMap.this.remove(local_k);
      return Char2ReferenceAVLTreeMap.this.modified ? oldValue : this.defRetValue;
    }
    
    public V remove(Object local_ok)
    {
      V oldValue = remove(((Character)local_ok).charValue());
      return Char2ReferenceAVLTreeMap.this.modified ? oldValue : this.defRetValue;
    }
    
    public int size()
    {
      Char2ReferenceAVLTreeMap<V>.Submap.SubmapIterator local_i = new SubmapIterator();
      int local_n = 0;
      while (local_i.hasNext())
      {
        local_n++;
        local_i.nextEntry();
      }
      return local_n;
    }
    
    public boolean isEmpty()
    {
      return !new SubmapIterator().hasNext();
    }
    
    public CharComparator comparator()
    {
      return Char2ReferenceAVLTreeMap.this.actualComparator;
    }
    
    public Char2ReferenceSortedMap<V> headMap(char local_to)
    {
      if (this.top) {
        return new Submap(Char2ReferenceAVLTreeMap.this, this.from, this.bottom, local_to, false);
      }
      return Char2ReferenceAVLTreeMap.this.compare(local_to, this.field_47) < 0 ? new Submap(Char2ReferenceAVLTreeMap.this, this.from, this.bottom, local_to, false) : this;
    }
    
    public Char2ReferenceSortedMap<V> tailMap(char from)
    {
      if (this.bottom) {
        return new Submap(Char2ReferenceAVLTreeMap.this, from, false, this.field_47, this.top);
      }
      return Char2ReferenceAVLTreeMap.this.compare(from, this.from) > 0 ? new Submap(Char2ReferenceAVLTreeMap.this, from, false, this.field_47, this.top) : this;
    }
    
    public Char2ReferenceSortedMap<V> subMap(char from, char local_to)
    {
      if ((this.top) && (this.bottom)) {
        return new Submap(Char2ReferenceAVLTreeMap.this, from, false, local_to, false);
      }
      if (!this.top) {
        local_to = Char2ReferenceAVLTreeMap.this.compare(local_to, this.field_47) < 0 ? local_to : this.field_47;
      }
      if (!this.bottom) {
        from = Char2ReferenceAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
      }
      if ((!this.top) && (!this.bottom) && (from == this.from) && (local_to == this.field_47)) {
        return this;
      }
      return new Submap(Char2ReferenceAVLTreeMap.this, from, false, local_to, false);
    }
    
    public Char2ReferenceAVLTreeMap.Entry<V> firstEntry()
    {
      if (Char2ReferenceAVLTreeMap.this.tree == null) {
        return null;
      }
      Char2ReferenceAVLTreeMap.Entry<V> local_e;
      Char2ReferenceAVLTreeMap.Entry<V> local_e;
      if (this.bottom)
      {
        local_e = Char2ReferenceAVLTreeMap.this.firstEntry;
      }
      else
      {
        local_e = Char2ReferenceAVLTreeMap.this.locateKey(this.from);
        if (Char2ReferenceAVLTreeMap.this.compare(local_e.key, this.from) < 0) {
          local_e = local_e.next();
        }
      }
      if ((local_e == null) || ((!this.top) && (Char2ReferenceAVLTreeMap.this.compare(local_e.key, this.field_47) >= 0))) {
        return null;
      }
      return local_e;
    }
    
    public Char2ReferenceAVLTreeMap.Entry<V> lastEntry()
    {
      if (Char2ReferenceAVLTreeMap.this.tree == null) {
        return null;
      }
      Char2ReferenceAVLTreeMap.Entry<V> local_e;
      Char2ReferenceAVLTreeMap.Entry<V> local_e;
      if (this.top)
      {
        local_e = Char2ReferenceAVLTreeMap.this.lastEntry;
      }
      else
      {
        local_e = Char2ReferenceAVLTreeMap.this.locateKey(this.field_47);
        if (Char2ReferenceAVLTreeMap.this.compare(local_e.key, this.field_47) >= 0) {
          local_e = local_e.prev();
        }
      }
      if ((local_e == null) || ((!this.bottom) && (Char2ReferenceAVLTreeMap.this.compare(local_e.key, this.from) < 0))) {
        return null;
      }
      return local_e;
    }
    
    public char firstCharKey()
    {
      Char2ReferenceAVLTreeMap.Entry<V> local_e = firstEntry();
      if (local_e == null) {
        throw new NoSuchElementException();
      }
      return local_e.key;
    }
    
    public char lastCharKey()
    {
      Char2ReferenceAVLTreeMap.Entry<V> local_e = lastEntry();
      if (local_e == null) {
        throw new NoSuchElementException();
      }
      return local_e.key;
    }
    
    public Character firstKey()
    {
      Char2ReferenceAVLTreeMap.Entry<V> local_e = firstEntry();
      if (local_e == null) {
        throw new NoSuchElementException();
      }
      return local_e.getKey();
    }
    
    public Character lastKey()
    {
      Char2ReferenceAVLTreeMap.Entry<V> local_e = lastEntry();
      if (local_e == null) {
        throw new NoSuchElementException();
      }
      return local_e.getKey();
    }
    
    private final class SubmapValueIterator
      extends Char2ReferenceAVLTreeMap<V>.Submap.SubmapIterator
      implements ObjectListIterator<V>
    {
      private SubmapValueIterator()
      {
        super();
      }
      
      public V next()
      {
        return nextEntry().value;
      }
      
      public V previous()
      {
        return previousEntry().value;
      }
      
      public void set(V local_v)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(V local_v)
      {
        throw new UnsupportedOperationException();
      }
    }
    
    private final class SubmapKeyIterator
      extends Char2ReferenceAVLTreeMap.Submap.SubmapIterator
      implements CharListIterator
    {
      public SubmapKeyIterator()
      {
        super();
      }
      
      public SubmapKeyIterator(char from)
      {
        super(from);
      }
      
      public char nextChar()
      {
        return nextEntry().key;
      }
      
      public char previousChar()
      {
        return previousEntry().key;
      }
      
      public void set(char local_k)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(char local_k)
      {
        throw new UnsupportedOperationException();
      }
      
      public Character next()
      {
        return Character.valueOf(nextEntry().key);
      }
      
      public Character previous()
      {
        return Character.valueOf(previousEntry().key);
      }
      
      public void set(Character local_ok)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(Character local_ok)
      {
        throw new UnsupportedOperationException();
      }
    }
    
    private class SubmapEntryIterator
      extends Char2ReferenceAVLTreeMap<V>.Submap.SubmapIterator
      implements ObjectListIterator<Char2ReferenceMap.Entry<V>>
    {
      SubmapEntryIterator()
      {
        super();
      }
      
      SubmapEntryIterator(char local_k)
      {
        super(local_k);
      }
      
      public Char2ReferenceMap.Entry<V> next()
      {
        return nextEntry();
      }
      
      public Char2ReferenceMap.Entry<V> previous()
      {
        return previousEntry();
      }
      
      public void set(Char2ReferenceMap.Entry<V> local_ok)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(Char2ReferenceMap.Entry<V> local_ok)
      {
        throw new UnsupportedOperationException();
      }
    }
    
    private class SubmapIterator
      extends Char2ReferenceAVLTreeMap.TreeIterator
    {
      SubmapIterator()
      {
        super();
        this.next = Char2ReferenceAVLTreeMap.Submap.this.firstEntry();
      }
      
      SubmapIterator(char local_k)
      {
        this();
        if (this.next != null) {
          if ((!Char2ReferenceAVLTreeMap.Submap.this.bottom) && (Char2ReferenceAVLTreeMap.this.compare(local_k, this.next.key) < 0))
          {
            this.prev = null;
          }
          else if ((!Char2ReferenceAVLTreeMap.Submap.this.top) && (Char2ReferenceAVLTreeMap.this.compare(local_k, (this.prev = Char2ReferenceAVLTreeMap.Submap.this.lastEntry()).key) >= 0))
          {
            this.next = null;
          }
          else
          {
            this.next = Char2ReferenceAVLTreeMap.this.locateKey(local_k);
            if (Char2ReferenceAVLTreeMap.this.compare(this.next.key, local_k) <= 0)
            {
              this.prev = this.next;
              this.next = this.next.next();
            }
            else
            {
              this.prev = this.next.prev();
            }
          }
        }
      }
      
      void updatePrevious()
      {
        this.prev = this.prev.prev();
        if ((!Char2ReferenceAVLTreeMap.Submap.this.bottom) && (this.prev != null) && (Char2ReferenceAVLTreeMap.this.compare(this.prev.key, Char2ReferenceAVLTreeMap.Submap.this.from) < 0)) {
          this.prev = null;
        }
      }
      
      void updateNext()
      {
        this.next = this.next.next();
        if ((!Char2ReferenceAVLTreeMap.Submap.this.top) && (this.next != null) && (Char2ReferenceAVLTreeMap.this.compare(this.next.key, Char2ReferenceAVLTreeMap.Submap.this.field_47) >= 0)) {
          this.next = null;
        }
      }
    }
    
    private class KeySet
      extends AbstractChar2ReferenceSortedMap.KeySet
    {
      private KeySet()
      {
        super();
      }
      
      public CharBidirectionalIterator iterator()
      {
        return new Char2ReferenceAVLTreeMap.Submap.SubmapKeyIterator(Char2ReferenceAVLTreeMap.Submap.this);
      }
      
      public CharBidirectionalIterator iterator(char from)
      {
        return new Char2ReferenceAVLTreeMap.Submap.SubmapKeyIterator(Char2ReferenceAVLTreeMap.Submap.this, from);
      }
    }
  }
  
  private final class ValueIterator
    extends Char2ReferenceAVLTreeMap<V>.TreeIterator
    implements ObjectListIterator<V>
  {
    private ValueIterator()
    {
      super();
    }
    
    public V next()
    {
      return nextEntry().value;
    }
    
    public V previous()
    {
      return previousEntry().value;
    }
    
    public void set(V local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(V local_v)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class KeySet
    extends AbstractChar2ReferenceSortedMap.KeySet
  {
    private KeySet()
    {
      super();
    }
    
    public CharBidirectionalIterator iterator()
    {
      return new Char2ReferenceAVLTreeMap.KeyIterator(Char2ReferenceAVLTreeMap.this);
    }
    
    public CharBidirectionalIterator iterator(char from)
    {
      return new Char2ReferenceAVLTreeMap.KeyIterator(Char2ReferenceAVLTreeMap.this, from);
    }
  }
  
  private final class KeyIterator
    extends Char2ReferenceAVLTreeMap.TreeIterator
    implements CharListIterator
  {
    public KeyIterator()
    {
      super();
    }
    
    public KeyIterator(char local_k)
    {
      super(local_k);
    }
    
    public char nextChar()
    {
      return nextEntry().key;
    }
    
    public char previousChar()
    {
      return previousEntry().key;
    }
    
    public void set(char local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(char local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public Character next()
    {
      return Character.valueOf(nextEntry().key);
    }
    
    public Character previous()
    {
      return Character.valueOf(previousEntry().key);
    }
    
    public void set(Character local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Character local_ok)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class EntryIterator
    extends Char2ReferenceAVLTreeMap<V>.TreeIterator
    implements ObjectListIterator<Char2ReferenceMap.Entry<V>>
  {
    EntryIterator()
    {
      super();
    }
    
    EntryIterator(char local_k)
    {
      super(local_k);
    }
    
    public Char2ReferenceMap.Entry<V> next()
    {
      return nextEntry();
    }
    
    public Char2ReferenceMap.Entry<V> previous()
    {
      return previousEntry();
    }
    
    public void set(Char2ReferenceMap.Entry<V> local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Char2ReferenceMap.Entry<V> local_ok)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class TreeIterator
  {
    Char2ReferenceAVLTreeMap.Entry<V> prev;
    Char2ReferenceAVLTreeMap.Entry<V> next;
    Char2ReferenceAVLTreeMap.Entry<V> curr;
    int index = 0;
    
    TreeIterator()
    {
      this.next = Char2ReferenceAVLTreeMap.this.firstEntry;
    }
    
    TreeIterator(char local_k)
    {
      if ((this.next = Char2ReferenceAVLTreeMap.this.locateKey(local_k)) != null) {
        if (Char2ReferenceAVLTreeMap.this.compare(this.next.key, local_k) <= 0)
        {
          this.prev = this.next;
          this.next = this.next.next();
        }
        else
        {
          this.prev = this.next.prev();
        }
      }
    }
    
    public boolean hasNext()
    {
      return this.next != null;
    }
    
    public boolean hasPrevious()
    {
      return this.prev != null;
    }
    
    void updateNext()
    {
      this.next = this.next.next();
    }
    
    Char2ReferenceAVLTreeMap.Entry<V> nextEntry()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      this.curr = (this.prev = this.next);
      this.index += 1;
      updateNext();
      return this.curr;
    }
    
    void updatePrevious()
    {
      this.prev = this.prev.prev();
    }
    
    Char2ReferenceAVLTreeMap.Entry<V> previousEntry()
    {
      if (!hasPrevious()) {
        throw new NoSuchElementException();
      }
      this.curr = (this.next = this.prev);
      this.index -= 1;
      updatePrevious();
      return this.curr;
    }
    
    public int nextIndex()
    {
      return this.index;
    }
    
    public int previousIndex()
    {
      return this.index - 1;
    }
    
    public void remove()
    {
      if (this.curr == null) {
        throw new IllegalStateException();
      }
      if (this.curr == this.prev) {
        this.index -= 1;
      }
      this.next = (this.prev = this.curr);
      updatePrevious();
      updateNext();
      Char2ReferenceAVLTreeMap.this.remove(this.curr.key);
      this.curr = null;
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
  
  private static final class Entry<V>
    implements Cloneable, Char2ReferenceMap.Entry<V>
  {
    private static final int SUCC_MASK = -2147483648;
    private static final int PRED_MASK = 1073741824;
    private static final int BALANCE_MASK = 255;
    char key;
    V value;
    Entry<V> left;
    Entry<V> right;
    int info;
    
    Entry() {}
    
    Entry(char local_k, V local_v)
    {
      this.key = local_k;
      this.value = local_v;
      this.info = -1073741824;
    }
    
    Entry<V> left()
    {
      return (this.info & 0x40000000) != 0 ? null : this.left;
    }
    
    Entry<V> right()
    {
      return (this.info & 0x80000000) != 0 ? null : this.right;
    }
    
    boolean pred()
    {
      return (this.info & 0x40000000) != 0;
    }
    
    boolean succ()
    {
      return (this.info & 0x80000000) != 0;
    }
    
    void pred(boolean pred)
    {
      if (pred) {
        this.info |= 1073741824;
      } else {
        this.info &= -1073741825;
      }
    }
    
    void succ(boolean succ)
    {
      if (succ) {
        this.info |= -2147483648;
      } else {
        this.info &= 2147483647;
      }
    }
    
    void pred(Entry<V> pred)
    {
      this.info |= 1073741824;
      this.left = pred;
    }
    
    void succ(Entry<V> succ)
    {
      this.info |= -2147483648;
      this.right = succ;
    }
    
    void left(Entry<V> left)
    {
      this.info &= -1073741825;
      this.left = left;
    }
    
    void right(Entry<V> right)
    {
      this.info &= 2147483647;
      this.right = right;
    }
    
    int balance()
    {
      return (byte)this.info;
    }
    
    void balance(int level)
    {
      this.info &= -256;
      this.info |= level & 0xFF;
    }
    
    void incBalance()
    {
      this.info = (this.info & 0xFFFFFF00 | (byte)this.info + 1 & 0xFF);
    }
    
    protected void decBalance()
    {
      this.info = (this.info & 0xFFFFFF00 | (byte)this.info - 1 & 0xFF);
    }
    
    Entry<V> next()
    {
      Entry<V> next = this.right;
      if ((this.info & 0x80000000) == 0) {
        while ((next.info & 0x40000000) == 0) {
          next = next.left;
        }
      }
      return next;
    }
    
    Entry<V> prev()
    {
      Entry<V> prev = this.left;
      if ((this.info & 0x40000000) == 0) {
        while ((prev.info & 0x80000000) == 0) {
          prev = prev.right;
        }
      }
      return prev;
    }
    
    public Character getKey()
    {
      return Character.valueOf(this.key);
    }
    
    public char getCharKey()
    {
      return this.key;
    }
    
    public V getValue()
    {
      return this.value;
    }
    
    public V setValue(V value)
    {
      V oldValue = this.value;
      this.value = value;
      return oldValue;
    }
    
    public Entry<V> clone()
    {
      Entry<V> local_c;
      try
      {
        local_c = (Entry)super.clone();
      }
      catch (CloneNotSupportedException cantHappen)
      {
        throw new InternalError();
      }
      local_c.key = this.key;
      local_c.value = this.value;
      local_c.info = this.info;
      return local_c;
    }
    
    public boolean equals(Object local_o)
    {
      if (!(local_o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<Character, V> local_e = (Map.Entry)local_o;
      return (this.key == ((Character)local_e.getKey()).charValue()) && (this.value == local_e.getValue());
    }
    
    public int hashCode()
    {
      return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
    }
    
    public String toString()
    {
      return this.key + "=>" + this.value;
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.chars.Char2ReferenceAVLTreeMap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */