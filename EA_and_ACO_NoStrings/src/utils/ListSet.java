package utils;

/*
GNU LESSER GENERAL PUBLIC LICENSE
Copyright (C) 2006 The Lobo Project

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

Contact info: lobochief@users.sourceforge.net
 */
/*
 * Created on Sep 3, 2005
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class ListSet<T> implements List<T>, Set<T>, Cloneable {

    private ArrayList<T> list = new ArrayList<T>();
    private HashSet<T> set = new HashSet<T>();

    public ListSet() {
        super();
    }

    public ListSet(Collection<T> collection) {
        super();
        list = new ArrayList<T>(collection);
        set = new HashSet<T>(collection);
    }

    public ListSet(int size) {
        super();
        list = new ArrayList<>(size);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#add(int, E)
     */
    public void add(int index, T element) {
        if (this.set.add(element)) {
            list.add(index, element);
        }
    }

    /*
     *
     * @see java.util.List#add(E)
     */
    public boolean add(T o) {
        if (this.set.add(o)) {
            return this.list.add(o);
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        Iterator<? extends T> i = c.iterator();
        while (i.hasNext()) {
            T element = i.next();
            if (this.add(element)) {
                changed = true;
            }
        }
        return changed;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean changed = false;
        int insertIndex = index;
        Iterator<? extends T> i = c.iterator();
        while (i.hasNext()) {
            T element = i.next();
            if (this.set.add(element)) {
                this.list.add(insertIndex++, element);
                changed = true;
            }
        }
        return changed;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#clear()
     */
    public void clear() {
        this.set.clear();
        this.list.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return this.set.contains(o);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        return this.set.containsAll(c);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#get(int)
     */
    public T get(int index) {
        return this.list.get(index);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#isEmpty()
     */
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#iterator()
     */
    public Iterator<T> iterator() {
        return this.list.iterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object o) {
        return this.list.lastIndexOf(o);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#listIterator()
     */
    public ListIterator<T> listIterator() {
        return this.list.listIterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#listIterator(int)
     */
    public ListIterator<T> listIterator(int index) {
        return this.list.listIterator(index);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#remove(int)
     */
    public T remove(int index) {
        T element = this.list.remove(index);
        if (element != null) {
            this.set.remove(element);
        }
        return element;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        if (this.set.remove(o)) {
            this.list.remove(o);
            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) {
        if (this.set.removeAll(c)) {
            this.list.removeAll(c);
            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        if (this.set.retainAll(c)) {
            this.list.retainAll(c);
            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#set(int, E)
     */
    public T set(int index, T element) {
        //remove old element from set
        this.set.remove(this.list.get(index));
        this.set.add(element);
        return this.list.set(index, element);
    }

    public T addAt(int index, T element) {
        for (int i = this.list.size(); i < index + 1; i++) {
            this.list.add(null);
        }
        return this.set(index, element);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#size()
     */
    public int size() {
        return this.list.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#subList(int, int)
     */
    public List<T> subList(int fromIndex, int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#toArray()
     */
    public Object[] toArray() {
        return this.list.toArray();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.List#toArray(T[])
     */
    @Override
    public <E> E[] toArray(E[] a) {
        return this.list.toArray(a);
    }

    public boolean equals(Object other) {
        return other instanceof ListSet<?>
                && this.list.equals(((ListSet<T>) other).list);
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ListSet<T> list = new ListSet<T>();
        list.list = (ArrayList<T>) this.list.clone();
        list.set = (HashSet<T>) this.set.clone();
        return list;
    }

    public ArrayList<T> getArrayList() {
        return list;
    }
}
