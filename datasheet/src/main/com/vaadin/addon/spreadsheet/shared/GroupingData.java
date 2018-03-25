package com.vaadin.addon.spreadsheet.shared;

import java.io.Serializable;

/**
 * 数据分组
 * Shared state for the grouping feature
 */
public class GroupingData implements Serializable {

    public int startIndex;
    public int endIndex;
    public int level;
    /**
     * index unique for this group, for collapse/expand
     */
    public int uniqueIndex;
    public boolean collapsed;

    public GroupingData() {
    }

    public GroupingData(long start, long end, short level, long unique,
                        boolean coll) {
        this((int) start, (int) end, (int) level, (int) unique, coll);
    }

    public GroupingData(int start, int end, int level, int unique, boolean coll) {
        startIndex = start;
        endIndex = end;
        this.level = level;
        uniqueIndex = unique;
        collapsed = coll;
    }

}
