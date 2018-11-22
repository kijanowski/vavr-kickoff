import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import helpers.Skills;
import io.vavr.collection.List;
import io.vavr.control.Option;

public class ListWrapper {

  // instead of
  private static final java.util.Map<Integer, String> myMap = new java.util.HashMap<>();
  static {
    myMap.put(1, "one");
    myMap.put(2, "two");
  }

  // or
  private static final java.util.Map<Integer, String> myJava9Map = java.util.Map.of(
    1, "one",
    2, "two"
  );

  // do it the functional way
  private static final io.vavr.collection.Map<Integer, String> myVavrMap = io.vavr.collection.HashMap.of(
    1, "one",
    2, "two"
  );

  // instead of
  public java.util.List<Integer> legacyIterate(Integer... ints) {
    return java.util.Arrays
      .stream(ints)
      .map(i -> i + 1)
      .peek(System.out::println)
      .collect(Collectors.toList());
  }

  // do it the functional way
  public io.vavr.collection.Seq<Integer> vavrIterate(Integer... ints) {
    return io.vavr.collection.Stream
      .of(ints)
      .map(i -> i + 1)
      .peek(System.out::println);
  }


  // instead of
  public String legacyJoin(String... args) {
    return java.util.Arrays
      .stream(args)
      .collect(Collectors.joining(", "));
  }

  // do it the functional way
  public String vavrJoin(String... args) {
    return io.vavr.collection.List
      .of(args)
      .mkString(", ");
  }

  // or just be aware of what the JDK offers
  public String stringJavaJoiner(String... args) {
    return String.join(", ", args);
  }

  // instead of
  public java.util.List<String> legacyIteratingWithIndex1(String... args) {
    java.util.List<String> result = new ArrayList<>();

    for (int i = 0; i < args.length; i++) {
      result.add(i + ". " + args[i]);
    }
    return result;
  }

  // or
  public java.util.List<String> legacyIteratingWithIndex2(String... args) {
    return java.util.stream.IntStream
      .range(0, args.length)
      .mapToObj(index -> index + ". " + args[index])
      .collect(Collectors.toList());
  }

  // do it the functional way
  public io.vavr.collection.Seq<String> vavrIteratingWithIndex(String... args) {
    return List
      .of(args)
      .zipWithIndex((arg, index) -> index + ". " + arg);
  }

  // instead of
  public java.util.List<String> legacyFlatMap1(java.util.List<Optional<String>> args) {
    return args
      .stream()
      .filter(Optional::isPresent)
      .map(Optional::get)
      .map(String::toLowerCase)
      .collect(Collectors.toList());
  }

  // or
  public java.util.List<String> legacyFlatMap2(java.util.List<Optional<String>> args) {
    return args
      .stream()
      .flatMap(arg -> arg.map(Stream::of).orElseGet(Stream::empty))
      .map(String::toLowerCase)
      .collect(Collectors.toList());
  }

  // or
  public java.util.List<String> legacyFlatMap3(java.util.List<Optional<String>> args) {
    // since Java 9 introducing Optional.stream()
    return args
      .stream()
      .flatMap(Optional::stream)
      .map(String::toLowerCase)
      .collect(Collectors.toList());
  }

  // do it the functional way
  public io.vavr.collection.Seq vavrFlatMap(io.vavr.collection.Seq<Option<String>> args) {
    return args
      .flatMap(Function.identity()) // sames as .flatMap(arg -> arg)
      .map(String::toLowerCase);
  }

  // instead of
  public Skills legacyFold(java.util.List<String> args) {
    Skills skills = new Skills(args.get(0));
    args.remove(0); // someone may be surprised that passing you a list makes the first element disappearing
    args.forEach(skills::addSkill);
    return skills;
  }

  // do it the functional way
  public Skills vavrFold(io.vavr.collection.Seq<String> args) {
    return args
      .tail()
      .foldLeft(new Skills(args.head()), (skills, arg) -> skills.addSkill(arg));
  }

  // instead of
  public java.util.Map legacyJoiningLists1(java.util.List<String> fruits, java.util.List<BigDecimal> priceList) {
    java.util.Map<String, BigDecimal> map = new java.util.HashMap<String, BigDecimal>();
    for (int i = 0; i < fruits.size(); i++) {
      BigDecimal price = i < priceList.size() ? priceList.get(i) : BigDecimal.ZERO;
      map.put(fruits.get(i), price);
    }
    return map;
  }
  // or
  public java.util.Map legacyJoiningLists2(java.util.List<String> fruits, java.util.List<BigDecimal> priceList) {
    java.util.Map<String, BigDecimal> map = new java.util.HashMap<String, BigDecimal>();
    Iterator<String> i1 = fruits.iterator();
    Iterator<BigDecimal> i2 = priceList.iterator();
    while (i1.hasNext()) {
      BigDecimal price = i2.hasNext() ? i2.next() : BigDecimal.ZERO;
      map.put(i1.next(), price);
    }
    return map;
  }
  // or
  public java.util.Map legacyJoiningLists3(java.util.List<String> fruits, java.util.List<BigDecimal> priceList) {
    return java.util.stream.IntStream
      .range(0, fruits.size())
      .boxed()
      .collect(Collectors.toMap(
        fruits::get,
        index -> index < priceList.size() ? priceList.get(index) : BigDecimal.ZERO)
      );
  }

  // do it the functional way
  public io.vavr.collection.Map<String, BigDecimal> vavrJoiningLists(io.vavr.collection.Seq<String> fruits,
                                                                     io.vavr.collection.Seq<BigDecimal> priceList) {
    return fruits
      .zipAll(priceList.take(fruits.size()), "ignored", BigDecimal.ZERO)
      .toMap(pair -> pair._1, pair -> pair._2);
  }

}
