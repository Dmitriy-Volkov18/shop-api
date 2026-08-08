package com.example.shopapi.shipment;

import com.example.shopapi.shipment.dto.ShipRequest;
import com.example.shopapi.shipment.dto.ShipmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentFacade facade;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ShipmentResponse get(
            @PathVariable Long id
    ){
        return facade.get(id);
    }

    @PatchMapping("/{id}/process")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void process(
            @PathVariable Long id
    ){
        facade.process(id);
    }

    @PatchMapping("/{id}/ship")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void ship(
            @PathVariable Long id,

            @Valid
            @RequestBody ShipRequest request
    ){
        facade.ship(
                id,
                request
        );
    }

    @PatchMapping("/{id}/deliver")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deliver(
            @PathVariable Long id
    ){
        facade.deliver(id);
    }

}
