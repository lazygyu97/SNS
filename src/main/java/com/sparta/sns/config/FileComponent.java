package com.sparta.sns.config;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FileComponent {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile) throws IOException {  // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
            // 매개변수로 받은 MultipartFile > File 변환
            File fileToUpload = convert(multipartFile)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));

        String fileName = "image" + "/" + fileToUpload.getName();
        String uploadImageUrl = putS3(fileToUpload, fileName);

        fileToUpload.delete();    // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)    // PublicRead 권한으로 업로드
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertFile);
        fos.write(file.getBytes());
        fos.close();
        return Optional.of(convertFile);
    }
}