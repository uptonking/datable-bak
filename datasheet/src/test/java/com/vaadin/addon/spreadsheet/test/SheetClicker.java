package com.vaadin.addon.spreadsheet.test;

/**
 * 工作表点击 接口
 */
public interface SheetClicker {

    public void clickCell(String cell);

    public void clickRow(int row);

    public void clickColumn(String column);
}
