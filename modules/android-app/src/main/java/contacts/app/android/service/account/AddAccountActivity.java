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
 * Allows user create an account.
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

    private void createAccount(View view) {
        Context context = view.getContext();

        String accountType = getString(R.string.accountType);

        String name = nameInput.getText().toString();
        if (StringUtils.isNullOrEmpty(name)) {
            Toast toast = Toast.makeText(context, R.string.accountNameEmpty,
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        AccountManager manager = AccountManager.get(context);
        Account[] accounts = manager.getAccountsByType(accountType);
        for (Account account : accounts) {
            if (name.equals(account.name)) {
                Log.d(TAG, format("Account for {0} already exists.", name));
                Toast toast = Toast.makeText(context,
                        R.string.accountAlreadyExists, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }

        String password = passwordInput.getText().toString();
        if (StringUtils.isNullOrEmpty(password)) {
            Toast toast = Toast.makeText(context,
                    R.string.accountPasswordEmpty, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Log.d(TAG, format("Create account for {0}.", name));

        Account account = new Account(name, accountType);
        boolean accountAdded = manager.addAccountExplicitly(account, password,
                null);
        if (!accountAdded) {
            Log.d(TAG, format("Account for {0} was not created.", name));
            Toast toast = Toast.makeText(context,
                    R.string.accountInvalidCredentials, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        setAccountAuthenticatorResult(bundle);

        finish();
    }

}