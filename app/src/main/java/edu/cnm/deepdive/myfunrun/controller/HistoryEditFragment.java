package edu.cnm.deepdive.myfunrun.controller;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
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
import edu.cnm.deepdive.myfunrun.viewmodel.HistoryViewModel;
import java.util.Date;
import java.util.List;


public class HistoryEditFragment extends DialogFragment {

  private static final String ID_KEY = "id";

  private long historyId;
  private History history;
  private View root;
  private AlertDialog dialog;
  private HistoryViewModel historyViewModel;
  private Spinner spinner;
  private DatePicker datePicker;
  private TimePicker timePickerStart;
  private TimePicker timePickerEnd;
  private EditText distanceText;
  private EditText paceText;
  private Date start;
  private Date end;
  private Race race;
  private List<Race> races;


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
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    super.onCreateDialog(savedInstanceState);
    root = LayoutInflater.from(getContext()).inflate(R.layout.fragment_history_edit, null, false);
    datePicker = root.findViewById(R.id.date);
    timePickerStart = root.findViewById(R.id.start);
    timePickerEnd = root.findViewById(R.id.end);
    distanceText = root.findViewById(R.id.distance);
    paceText = root.findViewById(R.id.pace);
    spinner = root.findViewById(R.id.race_spinner);
    dialog = new AlertDialog.Builder(getContext())
        .setTitle("Run History")
        .setView(root)
        .setNegativeButton(android.R.string.cancel, null)
        .setPositiveButton(android.R.string.ok, (dlg, wh) -> save())
        .create();

    dialog.setOnShowListener((dlg) -> checkSubmitCondition());
    return dialog;
  }

  private void checkSubmitCondition() {
    Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
    positive.setEnabled(
        !distanceText.getText().toString().trim().isEmpty() && !paceText.getText().toString().trim()
            .isEmpty() && datePicker.isEnabled() && timePickerStart.isEnabled() && timePickerEnd.isEnabled());

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
          distanceText.setText(history.getDistance());
        }
      });
      historyViewModel.setHistoryId(historyId);
    }
  }

  private void save() {
    history.setDistance(Integer.parseInt(distanceText.getText().toString().trim()));
    historyViewModel.save(history);

  }

}
