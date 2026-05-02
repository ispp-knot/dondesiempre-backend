package ispp.project.dondesiempre.modules.common.utils;

import org.springframework.stereotype.Component;

@Component
public class Utils {

  public String escapeString(String value) {
    if (value == null) return value;
    String res = value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    return res;
  }
}
