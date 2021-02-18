package com.example.monzun.requests;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
    @NotNull
    @ApiModelProperty(required = true)
    private String token;
    @NotNull @ApiModelProperty(required = true)
    private String newPassword;
}
