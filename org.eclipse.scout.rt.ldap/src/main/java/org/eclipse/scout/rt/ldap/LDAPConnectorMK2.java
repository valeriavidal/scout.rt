package org.eclipse.scout.rt.ldap;

import static org.apache.directory.ldap.client.api.search.FilterBuilder.and;
import static org.apache.directory.ldap.client.api.search.FilterBuilder.or;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
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
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.cache.CacheRegistryService;
import org.eclipse.scout.rt.platform.cache.ICache;
import org.eclipse.scout.rt.platform.cache.ICacheBuilder;
import org.eclipse.scout.rt.platform.cache.ICacheValueResolver;
import org.eclipse.scout.rt.platform.util.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

//@ApplicationScoped
@Order(5000)
public class LDAPConnectorMK2 implements ILDAPConnector {

  private static final XLogger LOG = XLoggerFactory.getXLogger(LDAPConnectorMK2.class);

  public static final String GROUP_CACHE_CACHE_ID = LDAPConnectorMK2.class.getName() + ".GROUP_CACHE";

  protected final LDAPConnectorProperties p = new LDAPConnectorProperties();
  protected LdapConnectionPool pool = null;
  protected boolean groupCacheEnabled = false;

  public LDAPConnectorMK2() {
    LOG.entry();

    // LdapConnectionConfig config = LDAPUtil.getLdapConnectionConfig(p);
    // DefaultLdapConnectionFactory factory = new DefaultLdapConnectionFactory(config);
    DefaultLdapConnectionFactory factory = new DefaultLdapConnectionFactory(LDAPUtil.getLdapConnectionConfig(p));
    factory.setTimeOut(p.getConn_timeout());

    // GenericObjectPoolConfig<LdapConnection> poolConfig = LDAPUtil.getLdapPoolConfig(p);
    // pool = new LdapConnectionPool(new ValidatingPoolableLdapConnectionFactory(factory), poolConfig );
    pool = new LdapConnectionPool(new ValidatingPoolableLdapConnectionFactory(factory), LDAPUtil.getLdapPoolConfig(p));

    if (p.getCache_groups() > 0) {
      groupCacheEnabled = true;
    }

    LOG.exit();
  }

  @Override
  public boolean authenticate(String username, char[] password) {
    LOG.entry(username, "***");
    boolean result = false;

    Dn userDn = LDAPUtil.prepareUserDN(username);

    if (validateGroupMembership(userDn) && authenticateWithUsernamePassword(userDn, password)) {
      result = true;
    }

    // if LDAP authentication failed, attempt superadmin access
    if (!result) {
      result = superadminAuthentication(username, password);
    }

    return LOG.exit(result);
  }

  protected boolean authenticateWithUsernamePassword(Dn userDn, char[] password) {
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

    Set<String> users = getLdapGroupMembers(p.getLogin_group());

    if (users.contains(userDn.getName().toLowerCase())) {
      result = true;
    }

    return LOG.exit(result);
  }

  protected boolean superadminAuthentication(String username, char[] password) {
    LOG.entry();
    boolean result = false;

    if (p.getSuperadmin().equals("1") && p.getSuperadmin_login().equals(username) && p.getSuperadmin_pass().equals(new String(password))) {

      LOG.warn("superadmin access granted");
      result = true;
    }

    return LOG.exit(result);
  }

  public Set<String> getLdapGroupMembers(String ldapGroup) {
    LOG.entry(ldapGroup);
    Set<String> result = null;

    Assertions.assertNotNull(ldapGroup);

    if (groupCacheEnabled) {
      LOG.debug("attempting to get values from cache");
      result = getCache().get(ldapGroup);

    }
    else {
      result = getLdapGroupMembersNoCache(ldapGroup);
    }
    result.stream().forEach(x -> LOG.debug("member: {}", x));

    return LOG.exit(result);
  }

  public Set<String> getLdapGroupMembersNoCache(String ldapGroup) {
    LOG.entry(ldapGroup);
    Set<String> result = null;

    LdapConnectionTemplate con = new LdapConnectionTemplate(pool);
    result = con.lookup(LDAPUtil.getDn(ldapGroup), groupMembersEntryMapper);

    return LOG.exit(result);
  }

