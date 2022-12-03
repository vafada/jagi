package com.sierra.agi.debug;

import com.sierra.agi.debug.logic.LogicComponent;
import com.sierra.agi.inv.InventoryObjects;
import com.sierra.agi.logic.debug.LogicContextDebug;

import javax.swing.table.AbstractTableModel;

public class InventoryTableModel extends AbstractTableModel {
    private final LogicContextDebug logicContext;
    private final LogicComponent logicComponent;

    public InventoryTableModel(LogicContextDebug logicContext, LogicComponent logicComponent) {
        this.logicComponent = logicComponent;
        this.logicContext = logicContext;
    }

    @Override
    public int getRowCount() {
        return this.logicContext.getObjects().length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (columnIndex == 0) {
            try {
                InventoryObjects objects = this.logicContext.getCache().getObjects();
                return objects.getObject((short) rowIndex).getName();
            } catch (Exception e) {
                return String.valueOf(rowIndex);
            }
        }
        int location = this.logicContext.getObject((short) rowIndex);
        if (location == 0) {
            try {
                InventoryObjects objects = this.logicContext.getCache().getObjects();
                location = objects.getObject((short) rowIndex).getLocation();
            } catch (Exception e) {

            }
        }

        return location;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Object";
        }

        return "Location";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            try {
                Short shortObj = Short.valueOf(aValue.toString());
                this.logicContext.setObject((short) rowIndex, shortObj);
            } catch (Exception e) {

            }
        }
    }
}

