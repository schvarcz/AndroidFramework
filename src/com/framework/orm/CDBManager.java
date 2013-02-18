package com.framework.orm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.framework.CApp;
import com.framework.orm.CDBCore.OpenHelper;

/**
 * Auxilia no gerenciamento de uma conexão com um banco de dados SQLite.
 * 
 * @author enovative
 */
public class CDBManager {
	private static SQLiteDatabase mDB = null;
	private static OpenHelper mOpenHelper = null;

	public static SQLiteDatabase db() {
		open(CApp.getInstance());
		return mDB;
	}

	public static SQLiteDatabase db(Context c) {
		open(c);
		return mDB;
	}

	/**
	 * Abre uma conexão com o banco de dados. Se o banco não existir, cria um
	 * novo.
	 */
	private static void open(Context c) {
		if (mDB == null || !mDB.isOpen()) {
			mDB = openHelper(c).getWritableDatabase();
		}
	}

	public static OpenHelper openHelper() {
		if (mOpenHelper == null) {
			mOpenHelper = new OpenHelper(CApp.getInstance());
		}
		return mOpenHelper;
	}

	public static OpenHelper openHelper(Context c) {
		if (mOpenHelper == null) {
			mOpenHelper = new OpenHelper(c);
		}
		return mOpenHelper;
	}

	/**
	 * Encerra a conexão com o banco de dados.
	 */
	public static void close() {
		if (mDB == null) {
			return;
		}

		if (mDB.isOpen()) {
			mDB.close();
		}
		mDB = null;
	}

	/**
	 * Executa um comando SQL. Não deve ser usado para realização de consultas
	 * (usar query).
	 * 
	 * @param sql
	 *            Comando SQL a ser executado.
	 */
	public static synchronized int execSQL(String sql) {
		if (sql == null || sql.equals(""))
			return -1;
		db().execSQL(sql);
		Cursor c = query("SELECT last_insert_rowid()");
		c.moveToFirst();
		int ret = c.getInt(0);
		c.close();

		return ret;
	}

	/**
	 * Executa uma seleção.
	 * 
	 * @param sql
	 *            SQL da seleção.
	 * @return Cursor contendo os resultados da seleção.
	 */
	public static Cursor query(String sql) {
		return db().rawQuery(sql, null);
	}

	/**
	 * Executa uma seleção.
	 * 
	 * @param sql
	 *            SQL da seleção.
	 * @param selectionArgs
	 *            Argumentos da seleção.
	 * @return Cursor contendo os resultados da seleção.
	 */
	public static Cursor query(String sql, String selectionArgs[]) {
		Log.d("Debug", sql);

		return db().rawQuery(sql, selectionArgs);
	}

	/**
	 * Executa um script. Cada comando SQL deve estar em uma única linha.
	 * 
	 * @param filename
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static synchronized void execScript(String filename)
			throws MalformedURLException, IOException {
		openHelper().execScript(db(), filename);
	}

	public static synchronized void runScript(String filename)
			throws FileNotFoundException, IOException {
		openHelper().runScript(db(), filename);
	}

	public static synchronized void runBufferedScript(BufferedReader reader)
			throws SQLException, IOException {
		openHelper().runBufferedScript(db(), reader);
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
	public static synchronized String[] fetchScript(String filename)
			throws MalformedURLException, IOException {
		URL serverURL = new URL(filename);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				serverURL.openStream()));
		return openHelper().fetchScript(in);
	}

	public static boolean checkDataBase(String dbname) {
		SQLiteDatabase checkDB = null;
		try {
			checkDB = SQLiteDatabase.openDatabase(dbname, null,
					SQLiteDatabase.OPEN_READONLY);
			checkDB.close();
		} catch (SQLiteException e) {
			// database doesn't exist yet.
		}
		return checkDB != null ? true : false;
	}
}
