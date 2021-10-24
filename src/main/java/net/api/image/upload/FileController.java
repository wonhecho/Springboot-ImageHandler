package net.api.image.upload;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload")                                                   // 파일 업로드 기능
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) {                                                          // 파일이 존재하지 않는 예외 처리
            System.out.println(file);
        }
        String fileName = fileService.saveFile(file);                                 // 파일을 저장

        /* 리턴값으로 파일을 다운로드 받는 URI를 제공해주기 위해서 ServletUriComponentsBuilder을 사용 */
        String downloadURI = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(fileName)
                .toUriString();

        return new ResponseEntity<>(downloadURI, HttpStatus.OK);
    }

    @GetMapping(value = "/download/{fileName:.+}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws FileNotFoundException {

        Resource resource = fileService.loadFile(fileName);                           // 파일을 다운로드 기능
        String contentType = null;
        try {
            /* 파일 유형을 확인 */
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // 파일 타입을 결정할 수 없으면 Default로 지정
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}