package com.example.shopapi.product.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrendingWarmup {

    private final TrendingRebuildJob rebuildJob;

    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        rebuildJob.rebuild();
    }
}