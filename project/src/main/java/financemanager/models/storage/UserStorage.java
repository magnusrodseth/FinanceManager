package financemanager.models.storage;

import financemanager.models.User;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Controls persistent storage of User objects.
 */
public class UserStorage extends Storage<User> {

    public UserStorage() {
        super("users.json");
    }

    @Override
    public void create(User instance) throws IOException {
        super.create(instance);
    }

    @Override
    public User read(String identifier) throws IOException {
        List<User> users = new ArrayList<>();

        try {
            users = this.getUsers();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all users.");
        }

        if (users == null)
            throw new NullPointerException("Could not find any users.");

        for (User user : users) {
            if (user.getEmail().equals(identifier))
                return user;
        }
        return null;
    }

    public User read(int id) throws IOException {
        List<User> users = new ArrayList<>();

        try {
            users = getUsers();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all users.");
        }

        if (users == null)
            throw new NullPointerException("Could not find any users.");

        for (User user : users) {
            if (user.getId() == id)
                return user;
        }
        return null;
    }

    @Override
    public void update(String identifier, User instance) throws IOException {
        List<User> users = new ArrayList<>();

        try {
            users = this.getUsers();
        } catch (IOException e) {
            throw new IOException("An error occured when trying to get users");
        }

        for (int i = 0; i < users.size(); i++) {
            User item = users.get(i);
            if (item.getEmail().equals(identifier)) {
                users.set(i, instance);
                break;
            }
        }

        try {
            writeToJson(users.toString());
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to write user to file.");
        }
    }

    @Override
    public void delete(String identifier) throws IOException {
        List<User> users = new ArrayList<>();

        try {
            users = this.getUsers();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all users.");
        }

        for (int i = 0; i < users.size(); i++) {
            User item = users.get(i);
            if (item.getEmail().equals(identifier)) {
                users.remove(i);
                break;
            }
        }

        try {
            writeToJson(users.toString());
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to write user to file.");
        }
    }

    public List<User> getUsers() throws IOException {
        List<User> allUsers = new ArrayList<>();

        String fileContent = super.getFileContent();

        // Matches all instances of JSON objects in fileContent
        Pattern curlyPattern = Pattern.compile("\\{([^}]+)\\}", Pattern.DOTALL);
        Matcher curlyMatcher = curlyPattern.matcher(fileContent);

        List<String> objects = new ArrayList<>();

        while (curlyMatcher.find()) {
            objects.add(curlyMatcher.group(1));
        }

        // Regex that matches JSON keys to values, explicitly string values
        Pattern pattern = Pattern.compile("\"*\"\\s*:\\s*\"(.+?)\"", Pattern.DOTALL);

        // The List values holds instances of the List objects
        List<List<String>> values = new ArrayList<>();

        // Matches values in array with JSON objects (List<String> objects)
        // and groups them inside their own ArrayList within the List values
        for (var i = 0; i < objects.size(); i++) {
            Matcher matcher = pattern.matcher(objects.get(i));

            values.add(new ArrayList<>());

            while (matcher.find()) {
                values.get(i).add(matcher.group(1));
            }

        }

        // Split file content into each JSON object
        // If there exists only one JSON object,
        // the split still works
        for (var i = 0; i < values.size(); i++) {
            User user = constructUser(values.get(i));
            allUsers.add(user);
        }

        return allUsers;
    }

    /**
     * Constructs a User object based on provided String data.
     *
     * @param data A List< String > representation of a User object
     * @return An instance of the User class
     * @throws IllegalStateException If an error occurs when constructing a User
     */
    private User constructUser(List<String> data) {

        User user = null;

        // This list will always only contain one constructor, as each model has only one constructor
        Constructor[] constructors = User.class.getConstructors();

        for (Constructor constructor : constructors) {
            // To instantiate a new instance of the class, all the values have to correlate with
            // the constructor of the class
            try {
                user = (User) constructor.newInstance(Integer.parseInt(data.get(0)), data.get(1), data.get(2), data.get(3), data.get(4));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("An error occurred when trying to construct a new User instance.");
            }
        }

        return user;
    }

    public int getLastUserId() throws IOException {
        int id = 0;
        List<User> users = new ArrayList<>();

        try {
            users = getUsers();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to retrieve the last used ID");
        }

        if (users.size() == 0)
            return id;

        return Collections.max(users.stream().map(User::getId).collect(Collectors.toList()));
    }
}
