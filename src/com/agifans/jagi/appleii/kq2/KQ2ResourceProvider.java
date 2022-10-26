package com.agifans.jagi.appleii.kq2;

import com.agifans.jagi.res.v1.LogicalFile;
import com.agifans.jagi.res.v1.LogicalFileCache;
import com.sierra.agi.io.ByteCaster;
import com.sierra.agi.io.CryptedInputStream;
import com.sierra.agi.res.*;
import com.sierra.agi.res.dir.ResourceDirectory;

import java.io.*;
import java.util.Properties;

/**
 * An implementation of ResourceProvider that knows how to fetch the resource
 * data from the Apple II disk images for KQ2. The version of the interpreter
 * will usually be either v1.08 or v1.10.
 *
 * @author Lance Ewing
 */
public class KQ2ResourceProvider implements ResourceProvider {

    /**
     * Sierra's Decryption Key. This key is used to decrypt original sierra games data.
     */
    public static final String SIERRA_KEY = "Avis Durgan";
    /**
     * CRC value calculated from the resources of the KQ2 game.
     */
    protected long crc;
    /**
     * Array holding the parsed data for the four DIR files.
     */
    protected ResourceDirectory[] entries = new ResourceDirectory[4];
    /**
     * The physical file system directory containing the game's files.
     */
    protected File gameFileDirectory;
    /**
     * Configuration used by various parts of the Java AGI interpreter to alter
     * how it behaves. Different versions of the AGI interpreter, and on different
     * platforms, often do things a bit differently.
     */
    protected ResourceConfiguration configuration = new ResourceConfiguration();
    /**
     * Holds all of the game data files in Map keyed by logical file name, e.g. VOL.1,
     * LOGDIR, OBJECT, etc.
     */
    private final LogicalFileCache logicalFileCache;

    /**
     * Constructor of KQ2ResourceProvider.
     *
     * @param gameFileDirectory The physical file system directory containing the game's files.
     * @param logicalFiles      A LogicalFile array that defines where on what disk images to get the game data.
     * @throws IOException
     */
    public KQ2ResourceProvider(File gameFileDirectory, LogicalFile[] logicalFiles) throws IOException {
        this.logicalFileCache = new LogicalFileCache(logicalFiles);
        this.gameFileDirectory = gameFileDirectory;

        readDirectories();
        calculateCRC();
        calculateConfiguration();
    }

    /**
     * Calculate CRC value from the resources of the KQ2 game.
     */
    protected void calculateCRC() throws IOException {
        File dirf = new File(gameFileDirectory, "vol.crc");

        try {
            /* Check if the CRC has been pre-calculated */
            DataInputStream meta = new DataInputStream(new FileInputStream(dirf));

            crc = meta.readLong();
            meta.close();

        } catch (IOException ex) {
            /* CRC need to be calculated from scratch */
            crc = calculateCRCFromScratch();

            /* Write down the CRC for next times */
            DataOutputStream meta = new DataOutputStream(new FileOutputStream(dirf));

            meta.writeLong(crc);
            meta.close();
        }
    }

    /**
     * Calculates the CRC value for the AGI game that has been loaded from scratch.
     *
     * @return The calculated CRC value.
     * @throws IOException
     */
    protected int calculateCRCFromScratch() throws IOException {
        int c = 0;

        for (int i = 0; i < entries.length; i++) {
            if (entries[i] != null) {
                c += entries[i].getCRC();
            }
        }

        c += logicalFileCache.getLogicalFileByName("OBJECT").getCRC();
        c += logicalFileCache.getLogicalFileByName("WORDS.TOK").getCRC();

        return c;
    }

