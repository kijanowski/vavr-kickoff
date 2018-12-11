import static helpers.TryHelper.anotherTryWrapper;
import static helpers.TryHelper.eitherWrapper;
import static helpers.TryHelper.optionWrapper;
import static helpers.TryHelper.wrapThrowOnXYZ;

import java.util.function.Function;

import helpers.TryHelper;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

public class FlatMapSamples {

  public List<String> flatMapListOfLists(List<String> a1, List<String> a2, List<String> a3, List<String> a4) {
    List<List<String>> listOfLists = List.of(a1, a2, a3, a4);
    return listOfLists.flatMap(innerList -> innerList);
  }

  public List<String> flatMapListOfListsWithInnerMap(List<String> a1, List<String> a2, List<String> a3, List<String> a4) {
    List<List<String>> listOfLists = List.of(a1, a2, a3, a4);
    return listOfLists.flatMap(innerList -> innerList.map(s -> s.toLowerCase()));
  }

  public List<String> flatMapListOfOptions(Option<String> a1, Option<String> a2, Option<String> a3) {
    List<Option<String>> b = List.of(a1, a2, a3);
    return b.flatMap(innerOption -> innerOption);
  }

  public List<String> flatMapListOfOptionsWithInnerMap(Option<String> a1, Option<String> a2, Option<String> a3) {
    List<Option<String>> b = List.of(a1, a2, a3);
    return b.flatMap(innerOption -> innerOption.map(wrappedString -> wrappedString.toLowerCase()));
  }

  public List<String> flatMapListOfTries(String a1, String a2, String a3) {
    Try<String> a1Try = Try.of(() -> TryHelper.throwOnXYZ(a1));
    Try<String> a2Try = Try.of(() -> TryHelper.throwOnXYZ(a2));
    Try<String> a3Try = Try.of(() -> TryHelper.throwOnXYZ(a3));

    List<Try<String>> b = List.of(a1Try, a2Try, Try.failure(new RuntimeException("RE")), a3Try);
    return b.flatMap(Function.identity());
  }

  public Try<String> flatMapTryOfTry(String text) {
    Try<Try<String>> tryOfTry = wrapThrowOnXYZ(text) // start a Try context
      .map(s -> anotherTryWrapper(s)); // we're inside map - a Try context - and call another method returning a Try leaving us with a Try<Try<String>>

    /* !! NOT THE WAY TO GO !! */ return tryOfTry.get(); /* !! NOT THE WAY TO GO !! */
  }

  public Try<String> properFlatMapTryOfTry(String text) {
    Try<String> tryOf = wrapThrowOnXYZ(text)
      .flatMap(s -> anotherTryWrapper(s));

    return tryOf;
  }

  public Option<String> flatMapOptionOfOption(String nullableText) {
    Option<Option<String>> optionOfOption = Option
      .of(nullableText)
      .map(s -> optionWrapper(s)); // we're inside map - an Option context - abd call another method returning an Option leaving us with an Option<Option<String>>

    /* !! NOT THE WAY TO GO !! */ return optionOfOption.get(); /* !! NOT THE WAY TO GO !! */
  }

  public Option<String> properFlatMapOptionOfOption(String nullableText) {
    Option<String> option = Option
      .of(nullableText)
      .flatMap(s -> optionWrapper(s));

    return option;
  }

  public Either<String, Integer> properFlatMapOfEither(String age) {
    Either<String, String> outerEither = Option
      .of(age)
      .map(Either::<String, String>right)
      .getOrElse(Either.left("empty"));

    Either<String, Integer> flattened = outerEither
      .flatMap(e -> eitherWrapper(e));

    return flattened;
  }

}
