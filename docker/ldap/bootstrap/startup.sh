#!/bin/sh
set -eu

USER1_HASH="$(slappasswd -s "${LDAP_USER1_PASSWORD}")"
USER2_HASH="$(slappasswd -s "${LDAP_USER2_PASSWORD}")"

cat <<EOF >/tmp/bootstrap.ldif
dn: ou=users,${SPRING_LDAP_BASE}
objectClass: organizationalUnit
ou: users

dn: ou=groups,${SPRING_LDAP_BASE}
objectClass: organizationalUnit
ou: groups

dn: uid=user1,ou=users,${SPRING_LDAP_BASE}
objectClass: inetOrgPerson
objectClass: organizationalPerson
objectClass: person
objectClass: top
cn: User One
sn: One
uid: user1
userPassword: ${USER1_HASH}

dn: uid=user2,ou=users,${SPRING_LDAP_BASE}
objectClass: inetOrgPerson
objectClass: organizationalPerson
objectClass: person
objectClass: top
cn: User Two
sn: Two
uid: user2
userPassword: ${USER2_HASH}

dn: cn=developers,ou=groups,${SPRING_LDAP_BASE}
objectClass: groupOfNames
objectClass: top
cn: developers
member: uid=user1,ou=users,${SPRING_LDAP_BASE}
member: uid=user2,ou=users,${SPRING_LDAP_BASE}
EOF

ldapadd -x \
  -H ldap://ldap_dev:389 \
  -D "${SPRING_LDAP_USERNAME}" \
  -w "${SPRING_LDAP_PASSWORD}" \
  -f /tmp/bootstrap.ldif || \
ldapmodify -x \
  -H ldap://ldap_dev:389 \
  -D "${SPRING_LDAP_USERNAME}" \
  -w "${SPRING_LDAP_PASSWORD}" \
  -a -f /tmp/bootstrap.ldif