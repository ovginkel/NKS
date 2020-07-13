package com.ihpukan.nks.view.screens.main.messages;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ihpukan.nks.R;
import com.ihpukan.nks.R2;
import com.ihpukan.nks.common.DialogUtils;
import com.ihpukan.nks.common.IDialogClickListener;
import com.ihpukan.nks.common.IGrantPermissionCallback;
import com.ihpukan.nks.model.Message;
import com.ihpukan.nks.view.base.AbstractBaseFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.sort.SortingTypes;


public class MessagesFragment extends AbstractBaseFragment implements MessagesContract.View, OnSendClickListener, OnRefreshClickListener, OnUploadClickListener {

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R2.id.progressBar)
    ProgressBar progressBar;

    @BindView(R2.id.backIconMain)
    ImageView backIcon;

    public MessagesContract.Presenter presenter;
    private MessagesAdapter messagesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R2.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messagesAdapter = new MessagesAdapter(this);
        recyclerView.setAdapter(messagesAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(presenter!=null)
        {
            presenter.loadAllMessages();
            messagesAdapter.clearMessages();
        }
        else
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            if(presenter!=null)
            {
                presenter.loadAllMessages();
                messagesAdapter.clearMessages();
            }

        }
    }

    @Override
    public void setPresenter(MessagesContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showErrorMessage(String message) {
        hideBackIcon();
        hideProgressBar();
        //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showBackIcon() {
        backIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void hideBackIcon() {
        backIcon.setVisibility(View.GONE);
    }

    @Override
    public void displayMessages(List<Message> mmessages) {
        hideProgressBar();
        hideBackIcon();
        //messagesAdapter.clearMessages();
        if (mmessages == null || mmessages.isEmpty()) {
            //Toast.makeText(getActivity(), R.string.not_found_messages, Toast.LENGTH_SHORT).show();
            messagesAdapter.addMessages(new ArrayList<>(),presenter.getMembersWrapper());
        } else {
            messagesAdapter.addMessages(mmessages,presenter.getMembersWrapper());
        }
    }

    /*@Override
    public void updateViews() {
        messagesAdapter.notifyDataSetChanged();
    }*/

    @Override
    public void onRefreshMessages() {
        presenter.updateWithNewMessages();
    }

    @Override
    public void onSendClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(getString(R.string.message));

// Set up the input
        final EditText input = new EditText(this.getContext());
        input.setMinHeight(200);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //m_Text = input.getText().toString();
                presenter.sendMessage(getActivity(), input.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void onFileClick() {



        final Activity activity = this.getActivity();
        checkStoragePermission(activity, new IGrantPermissionCallback() {
            @Override
            public void granted() {
                final String [] zipTypes = {".zip",".rar",".7z",".tar",".tar.bz2",".gz","tar.gz"};
                //final String [] imgTypes = {".png",".jpg",".jpeg",".bmp"};
                final String [] appTypes = {".exe",".apk",".dmg",".deb",".rpm"};
                final String [] codeTypes = {".php",".c",".cpp",".h",".hpp",".cs", ".py", ".tex" };
                DialogUtils.displayChooseOkNoDialog(activity, R.string.allow_reading_external,
                        R.string.file_selection_for_upload, new IDialogClickListener() {
                            @Override
                            public void onOK() {
                                if(activity!=null) {
                                    FilePickerBuilder.Companion.getInstance().setMaxCount(1)
                                            //.setSelectedFiles(filePaths)
                                            .sortDocumentsBy(SortingTypes.name)
                                            .enableDocSupport(true)
                                            .setActivityTheme(R2.style.AppTheme)
                                            .addFileSupport("ZIP", zipTypes, R.drawable.icon_file_unknown)
                                            //.addFileSupport("IMG",imgTypes, R.drawable.ic_camera)
                                            .addFileSupport("APP", appTypes, R.drawable.icon_file_unknown)
                                            .addFileSupport("CDE", codeTypes, R.drawable.icon_file_unknown)
                                            .showFolderView(true)
                                            .pickFile(activity, FilePickerConst.REQUEST_CODE_DOC);
                                }
                            }
                        });
            }

            @Override
            public void denied() {

            }
        });
    }

    public void onPhotoClick() {

        final Activity activity = this.getActivity();
        checkStoragePermission(activity, new IGrantPermissionCallback() {
            @Override
            public void granted() {
                DialogUtils.displayChooseOkNoDialog(activity, R.string.allow_reading_external,
                        R.string.image_selection_for_upload, new IDialogClickListener() {
                            @Override
                            public void onOK() {
                                if(activity!=null) {
                                    FilePickerBuilder.Companion.getInstance().setMaxCount(1)
                                            //.setSelectedFiles(photoPaths)
                                            .setActivityTheme(R2.style.AppTheme)
                                            .showGifs(false)
                                            .pickPhoto(activity, FilePickerConst.REQUEST_CODE_PHOTO); //Disable gif to prevent bad gif failure.
                                }
                            }
                        });
            }

            @Override
            public void denied() {

            }
        });

    }

    private void checkStoragePermission(Activity activity, final IGrantPermissionCallback callback) {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (callback == null) return;
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            callback.denied();
                        } else {
                            callback.granted();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

}
