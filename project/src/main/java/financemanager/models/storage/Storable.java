package financemanager.models.storage;

import java.io.IOException;

/**
 * Provides the four basic functions of persistent storage:
 * Create, read, update and delete.
 *
 * @param <T> is a generic type that the implementor class decides.
 */
public interface Storable<T> {

    /**
     * Creates a new instance.
     *
     * @param instance is a generic type that the implementor class decides.
     */
    void create(T instance) throws IOException;

    /**
     * Reads an existing instance.
     * Note that each implementing class must know the fields of the object it reads,
     * thus demanding a unique implementation for each respective model.
     *
     * @param identifier is a String representation of a unique property of the type T.
     * @return a generic type that the implementor class decides.
     */
    T read(String identifier) throws IOException;


    /**
     * Updates an existing instance.
     * Note that each implementing class must know the fields of the object it updates,
     * thus demanding a unique implementation for each respective model.
     *
     * @param identifier is a String representation of a unique property of the type T.
     * @param instance   is the generic object to be updated.
     */
    void update(String identifier, T instance) throws IOException;


    /**
     * Deletes an existing instance.
     * Note that each implementing class must know the fields of the object it deletes,
     * thus demanding a unique implementation for each respective model.
     *
     * @param identifier is a String representation of a unique property of the type T.
     */
    void delete(String identifier) throws IOException;
}
