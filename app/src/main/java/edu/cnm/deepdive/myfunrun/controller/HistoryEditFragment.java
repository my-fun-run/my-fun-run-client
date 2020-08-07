package edu.cnm.deepdive.myfunrun.controller;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.common.internal.Objects;
import edu.cnm.deepdive.myfunrun.controller.DateTimePickerFragment.Mode;
import edu.cnm.deepdive.myfunrun.controller.DateTimePickerFragment.OnChangeListener;
import edu.cnm.deepdive.myfunrun.model.entity.History;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.cnm.deepdive.myfunrun.R;
import edu.cnm.deepdive.myfunrun.model.entity.Race;
import edu.cnm.deepdive.myfunrun.model.pojo.HistoryWithDetails;
import edu.cnm.deepdive.myfunrun.viewmodel.HistoryViewModel;
import edu.cnm.deepdive.myfunrun.viewmodel.RaceViewModel;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class HistoryEditFragment extends DialogFragment implements TextWatcher, OnChangeListener {

  private static final String ID_KEY = "id";

  private long historyId;
  private HistoryWithDetails history;
  private View root;
  private AlertDialog dialog;
  private HistoryViewModel historyViewModel;
  private RaceViewModel raceViewModel;
  private Spinner raceSpinner;
  private EditText distance;
  private EditText pace;
  private EditText date;
  private EditText start;
  private EditText end;
  private Race race;
  private List<Race> races;
  private Calendar startCalendar;
  private Calendar endCalendar;
  private NumberFormat numberFormat;
  private DateFormat dateFormat;
  private DateFormat timeFormat;
  private List<Race> spinnerRaces;
  private EditText currentPickerField;


  public static HistoryEditFragment newInstance(long historyId) {
    HistoryEditFragment fragment = new HistoryEditFragment();
    Bundle args = new Bundle();
    args.putLong(ID_KEY, historyId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    historyId = getArguments().getLong(ID_KEY, 0);
    history = new HistoryWithDetails();
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    super.onCreateDialog(savedInstanceState);
    numberFormat = NumberFormat.getNumberInstance();
    numberFormat.setMaximumFractionDigits(3);
    dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
    timeFormat = android.text.format.DateFormat.getTimeFormat(getContext());
    root = LayoutInflater.from(getContext()).inflate(R.layout.fragment_history_edit, null, false);
    distance = root.findViewById(R.id.distance);
    distance.setText("");
    pace = root.findViewById(R.id.pace);
    pace.setText("");
    setupSpinner(new LinkedList<>());
    date = root.findViewById(R.id.date);
    date.setOnClickListener((v) -> openDateTimePicker(date, Mode.DATE));
    start = root.findViewById(R.id.start);
    start.setOnClickListener((v) -> openDateTimePicker(start, Mode.TIME));
    end = root.findViewById(R.id.end);
    end.setOnClickListener((v) -> openDateTimePicker(end, Mode.TIME));
    startCalendar = Calendar.getInstance();
    endCalendar = Calendar.getInstance();
    upDateTimeFields();
    date.setTag(startCalendar);
    start.setTag(startCalendar);
    end.setTag(endCalendar);
    dialog = new AlertDialog.Builder(getContext())
        .setTitle("Run History")
        .setView(root)
        .setNegativeButton(android.R.string.cancel, null)
        .setPositiveButton(android.R.string.ok, (dlg, wh) -> save())
        .create();

    dialog.setOnShowListener((dlg) -> {
      distance.addTextChangedListener(this);
      checkSubmitCondition();
    });
    return dialog;
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return root;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    //TODO connect to the view model
    historyViewModel = new ViewModelProvider(getActivity()).get(HistoryViewModel.class);
    if (historyId != 0) {
      historyViewModel.getHistory().observe(getViewLifecycleOwner(), (history) -> {
        if (history != null) {
          this.history = history;
          distance.setText(numberFormat.format(history.getDistance()));
          startCalendar.setTime(history.getStart());
          endCalendar.setTime(history.getEnd());
          upDateTimeFields();
          updateSpinner();
        }
      });
      historyViewModel.setHistoryId(historyId);
    }
    raceViewModel = new ViewModelProvider(getActivity()).get(RaceViewModel.class);
    raceViewModel.getRaces().observe(getViewLifecycleOwner(), (races) -> {
      setupSpinner(races);
      updateSpinner();
    });
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {
    updatePace();
    checkSubmitCondition();
  }

  @Override
  public void onChange(Calendar calendar) {
    Calendar c = (Calendar) currentPickerField.getTag();
    c.setTime(calendar.getTime());
    endCalendar.set(Calendar.YEAR, startCalendar.get(Calendar.YEAR));
    endCalendar.set(Calendar.MONTH, startCalendar.get(Calendar.MONTH));
    endCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.get(Calendar.DAY_OF_MONTH));
    if (endCalendar.compareTo(startCalendar) < 0) {
      endCalendar.setTime(startCalendar.getTime());
    }
    updatePace();
    upDateTimeFields();
  }

  private void updatePace() {
    long elapsed = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
    if (elapsed > 0 ) {
      String distanceInput = this.distance.getText().toString().trim();
      if (distanceInput.isEmpty()) {
        this.pace.setText("");
      } else {
        try {
          int distance = (int) (1000 * numberFormat.parse(distanceInput).doubleValue());
          this.pace.setText(getString(R.string.pace_format, distance * 3600.0 / elapsed));
        } catch (ParseException e) {
          this.pace.setText("");
        }
      }
    } else {
      this.pace.setText("");
    }
  }

  private void updateSpinner() {
    if (history.getRaceId() != null) {
      for (int i = 0; i <= spinnerRaces.size(); i++) {
        if (Objects.equal(race.getId(), history.getRaceId())) {
          raceSpinner.setSelection(i);
        }
      }
    }
  }

  private void save() {
    try {
      history.setDistance(
          (int) Math.round(1000 * numberFormat.parse(distance.getText().toString().trim()).doubleValue()));

    } catch (ParseException e) {
      // do nothing
    }
    history.setStart(startCalendar.getTime());
    history.setEnd(endCalendar.getTime());
    Race race = (Race) raceSpinner.getSelectedItem();
    if (race.getId() == 0) {
      history.setRaceId(null);
      history.setRace(null);
    } else {
      history.setRaceId(race.getId());
      history.setRace(race);
    }
    historyViewModel.save(history);

  }

  private void openDateTimePicker(EditText field, Mode mode) {
    DateTimePickerFragment fragment = DateTimePickerFragment.createInstance(mode,
        (Calendar) field.getTag());
    currentPickerField = field;
    fragment.show(getChildFragmentManager(), fragment.getClass().getName());
  }

  private void setupSpinner(List<Race> races) {
    raceSpinner = root.findViewById(R.id.race_spinner);
    spinnerRaces = new ArrayList<>(races);
    Race race = new Race();
    race.setName("(none)");
    spinnerRaces.add(0, race);
    ArrayAdapter<Race> adapter = new ArrayAdapter<>(getContext(),
        android.R.layout.simple_spinner_item, spinnerRaces);
    raceSpinner.setAdapter(adapter);
  }

  private void upDateTimeFields() {
    date.setText(dateFormat.format(startCalendar.getTime()));
    start.setText(timeFormat.format(startCalendar.getTime()));
    end.setText(timeFormat.format(endCalendar.getTime()));
  }

  private void checkSubmitCondition() {
    Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
    String distanceInput = distance.getText().toString().trim();
    if (!distanceInput.isEmpty()) {
      try {
        double d = numberFormat.parse(distanceInput).doubleValue();
       // distance.setText(numberFormat.format(d));
      } catch (ParseException e) {
        distanceInput = "";
        distance.setText(distanceInput);
      }
    }
    positive.setEnabled(!distanceInput.isEmpty());
  }

}
