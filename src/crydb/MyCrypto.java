package crydb;
/*
Криптографические утилиты, по мотивам
http://findevelop.blogspot.ru/2013/04/java.html
 */

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.bind.DatatypeConverter;

public class MyCrypto {
  private final String ALGORITHM = "RSA";
  private final String ALGORITHM_SIMMETRIC = "AES";

  private String s_publicKey = null;
  private String s_privateKey = null;

  private PublicKey k_publicKey = null;
  private PrivateKey k_privateKey = null;

  private final String delim = "ghijklmnopqrstuvwyz"; // буквы отделители

  public MyCrypto() {
    ;
  }

  public MyCrypto(String publicKey, String privateKey) {
    this.s_publicKey = publicKey;
    this.s_privateKey = privateKey;
    try {
      if (publicKey != null && publicKey.length() > 4)
        k_publicKey = restorePublic(s_publicKey);     // восстановим публичный ключ
      if (privateKey != null && privateKey.length() > 4)
        k_privateKey = restorePrivate(s_privateKey);  // восстановим приватный ключ
    } catch (Exception e) {
      System.err.println("?-Error-ошибка восстановления публичного или приватного ключа: " + e.getMessage());
    }
  }

  /**
   * Сгенерировать новую пару ключей
   */
  public void generateKeys() {
    try {
      final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
      keyGen.initialize(1024, new SecureRandom());
      final KeyPair key = keyGen.generateKeyPair();
      this.k_publicKey = key.getPublic();
      this.s_publicKey = byte2Hex(k_publicKey.getEncoded());
      this.k_privateKey = key.getPrivate();
      this.s_privateKey = byte2Hex(k_privateKey.getEncoded());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getPrivateKey() {
    return s_privateKey;
  }

  public String getPublicKey() {
    return s_publicKey;
  }

  /**
   * Зашифровать сообщение для публичным ключом.
   * Зашифровываем сообщения публичным ключом Получателя Сообщения
   *
   * @param message сообщение
   * @return зашифрованная строка из 16-ричных символов
   */
  public String encrypt(String message) {
    String otv = "<error encrypt>";
    try {
      byte[] cipherText = encryptRSA(message.getBytes());
      otv = byte2Hex(cipherText);
    } catch (GeneralSecurityException ex) {
      //eх.printStackTrace();
      otv = otv + " " + ex.getMessage();
    }
    return otv;
  }

  /**
   * Зашифровать байты по алгоритму RSA
   * @param mess
   * @return
   * @throws GeneralSecurityException
   */
  private byte[] encryptRSA(byte[] mess) throws GeneralSecurityException
  {
    final Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, this.k_publicKey);
    byte[] cipherText = cipher.doFinal(mess);
    return cipherText;
  }

  /**
   * Зашифровать байты по алгоритму RSA
   * @param key   ключ
   * @param mess  исходное сообщение
   * @return
   * @throws GeneralSecurityException
   */
  private byte[] encryptAES(byte[] key, byte[] mess) throws GeneralSecurityException
  {
    final Cipher cipher = Cipher.getInstance(ALGORITHM_SIMMETRIC);
    SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM_SIMMETRIC);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    byte[] cipherText = cipher.doFinal(mess);
    return cipherText;
  }

  /**
   * Расшифровать зашифрованное сообщение hex (16-ричных) символов.
   * Символы с кодом менее '0' (пробел, табуляция, перевод строки) игнорируются
   * Расшифровываем приватных ключом Получателя Сообщенияю
   * @param cryptMessage зашифрованное сообщение из hex символов и возможно пробелов, табуляций, переводов строки
   * @return расшифрованное сообщение
   */
  public String decrypt(String cryptMessage)
  {
    String otv = "<error decrypt> "; // ответ в случае ошибки раскодирования
    try {
      String hexc = onlyHex(cryptMessage);    // зашифрованное (16-ричные символы)
      byte[] b = hex2Byte(hexc);              // зашифрованное байты
      byte[] dectyptedText = decryptRSA(b);
      otv = new String(dectyptedText);
    } catch (GeneralSecurityException ex) {
      //ex.printStackTrace();
      otv = otv + ex.getMessage();
    }
    return otv;
  }

