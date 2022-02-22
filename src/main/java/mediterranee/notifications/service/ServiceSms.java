package mediterranee.notifications.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Service
public class ServiceSms {
    private final SmsSender smsSender;
@Autowired
    public ServiceSms(@Qualifier("twilio")TwilioSmsSender smsSender) {
        this.smsSender = smsSender;
    }
    public void sendSms(SmsRequest smsRequest){
    smsSender.sendSms(smsRequest);

    }
}
