package com.testtask.model;

import lombok.Getter;

@Getter
public enum OperationType {
    LIKE("+1"),
    LIKE_REMOVE("-1"),
    DISLIKE("-1"),
    DISLIKE_REMOVE("+1"),
    NEUTRAL("0");

    private final String operation;

    OperationType(String operation) {
        this.operation = operation;
    }
}
