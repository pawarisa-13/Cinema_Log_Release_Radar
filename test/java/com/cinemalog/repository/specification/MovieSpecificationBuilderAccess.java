package com.cinemalog.repository.specification;

public final class MovieSpecificationBuilderAccess {

    private MovieSpecificationBuilderAccess() {
    }

    public static int size(MovieSpecificationBuilder builder) {
        return builder.size();
    }
}
