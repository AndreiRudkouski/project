package by.rudkouski.auction.pool;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {
    private static final ConnectionPool INSTANCE = new ConnectionPool();
    private final BlockingQueue<ProxyConnection> connectionQueue;
    private final int poolSize;

    private ConnectionPool() {
        poolSize = DataBaseManager.getPoolSize();
        int tryNum = 0;
        connectionQueue = new ArrayBlockingQueue<>(poolSize);
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            //log
            throw new RuntimeException("Wrong register driver", e);
        }

        for (int i = 0; i < poolSize; i++) {
            try {
                ProxyConnection connection = DataBaseManager.getConnection();
                connectionQueue.put(connection);
            } catch (SQLException | InterruptedException e) {
                //if throw exception try create and put additional connection
                //log
                tryNum++;
                if (tryNum < poolSize) {
                    i--;
                } else {
                    //log
                    throw new RuntimeException("Wrong initialization connection pool", e);
                }
            }
        }
    }

    public static ConnectionPool getInstance() {
        return INSTANCE;
    }

    public void closeConnectionPool() {
        closeConnectionQueue(connectionQueue);
    }

    private void closeConnectionQueue(BlockingQueue<ProxyConnection> queue) {
        for (int i = 0; i < queue.size(); i++) {
            try {
                queue.take().realClose();
            } catch (SQLException e) {
                //log
            } catch (InterruptedException e) {
                //log
            }
        }
    }

    public ProxyConnection takeConnection() {
        ProxyConnection connection = null;
        try {
            connection = connectionQueue.take();
        } catch (InterruptedException e) {
            //log
        }
        return connection;
    }

    public void returnConnection(ProxyConnection connection) {
        try {
            if (connection != null) {
                connection.setAutoCommit(true);
                connectionQueue.put(connection);
            }
        } catch (InterruptedException e) {
            //log
        } catch (SQLException e) {
            //log
        }
    }
}
