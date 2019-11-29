package com.miro.widget.api.model.entity;

import com.miro.widget.api.model.dto.PageableDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Collection;
import java.util.Collections;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {

    @NonNull
    private Collection<T> items = Collections.emptyList();

    @NonNull
    private long number;

    @NonNull
    private long size;

    @NonNull
    private long totalItems;

    public long getAvailableCount() {
        return size == 0
                ? 0
                : (long) Math.ceil((double) totalItems / (double) size);
    }

    public int getItemCount() {
        return items.size();
    }

    public static <T> Page<T> createEmptyPage(PageableDto meta, long totalItemsCount) {
        return new Page<T>(Collections.emptyList(), meta.getPage(), meta.getSize(), totalItemsCount);
    }

    public static <T> Page<T> createPage(Collection<T> items, PageableDto meta, long totalItemsCount) {
        return new Page<T>(items, meta.getPage(), meta.getSize(), totalItemsCount);
    }

    public static <T> Page<T> createPageBy(Page other, Collection<T> items) {
        return new Page<T>(items, other.number, other.size, other.totalItems);
    }
}
