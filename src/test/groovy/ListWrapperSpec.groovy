import io.vavr.control.Option
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class ListWrapperSpec extends Specification {

  @Subject
  ListWrapper wrapper = new ListWrapper()

  @Unroll
  def "should static maps be initialized"() {
    given:
    def result = [1: "one", 2: "two"]

    expect:
    wrapper.myMap == result
    wrapper.myJava9Map == result
    wrapper.myVavrMap.toJavaMap() == result

  }

  @Unroll
  def "should iterate over array"() {
    given:
    def input = [1, 3, 5] as Integer[]
    def expected = [2, 4, 6]

    expect:
    wrapper.legacyIterate(input) == expected
    wrapper.vavrIterate(input).toJavaList() == expected
  }

  def "should join elements with given character"() {
    given:
    def input = ["cheese cake", "apple pie"] as String[]
    def expected = "cheese cake, apple pie"

    expect:
    wrapper.legacyJoin(input) == expected
    wrapper.vavrJoin(input) == expected
    wrapper.stringJavaJoiner(input) == expected
  }

  def "should iterate with index"() {
    given:
    def input = ["cheese cake", "apple pie"] as String[]
    def result = ["0. cheese cake", "1. apple pie"]

    expect:
    wrapper.legacyIteratingWithIndex1(input) == result
    wrapper.legacyIteratingWithIndex2(input) == result
    wrapper.vavrIteratingWithIndex(input).toJavaList() == result
  }

  def "should flat map that sheep"() {
    given:
    def result = ["cheese cake", "apple pie"]

    expect:
    wrapper.legacyFlatMap1([Optional.of("cheese cake"), Optional.ofNullable(null), Optional.of("apple pie")]) == result
    wrapper.legacyFlatMap2([Optional.of("cheese cake"), Optional.ofNullable(null), Optional.of("apple pie")]) == result
    wrapper.legacyFlatMap3([Optional.of("cheese cake"), Optional.ofNullable(null), Optional.of("apple pie")]) == result
    wrapper.vavrFlatMap(io.vavr.collection.List.of(Option.of("cheese cake"), Option.of(null), Option.of("apple pie"))).toJavaList() == result
  }

  def "should fold"() {
    given:
    def input = ["cooking", "swimming", "running", "sleeping"]
    def vavrInput = io.vavr.collection.List.ofAll(input)
    def all = ["cooking", "swimming", "running", "sleeping"]
    when:
    def result1 = wrapper.legacyFold(input)
    def result2 = wrapper.vavrFold(vavrInput)
    then:
    result1.main == "cooking"
    result2.main == "cooking"
    result1.all == all
    result2.all == all
  }

  @Unroll
  def "should join lists the legacy way"() {
    expect:
    wrapper.legacyJoiningLists1(fruits, priceList) == result
    wrapper.legacyJoiningLists2(fruits, priceList) == result
    wrapper.legacyJoiningLists3(fruits, priceList) == result
    where:
    fruits                        | priceList          || result
    ["orange", "apple"]           | [10.0, 15.0]       || ["orange": 10.0, "apple": 15.0]
    ["orange", "apple", "banana"] | [10.0, 15.0]       || ["orange": 10.0, "apple": 15.0, "banana": 0]
    ["orange", "apple"]           | [10.0, 15.0, 20.0] || ["orange": 10.0, "apple": 15.0]
  }

  @Unroll
  def "should join lists the vavr way"() {
    expect:
    wrapper.vavrJoiningLists(
          io.vavr.collection.List.ofAll(fruits),
          io.vavr.collection.List.ofAll(priceList)
    ) == result
    where:
    fruits                        | priceList          || result
    ["orange", "apple"]           | [10.0, 15.0]       || io.vavr.collection.HashMap.of("orange", 10.0, "apple", 15.0)
    ["orange", "apple", "banana"] | [10.0, 15.0]       || io.vavr.collection.HashMap.of("orange", 10.0, "apple", 15.0, "banana", BigDecimal.ZERO)
    ["orange", "apple"]           | [10.0, 15.0, 20.0] || io.vavr.collection.HashMap.of("orange", 10.0, "apple", 15.0)
  }

}