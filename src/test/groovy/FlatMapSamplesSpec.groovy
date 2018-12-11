import io.vavr.collection.List
import io.vavr.control.Option
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class FlatMapSamplesSpec extends Specification {

  @Subject
  FlatMapSamples flatMapSamples = new FlatMapSamples()

  def "should flatten a list of lists"() {
    given:
    def a1 = List.of("A", "B", "C")
    def a2 = List.of("D", "E", "F")
    def a3 = List.of("G", "H", "I")

    when:
    def result = flatMapSamples.flatMapListOfLists(a1, a2, List.empty(), a3)

    then:
    result == List.of("A", "B", "C", "D", "E", "F", "G", "H", "I")
  }

  def "should flatten a list of lists and apply a map function"() {
    given:
    def a1 = List.of("A", "B", "C")
    def a2 = List.of("D", "E", "F")
    def a3 = List.of("G", "H", "I")

    when:
    def result = flatMapSamples.flatMapListOfListsWithInnerMap(a1, a2, List.empty(), a3)

    then:
    result == List.of("a", "b", "c", "d", "e", "f", "g", "h", "i")
  }

  def "should flatten a list of options"() {
    given:
    def a1 = Option.some("A")
    def a2 = Option.of((String) null)
    def a3 = Option.some("C")

    when:
    def result = flatMapSamples.flatMapListOfOptions(a1, a2, a3)

    then:
    result == List.of("A", "C")
  }

  def "should flatten a list of options and apply a map function"() {
    given:
    def a1 = Option.some("A")
    def a2 = Option.of((String) null)
    def a3 = Option.some("C")

    when:
    def result = flatMapSamples.flatMapListOfOptionsWithInnerMap(a1, a2, a3)

    then:
    result == List.of("a", "c")
  }

  def "should flatten a list of tries"() {
    when:
    def result = flatMapSamples.flatMapListOfTries("A", "XYZ", "C")

    then:
    result == List.of("A", "C")
  }

  def "should not flatten a Try of Try"() {
    when:
    flatMapSamples.flatMapTryOfTry("XYZ")

    then:
    RuntimeException exc = thrown()
    exc.message == "XYZ provided"
  }

  def "should flatten a Try of Try (success only)"() {
    when:
    def result = flatMapSamples.flatMapTryOfTry("ABC")

    then:
    result.isFailure()
    result.getCause().getMessage() == "ABC provided"
  }

  @Unroll
  def "should properly flatten a Try of Try failure"() {
    when:
    def result = flatMapSamples.properFlatMapTryOfTry(input)

    then:
    result.isFailure()
    result.getCause().getMessage() == message

    where:
    input | message
    "XYZ" | "XYZ provided"
    "ABC" | "ABC provided"
  }

  def "should properly flatten a Try of Try success"() {
    when:
    def result = flatMapSamples.properFlatMapTryOfTry("DEF")

    then:
    result.isSuccess()
    result.get() == "DEF"
  }

  def "should not flatten an Option of Option"() {
    when:
    flatMapSamples.flatMapOptionOfOption(null)

    then:
    RuntimeException exc = thrown()
    exc.message == "No value present"
  }

  @Unroll
  def "should flatten an Option of Option (defined only)"() {
    when:
    def result = flatMapSamples.flatMapOptionOfOption(input)

    then:
    result.isEmpty() == isEmpty

    where:
    input | isEmpty
    "ABC" | false
    "DEF" | true
  }


  def "should properly flatten an Option of defined Option"() {
    when:
    def result = flatMapSamples.properFlatMapOptionOfOption("ABC")

    then:
    result.isDefined()
    result.get() == "ABC"
  }

  @Unroll
  def "should properly flatten an Option of undefined Option"() {
    when:
    def result = flatMapSamples.properFlatMapOptionOfOption(input)

    then:
    result.isEmpty() == isEmpty

    where:
    input | isEmpty
    null  | true
    "DEF" | true
  }

  def "should properly flatten an Either of Either"() {
    when:
    def result = flatMapSamples.properFlatMapOfEither("12")

    then:
    result.get() == 12

  }

  @Unroll
  def "should properly flatten an Either of left Either"() {
    when:
    def result = flatMapSamples.properFlatMapOfEither(input)

    then:
    result.isLeft()
    result.getLeft() == value

    where:
    input | value
    null  | "empty"
    "DEF" | "DEF"
  }
}
