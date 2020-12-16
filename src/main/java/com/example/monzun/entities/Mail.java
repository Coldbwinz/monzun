package com.example.monzun.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Mail {
    private String from;
    private String mailTo;
    private String subject;
    private Map<String, Object> props;
}
