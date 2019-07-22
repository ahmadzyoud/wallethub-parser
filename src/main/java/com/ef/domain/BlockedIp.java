package com.ef.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "BLOCkED_IP")
@Getter
@Setter
@ToString
public class BlockedIp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column
    private String ip;

    @Column
    private String blockReason;

    @Column
    private Long accessCount;


}
