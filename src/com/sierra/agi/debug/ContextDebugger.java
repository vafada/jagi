/**
 * ContextDebugger.java
 * Adventure Game Interpreter Debug Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug;

import com.sierra.agi.debug.logic.LogicComponent;
import com.sierra.agi.logic.debug.LogicContextDebug;
import com.sierra.agi.logic.debug.LogicContextEvent;
import com.sierra.agi.logic.debug.LogicContextListener;
import com.sierra.agi.logic.debug.LogicStackEntry;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

public class ContextDebugger extends JFrame implements LogicContextListener, ActionListener, ItemListener, TreeSelectionListener {
    protected JComboBox stackCombo;
    protected boolean stackChanging;
    protected JTable watchTable;

    protected LogicContextDebug logicContext;
    protected LogicComponent logicComponent;

    protected VariableTableModel variableModel;
    protected DefaultTableModel flagModel;

    public ContextDebugger(LogicContextDebug logicContext) {
        super("Adventure Game Debugger");

        logicComponent = new LogicComponent(logicContext.getCache());
        variableModel = new VariableTableModel(logicContext, logicComponent);
        flagModel = new DefaultTableModel(new Object[256][2], new String[]{"Flag", "Value"});

        JSplitPane bottomPane = new JSplitPane();
        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JScrollPane scroll = new JScrollPane(logicComponent);

        scroll.setPreferredSize(new Dimension(600, 100));

        bottomPane.add(getStack(), JSplitPane.LEFT);
        bottomPane.add(getWatches(), JSplitPane.RIGHT);

        pane.add(scroll, JSplitPane.TOP);
        pane.add(bottomPane, JSplitPane.BOTTOM);

        getContentPane().add(pane);

        setMenuBar(addMenu());
        pack();

        this.logicContext = logicContext;
        this.logicContext.addLogicContextListener(this);
    }

    protected Component getStack() {
        JTree tree = generateTree();
        Container container = new JPanel();
        JScrollPane pane = new JScrollPane(tree);

        stackCombo = new JComboBox();
        stackCombo.setEditable(false);
        stackCombo.addItemListener(this);

        pane.setPreferredSize(new Dimension(300, 100));

        container.setLayout(new BorderLayout());
        container.add(stackCombo, BorderLayout.NORTH);
        container.add(pane, BorderLayout.CENTER);

        stackChanging = true;
        stackCombo.removeAllItems();
        stackCombo.addItem("<Not Running>");
        stackChanging = false;

        tree.addTreeSelectionListener(this);
        return container;
    }

    protected Component getWatches() {
        JScrollPane pane;

        watchTable = new JTable();
        pane = new JScrollPane(watchTable);

        pane.setPreferredSize(new Dimension(300, 100));
        return pane;
    }

    protected MenuBar addMenu() {
        MenuBar menubar = new MenuBar();
        Menu menu;
        MenuItem item;

        menu = new Menu("Debug");
        item = new MenuItem("Continue");
        item.setActionCommand("run");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_R, true));
        menu.add(item);

        item = new MenuItem("Pause");
        item.setActionCommand("pause");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_P, true));
        menu.add(item);
        menu.addSeparator();

        item = new MenuItem("Step Out");
        item.setActionCommand("stepout");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_O, true));
        menu.add(item);

        item = new MenuItem("Step Into");
        item.setActionCommand("stepinto");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_I, true));
        menu.add(item);

        item = new MenuItem("Step Over");
        item.setActionCommand("stepover");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_T, true));
        menu.add(item);
        menubar.add(menu);

        return menubar;
    }

    public void logicBreakpointReached(LogicContextEvent ev) {
        Object[] stack = logicContext.getLogicStack();
        int i;

        stackChanging = true;
        stackCombo.removeAllItems();
        for (i = 0; i < stack.length; i++) {
            stackCombo.addItem(stack[i]);
        }

        stackCombo.setSelectedItem(stack[i - 1]);

        LogicStackEntry entry = (LogicStackEntry) stack[i - 1];

        logicComponent.setLogic(entry.logic);
        logicComponent.setInstructionNumber(entry.in);
        stackChanging = false;
    }

    public void logicResumed(LogicContextEvent ev) {
        stackChanging = true;
        stackCombo.removeAllItems();
        stackCombo.addItem("<Running>");
        stackChanging = false;
    }

    public void itemStateChanged(ItemEvent ev) {
        if (!stackChanging) {
            if (ev.getStateChange() == ItemEvent.SELECTED) {
                Object o = ev.getItem();

                if (o instanceof LogicStackEntry entry) {

                    logicComponent.setLogic(entry.logic);
                    logicComponent.setInstructionNumber(entry.in);
                }
            }
        }
    }

    public void actionPerformed(ActionEvent ev) {
        String s = ev.getActionCommand();

        if (s.equals("run")) {
            logicContext.resumeExecution();
        } else if (s.equals("pause")) {
            logicContext.breakExecution();
        } else if (s.equals("stepinto")) {
            logicContext.stepIntoExecution();
        } else if (s.equals("stepout")) {
            logicContext.stepOutExecution();
        } else if (s.equals("stepover")) {
            logicContext.stepOverExecution();
        }
    }

    protected JTree generateTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Environment", true);
        DefaultMutableTreeNode node;
        JTree tree;

        node = new DefaultMutableTreeNode("Variables", false);
        root.add(node);

        node = new DefaultMutableTreeNode("Flags", false);
        root.add(node);

        node = new DefaultMutableTreeNode("Inventory Objects", false);
        root.add(node);

        node = new DefaultMutableTreeNode("Logic Entry Points", false);
        root.add(node);

        //addMouseListener(this);

        tree = new JTree(root, true);
        //tree.addMouseListener(this);

        return tree;
    }

    public void valueChanged(TreeSelectionEvent ev) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) ev.getPath().getLastPathComponent();
        String name = node.toString();

        if (name.equals("Variables")) {
            watchTable.setModel(variableModel);
        } else if (name.equals("Flags")) {
            watchTable.setModel(flagModel);
        }
    }

    @Override
    public void variableChanged(LogicContextEvent ev) {
        if (ev.getVariableNumber() != -1) {
            variableModel.fireTableCellUpdated(ev.getVariableNumber(), 1);
        }
    }

    @Override
    public void flagChanged(LogicContextEvent ev) {
        boolean[] flags = logicContext.getFlags();
        for (int i = 0; i < flags.length; i++) {
            String flagName = logicComponent.getLogicEvaluator().getFlagTokenMappings(i);
            boolean val = flags[i];
            flagModel.setValueAt(flagName, i, 0);
            flagModel.setValueAt(String.valueOf(val), i, 1);
        }
    }
}
