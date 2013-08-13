package contacts.app.android.model;

/**
 * Contact for single person.
 *
 * <p>
 * The user name of person is treated as unique identifier. Therefore, if two
 * contacts have the same user name it means that they belong to one person.
 */
public class Contact extends contacts.model.Contact {

    /**
     * Formats phone number according to requirements of address book.
     */
    public String getFormattedPhone() {
        return "+" + getPhone();
    }
}
