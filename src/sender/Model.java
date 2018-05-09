package sender;

import crydb.Database;
import crydb.DatabaseSqlite;
import crydb.MyCrypto;

import java.util.ArrayList;

/*
 Модель отправки сообщений
 */
public class Model {
  private String    f_dbName = "keys.db";
  private Database  f_db;    // база данных ключей

  Model()
  {
    Database db = new DatabaseSqlite(f_dbName);
    f_db = db;
  }

  /**
   * Взять список получателей из таблицы keys в виде массива строк
   * @return массив строк
   */
  public String[]  getUsrKeys()
  {
    // получим список имен из БД
    ArrayList<String[]> ardb = f_db.DlookupArray("SELECT usr FROM keys ORDER BY usr");
    int n = ardb.size();          // кол-во имен
    String[] ar = new String[n];  // создадим массив для значений имен
    int i = 0;
    for (String[] rst: ardb) {
      ar[i++] = rst[0]; // добавим имя в массив
    }
    return  ar;
  }

  /**
   * Зашифровать текст для получателя
   * @param receiver  имя получателя
   * @param text      текст сообщения
   * @return  зашифрованное сообщение
   */
  public String encryptText(String receiver, String text)
  {
    String  pubkey = f_db.Dlookup("SELECT publickey FROM keys WHERE usr='" + receiver + "'");
    MyCrypto mc = new MyCrypto(pubkey, null);
    String  crypt = mc.encryptBig(text);
    return crypt;
  }

}

