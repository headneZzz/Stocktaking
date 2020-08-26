package ru.gosarcho.stocktaking.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.fragment.app.DialogFragment;

import ru.gosarcho.stocktaking.R;

public class NewItemDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_new_item, null))
                .setPositiveButton(R.string.add, (dialog, id) -> {
                    //TODO
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> NewItemDialogFragment.this.getDialog().cancel());
        return builder.create();
    }
}
