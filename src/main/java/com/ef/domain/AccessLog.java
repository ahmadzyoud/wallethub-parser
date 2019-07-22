package com.ef.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACCESS_LOG")
@Getter
@Setter
@ToString
public class AccessLog {

    @Id
    /*@GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "native"
    )
    @GenericGenerator(
            name = "native",
            strategy = "native"
    )*/
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
