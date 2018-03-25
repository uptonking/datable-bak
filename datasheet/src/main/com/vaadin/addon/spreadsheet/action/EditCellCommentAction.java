package com.vaadin.addon.spreadsheet.action;

import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.addon.spreadsheet.Spreadsheet.SelectionChangeEvent;

/**
 * 编辑单元格注释
 * Spreadsheet action for inserting or deleting a comment to a cell.
 *
 * @author Vaadin Ltd.
 * @since 1.0
 */
@SuppressWarnings("serial")
public class EditCellCommentAction extends SpreadsheetAction {

    public EditCellCommentAction() {
        super("Edit comment");
    }

    @Override
    public boolean isApplicableForSelection(Spreadsheet spreadsheet, SelectionChangeEvent event) {
        if (!spreadsheet.getActiveSheet().getProtect()) {
            if (event.getCellRangeAddresses().size() == 0
                    && event.getIndividualSelectedCells().size() == 0) {
                CellReference cr = event.getSelectedCellReference();
                Comment cellComment = spreadsheet.getActiveSheet()
                        .getCellComment(new CellAddress(cr.getRow(), cr.getCol()));
                if (cellComment != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isApplicableForHeader(Spreadsheet spreadsheet,
                                         CellRangeAddress headerRange) {
        return false;
    }

    @Override
    public void executeActionOnSelection(Spreadsheet spreadsheet,
                                         SelectionChangeEvent event) {
        CellReference cr = event.getSelectedCellReference();
        spreadsheet.editCellComment(cr);
    }

    @Override
    public void executeActionOnHeader(Spreadsheet spreadsheet,
                                      CellRangeAddress headerRange) {
        throw new UnsupportedOperationException(
                "Cell comment actions can't be executed against a header range.");
    }

}
