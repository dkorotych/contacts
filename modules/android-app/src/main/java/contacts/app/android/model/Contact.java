package contacts.app.android.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contact for single person.
 */
public class Contact {

    private static final String JSON_USER_NAME = "userName";
    private static final String JSON_FIRST_NAME = "firstName";
    private static final String JSON_LAST_NAME = "lastName";
    private static final String JSON_PHONE = "phone";
    private static final String JSON_MAIL = "mail";
    private static final String JSON_LOCATION = "location";

    /**
     * Creates a contact from JSON object.
     */
    public static Contact fromJson(JSONObject json) throws JSONException {
        Contact contact = new Contact();

        contact.userName = json.getString(JSON_USER_NAME);
        contact.firstName = json.getString(JSON_FIRST_NAME);
        contact.lastName = json.getString(JSON_LAST_NAME);
        contact.phone = json.getString(JSON_PHONE);
        contact.mail = json.getString(JSON_MAIL);
        contact.location = json.getString(JSON_LOCATION);

        return contact;
    }

    private String userName;
    private String firstName;
    private String lastName;
    private String mail;
    private String phone;
    private String location;

    public Contact() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDisplayName() {
        return lastName + " " + firstName;
    }

    public String getFormattedPhone() {
        return "+" + phone;
    }

}
