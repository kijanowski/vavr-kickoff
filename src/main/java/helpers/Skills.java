package helpers;

import java.util.ArrayList;
import java.util.List;

public class Skills {
  private final String main;
  private final java.util.List<String> all = new ArrayList<>();

  public Skills(String main) {
    this.main = main;
    all.add(main);
  }

  public Skills addSkill(String skill) {
    all.add(skill);
    return this;
  }

  public String getMain() {
    return main;
  }

  public List<String> getAll() {
    return all;
  }
}
