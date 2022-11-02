/**
 * WordViewer.java
 * Adventure Game Interpreter Debug Package
 * <p>
 * Created by Dr. Z
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug;

import com.sierra.agi.word.Word;
import com.sierra.agi.word.Words;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;

public class WordViewer extends JFrame implements ListSelectionListener {
    protected Words words;

    protected JList wordList;
    protected JList synonymList;
    protected JTextField parseField;

    protected Word[] wordso;
    protected DefaultListModel synonyms = new DefaultListModel();

    public WordViewer(Words words) {
        super("Words");
        this.words = words;

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        Container cont = getContentPane();
        JLabel label;
        JScrollPane pane;

        cont.setLayout(gridBag);

        label = new JLabel("Words:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(4, 4, 0, 0);
        gridBag.setConstraints(label, c);
        cont.add(label);

        label = new JLabel("Synonym(s):");
        c.gridx = 1;
        c.insets = new Insets(4, 0, 0, 0);
        gridBag.setConstraints(label, c);
        cont.add(label);

        wordList = new JList(wordso = loadWords());
        wordList.addListSelectionListener(this);
        wordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pane = new JScrollPane(wordList);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(4, 4, 4, 4);
        gridBag.setConstraints(pane, c);
        cont.add(pane);

        synonymList = new JList(synonyms);
        pane = new JScrollPane(synonymList);
        c.gridx = 1;
        c.insets = new Insets(4, 0, 4, 4);
        gridBag.setConstraints(pane, c);
        cont.add(pane);
        
/* Actually the Text Parser is not yet done.
        parseField  = new JTextField();
        c.gridx     = 0;
        c.gridy     = 2;
        c.gridwidth = 2;
        c.weightx   = 0.0;
        c.weighty   = 0.0;
        c.fill      = GridBagConstraints.HORIZONTAL;
        c.insets    = new Insets(0, 4, 4, 4);
        gridBag.setConstraints(parseField, c);
        cont.add(parseField);
*/
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent ev) {
                WordViewer.this.words = null;
                WordViewer.this.wordso = null;
            }
        });

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    }

    protected Word[] loadWords() {
        Collection<Word> allWords = words.words();
        Word[] o = new Word[words.getWordCount()];

        int i =0;
        for (Word word : allWords) {
            o[i] = word;
            i++;
        }

        Arrays.sort(o);
        return o;
    }

    public void valueChanged(ListSelectionEvent ev) {
        Word y, w = wordso[wordList.getSelectedIndex()];
        int i;

        synonyms.clear();

        for (i = 0; i < wordso.length; i++) {
            y = wordso[i];
            if ((y.number == w.number) && (y != w)) {
                synonyms.addElement(y);
            }
        }
    }
}