    /**
     * Calculates the ResourceConfiguration for the game that is identified by
     * the CRC.
     */
    protected void calculateConfiguration() {
        Properties props = new Properties();
        String scrc = "0x" + Long.toString(crc, 16);
        String ver;

        try {
            props.load(getClass().getResourceAsStream("version.conf"));
        } catch (IOException ioex) {
        }

        ver = props.getProperty(scrc, "0x2917");
        configuration.amiga = ver.indexOf('a') != -1;
        configuration.agds = ver.indexOf('g') != -1;
        ver = ver.substring(2);

        while (!Character.isDigit(ver.charAt(ver.length() - 1))) {
            ver = ver.substring(0, ver.length() - 1);
        }

        configuration.engineEmulation = (Integer.valueOf(ver, 16).shortValue());

        props = new Properties();

        try {
            props.load(getClass().getResourceAsStream("name.conf"));
        } catch (IOException ioex) {
        }

        configuration.name = props.getProperty(scrc, "Unknown Game");
    }

    /**
     * Read all directory files.
     *
     * @throws IOException
     */
    public void readDirectories() throws IOException {
        LogicalFile logicDirFile = logicalFileCache.getLogicalFileByName("LOGDIR");
        LogicalFile pictureDirFile = logicalFileCache.getLogicalFileByName("PICDIR");
        LogicalFile viewDirFile = logicalFileCache.getLogicalFileByName("VIEWDIR");
        LogicalFile soundDirFile = logicalFileCache.getLogicalFileByName("SNDDIR");
        entries[0] = new ResourceDirectory(new ByteArrayInputStream(logicDirFile.getRawData()));
        entries[1] = new ResourceDirectory(new ByteArrayInputStream(pictureDirFile.getRawData()));
        entries[2] = new ResourceDirectory(new ByteArrayInputStream(viewDirFile.getRawData()));
        entries[3] = new ResourceDirectory(new ByteArrayInputStream(soundDirFile.getRawData()));
    }

    /**
     * Checks that the given resource type value is valid.
     *
     * @param resType The resource type value to validate.
     * @throws ResourceTypeInvalidException
     */
    protected void validateType(byte resType) throws ResourceTypeInvalidException {
        if ((resType > TYPE_WORD) || (resType < TYPE_LOGIC)) {
            throw new ResourceTypeInvalidException();
        }
    }

    /**
     * Calculate the CRC of the resources. In this implementation the CRC is not
     * calculated by this function, it only return the cached CRC value.
     *
     * @return CRC of the resources.
     */
    @Override
    public long getCRC() {
        return crc;
    }

    /**
     * Retrieve the count of resources of the specified type. Only valid with
     * Logic, Picture, Sound and View resource types.
     *
     * @param resType Resource type
     * @return Resource count.
     * @see com.sierra.agi.res.ResourceProvider#TYPE_LOGIC
     * @see com.sierra.agi.res.ResourceProvider#TYPE_PICTURE
     * @see com.sierra.agi.res.ResourceProvider#TYPE_SOUND
     * @see com.sierra.agi.res.ResourceProvider#TYPE_VIEW
     */
    @Override
    public int count(byte resType) throws ResourceException {
        validateType(resType);

        if (resType >= TYPE_OBJECT) {
            return 1;
        }

        return entries[resType].getCount();
    }

    /**
     * Enumerate the resource numbers of the specified type. Only valid with
     * Logic, Picture, Sound and View resource types.
     *
     * @param resType Resource type.
     * @return Array containing the resource numbers.
     * @see com.sierra.agi.res.ResourceProvider#TYPE_LOGIC
     * @see com.sierra.agi.res.ResourceProvider#TYPE_PICTURE
     * @see com.sierra.agi.res.ResourceProvider#TYPE_SOUND
     * @see com.sierra.agi.res.ResourceProvider#TYPE_VIEW
     */
    @Override
    public short[] enumerate(byte resType) throws ResourceException {
        validateType(resType);

        return entries[resType].getNumbers();
    }

