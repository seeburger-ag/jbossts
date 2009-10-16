/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 2009,
 * @author JBoss, a division of Red Hat.
 */
package com.arjuna.ats.arjuna.common;

import com.arjuna.ats.arjuna.objectstore.ObjectStore;
import com.arjuna.ats.arjuna.ArjunaNames;
import com.arjuna.ats.internal.arjuna.objectstore.HashedStore;
import com.arjuna.common.internal.util.propertyservice.FullPropertyName;
import com.arjuna.common.internal.util.propertyservice.PropertyPrefix;

import java.io.File;

/**
 * A JavaBean containing configuration properties for the objectstore and various implementations thereof.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com)
 */
@PropertyPrefix(prefix = "com.arjuna.ats.arjuna.objectstore.")
public class ObjectStoreEnvironmentBean implements ObjectStoreEnvironmentBeanMBean
{
    private String localOSRoot = "defaultStore";
    private String objectStoreDir = System.getProperty("user.dir") + File.separator + "ObjectStore";
    private boolean objectStoreSync = true;
    private String objectStoreType = ArjunaNames.Implementation_ObjectStore_defaultStore().stringForm();
    private int hashedDirectories = HashedStore.DEFAULT_NUMBER_DIRECTORIES;
    private boolean transactionSync = true;
    
    private String jdbcUserDbAccess = null;
    private String jdbcTxDbAccess = null;
    private int jdbcPoolSizeInitial = 1;
    private int jdbcPoolSizeMaximum = 1;
    private boolean jdbcPoolPutConnections = false;

    private int share = ObjectStore.OS_UNKNOWN;
    private int hierarchyRetry = 100;
    private int hierarchyTimeout = 100;

    @FullPropertyName(name = "com.arjuna.ats.internal.arjuna.objectstore.cacheStore.size")
    private int cacheStoreSize = 10240;  // size in bytes
    @FullPropertyName(name = "com.arjuna.ats.internal.arjuna.objectstore.cacheStore.sync")
    private boolean cacheStoreSync = false;
    @FullPropertyName(name = "com.arjuna.ats.internal.arjuna.objectstore.cacheStore.removedItems")
    private int cacheStoreRemovedItems = 256;
    @FullPropertyName(name = "com.arjuna.ats.internal.arjuna.objectstore.cacheStore.scanPeriod")
    private int cacheStoreScanPeriod = 120000;
    @FullPropertyName(name = "com.arjuna.ats.internal.arjuna.objectstore.cacheStore.workItems")
    private int cacheStoreWorkItems = 100;
    @FullPropertyName(name = "com.arjuna.ats.internal.arjuna.objectstore.cacheStore.hash")
    private int cacheStoreHash = 128;

    @FullPropertyName(name = "com.arjuna.ats.arjuna.coordinator.transactionLog.synchronousRemoval")
    private boolean synchronousRemoval = true;
    @FullPropertyName(name = "com.arjuna.ats.arjuna.coordinator.transactionLog.size")
    private long txLogSize = 10 * 1024 * 1024;  // default maximum log txLogSize in bytes;
    @FullPropertyName(name = "com.arjuna.ats.arjuna.coordinator.transactionLog.purgeTime")
    private long purgeTime = 100000; // in milliseconds


    /**
     * Returns the maximum allowed size, in bytes, of the cache store's in-memory cache.
     *
     * Default: 10240 bytes
     * Equivalent deprecated property: com.arjuna.ats.internal.arjuna.objectstore.cacheStore.size
     *
     * @return the memory cache size in bytes.
     */
    public int getCacheStoreSize()
    {
        return cacheStoreSize;
    }

    /**
     * Sets the maximum size, in bytes, of the in-memory object state cache.
     *
     * @param cacheStoreSize the maximum cache size in bytes.
     */
    public void setCacheStoreSize(int cacheStoreSize)
    {
        this.cacheStoreSize = cacheStoreSize;
    }

    /**
     * Returns true if writes to the objectstore should include a disk sync. Unlikely to be worthwile
     * since the store caches state in memory anyhow.
     *
     * Default: false
     * Equivalent deprecated property: com.arjuna.ats.internal.arjuna.objectstore.cacheStore.sync
     *
     * @return true if writes should be synched to disk, false otherwise.
     */
    public boolean isCacheStoreSync()
    {
        return cacheStoreSync;
    }

    /**
     * Sets if writes to the store should be synched to disk or not.
     *
     * @param cacheStoreSync true to enable syncing, false to disable.
     */
    public void setCacheStoreSync(boolean cacheStoreSync)
    {
        this.cacheStoreSync = cacheStoreSync;
    }

