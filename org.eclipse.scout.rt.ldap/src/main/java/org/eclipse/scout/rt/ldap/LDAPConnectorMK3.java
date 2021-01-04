package org.eclipse.scout.rt.ldap;

import static org.apache.directory.ldap.client.api.search.FilterBuilder.and;
import static org.apache.directory.ldap.client.api.search.FilterBuilder.or;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.DefaultLdapConnectionFactory;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.ValidatingPoolableLdapConnectionFactory;
import org.apache.directory.ldap.client.api.search.FilterBuilder;
import org.apache.directory.ldap.client.template.EntryMapper;
import org.apache.directory.ldap.client.template.LdapConnectionTemplate;
import org.apache.directory.ldap.client.template.exception.PasswordException;
import org.eclipse.scout.rt.ldap.util.LDAPUtil;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.Assertions;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

@Order(4900)
public class LDAPConnectorMK3 implements ILDAPConnector {

  private static final XLogger LOG = XLoggerFactory.getXLogger(LDAPConnectorMK3.class);

  protected final LDAPConnectorProperties p = new LDAPConnectorProperties();
  protected LdapConnectionPool pool = null;

  public LDAPConnectorMK3() {
    LOG.entry();

    // java 10+ required
    //var factory = new DefaultLdapConnectionFactory(LDAPUtil.getLdapConnectionConfig(p));
    // java < 10
    DefaultLdapConnectionFactory factory = new DefaultLdapConnectionFactory(LDAPUtil.getLdapConnectionConfig(p));

    factory.setTimeOut(p.getConn_timeout());
    pool = new LdapConnectionPool(new ValidatingPoolableLdapConnectionFactory(factory), LDAPUtil.getLdapPoolConfig(p));

    LOG.exit();
  }

  @Override
  public boolean authenticate(String username, char[] password) {
    LOG.entry(username, "***");
    boolean result = false;

    Dn userDn = LDAPUtil.prepareUserDN(username);
    if (validateGroupMembership(userDn) && authenticateUsernamePassword(userDn, password)) {
      result = true;

    }
    else {
      // if LDAP authentication failed, attempt superadmin access
      result = superadminAuthentication(username, password);
    }

    return LOG.exit(result);
  }

  @Override
  public Set<Dn> getLdapGroupsForUser(Dn userDn) {
    LOG.entry(userDn);
    Set<Dn> result = null;

    LdapConnectionTemplate con = new LdapConnectionTemplate(pool);
    List<Dn> groups = con.search(p.getGroup_baseDn(), filterGroupsForUser(userDn), SearchScope.ONELEVEL, new String[]{""}, groupEntryMapper);
    result = groups.stream().collect(Collectors.toSet());

    return LOG.exit(result);
  }

