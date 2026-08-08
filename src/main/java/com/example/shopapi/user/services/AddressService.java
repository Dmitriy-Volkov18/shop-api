package com.example.shopapi.user.services;

import com.example.shopapi.user.dto.AddressRequest;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.entities.UserAddress;
import com.example.shopapi.common.exception.AddressNotFoundException;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.order.mappers.AddressMapper;
import com.example.shopapi.user.repositories.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final UserAddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public List<UserAddress> getUserAddresses(
            Long userId
    ) {
        return addressRepository.findByUserId(userId);
    }

    public UserAddress getUserAddress(
            Long userId,
            Long addressId
    ) {
        return addressRepository
                .findByIdAndUserId(
                        addressId,
                        userId
                )
                .orElseThrow(() ->
                        new AddressNotFoundException(addressId)
                );
    }

    public UserAddress create(
            User user,
            UserAddress address
    ) {
        address.setUser(user);

        if (addressRepository
                .findByUserIdAndPrimaryAddressTrue(
                        user.getId()
                )
                .isEmpty()) {
            address.setPrimaryAddress(true);
        }

        return addressRepository.save(address);
    }

    public UserAddress update(
            UserAddress address,
            AddressRequest request
    ) {
        addressMapper.update(
                request,
                address
        );

        return addressRepository.save(address);
    }

    private void ensureNotPrimary(
            UserAddress address
    ) {

        if(address.isPrimaryAddress()) {

            throw new BadRequestException(
                    "Cannot delete primary address"
            );
        }
    }

    public void delete(
            UserAddress address
    ) {
        ensureNotPrimary(address);
        addressRepository.delete(address);
    }

    public void setPrimary(
            UserAddress addressToMakePrimary
    ) {
        if (addressToMakePrimary.isPrimaryAddress()) {
            return;
        }

        List<UserAddress> addresses =
                addressRepository.findByUserId(
                        addressToMakePrimary
                                .getUser()
                                .getId()
                );

        addresses.forEach(address ->
                address.setPrimaryAddress(
                        address.equals(addressToMakePrimary)
                )
        );
    }

    @Transactional(readOnly = true)
    public UserAddress getPrimaryAddress(
            User user
    ) {
        return addressRepository
                .findByUserIdAndPrimaryAddressTrue(
                        user.getId()
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "Primary address not found"
                        )
                );
    }
}