package org.blackequity.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryDto {

    private Long id;
    private String name;

    public CategoryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
