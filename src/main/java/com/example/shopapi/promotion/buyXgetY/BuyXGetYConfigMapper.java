package com.example.shopapi.promotion.buyXgetY;

import com.example.shopapi.promotion.buyXgetY.dto.BuyXGetYConfigRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BuyXGetYConfigMapper {


    BuyXGetYActionConfig toEntity(
            BuyXGetYConfigRequest request
    );

    void updateEntity(
            BuyXGetYConfigRequest request,
            @MappingTarget BuyXGetYActionConfig entity
    );

}