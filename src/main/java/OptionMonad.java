import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;
import static io.vavr.Patterns.$Tuple2;

import helpers.MethodExecutor;
import io.vavr.Tuple2;
import io.vavr.control.Option;

public class OptionMonad {

  public static final String DEFAULT = "default";
  private final MethodExecutor exec;

  public OptionMonad(MethodExecutor exec) {
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
      exec.methodOne(nullableValue);
    }
  }

  // do it the functional way
  public void vavrNullConditionalExecution(String nullableValue) {
    Option.of(nullableValue)
      .forEach(exec::methodOne);
  }


  // instead of
  public void legacyConditionalExecutionOfTheSameMethod(String nullableValue) {
    if (nullableValue == null) {
      exec.methodOne(DEFAULT);
    } else {
      exec.methodOne(nullableValue);
    }
  }

  // do it the functional way
  public void vavrConditionalExecutionOfTheSameMethod(String nullableValue) {
    Option.of(nullableValue)
      .orElse(Option.of(DEFAULT))
      .forEach(exec::methodOne);
  }


  // instead of
  public void legacyConditionalExecutionOfDifferentMethods(String nullableValue) {
    if (nullableValue == null) {
      exec.methodOne(DEFAULT);
    } else {
      exec.methodTwo(nullableValue);
    }
  }

  // do it the functional way
  public void vavrConditionalExecutionOfDifferentMethods(String nullableValue) {
    Option.of(nullableValue)
      .peek(exec::methodTwo)
      .onEmpty(() -> exec.methodOne(DEFAULT));
  }


  // instead of
  public void legacyConditionalException(String nullableValue) {
    if (nullableValue != null) {
      exec.methodOne(nullableValue);
    } else {
      throw new RuntimeException("don't like nulls");
    }
  }

  // do it the functional way
  public void vavrConditionalException(String nullableValue) {
    Option.of(nullableValue)
      .peek(exec::methodOne)
      .getOrElseThrow(() -> new RuntimeException("don't like nulls"));
  }


  // instead of
  public String legacyComplexAndConditional(String nullableValue) {
    if (nullableValue != null && nullableValue.startsWith("ONE")) {
      return "1st condition";
    } else {
      return DEFAULT;
    }
  }

  // do it the functional way
  public String vavrComplexAndConditional(String nullableValue) {
    return Option.of(nullableValue)
      .filter(given -> given.startsWith("ONE"))
      .map(ignore -> "1st condition")
      .getOrElse(DEFAULT);
  }


  // instead of
  public String legacyComplexOrConditional(String nullableValue) {
    if (nullableValue == null || nullableValue.startsWith("ONE")) {
      return "1st condition";
    } else {
      return DEFAULT;
    }
  }

  // do it the functional way (not necessarily)
  public String vavrComplexOrConditional(String nullableValue) {
    return Option.of(nullableValue)
      .orElse(Option.of("ONE"))
      .filter(given -> given.startsWith("ONE"))
      .map(ignore -> "1st condition")
      .getOrElse(DEFAULT);
  }


  // instead of
  public void legacyNestedConditionWithMultipleValues(String nullableValueA, String nullableValueB) {
    if (nullableValueA == null && nullableValueB == null) {
      exec.methodOne(DEFAULT);
    } else if (nullableValueA == null) {  // nullableValueB != null
      exec.methodOne(nullableValueB);
    } else if (nullableValueB == null) {  // nullableValueA != null
      exec.methodTwo(nullableValueA);
    } else { // nullableValueA != null && nullableValueB != null
      exec.methodThree(DEFAULT);
    }
  }

  // do it the functional way
  public void vavrNestedConditionWithMultipleValues(String nullableValueA, String nullableValueB) {
    Match(new Tuple2<>(Option.of(nullableValueA), Option.of(nullableValueB))).of(
      Case($Tuple2($None(), $None()), () -> run(() -> exec.methodOne(DEFAULT))),
      Case($Tuple2($None(), $Some($())), () -> run(() -> exec.methodOne(nullableValueB))),
      Case($Tuple2($Some($()), $None()), () -> run(() -> exec.methodTwo(nullableValueA))),
      Case($Tuple2($Some($()), $Some($())), () -> run(() -> exec.methodThree(DEFAULT)))
    );
  }


  // instead of
  public String legacyNestedConditionsWithReturn(String nullableValue) {
    if (nullableValue == null) {
      return DEFAULT;
    } else if (nullableValue.startsWith("ONE")) {
      return nullableValue.concat("123");
    } else if (nullableValue.startsWith("TWO")) {
      return nullableValue.concat("456");
    } else {
      return nullableValue.concat("789");
    }
  }

  // do it the functional way
  public String vavrNestedConditionsWithReturn(String nullableValue) {
    return Match(Option.of(nullableValue)).of(
      Case($None(), () -> DEFAULT),
      Case($Some($(v -> v.startsWith("ONE"))), () -> nullableValue.concat("123")),
      Case($Some($(v -> v.startsWith("TWO"))), () -> nullableValue.concat("456")),
      Case($Some($()), () -> nullableValue.concat("789"))
    );
  }


  // instead of
  public void legacyNestedConditionsWithExecution(String nullableValue) {
    if (nullableValue == null) {
      exec.methodOne(DEFAULT);
    } else if (nullableValue.startsWith("ONE")) {
      exec.methodOne(nullableValue);
    } else if (nullableValue.startsWith("TWO")) {
      exec.methodTwo(nullableValue);
    } else {
      exec.methodThree(nullableValue);
    }
  }

  // do it the functional way
  public void vavrNestedConditionsWithExecution(String nullableValue) {
    Match(Option.of(nullableValue)).of(
      Case($None(), () -> run(() -> exec.methodOne(DEFAULT))),
      Case($Some($(v -> v.startsWith("ONE"))), () -> run(() -> exec.methodOne(nullableValue))),
      Case($Some($(v -> v.startsWith("TWO"))), () -> run(() -> exec.methodTwo(nullableValue))),
      Case($Some($()), () -> run(() -> exec.methodThree(nullableValue)))
    );
  }

}
