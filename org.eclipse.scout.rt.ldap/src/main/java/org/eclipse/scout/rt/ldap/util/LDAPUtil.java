package org.eclipse.scout.rt.ldap.util;

import java.security.KeyStore;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.directory.api.ldap.codec.api.DefaultConfigurableBinaryAttributeDetector;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.eclipse.scout.rt.ldap.LDAPConnectorProperties;
import org.eclipse.scout.rt.ldap.property.LDAPProperties;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.exception.PlatformException;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class LDAPUtil {

  private static final XLogger LOG = XLoggerFactory.getXLogger(LDAPUtil.class);

  public LDAPUtil() {
    LOG.entry();
    LOG.exit();
  }

  public static Dn prepareUserDN(String userId) {
    LOG.entry(userId);
    Dn result = null;

    String user_uid_attr = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPUserUidAttrProperty.class);
    String user_DN = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPUserBaseDNProperty.class);

    String tmpDN = user_uid_attr + "=" + userId + "," + user_DN;
    LOG.debug("tmpDN: {}", tmpDN);

    result = getDn(tmpDN);

    return LOG.exit(result);
  }

  public static Dn getDn(String stringDn) {
    LOG.entry(stringDn);
    Dn result = null;

    try {
      result = new Dn(stringDn);

    }
    catch (LdapInvalidDnException e) {
      String msg = "Cannot create Dn for: " + stringDn;
      LOG.error(msg, e);
      throw new PlatformException(msg, e).withContextInfo("dn", stringDn);
    }

    return LOG.exit(result);
  }

