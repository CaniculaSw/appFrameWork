package app.studio.com.appframework.component.storage.impl;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import app.studio.com.appframework.component.log.Logger;
import app.studio.com.appframework.component.storage.IStorage;
import app.studio.com.appframework.utils.FileUtil;
import app.studio.com.appframework.utils.StringUtil;

/**
 * Created by Administrator on 2016/3/28 0028.
 */
public class SharedPStorage implements IStorage {
    private static final String TAG = "SharedPStorage";

    private SharedPreferences sharedPreferencesCache;

    public SharedPStorage(Context context) {
        sharedPreferencesCache = context.getSharedPreferences("storage_public", Context.MODE_PRIVATE);
    }

    public SharedPStorage(Context context, String id) {
        sharedPreferencesCache = context.getSharedPreferences("storage_private_" + id, Context.MODE_PRIVATE);
    }

    @Override
    public void save(String key, String value) {
        if (null == key) {
            return;
        }

        SharedPreferences.Editor editor = sharedPreferencesCache.edit();
        editor.putString(key, value);
        editor.commit();
    }

    @Override
    public void save(String key, int value) {
        if (null == key) {
            return;
        }

        SharedPreferences.Editor editor = sharedPreferencesCache.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    @Override
    public void save(String key, boolean value) {
        if (null == key) {
            return;
        }

        SharedPreferences.Editor editor = sharedPreferencesCache.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    @Override
    public void save(String key, Serializable value) {
        if (null == key) {
            return;
        }

        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(value);
        } catch (IOException e) {
            Logger.w(TAG, "save serializable object failed. IOException: ", e);
        } catch (Exception e) {
            Logger.w(TAG, "save serializable object failed. Exception: ", e);
        } finally {
            FileUtil.closeStream(os);
            FileUtil.closeStream(bos);
        }


        String valueStr = StringUtil.bytes2Hex(bos.toByteArray());
        save(key, valueStr);
    }

    @Override
    public void save(String key, Object value) {
        if (null == key) {
            return;
        }

        if (null == value) {
            remove(key);
        } else if (value instanceof String) {
            save(key, (String) value);
        } else if (value instanceof Integer) {
            save(key, (Integer) value);
        } else if (value instanceof Boolean) {
            save(key, (Boolean) value);
        } else if (value instanceof Serializable) {
            save(key, (Serializable) value);
        }
    }

    @Override
    public boolean isContain(String key) {
        if (null == key) {
            return false;
        }

        return sharedPreferencesCache.contains(key);
    }

    @Override
    public String getString(String key) {
        if (null == key) {
            return null;
        }
        return sharedPreferencesCache.getString(key, null);
    }

    @Override
    public int getInt(String key) {
        if (null == key) {
            return Integer.MIN_VALUE;
        }
        return sharedPreferencesCache.getInt(key, Integer.MIN_VALUE);
    }

    @Override
    public boolean getBoolean(String key) {
        if (null == key) {
            return Boolean.FALSE;
        }
        return sharedPreferencesCache.getBoolean(key, Boolean.FALSE);
    }

    @Override
    public Object getObject(String key) {
        if (null == key) {
            return null;
        }

        String valueStr = getString(key);
        if (null == valueStr) {
            return null;
        }

        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        Object readObject = null;
        try {
            bis = new ByteArrayInputStream(StringUtil.hex2Bytes(valueStr));
            is = new ObjectInputStream(bis);
            readObject = is.readObject();
        } catch (ClassNotFoundException e) {
            Logger.w(TAG, "get object failed. ClassNotFoundException: ", e);
        } catch (IOException e) {
            Logger.w(TAG, "get object failed. IOException: ", e);
        } catch (Exception e) {
            Logger.w(TAG, "get object failed. Exception: ", e);
        } finally {
            FileUtil.closeStream(is);
            FileUtil.closeStream(bis);
        }
        return readObject;
    }

    @Override
    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferencesCache.edit();
        editor.remove(key);
        editor.commit();
    }

    @Override
    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferencesCache.edit();
        editor.clear();
        editor.commit();
    }

}
