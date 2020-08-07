package edu.cnm.deepdive.myfunrun.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import edu.cnm.deepdive.myfunrun.model.entity.History;
import edu.cnm.deepdive.myfunrun.model.entity.History;
import edu.cnm.deepdive.myfunrun.model.entity.User;
import edu.cnm.deepdive.myfunrun.model.pojo.UserWithHistory;
import io.reactivex.Single;
import java.util.Collection;
import java.util.List;

/**
 * This class contains static methods, with convenience annotations,
 *  *  * which update the user info posted by users. User may be posted, deleted, and retrieved.
 */
@Dao
public interface UserDao {

  /**
   * Insert single.
   *
   * @param user the user
   * @return the single
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  Single<Long> insert(User user);

  /**
   * Insert single.
   *
   * @param users the users
   * @return the single
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  Single<List<Long>> insert(User... users);

  /**
   * Insert single.
   *
   * @param users the users
   * @return the single
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  Single<List<Long>> insert(Collection<User> users);

  /**
   * Update single.
   *
   * @param users the users
   * @return the single
   */
  @Update
  Single<Integer> update(User... users);

  /**
   * Delete single.
   *
   * @param users the users
   * @return the single
   */
  @Delete
  Single<Integer> delete(User... users);

  /**
   * Select all live data.
   *
   * @return the live data
   */
  @Transaction
 @Query("SELECT * FROM User ORDER BY display_name")
  LiveData<List<User>> selectAll();

  /**
   * Select by id single.
   *
   * @param userId the user id
   * @return the single
   */
  @Transaction
  @Query("SELECT * FROM User WHERE user_id = :userId")
  Single<UserWithHistory> selectById(Long userId);

}
