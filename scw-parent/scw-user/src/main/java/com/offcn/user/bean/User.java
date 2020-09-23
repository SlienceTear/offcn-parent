package com.offcn.user.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户实体类")
public class User {


    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("邮箱")
    private String email;
}
