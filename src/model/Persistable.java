package model;

/**
 * Interface for every object that can be saved as one line in a text file.
 *
 * Any class that implements this interface promises to provide a method that
 * converts the object into an array of field values. The FileManager then joins
 * these values into a single safe line of text.
 */
public interface Persistable {

    /**
     * Returns the values of this object in the order they are stored in the file.
     */
    String[] toRecord();
}
