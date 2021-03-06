package by.rudkouski.auction.service;

import by.rudkouski.auction.entity.impl.Lot;
import by.rudkouski.auction.service.exception.ServiceException;

import javax.servlet.http.Part;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * This interface contains methods for data conversion and sending data to other layer.
 * Provides performance of the main application logic.
 * All methods are associated with entity "Lot"
 */

public interface ILotService<T extends Lot> {

    /**
     * Returns list of checked Lots
     *
     * @return list containing Lots
     * @throws ServiceException if DaoException or ConnectionPoolException is thrown
     */
    List<T> setupLot() throws ServiceException;

    /**
     * Returns list of checked Lots which refer to a certain category
     *
     * @param categoryId unique category id
     * @param page       number of page to determining of position to search
     * @param lotChoiceType    lot type (unfinished - not finished lots, finished - finished lots, otherwise - all lots)
     * @return list containing Lots for the specified category
     * @throws ServiceException if DaoException or ConnectionPoolException is thrown
     */
    List<T> searchLotByCategory(long categoryId, int page, String lotChoiceType) throws ServiceException;

    /**
     * Returns list of checked Lots with partial coincidence of name
     *
     * @param search the parameter to be compared for partial coincidence with lot name
     * @param page   number of page to determining of position to search
     * @param lotChoiceType    lot type (unfinished - not finished lots, finished - finished lots, otherwise - all lots)
     * @return list containing Lots with partial coincidence of name with the specified search
     * @throws ServiceException if DaoException or ConnectionPoolException is thrown
     */
    List<T> searchLotByName(String search, int page, String lotChoiceType) throws ServiceException;

    /**
     * Returns Lot
     *
     * @param lotId unique lot id
     * @return Lot with the specified id
     * @throws ServiceException if DaoException or ConnectionPoolException is thrown
     */
    T searchLotById(long lotId) throws ServiceException;

    /**
     * Returns list of lot lists with different status for a certain user
     *
     * @param userId unique user id to search in database
     * @return list containing three lists: the first list contains finished lots, the second list contains unfinished lots, the third list contains unchecked lots and the fourth - removed lots
     * @throws ServiceException if DaoException or ConnectionPoolException is thrown
     */
    List<List<T>> receiveLotHistoryByUser(long userId) throws ServiceException;

    /**
     * Returns list of finished Lots for a certain user
     *
     * @param userId unique user id
     * @return list containing finished Lots for the specified user
     * @throws ServiceException if DaoException or ConnectionPoolException is thrown
     */
    List<T> receiveFinishedLotHistoryByUser(long userId) throws ServiceException;

    /**
     * Returns list of unfinished Lots for a certain user
     *
     * @param userId unique user id
     * @return list containing unfinished Lots for the specified user
     * @throws ServiceException if DaoException or ConnectionPoolException is thrown
     */
    List<T> receiveUnfinishedLotHistoryByUser(long userId) throws ServiceException;

    /**
     * Returns list of unchecked Lots for a certain user
     *
     * @param userId unique user id
     * @return list containing unchecked Lots for the specified user
     * @throws ServiceException if DaoException or ConnectionPoolException is thrown
     */
    List<T> receiveUncheckedLotHistoryByUser(long userId) throws ServiceException;

    /**
     * Returns list of removed Lots for a certain user
     *
     * @param userId unique user id
     * @return list containing removed Lots for the specified user
     * @throws ServiceException if DaoException or ConnectionPoolException is thrown
     */
    List<T> receiveRemovedLotHistoryByUser(long userId) throws ServiceException;

    /**
     * Returns min bet for a certain lot
     *
     * @param lotId unique lot id
     * @return min bet for the specified lot
     * @throws ServiceException if DaoException or ConnectionPoolException is thrown
     */
    BigDecimal determineLotMinBet(long lotId) throws ServiceException;

    /**
     * Receives lot data, creates new object "Lot" and gets it to next layer for saving in database.
     * Also save lot photo in server
     *
     * @param paramMap HashMap of parameters for creating a new lot
     * @param part a photo for lot
     * @param appPath relative path to the application
     * @return <tt>true</tt> if the lot is successful added
     * @throws ServiceException if SQLException, DaoException or ConnectionPoolException is thrown
     */
    boolean addLot(Map<String, String[]> paramMap, Part part, String appPath) throws ServiceException;

    /**
     * Receives lot data, creates new object "Lot" and gets it to next layer for updating in database.
     * Also save a new lot photo in server
     *
     * @param paramMap HashMap of parameters for creating a new lot
     * @param part a new photo for lot
     * @param appPath relative path to the application
     * @return <tt>true</tt> if the lot is successful added
     * @throws ServiceException if SQLException, DaoException or ConnectionPoolException is thrown
     */
    boolean editLot(Map<String, String[]> paramMap, Part part, String appPath) throws ServiceException;

    /**
     * Changes mark of checked for lot by admin
     *
     * @param lotId unique lot id
     * @throws ServiceException if DaoException or ConnectionPoolException is thrown
     */
    void checkLot(long lotId) throws ServiceException;

    /**
     * Changes mark of removed for lot by admin and returns max bet to user
     *
     * @param lotId unique lot id
     * @throws ServiceException if DaoException or ConnectionPoolException is thrown
     */
    void removeLot(long lotId) throws ServiceException;
}
