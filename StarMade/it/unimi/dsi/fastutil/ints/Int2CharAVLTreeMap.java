package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
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

public class Int2CharAVLTreeMap
  extends AbstractInt2CharSortedMap
  implements Serializable, Cloneable
{
  protected transient Entry tree;
  protected int count;
  protected transient Entry firstEntry;
  protected transient Entry lastEntry;
  protected volatile transient ObjectSortedSet<Int2CharMap.Entry> entries;
  protected volatile transient IntSortedSet keys;
  protected volatile transient CharCollection values;
  protected transient boolean modified;
  protected Comparator<? super Integer> storedComparator;
  protected transient IntComparator actualComparator;
  public static final long serialVersionUID = -7046029254386353129L;
  private static final boolean ASSERTS = false;
  private transient boolean[] dirPath;
  
  public Int2CharAVLTreeMap()
  {
    allocatePaths();
    this.tree = null;
    this.count = 0;
  }
  
  private void setActualComparator()
  {
    if ((this.storedComparator == null) || ((this.storedComparator instanceof IntComparator))) {
      this.actualComparator = ((IntComparator)this.storedComparator);
    } else {
      this.actualComparator = new IntComparator()
      {
        public int compare(int local_k1, int local_k2)
        {
          return Int2CharAVLTreeMap.this.storedComparator.compare(Integer.valueOf(local_k1), Integer.valueOf(local_k2));
        }
        
        public int compare(Integer ok1, Integer ok2)
        {
          return Int2CharAVLTreeMap.this.storedComparator.compare(ok1, ok2);
        }
      };
    }
  }
  
  public Int2CharAVLTreeMap(Comparator<? super Integer> local_c)
  {
    this();
    this.storedComparator = local_c;
    setActualComparator();
  }
  
  public Int2CharAVLTreeMap(Map<? extends Integer, ? extends Character> local_m)
  {
    this();
    putAll(local_m);
  }
  
  public Int2CharAVLTreeMap(SortedMap<Integer, Character> local_m)
  {
    this(local_m.comparator());
    putAll(local_m);
  }
  
  public Int2CharAVLTreeMap(Int2CharMap local_m)
  {
    this();
    putAll(local_m);
  }
  
  public Int2CharAVLTreeMap(Int2CharSortedMap local_m)
  {
    this(local_m.comparator());
    putAll(local_m);
  }
  
  public Int2CharAVLTreeMap(int[] local_k, char[] local_v, Comparator<? super Integer> local_c)
  {
    this(local_c);
    if (local_k.length != local_v.length) {
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + local_k.length + " and " + local_v.length + ")");
    }
    for (int local_i = 0; local_i < local_k.length; local_i++) {
      put(local_k[local_i], local_v[local_i]);
    }
  }
  
  public Int2CharAVLTreeMap(int[] local_k, char[] local_v)
  {
    this(local_k, local_v, null);
  }
  
  final int compare(int local_k1, int local_k2)
  {
    return this.actualComparator == null ? 1 : local_k1 == local_k2 ? 0 : local_k1 < local_k2 ? -1 : this.actualComparator.compare(local_k1, local_k2);
  }
  
  final Entry findKey(int local_k)
  {
    int cmp;
    for (Entry local_e = this.tree; (local_e != null) && ((cmp = compare(local_k, local_e.key)) != 0); local_e = cmp < 0 ? local_e.left() : local_e.right()) {}
    return local_e;
  }
  
  final Entry locateKey(int local_k)
  {
    Entry local_e = this.tree;
    Entry last = this.tree;
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
  
  public char put(int local_k, char local_v)
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
      Entry local_p = this.tree;
      Entry local_q = null;
      Entry local_y = this.tree;
      Entry local_z = null;
      Entry local_e = null;
      Entry local_w = null;
      int local_i = 0;
      for (;;)
      {
        int cmp;
        if ((cmp = compare(local_k, local_p.key)) == 0)
        {
          char oldValue = local_p.value;
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
        Entry oldValue = local_y.left;
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
        Entry oldValue = local_y.right;
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
  
  private Entry parent(Entry local_e)
  {
    if (local_e == this.tree) {
      return null;
    }
    Entry local_y;
    Entry local_x = local_y = local_e;
    for (;;)
    {
      if (local_y.succ())
      {
        Entry local_p = local_y.right;
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
        Entry local_p = local_x.left;
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
  
  public char remove(int local_k)
  {
    this.modified = false;
    if (this.tree == null) {
      return this.defRetValue;
    }
    Entry local_p = this.tree;
    Entry local_q = null;
    boolean dir = false;
    int local_kk = local_k;
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
      Entry local_r = local_p.right;
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
        Entry local_s;
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
      Entry local_r = local_q;
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
          Entry local_s = local_r.right;
          if (local_s.balance() == -1)
          {
            Entry local_w = local_s.left;
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
          Entry local_s = local_r.left;
          if (local_s.balance() == 1)
          {
            Entry local_w = local_s.right;
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
  
  public Character put(Integer local_ok, Character local_ov)
  {
    char oldValue = put(local_ok.intValue(), local_ov.charValue());
    return this.modified ? null : Character.valueOf(oldValue);
  }
  
  public Character remove(Object local_ok)
  {
    char oldValue = remove(((Integer)local_ok).intValue());
    return this.modified ? Character.valueOf(oldValue) : null;
  }
  
  public boolean containsValue(char local_v)
  {
    ValueIterator local_i = new ValueIterator(null);
    int local_j = this.count;
    while (local_j-- != 0)
    {
      char local_ev = local_i.nextChar();
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
  
  public boolean containsKey(int local_k)
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
  
  public char get(int local_k)
  {
    Entry local_e = findKey(local_k);
    return local_e == null ? this.defRetValue : local_e.value;
  }
  
  public int firstIntKey()
  {
    if (this.tree == null) {
      throw new NoSuchElementException();
    }
    return this.firstEntry.key;
  }
  
  public int lastIntKey()
  {
    if (this.tree == null) {
      throw new NoSuchElementException();
    }
    return this.lastEntry.key;
  }
  
  public ObjectSortedSet<Int2CharMap.Entry> int2CharEntrySet()
  {
    if (this.entries == null) {
      this.entries = new AbstractObjectSortedSet()
      {
        final Comparator<? super Int2CharMap.Entry> comparator = new Comparator()
        {
          public int compare(Int2CharMap.Entry local_x, Int2CharMap.Entry local_y)
          {
            return Int2CharAVLTreeMap.this.storedComparator.compare(local_x.getKey(), local_y.getKey());
          }
        };
        
        public Comparator<? super Int2CharMap.Entry> comparator()
        {
          return this.comparator;
        }
        
        public ObjectBidirectionalIterator<Int2CharMap.Entry> iterator()
        {
          return new Int2CharAVLTreeMap.EntryIterator(Int2CharAVLTreeMap.this);
        }
        
        public ObjectBidirectionalIterator<Int2CharMap.Entry> iterator(Int2CharMap.Entry from)
        {
          return new Int2CharAVLTreeMap.EntryIterator(Int2CharAVLTreeMap.this, ((Integer)from.getKey()).intValue());
        }
        
        public boolean contains(Object local_o)
        {
          if (!(local_o instanceof Map.Entry)) {
            return false;
          }
          Map.Entry<Integer, Character> local_e = (Map.Entry)local_o;
          Int2CharAVLTreeMap.Entry local_f = Int2CharAVLTreeMap.this.findKey(((Integer)local_e.getKey()).intValue());
          return local_e.equals(local_f);
        }
        
        public boolean remove(Object local_o)
        {
          if (!(local_o instanceof Map.Entry)) {
            return false;
          }
          Map.Entry<Integer, Character> local_e = (Map.Entry)local_o;
          Int2CharAVLTreeMap.Entry local_f = Int2CharAVLTreeMap.this.findKey(((Integer)local_e.getKey()).intValue());
          if (local_f != null) {
            Int2CharAVLTreeMap.this.remove(local_f.key);
          }
          return local_f != null;
        }
        
        public int size()
        {
          return Int2CharAVLTreeMap.this.count;
        }
        
        public void clear()
        {
          Int2CharAVLTreeMap.this.clear();
        }
        
        public Int2CharMap.Entry first()
        {
          return Int2CharAVLTreeMap.this.firstEntry;
        }
        
        public Int2CharMap.Entry last()
        {
          return Int2CharAVLTreeMap.this.lastEntry;
        }
        
        public ObjectSortedSet<Int2CharMap.Entry> subSet(Int2CharMap.Entry from, Int2CharMap.Entry local_to)
        {
          return Int2CharAVLTreeMap.this.subMap((Integer)from.getKey(), (Integer)local_to.getKey()).int2CharEntrySet();
        }
        
        public ObjectSortedSet<Int2CharMap.Entry> headSet(Int2CharMap.Entry local_to)
        {
          return Int2CharAVLTreeMap.this.headMap((Integer)local_to.getKey()).int2CharEntrySet();
        }
        
        public ObjectSortedSet<Int2CharMap.Entry> tailSet(Int2CharMap.Entry from)
        {
          return Int2CharAVLTreeMap.this.tailMap((Integer)from.getKey()).int2CharEntrySet();
        }
      };
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
  
  public CharCollection values()
  {
    if (this.values == null) {
      this.values = new AbstractCharCollection()
      {
        public CharIterator iterator()
        {
          return new Int2CharAVLTreeMap.ValueIterator(Int2CharAVLTreeMap.this, null);
        }
        
        public boolean contains(char local_k)
        {
          return Int2CharAVLTreeMap.this.containsValue(local_k);
        }
        
        public int size()
        {
          return Int2CharAVLTreeMap.this.count;
        }
        
        public void clear()
        {
          Int2CharAVLTreeMap.this.clear();
        }
      };
    }
    return this.values;
  }
  
  public IntComparator comparator()
  {
    return this.actualComparator;
  }
  
  public Int2CharSortedMap headMap(int local_to)
  {
    return new Submap(0, true, local_to, false);
  }
  
  public Int2CharSortedMap tailMap(int from)
  {
    return new Submap(from, false, 0, true);
  }
  
  public Int2CharSortedMap subMap(int from, int local_to)
  {
    return new Submap(from, false, local_to, false);
  }
  
  public Int2CharAVLTreeMap clone()
  {
    Int2CharAVLTreeMap local_c;
    try
    {
      local_c = (Int2CharAVLTreeMap)super.clone();
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
      Entry local_rp = new Entry();
      Entry local_rq = new Entry();
      Entry local_p = local_rp;
      local_rp.left(this.tree);
      Entry local_q = local_rq;
      local_rq.pred(null);
      for (;;)
      {
        if (!local_p.pred())
        {
          Entry cantHappen = local_p.left.clone();
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
          Entry cantHappen = local_p.right.clone();
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
    EntryIterator local_i = new EntryIterator();
    local_s.defaultWriteObject();
    while (local_n-- != 0)
    {
      Entry local_e = local_i.nextEntry();
      local_s.writeInt(local_e.key);
      local_s.writeChar(local_e.value);
    }
  }
  
  private Entry readTree(ObjectInputStream local_s, int local_n, Entry pred, Entry succ)
    throws IOException, ClassNotFoundException
  {
    if (local_n == 1)
    {
      Entry top = new Entry(local_s.readInt(), local_s.readChar());
      top.pred(pred);
      top.succ(succ);
      return top;
    }
    if (local_n == 2)
    {
      Entry top = new Entry(local_s.readInt(), local_s.readChar());
      top.right(new Entry(local_s.readInt(), local_s.readChar()));
      top.right.pred(top);
      top.balance(1);
      top.pred(pred);
      top.right.succ(succ);
      return top;
    }
    int top = local_n / 2;
    int leftN = local_n - top - 1;
    Entry top = new Entry();
    top.left(readTree(local_s, leftN, pred, top));
    top.key = local_s.readInt();
    top.value = local_s.readChar();
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
      for (Entry local_e = this.tree; local_e.left() != null; local_e = local_e.left()) {}
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
    extends AbstractInt2CharSortedMap
    implements Serializable
  {
    public static final long serialVersionUID = -7046029254386353129L;
    int from;
    int field_47;
    boolean bottom;
    boolean top;
    protected volatile transient ObjectSortedSet<Int2CharMap.Entry> entries;
    protected volatile transient IntSortedSet keys;
    protected volatile transient CharCollection values;
    
    public Submap(int from, boolean bottom, int local_to, boolean top)
    {
      if ((!bottom) && (!top) && (Int2CharAVLTreeMap.this.compare(from, local_to) > 0)) {
        throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + local_to + ")");
      }
      this.from = from;
      this.bottom = bottom;
      this.field_47 = local_to;
      this.top = top;
      this.defRetValue = Int2CharAVLTreeMap.this.defRetValue;
    }
    
    public void clear()
    {
      SubmapIterator local_i = new SubmapIterator();
      while (local_i.hasNext())
      {
        local_i.nextEntry();
        local_i.remove();
      }
    }
    
    final boolean in4(int local_k)
    {
      return ((this.bottom) || (Int2CharAVLTreeMap.this.compare(local_k, this.from) >= 0)) && ((this.top) || (Int2CharAVLTreeMap.this.compare(local_k, this.field_47) < 0));
    }
    
    public ObjectSortedSet<Int2CharMap.Entry> int2CharEntrySet()
    {
      if (this.entries == null) {
        this.entries = new AbstractObjectSortedSet()
        {
          public ObjectBidirectionalIterator<Int2CharMap.Entry> iterator()
          {
            return new Int2CharAVLTreeMap.Submap.SubmapEntryIterator(Int2CharAVLTreeMap.Submap.this);
          }
          
          public ObjectBidirectionalIterator<Int2CharMap.Entry> iterator(Int2CharMap.Entry from)
          {
            return new Int2CharAVLTreeMap.Submap.SubmapEntryIterator(Int2CharAVLTreeMap.Submap.this, ((Integer)from.getKey()).intValue());
          }
          
          public Comparator<? super Int2CharMap.Entry> comparator()
          {
            return Int2CharAVLTreeMap.this.entrySet().comparator();
          }
          
          public boolean contains(Object local_o)
          {
            if (!(local_o instanceof Map.Entry)) {
              return false;
            }
            Map.Entry<Integer, Character> local_e = (Map.Entry)local_o;
            Int2CharAVLTreeMap.Entry local_f = Int2CharAVLTreeMap.this.findKey(((Integer)local_e.getKey()).intValue());
            return (local_f != null) && (Int2CharAVLTreeMap.Submap.this.in4(local_f.key)) && (local_e.equals(local_f));
          }
          
          public boolean remove(Object local_o)
          {
            if (!(local_o instanceof Map.Entry)) {
              return false;
            }
            Map.Entry<Integer, Character> local_e = (Map.Entry)local_o;
            Int2CharAVLTreeMap.Entry local_f = Int2CharAVLTreeMap.this.findKey(((Integer)local_e.getKey()).intValue());
            if ((local_f != null) && (Int2CharAVLTreeMap.Submap.this.in4(local_f.key))) {
              Int2CharAVLTreeMap.Submap.this.remove(local_f.key);
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
            return !new Int2CharAVLTreeMap.Submap.SubmapIterator(Int2CharAVLTreeMap.Submap.this).hasNext();
          }
          
          public void clear()
          {
            Int2CharAVLTreeMap.Submap.this.clear();
          }
          
          public Int2CharMap.Entry first()
          {
            return Int2CharAVLTreeMap.Submap.this.firstEntry();
          }
          
          public Int2CharMap.Entry last()
          {
            return Int2CharAVLTreeMap.Submap.this.lastEntry();
          }
          
          public ObjectSortedSet<Int2CharMap.Entry> subSet(Int2CharMap.Entry from, Int2CharMap.Entry local_to)
          {
            return Int2CharAVLTreeMap.Submap.this.subMap((Integer)from.getKey(), (Integer)local_to.getKey()).int2CharEntrySet();
          }
          
          public ObjectSortedSet<Int2CharMap.Entry> headSet(Int2CharMap.Entry local_to)
          {
            return Int2CharAVLTreeMap.Submap.this.headMap((Integer)local_to.getKey()).int2CharEntrySet();
          }
          
          public ObjectSortedSet<Int2CharMap.Entry> tailSet(Int2CharMap.Entry from)
          {
            return Int2CharAVLTreeMap.Submap.this.tailMap((Integer)from.getKey()).int2CharEntrySet();
          }
        };
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
    
    public CharCollection values()
    {
      if (this.values == null) {
        this.values = new AbstractCharCollection()
        {
          public CharIterator iterator()
          {
            return new Int2CharAVLTreeMap.Submap.SubmapValueIterator(Int2CharAVLTreeMap.Submap.this, null);
          }
          
          public boolean contains(char local_k)
          {
            return Int2CharAVLTreeMap.Submap.this.containsValue(local_k);
          }
          
          public int size()
          {
            return Int2CharAVLTreeMap.Submap.this.size();
          }
          
          public void clear()
          {
            Int2CharAVLTreeMap.Submap.this.clear();
          }
        };
      }
      return this.values;
    }
    
    public boolean containsKey(int local_k)
    {
      return (in4(local_k)) && (Int2CharAVLTreeMap.this.containsKey(local_k));
    }
    
    public boolean containsValue(char local_v)
    {
      SubmapIterator local_i = new SubmapIterator();
      while (local_i.hasNext())
      {
        char local_ev = local_i.nextEntry().value;
        if (local_ev == local_v) {
          return true;
        }
      }
      return false;
    }
    
    public char get(int local_k)
    {
      int local_kk = local_k;
      Int2CharAVLTreeMap.Entry local_e;
      return (in4(local_kk)) && ((local_e = Int2CharAVLTreeMap.this.findKey(local_kk)) != null) ? local_e.value : this.defRetValue;
    }
    
    public char put(int local_k, char local_v)
    {
      Int2CharAVLTreeMap.this.modified = false;
      if (!in4(local_k)) {
        throw new IllegalArgumentException("Key (" + local_k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.field_47)) + ")");
      }
      char oldValue = Int2CharAVLTreeMap.this.put(local_k, local_v);
      return Int2CharAVLTreeMap.this.modified ? this.defRetValue : oldValue;
    }
    
    public Character put(Integer local_ok, Character local_ov)
    {
      char oldValue = put(local_ok.intValue(), local_ov.charValue());
      return Int2CharAVLTreeMap.this.modified ? null : Character.valueOf(oldValue);
    }
    
    public char remove(int local_k)
    {
      Int2CharAVLTreeMap.this.modified = false;
      if (!in4(local_k)) {
        return this.defRetValue;
      }
      char oldValue = Int2CharAVLTreeMap.this.remove(local_k);
      return Int2CharAVLTreeMap.this.modified ? oldValue : this.defRetValue;
    }
    
    public Character remove(Object local_ok)
    {
      char oldValue = remove(((Integer)local_ok).intValue());
      return Int2CharAVLTreeMap.this.modified ? Character.valueOf(oldValue) : null;
    }
    
    public int size()
    {
      SubmapIterator local_i = new SubmapIterator();
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
    
    public IntComparator comparator()
    {
      return Int2CharAVLTreeMap.this.actualComparator;
    }
    
    public Int2CharSortedMap headMap(int local_to)
    {
      if (this.top) {
        return new Submap(Int2CharAVLTreeMap.this, this.from, this.bottom, local_to, false);
      }
      return Int2CharAVLTreeMap.this.compare(local_to, this.field_47) < 0 ? new Submap(Int2CharAVLTreeMap.this, this.from, this.bottom, local_to, false) : this;
    }
    
    public Int2CharSortedMap tailMap(int from)
    {
      if (this.bottom) {
        return new Submap(Int2CharAVLTreeMap.this, from, false, this.field_47, this.top);
      }
      return Int2CharAVLTreeMap.this.compare(from, this.from) > 0 ? new Submap(Int2CharAVLTreeMap.this, from, false, this.field_47, this.top) : this;
    }
    
    public Int2CharSortedMap subMap(int from, int local_to)
    {
      if ((this.top) && (this.bottom)) {
        return new Submap(Int2CharAVLTreeMap.this, from, false, local_to, false);
      }
      if (!this.top) {
        local_to = Int2CharAVLTreeMap.this.compare(local_to, this.field_47) < 0 ? local_to : this.field_47;
      }
      if (!this.bottom) {
        from = Int2CharAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
      }
      if ((!this.top) && (!this.bottom) && (from == this.from) && (local_to == this.field_47)) {
        return this;
      }
      return new Submap(Int2CharAVLTreeMap.this, from, false, local_to, false);
    }
    
    public Int2CharAVLTreeMap.Entry firstEntry()
    {
      if (Int2CharAVLTreeMap.this.tree == null) {
        return null;
      }
      Int2CharAVLTreeMap.Entry local_e;
      Int2CharAVLTreeMap.Entry local_e;
      if (this.bottom)
      {
        local_e = Int2CharAVLTreeMap.this.firstEntry;
      }
      else
      {
        local_e = Int2CharAVLTreeMap.this.locateKey(this.from);
        if (Int2CharAVLTreeMap.this.compare(local_e.key, this.from) < 0) {
          local_e = local_e.next();
        }
      }
      if ((local_e == null) || ((!this.top) && (Int2CharAVLTreeMap.this.compare(local_e.key, this.field_47) >= 0))) {
        return null;
      }
      return local_e;
    }
    
    public Int2CharAVLTreeMap.Entry lastEntry()
    {
      if (Int2CharAVLTreeMap.this.tree == null) {
        return null;
      }
      Int2CharAVLTreeMap.Entry local_e;
      Int2CharAVLTreeMap.Entry local_e;
      if (this.top)
      {
        local_e = Int2CharAVLTreeMap.this.lastEntry;
      }
      else
      {
        local_e = Int2CharAVLTreeMap.this.locateKey(this.field_47);
        if (Int2CharAVLTreeMap.this.compare(local_e.key, this.field_47) >= 0) {
          local_e = local_e.prev();
        }
      }
      if ((local_e == null) || ((!this.bottom) && (Int2CharAVLTreeMap.this.compare(local_e.key, this.from) < 0))) {
        return null;
      }
      return local_e;
    }
    
    public int firstIntKey()
    {
      Int2CharAVLTreeMap.Entry local_e = firstEntry();
      if (local_e == null) {
        throw new NoSuchElementException();
      }
      return local_e.key;
    }
    
    public int lastIntKey()
    {
      Int2CharAVLTreeMap.Entry local_e = lastEntry();
      if (local_e == null) {
        throw new NoSuchElementException();
      }
      return local_e.key;
    }
    
    public Integer firstKey()
    {
      Int2CharAVLTreeMap.Entry local_e = firstEntry();
      if (local_e == null) {
        throw new NoSuchElementException();
      }
      return local_e.getKey();
    }
    
    public Integer lastKey()
    {
      Int2CharAVLTreeMap.Entry local_e = lastEntry();
      if (local_e == null) {
        throw new NoSuchElementException();
      }
      return local_e.getKey();
    }
    
    private final class SubmapValueIterator
      extends Int2CharAVLTreeMap.Submap.SubmapIterator
      implements CharListIterator
    {
      private SubmapValueIterator()
      {
        super();
      }
      
      public char nextChar()
      {
        return nextEntry().value;
      }
      
      public char previousChar()
      {
        return previousEntry().value;
      }
      
      public void set(char local_v)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(char local_v)
      {
        throw new UnsupportedOperationException();
      }
      
      public Character next()
      {
        return Character.valueOf(nextEntry().value);
      }
      
      public Character previous()
      {
        return Character.valueOf(previousEntry().value);
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
    
    private final class SubmapKeyIterator
      extends Int2CharAVLTreeMap.Submap.SubmapIterator
      implements IntListIterator
    {
      public SubmapKeyIterator()
      {
        super();
      }
      
      public SubmapKeyIterator(int from)
      {
        super(from);
      }
      
      public int nextInt()
      {
        return nextEntry().key;
      }
      
      public int previousInt()
      {
        return previousEntry().key;
      }
      
      public void set(int local_k)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(int local_k)
      {
        throw new UnsupportedOperationException();
      }
      
      public Integer next()
      {
        return Integer.valueOf(nextEntry().key);
      }
      
      public Integer previous()
      {
        return Integer.valueOf(previousEntry().key);
      }
      
      public void set(Integer local_ok)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(Integer local_ok)
      {
        throw new UnsupportedOperationException();
      }
    }
    
    private class SubmapEntryIterator
      extends Int2CharAVLTreeMap.Submap.SubmapIterator
      implements ObjectListIterator<Int2CharMap.Entry>
    {
      SubmapEntryIterator()
      {
        super();
      }
      
      SubmapEntryIterator(int local_k)
      {
        super(local_k);
      }
      
      public Int2CharMap.Entry next()
      {
        return nextEntry();
      }
      
      public Int2CharMap.Entry previous()
      {
        return previousEntry();
      }
      
      public void set(Int2CharMap.Entry local_ok)
      {
        throw new UnsupportedOperationException();
      }
      
      public void add(Int2CharMap.Entry local_ok)
      {
        throw new UnsupportedOperationException();
      }
    }
    
    private class SubmapIterator
      extends Int2CharAVLTreeMap.TreeIterator
    {
      SubmapIterator()
      {
        super();
        this.next = Int2CharAVLTreeMap.Submap.this.firstEntry();
      }
      
      SubmapIterator(int local_k)
      {
        this();
        if (this.next != null) {
          if ((!Int2CharAVLTreeMap.Submap.this.bottom) && (Int2CharAVLTreeMap.this.compare(local_k, this.next.key) < 0))
          {
            this.prev = null;
          }
          else if ((!Int2CharAVLTreeMap.Submap.this.top) && (Int2CharAVLTreeMap.this.compare(local_k, (this.prev = Int2CharAVLTreeMap.Submap.this.lastEntry()).key) >= 0))
          {
            this.next = null;
          }
          else
          {
            this.next = Int2CharAVLTreeMap.this.locateKey(local_k);
            if (Int2CharAVLTreeMap.this.compare(this.next.key, local_k) <= 0)
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
        if ((!Int2CharAVLTreeMap.Submap.this.bottom) && (this.prev != null) && (Int2CharAVLTreeMap.this.compare(this.prev.key, Int2CharAVLTreeMap.Submap.this.from) < 0)) {
          this.prev = null;
        }
      }
      
      void updateNext()
      {
        this.next = this.next.next();
        if ((!Int2CharAVLTreeMap.Submap.this.top) && (this.next != null) && (Int2CharAVLTreeMap.this.compare(this.next.key, Int2CharAVLTreeMap.Submap.this.field_47) >= 0)) {
          this.next = null;
        }
      }
    }
    
    private class KeySet
      extends AbstractInt2CharSortedMap.KeySet
    {
      private KeySet()
      {
        super();
      }
      
      public IntBidirectionalIterator iterator()
      {
        return new Int2CharAVLTreeMap.Submap.SubmapKeyIterator(Int2CharAVLTreeMap.Submap.this);
      }
      
      public IntBidirectionalIterator iterator(int from)
      {
        return new Int2CharAVLTreeMap.Submap.SubmapKeyIterator(Int2CharAVLTreeMap.Submap.this, from);
      }
    }
  }
  
  private final class ValueIterator
    extends Int2CharAVLTreeMap.TreeIterator
    implements CharListIterator
  {
    private ValueIterator()
    {
      super();
    }
    
    public char nextChar()
    {
      return nextEntry().value;
    }
    
    public char previousChar()
    {
      return previousEntry().value;
    }
    
    public void set(char local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(char local_v)
    {
      throw new UnsupportedOperationException();
    }
    
    public Character next()
    {
      return Character.valueOf(nextEntry().value);
    }
    
    public Character previous()
    {
      return Character.valueOf(previousEntry().value);
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
  
  private class KeySet
    extends AbstractInt2CharSortedMap.KeySet
  {
    private KeySet()
    {
      super();
    }
    
    public IntBidirectionalIterator iterator()
    {
      return new Int2CharAVLTreeMap.KeyIterator(Int2CharAVLTreeMap.this);
    }
    
    public IntBidirectionalIterator iterator(int from)
    {
      return new Int2CharAVLTreeMap.KeyIterator(Int2CharAVLTreeMap.this, from);
    }
  }
  
  private final class KeyIterator
    extends Int2CharAVLTreeMap.TreeIterator
    implements IntListIterator
  {
    public KeyIterator()
    {
      super();
    }
    
    public KeyIterator(int local_k)
    {
      super(local_k);
    }
    
    public int nextInt()
    {
      return nextEntry().key;
    }
    
    public int previousInt()
    {
      return previousEntry().key;
    }
    
    public void set(int local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(int local_k)
    {
      throw new UnsupportedOperationException();
    }
    
    public Integer next()
    {
      return Integer.valueOf(nextEntry().key);
    }
    
    public Integer previous()
    {
      return Integer.valueOf(previousEntry().key);
    }
    
    public void set(Integer local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Integer local_ok)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class EntryIterator
    extends Int2CharAVLTreeMap.TreeIterator
    implements ObjectListIterator<Int2CharMap.Entry>
  {
    EntryIterator()
    {
      super();
    }
    
    EntryIterator(int local_k)
    {
      super(local_k);
    }
    
    public Int2CharMap.Entry next()
    {
      return nextEntry();
    }
    
    public Int2CharMap.Entry previous()
    {
      return previousEntry();
    }
    
    public void set(Int2CharMap.Entry local_ok)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(Int2CharMap.Entry local_ok)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private class TreeIterator
  {
    Int2CharAVLTreeMap.Entry prev;
    Int2CharAVLTreeMap.Entry next;
    Int2CharAVLTreeMap.Entry curr;
    int index = 0;
    
    TreeIterator()
    {
      this.next = Int2CharAVLTreeMap.this.firstEntry;
    }
    
    TreeIterator(int local_k)
    {
      if ((this.next = Int2CharAVLTreeMap.this.locateKey(local_k)) != null) {
        if (Int2CharAVLTreeMap.this.compare(this.next.key, local_k) <= 0)
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
    
    Int2CharAVLTreeMap.Entry nextEntry()
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
    
    Int2CharAVLTreeMap.Entry previousEntry()
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
      Int2CharAVLTreeMap.this.remove(this.curr.key);
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
  
  private static final class Entry
    implements Cloneable, Int2CharMap.Entry
  {
    private static final int SUCC_MASK = -2147483648;
    private static final int PRED_MASK = 1073741824;
    private static final int BALANCE_MASK = 255;
    int key;
    char value;
    Entry left;
    Entry right;
    int info;
    
    Entry() {}
    
    Entry(int local_k, char local_v)
    {
      this.key = local_k;
      this.value = local_v;
      this.info = -1073741824;
    }
    
    Entry left()
    {
      return (this.info & 0x40000000) != 0 ? null : this.left;
    }
    
    Entry right()
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
    
    void pred(Entry pred)
    {
      this.info |= 1073741824;
      this.left = pred;
    }
    
    void succ(Entry succ)
    {
      this.info |= -2147483648;
      this.right = succ;
    }
    
    void left(Entry left)
    {
      this.info &= -1073741825;
      this.left = left;
    }
    
    void right(Entry right)
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
    
    Entry next()
    {
      Entry next = this.right;
      if ((this.info & 0x80000000) == 0) {
        while ((next.info & 0x40000000) == 0) {
          next = next.left;
        }
      }
      return next;
    }
    
    Entry prev()
    {
      Entry prev = this.left;
      if ((this.info & 0x40000000) == 0) {
        while ((prev.info & 0x80000000) == 0) {
          prev = prev.right;
        }
      }
      return prev;
    }
    
    public Integer getKey()
    {
      return Integer.valueOf(this.key);
    }
    
    public int getIntKey()
    {
      return this.key;
    }
    
    public Character getValue()
    {
      return Character.valueOf(this.value);
    }
    
    public char getCharValue()
    {
      return this.value;
    }
    
    public char setValue(char value)
    {
      char oldValue = this.value;
      this.value = value;
      return oldValue;
    }
    
    public Character setValue(Character value)
    {
      return Character.valueOf(setValue(value.charValue()));
    }
    
    public Entry clone()
    {
      Entry local_c;
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
      Map.Entry<Integer, Character> local_e = (Map.Entry)local_o;
      return (this.key == ((Integer)local_e.getKey()).intValue()) && (this.value == ((Character)local_e.getValue()).charValue());
    }
    
    public int hashCode()
    {
      return this.key ^ this.value;
    }
    
    public String toString()
    {
      return this.key + "=>" + this.value;
    }
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     it.unimi.dsi.fastutil.ints.Int2CharAVLTreeMap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */