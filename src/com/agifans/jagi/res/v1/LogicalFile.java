package com.agifans.jagi.res.v1;

import com.sierra.agi.io.ByteCaster;
import com.sierra.agi.res.CorruptedResourceException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Represents a logical AGI file within a disk image. In AGI games prior to
 * AGIv2, data was stored directly on disk sectors with no actual file system in
 * use on the disk. The booter loaded up the Sierra AGI system and data was read
 * directly from disk sectors. Such games therefore come as disk images, but on
 * these disk images there are what I'll refer to as logical files. These are the
 * parts of the disk image that will later be split out in to separate files in
 * the AGIv2 games.
 *
 * @author Lance Ewing
 */
public class LogicalFile {

    /**
     * The physical disk image file that contains this LogicalFile.
     */
    private final File physicalFile;

    /**
     * The type of LogicalFile, i.e. DIR, VOL, OBJECT, WORDS.
     */
    private LogicalFileType type;

    /**
     * The name of this LogicalFile, e.g. VOL.1, LOGDIR, or WORDS.TOK.
     */
    private String logicalFileName;

    /**
     * The size of the logical file in bytes.
     */
    private int logicalFileSize;

    /**
     * The offset within the physical disk image file where this logical file starts.
     */
    private int physicalFileOffset;

    /**
     * The raw byte data of the logical file.
     */
    private byte[] rawData;

    /**
     * For LogicalFiles of type VOL, the distinguisher holds the VOL number.
     */
    private int distinguisher;

    /**
     * Constructor for LogicalFile.
     *
     * @param diskImageDirectory
     * @param physicalFileName
     * @param physicalFileOffset
     * @param validateHeader
     * @param processHeader
     * @param type
     * @param distinguisher
     * @throws CorruptedResourceException
     * @throws IOException
     */
    public LogicalFile(
            File diskImageDirectory, String physicalFileName, int physicalFileOffset,
            boolean validateHeader, boolean processHeader, LogicalFileType type,
            Object... distinguisher) throws CorruptedResourceException, IOException {

        this.physicalFile = new File(diskImageDirectory, physicalFileName);
        this.physicalFileOffset = physicalFileOffset;
        this.type = type;
        this.logicalFileName = type.getLogicalFileName(distinguisher);

        // If the distinguisher is an Integer, we'll use it for validation later on.
        if ((distinguisher.length > 0) && (distinguisher[0] instanceof Integer)) {
            this.distinguisher = (Integer) distinguisher[0];
        }

        loadData(validateHeader, processHeader);
    }

