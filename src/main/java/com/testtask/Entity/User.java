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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private Instant createTime;
    private String roles;

}
