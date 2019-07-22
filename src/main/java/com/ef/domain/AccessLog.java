package com.ef.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ACCESS_LOG")
@Getter
@Setter
@ToString
public class AccessLog {

    @Id
    private Long id;


    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date accessDate;


    @Column
    private String ip;

    @Column
    private String request;

    @Column
    private Integer status;

    @Column
    private String userAgent;


}
