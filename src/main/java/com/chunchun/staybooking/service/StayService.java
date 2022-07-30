package com.chunchun.staybooking.service;

import com.chunchun.staybooking.exception.StayDeleteException;
import com.chunchun.staybooking.exception.StayNotExistException;
import com.chunchun.staybooking.model.*;
import com.chunchun.staybooking.repository.LocationRepository;
import com.chunchun.staybooking.repository.ReservationRepository;
import com.chunchun.staybooking.repository.StayRepository;
import com.chunchun.staybooking.repository.StayReservationDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StayService {
    private StayRepository stayRepository;
    private ImageStorageService imageStorageService;
    private LocationRepository locationRepository;
    private GeoCodingService geoCodingService;
    private ReservationRepository reservationRepository;
    private StayReservationDateRepository stayReservationDateRepository;

    @Autowired
    public StayService(StayRepository stayRepository, LocationRepository locationRepository,
                       ImageStorageService imageStorageService, GeoCodingService geoCodingService,
                       ReservationRepository reservationRepository, StayReservationDateRepository stayReservationDateRepository) {
        this.stayRepository = stayRepository;
        this.imageStorageService = imageStorageService;
        this.geoCodingService = geoCodingService;
        this.locationRepository = locationRepository;
        this.reservationRepository = reservationRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
    }

    public List<Stay> listByUser(String username) {
        return stayRepository.findByHost(new User.Builder().setUsername(username).build());
    }

    public Stay findByIdAndHost(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }
        return stay;
    }

    public void add(Stay stay) {
        stayRepository.save(stay);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }
        List<Reservation> reservations = reservationRepository.findByStayAndCheckoutDateAfter(stay, LocalDate.now());
        if (reservations != null && reservations.size() > 0) {
            throw new StayDeleteException("Cannot delete stay with active reservation");
        }
  //      stayReservationDateRepository.deleteAllById(Arrays.asList(stayId));

        stayRepository.deleteById(stayId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void add(Stay stay, MultipartFile[] images) {
        List<String> mediaLinks = Arrays.stream(images).parallel().map(image -> imageStorageService.save(image)).collect(Collectors.toList());
        List<StayImage> stayImages = new ArrayList<>();
        for (String mediaLink : mediaLinks) {
            stayImages.add(new StayImage(mediaLink, stay));
        }
        stay.setImages(stayImages);

        stayRepository.save(stay);

        Location location = geoCodingService.getLatLng(stay.getId(), stay.getAddress());
        locationRepository.save(location); // 将location保存到elastic search



    }

}
