package mediterranee.notifications.service;

public interface SmsSender {
    void sendSms(SmsRequest smsRequest);
}
