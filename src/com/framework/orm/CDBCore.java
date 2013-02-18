package com.framework.orm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CDBCore {
	public static class OpenHelper extends SQLiteOpenHelper {
		public static final String DATABASE_NAME = "eneonits.db";
		private static final int DATABASE_VERSION = 1;

		public OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			Log.i("Banco", "construct");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("Banco", "create");
		    db.execSQL("CREATE TABLE Exemplo (id_exemplo INTEGER PRIMARY KEY, nome_qualquer TEXT, id_tipo_tabela INTEGER)");
			// TODO: Pegar SQL do App.
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i("Banco", "upgrade");
			// TODO: Por os deletes aqui!
			
			//Reseta os AI dos IDs
			db.execSQL("PRAGMA writable_schema = 1");
			db.execSQL("delete from sqlite_master where type = 'table'");
			db.execSQL("PRAGMA writable_schema = 0");
			onCreate(db);
		}
		
		//TODO: Talvez mover isso para uma classe a parte.

		/**
		 * Executa um script. Cada comando SQL deve estar em uma única linha.
		 * 
		 * @param filename
		 * @throws MalformedURLException
		 * @throws IOException
		 */
		public synchronized void execScript(SQLiteDatabase db, String filename)
				throws MalformedURLException, IOException {

			URL serverURL = new URL(filename);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					serverURL.openStream()));

			runBufferedScript(db, reader);
		}

		public synchronized void runScript(SQLiteDatabase db, String filename)
				throws FileNotFoundException, IOException {
			File file = new File(filename);
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));

			runBufferedScript(db, reader);
			reader.close();
		}

		public synchronized void runBufferedScript(SQLiteDatabase db,
				BufferedReader reader) throws MalformedURLException,
				IOException {

			String[] sqls = fetchScript(reader);
			for (String sql : sqls) {
				if (!sql.trim().equals("")) {
					Log.i("Executando", sql);
					db.execSQL(sql);
				}
			}
		}

		/**
		 * Busca os comandos SQL para a criação do banco de dados em um servidor
		 * remoto.
		 * 
		 * @param filename
		 *            Endereço do arquivo contendo o SQL no servidor remoto.
		 * @return Vetor de strings, cada uma contendo um comando SQL.
		 * @throws MalformedURLException
		 * @throws IOException
		 */
		public synchronized String[] fetchScript(String filename)
				throws MalformedURLException, IOException {
			URL serverURL = new URL(filename);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					serverURL.openStream()));
			return fetchScript(in);
		}

		/**
		 * Busca os comandos SQL para a criação do banco de dados em um servidor
		 * remoto.
		 * ON = 1;ON = 1;ON = 1;
		 * @param reader
		 *            BufferedReader que deverá ser separado.
		 * @return Vetor de strings, cada uma contendo um comando SQL.
		 * @throws MalformedURLException
		 * @throws IOException
		 */
		public synchronized String[] fetchScript(BufferedReader reader)
				throws MalformedURLException, IOException {
			String tmpstr = "";

			while (reader.ready()) {
				String line = reader.readLine();
				if (!(line.startsWith("--") || line.startsWith("/*") || line
						.startsWith("SET"))) {
					tmpstr += line;
				}
			}

			reader.close();

			String result[] = tmpstr.split(";");

			return result;
		}
	}
}
