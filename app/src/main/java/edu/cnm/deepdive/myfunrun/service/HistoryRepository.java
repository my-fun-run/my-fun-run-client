package edu.cnm.deepdive.myfunrun.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.myfunrun.model.dao.HistoryDao;
import edu.cnm.deepdive.myfunrun.model.dao.RaceDao;
import edu.cnm.deepdive.myfunrun.model.dao.UserDao;
import edu.cnm.deepdive.myfunrun.model.entity.History;
import edu.cnm.deepdive.myfunrun.model.entity.Race;
import edu.cnm.deepdive.myfunrun.model.pojo.HistoryWithDetails;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type History repository.
 */
public class HistoryRepository {

  private static final int NETWORK_POOL_SIZE = 4;
  private static final String AUTH_HEADER_FORMAT = "Bearer %s";

  private final Context context;
  private final MyFunRunDatabase database;
  private final HistoryDao historyDao;
  private final UserDao userDao;
  private final RaceDao raceDao;
  //  private final History history;
  private final BackendService backendService;
  private final ExecutorService networkPool;


  /**
   * Instantiates a new History repository.
   *
   * @param context the context
   */
  public HistoryRepository(Context context) {
    this.context = context;
    database = MyFunRunDatabase.getInstance();
    userDao = database.getUserDao();
    historyDao = database.getHistoryDao();
    raceDao = database.getRaceDao();
    backendService = BackendService.getInstance();
    networkPool = Executors.newFixedThreadPool(NETWORK_POOL_SIZE);
  }

  /**
   * Gets all.
   *
   * @return the all
   */
  public LiveData<List<HistoryWithDetails>> getAll() {
    return historyDao.selectAll();

  }

  /**
   * Get single.
   *
   * @param id the id
   * @return the single
   */
  public Single<HistoryWithDetails> get(long id) {
    return historyDao.selectById(id);
//        .subscribeOn(Schedulers.io());
  }

  public Completable refresh(String idToken) {
    return backendService.getAllHistories(idToken)
        .subscribeOn(Schedulers.from(networkPool))
        .flatMap((histories) -> {
          histories.forEach((h) -> {
            if (h.getRace() != null) {
              h.setRaceId(h.getRace().getId());
            }
          });
         return historyDao.insert(histories);
        })
        .subscribeOn(Schedulers.io())
        .flatMapCompletable((ids) -> Completable.complete());
  }

  /**
   * Save completable.
   *
   * @param history the history
   * @return the completable
   */

  public Completable save(String idToken, HistoryWithDetails history) {
    Single<?> localTask =
        (history.getId() == 0) ? historyDao.insert(history) : historyDao.update(history);
    Single<HistoryWithDetails> remoteTask = (history.getId() == 0)
        ? backendService.postHistory(getHeader(idToken), history)
        .map((r) -> {
          history.setId(r.getId());
          return history;
        })
        : backendService.putHistory(getHeader(idToken), history, history.getId());
    return remoteTask
        .subscribeOn(Schedulers.from(networkPool))
        .flatMap((s) -> localTask)
        .subscribeOn(Schedulers.io())
        .flatMapCompletable((ignore) -> Completable.complete());
  }

  /**
   * Delete completable.
   *
   * @param history the history
   * @return the completable
   */
  public Completable delete(History history) {
    if (history.getId() == 0) {
      return Completable.fromAction(() -> {
      })
          .subscribeOn(Schedulers.io());
    } else {
      return Completable.fromSingle(historyDao.delete(history))
          .subscribeOn(Schedulers.io());
    }
  }

  //    public Completable delete (String idToken, History history){
//      Single<?> localTask = (history.getId() == 0) ? Single.just(null) : historyDao.delete(history);
//      Completable remoteTask = (history.getId() == 0)
//          ? Completable.complete()
//          : backendService.deleteRace(getHeader(idToken), history.getId());
//      return remoteTask
//          .subscribeOn(Schedulers.from(networkPool))
//          .andThen(localTask)
//          .subscribeOn(Schedulers.io())
//          .flatMapCompletable((ignore) -> Completable.complete());
//    }
//
//    private String getHeader (String idToken){
//      return String.format(AUTH_HEADER_FORMAT, idToken);
//    }
//
//
//  }
  private String getHeader(String idToken) {
    return String.format(AUTH_HEADER_FORMAT, idToken);
  }
}
