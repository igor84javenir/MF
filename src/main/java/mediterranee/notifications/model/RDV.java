package mediterranee.notifications.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RDV {

    private String firstName;
    private String lastName;
    private String address1;
    private String address2;
    private String address3;
    private String phoneNumber;
    private String mail;
    private String RDVDate;
    private String RDVTime;
    private String RDVLocation;
}
