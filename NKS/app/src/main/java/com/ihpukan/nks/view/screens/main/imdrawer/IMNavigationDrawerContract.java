package com.ihpukan.nks.view.screens.main.imdrawer;

import com.ihpukan.nks.model.IM;
import com.ihpukan.nks.model.User;
import com.ihpukan.nks.view.base.AbstractBaseView;

import java.util.List;

/**
 * Created by User on 20.04.2017.
 */

public interface IMNavigationDrawerContract {

    interface View extends AbstractBaseView<IMNavigationDrawerContract.Presenter> {

        void showErrorMessage(String message);

        void loadIMsComplete(List<IM> ims);

        void loadProfileComplete(User user);

    }

    interface Presenter {

        void loadIMs();

        void loadAllUsers();

        void loadProfile();

    }

}
