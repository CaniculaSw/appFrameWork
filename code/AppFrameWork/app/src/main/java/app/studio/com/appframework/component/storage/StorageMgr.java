package app.studio.com.appframework.component.storage;

import app.studio.com.appframework.component.storage.impl.MemStorage;

/**
 * Created by Administrator on 2016/3/28 0028.
 */
public final class StorageMgr {

    private static final String TAG = "StorageMgr";

    private static final StorageMgr mInstance = new StorageMgr();

    private IStorage memStorage = new MemStorage();

//    private IStorage sharedPStorage = new SharedPStorage();

    private StorageMgr() {
    }

    public static StorageMgr getInstance() {
        return mInstance;
    }

    public IStorage getMemStorage() {
        return memStorage;
    }

//    public IStorage getSharedPStorage() {
//        return sharedPStorage;
//    }
}
