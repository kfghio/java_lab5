package itmo.dev.cat_microservice.mapper;

import itmo.dev.common.models.Cat;
import itmo.dev.common.dto.CatDto;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class CatMapper {

    public CatDto catToDto(Cat cat) {

        return CatDto.builder()
                .name(cat.getName())
                .birthDate(cat.getBirthDate())
                .breed(cat.getBreed())
                .color(cat.getColor())
                .catFriends(cat.getCatFriends() == null ? null : cat.getCatFriends().stream()
                        .map(tmp -> catToDto(tmp))
                        .toList())
                .build();
    }

    public Cat catToModel(CatDto catDto) {

        return Cat.builder()
                .name(catDto.getName())
                .birthDate(catDto.getBirthDate())
                .breed(catDto.getBreed())
                .color(catDto.getColor())
                .catFriends(catDto.getCatFriends() == null ? null : catDto.getCatFriends().stream()
                        .map(tmp -> catToModel(tmp))
                        .toList())
                .build();
    }
}