    /**
     * Loads the raw data that relates to this logical file from the physical file.
     *
     * @param validateHeader
     * @param processHeader
     * @throws IOException
     * @throws CorruptedResourceException
     */
    private void loadData(boolean validateHeader, boolean processHeader) throws IOException, CorruptedResourceException {
        RandomAccessFile raf = null;

        try {
            raf = new RandomAccessFile(physicalFile, "r");
            raf.seek(physicalFileOffset);

            if (validateHeader) {
                // Read past the 0x1234 signature.
                int sig1 = raf.read();
                int sig2 = raf.read();
                if ((sig1 != 0x12) || (sig2 != 0x34)) {
                    throw new CorruptedResourceException("Start of logical file does not start with 0x1234.");
                }

                // VOL number. Not of interest to non-VOL logical files.
                int volNum = raf.read();
                if (volNum != distinguisher) {
                    throw new CorruptedResourceException("VOL number in header does not match distinguisher.");
                }
            }

            if (processHeader) {
                // Read the size from the header.
                int sizeLo = raf.read();
                int sizeHi = raf.read();
                logicalFileSize = ((sizeHi * 256) + sizeLo);

            } else {
                // Set back to start offset, since if we're not processing the header, we want
                // to read it in to the raw data of the LogicalFile. It is required as the start
                // of a sub file (e.g. a LOGIC, VIEW, etc. which are sub files or a VOL file)
                raf.seek(physicalFileOffset);

                // Default behaviour is to read from the offset to the end of the file. This
                // means that for VOL.1, it will read more than it should, but that redundancy
                // is fine for now.
                logicalFileSize = ((int) raf.length()) - this.physicalFileOffset;
            }

            // Read the full data for this resource.
            rawData = new byte[logicalFileSize];
            raf.readFully(rawData);

        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    // Should never happen.
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Gets the raw data inside the logical file.
     *
     * @return The raw data from inside the logical file.
     */
    public byte[] getRawData() {
        return rawData;
    }

    /**
     * Calculates the CRC for this LogicalFile from the raw data.
     *
     * @return The calculated CRC.
     */
    public int getCRC() {
        int c = 0;

        for (int i = 0; i < rawData.length; i++) {
            c += (rawData[i] & 0xFF);
        }

        return c;
    }

    /**
     * Gets the size of the sub file at the given offset.
     *
     * @param offset The offset within the LogicalFile to find the header for the sub-file.
     * @return The size of the sub file at the given offset.
     * @throws CorruptedResourceException
     */
    public int getSubFileSize(int offset) throws CorruptedResourceException {
        int sig1 = rawData[offset];
        int sig2 = rawData[offset + 1];
        if ((sig1 != 0x12) || (sig2 != 0x34)) {
            throw new CorruptedResourceException("The sub file offset does not point to a 0x1234 header signature.");
        }

        // VOL number. Performs a validation check that the sub file is marked as belonging
        // to this particular VOL.
        int volNumber = rawData[offset + 2];
        if (volNumber != distinguisher) {
            throw new CorruptedResourceException("VOL number in header does not match distinguisher.");
        }

        // Read in and return the size of the sub file.
        return ByteCaster.lohiUnsignedShort(rawData, offset + 3);
    }

    /**
     * Gets the raw data for a sub file within the LogicFile. The obvious case of this is
     * the VOL file that has within it resources files of the types LOGIC, PICTURE, VIEW,
     * and SOUND.
     *
     * @param offset The offset within the LogicalFile to find the header for the sub-file.
     * @return The raw data for the sub file at the given location.
     * @throws CorruptedResourceException
     */
    public byte[] getSubFileRawData(int offset) throws CorruptedResourceException {
        try {
            int sig1 = rawData[offset];
            int sig2 = rawData[offset + 1];
            if ((sig1 != 0x12) || (sig2 != 0x34)) {
                throw new CorruptedResourceException("The sub file offset does not point to a 0x1234 header signature.");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("Offset " + offset + " is beyond size of rawData (" + rawData.length + ") for LogicalFile " + this.logicalFileName);
            throw e;
        }

        // VOL number. Performs a validation check that the sub file is marked as belonging
        // to this particular VOL.
        int volNumber = rawData[offset + 2];
        if (volNumber != distinguisher) {
            throw new CorruptedResourceException("VOL number in header does not match distinguisher.");
        }

        // Read the size of the sub file.
        int size = ByteCaster.lohiUnsignedShort(rawData, offset + 3);

        // Read the full data for this sub file.
        byte[] subFileRawData = new byte[size];

        System.arraycopy(rawData, offset + 5, subFileRawData, 0, size);

        return subFileRawData;
    }

    public LogicalFileType getType() {
        return type;
    }

    public void setType(LogicalFileType type) {
        this.type = type;
    }

    public String getLogicalFileName() {
        return logicalFileName;
    }

    public void setLogicalFileName(String logicalFileName) {
        this.logicalFileName = logicalFileName;
    }

    public int getLogicalFileSize() {
        return logicalFileSize;
    }

    public void setLogicalFileSize(int logicalFileSize) {
        this.logicalFileSize = logicalFileSize;
    }

    public String getPhysicalFileName() {
        return physicalFile.getName();
    }

    public int getPhysicalFileOffset() {
        return physicalFileOffset;
    }

    public void setPhysicalFileOffset(int physicalFileOffset) {
        this.physicalFileOffset = physicalFileOffset;
    }
}
