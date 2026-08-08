package com.example.shopapi.user.controllers;

import com.example.shopapi.user.facades.AddressFacade;
import com.example.shopapi.user.dto.AddressRequest;
import com.example.shopapi.user.dto.AddressResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressFacade facade;

    @GetMapping
    public List<AddressResponse> getAll(){
        return facade.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse create(
            @Valid
            @RequestBody AddressRequest request
    ){
        return facade.create(request);
    }

    @PutMapping("/{id}")
    public AddressResponse update(
            @PathVariable Long id,

            @Valid
            @RequestBody AddressRequest request
    ){
        return facade.update(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ){
        facade.delete(id);
    }

    @PatchMapping("/{id}/primary")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void primary(
            @PathVariable Long id
    ){
        facade.makePrimary(id);
    }
}