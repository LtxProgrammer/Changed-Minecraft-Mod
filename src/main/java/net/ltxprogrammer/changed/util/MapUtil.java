package net.ltxprogrammer.changed.util;

import java.util.HashMap;

public class MapUtil {
    public static class HashBuilder<K, V> {
        HashMap<K, V> map = new HashMap<>();

        public HashBuilder<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public HashMap<K, V> finish() {
            return map;
        }
    }
}
