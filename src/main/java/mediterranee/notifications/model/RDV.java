package mediterranee.notifications.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RDV {

    private String lastName;
    private String firstName;
    private Integer phoneNumber;
    private String mail;
    private LocalDate letterSentDate;
    private LocalDate RDVDate;
    private LocalTime RDVTime;
    private String RDVLocation;
    private String referentName;
    private String RDVType;




}