    /**
     * Returns the maximum number of removed items that may be held in the cache before being purged.
     *
     * Default: 256
     * Equivalent deprecated property: com.arjuna.ats.internal.arjuna.objectstore.cacheStore.removedItems
     *
     * @return the maximum number of removed items in the cache.
     */
    public int getCacheStoreRemovedItems()
    {
        return cacheStoreRemovedItems;
    }

    /**
     * Sets the maximum number of removed items that may be held in the cache before being purged.
     *
     * @param cacheStoreRemovedItems teh maximun number of items.
     */
    public void setCacheStoreRemovedItems(int cacheStoreRemovedItems)
    {
        this.cacheStoreRemovedItems = cacheStoreRemovedItems;
    }

    /**
     * Returns the interval on which the cache will wake and process outstanding work.
     *
     * Default: 120000 milliseconds
     * Equivalent deprecated property: com.arjuna.ats.internal.arjuna.objectstore.cacheStore.scanPeriod
     *
     * @return the work interval of the cache, in milliseconds.
     */
    public int getCacheStoreScanPeriod()
    {
        return cacheStoreScanPeriod;
    }

    /**
     * Sets the interval on which the cache will process outstanding work, in milliseconds.
     *
     * @param cacheStoreScanPeriod the sleep duration, in milliseconds.
     */
    public void setCacheStoreScanPeriod(int cacheStoreScanPeriod)
    {
        this.cacheStoreScanPeriod = cacheStoreScanPeriod;
    }

    /**
     * Returns the maximum number of outstanding writes that may be held in the cache.
     *
     * Default: 100
     * Equivalent deprecated property: com.arjuna.ats.internal.arjuna.objectstore.cacheStore.workItems
     *
     * @return the maximum number of outstanding writes in the cache.
     */
    public int getCacheStoreWorkItems()
    {
        return cacheStoreWorkItems;
    }

    /**
     * Sets the maximum number of outstanding writes that may be held in the cache.
     *
     * @param cacheStoreWorkItems the maximum number of outstnading writes.
     */
    public void setCacheStoreWorkItems(int cacheStoreWorkItems)
    {
        this.cacheStoreWorkItems = cacheStoreWorkItems;
    }

    /**
     * Returns the number of hash buckets used for the cache work queue.
     *
     * Default: 128
     * Equivalent deprecated property: com.arjuna.ats.internal.arjuna.objectstore.cacheStore.hash
     *
     * @return the number of hash buckets used to store the cache state.
     */
    public int getCacheStoreHash()
    {
        return cacheStoreHash;
    }

    /**
     * Sets the number of hash buskets used to store the cache work queue.
     *
     * @param cacheStoreHash the number of hash buckets.
     */
    public void setCacheStoreHash(int cacheStoreHash)
    {
        this.cacheStoreHash = cacheStoreHash;
    }


    /**
     * Returns the local ObjectStore root directory name. This should be a path element, not a complete path.
     *
     * Default: "defaultStore"
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.localOSRoot
     *
     * @return the local ObjectStore root directory name.
     */
    public String getLocalOSRoot()
    {
        return localOSRoot;
    }

    /**
     * Sets the local ObjectStore root directory name. This shold be a path element, not a complete path.
     *
     * @param localOSRoot the directory name.
     */
    public void setLocalOSRoot(String localOSRoot)
    {
        this.localOSRoot = localOSRoot;
    }

    /**
     * Returns the ObjectStore directory path.
     *
     * Default: {user.dir}/ObjectStore
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.objectStoreDir
     *
     * @return the ObjectStore directory path.
     */
    public String getObjectStoreDir()
    {
        return objectStoreDir;
    }

    /**
     * Sets the ObjectStore directory path.
     *
     * @param objectStoreDir the directory path.
     */
    public void setObjectStoreDir(String objectStoreDir)
    {
        this.objectStoreDir = objectStoreDir;
    }

    /**
     * Returns true if ObjectStore operations should be synched to disk.
     * Note that this value may be overridden by store implementation specific configuration.
     * See also: isTransactionSync
     *
     * Default: true
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.objectStoreSync
     *
     * @return true for synched operations, false otherwise.
     */
    public boolean isObjectStoreSync()
    {
        return objectStoreSync;
    }

