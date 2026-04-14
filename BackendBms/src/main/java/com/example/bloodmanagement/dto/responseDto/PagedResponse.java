package com.example.bloodmanagement.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {
    private List<T> data;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
