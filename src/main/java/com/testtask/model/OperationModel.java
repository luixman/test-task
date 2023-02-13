package com.testtask.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperationModel {
    Long quoteId;
    Long userId;

    OperationType operation;
}
