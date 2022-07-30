package com.chunchun.staybooking.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stay_reserved_data")
public class StayReservedDate {

    @EmbeddedId
    private StayReservedDateKey id;

    @ManyToOne
    @MapsId("stay_id")
    private Stay stay;

    public StayReservedDate() {}

    public StayReservedDate(StayReservedDateKey id, Stay stay) {
        this.id = id;
        this.stay = stay;
    }

    public StayReservedDateKey getId() {
        return id;
    }

    public Stay getStay() {
        return stay;
    }


}
