package edu.cnm.deepdive.myfunrun.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import edu.cnm.deepdive.myfunrun.model.entity.History;
import edu.cnm.deepdive.myfunrun.model.entity.Race;
import edu.cnm.deepdive.myfunrun.model.pojo.HistoryWithDetails;
import edu.cnm.deepdive.myfunrun.service.GoogleSignInService;
import edu.cnm.deepdive.myfunrun.service.HistoryRepository;
import edu.cnm.deepdive.myfunrun.viewmodel.RaceViewModel.AuthenticatedTask;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import java.util.List;

/**
 * The type History view model.
 */
public class HistoryViewModel extends AndroidViewModel implements LifecycleObserver {

  private final HistoryRepository historyRepository;
  private final MutableLiveData<HistoryWithDetails> history;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;
  private final GoogleSignInService signInService;

  /**
   * Instantiates a new History view model.
   *
   * @param application the application
   */
  public HistoryViewModel(@NonNull Application application) {
    super(application);
    historyRepository = new HistoryRepository(application);
    history = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    signInService = GoogleSignInService.getInstance();
    refreshHistory();
  }

  /**
   * Gets history.
   *
   * @return the history
   */
  public LiveData<HistoryWithDetails> getHistory() {
    return history;
  }

  /**
   * Gets throwable.
   *
   * @return the throwable
   */
  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  /**
   * Gets histories.
   *
   * @return the histories
   */
  public LiveData<List<HistoryWithDetails>> getHistories() {
    return historyRepository.getAll();
  }

  /**
   * Sets history id.
   *
   * @param id the id
   */
  public void setHistoryId(long id) {
    throwable.setValue(null);
    pending.add(
        historyRepository.get(id)
            .subscribe(
                (history) -> this.history.postValue(history),
                (throwable) -> this.throwable.postValue(throwable)
            )
    );
  }

  public void save(HistoryWithDetails history) {
    refreshAndExecute((account) ->
        historyRepository.save(account.getIdToken(), history)
            .subscribe(
                () -> {
                },
                (throwable) -> this.throwable.postValue(throwable)
            )
    );
  }


//  public void delete(H) {
//    refreshAndExecute((account) ->
//        raceRepository.delete(account.getIdToken(), race)
//            .subscribe(
//                () -> {
//                },
//                (throwable) -> this.throwable.postValue(throwable)
//            )
//    );
//  }

  private void refreshHistory() {
    refreshAndExecute((account) ->
        historyRepository.refresh(account.getIdToken())
            .subscribe(
                () -> {
                },
                (throwable) -> this.throwable.postValue(throwable)
            )
    );
  }


  @OnLifecycleEvent(Event.ON_STOP)
  private void clearPending() {
    pending.clear();
  }


  private void refreshAndExecute(RaceViewModel.AuthenticatedTask task) {
    throwable.setValue(null);
    signInService.refresh()
        .addOnSuccessListener((account) -> pending.add(task.execute(account)))
        .addOnFailureListener(throwable::postValue);
  }

  public interface AuthenticatedTask {

    Disposable execute(GoogleSignInAccount account);

  }
}