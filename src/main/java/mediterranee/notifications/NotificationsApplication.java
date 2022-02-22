package mediterranee.notifications;

import mediterranee.notifications.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class NotificationsApplication {

	@Autowired
	private EmailSenderService senderService;

	public static void main(String[] args) {
		SpringApplication.run(NotificationsApplication.class, args);
	}

//	@EventListener(ApplicationReadyEvent.class)
//	public void SendMail(){
//		senderService.sendEmail("amine-finance@hotmail.com",
//				"Votre rendez_vous à mediterranée formation",
//				"veuillez vous présenter le 25/03/2022 à medfo à 12h45");
//	}

}
