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

//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.CannedAccessControlList;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import com.amazonaws.services.s3.model.S3Object;
//import com.amazonaws.util.StringUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class FileComponent {
//
//    // S3 config에서 만든 Bean 주입 받음
//    private final AmazonS3 s3Client;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    // 사진 업로드
//    public String upload(MultipartFile file) {
//        try {
//            // 매개변수로 받은 MultipartFile > File 변환
//            File fileToUpload = convert(file)
//                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
//
////            PutObjectRequest objectRequest = PutObjectRequest.builder()
////                    .bucket(bucket)
////                    .key(file.getName())
////                    .build();
//
//            s3Client.putObject(
//                    new PutObjectRequest(bucket, file.getName(), fileToUpload)
//                            .withCannedAcl(CannedAccessControlList.PublicRead)    // PublicRead 권한으로 업로드 됨
//            );
//            return s3Client.getUrl(bucket, file.getName()).toString();  // 업로드된 파일의 S3 URL 주소 반환
//        } catch (Exception e) {
//            throw new IllegalArgumentException("fail to upload files", e);
//        }
//    }
//
//    // 파일 변환, 변환을 위해 로컬 프로젝트에 사진 파일이 생성된다
//    private Optional<File> convert(MultipartFile file) throws IOException {
//
//        String originalFilename = file.getOriginalFilename();
//
//        if (StringUtils.isNullOrEmpty(originalFilename)) {
//            originalFilename = "temp";
//        }
//
//        File convertFile = new File(originalFilename);
//
////        if(convertFile.createNewFile()) {
//            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
//                fos.write(file.getBytes());
//                fos.close();
//            }
//            return Optional.of(convertFile);
////        }
////        return Optional.empty(); // 파일 없음
//    }
//
//    // s3에 업로드 된 파일 삭제
//    public void delete(String fileName) {
//        try {
//            s3Client.deleteObject(bucket, fileName);
//        }
//        catch (Exception e) {
//            throw new IllegalArgumentException("fail to delete files", e);
//        }
//    }
//
//    public byte[] download(String fileName) {
//        try {
//            // 객체로 파일 받아오기, inputStream으로 읽어온 뒤 File 객체로 가져옴
//            // File : 메모리에 File 전체가 binary 파일로 올라가 있음 (ex, 100*100) 용량 크고, 사용 후 해제해야 함
//            S3Object s3Object = s3Client.getObject(bucket, fileName);
//
//            String contentType = s3Object.getObjectMetadata().getContentType();
//
//            return s3Object.getObjectContent().readAllBytes();
//
////        // API Response 만들어줄 때
////        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
////        ResponseEntity.ok(byteArrayResource);
//
//        } catch (Exception e) {
//            throw new IllegalArgumentException("fail to download files", e);
//        }
//
//    }
//}