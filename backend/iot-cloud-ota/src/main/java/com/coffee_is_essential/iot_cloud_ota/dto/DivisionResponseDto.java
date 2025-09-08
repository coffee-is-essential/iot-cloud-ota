package com.coffee_is_essential.iot_cloud_ota.dto;

import com.coffee_is_essential.iot_cloud_ota.entity.Division;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DivisionResponseDto {
    @JsonProperty("groupId")
    private Long id;
    @JsonProperty("groupCode")
    private String code;
    @JsonProperty("groupName")
    private String name;

    public static DivisionResponseDto from(Division d) {
        return new DivisionResponseDto(d.getId(), d.getDivisionCode(), d.getDivisionName());
    }
}
