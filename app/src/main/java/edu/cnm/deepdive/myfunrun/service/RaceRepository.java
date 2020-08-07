package edu.cnm.deepdive.myfunrun.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import edu.cnm.deepdive.myfunrun.model.dao.RaceDao;
import edu.cnm.deepdive.myfunrun.model.entity.Race;
import edu.cnm.deepdive.myfunrun.model.pojo.RaceWithHistory;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Race repository.
 */
public class RaceRepository {

  private static final int NETWORK_POOL_SIZE = 4;
  private static final String AUTH_HEADER_FORMAT = "Bearer %s";

  private final Context context;
  private final MyFunRunDatabase database;
  private final RaceDao raceDao;
  private final BackendService backendService;
  private final ExecutorService networkPool;

  /**
   * Instantiates a new Race repository.
   *
   * @param context the context
   */
  public RaceRepository(Context context) {
    this.context = context;
    database = MyFunRunDatabase.getInstance();
    raceDao = database.getRaceDao();
    backendService = BackendService.getInstance();
    networkPool = Executors.newFixedThreadPool(NETWORK_POOL_SIZE);
  }

  /**
   * Gets all.
   *
   * @return the all
   */
  public LiveData<List<Race>> getAll() {

    return raceDao.selectAll();
  }

  public Completable refresh(String idToken) {
    return backendService.getRaces(getHeader(idToken))
        .subscribeOn(Schedulers.from(networkPool))
        .flatMap((races) -> raceDao.insert(races))
        .subscribeOn(Schedulers.io())
        .flatMapCompletable((ids) -> Completable.complete());
  }
  /**
   * Get single.
   *
   * @param id the id
   * @return the single
   */
  public Single<RaceWithHistory> get(long id) {
    return raceDao.selectById(id)
        .subscribeOn(Schedulers.io());
  }

  /**
   * Save completable.
   *
   * @param race the race
   * @return the completable
   */
  public Completable save(String idToken, Race race) {
    Single<?> localTask = (race.getId() == 0) ? raceDao.insert(race) : raceDao.update(race);
    Single<Race> remoteTask = (race.getId() == 0)
        ? backendService.postRace(getHeader(idToken), race)
            .map((r) -> {
              race.setId(r.getId());
              return race;
            })
        : backendService.updateRace(getHeader(idToken), race.getId(), race);
    return remoteTask
        .subscribeOn(Schedulers.from(networkPool))
        .flatMap((s) -> localTask)
        .subscribeOn(Schedulers.io())
        .flatMapCompletable((ignore)-> Completable.complete());
  }

  /**
   * Delete completable.
   *
   * @param race the race
   * @return the completable
   */
  public Completable delete(String idToken, Race race) {
    Single<?> localTask = (race.getId() == 0) ? Single.just(null) : raceDao.delete(race);
    Completable remoteTask = (race.getId() == 0)
        ? Completable.complete()
        : backendService.deleteRace(getHeader(idToken), race.getId());
    return remoteTask
        .subscribeOn(Schedulers.from(networkPool))
        .andThen(localTask)
        .subscribeOn(Schedulers.io())
        .flatMapCompletable((ignore)-> Completable.complete());
  }

  private String getHeader(String idToken) {
    return String.format(AUTH_HEADER_FORMAT, idToken);
  }
}
