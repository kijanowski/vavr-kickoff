import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;
import static io.vavr.Patterns.$Tuple2;

import helpers.LolExecutor;
import io.vavr.Tuple2;
import io.vavr.control.Option;

public class OptionMonad {

  public static final String DEFAULT = "default";
  private final LolExecutor exec;

  public OptionMonad(LolExecutor exec) {
    this.exec = exec;
  }

  // instead of
  public String legacyNullCheck(String nullableValue) {
    if (nullableValue != null) {
      return nullableValue.toLowerCase();
    }
    return nullableValue;
  }

  // do it the functional way
  public String vavrNullCheck(String nullableValue) {
    return Option.of(nullableValue)
      .map(String::toLowerCase)
      .getOrElse(nullableValue);
  }


  // instead of
  public void legacyNullConditionalExecution(String nullableValue) {
    if (nullableValue != null) {
      exec.lol(nullableValue);
    }
  }

  // do it the functional way
  public void vavrNullConditionalExecution(String nullableValue) {
    Option.of(nullableValue)
      .forEach(exec::lol);
  }


  // instead of
  public void legacyConditionalExecutionOfTheSameMethod(String nullableValue) {
    if (nullableValue == null) {
      exec.lol(DEFAULT);
    } else {
      exec.lol(nullableValue);
    }
  }

  // do it the functional way
  public void vavrConditionalExecutionOfTheSameMethod(String nullableValue) {
    Option.of(nullableValue)
      .orElse(Option.of(DEFAULT))
      .forEach(exec::lol);
  }


  // instead of
  public void legacyConditionalExecutionOfDifferentMethods(String nullableValue) {
    if (nullableValue == null) {
      exec.lol(DEFAULT);
    } else {
      exec.yolo(nullableValue);
    }
  }

  // do it the functional way
  public void vavrConditionalExecutionOfDifferentMethods(String nullableValue) {
    Option.of(nullableValue)
      .peek(exec::yolo)
      .onEmpty(() -> exec.lol(DEFAULT));
  }


  // instead of
  public void legacyConditionalException(String nullableValue) {
    if (nullableValue != null) {
      exec.lol(nullableValue);
    } else {
      throw new RuntimeException("don't like nulls");
    }
  }

  // do it the functional way
  public void vavrConditionalException(String nullableValue) {
    Option.of(nullableValue)
      .peek(exec::lol)
      .getOrElseThrow(() -> new RuntimeException("don't like nulls"));
  }


  // instead of
  public String legacyComplexAndConditional(String nullableValue) {
    if (nullableValue != null && nullableValue.startsWith("LOL")) {
      return "1st condition";
    } else {
      return DEFAULT;
    }
  }

  // do it the functional way
  public String vavrComplexAndConditional(String nullableValue) {
    return Option.of(nullableValue)
      .filter(given -> given.startsWith("LOL"))
      .map(ignore -> "1st condition")
      .getOrElse(DEFAULT);
  }


  // instead of
  public String legacyComplexOrConditional(String nullableValue) {
    if (nullableValue == null || nullableValue.startsWith("LOL")) {
      return "1st condition";
    } else {
      return DEFAULT;
    }
  }

  // do it the functional way (not necessarily)
  public String vavrComplexOrConditional(String nullableValue) {
    return Option.of(nullableValue)
      .orElse(Option.of("LOL"))
      .filter(given -> given.startsWith("LOL"))
      .map(ignore -> "1st condition")
      .getOrElse(DEFAULT);
  }


  // instead of
  public void legacyNestedConditionWithMultipleValues(String nullableValueA, String nullableValueB) {
    if (nullableValueA == null && nullableValueB == null) {
      exec.lol(DEFAULT);
    } else if (nullableValueA == null) {  // nullableValueB != null
      exec.lol(nullableValueB);
    } else if (nullableValueB == null) {  // nullableValueA != null
      exec.yolo(nullableValueA);
    } else { // nullableValueA != null && nullableValueB != null
      exec.xD(DEFAULT);
    }
  }

  // do it the functional way
  public void vavrNestedConditionWithMultipleValues(String nullableValueA, String nullableValueB) {
    Match(new Tuple2<>(Option.of(nullableValueA), Option.of(nullableValueB))).of(
      Case($Tuple2($None(), $None()), () -> run(() -> exec.lol(DEFAULT))),
      Case($Tuple2($None(), $Some($())), () -> run(() -> exec.lol(nullableValueB))),
      Case($Tuple2($Some($()), $None()), () -> run(() -> exec.yolo(nullableValueA))),
      Case($Tuple2($Some($()), $Some($())), () -> run(() -> exec.xD(DEFAULT)))
    );
  }


  // instead of
  public String legacyNestedConditionsWithReturn(String nullableValue) {
    if (nullableValue == null) {
      return DEFAULT;
    } else if (nullableValue.startsWith("LOL")) {
      return nullableValue.concat("123");
    } else if (nullableValue.startsWith("YOLO")) {
      return nullableValue.concat("456");
    } else {
      return nullableValue.concat("789");
    }
  }

  // do it the functional way
  public String vavrNestedConditionsWithReturn(String nullableValue) {
    return Match(Option.of(nullableValue)).of(
      Case($None(), () -> DEFAULT),
      Case($Some($(v -> v.startsWith("LOL"))), () -> nullableValue.concat("123")),
      Case($Some($(v -> v.startsWith("YOLO"))), () -> nullableValue.concat("456")),
      Case($Some($()), () -> nullableValue.concat("789"))
    );
  }


  // instead of
  public void legacyNestedConditionsWithExecution(String nullableValue) {
    if (nullableValue == null) {
      exec.lol(DEFAULT);
    } else if (nullableValue.startsWith("LOL")) {
      exec.lol(nullableValue);
    } else if (nullableValue.startsWith("YOLO")) {
      exec.yolo(nullableValue);
    } else {
      exec.xD(nullableValue);
    }
  }

  // do it the functional way
  public void vavrNestedConditionsWithExecution(String nullableValue) {
    Match(Option.of(nullableValue)).of(
      Case($None(), () -> run(() -> exec.lol(DEFAULT))),
      Case($Some($(v -> v.startsWith("LOL"))), () -> run(() -> exec.lol(nullableValue))),
      Case($Some($(v -> v.startsWith("YOLO"))), () -> run(() -> exec.yolo(nullableValue))),
      Case($Some($()), () -> run(() -> exec.xD(nullableValue)))
    );
  }

}
