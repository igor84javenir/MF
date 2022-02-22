package mediterranee.notifications.controller;

import mediterranee.notifications.service.ServiceSms;
import mediterranee.notifications.service.SmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/sms")
public class ControllerSms {
    private final ServiceSms service;

    @Autowired
    public ControllerSms(ServiceSms service) {
        this.service = service;
    }

    @PostMapping
    public void sendSms( @RequestBody SmsRequest smsRequest){
        service.sendSms(smsRequest);
    }

    @GetMapping("test")
    public String test() {
        return "test";
    }
}
