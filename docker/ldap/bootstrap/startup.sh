#!/bin/sh
set -eu

# Funções auxiliares para evitar repetição de código
ldap_exec() {
    ldapadd -x -H ldap://ldap_dev:389 -D "${SPRING_LDAP_USERNAME}" -w "${SPRING_LDAP_PASSWORD}" "$@"
}

ldap_exists() {
    # Retorna 0 se o DN existir, 1 se não existir
    ldapsearch -x -H ldap://ldap_dev:389 -D "${SPRING_LDAP_USERNAME}" -w "${SPRING_LDAP_PASSWORD}" -b "$1" -s base "objectClass=*" > /dev/null 2>&1
}

create_if_missing() {
    DN=$1
    LDIF_FILE=$2
    if ldap_exists "$DN"; then
        echo "Aviso: Objeto $DN já existe. Pulando..."
    else
        echo "Criando objeto: $DN"
        # Extrai apenas o bloco correspondente ao DN do arquivo temporário
        sed -n "/^dn: $DN/,/^$/p" "$LDIF_FILE" | ldap_exec
    fi
}

# Gerar hashes
USER1_HASH="$(slappasswd -s "${LDAP_USER1_PASSWORD}")"
USER2_HASH="$(slappasswd -s "${LDAP_USER2_PASSWORD}")"

# Gerar o arquivo LDIF completo
LDIF_PATH="/tmp/bootstrap.ldif"
cat <<EOF > "$LDIF_PATH"
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

# Execução condicional
create_if_missing "ou=users,${SPRING_LDAP_BASE}" "$LDIF_PATH"
create_if_missing "ou=groups,${SPRING_LDAP_BASE}" "$LDIF_PATH"
create_if_missing "uid=user1,ou=users,${SPRING_LDAP_BASE}" "$LDIF_PATH"
create_if_missing "uid=user2,ou=users,${SPRING_LDAP_BASE}" "$LDIF_PATH"
create_if_missing "cn=developers,ou=groups,${SPRING_LDAP_BASE}" "$LDIF_PATH"