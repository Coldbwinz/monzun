package com.example.monzun.services;

import com.example.monzun.dto.TrackingDTO;
import com.example.monzun.dto.TrackingListDTO;
import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.User;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.exception.TrackingAccessNotAllowedException;
import com.example.monzun.repositories.StartupRepository;
import com.example.monzun.repositories.TrackingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TrackingService {

    private final ModelMapper modelMapper;
    private final TrackingRepository trackingRepository;
    private final StartupRepository startupRepository;

    public TrackingService(
            ModelMapper modelMapper,
            TrackingRepository trackingRepository,
            StartupRepository startupRepository
    ) {
        this.modelMapper = modelMapper;
        this.trackingRepository = trackingRepository;
        this.startupRepository = startupRepository;
    }

    /**
     * Список набором отсортированный по активности и дате начала. Учитывается роль пользователя
     *
     * @param user пользователь
     * @return List<TrackingListDTO>
     */
    public List<TrackingListDTO> getTrackings(User user) {
        if (user.getRole().equals(RoleEnum.STARTUP.getRole())) {
            return trackingRepository
                    .getStartupTrackings(user)
                    .stream()
                    .map(this::convertToListDto)
                    .collect(Collectors.toList());
        } else if (user.getRole().equals(RoleEnum.TRACKER.getRole())) {
            return trackingRepository
                    .getTrackerTrackings(user)
                    .stream()
                    .map(this::convertToListDto)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }


    /**
     * Конкретный набор
     *
     * @param id   Tracking id
     * @param user пользователь
     * @return Object
     * @throws EntityNotFoundException           EntityNotFoundException
     * @throws TrackingAccessNotAllowedException TrackingAccessNotAllowedException
     */
    public TrackingDTO getTracking(Long id, User user) throws EntityNotFoundException, TrackingAccessNotAllowedException {
        Tracking tracking = trackingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tracking not found id " + id));

        if (user.getRole().equals(RoleEnum.TRACKER.getRole())) {
            if (!trackingRepository.getTrackerTrackings(user).contains(tracking)) {
                throw new TrackingAccessNotAllowedException(tracking, user);
            }
            tracking.setStartups(startupRepository.getTrackerStartups(user));
        }

        return this.convertToDto(tracking);
    }


    /**
     * Преобразование в список DTO
     *
     * @param tracking Startup стартап
     * @return TrackingListDTO
     */
    public TrackingListDTO convertToListDto(Tracking tracking) {
        return modelMapper.map(tracking, TrackingListDTO.class);
    }

    /**
     * Преобразование в DTO
     *
     * @param tracking Startup стартап
     * @return TrackingDTO
     */
    public TrackingDTO convertToDto(Tracking tracking) {
        return modelMapper.map(tracking, TrackingDTO.class);
    }
}
