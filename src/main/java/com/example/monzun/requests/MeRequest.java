package com.example.monzun.requests;

import com.example.monzun.validation.rules.ExistsAttachment;
import com.example.monzun.validation.rules.UniqueUserEmail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeRequest {
    @NotNull
    private String name;
    @UniqueUserEmail
    @NotNull
    private String email;
    @Pattern(
            regexp = "^(\\+7|7|8)?[\\s\\-]?\\(?[0-9]{3}\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}$",
            message = "Phone format is invalid"
    )
    private String phone;
    @ExistsAttachment
    private Long avatarId;
    @NotNull
    @Min(6)
    private String password;
}
