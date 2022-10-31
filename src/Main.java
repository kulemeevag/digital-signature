import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        final char[] password = "changeit".toCharArray();

        System.out.println("Загружаем закрытый ключ");
        PrivateKey privateKey = getPrivateKey(password);

        System.out.println("Читаем сообщение из файла");
        byte[] messageBytes = getMessageBytes();

        System.out.println();
        System.out.println("Сообщение = " + new String(messageBytes, StandardCharsets.UTF_8));
        System.out.println();

        System.out.println("Генерируем hash-функцию");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageHash = md.digest(messageBytes);

        System.out.println("Шифруем сообщение");
        Cipher privateCipher = Cipher.getInstance("RSA");
        privateCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] digitalSignature = privateCipher.doFinal(messageHash);

        System.out.println("Читаем открытый ключ");
        PublicKey publicKey = getPublicKey(password);

        System.out.println("Читаем hash-функцию зашифрованного сообщения");
        Cipher publicCipher = Cipher.getInstance("RSA");
        publicCipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedMessageHash = publicCipher.doFinal(digitalSignature);

        System.out.println("Генерируем hash-функцию сообщения для проверки");
        byte[] newMessageBytes = getMessageBytes();
        byte[] newMessageHash = md.digest(newMessageBytes);

        System.out.println("Проверяем hash-функции");
        boolean isCorrect = Arrays.equals(decryptedMessageHash, newMessageHash);

        System.out.println("hash-функции совпадают = " + isCorrect);
    }

    private static byte[] getMessageBytes() throws IOException {
        return Files.readAllBytes(Paths.get("message.txt"));
    }

    private static PublicKey getPublicKey(char[] password) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream("receiver_keystore.p12"), password);
        Certificate certificate = keyStore.getCertificate("receiverKeyPair");
        return certificate.getPublicKey();
    }

    private static PrivateKey getPrivateKey(char[] password) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream("sender_keystore.p12"), password);
        return (PrivateKey) keyStore.getKey("senderKeyPair", password);
    }


}