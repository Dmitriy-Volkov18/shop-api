package com.example.shopapi.common;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false, length = 500)
    protected String imageUrl;

    @Column(nullable = false)
    protected String fileName;

    @Column(nullable = false)
    protected String contentType;

    @Column(nullable = false)
    protected Long fileSize;

    private Integer width;

    private Integer height;

    @Column(nullable = false)
    protected Integer sortOrder;

}