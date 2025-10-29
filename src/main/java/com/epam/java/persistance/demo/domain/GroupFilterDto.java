package com.epam.java.persistance.demo.domain;

public record GroupFilterDto(
        Boolean active,
        Boolean available,
        String studentEmail,
        Boolean orderedByCount
) {}
