/*
 * Copyright (C) 2016 Adarsh Soodan
 * 
 * Stoa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3.0 as 
 * published by the Free Software Foundation.
 *
 * Stoa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3.0 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License version 3.0
 * along with Stoa.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.decon.stoa;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.SecureRandom;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Supplier;

import org.decon.stoa.types.BoolComponent;
import org.decon.stoa.types.InlineComponent;
import org.decon.stoa.util.BitBuffer;
import org.decon.stoa.util.ExceptionUtils;

/**
 * FIXME Convert this map into a pseudo-HopScotch map A pseudo-HopScotch map => where hop distance is increased instead
 * of resizing the map. Resizing the map in HopScotch map happens, whenever current neighorhood of empty slot is full of
 * keys that do not belong here. Instead of resizing the map, the hop distance can be increased uptill Integer.MAX_INT
 * thus increasing neighorhood size to entire array. This makes the insert complexity the same as linear probe within a
 * small constant factor. Remove in a hop scotch map has worse complexity than linear probe. To resize map, one can
 * either exceed the load factor, or create a new map (not a clone) with a different perMapIV.
 */
public class StoaMap<K, V> extends AbstractMap<K, V> implements Cloneable {
    private final Defaults defaults;

    private final Stoa<Slot<K, V>> slots;

    private final int    perMapIV;
    private final double loadFactor;

    private int size;

    public StoaMap(Supplier<Stoa<K>> keyStoaFactory, Supplier<Stoa<V>> valueStoaFactory) {
        this(new Defaults() {
        }, keyStoaFactory, valueStoaFactory);
    }

    @SuppressWarnings({ "unchecked" })
    public StoaMap(Defaults defaults, Supplier<Stoa<K>> keyStoaFactory, Supplier<Stoa<V>> valueStoaFactory) {
        this.defaults = defaults;
        Map<String, Component<Slot<K, V>, ?>> slotComponents = new HashMap<>();
        slotComponents.put("present", new BoolComponent<Slot<K, V>>(Slot.isPresent, Slot.setPresent));
        slotComponents.put("key", new InlineComponent<Slot<K, V>, K>(keyStoaFactory, Slot::getKey, Slot::setKey));
        slotComponents.put("value",
                new InlineComponent<Slot<K, V>, V>(valueStoaFactory, Slot::getValue, Slot::setValue));

        this.slots = new Stoa<Slot<K, V>>(() -> new Slot<>(), (Class<Slot<K, V>>) (Object) Slot.class, slotComponents);
        this.perMapIV = new SecureRandom().nextInt();
        this.loadFactor = Math.max(0.01, Math.min(defaults.getMapLoadFactor(), 0.99));

        slots.resize(Math.max(1, defaults.getMapInitialSize()));
    }

    /**
     * NOTE : The other maps {@link #perMapIV} is copied into this map.
     * 
     * @param other
     */
    public StoaMap(StoaMap<K, V> other) {
        this.defaults = other.defaults;
        this.slots = other.slots.clone();
        this.perMapIV = other.perMapIV;
        this.loadFactor = other.loadFactor;
        this.size = other.size;
    }

