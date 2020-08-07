package edu.cnm.deepdive.myfunrun.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;
import androidx.room.Update;
import edu.cnm.deepdive.myfunrun.model.entity.History;
import edu.cnm.deepdive.myfunrun.model.pojo.HistoryWithDetails;
import io.reactivex.Single;
import java.util.Collection;
import java.util.List;

/**
 * This class contains static methods, with convenience annotations,
 * which allow Histories may be posted, deleted, and retrieved.
 */
@Dao
public interface HistoryDao {

  /**
   * Insert single.
   *
   * @param history the history
   * @return the single
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  Single<Long> insert(History history);

  /**
   * Insert single.
   *
   * @param histories the histories
   * @return the single
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  Single<List<Long>> insert(History... histories);

  /**
   * Insert single.
   *
   * @param histories the histories
   * @return the single
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  Single<List<Long>> insert(Collection<? extends History> histories);

  /**
   * Update single.
   *
   * @param histories the histories
   * @return the single
   */
  @Update
  Single<Integer> update(History... histories);

  /**
   * Delete single.
   *
   * @param histories the histories
   * @return the single
   */
  @Delete
  Single<Integer> delete(History... histories);

  /**
   * Select all live data.
   *
   * @return the live data
   */
  @Transaction
  @Query("SELECT * FROM History ORDER BY distance")
  LiveData<List<HistoryWithDetails>> selectAll();

 // @Transaction
 // @Query("SELECT * FROM History ORDER BY ")
 // LiveData<List<History>> selectById;

  /**
   * Select by id single.
   *
   * @param historyId the history id
   * @return the single
   */
  @Transaction
  @Query("SELECT * FROM History WHERE History_id = :historyId")
  Single<HistoryWithDetails> selectById(long historyId);



}
