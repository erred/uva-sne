package com.j256.ormlite.field.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseResults;
import java.sql.SQLException;
import java.util.UUID;

public class UuidType extends BaseDataType {
    public static int DEFAULT_WIDTH = 48;
    private static final UuidType singleTon = new UuidType();

    public boolean isSelfGeneratedId() {
        return true;
    }

    public boolean isValidGeneratedType() {
        return true;
    }

    public static UuidType getSingleton() {
        return singleTon;
    }

    private UuidType() {
        super(SqlType.STRING, new Class[]{UUID.class});
    }

    protected UuidType(SqlType sqlType, Class<?>[] clsArr) {
        super(sqlType, clsArr);
    }

    public Object parseDefaultString(FieldType fieldType, String str) throws SQLException {
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Problems with field ");
            sb.append(fieldType);
            sb.append(" parsing default UUID-string '");
            sb.append(str);
            sb.append("'");
            throw SqlExceptionUtil.create(sb.toString(), e);
        }
    }

    public Object resultToSqlArg(FieldType fieldType, DatabaseResults databaseResults, int i) throws SQLException {
        return databaseResults.getString(i);
    }

    public Object sqlArgToJava(FieldType fieldType, Object obj, int i) throws SQLException {
        String str = (String) obj;
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Problems with column ");
            sb.append(i);
            sb.append(" parsing UUID-string '");
            sb.append(str);
            sb.append("'");
            throw SqlExceptionUtil.create(sb.toString(), e);
        }
    }

    public Object javaToSqlArg(FieldType fieldType, Object obj) {
        return ((UUID) obj).toString();
    }

    public Object generateId() {
        return UUID.randomUUID();
    }

    public int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }
}