  /**
   * Расшифровать сообщения по RSA
   * @param cryptMess  зашифрованные байты
   * @return
   */
  private byte[]  decryptRSA(byte[] cryptMess) throws GeneralSecurityException
  {
    final Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, this.k_privateKey);
    byte[] dectyptedText = cipher.doFinal(cryptMess);
    return dectyptedText;
  }

  /**
   * Расшифровать сообщения по AES
   * @param key       ключевые байты
   * @param cryptMess зашифрованные байты
   * @return
   */
  private byte[]  decryptAES(byte[] key,byte[] cryptMess) throws GeneralSecurityException
  {
    final Cipher cipher = Cipher.getInstance(ALGORITHM_SIMMETRIC);
    SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM_SIMMETRIC);
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    byte[] dectyptedText = cipher.doFinal(cryptMess);
    return dectyptedText;
  }

  /**
   * Восстановить публичный ключ из строки hex (16-ричных символов)
   * @param hexStr  строка hex(16-ричных) символов
   * @return  публичный ключ
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  private PublicKey restorePublic(String hexStr) throws NoSuchAlgorithmException, InvalidKeySpecException
  {
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    byte[] b = hex2Byte(hexStr);    // сделаем, требуемый байтовый массив
    EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(b);
    return keyFactory.generatePublic(publicKeySpec);
  }

  /**
   * Восстановить приватный ключ из строки hex (16-ричных символов)
   * @param hexStr  строка hex(16-ричных) символов
   * @return  приватный ключ
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  private PrivateKey restorePrivate(String hexStr) throws NoSuchAlgorithmException, InvalidKeySpecException
  {
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    byte[] b = hex2Byte(hexStr);    // сделаем, требуемый байтовый массив
    EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(b);
    return keyFactory.generatePrivate(privateKeySpec);
  }

  /*
  private String byte2Hex(byte b[])
  {
    String hs = "";
    String stmp;
    for (int n = 0; n < b.length; n++) {
      stmp = Integer.toHexString(b[n] & 0xff);
      if (stmp.length() == 1)
        hs = hs + "0" + stmp;
      else
        hs = hs + stmp;
    }
    return hs.toLowerCase();
  }

  private byte hex2Byte(char a1, char a2)
  {
    int k;
    if (a1 >= '0' && a1 <= '9') k = a1 - 48;
    else if (a1 >= 'a' && a1 <= 'f') k = (a1 - 97) + 10;
    else if (a1 >= 'A' && a1 <= 'F') k = (a1 - 65) + 10;
    else k = 0;
    k <<= 4;
    if (a2 >= '0' && a2 <= '9') k += a2 - 48;
    else if (a2 >= 'a' && a2 <= 'f') k += (a2 - 97) + 10;
    else if (a2 >= 'A' && a2 <= 'F') k += (a2 - 65) + 10;
    else k += 0;
    return (byte) (k & 0xff);
  }

  private byte[] hex2Byte(String str)
  {
    int l = str.length();
    if (l % 2 != 0) return null;
    byte r[] = new byte[l / 2];
    int k = 0;
    for (int i = 0; i < str.length() - 1; i += 2) {
      r[k] = hex2Byte(str.charAt(i), str.charAt(i + 1));
      k++;
    }
    return r;
  }
*/


  public String byte2Hex(byte b[])
  {
    String hex = DatatypeConverter.printHexBinary(b);
    return hex;
  }

  public byte[] hex2Byte(String str)
  {
    byte b[] = DatatypeConverter.parseHexBinary(str);
    return b;
  }

  // TODO сделать проверку хэш-суммы сообщения
  // CRC32 https://www.quickprogrammingtips.com/java/how-to-calculate-crc32-checksum-in-java.html
  // http://www.codejava.net/coding/how-to-calculate-md5-and-sha-hash-values-in-java
  // https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html

  /**
   * Добавляет переводы строки во входную строку
   * @param input входная строка
   * @return  выходная строка с переводами строк
   */
  public String wrapLine(String input)
  {
    final int mmx = 46; // максимальная длина строки
    final int n = input.length();
    final int m = n + 2*(mmx-1+n)/mmx; // добавка перевод строк
    int i;
    StringBuilder sb = new StringBuilder(m);
    for(i=0; i<n;) {
      sb.append(input.charAt(i++));
      if((i % mmx) == 0)
        sb.append("\r\n");  // добавляем перевод строки
    }
    return sb.toString();
  }

  /**
   * Выбирает из входной строки только hex-символы и записывает их на выход
   * @param input входная строка
   * @return  выходная строка состоящая только их hex-символов
   */
  public String onlyHex(String input)
  {
    int i, n;
    n = input.length();
    StringBuilder sb = new StringBuilder(n);
    for(i=0; i<n; i++) {
      char c = input.charAt(i);
      if( (c >= '0' && c <= '9') ||
          (c >= 'a' && c <= 'f') ||
          (c >= 'A' && c <= 'F') )
        sb.append(c);
    }
    return sb.toString();
  }

  ////////////////////////////////////////////////
  // Эксперимент с большими данными

  public String encryptBig(String text)
  {
    String otvet = "<big encrypt error> ";
    // тест
    SecureRandom random = new SecureRandom();
    byte[] seskey = new byte[16];  // формируем сеансовый ключ
    random.nextBytes(seskey);      // случайная последовательность
    byte[] mess = text.getBytes();  // байты сообщения
    try {
      byte[] cryptSesKey = encryptRSA(seskey);  // зашифруем ключ - получим 128 байт
      byte[] cryptText   = encryptAES(seskey, mess);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      bos.write(cryptSesKey);
      bos.write(cryptText);
      byte[] bo = bos.toByteArray();
      String otv = byte2Hex(bo);
      otvet = wrapLine(otv);
    } catch (IOException | GeneralSecurityException ex) {
      //eх.printStackTrace();
      otvet = otvet + " " + ex.getMessage();
    }
    return otvet;
  }

  public String decryptBig(String message)
  {
    int l, lk, i, j;
    String otvet = "<error big decrypt> "; // ответ в случае ошибки раскодирования
    try {
      String mess = onlyHex(message);     // hex символы
      byte[] crypto = hex2Byte(mess);     // зашифрованные байты
      //
      l = crypto.length;                  // длина общего массивы
      lk = 128;                           // длина массива зашифрованного ключа
      //
      byte[] cryptSesKey = new byte[lk];            // байтовый массив с зашифрованным ключом
      for(i=0; i<lk; i++) cryptSesKey[i] = crypto[i];
      byte[] sesKey    = decryptRSA(cryptSesKey);   // расшифрованный ключ
      byte[] cryptText = new byte[l - lk];          // байтовый массив с зашифрованным сообщением
      for(i=lk, j=0; i<l;) cryptText[j++] = crypto[i++];
      byte[] decrMess = decryptAES(sesKey, cryptText);  // расшифрованное сообщение
      otvet = new String(decrMess);                 // строка расшифрованного сообщения
    } catch (NullPointerException | ArrayIndexOutOfBoundsException | GeneralSecurityException ex) {
      otvet = otvet + ex.getMessage();
    }
    return otvet;
  }

} // end of class

