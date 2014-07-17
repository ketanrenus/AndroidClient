
package org.missionassetfund.apps.android.fragments;

import java.util.Date;

import org.missionassetfund.apps.android.R;
import org.missionassetfund.apps.android.models.Goal;
import org.missionassetfund.apps.android.models.Transaction;
import org.missionassetfund.apps.android.models.Transaction.TransactionType;
import org.missionassetfund.apps.android.models.User;
import org.missionassetfund.apps.android.utils.FormatterUtils;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

public class GoalPaymentFragment extends DialogFragment {

    public interface UpdatePaymentsListener {
        public void updatePayment(Transaction txn);
    }

    private Goal goal;
    EditText etAmount;
    Button btnGoalPayment;

    public static GoalPaymentFragment newInstance(Goal goal) {
        GoalPaymentFragment goalPaymentFragment = new GoalPaymentFragment();

        Bundle args = new Bundle();
        args.putSerializable("goal", goal);
        goalPaymentFragment.setArguments(args);
        return goalPaymentFragment;
    }

    public GoalPaymentFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.NewGoalDialog);
        goal = (Goal) getArguments().getSerializable("goal");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_payment, container);
        btnGoalPayment = (Button) view.findViewById(R.id.btnGoalPayment);
        etAmount = (EditText) view.findViewById(R.id.etAmount);
        etAmount.setText(FormatterUtils.formatAmount(goal.getPaymentAmount()));
        btnGoalPayment.setOnClickListener(newGoalPaymentListener);

        return view;
    }

    private OnClickListener newGoalPaymentListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Double amount = Double.parseDouble(etAmount.getText().toString());
            final Transaction txn = new Transaction();
            txn.setAmount(amount);
            txn.setUser((User) User.getCurrentUser());
            txn.setGoal(goal);
            txn.setTransactionDate(new Date());
            txn.setName(getString(R.string.goal_payment_desc));
            txn.setType(TransactionType.CREDIT);
            txn.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getActivity(), "Done savign txn", Toast.LENGTH_LONG).show();
                        UpdatePaymentsListener upListener = (UpdatePaymentsListener) getActivity();
                        upListener.updatePayment(txn);
                        dismiss();
                    } else {
                        Log.e("goal", e.getLocalizedMessage(), e);
                        Toast.makeText(getActivity(), R.string.parse_error_saving,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

}
