package com.agifans.jagi.res.v1;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides storage and quick lookup for LogicalFiles.
 *
 * @author Lance Ewing
 */
public class LogicalFileCache {

    /**
     * A Map of LogicFiles keyed by their logical file name.
     */
    private final Map<String, LogicalFile> cacheMap;

    /**
     * Constructor for LogicalFileCache.
     *
     * @param logicalFiles
     */
    public LogicalFileCache(LogicalFile[] logicalFiles) {
        this.cacheMap = new HashMap<String, LogicalFile>();

        for (LogicalFile logicalFile : logicalFiles) {
            cacheMap.put(logicalFile.getLogicalFileName(), logicalFile);
        }
    }

    /**
     * Gets a LogicalFile by it's logical file name.
     *
     * @param logicalFileName The logical file name to return the LogicalFile for.
     * @return The LogicalFile for the given logical file name.
     */
    public LogicalFile getLogicalFileByName(String logicalFileName) {
        return cacheMap.get(logicalFileName);
    }
}
