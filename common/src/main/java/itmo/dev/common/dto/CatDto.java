package itmo.dev.common.dto;

import itmo.dev.common.enums.Breed;
import itmo.dev.common.enums.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatDto {

    private String name;
    private LocalDate birthDate;
    private Breed breed;
    private Color color;
    private List<CatDto> catFriends;
}
