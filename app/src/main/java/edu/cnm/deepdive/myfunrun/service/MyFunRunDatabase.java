package edu.cnm.deepdive.myfunrun.service;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import edu.cnm.deepdive.myfunrun.model.dao.CommentDao;
import edu.cnm.deepdive.myfunrun.model.dao.HistoryDao;
import edu.cnm.deepdive.myfunrun.model.dao.UserDao;
import edu.cnm.deepdive.myfunrun.model.dao.RaceDao;
import edu.cnm.deepdive.myfunrun.model.entity.Comment;
import edu.cnm.deepdive.myfunrun.model.entity.History;
import edu.cnm.deepdive.myfunrun.model.entity.Race;
import edu.cnm.deepdive.myfunrun.model.entity.User;
import edu.cnm.deepdive.myfunrun.service.MyFunRunDatabase.Converters;
import io.reactivex.schedulers.Schedulers;
import java.util.Date;

/**
 * The type My fun run database.
 */
@Database(
    entities = {Race.class, User.class, History.class, Comment.class},
    version = 2,
    exportSchema = true
)
@TypeConverters({Converters.class})
public abstract class MyFunRunDatabase extends RoomDatabase {

  private static final String DB_NAME = "MyFunRuns_db";

  private static Application context;

  /**
   * Sets context.
   *
   * @param context the context
   */
  public static void setContext(Application context) {
    MyFunRunDatabase.context = context;
  }

  /**
   * Gets user dao.
   *
   * @return the user dao
   */
  public abstract UserDao getUserDao();

  /**
   * Gets race dao.
   *
   * @return the race dao
   */
  public abstract RaceDao getRaceDao();

  /**
   * Gets history dao.
   *
   * @return the history dao
   */
  public abstract HistoryDao getHistoryDao();

  public abstract CommentDao getCommentDao();

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static MyFunRunDatabase getInstance() {
    return InstanceHolder.INSTANCE;
  }

  private static class InstanceHolder {

    private static final MyFunRunDatabase INSTANCE =
        Room.databaseBuilder(context, MyFunRunDatabase.class, DB_NAME)
        //    .addCallback(new Callback(context))
            .build();

  }

  private static class Callback extends RoomDatabase.Callback {

    private final Context context;

    private Callback(Context context) {
      this.context = context;
    }

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
      super.onCreate(db);
      MyFunRunDatabase database = MyFunRunDatabase.getInstance();
      RaceDao raceDao = database.getRaceDao();
      Race race1 = new Race();
      race1.setName("Spartan");
      Race race2 = new Race();
      race2.setName("Zombie");
      raceDao.insert(race1, race2)
          .subscribeOn(Schedulers.io())
          .subscribe();
    }
  }

  /**
   * The type Converters.
   */
  public static class Converters {

    /**
     * Date to long long.
     *
     * @param value the value
     * @return the long
     */
    @TypeConverter
    public static Long dateToLong(Date value) {
      return (value != null) ? value.getTime() : null;
    }

    /**
     * Long to date date.
     *
     * @param value the value
     * @return the date
     */
    @TypeConverter
    public static Date longToDate(Long value) {
      return (value != null) ? new Date(value) : null;
    }
  }


}

