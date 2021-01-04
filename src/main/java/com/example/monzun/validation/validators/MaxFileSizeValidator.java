package com.example.monzun.validation.validators;

import com.example.monzun.validation.rules.MaxFileSize;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MaxFileSizeValidator implements ConstraintValidator<MaxFileSize, MultipartFile[]> {

    @Override
    public boolean isValid(MultipartFile[] files, ConstraintValidatorContext constraintValidatorContext) {
        for (MultipartFile file : files) {
            long MAX_FILE_SIZE = 800_000_000L;

            if (file.getSize() > MAX_FILE_SIZE) {
                return false;
            }
        }

        return true;
    }
}
