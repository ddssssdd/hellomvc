package com.sfu.chapter2.helper;


import com.sfu.chapter2.util.PropsUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class DatabaseHelper {
    private static final org.slf4j.Logger LOGGER= LoggerFactory.getLogger(DatabaseHelper.class);

    private static final QueryRunner QUERY_RUNNER;// = new QueryRunner();
    private static final ThreadLocal<Connection> CONNECTION_HOLDER;//= new ThreadLocal<Connection>();
    private static final BasicDataSource DATA_SOURCE;


    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    static{
        CONNECTION_HOLDER = new ThreadLocal<Connection>();
        QUERY_RUNNER = new QueryRunner();

        Properties conf = PropsUtil.loadProps("config.properties");
        DRIVER = conf.getProperty("jdbc.driver");
        URL = conf.getProperty("jdbc.url");
        USERNAME = conf.getProperty("jdbc.username");
        PASSWORD = conf.getProperty("jdbc.password");

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUrl(URL);
        DATA_SOURCE.setUsername(USERNAME);
        DATA_SOURCE.setPassword(PASSWORD);
        /*
        try{
            Class.forName(DRIVER);

        }catch (ClassNotFoundException e){
            LOGGER.error("can not load jdbc driver", e);
        }
        */
    }
    public static Connection getConnection(){
        Connection connection= CONNECTION_HOLDER.get();
        if (connection==null){
            try{

                //connection= DriverManager.getConnection(URL,USERNAME,PASSWORD);
                connection = DATA_SOURCE.getConnection();

            }catch (Exception ex){
                LOGGER.error("create connection failure", ex);
                throw new RuntimeException(ex);
            }finally {
                CONNECTION_HOLDER.set(connection);
            }
        }

        return connection;
    }

    public static void closeConnection(){
        /*
        Connection connection = CONNECTION_HOLDER.get();
        if (connection!=null){
            try{
                connection.close();
            }catch (Exception e){
                LOGGER.error("close connection error", e);
            }
            finally {
                CONNECTION_HOLDER.remove();
            }
        }*/
    }


    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object ... params){

        Connection connection = getConnection();
        List<T> entityList = null;
        try{
            entityList = QUERY_RUNNER.query(connection,sql,new BeanListHandler<T>(entityClass),params);
            LOGGER.info("Execute sql:" + sql);
        }catch (SQLException e){
            LOGGER.error("query entity failure", e);

        }finally {
            closeConnection();
        }
        return entityList;
    }
    public static <T> T queryEntity(Class<T> entityClass, String sql, Object ... params){
        T entity;
        try{
            Connection connection = getConnection();
            entity = QUERY_RUNNER.query(connection, sql, new BeanHandler<T>(entityClass),params);
        }catch (SQLException ex){
            LOGGER.error("query entity failure.", ex);
            throw new RuntimeException(ex);
        }finally {
            closeConnection();
        }
        return entity;
    }
    public static List<Map<String, Object>> executeQuery(String sql, Object ... params){
        List<Map<String, Object>> result;
        try{
            Connection connection = getConnection();
            result = QUERY_RUNNER.query(connection,sql,new MapListHandler(),params);
        }catch (SQLException ex){
            LOGGER.error("execute query failure.", ex);
            throw new RuntimeException(ex);
        }finally {
            closeConnection();
        }
        return result;
    }

    public static int executeUpdate(String sql, Object ... params){
        int rows = 0;
        try {
            Connection connection = getConnection();
            rows = QUERY_RUNNER.update(connection, sql, params);
        }catch (SQLException ex){
            LOGGER.error("update failure", ex);
            throw new RuntimeException(ex);
        }finally {
            closeConnection();
        }
        return rows;
    }

    public static <T> boolean insertEntity(Class<T> entityClass, Map<String,Object> fieldMap){
        String sql = "insert into "+getTableName(entityClass);
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for(String fieldName : fieldMap.keySet()){
            columns.append(fieldName).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "),columns.length(),")");
        values.replace(values.lastIndexOf(", "),values.length(),")");
        sql += columns +" values "+ values;
        Object[] params = fieldMap.values().toArray();
        return executeUpdate(sql, params)==1;
    }
    public static <T> boolean updateEntity(Class<T> entityClass,long id, Map<String,Object> fieldMap){
        String sql = "Update "+getTableName(entityClass)+" set ";
        StringBuilder columns = new StringBuilder("(");
        for(String fieldName : fieldMap.keySet()){
            columns.append(fieldName).append("=?, ");

        }

        sql += columns.replace(columns.lastIndexOf(", "),columns.length()," where id=?");
        List<Object> paramList = new ArrayList<Object>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        return executeUpdate(sql, paramList)==1;
    }
    public static <T> boolean deleteEntity(Class<T> entityClass, long id){
        String sql = "Delete from " + getTableName(entityClass) + " where id=?";
        return executeUpdate(sql,id)==1;
    }



    public static String getTableName(Class<?> entityClass){
        return entityClass.getSimpleName();
    }

}
