package com.ihpukan.nks.view.screens.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ihpukan.nks.R;
import com.ihpukan.nks.common.Constants;
import com.ihpukan.nks.common.JavaScriptTokenIntercept;
import com.ihpukan.nks.view.base.AbstractBaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

//import android.support.v7.app.AlertDialog;

public class LoginFragment extends AbstractBaseFragment implements LoginContract.View {

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.backIcon)
    ImageView backIcon;


    private JavaScriptTokenIntercept  myJSTI;

    private String teamName;

    private LoginContract.Presenter loginPresenter;

    public LoginFragment() {

    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new CustomWebClient());
        myJSTI = new JavaScriptTokenIntercept();
        webView.addJavascriptInterface(myJSTI, "INTERFACE");
        backIcon.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(loginPresenter!=null) {
            if (loginPresenter.needLogin()) {
                //First ask for team name then grab token after online login
                getTeam();
            } else {
                successLogin();
            }
        }
    }

    @Override
    public void getTeam() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(R.string.team);

        // Set up the input
        final EditText input = new EditText(this.getContext());
        input.setHint(R.string.team_name);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES );
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(R.string.do_continue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                teamName = input.getText().toString();
                loginPresenter.loginTokenGrab(teamName);
            }
        });
        builder.setNegativeButton(R.string.cancel_login, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ((LoginActivity) getActivity()).gotoMainActivity();
            }
        });

        builder.show();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void successLogin() {
        hideProgress();
        webView.setVisibility(View.GONE);
        backIcon.setVisibility(View.VISIBLE);
        ((LoginActivity) getActivity()).gotoMainActivity();
    }

    @Override
    public void showErrorMessage(String message) {
        hideProgress();
        //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessageInWebView(String html) {
        hideProgress();
        backIcon.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.loadData(html, "text/html", "utf-8");
    }

    @Override
    public void loadUrlToWebView(String url) {
        //Start clean
        webView.clearCache(true);
        webView.clearHistory();
        this.clearCookies(getContext());
        webView.loadUrl(url);
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        loginPresenter = presenter;
    }


    @SuppressWarnings("deprecation")
    public void clearCookies(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //Log.d(C.TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            //Log.d(C.TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }


    private final class CustomWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showProgress();
            if(url.contains("api.test"))
            {
                if(!myJSTI.token.equals(""))
                {
                    loginPresenter.doGrabbedToken(myJSTI.token);
                }

            }

            if(url.contains("/app")) { //No bloody app nonsense
                backIcon.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                return;
            }
            if(url.contains("token="))
            {
                loginPresenter.doGrabLogin(url);
            }
        }



        @Override
        public void onPageFinished(WebView view, String url) {
            if(url.contains("/home")||url.contains("/messages")||url.contains("/app"))
            {
                if(!myJSTI.token.equals(""))
                {
                    loginPresenter.doGrabbedToken(myJSTI.token);
                    return;
                }
                else {
                    view.loadUrl("javascript:window.INTERFACE.processContent(boot_data.api_token);"); //New token store
                    view.loadUrl("javascript:window.INTERFACE.processContent(boot_data.ms_connect_url);"); //Old token store
                    view.loadUrl(String.format(Constants.LOGIN_URL,teamName)+"/home");
                    return;
                }
            }
            if(url.contains("/app")) { //No bloody app nonsense
                view.loadUrl(String.format(Constants.LOGIN_URL,teamName) + "/home");
                return;
            }
            //Log.d("URL",url);
            super.onPageFinished(view, url);
            hideProgress();
        }
    }
}
