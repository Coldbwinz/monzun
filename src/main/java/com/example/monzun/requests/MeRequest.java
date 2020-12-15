package com.example.monzun.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeRequest {
    @NotNull
    private String name;
    private String email;
    private String phone;
    private Long avatarId;
    @NotNull @Min(6)
    private String password;
}
