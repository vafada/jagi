/**
 * ResourceProvider.java
 * Adventure Game Interpreter Resource Package
 * <p>
 * Created by Dr. Z
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.res;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * The ResourceProvider interface is a standard
 * way for loading resources dynamicly. Gives the
 * interresing possibility of being able to read
 * them from every kind of data container.
 *
 * @author Dr. Z
 * @version 0.00.00.02
 */
public interface ResourceProvider {
    /** Logic Resource Type. */
    byte TYPE_LOGIC = 0;

    /** Picture Resource Type. */
    byte TYPE_PICTURE = 1;

    /** Sound Resource Type. */
    byte TYPE_SOUND = 2;

    /** View Resource Type. */
    byte TYPE_VIEW = 3;

    /** Compiled Logic Resource Type. (Reserverd for future uses) */
    byte TYPE_LOGIC_COMPILED = 4;

    /** Object File. */
    byte TYPE_OBJECT = 10;

    /** Word Tokenizer File. */
    byte TYPE_WORD = 11;

    /** Fast Provider Type. (ie. File system) */
    byte PROVIDER_TYPE_FAST = 0;

    /** Slow Provider Type. (ie. URL) */
    byte PROVIDER_TYPE_SLOW = 1;

    /**
     * Calculate the CRC of the resources.
     *
     * @return CRC of the resources.
     */
    long getCRC();

    /**
     * Retreive the count of resources of the specified type.
     *
     * @param  resType Resource type
     * @return Resource count.
     */
    int count(byte resType) throws ResourceException;

    /**
     * Enumerate the resource numbers of the specified type.
     *
     * @param  resType Resource type
     * @return Returns an array containing the resource numbers.
     */
    short[] enumerate(byte resType) throws ResourceException;

    /**
     * Retreive the size in bytes of the specified resource.
     *
     * @param  resType   Resource type
     * @param  resNumber Resource number
     * @return Returns the size in bytes of the specified resource.
     */
    int getSize(byte resType, short resNumber) throws ResourceException, IOException;

    /**
     * Open the specified resource and return a pointer
     * to the resource. The InputStream is decrypted/decompressed,
     * if neccessary, by this function. (So you don't have to care
     * about them.)
     *
     * @param  resType   Resource type
     * @param  resNumber Resource number
     * @return InputStream linked to the specified resource.
     */
    InputStream open(byte resType, short resNumber) throws ResourceException, IOException;

    /**
     * Return the provider type. Used has a optimization hint by
     * the resource cache. (For example, PROVIDER_TYPE_SLOW whould
     * mean to never ask twice for the same resource because transfert
     * rate may be slow.)
     */
    byte getProviderType();

    /**
     * Return the resource configuration.
     */
    ResourceConfiguration getConfiguration();

    File getPath();

    String getVersion();
}
