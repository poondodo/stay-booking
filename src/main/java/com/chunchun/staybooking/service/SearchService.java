package com.chunchun.staybooking.service;

import com.chunchun.staybooking.model.Stay;
import com.chunchun.staybooking.repository.LocationRepository;
import com.chunchun.staybooking.repository.StayRepository;
import com.chunchun.staybooking.repository.StayReservationDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class SearchService {

    private StayRepository stayRepository;

    private StayReservationDateRepository stayReservationDateRepository;

    private LocationRepository locationRepository;

    @Autowired
    public SearchService(StayRepository stayRepository, StayReservationDateRepository stayReservationDateRepository,
                         LocationRepository locationRepository) {
        this.stayRepository = stayRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
        this.locationRepository = locationRepository;
    }

    public List<Stay> search(int guestNumber, LocalDate checkinDate, LocalDate checkoutDate, double lat,
                             double lon, String distance) {
        // this searchByDistance is the method that we write by ourselves and that's why we let
        // locationRepository extends CustomLocationRepository
        List<Long> stayIds = locationRepository.searchByDistance(lat, lon, distance);
        if (stayIds == null || stayIds.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> reservedStayIds = stayReservationDateRepository.findByIdInAndDateBetween(stayIds, checkinDate, checkoutDate.minusDays(1));
        List<Long> filterStayIds = new ArrayList<>();
        for (Long stayId : stayIds) {
            if (!reservedStayIds.contains(stayId)) {
                filterStayIds.add(stayId);
            }
        }
        return stayRepository.findByIdInAndGuestNumberGreaterThanEqual(filterStayIds, guestNumber);
    }
}
