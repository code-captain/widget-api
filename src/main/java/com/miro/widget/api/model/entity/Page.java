package com.miro.widget.api.model.entity;

import lombok.*;

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
}
