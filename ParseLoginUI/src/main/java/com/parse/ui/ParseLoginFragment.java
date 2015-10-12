/*
 *  Copyright (c) 2014, Parse, LLC. All rights reserved.
 *
 *  You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 *  copy, modify, and distribute this software in source code or binary form for use
 *  in connection with the web services and APIs provided by Parse.
 *
 *  As with any software that integrates with the Parse platform, your use of
 *  this software is subject to the Parse Terms of Service
 *  [https://www.parse.com/about/terms]. This copyright notice shall be
 *  included in all copies or substantial portions of the software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.parse.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.twitter.Twitter;

import org.json.JSONObject;

/**
 * Fragment for the user login screen.
 */
public class ParseLoginFragment extends ParseLoginFragmentBase implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


  public interface ParseLoginFragmentListener {
    public void onSignUpClicked(String username, String password);

    public void onLoginHelpClicked();

    public void onLoginSuccess();
  }

    private static final String LOG_TAG = "ParseLoginFragment";
    private static final String USER_OBJECT_NAME_FIELD = "name";
    private static final String USER_OBJECT_EMAIL_FIELD = "email";
    private static final int RC_SIGN_IN = 9001;
    private View parseLogin;
    private EditText usernameField;
    private EditText passwordField;
    private TextView parseLoginHelpButton;
    private Button parseLoginButton;
    private Button parseSignupButton;
    private Button facebookLoginButton;
    private Button twitterLoginButton;
    private Button mPlusSignInButton;
    private ParseLoginFragmentListener loginFragmentListener;
    private ParseOnLoginSuccessListener onLoginSuccessListener;
    private GoogleApiClient mGoogleApiClient;
    private ParseLoginConfig config;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private static final String KEY_IS_RESOLVING = "is_resolving";
    private static final String KEY_SHOULD_RESOLVE = "should_resolve";

  public static ParseLoginFragment newInstance(Bundle configOptions) {
    ParseLoginFragment loginFragment = new ParseLoginFragment();
    loginFragment.setArguments(configOptions);

    return loginFragment;
  }
    private void showErrorDialog(ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();

        if (GooglePlayServicesUtil.isUserRecoverableError(errorCode)) {
            // Show the default Google Play services error dialog which may still start an intent
            // on our behalf if the user can resolve the issue.
            GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(), RC_SIGN_IN,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mShouldResolve = false;

                        }
                    }).show();
        } else {
            // No default Google Play Services error, display a message to the user.
            String errorString = getString(R.string.play_services_error_fmt, errorCode);
            Toast.makeText(getActivity(), errorString, Toast.LENGTH_SHORT).show();
            mShouldResolve = false;

        }
    }
  @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
      Log.d(LOG_TAG, "onConnectionFailed:" + connectionResult);

      if (!mIsResolving && mShouldResolve) {
          if (connectionResult.hasResolution()) {
              try {
                  connectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
                  mIsResolving = true;
              } catch (IntentSender.SendIntentException e) {
                  Log.e(LOG_TAG, "Could not resolve ConnectionResult.", e);
                  mIsResolving = false;
                  mGoogleApiClient.connect();
              }
          } else {
              // Could not resolve the connection result, show the user an
              // error dialog.
              showErrorDialog(connectionResult);
          }
      }
    }

    @Override
    public void onConnected(Bundle bundle) {
        String name = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null ? Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName() : " ";
        Intent mHomeIntent = new Intent(getActivity(), ParseLoginFragment.class);
        mHomeIntent.putExtra("Email", Plus.AccountApi.getAccountName(mGoogleApiClient));
        mHomeIntent.putExtra("Name", name);
        startActivity(mHomeIntent);
        //finish();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended:");
    }


    @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {
    config = ParseLoginConfig.fromBundle(getArguments(), getActivity());

    View v = inflater.inflate(R.layout.com_parse_ui_parse_login_fragment,
        parent, false);
    ImageView appLogo = (ImageView) v.findViewById(R.id.app_logo);
    parseLogin = v.findViewById(R.id.parse_login);
    usernameField = (EditText) v.findViewById(R.id.login_username_input);
    passwordField = (EditText) v.findViewById(R.id.login_password_input);
    parseLoginHelpButton = (Button) v.findViewById(R.id.parse_login_help);
    parseLoginButton = (Button) v.findViewById(R.id.parse_login_button);
    parseSignupButton = (Button) v.findViewById(R.id.parse_signup_button);
    facebookLoginButton = (Button) v.findViewById(R.id.facebook_login);
    twitterLoginButton = (Button) v.findViewById(R.id.twitter_login);
    mPlusSignInButton = (Button) v.findViewById(R.id.plus_sign_in_button);



    if (appLogo != null && config.getAppLogo() != null) {
      appLogo.setImageResource(config.getAppLogo());
    }
    if (allowParseLoginAndSignup()) {
      setUpParseLoginAndSignup();
    }
    if (allowFacebookLogin()) {
      setUpFacebookLogin();
    }
    if (allowTwitterLogin()) {
      setUpTwitterLogin();
    }
      setUpGmailLogin();


    return v;
  }


  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof ParseLoginFragmentListener) {
      loginFragmentListener = (ParseLoginFragmentListener) activity;
    } else {
      throw new IllegalArgumentException(
          "Activity must implemement ParseLoginFragmentListener");
    }

    if (activity instanceof ParseOnLoginSuccessListener) {
      onLoginSuccessListener = (ParseOnLoginSuccessListener) activity;
    } else {
      throw new IllegalArgumentException(
          "Activity must implemement ParseOnLoginSuccessListener");
    }

    if (activity instanceof ParseOnLoadingListener) {
      onLoadingListener = (ParseOnLoadingListener) activity;
    } else {
      throw new IllegalArgumentException(
          "Activity must implemement ParseOnLoadingListener");
    }
  }

  @Override
  protected String getLogTag() {
    return LOG_TAG;
  }

  private void setUpParseLoginAndSignup() {
    parseLogin.setVisibility(View.VISIBLE);

    if (config.isParseLoginEmailAsUsername()) {
      usernameField.setHint(R.string.com_parse_ui_email_input_hint);
      usernameField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    if (config.getParseLoginButtonText() != null) {
      parseLoginButton.setText(config.getParseLoginButtonText());
    }

    parseLoginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        if (username.length() == 0) {
          if (config.isParseLoginEmailAsUsername()) {
            showToast(R.string.com_parse_ui_no_email_toast);
          } else {
            showToast(R.string.com_parse_ui_no_username_toast);
          }
        } else if (password.length() == 0) {
          showToast(R.string.com_parse_ui_no_password_toast);
        } else {
          loadingStart(true);
          ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
              if (isActivityDestroyed()) {
                return;
              }

              if (user != null) {
                loadingFinish();
                loginSuccess();
              } else {
                loadingFinish();
                if (e != null) {
                  debugLog(getString(R.string.com_parse_ui_login_warning_parse_login_failed) +
                      e.toString());
                  if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                    if (config.getParseLoginInvalidCredentialsToastText() != null) {
                      showToast(config.getParseLoginInvalidCredentialsToastText());
                    } else {
                      showToast(R.string.com_parse_ui_parse_login_invalid_credentials_toast);
                    }
                    passwordField.selectAll();
                    passwordField.requestFocus();
                  } else {
                    showToast(R.string.com_parse_ui_parse_login_failed_unknown_toast);
                  }
                }
              }
            }
          });
        }
      }
    });

    if (config.getParseSignupButtonText() != null) {
      parseSignupButton.setText(config.getParseSignupButtonText());
    }

    parseSignupButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        loginFragmentListener.onSignUpClicked(username, password);
      }
    });

    if (config.getParseLoginHelpText() != null) {
      parseLoginHelpButton.setText(config.getParseLoginHelpText());
    }

    parseLoginHelpButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        loginFragmentListener.onLoginHelpClicked();
      }
    });
  }


  private LogInCallback facebookLoginCallbackV4 = new LogInCallback() {
    @Override
    public void done(ParseUser user, ParseException e) {
      if (isActivityDestroyed()) {
        return;
      }

      if (user == null) {
        loadingFinish();
        if (e != null) {
          showToast(R.string.com_parse_ui_facebook_login_failed_toast);
          debugLog(getString(R.string.com_parse_ui_login_warning_facebook_login_failed) +
                  e.toString());
        }
      } else if (user.isNew()) {
        GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
            new GraphRequest.GraphJSONObjectCallback() {
              @Override
              public void onCompleted(JSONObject fbUser,
                                      GraphResponse response) {
                  /*
                    If we were able to successfully retrieve the Facebook
                    user's name, let's set it on the fullName field.
                  */
                ParseUser parseUser = ParseUser.getCurrentUser();
                if (fbUser != null && parseUser != null
                        && fbUser.optString("name").length() > 0) {
                  parseUser.put(USER_OBJECT_NAME_FIELD, fbUser.optString("name"));
                  parseUser.put(USER_OBJECT_EMAIL_FIELD, fbUser.optString("email"));
/*
                    Iterator<String> it= fbUser.keys();

                    while(it.hasNext())
                    {
                        String key= it.next();
                        Toast.makeText(getContext(),key+" : "+fbUser.optString(key),Toast.LENGTH_SHORT).show();
                    }
*/

                   // parseUser.setEmail(fbUser.optString("email"));

                    //parseUser.put(USER_OBJECT_EMAIL_FIELD, fbUser.optString("email"));
                    //parseUser.put("emailVerified",fbUser.optString("verified"));
                    //Toast.makeText(getContext(),  " Email: " + parseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(),  " EmailVerified: " + parseUser.get("emailVerified"), Toast.LENGTH_SHORT).show();
                    /*parseUser.pinInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(getContext(),  "Pin done successfully", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                    parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                      if (e != null) {
                        debugLog(getString(
                                R.string.com_parse_ui_login_warning_facebook_login_user_update_failed) +
                                e.toString());
                      }
                      loginSuccess();
                    }
                  });
                }
                loginSuccess();
              }
            }
        ).executeAsync();
      } else {
        loginSuccess();
      }
    }
  };

    private void setUpGmailLogin(){
        mPlusSignInButton.setVisibility(View.VISIBLE);
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();

        if (getArguments() != null) {
            mIsResolving = getArguments().getBoolean(KEY_IS_RESOLVING);
            mShouldResolve = getArguments().getBoolean(KEY_SHOULD_RESOLVE);
        }
        mPlusSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShouldResolve = true;
                mGoogleApiClient.connect();
            }
        });

    }

  private void setUpFacebookLogin() {
    facebookLoginButton.setVisibility(View.VISIBLE);

    if (config.getFacebookLoginButtonText() != null) {
      facebookLoginButton.setText(config.getFacebookLoginButtonText());
    }

    facebookLoginButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        loadingStart(false); // Facebook login pop-up already has a spinner
        if (config.isFacebookLoginNeedPublishPermissions()) {
          ParseFacebookUtils.logInWithPublishPermissionsInBackground(getActivity(),
                  config.getFacebookLoginPermissions(), facebookLoginCallbackV4);
        } else {
          ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(),
                  config.getFacebookLoginPermissions(), facebookLoginCallbackV4);
        }
      }
    });
  }

  private void setUpTwitterLogin() {
    twitterLoginButton.setVisibility(View.VISIBLE);

    if (config.getTwitterLoginButtonText() != null) {
      twitterLoginButton.setText(config.getTwitterLoginButtonText());
    }

    twitterLoginButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        loadingStart(false); // Twitter login pop-up already has a spinner
        ParseTwitterUtils.logIn(getActivity(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if (isActivityDestroyed()) {
              return;
            }

            if (user == null) {
              loadingFinish();
              if (e != null) {
                showToast(R.string.com_parse_ui_twitter_login_failed_toast);
                debugLog(getString(R.string.com_parse_ui_login_warning_twitter_login_failed) +
                    e.toString());
              }
            } else if (user.isNew()) {
              Twitter twitterUser = ParseTwitterUtils.getTwitter();
              if (twitterUser != null
                  && twitterUser.getScreenName().length() > 0) {
                /*
                  To keep this example simple, we put the users' Twitter screen name
                  into the name field of the Parse user object. If you want the user's
                  real name instead, you can implement additional calls to the
                  Twitter API to fetch it.
                */
                user.put(USER_OBJECT_NAME_FIELD, twitterUser.getScreenName());
                user.saveInBackground(new SaveCallback() {
                  @Override
                  public void done(ParseException e) {
                    if (e != null) {
                      debugLog(getString(
                          R.string.com_parse_ui_login_warning_twitter_login_user_update_failed) +
                          e.toString());
                    }
                    loginSuccess();
                  }
                });
              }
            } else {
              loginSuccess();
            }
          }
        });
      }
    });
  }

  private boolean allowParseLoginAndSignup() {
    if (!config.isParseLoginEnabled()) {
      return false;
    }

    if (usernameField == null) {
      debugLog(R.string.com_parse_ui_login_warning_layout_missing_username_field);
    }
    if (passwordField == null) {
      debugLog(R.string.com_parse_ui_login_warning_layout_missing_password_field);
    }
    if (parseLoginButton == null) {
      debugLog(R.string.com_parse_ui_login_warning_layout_missing_login_button);
    }
    if (parseSignupButton == null) {
      debugLog(R.string.com_parse_ui_login_warning_layout_missing_signup_button);
    }
    if (parseLoginHelpButton == null) {
      debugLog(R.string.com_parse_ui_login_warning_layout_missing_login_help_button);
    }

    boolean result = (usernameField != null) && (passwordField != null)
        && (parseLoginButton != null) && (parseSignupButton != null)
        && (parseLoginHelpButton != null);

    if (!result) {
      debugLog(R.string.com_parse_ui_login_warning_disabled_username_password_login);
    }
    return result;
  }

  private boolean allowFacebookLogin() {
    if (!config.isFacebookLoginEnabled()) {
      return false;
    }

    if (facebookLoginButton == null) {
      debugLog(R.string.com_parse_ui_login_warning_disabled_facebook_login);
      return false;
    } else {
      return true;
    }
  }

  private boolean allowTwitterLogin() {
    if (!config.isTwitterLoginEnabled()) {
      return false;
    }

    if (twitterLoginButton == null) {
      debugLog(R.string.com_parse_ui_login_warning_disabled_twitter_login);
      return false;
    } else {
      return true;
    }
  }

  private void loginSuccess() {
    onLoginSuccessListener.onLoginSuccess();
  }

    public boolean ismIsResolving() {
        return mIsResolving;
    }

    public void setmIsResolving(boolean mIsResolving) {
        this.mIsResolving = mIsResolving;
    }

    public boolean ismShouldResolve() {
        return mShouldResolve;
    }

    public void setmShouldResolve(boolean mShouldResolve) {
        this.mShouldResolve = mShouldResolve;
    }
}
