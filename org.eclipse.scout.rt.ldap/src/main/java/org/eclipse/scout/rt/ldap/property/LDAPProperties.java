package org.eclipse.scout.rt.ldap.property;

import org.eclipse.scout.rt.platform.config.AbstractBooleanConfigProperty;
import org.eclipse.scout.rt.platform.config.AbstractIntegerConfigProperty;
import org.eclipse.scout.rt.platform.config.AbstractStringConfigProperty;

public final class LDAPProperties {

  public static final String AUTH_LDAP_PROPERTIES_NS = "scout.auth.ldap";

  // not used
  public static final String AUTH_LDAP_PROPERTY = AUTH_LDAP_PROPERTIES_NS;
  public static final String AUTH_LDAP_PROPERTY_DESC = "Enable LDAP authentication";
  public static final String AUTH_LDAP_PROPERTY_DEFAULT = "0";

  public static final String AUTH_LDAP_HOST_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".host";
  public static final String AUTH_LDAP_HOST_PROPERTY_DESC = "LDAP hostname or IP";

  public static final String AUTH_LDAP_PORT_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".port";
  public static final String AUTH_LDAP_PORT_PROPERTY_DESC = "LDAP server port number";
  public static final Integer AUTH_LDAP_PORT_PROPERTY_DEFAULT = Integer.valueOf(389);

  public static final String AUTH_LDAP_SUPERADMIN_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".superadmin";
  public static final String AUTH_LDAP_SUPERADMIN_PROPERTY_DESC = "Enable local superadmin account";
  public static final boolean AUTH_LDAP_SUPERADMIN_PROPERTY_DEFAULT = false;

  public static final String AUTH_LDAP_SUPERADMIN_LOGIN_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".superadmin.login";
  public static final String AUTH_LDAP_SUPERADMIN_LOGIN_PROPERTY_DESC = "Superadmin account name";

  public static final String AUTH_LDAP_SUPERADMIN_PASS_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".superadmin.password";
  public static final String AUTH_LDAP_SUPERADMIN_PASS_PROPERTY_DESC = "Superadmin account password";

  public static final String AUTH_LDAP_CONNECTION_TIMEOUT_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".connection.timeout";
  public static final String AUTH_LDAP_CONNECTION_TIMEOUT_PROPERTY_DESC = "Connection timeout";
  public static final Integer AUTH_LDAP_CONNECTION_TIMEOUT_PROPERTY_DEFAULT = Integer.valueOf(1000);

  public static final String AUTH_LDAP_CONNECTION_TYPE_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".connection.type";
  public static final String AUTH_LDAP_CONNECTION_TYPE_PROPERTY_DESC = "Connection type to LDAP server: 0 - insecure, 1 - StartTLS, 2 - LDAPS";
  public static final String AUTH_LDAP_CONNECTION_TYPE_PROPERTY_DEFAULT = "1";
  public static final String AUTH_LDAP_CONNECTION_TYPE_INSECURE = "0";
  public static final String AUTH_LDAP_CONNECTION_TYPE_STARTTLS = "1";
  public static final String AUTH_LDAP_CONNECTION_TYPE_LDAPS = "2";

  public static final String AUTH_LDAP_CONNECTION_PROTOCOL_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".connection.protocol";
  public static final String AUTH_LDAP_CONNECTION_PROTOCOL_PROPERTY_DESC = "Connection security protocol";
  public static final String AUTH_LDAP_CONNECTION_PROTOCOL_PROPERTY_DEFAULT = "TLSv1.3,TLSv1.2";

  public static final String AUTH_LDAP_CONNECTION_CIPHERS_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".connection.ciphers";
  public static final String AUTH_LDAP_CONNECTION_CIPHERS_PROPERTY_DESC = "Connection allowed ciphers";
  public static final String AUTH_LDAP_CONNECTION_CIPHERS_PROPERTY_DEFAULT = "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,TLS_DHE_RSA_WITH_AES_256_GCM_SHA384";

  public static final String AUTH_LDAP_CONNECTION_POOL_MAXTOTAL_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".connection.pool.maxtotal";
  public static final String AUTH_LDAP_CONNECTION_POOL_MAXTOTAL_PROPERTY_DESC = "LDAP connection pool maxtotal";
  public static final Integer AUTH_LDAP_CONNECTION_POOL_MAXTOTAL_PROPERTY_DEFAULT = Integer.valueOf(10);

  public static final String AUTH_LDAP_CONNECTION_POOL_MINIDLE_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".connection.pool.minidle";
  public static final String AUTH_LDAP_CONNECTION_POOL_MINIDLE_PROPERTY_DESC = "LDAP connection pool minidle";
  public static final Integer AUTH_LDAP_CONNECTION_POOL_MINIDLE_PROPERTY_DEFAULT = Integer.valueOf(2);

