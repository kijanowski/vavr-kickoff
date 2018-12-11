package helpers;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

public class TryHelper {

  public static String throwOnABC(String abc) {
    if ("ABC".equals(abc)) throw new RuntimeException("ABC provided");
    return abc;
  }

  public static String throwOnXYZ(String xyz) {
    if ("XYZ".equals(xyz)) throw new RuntimeException("XYZ provided");
    return xyz;
  }

  public static Try<String> wrapThrowOnXYZ(String xyz) {
    return Try.of(() -> throwOnXYZ(xyz));
  }

  public static Try<String> anotherTryWrapper(String abc) {
    return Try.of(() -> throwOnABC(abc));
  }

  public static Option<String> optionWrapper(String abc) {
    return "ABC".equals(abc) ? Option.some(abc) : Option.none();
  }

  public static Either<String, Integer> eitherWrapper(String value) {
    return Try
      .of(() -> value)
      .map(v -> Either.<String, Integer>right(Integer.valueOf(v)))
      .getOrElse(() -> Either.left(value));
  }

}
