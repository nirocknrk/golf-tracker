package com.rapsodo.golftracker.domain.model

/** Golfer's dominant hand — drives equipment selection logic. */
enum class Handedness {
    RIGHT, LEFT;

    companion object {
        fun fromString(value: String): Handedness =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: RIGHT
    }
}
