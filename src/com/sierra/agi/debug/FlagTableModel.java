package com.sierra.agi.debug;

import com.sierra.agi.debug.logic.LogicComponent;
import com.sierra.agi.logic.debug.LogicContextDebug;

import javax.swing.table.AbstractTableModel;

public class FlagTableModel extends AbstractTableModel {
    private final LogicContextDebug logicContext;
    private final LogicComponent logicComponent;

    public FlagTableModel(LogicContextDebug logicContext, LogicComponent logicComponent) {
        this.logicComponent = logicComponent;
        this.logicContext = logicContext;
    }

    @Override
    public int getRowCount() {
        return this.logicContext.getFlags().length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return logicComponent.getLogicEvaluator().getFlagTokenMappings(rowIndex);
        }
        return this.logicContext.getFlag((short) rowIndex);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Flag";
        }

        return "Value";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            boolean flagVal = (Boolean) aValue;
            this.logicContext.setFlag((short) rowIndex, flagVal);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return String.class;
        }
        return Boolean.class;
    }
}

