package com.example.shopapi.returnProducts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnFilter {

    private ReturnStatus status;
    private Boolean active;
    private Boolean finished;

}