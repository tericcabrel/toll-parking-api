package com.tericcabrel.parking.controllers;

import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import com.tericcabrel.parking.models.dbs.CarType;
import com.tericcabrel.parking.models.dbs.Customer;
import com.tericcabrel.parking.models.dbs.ParkingSlot;
import com.tericcabrel.parking.utils.Helpers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Calendar;
import java.util.Date;

@Controller
public class TemplateController {

    @GetMapping("/template")
    public String template(Model model) {
        Date startTime = new Date(2020, Calendar.MAY, 1, 11, 3, 4);
        Date endTime = new Date(2020, Calendar.MAY, 1, 14, 36, 4);

        double hour = Helpers.calculateDuration(startTime, endTime); // 3.55

        CarType carType = new CarType(null, null, null, "Gasoline");

        Customer customer = Customer.builder()
            .carType(carType)
            .phone("+33 6 93 64 28 89")
            .name("Eric Cabrel TIOGO")
            .email("tericcabrel@yahoo.com")
            .build();

        ParkingSlot parkingSlot = ParkingSlot.builder()
            .carType(carType)
            .label("Parking Slot C")
            .build();

        CarRechargeSession carRechargeSession = CarRechargeSession.builder()
            .customer(customer)
            .parkingSlot(parkingSlot)
            .startTime(startTime)
            .endTime(endTime)
            .price(257.35d)
            .build();

        model.addAttribute("customer", customer);
        model.addAttribute("carRecharge", carRechargeSession);
        model.addAttribute("duration", hour);
        model.addAttribute("carRechargeStartTime", Helpers.formatDate(startTime));
        model.addAttribute("carRechargeEndTime", Helpers.formatDate(endTime));

        return "html/recharge";
    }
}
