FROM alpine:latest

RUN apk add --no-cache openldap openldap-back-mdb openldap-clients bash

RUN mkdir -p /run/openldap /var/lib/openldap/openldap-data /etc/openldap/slapd.d \
    && chown -R ldap:ldap /run/openldap /var/lib/openldap /etc/openldap

COPY slapd.conf /etc/openldap/slapd.conf
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 389

ENTRYPOINT ["/entrypoint.sh"]
