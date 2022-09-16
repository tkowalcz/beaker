package pl.tkowalcz.meetup1;

public enum DataFormat {

    ROW, COLUMN;

    public static DataFormat fromString(String value) {
        for (DataFormat dataFormat : DataFormat.values()) {
            if (dataFormat.name().equalsIgnoreCase(value)) {
                return dataFormat;
            }
        }

        throw new IllegalArgumentException("Unrecognised data format: " + value);
    }
}
