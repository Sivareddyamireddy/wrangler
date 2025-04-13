public class ByteSize extends Token {
    private final double value;
    private final String unit;

    public ByteSize(String raw) {
        super(raw);
        raw = raw.trim().toUpperCase();
        String numPart = raw.replaceAll("[^0-9.]", "");
        this.value = Double.parseDouble(numPart);
        this.unit = raw.replaceAll("[0-9.]", "");
    }

    public long getBytes() {
        switch (unit) {
            case "B": return (long) value;
            case "KB": return (long) (value * 1024);
            case "MB": return (long) (value * 1024 * 1024);
            case "GB": return (long) (value * 1024 * 1024 * 1024);
            case "TB": return (long) (value * 1024L * 1024 * 1024 * 1024);
            default: throw new IllegalArgumentException("Unknown unit: " + unit);
        }
    }
}

@Test
public void testByteSizeParsing() {
    ByteSize b = new ByteSize("1.5MB");
    assertEquals(1.5 * 1024 * 1024, b.getBytes(), 0.1);
}