    /**
     * Sets if ObjectStore operations should be synched to disk or not.
     * Caution: Disabling this may be lead to non-ACID transaction behaviour.
     *
     * @param objectStoreSync true to sunc to disk, false to skip synching.
     */
    public void setObjectStoreSync(boolean objectStoreSync)
    {
        this.objectStoreSync = objectStoreSync;
    }

    /**
     * Returns the symbolic name for the ObjectStore implementation.
     *
     * Default: "ShadowNoFileLockStore" TODO test
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.objectStoreType
     *
     * @return the symbolic name of the ObjectStore implementation.
     */
    public String getObjectStoreType()
    {
        return objectStoreType;
    }

    /**
     * Sets the symbolic name of the ObjectStore implementation.
     *
     * @param objectStoreType the symbolic name of the implementation.
     */
    public void setObjectStoreType(String objectStoreType)
    {
        this.objectStoreType = objectStoreType;
    }

    /**
     * Returns the number of directories over which the ObjectStore contents will be distributed.
     * Splitting the contents is important for performance on some file systems, as it reduces
     * chain length (number of items in a directory) and directory lock contention.
     *
     * Default: 255
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.hashedDirectories
     *
     * @return the number of directories over which to distribute the store.
     */
    public int getHashedDirectories()
    {
        return hashedDirectories;
    }

    /**
     * Sets the number of directories over which the ObjectStore will be split.
     *
     * @param hashedDirectories the number of directories.
     */
    public void setHashedDirectories(int hashedDirectories)
    {
        this.hashedDirectories = hashedDirectories;
    }

    /**
     * Returns true if transaction log operations should be synched to disk.
     *
     * Default: true
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.transactionSync
     *
     * @return true if operations should be forcedto disk, false otherwise.
     */
    public boolean isTransactionSync()
    {
        return transactionSync;
    }

    /**
     * Sets if transaction log operations should be synched to disk or not.
     * Caution: Disabling this may be lead to non-ACID transaction behaviour.
     *
     * @param transactionSync true to enable synching, false to disable.
     */
    public void setTransactionSync(boolean transactionSync)
    {
        this.transactionSync = transactionSync;
    }

    /**
     * Returns the classname of the JDBCAccess implementation used for the ObjectStore.
     *
     * Default: null
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.jdbcUserDbAccess
     *
     * @return the name of a class implementing JDBCAccess.
     */
    public String getJdbcUserDbAccess()
    {
        return jdbcUserDbAccess;
    }

    /**
     * Sets the classname of the JDBCAccess implementation used for the ObjectStore.
     *
     * @param jdbcUserDbAccess the name of the class implementing JDBCAccess.
     */
    public void setJdbcUserDbAccess(String jdbcUserDbAccess)
    {
        this.jdbcUserDbAccess = jdbcUserDbAccess;
    }

    /**
     * Returns the classname of the JDBCAccess implementation used for the ActionStore.
     *
     * Default: null
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.jdbcTxDbAccess
     *
     * @return the name of a class implementing JDBCAccess.
     */
    public String getJdbcTxDbAccess()
    {
        return jdbcTxDbAccess;
    }

    /**
     * Sets the classname of the JDBCAccess implementation used for the ActionStore.
     *
     * @param jdbcTxDbAccess the name of the class implementing JDBCAccess.
     */
    public void setJdbcTxDbAccess(String jdbcTxDbAccess)
    {
        this.jdbcTxDbAccess = jdbcTxDbAccess;
    }

    /**
     * Returns the number of connections to initialize in the pool at startup.
     *
     * Default: 1
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.jdbcPoolSizeInitial
     *
     * @return the initial size of the connection pool.
     */
    public int getJdbcPoolSizeInitial()
    {
        return jdbcPoolSizeInitial;
    }

    /**
     * Sets the number of the connection to initialize in the pool at startup.
     *
     * @param jdbcPoolSizeInitial the initial size of the connection pool.
     */
    public void setJdbcPoolSizeInitial(int jdbcPoolSizeInitial)
    {
        this.jdbcPoolSizeInitial = jdbcPoolSizeInitial;
    }

    /**
     * Returns the maximum number of connections to hold in the pool.
     *
     * Default: 1
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.jdbcPoolSizeMaximum"
     *
     * @return the maximum size of the connection pool.
     */
    public int getJdbcPoolSizeMaximum()
    {
        return jdbcPoolSizeMaximum;
    }

    /**
     * Sets the maximum number of connections to hold in the pool.
     *
     * @param jdbcPoolSizeMaximum the maximum size of the connection pool.
     */
    public void setJdbcPoolSizeMaximum(int jdbcPoolSizeMaximum)
    {
        this.jdbcPoolSizeMaximum = jdbcPoolSizeMaximum;
    }

