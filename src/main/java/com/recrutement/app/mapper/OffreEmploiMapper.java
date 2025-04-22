package com.recrutement.app.mapper;

import com.recrutement.app.dto.OffreEmploiDto;
import com.recrutement.app.model.OffreEmploi;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OffreEmploiMapper {
    OffreEmploiDto toDto(OffreEmploi entity);
}
