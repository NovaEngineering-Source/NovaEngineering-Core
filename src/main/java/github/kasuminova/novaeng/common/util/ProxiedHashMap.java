package github.kasuminova.novaeng.common.util;

import java.util.HashMap;
import java.util.Map;

public class ProxiedHashMap<K, V> extends HashMap<K, V> {

    public ProxiedHashMap(final Map<? extends K, ? extends V> m) {
        super(m);
    }
    
    
    
}
