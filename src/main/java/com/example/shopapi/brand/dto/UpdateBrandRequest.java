package com.example.shopapi.brand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UpdateBrandRequest(
        @NotBlank(message = "Название бренда не должно быть пустым")
        @Size(max = 100, message = "Название бренда не должно превышать {max} символов")
        String name,

        @Size(max = 1000, message = "Описание не должно превышать {max} символов")
        String description,

        @URL(message = "Ссылка на логотип должна быть валидным URL")
        String logoUrl,

        @URL(message = "Ссылка на сайт должна быть валидным URL")
        String website
) {}