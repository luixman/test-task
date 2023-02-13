package com.testtask.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserModel {
    Long id;
    String username;
    Instant createTime;
}