  public static final String AUTH_LDAP_AUTH_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".auth";
  public static final String AUTH_LDAP_AUTH_PROPERTY_DESC = "LDAP server authentication method: simple | SASL_External";
  public static final String AUTH_LDAP_AUTH_PROPERTY_DEFAULT = "simple";

  public static final String AUTH_LDAP_BIND_DN_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".bind_dn";
  public static final String AUTH_LDAP_BIND_DN_PROPERTY_DESC = "Bind dn to authenticate against";

  public static final String AUTH_LDAP_BIND_PASS_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".bind_password";
  public static final String AUTH_LDAP_BIND_PASS_PROPERTY_DESC = "Password for bind DN object";

  public static final String AUTH_LDAP_SECURITY_KEYSTORE_TYPE_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".security.keystore.type";
  public static final String AUTH_LDAP_SECURITY_KEYSTORE_TYPE_PROPERTY_DESC = "Keystore type";
  public static final String AUTH_LDAP_SECURITY_KEYSTORE_TYPE_PROPERTY_DEFAULT = "PKCS12";

  public static final String AUTH_LDAP_SECURITY_KEYSTORE_PROVIDER_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".security.keystore.provider";
  public static final String AUTH_LDAP_SECURITY_KEYSTORE_PROVIDER_PROPERTY_DESC = "Keystore provider";

  public static final String AUTH_LDAP_SECURITY_KEYSTORE_FILE_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".security.keystore.file";
  public static final String AUTH_LDAP_SECURITY_KEYSTORE_FILE_PROPERTY_DESC = "Keystore file location";

  public static final String AUTH_LDAP_SECURITY_KEYSTORE_PASS_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".security.keystore.password";
  public static final String AUTH_LDAP_SECURITY_KEYSTORE_PASS_PROPERTY_DESC = "Keystore password";

  public static final String AUTH_LDAP_SECURITY_KEYSTORE_CERT_ALIAS_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".security.keystore.cert.alias";
  public static final String AUTH_LDAP_SECURITY_KEYSTORE_CERT_ALIAS_PROPERTY_DESC = "Alias of the certificate used for identity";

  public static final String AUTH_LDAP_SECURITY_KEYSTORE_CERT_PASS_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".security.keystore.cert.password";
  public static final String AUTH_LDAP_SECURITY_KEYSTORE_CERT_PASS_PROPERTY_DESC = "Password for the private key";

  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_TYPE_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".security.truststore.type";
  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_TYPE_PROPERTY_DESC = "Truststore type";
  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_TYPE_PROPERTY_DEFAULT = "PKCS12";

  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_PROVIDER_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".security.truststore.provider";
  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_PROVIDER_PROPERTY_DESC = "Truststore provider";

  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_TRUSTLEVEL_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".security.truststore.trustlevel";
  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_TRUSTLEVEL_PROPERTY_DESC = "Truststore trustlevel";
  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_TRUSTLEVEL_PROPERTY_DEFAULT = "0";

  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_OCSP_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".security.truststore.ocsp";
  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_OCSP_PROPERTY_DESC = "Truststore ocsp";
  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_OCSP_PROPERTY_DEFAULT = "0";

  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_FILE_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".security.truststore.file";
  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_FILE_PROPERTY_DESC = "Truststore file location";

  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_PASS_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".security.truststore.password";
  public static final String AUTH_LDAP_SECURITY_TRUSTSTORE_PASS_PROPERTY_DESC = "Truststore password";

  // You need to be authorized to be authenticated
  public static final String AUTH_LDAP_LOGIN_GROUP_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".login_group";
  public static final String AUTH_LDAP_LOGIN_GROUP_PROPERTY_DESC = "LDAP group of authorized to login accounts";

  public static final String AUTH_LDAP_GROUP_CLASSES_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".group.class";
  public static final String AUTH_LDAP_GROUP_CLASSES_PROPERTY_DESC = "LDAP object classes of a group";
  public static final String AUTH_LDAP_GROUP_CLASSES_PROPERTY_DEFAULT = "groupOfUniqueNames,groupOfNames";

  public static final String AUTH_LDAP_GROUP_FIELDS_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".group.member_field";
  public static final String AUTH_LDAP_GROUP_FIELDS_PROPERTY_DESC = "LDAP attribute names of a group member";
  public static final String AUTH_LDAP_GROUP_FIELDS_PROPERTY_DEFAULT = "member,uniqueMember,memberUid";

  public static final String AUTH_LDAP_USER_UID_ATTR_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".user.uid_attr";
  public static final String AUTH_LDAP_USER_UID_ATTR_PROPERTY_DESC = "LDAP atribute name to verify login against";
  public static final String AUTH_LDAP_USER_UID_ATTR_PROPERTY_DEFAULT = "uid";

  public static final String AUTH_LDAP_USER_BASE_DN_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".user.base_dn";
  public static final String AUTH_LDAP_USER_BASE_DN_PROPERTY_DESC = "Location of user objects in LDAP";

