package com.skybook.training;

/**
 * TRAINING LAB ONLY — not production-secure.
 * Controllers in this package load exclusively under Spring profile {@code training}.
 */
public final class TrainingMarkers {
    public static final String NOTICE = "TRAINING ONLY — intentionally vulnerable demo. Not for production.";
    public static final String PROFILE = "training";

    private TrainingMarkers() {
    }
}