    /**
     * Returns if connections should be returned to the pool after use.
     *
     * Default: false
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.jdbcPoolPutConnections
     *
     * @deprecated I'm unused and should be removed.
     * @return true if connections should be reused, false otherwise.
     */
    public boolean isJdbcPoolPutConnections()
    {
        return jdbcPoolPutConnections;
    }

    /**
     * Sets if connections should be returned to the pool after use.
     *
     * @param jdbcPoolPutConnections true to enable connection reuse, false to disable.
     */
    public void setJdbcPoolPutConnections(boolean jdbcPoolPutConnections)
    {
        this.jdbcPoolPutConnections = jdbcPoolPutConnections;
    }

    /**
     * Returns the share mode for the ObjectStore.
     *
     * Default: ObjectStore.OS_UNKNOWN
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.share
     *
     * @deprecated I'm unused and should be removed.
     * @return the default share mode.
     */
    public int getShare()
    {
        return share;
    }

    /**
     * Sets the share mode of the ObjectStore
     *
     * @param share a valid share mode.
     */
    public void setShare(int share)
    {
        this.share = share;
    }

    /**
     * Returns the maximum number of attempts which may be made to create a file path in the store.
     *
     * Default: 100
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.hierarchyRetry
     *
     * @return the maximum number of attempts to create a nested directory tree.
     */
    public int getHierarchyRetry()
    {
        return hierarchyRetry;
    }

    /**
     * Sets the maximum number of attempts which may be made to create a direcory tree in the store.
     *
     * @param hierarchyRetry the maximum number of file creation attempts.
     */
    public void setHierarchyRetry(int hierarchyRetry)
    {
        this.hierarchyRetry = hierarchyRetry;
    }

    /**
     * Returns the time in milliseconds to wait between file creation retries.
     *
     * Default: 100 milliseconds.
     * Equivalent deprecated property: com.arjuna.ats.arjuna.objectstore.hierarchyTimeout
     *
     * @return the time to wait before retrying a failed file creation, in milliseconds.
     */
    public int getHierarchyTimeout()
    {
        return hierarchyTimeout;
    }

    /**
     * Sets the time in milliseconds to wait between file creation retries.
     *
     * @param hierarchyTimeout the wait time in milliseconds.
     */
    public void setHierarchyTimeout(int hierarchyTimeout)
    {
        this.hierarchyTimeout = hierarchyTimeout;
    }

    /**
     * Returns true if the LogStore should write removal records synchronously.
     * Disabling this may increase performance at the cost of recovery complexity.
     *
     * Default: true
     * Equivalent deprecated property: com.arjuna.ats.arjuna.coordinator.transactionLog.synchronousRemoval
     *
     * @return true for synchronous removals, false for buffered (asynchronous) operation.
     */
    public boolean isSynchronousRemoval()
    {
        return synchronousRemoval;
    }

    /**
     * Sets if the LogStore should write removal records synchronously or not.
     *
     * @param synchronousRemoval true for synchronous operation, false for asynchronous.
     */
    public void setSynchronousRemoval(boolean synchronousRemoval)
    {
        this.synchronousRemoval = synchronousRemoval;
    }

    /**
     * Returns the default size of the LogStore file, in bytes.
     *
     * Default: 10MB
     * Equivalent deprecated property: com.arjuna.ats.arjuna.coordinator.transactionLog.txLogSize
     *
     * @return the default file size for the LogStore, in bytes.
     */
    public long getTxLogSize()
    {
        return txLogSize;
    }

    /**
     * Sets the default size of the LogStore, in bytes.
     *
     * @param txLogSize the default file size, in bytes.
     */
    public void setTxLogSize(long txLogSize)
    {
        this.txLogSize = txLogSize;
    }

    /**
     * Returns the purge interval for the LogStore, in milliseconds.
     *
     * Default: 100000 milliseconds
     * Equivalent deprecated property: com.arjuna.ats.arjuna.coordinator.transactionLog.purgeTime
     *
     * @return the purge interval in milliseconds.
     */
    public long getPurgeTime()
    {
        return purgeTime;
    }

    /**
     * Sets the purge interval for the LogStore, in milliseconds.
     *
     * @param purgeTime the purge interval in milliseconds.
     */
    public void setPurgeTime(long purgeTime)
    {
        this.purgeTime = purgeTime;
    }
}