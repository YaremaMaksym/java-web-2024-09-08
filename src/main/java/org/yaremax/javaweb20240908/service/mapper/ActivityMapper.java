package org.yaremax.javaweb20240908.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.yaremax.javaweb20240908.dto.ActivityDto;
import org.yaremax.javaweb20240908.entity.Activity;

import java.util.List;

@Mapper
public interface ActivityMapper {
    ActivityMapper INSTANCE = Mappers.getMapper(ActivityMapper.class);

    Activity toEntity(ActivityDto dto);

    @Mapping(source = "user.id", target = "userId")
    ActivityDto toDto(Activity entity);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    List<ActivityDto> toDtoList(List<Activity> entityList);
}
