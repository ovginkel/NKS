package com.ihpukan.nks.view.screens.main.users;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ihpukan.nks.R;
import com.ihpukan.nks.model.User;
import com.ihpukan.nks.view.base.AbstractBaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersFragment extends AbstractBaseFragment implements UsersContract.View, OnOpenIMClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.backIconMain)
    ImageView backIcon;



    private UsersContract.Presenter presenter;
    public UsersAdapter usersAdapter;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersAdapter = new UsersAdapter(this);
        recyclerView.setAdapter(usersAdapter);
        progressDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage(getString(R.string.progress_dialog_wait));
        progressDialog.setTitle("");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.hide();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //usersAdapter.clearUsers();
        presenter.loadAllUsers();

    }

    @Override
    public void setPresenter(UsersContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showErrorMessage(String message) {
        hideProgressBar();
        //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideBackIcon() {
        backIcon.setVisibility(View.GONE);
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
    public void displayUsers(List<User> musers) {
        hideProgressBar();
        if (musers == null || musers.isEmpty()) {
            //Toast.makeText(getActivity(), R.string.not_found_users, Toast.LENGTH_SHORT).show();
            usersAdapter.addUsers(new ArrayList<User>());
            updateViews();
        } else {
            if((musers!=null)?(!musers.isEmpty()):false)
            {
                usersAdapter.addUsers(musers);
                updateViews();
            }
        }
    }

    @Override
    public void updateViews() {
        usersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOpenIMClick(final User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(getString(R.string.activate_messaging_with)+" "+user.profile.real_name+"?");

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.openIM(getActivity(), user.id);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
