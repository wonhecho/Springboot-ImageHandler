package net.api.image.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {

    private final Path dirLocation;                                             // Path 클래스를 불러온다.

    @Autowired
    public FileService(FileUploadProperties fileUploadProperties) {             // FileUploadProperties 파일 확인
        this.dirLocation = Paths.get(fileUploadProperties.getLocation())        // dirlocation에 파일을 저장할 위치를 가저옴.
                .toAbsolutePath().normalize();                                  // AbsolutePath : 절대경로, normalize : 중복성 제거
        System.out.println(this.dirLocation);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.dirLocation);                          //디렉토리 생성
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String saveFile(MultipartFile multipartFile) {                       // 파일 저장 기능
        String fileName = multipartFile.getOriginalFilename();                  // 파일의 이름을 fileName부분에 저장
        Path location = this.dirLocation.resolve(fileName);                     // 경로와 파일을 합치는 부분
        try {
            /* 실제 파일이 upload 되는 부분 */
            Files.copy(multipartFile.getInputStream(), location, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }
    public Resource loadFile(String fileName) throws FileNotFoundException {        // 파일을 불러오는 기능

        try {
            Path file = this.dirLocation.resolve(fileName).normalize();             // 받은 파일이름을 합치는 부분
            Resource resource = new UrlResource(file.toUri());

            if(resource.exists() || resource.isReadable()) {
                /* 실제 파일을 loading 하는 부분 */
                return resource;
            }else {
                throw new FileNotFoundException("Could not find file");
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not download file");
        }

    }
}
