package app.studio.com.appframework.component.storage.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import app.studio.com.appframework.component.storage.IStorage;

/**
 * 内存存储的实现
 */
public class MemStorage implements IStorage {

    private final Map<String, Object> memCache = new HashMap<>();

    @Override
    public void save(String key, String value) {
        if (null == key) {
            return;
        }

        synchronized (memCache) {
            memCache.put(key, value);
        }
    }

    @Override
    public void save(String key, int value) {
        if (null == key) {
            return;
        }

        synchronized (memCache) {
            memCache.put(key, value);
        }
    }

    @Override
    public void save(String key, boolean value) {
        if (null == key) {
            return;
        }

        synchronized (memCache) {
            memCache.put(key, value);
        }
    }

    @Override
    public void save(String key, Serializable value) {
        if (null == key) {
            return;
        }

        synchronized (memCache) {
            memCache.put(key, value);
        }
    }

    @Override
    public void save(String key, Object value) {
        if (null == key) {
            return;
        }

        synchronized (memCache) {
            memCache.put(key, value);
        }
    }

    @Override
    public boolean isContain(String key) {
        if (null == key) {
            return false;
        }

        synchronized (memCache) {
            return memCache.containsKey(key);
        }
    }

    @Override
    public String getString(String key) {
        if (null == key) {
            return null;
        }

        Object valueObj;
        synchronized (memCache) {
            valueObj = memCache.get(key);
        }

        if (null == valueObj) {
            return null;
        }

        if (valueObj instanceof String) {
            return (String) valueObj;
        }

        return valueObj.toString();
    }

    @Override
    public int getInt(String key) {
        if (null == key) {
            return Integer.MIN_VALUE;
        }

        Object valueObj;
        synchronized (memCache) {
            valueObj = memCache.get(key);
        }

        if (null == valueObj) {
            return Integer.MIN_VALUE;
        }

        if (valueObj instanceof Integer) {
            return (Integer) valueObj;
        }

        if (valueObj instanceof String) {
            try {
                return Integer.parseInt((String) valueObj);
            } catch (Exception e) {
                return Integer.MIN_VALUE;
            }
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean getBoolean(String key) {
        if (null == key) {
            return Boolean.FALSE;
        }

        Object valueObj;
        synchronized (memCache) {
            valueObj = memCache.get(key);
        }

        if (null == valueObj) {
            return Boolean.FALSE;
        }

        if (valueObj instanceof Boolean) {
            return (Boolean) valueObj;
        }

        if (valueObj instanceof String) {
            try {
                return Boolean.parseBoolean((String) valueObj);
            } catch (Exception e) {
                return Boolean.FALSE;
            }
        }
        return Boolean.FALSE;
    }


    @Override
    public Object getObject(String key) {
        if (null == key) {
            return null;
        }

        synchronized (memCache) {
            return memCache.get(key);
        }
    }

    @Override
    public void remove(String key) {
        synchronized (memCache) {
            if (memCache.containsKey(key)) {
                memCache.remove(key);
            }
        }
    }

    @Override
    public void clearAll() {
        synchronized (memCache) {
            memCache.clear();
        }
    }

}
