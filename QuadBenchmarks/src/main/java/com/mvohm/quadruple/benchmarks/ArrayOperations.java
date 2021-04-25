    package com.mvohm.quadruple.benchmarks;

    import java.io.IOException;
    import java.math.BigDecimal;
    import java.math.MathContext;
    import java.math.RoundingMode;
    import java.util.Random;

    import org.openjdk.jmh.annotations.Benchmark;
    import org.openjdk.jmh.annotations.BenchmarkMode;
    import org.openjdk.jmh.annotations.Fork;
    import org.openjdk.jmh.annotations.Measurement;
    import org.openjdk.jmh.annotations.Mode;
    import org.openjdk.jmh.annotations.OutputTimeUnit;
    import org.openjdk.jmh.annotations.Scope;
    import org.openjdk.jmh.annotations.Setup;
    import org.openjdk.jmh.annotations.State;
    import org.openjdk.jmh.annotations.Warmup;
    import org.openjdk.jmh.runner.Runner;
    import org.openjdk.jmh.runner.RunnerException;
    import org.openjdk.jmh.runner.options.Options;
    import org.openjdk.jmh.runner.options.OptionsBuilder;

    import com.mvohm.quadruple.Quadruple;

    @State(value = Scope.Benchmark)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
    @Fork(value = 1)
    @Warmup(iterations = 3, time = 7)
    @Measurement(iterations = 5, time = 10)
    public class ArrayOperations {

      // To do BigDecimal arithmetic with the precision close to this of Quadruple
      private static final MathContext MC_38 = new MathContext(38, RoundingMode.HALF_EVEN);

      private static final int DATA_SIZE = 0x1_0000;        // 65536
      private static final int INDEX_MASK = DATA_SIZE - 1;  // 0xFFFF

      private static final double RAND_SCALE = 1e39; // To provide a sensible range of operands,
                                                     // so that the actual calculations don't get bypassed

      private final BigDecimal[]
          bdOp1     = new BigDecimal[DATA_SIZE],
          bdOp2     = new BigDecimal[DATA_SIZE],
          bdResult  = new BigDecimal[DATA_SIZE];
      private final Quadruple[]
          qOp1      = new Quadruple[DATA_SIZE],
          qOp2      = new Quadruple[DATA_SIZE],
          qResult   = new Quadruple[DATA_SIZE];

      private int index = 0;

      @Setup
      public void initData() {
        final Random rand = new Random(12345); // for reproducibility
        for (int i = 0; i < DATA_SIZE; i++) {
          bdOp1[i] = randomBigDecimal(rand);
          bdOp2[i] = randomBigDecimal(rand);
          qOp1[i] = randomQuadruple(rand);
          qOp2[i] = randomQuadruple(rand);
        }
      }

      private static Quadruple randomQuadruple(Random rand) {
        return Quadruple.nextNormalRandom(rand).multiply(RAND_SCALE);
      }

      private static BigDecimal randomBigDecimal(Random rand) {
        return Quadruple.nextNormalRandom(rand).multiply(RAND_SCALE).bigDecimalValue();
      }

      @Benchmark
      public void a_bigDecimalAddition() {
        bdResult[index] = bdOp1[index].add(bdOp2[index], MC_38);
        index = ++index & INDEX_MASK;
      }

      @Benchmark
      public void b_quadrupleAddition() {
        qResult[index] = Quadruple.add(qOp1[index], qOp2[index]);
        index = ++index & INDEX_MASK;
      }

      @Benchmark
      public void c_bigDecimalSubtraction() {
        bdResult[index] = bdOp1[index].subtract(bdOp2[index], MC_38);
        index = ++index & INDEX_MASK;
      }

      @Benchmark
      public void d_quadrupleSubtraction() {
        qResult[index] = Quadruple.subtract(qOp1[index], qOp2[index]);
        index = ++index & INDEX_MASK;
      }

      @Benchmark
      public void e_bigDecimalMultiplcation() {
        bdResult[index] = bdOp1[index].multiply(bdOp2[index], MC_38);
        index = ++index & INDEX_MASK;
      }

      @Benchmark
      public void f_quadrupleMultiplcation() {
        qResult[index] = Quadruple.multiply(qOp1[index], qOp2[index]);
        index = ++index & INDEX_MASK;
      }

      @Benchmark
      public void g_bigDecimalDivision() {
        bdResult[index] = bdOp1[index].divide(bdOp2[index], MC_38);
        index = ++index & INDEX_MASK;
      }

      @Benchmark
      public void h_quadrupleDivision() {
        qResult[index] = Quadruple.divide(qOp1[index], qOp2[index]);
        index = ++index & INDEX_MASK;
      }

      private void run(String... args) throws IOException, RunnerException {
        final Options opt = new OptionsBuilder()
            .include(ArrayOperations.class.getSimpleName())
            .forks(1)
            .build();
        new Runner(opt).run();
      }

      public static void main(String... args) throws IOException, RunnerException {
        new ArrayOperations().run(args);
      }

    }