  @Override
  public Map<Dn, Set<Dn>> getAllLdapGroups() {
    LOG.entry();
    Map<Dn, Set<Dn>> result = null;
//		Map<Dn, Set<Dn>> result = new HashMap<Dn, Set<Dn>>();

//		Set<String> attrSet = Stream.of(p.getGroup_fields().split(",")).collect(Collectors.toSet());
//		String[] attrs = attrSet.toArray(new String[attrSet.size()]);

    String[] attrs = Stream.of(p.getGroup_fields().split(",")).toArray(String[]::new);

    LdapConnectionTemplate con = new LdapConnectionTemplate(pool);
//		List<Dn> groups = con.search(p.getGroup_baseDn(), filterAllGroups(), SearchScope.ONELEVEL, new String[]{""}, groupEntryMapper);
    List<Map<Dn, Set<Dn>>> all_ldap_groups = con.search(p.getGroup_baseDn(), filterAllGroups(), SearchScope.ONELEVEL, attrs, allGroupEntryMapper);
//		result = groups.stream().collect(Collectors.toSet());

    result = all_ldap_groups.stream()
        .flatMap(x -> x.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//		result.entrySet().stream().forEach(x -> LOG.debug("key: {} value: {}", x.getKey(), x.getValue()));

    return LOG.exit(result);
  }

  protected boolean authenticateUsernamePassword(Dn userDn, char[] password) {
    LOG.entry(userDn, "***");
    boolean result = false;

    Assertions.assertNotNull(userDn);
    Assertions.assertNotNullOrEmpty(new String(password));

    LdapConnectionTemplate con = new LdapConnectionTemplate(pool);
    try {
      con.authenticate(userDn, password);
      result = true;

    }
    catch (PasswordException e) {
      LOG.debug("LDAP authentication unsuccessful for: " + userDn.toString(), e);
    }

    return LOG.exit(result);
  }

  protected boolean validateGroupMembership(Dn userDn) {
    LOG.entry(userDn);
    boolean result = false;

    // if login group is not specified = anyone is allowed to authenticate
    if (StringUtility.isNullOrEmpty(p.getLogin_group())) {
      result = true;

    }
    else {

      LdapConnectionTemplate con = new LdapConnectionTemplate(pool);
      Integer ok = con.searchFirst(p.getLogin_group(), filterGroupsForUser(userDn), SearchScope.OBJECT, new String[]{""}, loginGroupEntryMapper);

      // on success ok is "1", on failure it should be null
      if (ok != null) {
        result = true;
      }
    }

    return LOG.exit(result);
  }

  protected boolean superadminAuthentication(String username, char[] password) {
    LOG.entry();
    boolean result = false;

    if (p.isSuperadmin()
        && p.getSuperadmin_login().equals(username)
        && p.getSuperadmin_pass().equals(new String(password))) {

      LOG.warn("superadmin access granted");
      result = true;
    }

    return LOG.exit(result);
  }

  private final EntryMapper<Dn> groupEntryMapper = new EntryMapper<Dn>() {

    @Override
    public Dn map(Entry entry) throws LdapException {
      LOG.entry(entry);
      return LOG.exit(entry.getDn());
    }

  };

  private final EntryMapper<Integer> loginGroupEntryMapper = new EntryMapper<Integer>() {

    @Override
    public Integer map(Entry entry) throws LdapException {
      LOG.entry(entry);
      Integer result = 1;
      return LOG.exit(result);
    }

  };

  private final EntryMapper<Map<Dn, Set<Dn>>> allGroupEntryMapper = new EntryMapper<Map<Dn, Set<Dn>>>() {

    @Override
    public Map<Dn, Set<Dn>> map(Entry entry) throws LdapException {
      LOG.entry(entry);
//			Map<Dn, Set<Dn>> result = null;
      Map<Dn, Set<Dn>> result = new HashMap<>();

      // TODO
      Set<String> group_fields = Stream.of(p.getGroup_fields().split(",")).collect(Collectors.toSet());
//			group_fields.stream().forEach(x -> LOG.debug("group field: {}", x));

      // java 10+ required
//			var key = entry.getDn();
      // java < 10
      Dn key = entry.getDn();

      Set<Dn> m = entry.getAttributes().stream()
//					.peek(a -> LOG.debug("peek 1: {} {}", a.getUpId(), a))
          .filter(x -> group_fields.contains(x.getUpId()))
//					.peek(a -> LOG.debug("peek 2: {}", a))
//					.flatMap(y -> {
//						Set<String> tmpSet = new HashSet<String>();
//						y.iterator().forEachRemaining(z -> tmpSet.add(z.getString()));
//						return tmpSet.stream();
//					})
          .flatMap(y -> StreamSupport.stream(y.spliterator(), false))
          .map(Value::getString)
//					.flatMap(y -> {
//							Set<String> tmpSet = new HashSet<String>();
//							y.forEach(z -> tmpSet.add(z.getString()));
//							return tmpSet.stream();
//					} )
//					.peek(a -> LOG.debug("peek 3: {}", a))
          .map(z -> LDAPUtil.getDn(z))
          .collect(Collectors.toSet());

      m.stream().forEach(b -> LOG.debug("b: {}", b));
      result.put(key, m);

      return LOG.exit(result);
    }

  };

  private String filterGroupsForUser(Dn userDn) {
    LOG.entry(userDn);
    String result = null;

//		Set<FilterBuilder> g_c = Stream.of(p.getGroup_classes().split(",")).map(x -> FilterBuilder.equal("objectClass", x)).collect(Collectors.toSet());
//		Set<FilterBuilder> g_f = Stream.of(p.getGroup_fields().split(",")).map(x -> FilterBuilder.equal(x, userDn.getName())).collect(Collectors.toSet());

    FilterBuilder[] filterGroupClasses = Stream.of(p.getGroup_classes().split(","))
        .map(x -> FilterBuilder.equal("objectClass", x))
        .toArray(FilterBuilder[]::new);
    FilterBuilder[] filterGroupFields = Stream.of(p.getGroup_fields().split(","))
        .map(x -> FilterBuilder.equal(x, userDn.getName()))
        .toArray(FilterBuilder[]::new);

    FilterBuilder f = and(
//				or(g_c.toArray(new FilterBuilder[g_c.size()]))
//				,or(g_f.toArray(new FilterBuilder[g_f.size()]))
//				or(g_c.toArray(FilterBuilder[]::new))
//				,or(g_f.toArray(FilterBuilder[]::new))
        or(filterGroupClasses), or(filterGroupFields));
    result = f.toString();

    return LOG.exit(result);
  }

  private String filterAllGroups() {
    LOG.entry();
    String result = null;

    FilterBuilder[] filterGroupClasses = Stream.of(p.getGroup_classes().split(","))
        .map(x -> FilterBuilder.equal("objectClass", x))
        .toArray(FilterBuilder[]::new);
//		Set<FilterBuilder> g_c = Stream.of(p.getGroup_classes().split(",")).map(x -> FilterBuilder.equal("objectClass", x)).collect(Collectors.toSet());
//		Set<FilterBuilder> g_f = Stream.of(p.getGroup_fields().split(",")).map(x -> FilterBuilder.equal(x, userDn.getName())).collect(Collectors.toSet());

    FilterBuilder f = and(
        or(filterGroupClasses)
//				or(g_c.toArray(new FilterBuilder[g_c.size()]))
//				,or(g_f.toArray(new FilterBuilder[g_f.size()]))
    );
    result = f.toString();

    return LOG.exit(result);
  }

}
