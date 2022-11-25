package Class;

public class comboItem {
    private String key;
    private String value;

    public comboItem(String key, String value) {
        this.key = key;
        this.value = value;
    }
    public comboItem(String key, int value) {
        this.key = key;
        this.value = String.valueOf(value);
    }

    public String toString() {
        return key;
    }
    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
}
