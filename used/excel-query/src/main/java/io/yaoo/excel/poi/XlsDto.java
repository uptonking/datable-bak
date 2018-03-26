package io.yaoo.excel.poi;

/**
 * 实体类
 */
public class XlsDto {
    private String timeColumn;
    private String columnA;
    private String columnB;

    public String getTimeColumn() {
        return timeColumn;
    }

    public void setTimeColumn(String timeColumn) {
        this.timeColumn = timeColumn;
    }

    public String getColumnA() {
        return columnA;
    }

    public void setColumnA(String columnA) {
        this.columnA = columnA;
    }

    public String getColumnB() {
        return columnB;
    }

    public void setColumnB(String columnB) {
        this.columnB = columnB;
    }

    public XlsDto() {
    }

    public XlsDto(String timeColumn, String columnA, String columnB) {
        super();
        this.timeColumn = timeColumn;
        this.columnA = columnA;
        this.columnB = columnB;
    }

    @Override
    public String toString() {
        return "XlsDto [timeColumn=" + timeColumn + ", columnA=" + columnA
                + ", columnB=" + columnB + "]";
    }
}
