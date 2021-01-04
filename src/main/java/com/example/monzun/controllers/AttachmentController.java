package com.example.monzun.controllers;

import com.example.monzun.dto.AttachmentDTO;
import com.example.monzun.repositories.AttachmentRepository;
import com.example.monzun.services.AttachmentService;
import com.example.monzun.validation.rules.FileIsImage;
import com.example.monzun.validation.rules.MaxFileSize;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("api/attachment")
public class AttachmentController extends BaseRestController {
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;

    public AttachmentController(AttachmentService attachmentService, AttachmentRepository attachmentRepository) {
        this.attachmentService = attachmentService;
        this.attachmentRepository = attachmentRepository;
    }

    /**
     * Загрузка файлов.
     *
     * @param files MultipartFile[] files
     * @return JSON-структура загруженного файла
     * @throws IOException IOException
     */
    @ApiOperation(
            value = "Загрузить файлы",
            notes = "Мультизагрузка файлов на сервер и возрат соответствующих сущностей. Максимальный размер файла 800 МБ",
            response = AttachmentDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
    })
    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @ApiParam(required = true, name = "files", value = "Файлы для загруки")
            @Valid @MaxFileSize @RequestPart("files") MultipartFile[] files
    ) throws IOException {
        return ResponseEntity.ok(attachmentService.getAttachmentsDTO(attachmentService.storeFiles(files)));
    }

    /**
     * Загрузка одного файла - изображения.
     *
     * @param file MultipartFile file
     * @return JSON
     * @throws IOException IOException
     */
    @ApiOperation(
            value = "Загрузить изображение",
            notes = "Загрузка изображения. Файл проверяется на максимальный размер - 800 МБ и MIMETYPE",
            response = AttachmentDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
    })
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(
            @ApiParam(required = true, name = "files", value = "Изображение для загруки")
            @Valid @MaxFileSize(groups = MultipartFile.class) @FileIsImage @RequestPart("files") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(attachmentService.convertToShortDto(attachmentService.storeFile(file)));
    }

    /**
     * Скачивание файла.
     *
     * @param forceDownload флаг принудительной загрузки файла. Если указано TRUE - файл скачивается без предварительного
     *                      просмотра
     * @param uuid          UUID модели файла
     * @param request       Request
     * @return ResponseEntity - JSON ответ
     * @throws IOException IOException
     */
    @ApiOperation(
            value = "Скачивание файла",
            notes = "Скачивание файла с сервера по UUID",
            response = AttachmentDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 404, message = "Файл не найден"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Доступ запрещен")
    })
    @GetMapping("/download/{uuid:.+}")
    public ResponseEntity<?> download(
            @ApiParam(
                    allowableValues = "true, false",
                    value = "Флаг принудительной загрузки.Если параметр передан true - загрузка происходит сразу"
            )
            @RequestParam boolean forceDownload,
            @ApiParam(
                    required = true,
                    value = "UUID файла для загрузки"
            )
            @PathVariable String uuid,
            HttpServletRequest request
    ) throws IOException {
        UUID uuidFromString = UUID.fromString(uuid);
        if (!attachmentRepository.existsAllByUuid(uuidFromString)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resource resource = attachmentService.download(uuidFromString);
        String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        String contentDisposition = forceDownload ? "attachment" : "inline";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition
                        + "; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Удаление файла
     *
     * @param uuid UUID модели файла
     * @return ResponseEntity - JSON ответ
     * @throws IOException IOException
     */
    @ApiOperation(
            value = "Удалить файл",
            notes = "Удаление файла по UUID"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 404, message = "Файл не найден"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Доступ запрещен")
    })
    @DeleteMapping("/delete/{uuid:.+}")
    public ResponseEntity<?> delete(
            @ApiParam(required = true, value = "UUID файла для удаления")
            @PathVariable String uuid
    ) throws IOException {
        return attachmentService.delete(UUID.fromString(uuid))
                ? ResponseEntity.status(HttpStatus.OK).body(this.getTrueResponse())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
