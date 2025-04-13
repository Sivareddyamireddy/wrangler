public class TimeDuration extends Token {
    private final double value;
    private final String unit;

    public TimeDuration(String raw) {
        super(raw);
        raw = raw.trim().toLowerCase();
        String numPart = raw.replaceAll("[^0-9.]", "");
        this.value = Double.parseDouble(numPart);
        this.unit = raw.replaceAll("[0-9.]", "");
    }

    public long getMilliseconds() {
        switch (unit) {
            case "ns": return (long) (value / 1_000_000);
            case "us": return (long) (value / 1_000);
            case "ms": return (long) value;
            case "s": return (long) (value * 1000);
            case "m": return (long) (value * 60 * 1000);
            case "h": return (long) (value * 60 * 60 * 1000);
            default: throw new IllegalArgumentException("Unknown unit: " + unit);
        }
    }
}
@Test
public void testTimeDurationParsing() {
    TimeDuration t = new TimeDuration("2s");
    assertEquals(2000, t.getMilliseconds());
}