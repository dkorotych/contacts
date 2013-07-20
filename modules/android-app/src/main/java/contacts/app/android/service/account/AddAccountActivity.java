package contacts.app.android.service.account;

import static java.text.MessageFormat.format;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import contacts.app.android.R;
import contacts.app.android.util.StringUtils;

/**
 * Allows create a new user account.
 * 
 * <p>
 * When user account is created we store password as is. Maybe, we should think
 * about using of tokens.
 */
public class AddAccountActivity extends AccountAuthenticatorActivity {

    private static final String TAG = AddAccountActivity.class.getName();

    private EditText nameInput;
    private EditText passwordInput;
    private Button signInButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_account_layout);

        nameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);

        signInButton = (Button) findViewById(R.id.signIn);
        signInButton.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                createAccount(view);
            }

        });
    }

    /**
     * Creates new account.
     * 
     * <p>
     * Accounts are differ by user name.
     */
    private void createAccount(View view) {
        Context context = view.getContext();

        String accountType = getString(R.string.accountType);

        /*
         * Checks that user has entered name.
         */
        String name = nameInput.getText().toString();
        if (StringUtils.isNullOrEmpty(name)) {
            Toast.makeText(context, R.string.accountNameEmpty,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        /*
         * Checks that user account with the same name not exists.
         */
        AccountManager manager = AccountManager.get(context);
        Account[] accounts = manager.getAccountsByType(accountType);
        for (Account account : accounts) {
            if (name.equals(account.name)) {
                Log.d(TAG, format("Account for {0} already exists.", name));
                Toast.makeText(context, R.string.accountAlreadyExists,
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        /*
         * Checks that user has entered password.
         */
        String password = passwordInput.getText().toString();
        if (StringUtils.isNullOrEmpty(password)) {
            Toast.makeText(context, R.string.accountPasswordEmpty,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, format("Create account for {0}.", name));

        /*
         * Creates new user account with given credentials.
         */
        Account account = new Account(name, accountType);
        boolean accountAdded = manager.addAccountExplicitly(account, password,
                null);
        if (!accountAdded) {
            Log.d(TAG, format("Account for {0} was not created.", name));
            Toast.makeText(context, R.string.accountInvalidCredentials,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        setAccountAuthenticatorResult(bundle);

        finish();
    }

}