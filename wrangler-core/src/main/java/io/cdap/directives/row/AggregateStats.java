public class AggregateStats implements Directive {
    private String sizeCol;
    private String timeCol;
    private String outputSizeCol;
    private String outputTimeCol;
    private long totalSize = 0;
    private long totalTime = 0;

    @Override
    public UsageDefinition define() {
        return UsageDefinition.builder("aggregate-stats")
            .define("sizeCol", TokenType.COLUMN_NAME)
            .define("timeCol", TokenType.COLUMN_NAME)
            .define("outputSizeCol", TokenType.COLUMN_NAME)
            .define("outputTimeCol", TokenType.COLUMN_NAME)
            .build();
    }

    @Override
    public void initialize(Arguments arguments) {
        sizeCol = arguments.value("sizeCol");
        timeCol = arguments.value("timeCol");
        outputSizeCol = arguments.value("outputSizeCol");
        outputTimeCol = arguments.value("outputTimeCol");
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) {
        for (Row row : rows) {
            String sizeRaw = (String) row.getValue(sizeCol);
            String timeRaw = (String) row.getValue(timeCol);

            ByteSize bs = new ByteSize(sizeRaw);
            TimeDuration td = new TimeDuration(timeRaw);

            totalSize += bs.getBytes();
            totalTime += td.getMilliseconds();
        }

        Row result = new Row();
        result.add(outputSizeCol, totalSize / (1024.0 * 1024)); // MB
        result.add(outputTimeCol, totalTime / 1000.0); // seconds

        return Collections.singletonList(result);
    }
}

@Test
public void testAggregation() {
    List<Row> rows = Arrays.asList(
        new Row().add("data_transfer", "1MB").add("response_time", "500ms"),
        new Row().add("data_transfer", "2MB").add("response_time", "1500ms")
    );

    String[] recipe = {
        "aggregate-stats :data_transfer :response_time total_mb total_sec"
    };

    List<Row> output = TestingRig.execute(recipe, rows);

    assertEquals(1, output.size());
    assertEquals(3.0, output.get(0).getValue("total_mb"));
    assertEquals(2.0, output.get(0).getValue("total_sec"));
}
