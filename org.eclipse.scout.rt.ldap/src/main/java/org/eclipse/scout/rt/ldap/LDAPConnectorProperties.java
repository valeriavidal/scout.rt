package org.eclipse.scout.rt.ldap;

import org.eclipse.scout.rt.ldap.property.LDAPProperties;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class LDAPConnectorProperties {

  private static final XLogger LOG = XLoggerFactory.getXLogger(LDAPConnectorProperties.class);

  private String conn_type = null;
  private String conn_protocol = null;
  private String conn_ciphers = null;
  private Integer conn_timeout = null;
  private String conn_host = null;
  private Integer conn_port = null;
  private String conn_auth = null;
  private Integer conn_pool_maxtotal = null;
  private Integer conn_pool_minidle = null;

  private String conn_bind_dn = null;
  private String conn_bind_pass = null;

  private String ks_type = null;
  private String ks_provider = null;
  private String ks_file = null;
  private String ks_pass = null;
  private String ks_cert_alias = null;
  private String ks_cert_pass = null;

  private String ts_type = null;
  private String ts_provider = null;
  private String ts_file = null;
  private String ts_pass = null;
  private String ts_trustlevel = null;
  private String ts_ocsp = null;

  private String superadmin = null;
  private String superadmin_login = null;
  private String superadmin_pass = null;

  private String login_group = null;
  private String user_uid_attr = null;
  private String user_baseDn = null;
  private String group_classes = null;
  private String group_fields = null;
  private String group_mapping = null;
  private String group_baseDn = null;
  private Integer cache_groups = null;

  private String base_dn = null;

  public LDAPConnectorProperties() {
    LOG.entry();

    conn_type = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPConnectionTypeProperty.class);
    conn_protocol = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPConnectionProtocolProperty.class);
    conn_ciphers = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPConnectionCiphersProperty.class);
    conn_timeout = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPConnectionTimeoutProperty.class);
    conn_host = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPHostProperty.class);
    conn_port = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPPortProperty.class);
    conn_auth = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPAuthProperty.class);
    conn_pool_maxtotal = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPConnectionPoolMaxtotalProperty.class);
    conn_pool_minidle = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPConnectionPoolMinidleProperty.class);

    conn_bind_dn = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPBindDNProperty.class);
    conn_bind_pass = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPBindPasswordProperty.class);

    ks_type = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSecurityKeystoreTypeProperty.class);
    ks_provider = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSecurityKeystoreProviderProperty.class);
    ks_file = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSecurityKeystoreFileProperty.class);
    ks_pass = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSecurityKeystorePasswordProperty.class);
    ks_cert_alias = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSecurityKeystoreCertAliasProperty.class);
    ks_cert_pass = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSecurityKeystoreCertPasswordProperty.class);
    ts_type = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSecurityTruststoreTypeProperty.class);
    ts_provider = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSecurityTruststoreProviderProperty.class);
    ts_file = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSecurityTruststoreFileProperty.class);
    ts_pass = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSecurityTruststorePasswordProperty.class);
    ts_trustlevel = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSecurityTruststoreTrustlevelProperty.class);
    ts_ocsp = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSecurityTruststoreOcspProperty.class);

    superadmin = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSuperadminProperty.class);
    superadmin_login = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSuperadminLoginProperty.class);
    superadmin_pass = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPSuperadminPasswordProperty.class);

    login_group = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPLoginGroupProperty.class);
    user_uid_attr = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPUserUidAttrProperty.class);
    user_baseDn = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPUserBaseDNProperty.class);
    group_classes = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPGroupClassesProperty.class);
    group_fields = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPGroupFieldsProperty.class);
    group_mapping = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPGroupMappingProperty.class);
    group_baseDn = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPGroupBaseDNProperty.class);
    cache_groups = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPCacheGroupsProperty.class);

    base_dn = CONFIG.getPropertyValue(LDAPProperties.AuthLDAPBaseDNProperty.class);

    LOG.debug("conn_type: {}", conn_type);
    LOG.debug("conn_protocol: {}", conn_protocol);
    LOG.debug("conn_ciphers: {}", conn_ciphers);
    LOG.debug("conn_timeout: {}", conn_timeout.intValue());
    LOG.debug("conn_host: {}", conn_host);
    LOG.debug("conn_port: {}", conn_port.intValue());
    LOG.debug("conn_auth: {}", conn_auth);
    LOG.debug("conn_pool_maxtotal: {}", conn_pool_maxtotal.intValue());
    LOG.debug("conn_pool_minidle: {}", conn_pool_minidle.intValue());

    LOG.debug("conn_bind_DN: {}", conn_bind_dn);
    LOG.debug("conn_bind_pass: {}", conn_bind_pass);

    LOG.debug("ks_type: {}", ks_type);
    LOG.debug("ks_provider: {}", ks_provider);
    LOG.debug("ks_file: {}", ks_file);
    LOG.debug("ks_pass: {}", ks_pass);
    LOG.debug("ks_cert_alias: {}", ks_cert_alias);
    LOG.debug("ks_cert_pass: {}", ks_cert_pass);
    LOG.debug("ts_type: {}", ts_type);
    LOG.debug("ts_provider: {}", ts_provider);
    LOG.debug("ts_file: {}", ts_file);
    LOG.debug("ts_pass: {}", ts_pass);
    LOG.debug("ts_trustlevel: {}", ts_trustlevel);
    LOG.debug("ts_ocsp: {}", ts_ocsp);

    LOG.debug("superadmin: {}", superadmin);
    LOG.debug("superadmin_login: {}", superadmin_login);
    LOG.debug("superadmin_pass: {}", superadmin_pass);

    LOG.debug("login_group: {}", login_group);
    LOG.debug("user_uid_attr: {}", user_uid_attr);
    LOG.debug("user_baseDN: {}", user_baseDn);
    LOG.debug("group_classes: {}", group_classes);
    LOG.debug("group_fields: {}", group_fields);
    LOG.debug("group_mapping: {}", group_mapping);
    LOG.debug("group_baseDN: {}", group_baseDn);
    LOG.debug("cache_groups: {}", cache_groups);

    LOG.debug("base_DN: {}", base_dn);

    LOG.exit();
  }

  public String getConn_type() {
    return conn_type;
  }

  public String getConn_protocol() {
    return conn_protocol;
  }

  public String getConn_ciphers() {
    return conn_ciphers;
  }

  public Integer getConn_timeout() {
    return conn_timeout;
  }

  public String getConn_host() {
    return conn_host;
  }

  public Integer getConn_port() {
    return conn_port;
  }

  public String getConn_auth() {
    return conn_auth;
  }

  public Integer getConn_pool_maxtotal() {
    return conn_pool_maxtotal;
  }

  public Integer getConn_pool_minidle() {
    return conn_pool_minidle;
  }

  public String getConn_bind_dn() {
    return conn_bind_dn;
  }

  public String getConn_bind_pass() {
    return conn_bind_pass;
  }

  public String getKs_type() {
    return ks_type;
  }

  public String getKs_provider() {
    return ks_provider;
  }

  public String getKs_file() {
    return ks_file;
  }

  public String getKs_pass() {
    return ks_pass;
  }

  public String getKs_cert_alias() {
    return ks_cert_alias;
  }

  public String getKs_cert_pass() {
    return ks_cert_pass;
  }

  public String getTs_type() {
    return ts_type;
  }

  public String getTs_provider() {
    return ts_provider;
  }

  public String getTs_file() {
    return ts_file;
  }

  public String getTs_pass() {
    return ts_pass;
  }

  public String getTs_trustlevel() {
    return ts_trustlevel;
  }

  public String getTs_ocsp() {
    return ts_ocsp;
  }

  public String getSuperadmin() {
    return superadmin;
  }

  public String getSuperadmin_login() {
    return superadmin_login;
  }

  public String getSuperadmin_pass() {
    return superadmin_pass;
  }

  public String getLogin_group() {
    return login_group;
  }

  public String getGroup_classes() {
    return group_classes;
  }

  public String getGroup_fields() {
    return group_fields;
  }

  public String getUser_uid_attr() {
    return user_uid_attr;
  }

  public String getUser_baseDn() {
    return user_baseDn;
  }

  public String getBase_dn() {
    return base_dn;
  }

  public String getGroup_mapping() {
    return group_mapping;
  }

  public Integer getCache_groups() {
    return cache_groups;
  }

  public String getGroup_baseDn() {
    return group_baseDn;
  }
}
