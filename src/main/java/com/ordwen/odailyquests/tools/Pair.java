package com.ordwen.odailyquests.tools;

public record Pair<F, S>(F first, S second) {

    /**
     * Get the first element of the pair.
     *
     * @return first element
     */
    @Override
    public F first() {
        return first;
    }

    /**
     * Get the second element of the pair.
     *
     * @return second element
     */
    @Override
    public S second() {
        return second;
    }

    /**
     * Convert the pair to a string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}

