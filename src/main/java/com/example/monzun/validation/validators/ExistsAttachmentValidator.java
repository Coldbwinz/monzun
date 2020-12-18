package com.example.monzun.validation.validators;

import com.example.monzun.repositories.AttachmentRepository;
import com.example.monzun.validation.rules.ExistsAttachment;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistsAttachmentValidator implements ConstraintValidator<ExistsAttachment, Long> {
    @Autowired
    private AttachmentRepository attachmentRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return id == null || attachmentRepository.existsById(id);
    }
}
