package com.auca.studentportal.dto;

import lombok.Data;
import java.util.List;

@Data
public class PagedResponse<T> {
    private List<T> data;
    private int page;
    private int pageSize;
    private int totalPages;
    private long totalElements;
}
