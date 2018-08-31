package com.sfu.chapter2.helper;


import com.sfu.chapter2.util.PropsUtil;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

public final class DatabaseHelper {
    private static final org.slf4j.Logger LOGGER= LoggerFactory.getLogger(DatabaseHelper.class);

    private static final QueryRunner QUERY_RUNNER = new QueryRunner();
    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<Connection>();

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

    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    static{
        Properties conf = PropsUtil.loadProps("config.properties");
        DRIVER = conf.getProperty("jdbc.driver");
        URL = conf.getProperty("jdbc.url");
        USERNAME = conf.getProperty("jdbc.username");
        PASSWORD = conf.getProperty("jdbc.password");
        try{
            Class.forName(DRIVER);

        }catch (ClassNotFoundException e){
            LOGGER.error("can not load jdbc driver", e);
        }
    }
    public static Connection getConnection(){
        Connection connection= CONNECTION_HOLDER.get();
        if (connection==null){
            try{

                connection= DriverManager.getConnection(URL,USERNAME,PASSWORD);

            }catch (Exception ex){
                LOGGER.error("create connection failure", ex);
            }finally {
                CONNECTION_HOLDER.set(connection);
            }
        }

        return connection;
    }

    public static void closeConnection(){
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
        }
    }
}
