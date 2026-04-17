package com.example.User.Handling.API.Model;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Base64MultipartFile implements MultipartFile {

    private final byte[] fileContent;
    private final String contentType;

    public Base64MultipartFile(byte[] fileContent, String contentType){
        this.fileContent = fileContent;
        this.contentType = contentType;
    }


    @Override
    public String getName() {
        return "file";
    }

    @Override
    public String getOriginalFilename() {
        return "image";
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return fileContent.length == 0;
    }

    @Override
    public long getSize() {
        return fileContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return fileContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {

    }
}
