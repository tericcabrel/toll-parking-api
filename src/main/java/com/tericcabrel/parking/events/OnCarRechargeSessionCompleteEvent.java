package com.tericcabrel.parking.events;

import com.tericcabrel.parking.models.dbs.CarRechargeSession;
import com.tericcabrel.parking.models.dbs.Customer;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

/**
 * Event fires when a car's recharge session is marked as completed
 */
@Getter
@Accessors(chain = true)
public class OnCarRechargeSessionCompleteEvent extends ApplicationEvent {
    private Customer customer;

    private CarRechargeSession carRechargeSession;

    public OnCarRechargeSessionCompleteEvent(Customer customer, CarRechargeSession carRechargeSession) {
        super(customer);

        this.customer = customer;
        this.carRechargeSession = carRechargeSession;
    }
}
