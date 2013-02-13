package com.framework.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.framework.annotations.DBField;
import com.framework.annotations.DBPrimaryKey;
import com.framework.annotations.DBRelation;
import com.framework.annotations.DBTable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author schvarcz
 */
abstract public class DBBaseModel<T extends DBBaseModel> {

	private Integer primaryKey;
	DBTable classAnotation = this.getClass().getAnnotation(DBTable.class);
	ArrayList<Field> DBfields = new ArrayList<Field>();
	Field DBPrimaryKeyfield = null;
	SQLiteDatabase mDB = null;

	public DBBaseModel() {
		mDB = DBManager.db();

		Field[] fields = this.getClass().getFields();
		for (Field f : fields) {
			DBField dbfield = f.getAnnotation(DBField.class);
			if (dbfield != null) {
				DBfields.add(f);
			}

			DBPrimaryKey dbPrimaryKey = f.getAnnotation(DBPrimaryKey.class);
			if (dbPrimaryKey != null) {
				DBPrimaryKeyfield = f;
			}
		}
	}

	public DBBaseModel(Integer primaryKey) {
		this();
		findByPk(primaryKey);
	}

	public String getTableName() {
		if (classAnotation != null) {
			return classAnotation.TableName();
		}
		return null;
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void save() {
		if (this.primaryKey != null) {
			this.update();
		} else {
			this.insert();
		}
	}

	private void update() {
	}

	private void insert() {
	}

	public boolean delete() {
		if (this.primaryKey != null) {
			mDB.delete(getTableName(), DBPrimaryKeyfield.getName() + " = "
					+ this.primaryKey, null);
			return true;
		}
		return false;
	}

	public boolean deleteAll() {
		if (classAnotation != null) {
			mDB.delete(getTableName(), null, null);
			return true;
		}
		return false;
	}

	public boolean findByPk(Integer primaryKey) {
		Cursor cursor = mDB.query(getTableName(), null,
				DBPrimaryKeyfield.getName() + " = " + primaryKey, null, null,
				null, null);

		if (cursor.moveToFirst()) {
			for (String col : cursor.getColumnNames()) {
				this.setValue(col, cursor.getString(cursor.getColumnIndex(col)));
			}
			this.primaryKey = primaryKey;
		}
		return false;
	}

	public ArrayList<T> findAll() {
		Cursor cursor = mDB.query(getTableName(), null, null, null, null, null,
				null);
		ArrayList<T> ret = new ArrayList<T>();
		if (cursor.moveToFirst()) {
			 try {
				T novo = (T)this.getClass().newInstance();

				for (String col : cursor.getColumnNames()) {
					novo.setValue(col, cursor.getString(cursor.getColumnIndex(col)));
				}
				
				ret.add(novo);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	//Friendly, só é visto internamente....
	void setValue(String fieldName, String value) {
		try {
			for (Field f : DBfields) {
				if (f.getName().equals(fieldName)) {
					if ((f.getType() == Integer.class)
							|| (f.getType() == int.class)) {
						f.setInt(f, Integer.parseInt(value));
					} else if ((f.getType() == Float.class)
							|| (f.getType() == float.class)) {
						f.setFloat(this, Float.parseFloat(value));
					} else if ((f.getType() == Double.class)
							|| f.getType() == double.class) {
						f.setDouble(this, Double.parseDouble(value));
					} else if ((f.getType() == Long.class)
							|| f.getType() == long.class) {
						f.setLong(this, Long.parseLong(value));
					} else if ((f.getType() == Short.class)
							|| (f.getType() == short.class)) {
						f.setShort(this, Short.parseShort(value));
					} else if ((f.getType() == Boolean.class)
							|| (f.getType() == boolean.class)) {
						f.setBoolean(this, (Integer.parseInt(value) != 0 ? true
								: false));
					} else if (f.get(this) instanceof String) {
						f.set(this, value);
					} else { //Se não for nenhum tipo válido... tenta chamar o método valueOf(String)
						Method parserString = f.getType().getMethod("valueOf", new Class[] { String.class });
						f.set(this, parserString.invoke(f, value));
					}
				}
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public T[] findAllByAttributes() {
		// TODO: Ainda não implementado.
		return null;
	}

	public ArrayList<T> loadRelations(String fieldName) {
		try {
			Field f = this.getClass().getField(fieldName);
			DBRelation relation = f.getAnnotation(DBRelation.class);
			ArrayList<T> ret = new ArrayList<T>();
			if (relation != null) {
				T novo = (T)this.getClass().newInstance();
				//TODO: Popular com dados.
				ret.add(novo);
			}
			return ret;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	public T loadRelation(String fieldName) {
		try {
			Field f = this.getClass().getField(fieldName);
			DBRelation relation = f.getAnnotation(DBRelation.class);
			if (relation != null) {
				Class<?> returnType = f.getType();
				T novo = (T)this.getClass().newInstance();
				//TODO: Popular com dados.
				return novo;
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
