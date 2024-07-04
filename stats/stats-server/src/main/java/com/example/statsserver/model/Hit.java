package com.example.statsserver.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "hits")
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String app;

    private String uri;
    @Column(name = "ip_address")
    private String ipAddress;

    private LocalDateTime timestamp;
}