  /**
   * no cache here
   * 
   * @param userDn
   * @return
   */
  @Override
  public Set<Dn> getLdapGroupsForUser(Dn userDn) {
    LOG.entry(userDn);
    Set<Dn> result = null;

    Set<FilterBuilder> g_c = Stream.of(p.getGroup_classes().split(",")).map(x -> FilterBuilder.equal("objectClass", x)).collect(Collectors.toSet());
    Set<FilterBuilder> g_f = Stream.of(p.getGroup_fields().split(",")).map(x -> FilterBuilder.equal(x, userDn.getName())).collect(Collectors.toSet());

    FilterBuilder f = and(or(g_c.toArray(new FilterBuilder[g_c.size()])), or(g_f.toArray(new FilterBuilder[g_f.size()])));
    LOG.debug("filter: {}", f.toString());

    LdapConnectionTemplate con = new LdapConnectionTemplate(pool);
    List<Dn> groups = con.search(p.getGroup_baseDn(), f.toString(), SearchScope.ONELEVEL, new String[]{""}, groupEntryMapper);

    // groups.stream().forEach(x -> LOG.debug("group: {}", x));
    result = groups.stream().collect(Collectors.toSet());

    return LOG.exit(result);
  }

  // private final EntryMapper<Entry> entryEntryMapper = new EntryMapper<Entry>() {
  //
  // @Override
  // public Entry map(Entry entry) throws LdapException {
  // return entry;
  // }
  //
  // };

  private final EntryMapper<Set<String>> groupMembersEntryMapper = new EntryMapper<Set<String>>() {

    @Override
    public Set<String> map(Entry entry) throws LdapException {
      LOG.entry(entry);
      Set<String> result = new HashSet<>();

      // checking groups for attributes: scout.auth.ldap.group_fields=member,uniqueMember,memberUid
      // String group_fields = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPGroupFieldsProperty.class);
      String group_fields = p.getGroup_fields();
      Set<String> group_fields_set = Stream.of(group_fields.split(",")).map(x -> x.toLowerCase()).collect(Collectors.toSet());
      group_fields_set.stream().forEach(x -> LOG.debug("group_field: {}", x));

      Collection<Attribute> c_attr = entry.getAttributes();

      c_attr.stream().filter(y -> group_fields_set.contains(y.getId().toLowerCase())).forEach(d -> d.forEach(e -> result.add(e.getString())));

      result.stream().forEach(x -> LOG.debug("user: {}", x));

      return LOG.exit(result);

    }

  };

  private final EntryMapper<Dn> groupEntryMapper = new EntryMapper<Dn>() {

    @Override
    public Dn map(Entry entry) throws LdapException {
      LOG.entry(entry);
      Dn result = null;

      result = entry.getDn();

      return LOG.exit(result);
    }

  };

  private ICache<String, Set<String>> getCache() {
    LOG.entry();
    ICache<String, Set<String>> result = null;

    CacheRegistryService crs = BEANS.get(CacheRegistryService.class);
    result = crs.opt(GROUP_CACHE_CACHE_ID);

    if (result == null) {
      result = createCache();
    }

    return LOG.exit(result);
  }

  private ICache<String, Set<String>> createCache() {
    LOG.entry();
    ICache<String, Set<String>> result = null;

    @SuppressWarnings("unchecked")
    ICacheBuilder<String, Set<String>> cacheBuilder = BEANS.get(ICacheBuilder.class);
    result = cacheBuilder.withCacheId(GROUP_CACHE_CACHE_ID).withTimeToLive(Long.valueOf(p.getCache_groups()), TimeUnit.MINUTES, false)
        .withValueResolver(new ICacheValueResolver<String, Set<String>>() {

          @Override
          public Set<String> resolve(String key) {
            LOG.entry(key);
            Set<String> result = getLdapGroupMembersNoCache(key);
            return LOG.exit(result);
          }

        }).withThreadSafe(true).build();

    return LOG.exit(result);
  }

  @Override
  public Map<Dn, Set<Dn>> getAllLdapGroups() {
    // TODO Auto-generated method stub
    return null;
  }

}
