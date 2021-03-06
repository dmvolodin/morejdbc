package org.morejdbc;

import org.jetbrains.annotations.Nullable;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Objects;

public class SqlType<T> {

    private final String printName;
    private final int sqlType;
    /**
     * Setter can be null for read-only types.
     */
    @Nullable
    final CallableStatementSetter<T> setter;
    private final CallableStatementExtractor<T> extractor;

    private SqlType(String printName, int sqlType, @Nullable CallableStatementSetter<T> setter,
                    CallableStatementExtractor<T> extractor) {
        this.printName = printName;
        this.sqlType = sqlType;
        this.setter = setter;
        this.extractor = extractor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SqlType<?> that = (SqlType<?>) o;
        return sqlType == that.sqlType &&
                printName.equals(that.printName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(printName, sqlType);
    }

    static <T> SqlType<T> of(String printName, int sqlType, @Nullable CallableStatementSetter<T> setter,
                             CallableStatementExtractor<T> extractor) {
        return new SqlType<>(printName, sqlType, setter, extractor);
    }

    String getPrintName() {
        return printName;
    }

    int getSqlType() {
        return sqlType;
    }

    @Nullable
    T getValueOrNull(CallableStatement cs, int idx) throws SQLException {
        return extractor.getValueOrNull(cs, idx);
    }

    @Override
    public String toString() {
        return printName + "[" + sqlType + "]";
    }

    void inBeforeExecute(CallableStatement cs, int idx, T value) throws SQLException {
        setter.setValue(cs, idx, sqlType, value);
    }

    interface CallableStatementSetter<T> {
        void setValue(CallableStatement cs, int idx, int sqlType, T value) throws SQLException;
    }

    interface CallableStatementExtractor<T> {
        T getValueOrNull(CallableStatement cs, int idx) throws SQLException;
    }
}
