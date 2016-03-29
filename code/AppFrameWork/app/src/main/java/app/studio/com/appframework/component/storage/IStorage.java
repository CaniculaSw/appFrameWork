package app.studio.com.appframework.component.storage;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/28 0028.
 */
public interface IStorage {

    void save(String key, String value);

    void save(String key, int value);

    void save(String key, boolean value);

    void save(String key, Serializable value);

    void save(String key, Object value);

    boolean isContain(String key);

    String getString(String key);

    int getInt(String key);

    boolean getBoolean(String key);

    Object getObject(String key);

    void remove(String key);

    void clearAll();
}
