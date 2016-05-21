package com.shouxinjk.ihealth.data.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.shouxinjk.ihealth.data.util.Column;
import com.shouxinjk.ihealth.data.util.Util;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariConnectionProvider{

    private Map<String, Object> configMap;
    private transient HikariDataSource dataSource;
    private int queryTimeoutSecs = 10;
	
	public HikariConnectionProvider(Map<String, Object> hikariConfigMap,int timeout) {
        this.configMap = hikariConfigMap;
        this.queryTimeoutSecs = timeout;
    }
    
	public synchronized void prepare() {
        if(dataSource == null) {
            Properties properties = new Properties();
            properties.putAll(configMap);
            HikariConfig config = new HikariConfig(properties);
            this.dataSource = new HikariDataSource(config);
            this.dataSource.setAutoCommit(false);
        }
	}

	public List<List<Column>> query(String sql) {
        Connection connection = null;
        try {
            connection = this.dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
//            PreparedStatement preparedStatement = connection.prepareStatement(sql);
//            preparedStatement.setQueryTimeout(queryTimeoutSecs);
//            setPreparedStatementParams(preparedStatement, queryParams);
//            ResultSet resultSet = preparedStatement.executeQuery();
            List<List<Column>> rows = new ArrayList<List<Column>>();
            while(resultSet.next()){
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                List<Column> row = new ArrayList<Column>();
                for(int i=1 ; i <= columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    int columnType = metaData.getColumnType(i);
                    Class columnJavaType = Util.getJavaType(columnType);
                    if (columnJavaType.equals(String.class)) {
                        row.add(new Column<String>(columnLabel, resultSet.getString(columnLabel), columnType));
                    } else if (columnJavaType.equals(Integer.class)) {
                        row.add(new Column<Integer>(columnLabel, resultSet.getInt(columnLabel), columnType));
                    } else if (columnJavaType.equals(Double.class)) {
                        row.add(new Column<Double>(columnLabel, resultSet.getDouble(columnLabel), columnType));
                    } else if (columnJavaType.equals(Float.class)) {
                        row.add(new Column<Float>(columnLabel, resultSet.getFloat(columnLabel), columnType));
                    } else if (columnJavaType.equals(Short.class)) {
                        row.add(new Column<Short>(columnLabel, resultSet.getShort(columnLabel), columnType));
                    } else if (columnJavaType.equals(Boolean.class)) {
                        row.add(new Column<Boolean>(columnLabel, resultSet.getBoolean(columnLabel), columnType));
                    } else if (columnJavaType.equals(byte[].class)) {
                        row.add(new Column<byte[]>(columnLabel, resultSet.getBytes(columnLabel), columnType));
                    } else if (columnJavaType.equals(Long.class)) {
                        row.add(new Column<Long>(columnLabel, resultSet.getLong(columnLabel), columnType));
                    } else if (columnJavaType.equals(Date.class)) {
                        row.add(new Column<Date>(columnLabel, resultSet.getDate(columnLabel), columnType));
                    } else if (columnJavaType.equals(Time.class)) {
                        row.add(new Column<Time>(columnLabel, resultSet.getTime(columnLabel), columnType));
                    } else if (columnJavaType.equals(Timestamp.class)) {
                        row.add(new Column<Timestamp>(columnLabel, resultSet.getTimestamp(columnLabel), columnType));
                    } else {
                        throw new RuntimeException("type =  " + columnType + " for column " + columnLabel + " not supported.");
                    }
                }
                rows.add(row);
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute select query " + sql, e);
        } finally {
            closeConnection(connection);
        }
	}

	public void execute(String sql) {
        Connection connection = null;
        try {
            connection = this.dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL", e);
        } finally {
            closeConnection(connection);
        }
	}
	
    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to close connection", e);
            }
        }
    }

}
