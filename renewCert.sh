#!/bin/bash
set -x #echo on

LETS_ENCRYPT_PATH=/etc/letsencrypt/live/kofify.com
TO_CERT_JAVA_FILE=/var/springboot/app/mycert.p12
FROM_CERT_JAVA_FILE="${LETS_ENCRYPT_PATH}/mycert.p12"

/usr/local/bin/certbot-auto renew --dry-run

echo "generating java certificate from pem"
openssl pkcs12 -export -in "${LETS_ENCRYPT_PATH}/fullchain.pem" -inkey "${LETS_ENCRYPT_PATH}/privkey.pem" -out FROM_CERT_JAVA_FILE -name "mycert" -CAfile "${LETS_ENCRYPT_PATH}/chain.pem" -caname root -password pass:password

echo "creating backup for java certificate ${TO_CERT_JAVA_FILE}.backup"
cp TO_CERT_JAVA_FILE "${TO_CERT_JAVA_FILE}.backup"

echo "coping new java certificate from ${FROM_CERT_JAVA_FILE} to ${TO_CERT_JAVA_FILE}"
cp FROM_CERT_JAVA_FILE TO_CERT_JAVA_FILE

echo "restarting application"
systemctl restart be
