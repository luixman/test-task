package com.testtask.Entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer score;
    private String content;
    private Instant date;


    @JoinColumn(name = "user_id")
    @ManyToOne()
    private User user;
}
