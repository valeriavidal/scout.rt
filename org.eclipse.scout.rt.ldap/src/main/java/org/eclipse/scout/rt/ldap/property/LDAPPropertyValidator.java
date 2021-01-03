package org.eclipse.scout.rt.ldap.property;

import org.eclipse.scout.rt.platform.config.IConfigurationValidator;
import org.eclipse.scout.rt.platform.exception.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LDAPPropertyValidator implements IConfigurationValidator {

  private static final Logger LOG = LoggerFactory.getLogger(LDAPPropertyValidator.class);

  @Override
  public boolean isValid(String key, String value) {

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_PROPERTY)) {

      if (value != null && value.length() == 1 && "01".indexOf(value) >= 0) {
        LOG.debug("Property: [{}] value: [{}] is valid", key, value);
        return true;
      }
      else {
        LOG.error("Incorrect value for property: {}", key);
        throw new InitializationException("Incorrect value for property: " + key);
      }
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_HOST_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
      return true;
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_PORT_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
      return true;
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SUPERADMIN_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SUPERADMIN_LOGIN_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SUPERADMIN_PASS_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_CONNECTION_TYPE_PROPERTY)) {

      if (value != null && value.length() == 1 && "012".indexOf(value) >= 0) {
        LOG.debug("Property: [{}] value: [{}] is valid", key, value);
        return true;
      }
      else {
        LOG.error("Incorrect value for property: {}", key);
        throw new InitializationException("Incorrect value for property: " + key);
      }
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_CONNECTION_PROTOCOL_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_CONNECTION_CIPHERS_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_AUTH_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_BIND_DN_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_BIND_PASS_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SECURITY_KEYSTORE_TYPE_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SECURITY_KEYSTORE_PROVIDER_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SECURITY_KEYSTORE_FILE_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SECURITY_KEYSTORE_PASS_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SECURITY_KEYSTORE_CERT_ALIAS_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SECURITY_KEYSTORE_CERT_PASS_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SECURITY_TRUSTSTORE_TYPE_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SECURITY_TRUSTSTORE_PROVIDER_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SECURITY_TRUSTSTORE_FILE_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SECURITY_TRUSTSTORE_PASS_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SECURITY_TRUSTSTORE_TRUSTLEVEL_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_SECURITY_TRUSTSTORE_OCSP_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_LOGIN_GROUP_PROPERTY)) {
      if (value != null) {
        LOG.debug("Property: [{}] value: [{}] is valid", key, value);
        return true;
      }
      else {
        LOG.error("Incorrect value for property: {}", key);
        throw new InitializationException("Incorrect value for property: " + key);
      }
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_GROUP_CLASSES_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_GROUP_FIELDS_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_USER_UID_ATTR_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_USER_BASE_DN_PROPERTY)) {
      if (value != null) {
        LOG.debug("Property: [{}] value: [{}] is valid", key, value);
        return true;
      }
      else {
        LOG.error("Incorrect value for property: {}", key);
        throw new InitializationException("Incorrect value for property: " + key);
      }
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_GROUP_BASE_DN_PROPERTY)) {
      if (value != null) {
        LOG.debug("Property: [{}] value: [{}] is valid", key, value);
        return true;
      }
      else {
        LOG.error("Incorrect value for property: {}", key);
        throw new InitializationException("Incorrect value for property: " + key);
      }
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_BASE_DN_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    if (key.equalsIgnoreCase(LDAPProperties.AUTH_LDAP_GROUP_MAPPING_PROPERTY)) {
      LOG.debug("Property: [{}] value: [{}]", key, value);
    }

    return false;
  }

}
