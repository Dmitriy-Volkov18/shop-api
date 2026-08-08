package com.example.shopapi.product.services;

import com.example.shopapi.product.dto.ImageMetadata;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.auth.security.ImageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final ImageValidator imageValidator;

    public ImageMetadata process(
            MultipartFile file
    ){
        imageValidator.validate(file);

        try {
            BufferedImage image =
                    ImageIO.read(
                            file.getInputStream()
                    );

            String fileName = file.getOriginalFilename();

            String storagePath =
                    "/uploads/products/"
                            + UUID.randomUUID()
                            + "_"
                            + fileName;

            return new ImageMetadata(
                    fileName,
                    file.getContentType(),
                    file.getSize(),
                    storagePath,
                    image.getWidth(),
                    image.getHeight()
            );
        } catch(IOException e){
            throw new BadRequestException("Image processing failed");
        }
    }
}