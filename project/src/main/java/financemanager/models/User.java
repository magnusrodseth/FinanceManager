package financemanager.models;

import financemanager.utils.validators.UserValidator;

/**
 * A model of a User object.
 */
public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public User(int id, String firstName, String lastName, String email, String password) {
        if (UserValidator.isValidId(id))
            this.id = id;

        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (UserValidator.isValidName(firstName))
            this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (UserValidator.isValidName(lastName))
            this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (UserValidator.isValidEmail(email))
            this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (UserValidator.isValidPassword(password))
            this.password = password;
    }

    /**
     * @return JSON representation of a User object.
     */
    @Override
    public String toString() {
        return "{" +
                "\"id\": " + "\"" + id + "\"" + "," +
                "\"firstName\": " + "\"" + firstName + "\"" + "," +
                "\"lastName\": " + "\"" + lastName + "\"" + "," +
                "\"email\": " + "\"" + email + "\"" + "," +
                "\"password\": " + "\"" + password + "\"" +
                "}";
    }
}
