package com.example.monzun.services;

import com.example.monzun.dto.TrackingListDTO;
import com.example.monzun.entities.Startup;
import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.TrackingRequest;
import com.example.monzun.entities.User;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.exception.StartupAccessNotAllowedException;
import com.example.monzun.exception.TrackingAlreadyStartedException;
import com.example.monzun.exception.TrackingForRequestListAccessException;
import com.example.monzun.repositories.StartupRepository;
import com.example.monzun.repositories.TrackingRepository;
import com.example.monzun.repositories.TrackingRequestRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TrackingRequestService {

    private final TrackingRequestRepository trackingRequestRepository;
    private final StartupRepository startupRepository;
    private final TrackingRepository trackingRepository;
    private final TrackingService trackingService;


    public TrackingRequestService(
            TrackingRequestRepository trackingRequestRepository,
            StartupRepository startupRepository,
            TrackingRepository trackingRepository,
            TrackingService trackingService
    ) {
        this.trackingRequestRepository = trackingRequestRepository;
        this.startupRepository = startupRepository;
        this.trackingRepository = trackingRepository;
        this.trackingService = trackingService;
    }

    /**
     * Список наборов
     *
     * @param user Пользователь, который запрашивает список
     * @return List<TrackingListDTO>
     */
    public List<TrackingListDTO> getTrackingList(User user) {
        if (!user.getRole().equals(RoleEnum.STARTUP.getRole())) {
            throw new TrackingForRequestListAccessException("Access denied for trackings to requests");
        }

        Startup startup = startupRepository.findByOwner(user)
                .orElseThrow(() -> new TrackingForRequestListAccessException("Not found your startup"));

        //Убираем из списка наборов, те на которые уже подписались
        List<Tracking> trackingsFromRequests = trackingRequestRepository
                .findByStartup(startup)
                .stream()
                .map(TrackingRequest::getTracking)
                .collect(Collectors.toList());

        List<Tracking> trackings = trackingRepository.findByActiveTrueAndStartedAtAfter(LocalDateTime.now());
        trackings.removeAll(trackingsFromRequests);

        return trackings.stream().map(trackingService::convertToListDto).collect(Collectors.toList());
    }

    /**
     * Подача заявки на набор
     *
     * @param trackingId ID набора
     * @param startupId  ID стартапа
     * @param user       Пользователь, подающий заявку
     * @return TrackingRequest
     * @throws EntityNotFoundException          сущность не найдена
     * @throws StartupAccessNotAllowedException попытка манипулировать чужим стартапом
     * @throws TrackingAlreadyStartedException  попытка запись на набор, который уже начался
     */
    public TrackingRequest create(Long trackingId, Long startupId, User user)
            throws EntityNotFoundException, StartupAccessNotAllowedException,
            TrackingAlreadyStartedException, ValidationException {
        Startup startup = checkPresenceAndGetStartup(startupId);
        if (!startup.getOwner().equals(user)) {
            throw new StartupAccessNotAllowedException(startup, user);
        }

        Tracking tracking = checkPresenceAndGetTracking(trackingId);

        if (trackingRequestRepository.findByStartupAndTracking(startup, tracking).isPresent()) {
            throw new ValidationException("Tracking request for this startup already created: startup id "+ startupId);
        }

        if (LocalDateTime.now().isAfter(tracking.getStartedAt())) {
            throw new TrackingAlreadyStartedException();
        }



        TrackingRequest trackingRequest = new TrackingRequest();
        trackingRequest.setStartup(startup);
        trackingRequest.setTracking(tracking);
        trackingRequest.setCreatedAt(LocalDateTime.now());
        trackingRequestRepository.save(trackingRequest);

        return trackingRequest;
    }

    /**
     * Отмена подачи заявки на набор
     *
     * @param trackingId ID набора
     * @param startupId  ID стартапа
     * @param user       Пользователь, отменяющий заявку
     */
    public void delete(Long trackingId, Long startupId, User user) throws EntityNotFoundException {
        Startup startup = checkPresenceAndGetStartup(startupId);
        if (!startup.getOwner().equals(user)) {
            throw new StartupAccessNotAllowedException(startup, user);
        }

        Tracking tracking = checkPresenceAndGetTracking(trackingId);
        TrackingRequest request = trackingRequestRepository.findByStartupAndTracking(startup, tracking)
                .orElseThrow(() -> new EntityNotFoundException("Tracking request not found"));

        trackingRequestRepository.delete(request);
    }

    /**
     * Получение стартапа
     *
     * @param startupId ID стартапа
     * @return Startup стартап
     */
    private Startup checkPresenceAndGetStartup(Long startupId) {
        return startupRepository.findById(startupId)
                .orElseThrow(() -> new EntityNotFoundException("Startup not found with id " + startupId));
    }

    /**
     * Получение набора
     *
     * @param trackingId ID набора
     * @return Tracking набор
     */
    private Tracking checkPresenceAndGetTracking(Long trackingId) {
        return trackingRepository.findById(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Tracking not found with id " + trackingId));
    }
}
