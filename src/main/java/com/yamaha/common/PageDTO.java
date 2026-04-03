package com.yamaha.common;

import lombok.Data;

@Data
public class PageDTO {

    private Long pageNum = 1L;
    private Long pageSize = 10L;
}
