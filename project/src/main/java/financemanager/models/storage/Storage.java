package financemanager.models.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * An abstract class for controlling persistent storage.
 *
 * @param <T> is a generic type that the implementor class decides.
 */
public abstract class Storage<T> implements Storable<T> {
    private final String path;

    /**
     * Creates a file if it does not already exist with the filename given.
     *
     * @param filename is the name of the file to be created.
     */
    public Storage(String filename) {
        path = String.valueOf(
                Paths.get(System.getProperty("user.home"), "financemanager", "storage", filename)
        );

        // ----- IMPORTANT -----
        // We try to create directory and files for persistent storage.
        // If this fails, the application cannot run.
        //Thus, the application exits with exit code 1.
        try {
            createDirectoryIfNotExists();
            createFileIfNotExists();
        } catch (IOException e) {
            System.exit(1);
        }
    }

    @Override
    public void create(T instance) throws IOException {
        String elements = "";

        try {
            elements = getFileContent();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to retrieve file contents.");
        }

        // Add instance to file content as String
        if (elements.equals(""))
            elements = instance.toString();
        else
            elements += "," + instance.toString();

        try {
            writeToJson(elements);
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to write file contents.");
        }
    }

    @Override
    public abstract T read(String identifier) throws IOException;

    @Override
    public abstract void update(String identifier, T instance) throws IOException;

    @Override
    public abstract void delete(String identifier) throws IOException;

    /**
     * Creates the directories required if they do not exist
     * Returns a boolean representing whether or not the operation was successful
     */
    private void createDirectoryIfNotExists() throws IOException {
        try {
            Path path = Paths.get(System.getProperty("user.home"), "tdt4100-prosjekt-magnrod", "storage");
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to create the directory: " + path);
        }
    }

    /**
     * Creates a new file for local storage if it does not exist already.
     * Initializes this JSON file as a JSON Array.
     * Returns a boolean representing whether or not the operation was successful
     */
    private void createFileIfNotExists() throws IOException {
        File file = new File(path);
        // Prepare JSON file for future insertions by initializing it as a JSON Array.
        // Cannot leave this empty, as we use it for parsing later.
        FileWriter writer = null;

        if (file.exists())
            return;

        try {
            file.createNewFile();
            writer = new FileWriter(path);
            writer.write("[]");
            writer.close();
        } catch (IOException e) {
            throw new IOException(
                    "An error occurred when trying to create a new file with the path: " + path
            );
        }
    }

    /**
     * Writes to a JSON file.
     *
     * @throws IOException if the JSON file cannot be written.
     */
    public void writeToJson(String elements) throws IOException {
        FileWriter writer = new FileWriter(path);

        if (!(elements.equals("[]"))) {

            // Initialize JSON Array
            writer.write("[");

            // Add file content to JSON file
            writer.write(elements);

            // Close JSON Array
            writer.write("]");
        } else {
            writer.write("[]");
        }
        writer.close();
    }


    /**
     * Gets file content as a String, without hard brackets
     *
     * @return file content as a String, without hard brackets
     * @throws IOException if the JSON file cannot be written.
     */
    public String getFileContent() throws IOException {
        String fileContent = Files.readString(Path.of(path));

        // Remove hard brackets
        // If JSON array is empty, fileContent will be an empty string
        fileContent = fileContent.substring(1, fileContent.length() - 1);

        return fileContent;
    }

    /**
     * Gets the file path.
     *
     * @return a String representation of the file path.
     */
    public String getPath() {
        return path;
    }
}
