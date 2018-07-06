/*******************************************************************************
 * Copyright (c) 2010-2017 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.shared.services.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.annotations.ConfigOperation;
import org.eclipse.scout.rt.platform.classid.ClassId;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;

/**
 * LookupCall for cases where no backend service exists.<br>
 * Data is directly provided by {@link #execCreateLookupRows()}
 *
 * @see LookupCall
 */
@ClassId("6a7d238a-11ab-478b-a3fb-7a99494b711d")
@SuppressWarnings("squid:S2057")
public class LocalLookupCall<T> extends LookupCall<T> {
  private static final long serialVersionUID = 0L;

  private boolean m_hierarchicalLookup;

  @Override
  @SuppressWarnings("squid:S1185") // method is required to satisfy LookupCall quality checks that require equals to be overridden
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  @Override
  @SuppressWarnings("squid:S1185") // method is expected because equals is implemented
  public int hashCode() {
    return super.hashCode();
  }

  @ConfigOperation
  @Order(30)
  protected List<? extends ILookupRow<T>> execCreateLookupRows() {
    return null;
  }

  protected Pattern createSearchPattern(String s) {
    if (s == null) {
      s = "";
    }
    s = s.replace(getWildcard(), "@wildcard@");
    s = s.toLowerCase();
    s = StringUtility.escapeRegexMetachars(s);
    s = s.replace("@wildcard@", ".*");
    if (!s.contains(".*")) {
      s = s + ".*";
    }
    return Pattern.compile(s, Pattern.DOTALL);
  }

  @Override
  protected final Class<? extends ILookupService<T>> getConfiguredService() {
    return null;
  }

  /**
   * Complete override using local data
   */
  @Override
  public List<? extends ILookupRow<T>> getDataByKey() {
    if (getKey() == null) {
      return CollectionUtility.emptyArrayList();
    }
    Object key = getKey();
    List<? extends ILookupRow<T>> rows = execCreateLookupRows();
    if (rows == null) {
      return CollectionUtility.emptyArrayList();
    }
    List<ILookupRow<T>> list = new ArrayList<>(rows.size());
    for (ILookupRow<T> row : rows) {
      if (key.equals(row.getKey())) {
        list.add(row);
      }
    }
    return list;
  }

  /**
   * Complete override using local data
   */
  @Override
  public List<? extends ILookupRow<T>> getDataByText() {
    List<ILookupRow<T>> list = new ArrayList<>();
    Pattern p = createSearchPattern(getText());
    List<? extends ILookupRow<T>> lookupRows = execCreateLookupRows();
    for (ILookupRow<T> row : lookupRows) {
      if (row.getText() != null && p.matcher(row.getText().toLowerCase()).matches()) {
        list.add(row);
      }
    }
    if (isHierarchicalLookup()) {
      Map<T, Set<ILookupRow<T>>> nodeMap = createNodeMap(lookupRows);
      List<ILookupRow<T>> children = new ArrayList<>();
      for (ILookupRow<T> res : list) {
        collectChildrenRec(nodeMap, res.getKey(), children);
      }
      list.addAll(children);
    }
    return list;
  }

  /**
   * builds up a tree representing the flat lookup rows result list.
   */
  protected Map<T, Set<ILookupRow<T>>> createNodeMap(List<? extends ILookupRow<T>> lookupRows) {
    Map<T, Set<ILookupRow<T>>> nodeMap = new HashMap<>();
    for (ILookupRow<T> row : lookupRows) {
      Set<ILookupRow<T>> node = nodeMap.computeIfAbsent(row.getParentKey(), k -> new HashSet<>());
      node.add(row);
    }
    return nodeMap;
  }

  /**
   * add all lookup rows found in {@code nodeMap} with {@code key} as their parent to {@code children} recursively.
   *
   * @param nodeMap
   * @param key
   * @param children
   */
  protected void collectChildrenRec(Map<T, Set<ILookupRow<T>>> nodeMap, T key, List<ILookupRow<T>> children) {
    if (nodeMap.get(key) == null) {
      return;
    }
    for (ILookupRow<T> var : nodeMap.get(key)) {
      children.add(var);
      collectChildrenRec(nodeMap, var.getKey(), children);
    }
  }

  /**
   * @return true if the parent of the specified lookupRow is found in given result list.
   */
  protected boolean isParentInResultList(List<ILookupRow<T>> result, ILookupRow<T> row) {
    return getLookupRowWithKey(result, row.getParentKey()) != null && !result.contains(row);
  }

  /**
   * finds the lookupRow with the specified key in a given result list.
   */
  protected ILookupRow<T> getLookupRowWithKey(List<ILookupRow<T>> result, T key) {
    for (ILookupRow<T> row : result) {
      if (row.getKey().equals(key)) {
        return row;
      }
    }
    return null;
  }

  /**
   * Complete override using local data
   */
  @Override
  public List<? extends ILookupRow<T>> getDataByAll() {
    List<ILookupRow<T>> list = new ArrayList<>();
    Pattern p = createSearchPattern(getAll());
    for (ILookupRow<T> row : execCreateLookupRows()) {
      if (row.getText() != null && p.matcher(row.getText().toLowerCase()).matches()) {
        list.add(row);
      }
    }
    return list;
  }

  /**
   * Complete override using local data
   */
  @Override
  public List<? extends ILookupRow<T>> getDataByRec() {
    List<ILookupRow<T>> list = new ArrayList<>();
    Object parentKey = getRec();
    if (parentKey == null) {
      for (ILookupRow<T> row : execCreateLookupRows()) {
        if (row.getParentKey() == null) {
          list.add(row);
        }
      }
    }
    else {
      for (ILookupRow<T> row : execCreateLookupRows()) {
        if (parentKey.equals(row.getParentKey())) {
          list.add(row);
        }
      }
    }
    return list;
  }

  public boolean isHierarchicalLookup() {
    return m_hierarchicalLookup;
  }

  public void setHierarchicalLookup(boolean hierarchicalLookup) {
    m_hierarchicalLookup = hierarchicalLookup;
  }
}