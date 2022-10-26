package com.agifans.jagi.appleii.kq2;

import com.sierra.agi.io.ByteCasterStream;
import com.sierra.agi.io.IOUtils;
import com.sierra.agi.word.Words;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads and interprets the Apple II KQ2 words data, which is a bit simpler than the
 * AGI v2 games. The words appear in there entirety rather than referring to parts of
 * the previous word, and the word numbers use LO-HI rather than HI-LO. Also the text
 * is not XORed with 0x7F but instead appears in its ASCII form.
 *
 * @author Lance Ewing
 */
public class KQ2WordProvider extends Words {

    /**
     * Read an AGI words table from the given InputStream.
     *
     * @param stream InputStream from where to read the words.
     * @return Returns the number of words read.
     */
    protected int loadWordTable(InputStream stream) throws IOException {
        ByteCasterStream bstream = null;

        try {
            String curr;
            int i, wordNum, wordCount;

            bstream = new ByteCasterStream(stream);

            // Skips the 26 pointers to the words that start with each letter.
            IOUtils.skip(stream, 52);

            wordCount = 0;

            while (true) {
                curr = "";

                while (true) {
                    i = stream.read();

                    // 0x00 ends the text for a word.
                    if ((i == 0) || (i == 0xFF)) {
                        break;

                    } else {
                        curr += (char) (i & 0x7F);
                    }
                }

                // 0xFF ends the WORDS data.
                if (i == 0xFF) {
                    break;
                }

                wordNum = bstream.lohiReadUnsignedShort();

                addWord(wordNum++, curr);
            }

            return wordCount;

        } finally {
            if (bstream != null) {
                try {
                    bstream.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
