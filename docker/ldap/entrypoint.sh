#!/bin/sh
set -e

# Default values if not provided
LDAP_BASE_DN=${LDAP_BASE_DN:-"dc=example,dc=org"}
LDAP_ADMIN_PASSWORD=${LDAP_ADMIN_PASSWORD:-"admin"}

# Hash the admin password
LDAP_ADMIN_PASSWORD_HASH=$(slappasswd -s "$LDAP_ADMIN_PASSWORD")

# Template slapd.conf with environment variables
sed -e "s|\${LDAP_BASE_DN}|$LDAP_BASE_DN|g" \
    -e "s|\${LDAP_ADMIN_PASSWORD_HASH}|$LDAP_ADMIN_PASSWORD_HASH|g" \
    /etc/openldap/slapd.conf > /tmp/slapd.conf

# Ensure data directory exists and is clean if needed
mkdir -p /var/lib/openldap/openldap-data
chown -R ldap:ldap /var/lib/openldap/openldap-data

# Create base DN if it doesn't exist (initial run)
if [ ! -f /var/lib/openldap/openldap-data/data.mdb ]; then
    echo "Initializing LDAP database with base DN: $LDAP_BASE_DN"
    
    cat <<EOF > /tmp/base.ldif
dn: $LDAP_BASE_DN
objectClass: top
objectClass: dcObject
objectClass: organization
o: Ampliar
dc: $(echo $LDAP_BASE_DN | cut -d, -f1 | cut -d= -f2)
EOF

    slapadd -l /tmp/base.ldif -f /tmp/slapd.conf
    chown -R ldap:ldap /var/lib/openldap/openldap-data
fi

echo "Starting slapd..."
exec slapd -u ldap -g ldap -d 256 -f /tmp/slapd.conf -h "ldap:///"
