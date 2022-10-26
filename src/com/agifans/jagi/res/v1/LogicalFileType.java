package com.agifans.jagi.res.v1;

/**
 * An enum that represents a type of logical file within a disk image that
 * existed in early AGI games.
 *
 * @author Lance Ewing
 */
public enum LogicalFileType {

    DIR("%sDIR"),
    VOL("VOL.%d"),
    OBJECT("OBJECT"),
    WORDS("WORDS.TOK");

    /**
     * A format String to be used to generate the file name of a particular
     * type of logical file.
     */
    private final String logicalFileNameFormat;

    /**
     * Constructor for LogicalFileType.
     *
     * @param logicalFileNameFormat
     */
    LogicalFileType(String logicalFileNameFormat) {
        this.logicalFileNameFormat = logicalFileNameFormat;
    }

    /**
     * Uses the given distinguisher to generate a formatted file name for this type
     * of logical file. For some logical file types, there can be more than one
     * instance of such a type, e.g. VOLs and DIRs. The distinguisher is the value
     * that is unique to a particular instance of a logical file type. So for VOLs
     * it is a number, and for DIRs is it a name such as "LOG", or "VIEW".
     *
     * @param distinguisher The distinguisher to use with this type's format String.
     * @return The generated file name.
     */
    public String getLogicalFileName(Object... distinguisher) {
        return String.format(logicalFileNameFormat, distinguisher);
    }
}