    /**
     * Gets the LogicalFile that represents the request VOL.
     *
     * @param vol The VOL number to get the corresponding LogicalFile for.
     * @return The LogicalFile that represents the request VOL.
     * @throws IOException
     */
    protected LogicalFile getVolumeFile(int vol) throws IOException {
        String volFileName = "VOL." + vol;

        LogicalFile logicalFile = logicalFileCache.getLogicalFileByName(volFileName);

        if (logicalFile == null) {
            throw new FileNotFoundException("File " + volFileName + " can't be found.");
        }

        return logicalFile;
    }

    /**
     * Retrieve the size in bytes of the specified resource.
     *
     * @param resType   Resource type
     * @param resNumber Resource number
     * @return Returns the size in bytes of the specified resource.
     */
    @Override
    public int getSize(byte resType, short resNumber) throws ResourceException, IOException {
        int size = 0;

        switch (resType) {
            case ResourceProvider.TYPE_OBJECT:
                size = logicalFileCache.getLogicalFileByName("OBJECT").getLogicalFileSize();
                break;

            case ResourceProvider.TYPE_WORD:
                size = logicalFileCache.getLogicalFileByName("WORDS.TOK").getLogicalFileSize();
                break;

            default:
                try {
                    if (entries[resType] != null) {
                        int vol, offset;

                        vol = entries[resType].getVolume(resNumber);
                        offset = entries[resType].getOffset(resNumber);

                        if ((vol != -1) && (offset != -1)) {
                            LogicalFile logicalFile = getVolumeFile(vol);
                            size = logicalFile.getSubFileSize(offset);
                        } else {
                            throw new ResourceNotExistingException();
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    throw new ResourceTypeInvalidException();
                }
        }

        return size;
    }

    /**
     * Open the specified resource and return a pointer to the resource. The
     * InputStream is decrypted/uncompressed, if necessary, by this function.
     * (So you don't have to care about them.)
     *
     * @param resType   Resource type
     * @param resNumber Resource number
     * @return InputStream linked to the specified resource.
     */
    @Override
    public InputStream open(byte resType, short resNumber) throws ResourceException, IOException {
        InputStream inputStream = null;

        switch (resType) {
            case ResourceProvider.TYPE_OBJECT:
                LogicalFile logicalFile = logicalFileCache.getLogicalFileByName("OBJECT");
                inputStream = new ByteArrayInputStream(logicalFile.getRawData());
                break;

            case ResourceProvider.TYPE_WORD:
                inputStream = new ByteArrayInputStream(logicalFileCache.getLogicalFileByName("WORDS.TOK").getRawData());
                break;
        }

        if (inputStream == null) {
            try {
                if (entries[resType] != null) {
                    int vol = entries[resType].getVolume(resNumber);
                    int offset = entries[resType].getOffset(resNumber);

                    if ((vol != -1) && (offset != -1)) {
                        LogicalFile logicalFile = getVolumeFile(vol);
                        byte[] data = logicalFile.getSubFileRawData(offset);

                        if (resType == TYPE_LOGIC) {
                            int messagesStartPos = ByteCaster.lohiUnsignedShort(data, 0) + 2;
                            int numMessages = ByteCaster.lohiUnsignedByte(data, messagesStartPos);
                            int offsetCrypted = messagesStartPos + 3 + (numMessages * 2);
                            inputStream = new CryptedInputStream(new ByteArrayInputStream(data), SIERRA_KEY, offsetCrypted);

                        } else {
                            inputStream = new ByteArrayInputStream(data);
                        }
                    }
                }

            } catch (IndexOutOfBoundsException e) {
                throw new ResourceTypeInvalidException();
            }
        }

        if (inputStream == null) {
            throw new ResourceNotExistingException();
        } else {
            return inputStream;
        }
    }

    /**
     * Return the provider type. Used has a optimization hint by the resource
     * cache. (For example, PROVIDER_TYPE_SLOW would mean to never ask twice for
     * the same resource because transfer rate may be slow.)
     */
    @Override
    public byte getProviderType() {
        return PROVIDER_TYPE_FAST;
    }

    /**
     * Return the resource configuration.
     */
    @Override
    public ResourceConfiguration getConfiguration() {
        return configuration;
    }
}
