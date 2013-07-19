package contacts.app.android.service.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import contacts.app.android.R;

/**
 * Allows user create new account.
 */
public class AddAccountActivity extends AccountAuthenticatorActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;

    private String accountType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_account_layout);

        accountType = getString(R.string.accountType);

        usernameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);

        loginButton = (Button) findViewById(R.id.signIn);
        loginButton.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                createAccount();
            }

        });
    }

    private void createAccount() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        Account account = new Account(username, accountType);
        boolean accountAdded = addAccount(password, account);
        if (!accountAdded) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        setAccountAuthenticatorResult(bundle);

        finish();
    }

    private boolean addAccount(String password, Account account) {
        AccountManager manager = AccountManager.get(getApplicationContext());
        return manager.addAccountExplicitly(account, password, null);
    }

}