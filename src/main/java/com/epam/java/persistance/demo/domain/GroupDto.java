package com.epam.java.persistance.demo.domain;

import java.time.LocalDate;
import java.util.Set;

public record GroupDto(
        Long id,
        String name,
        String code,
        LocalDate startDate,
        LocalDate endDate,
        Integer maxCapacity,
        String description,
        Set<Long> studentIds,
        Integer currentEnrollment
) {}