  public static final String AUTH_LDAP_GROUP_BASE_DN_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".group.base_dn";
  public static final String AUTH_LDAP_GROUP_BASE_DN_PROPERTY_DESC = "Location of group objects in LDAP";

  public static final String AUTH_LDAP_BASE_DN_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".base_dn";
  public static final String AUTH_LDAP_BASE_DN_PROPERTY_DESC = "LDAP user base dn";

  public static final String AUTH_LDAP_GROUP_MAPPING_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".group.mapping";
  public static final String AUTH_LDAP_GROUP_MAPPING_PROPERTY_DESC = "Mapping of security groups between LDAP and Scout";

  public static final String AUTH_LDAP_BRUTEFORCE_PREVENTION_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".bruteforce.prevention";
  public static final String AUTH_LDAP_BRUTEFORCE_PREVENTION_PROPERTY_DESC = "Lock client IP after specified number failed login attempts";
  public static final String AUTH_LDAP_BRUTEFORCE_PREVENTION_PROPERTY_DEFAULT = "0";

  public static final String AUTH_LDAP_BRUTEFORCE_LOCKOUT_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".bruteforce.lockout";
  public static final String AUTH_LDAP_BRUTEFORCE_LOCKOUT_PROPERTY_DESC = "Lockout time in minutes";
  public static final Integer AUTH_LDAP_BRUTEFORCE_LOCKOUT_PROPERTY_DEFAULT = Integer.valueOf(1440);

  public static final String AUTH_LDAP_CACHE_GROUPS_PROPERTY = AUTH_LDAP_PROPERTIES_NS + ".cache.groups";
  public static final String AUTH_LDAP_CACHE_GROUPS_PROPERTY_DESC = "cache LDAP groups (in minutes)";
  public static final Integer AUTH_LDAP_CACHE_GROUPS_PROPERTY_DEFAULT = Integer.valueOf(0);

//	public static final String AUTH_LDAP_PROPERTY = AUTH_LDAP_PROPERTIES_NS + "ldap";

  private LDAPProperties() {
  }

//	public static class AuthLDAPProperty extends AbstractStringConfigProperty {
//
//		@Override
//		public String getKey() {
//			return AUTH_LDAP_PROPERTY;
//		}
//
//		@Override
//		public String description() {
//			return AUTH_LDAP_PROPERTY_DESC;
//		}
//
//		@Override
//		public String getDefaultValue() {
//			return AUTH_LDAP_PROPERTY_DEFAULT;
//		}
//	}