//	public static  Set<String> getGroups_parseGroupNames() {
//		LOG.entry();
//		Set<String> result = new HashSet<String>();
//
//		String group_mapping = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPGroupMappingProperty.class);
//		String user_group = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPUserGroupProperty.class);
//
//
//		Set<String> x_group_mapping_set = Stream.of(group_mapping.split("\\|")).collect(Collectors.toSet());
//		x_group_mapping_set.stream().forEach(x -> LOG.debug("mapping: {}", x) );
//
//		Pattern pattern = Pattern.compile("(.?)=(.?)");
//
//		for (String str : x_group_mapping_set) {
//			LOG.debug("processing: {}", str);
//			Matcher m = pattern.matcher(str);
//			String group = m.group(1);
//			String ldap = m.group(2);
//			LOG.debug("group: {} ldap: {}", group, ldap);
//		}
//
//		try {
//			result.add(user_group);
//		} catch (Exception e) {
//			// it is already on the list
//			LOG.debug("Adding user_group to Set failed", e);
//		}
//
//		return LOG.exit(result);
//	}

  public static BidiMap<String, String> getGroupMappings() {
    LOG.entry();
    BidiMap<String, String> result = new DualHashBidiMap<>();

    String group_mapping = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPGroupMappingProperty.class);
    Set<String> x_group_mapping_set = Stream.of(group_mapping.split("\\|")).collect(Collectors.toSet());
    x_group_mapping_set.stream().forEach(x -> LOG.debug("mapping: {}", x));

    Pattern pattern = Pattern.compile("([^=]*)=(.*)");

    for (String str : x_group_mapping_set) {
      LOG.debug("processing: {}", str);
      Matcher m = pattern.matcher(str);
      if (m.find()) {
        String group = m.group(1);
        String ldap = m.group(2);
        LOG.debug("group: {} ldap: {}", group, ldap);
        result.put(group, ldap);

      }
      else {
        String msg = "Incorrect LDAP group mapping: " + str;
        LOG.error(msg);
        throw new PlatformException(msg)
            .withContextInfo("group mapping", str)
            .withContextInfo("system property", LDAPProperties.AUTH_LDAP_GROUP_MAPPING_PROPERTY);
      }
    } // for

    return LOG.exit(result);
  }

  public static LdapConnectionConfig getLdapConnectionConfig(LDAPConnectorProperties p) {
    LOG.entry();
    LdapConnectionConfig result = new LdapConnectionConfig();

    result.setLdapHost(p.getConn_host());
    result.setLdapPort(p.getConn_port());
    result.setTimeout(p.getConn_timeout());
    result.setName(p.getConn_bind_dn());
    result.setCredentials(p.getConn_bind_pass());

    String[] enabledCipherSuites = getEnabledCiphers(p.getConn_ciphers());
    if (enabledCipherSuites != null) {
      result.setEnabledCipherSuites(enabledCipherSuites);
    }

    String[] enabledSecurityProtocols = getEnabledProtocols(p.getConn_protocol());
    if (enabledSecurityProtocols != null) {
      result.setEnabledProtocols(enabledSecurityProtocols);
      result.setSslProtocol(enabledSecurityProtocols[0]);
    }

    result.setTrustManagers(getTrustManager(p));
    result.setKeyManagers(getKeyManagers(p));

    result.setBinaryAttributeDetector(new DefaultConfigurableBinaryAttributeDetector());

    switch (p.getConn_type()) {
      case LDAPProperties.AUTH_LDAP_CONNECTION_TYPE_INSECURE: {
        // 0
        // insecure
        LOG.debug("Preparing insecure connection");
        break;
      }
      case LDAPProperties.AUTH_LDAP_CONNECTION_TYPE_STARTTLS: {
        // 1
        // StartTLS
        LOG.debug("Preparing StartTLS connection");
        result.setUseTls(true);
        break;
      }
      case LDAPProperties.AUTH_LDAP_CONNECTION_TYPE_LDAPS: {
        // 2
        // LDAPS
        LOG.debug("Preparing LDAPS connection");
        result.setUseSsl(true);
        break;
      }
      default: {
        throw new ProcessingException("Unknown value for configuration property: "
            + LDAPProperties.AUTH_LDAP_CONNECTION_TYPE_PROPERTY + " : " + p.getConn_type());
      }

    } // switch

    return LOG.exit(result);
  }

  protected static String[] getEnabledCiphers(String enabledCiphers) {
    LOG.entry(enabledCiphers);
    String[] result = null;

    Set<String> tmpSet = Stream.of(enabledCiphers.split(",")).collect(Collectors.toSet());
    tmpSet.stream().forEach(x -> LOG.debug("enabled cipher: {}", x));
    result = tmpSet.stream().toArray(x -> new String[x]);

    return LOG.exit(result);
  }

  protected static String[] getEnabledProtocols(String protocols) {
    LOG.entry(protocols);
    String[] result = null;

    Set<String> tmpSet = Stream.of(protocols.split(",")).collect(Collectors.toSet());
    tmpSet.stream().forEach(x -> LOG.debug("enabled protocol: {}", x));
    result = tmpSet.stream().toArray(x -> new String[x]);

    return LOG.exit(result);
  }

  protected static KeyManager[] getKeyManagers(LDAPConnectorProperties p) {
    LOG.entry();
    KeyManager[] result = null;

//		PKIHelper pki = PKIHelper.getInstance();
    KeyStore ks = PKIHelper.getKeystore(p.getKs_type(), p.getKs_provider(), p.getKs_file(), p.getKs_pass());
    result = PKIHelper.prepareKeyManagers(ks, p.getKs_pass(), p.getKs_cert_alias(), p.getKs_cert_pass());

    return LOG.exit(result);
  }

  protected static TrustManager getTrustManager(LDAPConnectorProperties p) {
    LOG.entry();
    TrustManager result = null;

    KeyStore ts = null;
    if (p.getTs_file() != null) {
      // open truststore
//			PKIHelper pki = PKIHelper.getInstance();
      ts = PKIHelper.getKeystore(p.getTs_type(), p.getTs_provider(), p.getTs_file(), p.getTs_pass());
    }

    result = new FullVerificationTrustManager(ts, p.getTs_ocsp(), p.getTs_trustlevel());
//		result = new NoVerificationTrustManager();

    return LOG.exit(result);
  }

  public static GenericObjectPoolConfig<LdapConnection> getLdapPoolConfig(LDAPConnectorProperties p) {
    LOG.entry();
    GenericObjectPoolConfig<LdapConnection> result = new GenericObjectPoolConfig<>();

    result.setLifo(GenericObjectPoolConfig.DEFAULT_LIFO);
    result.setMaxTotal(p.getConn_pool_maxtotal());
    result.setMinIdle(p.getConn_pool_minidle());
    result.setMaxIdle(GenericObjectPoolConfig.DEFAULT_MAX_IDLE);
    result.setMaxWaitMillis(GenericObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS);
    result.setMinEvictableIdleTimeMillis(GenericObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
    result.setNumTestsPerEvictionRun(GenericObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN);
    result.setSoftMinEvictableIdleTimeMillis(GenericObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
    result.setTestOnBorrow(GenericObjectPoolConfig.DEFAULT_TEST_ON_BORROW);
    result.setTestOnReturn(GenericObjectPoolConfig.DEFAULT_TEST_ON_RETURN);
    result.setTestWhileIdle(GenericObjectPoolConfig.DEFAULT_TEST_WHILE_IDLE);
    result.setTimeBetweenEvictionRunsMillis(GenericObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
    result.setBlockWhenExhausted(GenericObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED);

    return LOG.exit(result);
  }

}
