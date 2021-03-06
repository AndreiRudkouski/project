package by.rudkouski.auction.dao.impl;

import by.rudkouski.auction.entity.impl.User;
import by.rudkouski.auction.dao.IUserDao;
import by.rudkouski.auction.dao.exception.DaoException;
import by.rudkouski.auction.pool.ProxyConnection;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao implements IUserDao<User> {
    private ProxyConnection con;

    private static final String SQL_USER = "SELECT user_id, login, email, balance, ban, role_id FROM user WHERE email = ? AND password = ?";
    private static final String SQL_ADD_USER = "INSERT INTO user (email, password) VALUES (?, ?)";
    private static final String SQL_MAIL = "SELECT user_id FROM user WHERE email = ?";
    private static final String SQL_CHANGE_BAN = "UPDATE user SET ban = ? WHERE user_id = ?";
    private static final String SQL_USER_ID = "SELECT user_id, login, email, balance, ban, role_id FROM user WHERE user_id = ?";
    private static final String SQL_LOGIN = "SELECT login FROM user WHERE login = ?";
    private static final String SQL_LOGIN_CHANGE = "UPDATE user SET login = ? WHERE user_id = ?";
    private static final String SQL_PASSWORD_CHANGE = "UPDATE user SET password = ? WHERE user_id = ?";
    private static final String SQL_BALANCE = "SELECT balance FROM user WHERE user_id = ?";
    private static final String SQL_UPDATE_BALANCE = "UPDATE user SET balance = ? WHERE user_id = ?";
    private static final String SQL_PREV_MAX_BET = "SELECT bet.user_id, bet, balance FROM bet " +
            "JOIN user ON bet.user_id = user.user_id " +
            "WHERE bet = (SELECT MAX(bet) FROM bet WHERE lot_id = ?)";
    private static final String SQL_PASSWORD = "SELECT password FROM user WHERE user_id = ? AND password = ?";
    private static final String SQL_USER_SEARCH = "SELECT user_id, login, email, balance, ban, role_id FROM user " +
            "WHERE role_id != 2 AND (email LIKE ? OR login LIKE ?) ORDER BY login, email";


    public UserDao(ProxyConnection con) {
        this.con = con;
    }

    @Override
    public User logInUser(String mail, String password) throws DaoException {
        User user = null;
        try (PreparedStatement prSt = con.prepareStatement(SQL_USER)) {
            prSt.setString(1, mail);
            prSt.setString(2, password);
            ResultSet res = prSt.executeQuery();

            while (res.next()) {
                user = createUser(res);
            }
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
        return user;
    }

    @Override
    public void registerUser(String mail, String password) throws DaoException {
        try (PreparedStatement prSt = con.prepareStatement(SQL_ADD_USER)) {
            prSt.setString(1, mail);
            prSt.setString(2, password);
            prSt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
    }

    @Override
    public User receiveUserById(long userId) throws DaoException {
        User user = null;
        try (PreparedStatement prSt = con.prepareStatement(SQL_USER_ID)) {
            prSt.setLong(1, userId);
            ResultSet res = prSt.executeQuery();
            while (res.next()) {
                user = createUser(res);
            }
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
        return user;
    }

    @Override
    public void changeBanUserById(long userId, boolean ban) throws DaoException {
        try (PreparedStatement prSt = con.prepareStatement(SQL_CHANGE_BAN)) {
            prSt.setBoolean(1, ban);
            prSt.setLong(2, userId);
            prSt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
    }

    @Override
    public void changeUserLogin(long userId, String login) throws DaoException {
        try (PreparedStatement prSt = con.prepareStatement(SQL_LOGIN_CHANGE)) {
            changeUserProfile(userId, login, prSt);
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
    }

    @Override
    public void changeUserPassword(long userId, String password) throws DaoException {
        try (PreparedStatement prSt = con.prepareStatement(SQL_PASSWORD_CHANGE)) {
            changeUserProfile(userId, password, prSt);
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
    }

    private void changeUserProfile(long userId, String content, PreparedStatement prSt) throws SQLException {
        prSt.setString(1, content);
        prSt.setLong(2, userId);
        prSt.executeUpdate();
    }

    @Override
    public BigDecimal receiveUserBalance(long userId) throws DaoException {
        BigDecimal balance = null;
        try (PreparedStatement prSt = con.prepareStatement(SQL_BALANCE)) {
            prSt.setLong(1, userId);
            ResultSet res = prSt.executeQuery();
            while (res.next()) {
                balance = res.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
        return balance;
    }

    @Override
    public boolean checkUserPassword(long userId, String password) throws DaoException {
        try (PreparedStatement prSt = con.prepareStatement(SQL_PASSWORD)) {
            prSt.setLong(1, userId);
            prSt.setString(2, password);
            ResultSet res = prSt.executeQuery();
            while (res.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
        return false;
    }

    @Override
    public long checkUniqueUserMail(String mail) throws DaoException {
        try (PreparedStatement prSt = con.prepareStatement(SQL_MAIL)) {
            prSt.setString(1, mail);
            ResultSet res = prSt.executeQuery();
            while (res.next()) {
                return res.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
        return -1;
    }

    @Override
    public boolean checkUniqueUserLogin(String login) throws DaoException {
        try (PreparedStatement prSt = con.prepareStatement(SQL_LOGIN)) {
            prSt.setString(1, login);
            ResultSet res = prSt.executeQuery();
            while (res.next()) {
                return false;
            }
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
        return true;
    }

    @Override
    public void updateUserBalanceById(long userId, BigDecimal newBalance) throws DaoException {
        try (PreparedStatement prSt = con.prepareStatement(SQL_UPDATE_BALANCE)) {
            prSt.setBigDecimal(1, newBalance);
            prSt.setLong(2, userId);
            prSt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
    }

    @Override
    public User receivePrevMaxBetUser(long lotId) throws DaoException {
        User user = null;
        try (PreparedStatement prSt = con.prepareStatement(SQL_PREV_MAX_BET)) {
            prSt.setLong(1, lotId);
            ResultSet res = prSt.executeQuery();
            while (res.next()) {
                user = new User();
                long userId = res.getLong(1);
                BigDecimal bet = res.getBigDecimal(2);
                BigDecimal balance = res.getBigDecimal(3);
                user.setId(userId);
                user.setBalance(balance.add(bet));
            }
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
        return user;
    }

    @Override
    public List<User> searchUserByLoginMail(String search) throws DaoException {
        List<User> userList = new ArrayList<>();
        try (PreparedStatement prSt = con.prepareStatement(SQL_USER_SEARCH)) {
            prSt.setString(1, "%" + search + "%");
            prSt.setString(2, "%" + search + "%");
            ResultSet res = prSt.executeQuery();
            while (res.next()) {
                User user = createUser(res);
                userList.add(user);
            }
        } catch (SQLException e) {
            throw new DaoException("SQLException", e);
        }
        return userList;
    }

    private User createUser(ResultSet res) throws SQLException {
        User user = new User();
        user.setId(res.getLong(1));
        user.setLogin(res.getString(2));
        user.setMail(res.getString(3));
        user.setBalance(new BigDecimal(res.getString(4)));
        user.setBan(res.getBoolean(5));
        user.setRoleId(res.getLong(6));
        return user;
    }
}
