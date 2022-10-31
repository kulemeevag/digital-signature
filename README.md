# digital-signature

## Генерируем пару для закрытого и открытого ключа

```bash
keytool -genkeypair -alias senderKeyPair -keyalg RSA -keysize 2048 -dname "cn=digital-signature, ou=crypto, o=tsu, c=ru" \
        -validity 365 -storetype PKCS12 -keystore sender_keystore.p12 -storepass changeit
```

## Экспортируем открытый ключ

```bash
keytool -exportcert -alias senderKeyPair -storetype PKCS12 -keystore sender_keystore.p12 -file sender_certificate.cer -rfc -storepass changeit
```

## Импортируем открытый ключ

```bash
keytool -importcert -alias receiverKeyPair -storetype PKCS12 -keystore receiver_keystore.p12 -file sender_certificate.cer -rfc -storepass changeit
```