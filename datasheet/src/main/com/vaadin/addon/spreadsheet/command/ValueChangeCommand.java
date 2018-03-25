package com.vaadin.addon.spreadsheet.command;

import java.util.Set;

import org.apache.poi.ss.util.CellReference;

/**
 * 单元格值修改命令 接口
 * Common interface for all Spreadsheet commands that change cell values.
 *
 * @author Vaadin Ltd.
 * @since 1.0
 */
public interface ValueChangeCommand extends Command {

    /**
     * Returns the cells that had their value(s) changed.
     */
    public Set<CellReference> getChangedCells();

}
