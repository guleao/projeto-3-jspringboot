// src/main/java/com/example/projeto3/config/ModelMapperConfig.java
package com.example.projeto3.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Conversor para Converter qualquer Collection para List
        Converter<Collection<?>, List<?>> collectionToListConverter = new Converter<Collection<?>, List<?>>() {
            @Override
            public List<?> convert(MappingContext<Collection<?>, List<?>> context) {
                if (context.getSource() == null) {
                    return null;
                }
                return new ArrayList<>(context.getSource());
            }
        };

        modelMapper.addConverter(collectionToListConverter);
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        return modelMapper;
    }
}