package com.agifans.jagi.appleii.kq2;

import com.sierra.agi.logic.LogicException;
import com.sierra.agi.pic.PictureException;
import com.sierra.agi.res.CorruptedResourceException;
import com.sierra.agi.res.ResourceException;
import com.sierra.agi.view.ViewException;

import java.io.*;

/**
 * A utility class for extracting LOGIC, PICTURE, VIEW, SOUND, WORDS, and OBJECT
 * files from the Apple II KQ2 disk images. It expects the files to be named a
 * certain way, and to be in the DOS order disk image format. If your disk image
 * files are in another format, such as .NIB, then use a tool such as CiderPress
 * to convert them to DOS order.
 *
 * @author Lance Ewing
 */
public class KQ2Extractor extends KQ2Tool {

    /**
     * Constructor KQ2Extractor.
     *
     * @param args The command line arguments.
     * @throws IOException
     * @throws CorruptedResourceException
     */
    public KQ2Extractor(String[] args) throws IOException, CorruptedResourceException {
        super(args);
    }

    public static void main(String[] args) throws IOException, ResourceException, PictureException, LogicException, ViewException {
        KQ2Extractor extractor = new KQ2Extractor(args);
        extractor.extractFiles();
        System.exit(0);
    }

    /**
     * Extracts all of the LOGIC, PICTURE, SOUND, and VIEW resources from the disk images
     * of the Apple II KQ2 game.
     *
     * @throws IOException
     * @throws ResourceException
     * @throws PictureException
     * @throws LogicException
     * @throws ViewException
     */
    public void extractFiles() throws IOException, ResourceException, PictureException, LogicException, ViewException {
        String[] RES_TYPE_FILE_NAMES = {"LOGIC.%03d", "PICTURE.%03d", "SOUND.%03d", "VIEW.%03d"};

        for (byte resType = 0; resType < 4; resType++) {
            for (short resNumber : resourceProvider.enumerate(resType)) {
                String resourceFileName = String.format(RES_TYPE_FILE_NAMES[resType], resNumber);
                FileOutputStream out = null;
                BufferedInputStream in = null;

                System.out.println("Extracting resource " + resourceFileName);

                try {
                    InputStream is = resourceProvider.open(resType, resNumber);
                    out = new FileOutputStream(new File(diskImageDirectory, resourceFileName));
                    in = new BufferedInputStream(is);

                    int n;
                    byte[] buffer = new byte[1024];
                    while ((n = in.read(buffer)) > -1) {
                        out.write(buffer, 0, n);
                    }

                } catch (Exception e) {
                    System.out.println("ERROR extracting resource " + resourceFileName);
                    e.printStackTrace(System.out);

                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
