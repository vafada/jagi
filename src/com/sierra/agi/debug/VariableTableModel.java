package com.sierra.agi.debug;

import com.sierra.agi.debug.logic.LogicComponent;
import com.sierra.agi.logic.debug.LogicContextDebug;

import javax.swing.table.AbstractTableModel;

public class VariableTableModel extends AbstractTableModel {
    private LogicContextDebug logicContext;
    private LogicComponent logicComponent;

    public VariableTableModel(LogicContextDebug logicContext, LogicComponent logicComponent) {
        this.logicComponent = logicComponent;
        this.logicContext = logicContext;
    }

    @Override
    public int getRowCount() {
        return this.logicContext.getVars().length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return logicComponent.getLogicEvaluator().getVariableTokenMappings(rowIndex);
        }
        return this.logicContext.getVar((short) rowIndex);
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
            return "Variable";
        }

        return "Value";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            try {
                Short shortObj = Short.valueOf(aValue.toString());
                this.logicContext.setVar((short) rowIndex, shortObj);
            } catch (Exception e) {

            }
        }
    }
}
