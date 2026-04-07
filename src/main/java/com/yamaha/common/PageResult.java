package com.yamaha.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;
    private long total;

    public PageResult(List<T> records, long total) {
        this.records = records;
        this.total = total;
    }
}