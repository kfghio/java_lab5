package itmo.dev.owner_microservice.services.interfaces;

import itmo.dev.common.dto.OwnerDto;

import java.util.List;

public interface OwnerService {

    OwnerDto createOwner(OwnerDto ownerDto);
    List<OwnerDto> getAllOwners();
    OwnerDto getOwnerById(Integer id);
    OwnerDto updateOwner(OwnerDto ownerDto, Integer id);
    void deleteOwner(Integer id);
}
