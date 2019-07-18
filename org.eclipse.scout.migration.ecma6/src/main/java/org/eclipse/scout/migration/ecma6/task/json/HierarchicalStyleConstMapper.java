package org.eclipse.scout.migration.ecma6.task.json;

import java.nio.file.Path;
import java.util.Set;

import org.eclipse.scout.migration.ecma6.context.Context;
import org.eclipse.scout.rt.platform.util.ObjectUtility;

public class HierarchicalStyleConstMapper implements IConstPlaceholderMapper {
  @Override
  public String migrate(String key, String value, Path file, Context context, Set<String> importsToAdd) {
    if (!"hierarchicalStyle".equals(key)) {
      return null;
    }
    if (!ObjectUtility.isOneOf(value, "DEFAULT", "STRUCTURED")) {
      // cannot resolve to the enum
      return null;
    }
    importsToAdd.add("Table");
    return "Table.HierarchicalStyle." + value;
  }
}