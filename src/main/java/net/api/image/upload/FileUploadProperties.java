package net.api.image.upload;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.upload")                                            //application.yml에서 미리 설정해둔 경로 불러옴
public class FileUploadProperties {

    private String location;

    public String getLocation(){
        return location;
    }
    public void setLocation(String location)
    {
        this.location = location;
    }
}
