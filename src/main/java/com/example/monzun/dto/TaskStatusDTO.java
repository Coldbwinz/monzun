package com.example.monzun.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class TaskStatusDTO {
    private Long id;
    private String name;
    private String alias;
}


