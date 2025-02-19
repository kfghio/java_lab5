package itmo.dev.cat_microservice.services.interfaces;

import itmo.dev.common.dto.CatDto;

import java.util.List;

public interface CatService {

    CatDto createCat(CatDto catDto, Integer ownerId);
    List<CatDto> getAllCats();
    CatDto getCatById(Integer ownerId, Integer catId);
    List<CatDto> getCatsByOwnerId(Integer ownerId);
    CatDto updateCat(Integer ownerId, Integer catId, CatDto catDto);
    void addCatFriend(Integer catId, Integer friendId);
    void deleteCat(Integer ownerId, Integer catId);
}
