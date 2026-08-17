package com.example.shopapi.user.facades;

import com.example.shopapi.user.dto.AddressRequest;
import com.example.shopapi.user.dto.AddressResponse;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.entities.UserAddress;
import com.example.shopapi.order.mappers.AddressMapper;
import com.example.shopapi.auth.services.CurrentUserService;
import com.example.shopapi.user.services.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressFacade {

    private final CurrentUserService currentUserService;
    private final AddressService addressService;
    private final AddressMapper mapper;

    public List<AddressResponse> getAll(){
        User user = currentUser();

        return addressService
                .getUserAddresses(user.getId())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public AddressResponse create(
            AddressRequest request
    ){
        User user = currentUser();
        UserAddress address = mapper.toEntity(request);

        log.info("User address is created");

        return mapper.toResponse(
                addressService.create(
                        user,
                        address
                )
        );
    }

    public AddressResponse update(
            Long id,
            AddressRequest request
    ) {
        UserAddress address = getAddress(id);

        log.info("User address is updated");

        return mapper.toResponse(
                addressService.update(
                        address,
                        request
                )
        );
    }

    public void delete(Long id){
        UserAddress address = getAddress(id);
        addressService.delete(address);

        log.info("User address is deleted");
    }

    public void makePrimary(Long id){
        UserAddress address = getAddress(id);
        addressService.setPrimary(address);

        log.info("User address is made primary");
    }

    private User currentUser() {
        return currentUserService.getCurrentUserEntity();
    }

    private UserAddress getAddress(
            Long addressId
    ) {
        User user = currentUser();

        return addressService.getUserAddress(
                user.getId(),
                addressId
        );
    }
}