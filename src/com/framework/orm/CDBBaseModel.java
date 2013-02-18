package com.framework.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.framework.annotations.CDBField;
import com.framework.annotations.CDBPrimaryKey;
import com.framework.annotations.CDBRelation;
import com.framework.annotations.CDBTable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 
 * @author schvarcz
 */
abstract public class CDBBaseModel<T extends CDBBaseModel<?>> {

	private Integer primaryKey;
	CDBTable classAnotation = this.getClass().getAnnotation(CDBTable.class);
	ArrayList<Field> DBfields = new ArrayList<Field>();
	Field DBPrimaryKeyfield = null;
	SQLiteDatabase mDB = null;

	public CDBBaseModel() {
		mDB = CDBManager.db();

		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			CDBField dbfield = f.getAnnotation(CDBField.class);
			if (dbfield != null) {
				DBfields.add(f);
			}

			CDBPrimaryKey dbPrimaryKey = f.getAnnotation(CDBPrimaryKey.class);
			if (dbPrimaryKey != null) {
				DBPrimaryKeyfield = f;
			}
		}
	}

	public CDBBaseModel(Integer primaryKey) {
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
	public void setPrimaryKey(Integer pk) {
		primaryKey = pk;
	}

	public String getPrimaryKeyName() {
		CDBField field = DBPrimaryKeyfield.getAnnotation(CDBField.class);
		if (field != null)
			return field.fieldName();
		return null;
	}

	public Field getFieldByFieldName(String name) {
		for (Field f : DBfields) {
			if (f.getAnnotation(CDBField.class).fieldName().equals(name)) {
				return f;
			}
		}
		return null;
	}

	public boolean save() {
		Integer id;
		if (this.primaryKey != null) {
			id = this.update();
		} else {
			id = this.insert();
		}
		if (id != null)
			return true;
		else
			return false;
	}

	private Integer update() {
		if (this.primaryKey != null) {
			return (int) mDB.update(getTableName(), getValues(),
					getPrimaryKeyName() + " = ?",
					new String[] { this.primaryKey.toString() });
		}
		return null;
	}

	private Integer insert() {
		return (int) mDB.insert(getTableName(), null, getValues());
	}

	public boolean delete() {
		if (this.primaryKey != null) {
			mDB.delete(getTableName(), getPrimaryKeyName() + " = ?",
					new String[] { this.primaryKey.toString() });
			return true;
		}
		return false;
	}

	public boolean findByPk(Integer primaryKey) {
		Cursor cursor = mDB.query(getTableName(), null, getPrimaryKeyName()
				+ " = " + primaryKey, null, null, null, null);

		if (cursor.moveToFirst()) {
			for (String col : cursor.getColumnNames()) {
				this.setValue(getFieldByFieldName(col),
						cursor.getString(cursor.getColumnIndex(col)));
			}
			this.primaryKey = primaryKey;
		}
		return false;
	}

	private ContentValues getValues() {
		ContentValues values = new ContentValues();
		for (Field f : DBfields) {
			getValues(values, f);
		}
		return values;
	}

	private void getValues(ContentValues values, Field f) {

		CDBField dbField = f.getAnnotation(CDBField.class);
		boolean isPrimaryKey = f.getAnnotation(CDBPrimaryKey.class) != null;

		try {
			if ((f.getType() == Integer.class) || (f.getType() == int.class)) {
				if (isPrimaryKey && (f.getInt(this) == 0))
					return;
				values.put(dbField.fieldName(), f.getInt(this));
			} else if ((f.getType() == Float.class)
					|| (f.getType() == float.class)) {

				if (isPrimaryKey && (f.getFloat(this) == 0))
					return;
				values.put(dbField.fieldName(), f.getFloat(this));
			} else if ((f.getType() == Double.class)
					|| f.getType() == double.class) {

				if (isPrimaryKey && (f.getDouble(this) == 0))
					return;
				values.put(dbField.fieldName(), f.getDouble(this));
			} else if ((f.getType() == Long.class) || f.getType() == long.class) {

				if (isPrimaryKey && (f.getLong(this) == 0))
					return;
				values.put(dbField.fieldName(), f.getLong(this));
			} else if ((f.getType() == Short.class)
					|| (f.getType() == short.class)) {

				if (isPrimaryKey && (f.getShort(this) == 0))
					return;
				values.put(dbField.fieldName(), f.getShort(this));
			} else if ((f.getType() == Boolean.class)
					|| (f.getType() == boolean.class)) {
				values.put(dbField.fieldName(), (Boolean) f.get(this));
			} else if (f.getType() == String.class) {

				if (isPrimaryKey && (((String) f.get(this)).equals("")))
					return;
				values.put(dbField.fieldName(), (String) f.get(this));
			} else { // Se não for nenhum tipo válido... tenta chamar o método
						// toString()
				Method parserString = f.getType().getMethod("toString");
				values.put(dbField.fieldName(),
						(String) parserString.invoke(f.get(this)));
			}
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

	void setValue(Field f, String value) {
		try {
			if(value == null)
			{
				f.set(this, value);
				return;
			}
			if ((f.getType() == Integer.class) || (f.getType() == int.class)) {
				f.setInt(this, Integer.parseInt(value));
			} else if ((f.getType() == Float.class)
					|| (f.getType() == float.class)) {
				f.setFloat(this, Float.parseFloat(value));
			} else if ((f.getType() == Double.class)
					|| f.getType() == double.class) {
				f.setDouble(this, Double.parseDouble(value));
			} else if ((f.getType() == Long.class) || f.getType() == long.class) {
				f.setLong(this, Long.parseLong(value));
			} else if ((f.getType() == Short.class)
					|| (f.getType() == short.class)) {
				f.setShort(this, Short.parseShort(value));
			} else if ((f.getType() == Boolean.class)
					|| (f.getType() == boolean.class)) {
				f.setBoolean(this,
						(Integer.parseInt(value) != 0 ? true : false));
			} else if (f.getType() == String.class) {
				f.set(this, value);
			} else { // Se não for nenhum tipo válido... tenta chamar o método valueOf(String)
				Method parserString = f.getType().getMethod("valueOf",
						new Class[] { String.class });
				f.set(this, parserString.invoke(f, value));
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

	public int deleteAll() {
		return mDB.delete(getTableName(), null, null);
	}

	public ArrayList<T> findAll() {
		String table = getTableName();
		Cursor cursor = mDB.query(table, null, null, null, null, null, null);
		ArrayList<T> ret = new ArrayList<T>();
		if (cursor.moveToFirst()) {
			do {
				try {
					T novo = (T) this.getClass().newInstance();
					for (String col : cursor.getColumnNames()) {
						novo.setValue(getFieldByFieldName(col),
								cursor.getString(cursor.getColumnIndex(col)));
						if(novo.getPrimaryKeyName().equals(col))
						{
							novo.setPrimaryKey(Integer.valueOf(cursor.getString(cursor.getColumnIndex(col))));
						}
					}

					ret.add(novo);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} while (cursor.moveToNext());
		}
		return ret;
	}

	public String getWhereClause(String joinWhere) {
		String ret = "";
		for (Field f : DBfields) {

			try {
				if (f.get(this) == null) {
					continue;
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			if (!ret.equals("")) {
				ret += " " + joinWhere + " ";
			}
			CDBField dbField = f.getAnnotation(CDBField.class);
			if ((f.getType() == Integer.class) || (f.getType() == int.class)) {
				ret += dbField.fieldName() + " = ?";
			} else if ((f.getType() == Float.class)
					|| (f.getType() == float.class)) {
				ret += dbField.fieldName() + " = ?";
			} else if ((f.getType() == Double.class)
					|| f.getType() == double.class) {
				ret += dbField.fieldName() + " = ?";
			} else if ((f.getType() == Long.class) || f.getType() == long.class) {
				ret += dbField.fieldName() + " = ?";
			} else if ((f.getType() == Short.class)
					|| (f.getType() == short.class)) {
				ret += dbField.fieldName() + " = ?";
			} else if ((f.getType() == Boolean.class)
					|| (f.getType() == boolean.class)) {
				ret += dbField.fieldName() + " = ?";
			} else if (f.getType() == String.class) {
				ret += dbField.fieldName() + " LIKE ?";
			} else { // Se não for nenhum tipo válido... vai tentar método
						// valueOf(String)
				ret += dbField.fieldName() + " LIKE ?";
			}
		}
		if(!ret.equals(""))
			return ret;
		return null;
	}

	public String[] getWhereArgs() {
		String ret = "";
		ArrayList<String> args = new ArrayList<String>();
		for (Field f : DBfields) {
			if (!ret.equals("")) {
				ret += " AND ";
			}
			String addField = null;


			try {

				Object obj = f.get(this);
				if (obj == null) {
					continue;
				}
				if ((f.getType() == Integer.class)
						|| (f.getType() == int.class)) {
					addField = Integer.toString(f.getInt(this));
				} else if ((f.getType() == Float.class)
						|| (f.getType() == float.class)) {
					addField = Float.toString(f.getFloat(this));
				} else if ((f.getType() == Double.class)
						|| f.getType() == double.class) {
					addField = Double.toString(f.getDouble(this));
				} else if ((f.getType() == Long.class)
						|| f.getType() == long.class) {
					addField = Long.toString(f.getLong(this));
				} else if ((f.getType() == Short.class)
						|| (f.getType() == short.class)) {
					addField = Short.toString(f.getShort(this));
				} else if ((f.getType() == Boolean.class)
						|| (f.getType() == boolean.class)) {
					addField = Boolean.toString(f.getBoolean(this));
				} else if (f.getType() == String.class) {
					addField = (String) f.get(this);
				} else { // Se não for nenhum tipo válido... tenta chamar o
							// método toString()
					Method parserString = f.getType().getMethod("toString");

					addField = (String) parserString.invoke(f.get(this));
				}
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
			args.add(addField);
		}
		if(args.size()>0)
			return args.toArray(new String[args.size()]);
		return null;
	}

	public ArrayList<T> findAllByAttributes() {
		String table = getTableName();
		String where = getWhereClause("AND");
		String[] whereArgs = getWhereArgs();
		Cursor cursor = mDB.query(table, null, where, whereArgs, null, null, null);
		ArrayList<T> ret = new ArrayList<T>();
		if (cursor.moveToFirst()) {
			do {
				try {
					T novo = (T) this.getClass().newInstance();

					for (String col : cursor.getColumnNames()) {
						novo.setValue(getFieldByFieldName(col),
								cursor.getString(cursor.getColumnIndex(col)));

						if(novo.getPrimaryKeyName().equals(col))
						{
							novo.setPrimaryKey(Integer.valueOf(cursor.getString(cursor.getColumnIndex(col))));
						}
					}

					ret.add(novo);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} while (cursor.moveToNext());
		}
		return ret;
	}

	public ArrayList<?> loadRelations(String fieldName) {
		try {
			Field f = this.getClass().getField(fieldName);
			CDBRelation relation = f.getAnnotation(CDBRelation.class);
			if (relation != null) {
				// TODO: Buscar os dados por relacao.
				Class<?> returnType = f.getType();
				CDBBaseModel<?> novo = (CDBBaseModel<?>) returnType.newInstance();
				return novo.findAll();
			}
			return null;
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

	public CDBBaseModel<?> loadRelation(String fieldName) {
		try {
			Field f = this.getClass().getField(fieldName);
			CDBRelation relation = f.getAnnotation(CDBRelation.class);
			if (relation != null) {
				Class<?> returnType = f.getType();
				CDBBaseModel<?> novo = (CDBBaseModel<?>) returnType.newInstance();

				// TODO: Testar field = '' e se eh derivada de DBBasemodel<?>.
				Field relField = getFieldByFieldName(relation.field());
				novo.findByPk(relField.getInt(this));
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

	@Override
	public String toString() {
		String ret = getTableName()+"("+this.primaryKey.toString()+")\n";
		ret+="{";
		for(Field f:DBfields)
		{
			String addField = "";
			try {
	
				if (f.get(this) == null) {
					continue;
				}
				if ((f.getType() == Integer.class)
						|| (f.getType() == int.class)) {
					addField = Integer.toString(f.getInt(this));
				} else if ((f.getType() == Float.class)
						|| (f.getType() == float.class)) {
					addField = Float.toString(f.getFloat(this));
				} else if ((f.getType() == Double.class)
						|| f.getType() == double.class) {
					addField = Double.toString(f.getDouble(this));
				} else if ((f.getType() == Long.class)
						|| f.getType() == long.class) {
					addField = Long.toString(f.getLong(this));
				} else if ((f.getType() == Short.class)
						|| (f.getType() == short.class)) {
					addField = Short.toString(f.getShort(this));
				} else if ((f.getType() == Boolean.class)
						|| (f.getType() == boolean.class)) {
					addField = Boolean.toString(f.getBoolean(this));
				} else if (f.getType() == String.class) {
					addField = (String) f.get(this);
				} else { // Se não for nenhum tipo válido... tenta chamar o
							// método toString()
					Method parserString = f.getType().getMethod("toString");
	
					addField = (String) parserString.invoke(f.get(this));
				}
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
			
			ret += "\n\t"+f.getName()+": "+addField;
		}
		ret += "\n}";
		return ret;
	}

}
