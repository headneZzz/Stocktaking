package ru.gosarhro.stocktaking.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import ru.gosarhro.stocktaking.R;

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
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel, (dialog, id) -> NewItemDialogFragment.this.getDialog().cancel());
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog)getDialog();

        if(d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                listener.onDialogPositiveClick(NewItemDialogFragment.this);
            });
        }
    }
}
