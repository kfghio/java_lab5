package itmo.dev.owner_microservice.mapper;

import itmo.dev.common.models.Owner;
import itmo.dev.common.dto.OwnerDto;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerMapper {

    private CatMapper catMapper = new CatMapper();

    public OwnerDto ownerToDto(Owner owner) {

        return OwnerDto.builder()
                .name(owner.getName())
                .birthDate(owner.getBirthDate())
                .catsList(owner.getCatsList() == null ? null : owner.getCatsList().stream()
                        .map(tmp -> catMapper.catToDto(tmp))
                        .toList())
                .build();
    }

    public Owner ownerToModel(OwnerDto ownerDto) {

        return Owner.builder()
                .name(ownerDto.getName())
                .birthDate(ownerDto.getBirthDate())
                .catsList(ownerDto.getCatsList() == null ? null : ownerDto.getCatsList().stream()
                        .map(tmp -> catMapper.catToModel(tmp))
                        .toList())
                .build();
    }
}
