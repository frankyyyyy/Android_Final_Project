package com.example.frank.final_project.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.R;

/**
 * Show dialog with confirm and cancel button
 * Created by Frank on 2018/5/22.
 */

public class ConfirmDialogFragment extends DialogFragment {

    private DialogInterface.OnClickListener positiveCallback;

    private DialogInterface.OnClickListener negativeCallback;

    private String title;

    private String message;

    /**
     * Show dialog
     * @param title
     * @param message
     * @param positiveCallback
     * @param negativeCallback
     * @param fragmentManager
     */
    public void show(String title, String message, DialogInterface.OnClickListener positiveCallback,
                     DialogInterface.OnClickListener negativeCallback, FragmentManager fragmentManager) {
        this.title = title;
        this.message = message;
        this.positiveCallback = positiveCallback;
        this.negativeCallback = negativeCallback;
        show(fragmentManager, Constant.MESSAGE_NOTIFIER_SERVICE_TAG);
    }

    /**
     *  Create dialog
     * @param savedInstanceState
     * @return created dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.exit_confirm), positiveCallback);
        builder.setNegativeButton(getString(R.string.exit_cancel), negativeCallback);
        return builder.create();
    }

}
