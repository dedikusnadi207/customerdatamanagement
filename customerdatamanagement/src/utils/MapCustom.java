/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dedikusnadi
 */
public class MapCustom {
    public static <K, V> Map<K, V> of() {
        return new HashMap<>();
    }
    
    public static <K, V> Map<K, V> of(K k1, V v1) {
        HashMap<K,V> data = new HashMap<>();
        data.put(k1, v1);
        return data;
    }
    
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        HashMap<K,V> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        return data;
    }
    
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        HashMap<K,V> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        data.put(k3, v3);
        return data;
    }
    
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        HashMap<K,V> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        data.put(k3, v3);
        data.put(k4, v4);
        return data;
    }
    
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        HashMap<K,V> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        data.put(k3, v3);
        data.put(k4, v4);
        data.put(k5, v5);
        return data;
    }
    
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        HashMap<K,V> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        data.put(k3, v3);
        data.put(k4, v4);
        data.put(k5, v5);
        data.put(k6, v6);
        return data;
    }
    
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        HashMap<K,V> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        data.put(k3, v3);
        data.put(k4, v4);
        data.put(k5, v5);
        data.put(k6, v6);
        data.put(k7, v7);
        return data;
    }
    
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
        HashMap<K,V> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        data.put(k3, v3);
        data.put(k4, v4);
        data.put(k5, v5);
        data.put(k6, v6);
        data.put(k7, v7);
        data.put(k8, v8);
        data.put(k9, v9);
        return data;
    }
}
