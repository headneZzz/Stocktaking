package ru.gosarcho.stocktaking.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.fragment.app.DialogFragment;

import ru.gosarcho.stocktaking.R;

public class NewItemDialogFragment extends DialogFragment {
    public interface NewItemDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    NewItemDialogListener listener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            listener = (NewItemDialogListener) getActivity();
        } catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_new_item_title);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_new_item, null))
                .setPositiveButton(R.string.add, (dialog, id) -> {
                    listener.onDialogPositiveClick(NewItemDialogFragment.this);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> NewItemDialogFragment.this.getDialog().cancel());
        return builder.create();
    }
}
