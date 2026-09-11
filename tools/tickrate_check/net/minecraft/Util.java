package net.minecraft;

import com.google.common.base.Ticker;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.PartialResult;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util.1;
import net.minecraft.Util.10;
import net.minecraft.Util.11;
import net.minecraft.Util.2;
import net.minecraft.Util.5;
import net.minecraft.Util.6;
import net.minecraft.Util.7;
import net.minecraft.Util.8;
import net.minecraft.Util.9;
import net.minecraft.Util.IdentityStrategy;
import net.minecraft.Util.OS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SingleKeyCache;
import net.minecraft.util.TimeSource.NanoTimeSource;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;

public class Util {
   static final Logger f_137446_ = LogUtils.getLogger();
   private static final int f_183935_ = 255;
   private static final String f_183936_ = "max.bg.threads";
   private static final AtomicInteger f_137442_ = new AtomicInteger(1);
   private static final ExecutorService f_137444_ = m_137477_("Main");
   private static final ExecutorService f_137445_ = m_137586_();
   private static final DateTimeFormatter f_241646_ = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss", Locale.ROOT);
   public static NanoTimeSource f_137440_ = System::nanoTime;
   public static final Ticker f_211544_ = new 1();
   public static final UUID f_137441_ = new UUID(0L, 0L);
   public static final FileSystemProvider f_143778_ = FileSystemProvider.installedProviders()
      .stream()
      .filter(p_201865_ -> p_201865_.getScheme().equalsIgnoreCase("jar"))
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("No jar file system provider found"));
   private static Consumer<String> f_183937_ = p_201905_ -> {
   };

   public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> m_137448_() {
      return Collectors.toMap(Entry::getKey, Entry::getValue);
   }

   public static <T extends Comparable<T>> String m_137453_(Property<T> p_137454_, Object p_137455_) {
      return p_137454_.m_6940_((Comparable)p_137455_);
   }

   public static String m_137492_(String p_137493_, @Nullable ResourceLocation p_137494_) {
      return p_137494_ == null ? p_137493_ + ".unregistered_sadface" : p_137493_ + "." + p_137494_.m_135827_() + "." + p_137494_.m_135815_().replace('/', '.');
   }

   public static long m_137550_() {
      return m_137569_() / 1000000L;
   }

   public static long m_137569_() {
      return f_137440_.getAsLong();
   }

   public static long m_137574_() {
      return Instant.now().toEpochMilli();
   }

   public static String m_241986_() {
      return f_241646_.format(ZonedDateTime.now());
   }

   private static ExecutorService m_137477_(String p_137478_) {
      int $$1 = Mth.m_14045_(Runtime.getRuntime().availableProcessors() - 1, 1, m_183993_());
      ExecutorService $$2;
      if ($$1 <= 0) {
         $$2 = MoreExecutors.newDirectExecutorService();
      } else {
         $$2 = new ForkJoinPool($$1, p_201863_ -> {
            ForkJoinWorkerThread $$2x = new 2(p_201863_);
            $$2x.setName("Worker-" + p_137478_ + "-" + f_137442_.getAndIncrement());
            return $$2x;
         }, Util::m_137495_, true);
      }

      return $$2;
   }

   private static int m_183993_() {
      String $$0 = System.getProperty("max.bg.threads");
      if ($$0 != null) {
         try {
            int $$1 = Integer.parseInt($$0);
            if ($$1 >= 1 && $$1 <= 255) {
               return $$1;
            }

            f_137446_.error("Wrong {} property value '{}'. Should be an integer value between 1 and {}.", new Object[]{"max.bg.threads", $$0, 255});
         } catch (NumberFormatException var2) {
            f_137446_.error("Could not parse {} property value '{}'. Should be an integer value between 1 and {}.", new Object[]{"max.bg.threads", $$0, 255});
         }
      }

      return 255;
   }

   public static ExecutorService m_183991_() {
      return f_137444_;
   }

   public static ExecutorService m_183992_() {
      return f_137445_;
   }

   public static void m_137580_() {
      m_137531_(f_137444_);
      m_137531_(f_137445_);
   }

   private static void m_137531_(ExecutorService p_137532_) {
      p_137532_.shutdown();

      boolean $$1;
      try {
         $$1 = p_137532_.awaitTermination(3L, TimeUnit.SECONDS);
      } catch (InterruptedException var3) {
         $$1 = false;
      }

      if (!$$1) {
         p_137532_.shutdownNow();
      }
   }

   private static ExecutorService m_137586_() {
      return Executors.newCachedThreadPool(p_201860_ -> {
         Thread $$1 = new Thread(p_201860_);
         $$1.setName("IO-Worker-" + f_137442_.getAndIncrement());
         $$1.setUncaughtExceptionHandler(Util::m_137495_);
         return $$1;
      });
   }

   public static void m_137559_(Throwable p_137560_) {
      throw p_137560_ instanceof RuntimeException ? (RuntimeException)p_137560_ : new RuntimeException(p_137560_);
   }

   private static void m_137495_(Thread p_137496_, Throwable p_137497_) {
      m_137570_(p_137497_);
      if (p_137497_ instanceof CompletionException) {
         p_137497_ = p_137497_.getCause();
      }

      if (p_137497_ instanceof ReportedException) {
         Bootstrap.m_135875_(((ReportedException)p_137497_).m_134761_().m_127526_());
         System.exit(-1);
      }

      f_137446_.error(String.format(Locale.ROOT, "Caught exception in thread %s", p_137496_), p_137497_);
   }

   @Nullable
   public static Type<?> m_137456_(TypeReference p_137457_, String p_137458_) {
      return !SharedConstants.f_136182_ ? null : m_137551_(p_137457_, p_137458_);
   }

   @Nullable
   private static Type<?> m_137551_(TypeReference p_137552_, String p_137553_) {
      Type<?> $$2 = null;

      try {
         $$2 = DataFixers.m_14512_().getSchema(DataFixUtils.makeKey(SharedConstants.m_183709_().m_183476_().m_193006_())).getChoiceType(p_137552_, p_137553_);
      } catch (IllegalArgumentException var4) {
         f_137446_.error("No data fixer registered for {}", p_137553_);
         if (SharedConstants.f_136183_) {
            throw var4;
         }
      }

      return $$2;
   }

   public static Runnable m_143787_(String p_143788_, Runnable p_143789_) {
      return SharedConstants.f_136183_ ? () -> {
         Thread $$2 = Thread.currentThread();
         String $$3 = $$2.getName();
         $$2.setName(p_143788_);

         try {
            p_143789_.run();
         } finally {
            $$2.setName($$3);
         }
      } : p_143789_;
   }

   public static <V> Supplier<V> m_183946_(String p_183947_, Supplier<V> p_183948_) {
      return SharedConstants.f_136183_ ? () -> {
         Thread $$2 = Thread.currentThread();
         String $$3 = $$2.getName();
         $$2.setName(p_183947_);

         Object var4;
         try {
            var4 = p_183948_.get();
         } finally {
            $$2.setName($$3);
         }

         return (V)var4;
      } : p_183948_;
   }

   public static OS m_137581_() {
      String $$0 = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if ($$0.contains("win")) {
         return OS.WINDOWS;
      } else if ($$0.contains("mac")) {
         return OS.OSX;
      } else if ($$0.contains("solaris")) {
         return OS.SOLARIS;
      } else if ($$0.contains("sunos")) {
         return OS.SOLARIS;
      } else if ($$0.contains("linux")) {
         return OS.LINUX;
      } else {
         return $$0.contains("unix") ? OS.LINUX : OS.UNKNOWN;
      }
   }

   public static Stream<String> m_137582_() {
      RuntimeMXBean $$0 = ManagementFactory.getRuntimeMXBean();
      return $$0.getInputArguments().stream().filter(p_201903_ -> p_201903_.startsWith("-X"));
   }

   public static <T> T m_137509_(List<T> p_137510_) {
      return p_137510_.get(p_137510_.size() - 1);
   }

   public static <T> T m_137466_(Iterable<T> p_137467_, @Nullable T p_137468_) {
      Iterator<T> $$2 = p_137467_.iterator();
      T $$3 = $$2.next();
      if (p_137468_ != null) {
         T $$4 = $$3;

         while ($$4 != p_137468_) {
            if ($$2.hasNext()) {
               $$4 = $$2.next();
            }
         }

         if ($$2.hasNext()) {
            return $$2.next();
         }
      }

      return $$3;
   }

   public static <T> T m_137554_(Iterable<T> p_137555_, @Nullable T p_137556_) {
      Iterator<T> $$2 = p_137555_.iterator();
      T $$3 = null;

      while ($$2.hasNext()) {
         T $$4 = $$2.next();
         if ($$4 == p_137556_) {
            if ($$3 == null) {
               $$3 = (T)($$2.hasNext() ? Iterators.getLast($$2) : p_137556_);
            }
            break;
         }

         $$3 = $$4;
      }

      return $$3;
   }

   public static <T> T m_137537_(Supplier<T> p_137538_) {
      return p_137538_.get();
   }

   public static <T> T m_137469_(T p_137470_, Consumer<T> p_137471_) {
      p_137471_.accept(p_137470_);
      return p_137470_;
   }

   public static <K> Strategy<K> m_137583_() {
      return IdentityStrategy.INSTANCE;
   }

   public static <V> CompletableFuture<List<V>> m_137567_(List<? extends CompletableFuture<V>> p_137568_) {
      if (p_137568_.isEmpty()) {
         return CompletableFuture.completedFuture(List.of());
      } else if (p_137568_.size() == 1) {
         return p_137568_.get(0).thenApply(List::of);
      } else {
         CompletableFuture<Void> $$1 = CompletableFuture.allOf(p_137568_.toArray(new CompletableFuture[0]));
         return $$1.thenApply(p_203746_ -> p_137568_.stream().map(CompletableFuture::join).toList());
      }
   }

   public static <V> CompletableFuture<List<V>> m_143840_(List<? extends CompletableFuture<? extends V>> p_143841_) {
      CompletableFuture<List<V>> $$1 = new CompletableFuture<>();
      return m_214631_(p_143841_, $$1::completeExceptionally).applyToEither($$1, Function.identity());
   }

   public static <V> CompletableFuture<List<V>> m_214684_(List<? extends CompletableFuture<? extends V>> p_214685_) {
      CompletableFuture<List<V>> $$1 = new CompletableFuture<>();
      return m_214631_(p_214685_, p_274642_ -> {
         if ($$1.completeExceptionally(p_274642_)) {
            for (CompletableFuture<? extends V> $$3 : p_214685_) {
               $$3.cancel(true);
            }
         }
      }).applyToEither($$1, Function.identity());
   }

   private static <V> CompletableFuture<List<V>> m_214631_(List<? extends CompletableFuture<? extends V>> p_214632_, Consumer<Throwable> p_214633_) {
      List<V> $$2 = Lists.newArrayListWithCapacity(p_214632_.size());
      CompletableFuture<?>[] $$3 = new CompletableFuture[p_214632_.size()];
      p_214632_.forEach(p_214641_ -> {
         int $$4 = $$2.size();
         $$2.add(null);
         $$3[$$4] = p_214641_.whenComplete((p_214650_, p_214651_) -> {
            if (p_214651_ != null) {
               p_214633_.accept(p_214651_);
            } else {
               $$2.set($$4, (V)p_214650_);
            }
         });
      });
      return CompletableFuture.allOf($$3).thenApply(p_214626_ -> $$2);
   }

   public static <T> Optional<T> m_137521_(Optional<T> p_137522_, Consumer<T> p_137523_, Runnable p_137524_) {
      if (p_137522_.isPresent()) {
         p_137523_.accept(p_137522_.get());
      } else {
         p_137524_.run();
      }

      return p_137522_;
   }

   public static <T> Supplier<T> m_214655_(Supplier<T> p_214656_, Supplier<String> p_214657_) {
      return p_214656_;
   }

   public static Runnable m_137474_(Runnable p_137475_, Supplier<String> p_137476_) {
      return p_137475_;
   }

   public static void m_143785_(String p_143786_) {
      f_137446_.error(p_143786_);
      if (SharedConstants.f_136183_) {
         m_183984_(p_143786_);
      }
   }

   public static void m_200890_(String p_200891_, Throwable p_200892_) {
      f_137446_.error(p_200891_, p_200892_);
      if (SharedConstants.f_136183_) {
         m_183984_(p_200891_);
      }
   }

   public static <T extends Throwable> T m_137570_(T p_137571_) {
      if (SharedConstants.f_136183_) {
         f_137446_.error("Trying to throw a fatal exception, pausing in IDE", p_137571_);
         m_183984_(p_137571_.getMessage());
      }

      return p_137571_;
   }

   public static void m_183969_(Consumer<String> p_183970_) {
      f_183937_ = p_183970_;
   }

   private static void m_183984_(String p_183985_) {
      Instant $$1 = Instant.now();
      f_137446_.warn("Did you remember to set a breakpoint here?");
      boolean $$2 = Duration.between($$1, Instant.now()).toMillis() > 500L;
      if (!$$2) {
         f_183937_.accept(p_183985_);
      }
   }

   public static String m_137575_(Throwable p_137576_) {
      if (p_137576_.getCause() != null) {
         return m_137575_(p_137576_.getCause());
      } else {
         return p_137576_.getMessage() != null ? p_137576_.getMessage() : p_137576_.toString();
      }
   }

   public static <T> T m_214670_(T[] p_214671_, RandomSource p_214672_) {
      return p_214671_[p_214672_.m_188503_(p_214671_.length)];
   }

   public static int m_214667_(int[] p_214668_, RandomSource p_214669_) {
      return p_214668_[p_214669_.m_188503_(p_214668_.length)];
   }

   public static <T> T m_214621_(List<T> p_214622_, RandomSource p_214623_) {
      return p_214622_.get(p_214623_.m_188503_(p_214622_.size()));
   }

   public static <T> Optional<T> m_214676_(List<T> p_214677_, RandomSource p_214678_) {
      return p_214677_.isEmpty() ? Optional.empty() : Optional.of(m_214621_(p_214677_, p_214678_));
   }

   private static BooleanSupplier m_137502_(Path p_137503_, Path p_137504_) {
      return new 5(p_137503_, p_137504_);
   }

   private static BooleanSupplier m_137500_(Path p_137501_) {
      return new 6(p_137501_);
   }

   private static BooleanSupplier m_137561_(Path p_137562_) {
      return new 7(p_137562_);
   }

   private static BooleanSupplier m_137572_(Path p_137573_) {
      return new 8(p_137573_);
   }

   private static boolean m_137548_(BooleanSupplier... p_137549_) {
      for (BooleanSupplier $$1 : p_137549_) {
         if (!$$1.getAsBoolean()) {
            f_137446_.warn("Failed to execute {}", $$1);
            return false;
         }
      }

      return true;
   }

   private static boolean m_137449_(int p_137450_, String p_137451_, BooleanSupplier... p_137452_) {
      for (int $$3 = 0; $$3 < p_137450_; $$3++) {
         if (m_137548_(p_137452_)) {
            return true;
         }

         f_137446_.error("Failed to {}, retrying {}/{}", new Object[]{p_137451_, $$3, p_137450_});
      }

      f_137446_.error("Failed to {}, aborting, progress might be lost", p_137451_);
      return false;
   }

   public static void m_137462_(File p_137463_, File p_137464_, File p_137465_) {
      m_137505_(p_137463_.toPath(), p_137464_.toPath(), p_137465_.toPath());
   }

   public static void m_137505_(Path p_137506_, Path p_137507_, Path p_137508_) {
      m_212229_(p_137506_, p_137507_, p_137508_, false);
   }

   public static void m_212224_(File p_212225_, File p_212226_, File p_212227_, boolean p_212228_) {
      m_212229_(p_212225_.toPath(), p_212226_.toPath(), p_212227_.toPath(), p_212228_);
   }

   public static void m_212229_(Path p_212230_, Path p_212231_, Path p_212232_, boolean p_212233_) {
      int $$4 = 10;
      if (!Files.exists(p_212230_) || m_137449_(10, "create backup " + p_212232_, m_137500_(p_212232_), m_137502_(p_212230_, p_212232_), m_137572_(p_212232_))) {
         if (m_137449_(10, "remove old " + p_212230_, m_137500_(p_212230_), m_137561_(p_212230_))) {
            if (!m_137449_(10, "replace " + p_212230_ + " with " + p_212231_, m_137502_(p_212231_, p_212230_), m_137572_(p_212230_)) && !p_212233_) {
               m_137449_(10, "restore " + p_212230_ + " from " + p_212232_, m_137502_(p_212232_, p_212230_), m_137572_(p_212230_));
            }
         }
      }
   }

   public static int m_137479_(String p_137480_, int p_137481_, int p_137482_) {
      int $$3 = p_137480_.length();
      if (p_137482_ >= 0) {
         for (int $$4 = 0; p_137481_ < $$3 && $$4 < p_137482_; $$4++) {
            if (Character.isHighSurrogate(p_137480_.charAt(p_137481_++)) && p_137481_ < $$3 && Character.isLowSurrogate(p_137480_.charAt(p_137481_))) {
               p_137481_++;
            }
         }
      } else {
         for (int $$5 = p_137482_; p_137481_ > 0 && $$5 < 0; $$5++) {
            p_137481_--;
            if (Character.isLowSurrogate(p_137480_.charAt(p_137481_)) && p_137481_ > 0 && Character.isHighSurrogate(p_137480_.charAt(p_137481_ - 1))) {
               p_137481_--;
            }
         }
      }

      return p_137481_;
   }

   public static Consumer<String> m_137489_(String p_137490_, Consumer<String> p_137491_) {
      return p_214645_ -> p_137491_.accept(p_137490_ + p_214645_);
   }

   public static DataResult<int[]> m_137539_(IntStream p_137540_, int p_137541_) {
      int[] $$2 = p_137540_.limit((long)(p_137541_ + 1)).toArray();
      if ($$2.length != p_137541_) {
         Supplier<String> $$3 = () -> "Input is not a list of " + p_137541_ + " ints";
         return $$2.length >= p_137541_ ? DataResult.error($$3, Arrays.copyOf($$2, p_137541_)) : DataResult.error($$3);
      } else {
         return DataResult.success($$2);
      }
   }

   public static DataResult<long[]> m_287262_(LongStream p_287579_, int p_287631_) {
      long[] $$2 = p_287579_.limit((long)(p_287631_ + 1)).toArray();
      if ($$2.length != p_287631_) {
         Supplier<String> $$3 = () -> "Input is not a list of " + p_287631_ + " longs";
         return $$2.length >= p_287631_ ? DataResult.error($$3, Arrays.copyOf($$2, p_287631_)) : DataResult.error($$3);
      } else {
         return DataResult.success($$2);
      }
   }

   public static <T> DataResult<List<T>> m_143795_(List<T> p_143796_, int p_143797_) {
      if (p_143796_.size() != p_143797_) {
         Supplier<String> $$2 = () -> "Input is not a list of " + p_143797_ + " elements";
         return p_143796_.size() >= p_143797_ ? DataResult.error($$2, p_143796_.subList(0, p_143797_)) : DataResult.error($$2);
      } else {
         return DataResult.success(p_143796_);
      }
   }

   public static void m_137584_() {
      Thread $$0 = new 9("Timer hack thread");
      $$0.setDaemon(true);
      $$0.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(f_137446_));
      $$0.start();
   }

   public static void m_137563_(Path p_137564_, Path p_137565_, Path p_137566_) throws IOException {
      Path $$3 = p_137564_.relativize(p_137566_);
      Path $$4 = p_137565_.resolve($$3);
      Files.copy(p_137566_, $$4);
   }

   public static String m_137483_(String p_137484_, CharPredicate p_137485_) {
      return p_137484_.toLowerCase(Locale.ROOT)
         .chars()
         .mapToObj(p_214666_ -> p_137485_.m_125854_((char)p_214666_) ? Character.toString((char)p_214666_) : "_")
         .collect(Collectors.joining());
   }

   public static <K, V> SingleKeyCache<K, V> m_269175_(Function<K, V> p_270326_) {
      return new SingleKeyCache(p_270326_);
   }

   public static <T, R> Function<T, R> m_143827_(Function<T, R> p_143828_) {
      return new 10(p_143828_);
   }

   public static <T, U, R> BiFunction<T, U, R> m_143821_(BiFunction<T, U, R> p_143822_) {
      return new 11(p_143822_);
   }

   public static <T> List<T> m_214661_(Stream<T> p_214662_, RandomSource p_214663_) {
      ObjectArrayList<T> $$2 = p_214662_.collect(ObjectArrayList.toList());
      m_214673_($$2, p_214663_);
      return $$2;
   }

   public static IntArrayList m_214658_(IntStream p_214659_, RandomSource p_214660_) {
      IntArrayList $$2 = IntArrayList.wrap(p_214659_.toArray());
      int $$3 = $$2.size();

      for (int $$4 = $$3; $$4 > 1; $$4--) {
         int $$5 = p_214660_.m_188503_($$4);
         $$2.set($$4 - 1, $$2.set($$5, $$2.getInt($$4 - 1)));
      }

      return $$2;
   }

   public static <T> List<T> m_214681_(T[] p_214682_, RandomSource p_214683_) {
      ObjectArrayList<T> $$2 = new ObjectArrayList(p_214682_);
      m_214673_($$2, p_214683_);
      return $$2;
   }

   public static <T> List<T> m_214611_(ObjectArrayList<T> p_214612_, RandomSource p_214613_) {
      ObjectArrayList<T> $$2 = new ObjectArrayList(p_214612_);
      m_214673_($$2, p_214613_);
      return $$2;
   }

   public static <T> void m_214673_(ObjectArrayList<T> p_214674_, RandomSource p_214675_) {
      int $$2 = p_214674_.size();

      for (int $$3 = $$2; $$3 > 1; $$3--) {
         int $$4 = p_214675_.m_188503_($$3);
         p_214674_.set($$3 - 1, p_214674_.set($$4, p_214674_.get($$3 - 1)));
      }
   }

   public static <T> CompletableFuture<T> m_214679_(Function<Executor, CompletableFuture<T>> p_214680_) {
      return m_214652_(p_214680_, CompletableFuture::isDone);
   }

   public static <T> T m_214652_(Function<Executor, T> p_214653_, Predicate<T> p_214654_) {
      BlockingQueue<Runnable> $$2 = new LinkedBlockingQueue<>();
      T $$3 = p_214653_.apply($$2::add);

      while (!p_214654_.test($$3)) {
         try {
            Runnable $$4 = $$2.poll(100L, TimeUnit.MILLISECONDS);
            if ($$4 != null) {
               $$4.run();
            }
         } catch (InterruptedException var5) {
            f_137446_.warn("Interrupted wait");
            break;
         }
      }

      int $$6 = $$2.size();
      if ($$6 > 0) {
         f_137446_.warn("Tasks left in queue: {}", $$6);
      }

      return $$3;
   }

   public static <T> ToIntFunction<T> m_214686_(List<T> p_214687_) {
      return m_214634_(p_214687_, Object2IntOpenHashMap::new);
   }

   public static <T> ToIntFunction<T> m_214634_(List<T> p_214635_, IntFunction<Object2IntMap<T>> p_214636_) {
      Object2IntMap<T> $$2 = p_214636_.apply(p_214635_.size());

      for (int $$3 = 0; $$3 < p_214635_.size(); $$3++) {
         $$2.put(p_214635_.get($$3), $$3);
      }

      return $$2;
   }

   public static <T, E extends Exception> T m_260975_(DataResult<T> p_261812_, Function<String, E> p_261468_) throws E {
      Optional<PartialResult<T>> $$2 = p_261812_.error();
      if ($$2.isPresent()) {
         throw p_261468_.apply($$2.get().message());
      } else {
         return (T)p_261812_.result().orElseThrow();
      }
   }

   public static boolean m_288213_(int p_289004_) {
      return Character.isWhitespace(p_289004_) || Character.isSpaceChar(p_289004_);
   }

   public static boolean m_288217_(@Nullable String p_288983_) {
      return p_288983_ != null && p_288983_.length() != 0 ? p_288983_.chars().allMatch(Util::m_288213_) : true;
   }
}
