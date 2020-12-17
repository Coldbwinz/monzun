package com.example.monzun.validation.validators;

import com.example.monzun.repositories.AttachmentRepository;
import com.example.monzun.validation.rules.ExistsAttachmentFromList;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistsAttachmentFromListValidator implements ConstraintValidator<ExistsAttachmentFromList, Long[]> {
    @Autowired
    private AttachmentRepository attachmentRepository;

    @Override
    public boolean isValid(Long[] ids, ConstraintValidatorContext constraintValidatorContext) {
        if (ids != null) {
            boolean isValid;
            for (Long id : ids) {
                isValid = id == null || attachmentRepository.existsById(id);

                if (!isValid) {
                    return false;
                }
            }
            return true;
        }

        return true;
    }
}
