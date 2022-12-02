package com.sierra.agi.debug;

import com.sierra.agi.debug.logic.LogicComponent;
import com.sierra.agi.logic.debug.LogicContextDebug;

import javax.swing.table.AbstractTableModel;

public class FlagTableModel extends AbstractTableModel {
    private LogicContextDebug logicContext;
    private LogicComponent logicComponent;

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
        if (column == 0) {
            return false;
        }
        return true;
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
            try {
                String stringVal = aValue.toString();
                boolean flagVal = false;
                if ("true".equalsIgnoreCase(stringVal) || "t".equalsIgnoreCase(stringVal)) {
                    flagVal = true;
                }
                this.logicContext.setFlag((short) rowIndex, flagVal);
            } catch (Exception e) {

            }
        }
    }
}

