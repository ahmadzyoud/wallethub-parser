package com.ef.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACCESS_LOG")
@Getter
@Setter
@ToString
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column
    private LocalDateTime accessDate;


    @Column
    private String ip;

    @Column
    private String request;

    @Column
    private Integer status;

    @Column
    private String userAgent;


}
