package com.testtask.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class QuoteModel {
    Long id;
    Integer score;
    String content;
    Instant date;
    String creator;
}