    private int hash(Object key) {
        int h = key.hashCode() ^ perMapIV;
        int h1 = h ^ (h >> 16);
        return h1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public V get(Object key) {
        if (key == null) {
            return null;
        }
        BitBuffer bitBuffer = (BitBuffer) slots.getComponents()
                                               .get("present");
        for (int i_slot = hash(key) % slots.size(), i = 0; i < slots.size();
             i_slot = (i_slot + 1) % slots.size(), i++) {
            if (bitBuffer.readBit(i_slot)) {
                Slot<K, V> slot = slots.get(i_slot);
                if (key.equals(slot.getKey())) {
                    return slot.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        V previousValue = plainPut(key, value);
        maintainEmptySlots();
        return previousValue;
    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            return null;
        }
        BitBuffer bitBuffer = (BitBuffer) slots.getComponents()
                                               .get("present");
        for (int i_slot = hash(key) % slots.size(), i = 0; i < slots.size();
             i_slot = (i_slot + 1) % slots.size(), i++) {
            if (bitBuffer.readBit(i_slot)) {
                Slot<K, V> slot = slots.get(i_slot);
                if (key.equals(slot.getKey())) {
                    V previousValue = slot.getValue();
                    bitBuffer.writeBit(i_slot, false);
                    slots.nullifyRange(i_slot, i_slot + 1);
                    size--;
                    return previousValue;
                }
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        BitBuffer bitBuffer = (BitBuffer) slots.getComponents()
                                               .get("present");
        for (int i_slot = hash(key) % slots.size(), i = 0; i < slots.size();
             i_slot = (i_slot + 1) % slots.size(), i++) {
            if (bitBuffer.readBit(i_slot)) {
                Slot<K, V> slot = slots.get(i_slot);
                if (key.equals(slot.getKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            return false;
        }
        BitBuffer bitBuffer = (BitBuffer) slots.getComponents()
                                               .get("present");
        for (int i = 0; i < slots.size(); i++) {
            if (bitBuffer.readBit(i)) {
                Slot<K, V> slot = slots.get(i);
                if (value.equals(slot.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void clear() {
        size = 0;
        slots.clear();
        slots.resize(1);
    }

    public Object internalKeyArray(String path) {
        String keyPath = "key." + path;
        return slots.data(keyPath);
    }

    public Object internalValueArray(String path) {
        String valuePath = "value." + path;
        return slots.data(valuePath);
    }

    public Stoa<Slot<K, V>> getSlots() {
        return slots;
    }

    public int getPerMapIV() {
        return perMapIV;
    }

    public double getMaxLoadFactor() {
        return loadFactor;
    }

    public double getCurrentLoadFactor() {
        return ((double) size()) / slots.size();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet(this);
    }

    final private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        private final StoaMap<K, V> map;

        EntrySet(StoaMap<K, V> map) {
            this.map = map;
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator(map);
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean remove(Object o) {
            if (o == null) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            V value = map.get(entry.getKey());
            if (entry.getValue()
                     .equals(value)) {
                map.remove(entry.getKey());
                return true;
            }
            return false;
        }

        @Override
        public boolean contains(Object o) {
            if (o == null) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            V value = map.get(entry.getKey());
            if ((value != null) && value.equals(entry.getValue())) {
                return true;
            }
            return false;
        }

        @Override
        public void clear() {
            map.clear();
        }

    }

    final private class EntryIterator implements Iterator<Map.Entry<K, V>> {

        private final StoaMap<K, V> map;
        private int                 index = -1;

        public EntryIterator(StoaMap<K, V> map) {
            this.map = map;
        }

        @Override
        public boolean hasNext() {
            BitBuffer bitBuffer = (BitBuffer) map.slots.getComponents()
                                                       .get("present");
            return bitBuffer.nextSetBit(index + 1) != -1;
        }

        @Override
        public Map.Entry<K, V> next() {
            BitBuffer bitBuffer = (BitBuffer) map.slots.getComponents()
                                                       .get("present");
            int oldIndex = index;
            index = (int) bitBuffer.nextSetBit(index + 1);
            if (index == -1) {
                index = oldIndex;
                throw new NoSuchElementException();
            }
            return new Entry(map, index);
        }

        @Override
        public void remove() {
            if (index == -1 || (map.slots.get(index)
                                         .isPresent() == false)) {
                throw new IllegalStateException();
            }
            map.remove(map.slots.get(index)
                                .getKey());
        }

    }

    final private class Entry implements Map.Entry<K, V> {
        private final StoaMap<K, V> map;
        private final int           index;

        public Entry(StoaMap<K, V> map, int index) {
            this.map = map;
            this.index = index;
        }

        @Override
        public K getKey() {
            return map.slots.get(index)
                            .getKey();
        }

        @Override
        public V getValue() {
            return map.slots.get(index)
                            .getValue();
        }

        @Override
        public V setValue(V value) {
            Slot<K, V> previous = map.slots.get(index);
            Slot<K, V> newSlot = new Slot<>(previous.isPresent(), previous.getKey(), value);
            map.slots.set(index, newSlot);
            return previous.getValue();
        }

        @Override
        public int hashCode() {
            return getKey().hashCode() ^ getValue().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj instanceof Map.Entry) {
                @SuppressWarnings("unchecked")
                Map.Entry<K, V> other = (Map.Entry<K, V>) obj;
                return getKey().equals(other.getKey()) && getValue().equals(other.getValue());
            }
            return false;
        }

        @Override
        public String toString() {
            return getKey() + "=" + getValue();
        }

    }

    private V plainPut(K key, V value) {
        BitBuffer bitBuffer = (BitBuffer) slots.getComponents()
                                               .get("present");
        for (int i_slot = hash(key) % slots.size(), i = 0; i < slots.size();
             i_slot = (i_slot + 1) % slots.size(), i++) {
            if (bitBuffer.readBit(i_slot) == false) {
                Slot<K, V> newSlot = new Slot<>(true, key, value);
                slots.set(i_slot, newSlot);
                size++;
                return null;
            }
            Slot<K, V> slot = slots.get(i_slot);
            if (key.equals(slot.getKey())) {
                Slot<K, V> newSlot = new Slot<>(true, slot.getKey(), value);
                slots.set(i_slot, newSlot);
                return slot.getValue();
            }
        }
        // Key is not present AND no slot is free. Should not happen if loadFactor is considered after every put.
        throw new IllegalStateException("Key is not present AND no slot is free");
    }

    private void maintainEmptySlots() {
        final int minEmpty = Math.max(defaults.getMapMinEmpty(), (int) ((1.0 - loadFactor) * slots.size()));
        final int empty = slots.size() - size();
        if (empty >= minEmpty) {
            return;
        }

        Stoa<Slot<K, V>> oldSlots = slots.clone();

        final int newSize =
                          size() + Math.max(defaults.getMapMinEmpty(), (int) (size() * defaults.getMapResizeFactor()));
        slots.clear();
        slots.resize(newSize);
        size = 0;

        for (Slot<K, V> m : oldSlots) {
            if (m.isPresent()) {
                plainPut(m.getKey(), m.getValue());
            }
        }
    }

    /**
     * perMapIV remains the same in a clone. This makes the clone operation faster as no rehash is required of new map.
     * To obtain a map with a different perMapIV, create a new map and then add entries of this map into it.
     */
    @Override
    public StoaMap<K, V> clone() throws CloneNotSupportedException {
        return new StoaMap<>(defaults, size, slots.clone(), perMapIV, loadFactor);
    }

    private StoaMap(Defaults defaults, int size, Stoa<Slot<K, V>> slots, int perMapIV, double loadFactor) {
        this.defaults = defaults;
        this.size = size;
        this.slots = slots;
        this.perMapIV = perMapIV;
        this.loadFactor = loadFactor;
    }

    public static class Slot<K, V> {
        private boolean present = false;
        private K       key;
        private V       value;

        public Slot() {
        }

        public Slot(boolean present, K key, V value) {
            this.present = present;
            this.key = key;
            this.value = value;
        }

        public boolean isPresent() {
            return present;
        }

        public Slot<K, V> setPresent(boolean present) {
            this.present = present;
            return this;
        }

        public K getKey() {
            return key;
        }

        public Slot<K, V> setKey(K key) {
            this.key = key;
            return this;
        }

        public V getValue() {
            return value;
        }

        public Slot<K, V> setValue(V value) {
            this.value = value;
            return this;
        }

        @Override
        public String toString() {
            return "Slot [present=" + present + ", key=" + key + ", value=" + value + "]";
        }

        private static MethodHandle isPresent;
        private static MethodHandle setPresent;
        static {
            initStatic();
        }

        private static void initStatic() {
            isPresent = unreflect(Slot.class, "isPresent", boolean.class).asType(
                    MethodType.methodType(boolean.class, Object.class));
            setPresent = unreflect(Slot.class, "setPresent", Slot.class, boolean.class).asType(
                    MethodType.methodType(Object.class, Object.class, boolean.class));
        }

        private static MethodHandle unreflect(Class<?> cls, String name, Class<?> returnType, Class<?>... parameters) {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            try {
                return lookup.findVirtual(cls, name, MethodType.methodType(returnType, parameters));
            } catch (IllegalAccessException | NoSuchMethodException e) {
                throw ExceptionUtils.sneakyThrow(e);
            }
        }

    }

}
