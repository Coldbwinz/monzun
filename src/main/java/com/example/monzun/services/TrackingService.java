package com.example.monzun.services;

import com.example.monzun.dto.TrackingListDTO;
import com.example.monzun.entities.Tracking;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
public class TrackingService {

    private final ModelMapper modelMapper;

    public TrackingService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Преобразование в список DTO
     * @param tracking Startup стартап
     * @return TrackingListDTO
     */
    public TrackingListDTO convertToDto(Tracking tracking) {
        return modelMapper.map(tracking, TrackingListDTO.class);
    }
}