  public static class AuthLDAPHostProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_HOST_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_HOST_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPPortProperty extends AbstractIntegerConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_PORT_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_PORT_PROPERTY_DESC;
    }

    @Override
    public Integer getDefaultValue() {
      return AUTH_LDAP_PORT_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPSuperadminProperty extends AbstractBooleanConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SUPERADMIN_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SUPERADMIN_PROPERTY_DESC;
    }

    @Override
    public Boolean getDefaultValue() {
      return AUTH_LDAP_SUPERADMIN_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPSuperadminLoginProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SUPERADMIN_LOGIN_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SUPERADMIN_LOGIN_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPSuperadminPasswordProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SUPERADMIN_PASS_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SUPERADMIN_PASS_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPConnectionTimeoutProperty extends AbstractIntegerConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_CONNECTION_TIMEOUT_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_CONNECTION_TIMEOUT_PROPERTY_DESC;
    }

    @Override
    public Integer getDefaultValue() {
      return AUTH_LDAP_CONNECTION_TIMEOUT_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPConnectionTypeProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_CONNECTION_TYPE_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_CONNECTION_TYPE_PROPERTY_DESC;
    }

    @Override
    public String getDefaultValue() {
      return AUTH_LDAP_CONNECTION_TYPE_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPConnectionProtocolProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_CONNECTION_PROTOCOL_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_CONNECTION_PROTOCOL_PROPERTY_DESC;
    }

    @Override
    public String getDefaultValue() {
      return AUTH_LDAP_CONNECTION_PROTOCOL_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPConnectionCiphersProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_CONNECTION_CIPHERS_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_CONNECTION_CIPHERS_PROPERTY_DESC;
    }

    @Override
    public String getDefaultValue() {
      return AUTH_LDAP_CONNECTION_CIPHERS_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPConnectionPoolMaxtotalProperty extends AbstractIntegerConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_CONNECTION_POOL_MAXTOTAL_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_CONNECTION_POOL_MAXTOTAL_PROPERTY_DESC;
    }

    @Override
    public Integer getDefaultValue() {
      return AUTH_LDAP_CONNECTION_POOL_MAXTOTAL_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPConnectionPoolMinidleProperty extends AbstractIntegerConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_CONNECTION_POOL_MINIDLE_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_CONNECTION_POOL_MINIDLE_PROPERTY_DESC;
    }

    @Override
    public Integer getDefaultValue() {
      return AUTH_LDAP_CONNECTION_POOL_MINIDLE_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPAuthProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_AUTH_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_AUTH_PROPERTY_DESC;
    }

    @Override
    public String getDefaultValue() {
      return AUTH_LDAP_AUTH_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPBindDNProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_BIND_DN_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_BIND_DN_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPBindPasswordProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_BIND_PASS_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_BIND_PASS_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPSecurityKeystoreTypeProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SECURITY_KEYSTORE_TYPE_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SECURITY_KEYSTORE_TYPE_PROPERTY_DESC;
    }

    @Override
    public String getDefaultValue() {
      return AUTH_LDAP_SECURITY_KEYSTORE_TYPE_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPSecurityKeystoreProviderProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SECURITY_KEYSTORE_PROVIDER_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SECURITY_KEYSTORE_PROVIDER_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPSecurityKeystoreFileProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SECURITY_KEYSTORE_FILE_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SECURITY_KEYSTORE_FILE_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPSecurityKeystorePasswordProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SECURITY_KEYSTORE_PASS_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SECURITY_KEYSTORE_PASS_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPSecurityKeystoreCertAliasProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SECURITY_KEYSTORE_CERT_ALIAS_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SECURITY_KEYSTORE_CERT_ALIAS_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPSecurityKeystoreCertPasswordProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SECURITY_KEYSTORE_CERT_PASS_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SECURITY_KEYSTORE_CERT_PASS_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPSecurityTruststoreTypeProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_TYPE_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_TYPE_PROPERTY_DESC;
    }

    @Override
    public String getDefaultValue() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_TYPE_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPSecurityTruststoreProviderProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_PROVIDER_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_PROVIDER_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPSecurityTruststoreTrustlevelProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_TRUSTLEVEL_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_TRUSTLEVEL_PROPERTY_DESC;
    }

    @Override
    public String getDefaultValue() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_TRUSTLEVEL_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPSecurityTruststoreOcspProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_OCSP_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_OCSP_PROPERTY_DESC;
    }

    @Override
    public String getDefaultValue() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_OCSP_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPSecurityTruststoreFileProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_FILE_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_FILE_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPSecurityTruststorePasswordProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_PASS_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_SECURITY_TRUSTSTORE_PASS_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPLoginGroupProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_LOGIN_GROUP_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_LOGIN_GROUP_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPGroupClassesProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_GROUP_CLASSES_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_GROUP_CLASSES_PROPERTY_DESC;
    }

    @Override
    public String getDefaultValue() {
      return AUTH_LDAP_GROUP_CLASSES_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPGroupFieldsProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_GROUP_FIELDS_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_GROUP_FIELDS_PROPERTY_DESC;
    }

    @Override
    public String getDefaultValue() {
      return AUTH_LDAP_GROUP_FIELDS_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPUserUidAttrProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_USER_UID_ATTR_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_USER_UID_ATTR_PROPERTY_DESC;
    }

    @Override
    public String getDefaultValue() {
      return AUTH_LDAP_USER_UID_ATTR_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPUserBaseDNProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_USER_BASE_DN_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_USER_BASE_DN_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPGroupBaseDNProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_GROUP_BASE_DN_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_GROUP_BASE_DN_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPBaseDNProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_BASE_DN_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_BASE_DN_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPGroupMappingProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_GROUP_MAPPING_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_GROUP_MAPPING_PROPERTY_DESC;
    }
  }

  public static class AuthLDAPBruteforcePreventionProperty extends AbstractStringConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_BRUTEFORCE_PREVENTION_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_BRUTEFORCE_PREVENTION_PROPERTY_DESC;
    }

    @Override
    public String getDefaultValue() {
      return AUTH_LDAP_BRUTEFORCE_PREVENTION_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPBruteforceLockoutProperty extends AbstractIntegerConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_BRUTEFORCE_LOCKOUT_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_BRUTEFORCE_LOCKOUT_PROPERTY_DESC;
    }

    @Override
    public Integer getDefaultValue() {
      return AUTH_LDAP_BRUTEFORCE_LOCKOUT_PROPERTY_DEFAULT;
    }
  }

  public static class AuthLDAPCacheGroupsProperty extends AbstractIntegerConfigProperty {

    @Override
    public String getKey() {
      return AUTH_LDAP_CACHE_GROUPS_PROPERTY;
    }

    @Override
    public String description() {
      return AUTH_LDAP_CACHE_GROUPS_PROPERTY_DESC;
    }

    @Override
    public Integer getDefaultValue() {
      return AUTH_LDAP_CACHE_GROUPS_PROPERTY_DEFAULT;
    }
  }

}
