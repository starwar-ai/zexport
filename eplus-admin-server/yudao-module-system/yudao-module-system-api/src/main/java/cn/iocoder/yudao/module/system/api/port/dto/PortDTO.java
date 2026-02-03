package cn.iocoder.yudao.module.system.api.port.dto;

import lombok.Data;

@Data
public class PortDTO {
    private Long id;
    private String code;
    private String name;
    private String nameEng;
    private Long countryId;
    private String countryName;
    private String region;
    private String city;
    private String Address;
    private Integer status;
}
