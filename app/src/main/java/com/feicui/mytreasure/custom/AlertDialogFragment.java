package com.feicui.mytreasure.custom;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.feicui.mytreasure.R;

/**
 * 自定义弹框
 */

public class AlertDialogFragment extends DialogFragment {

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_MESSAGE = "key_message";

    public static AlertDialogFragment getInstances(String title, String message) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE,title);
        bundle.putString(KEY_MESSAGE, message);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(KEY_TITLE);
        String message = getArguments().getString(KEY_MESSAGE);


        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

}
