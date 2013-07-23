package contacts.app.android.service.account;

import static java.text.MessageFormat.format;

import java.net.URI;
import java.net.URISyntaxException;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import contacts.app.android.R;
import contacts.app.android.rest.AuthorizationException;
import contacts.app.android.rest.NetworkException;
import contacts.app.android.rest.RestClient;
import contacts.app.android.util.StringUtils;

/**
 * Allows user to sign in.
 * 
 * <p>
 * When user account is created we store password as is. Maybe, we should think
 * about using of tokens.
 */
public class SignInActivity extends AccountAuthenticatorActivity {

    private static final String TAG = SignInActivity.class.getName();

    private EditText nameInput;
    private EditText passwordInput;
    private Button signInButton;

    private String accountType;

    private String username;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_account_layout);

        accountType = getString(R.string.accountType);

        nameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);

        signInButton = (Button) findViewById(R.id.signIn);
        signInButton.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                onSignIn(view);
            }

        });
    }

    /**
     * Creates new account.
     * 
     * <p>
     * Accounts are differ by user name.
     */
    private void onSignIn(View view) {
        /*
         * Checks that user has entered name.
         */
        username = nameInput.getText().toString();
        if (StringUtils.isNullOrEmpty(username)) {
            Toast.makeText(this, R.string.accountNameEmpty, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        /*
         * Checks that user account with the same name not exists.
         */
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType(accountType);
        for (Account account : accounts) {
            if (username.equals(account.name)) {
                Log.d(TAG, format("Account for {0} already exists.", username));
                Toast.makeText(this, R.string.accountAlreadyExists,
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        /*
         * Checks that user has entered password.
         */
        password = passwordInput.getText().toString();
        if (StringUtils.isNullOrEmpty(password)) {
            Toast.makeText(this, R.string.accountPasswordEmpty,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        /*
         * Verify name and password.
         */
        SignInTask signInTask = new SignInTask();
        signInTask.execute();
    }

    public void onAuthCompleted(Boolean authenticated) {
        if (!authenticated) {
            Toast.makeText(SignInActivity.this,
                    R.string.accountInvalidCredentials, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        createAccount();
    }

    private void createAccount() {
        Log.d(TAG, format("Create account for {0}.", username));

        AccountManager manager = AccountManager.get(this);
        Account account = new Account(username, accountType);
        boolean accountAdded = manager.addAccountExplicitly(account, password,
                null);
        if (!accountAdded) {
            Log.d(TAG, format("Account for {0} was not created.", username));
            Toast.makeText(this, R.string.accountInvalidCredentials,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        setAccountAuthenticatorResult(bundle);

        finish();
    }

    private class SignInTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... args) {
            RestClient restClient = new RestClient();
            try {
                URI uri = new URI(getString(R.string.restScheme),
                        getString(R.string.restAuthority),
                        getString(R.string.restPathMyContact), null, null);
                restClient.doGet(username, password, uri);
                return true;
            } catch (AuthorizationException exception) {
                Log.d(TAG, "Invalid credentials.", exception);
            } catch (NetworkException exception) {
                Log.d(TAG, "Network error.", exception);
            } catch (URISyntaxException exception) {
                Log.d(TAG, "Invalid URI.", exception);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean authenticated) {
            onAuthCompleted(authenticated);
        }

    }

}