package c195;

import java.util.*;

public class InvalidDataException extends Exception {
    private ArrayList<String> errors = new ArrayList<String>();
    public ArrayList<String> getErrors() { return errors; }
    public InvalidDataException(ArrayList<String> errors) {
        this.errors.addAll(errors);
    }
}
