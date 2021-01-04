package com.example.monzun.validation.validators;

import com.example.monzun.validation.rules.FileIsImage;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.URLConnection;
import java.util.Arrays;

public class FileIsImageValidator implements ConstraintValidator<FileIsImage, MultipartFile> {

    private static String[] AVAILABLE_IMAGE_MIMETYPES = {
            "image/gif",
            "image/jpeg",
            "image/pjpeg",
            "image/png",
            "image/svg+xml",
            "image/tiff",
            "image/webp",
    };

    @Override
    public boolean isValid(MultipartFile image, ConstraintValidatorContext constraintValidatorContext) {
        String mimeType = URLConnection.guessContentTypeFromName(image.getOriginalFilename());

        return Arrays.asList(AVAILABLE_IMAGE_MIMETYPES).contains(mimeType);
    }
}
