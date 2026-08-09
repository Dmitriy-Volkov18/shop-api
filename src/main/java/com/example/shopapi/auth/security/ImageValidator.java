package com.example.shopapi.auth.security;

import com.example.shopapi.common.constants.ImageConstants;
import com.example.shopapi.common.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

@Component
public class ImageValidator {
    private static final Set<String> ALLOWED_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            );

    public void validate(
            MultipartFile file
    ) {
        if(file == null || file.isEmpty()){
            throw new BadRequestException("Image file is empty");
        }

        validateSize(file);
        validateContentType(file);
        validateRealImage(file);
    }

    private void validateSize(
            MultipartFile file
    ){
        if(file.getSize() > ImageConstants.MAX_FILE_SIZE){
            throw new BadRequestException("Image size exceeds limit");
        }
    }

    private void validateContentType(
            MultipartFile file
    ){
        if(!ALLOWED_TYPES.contains(file.getContentType())){
            throw new BadRequestException("Unsupported image type");
        }
    }

    private void validateRealImage(
            MultipartFile file
    ){
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            if(image == null){
                throw new BadRequestException("Invalid image file");
            }
        } catch (IOException e){
            throw new BadRequestException("Cannot read image");
        }
    }
}