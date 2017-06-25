package online.findfootball.android.game.football.screen.find.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.location.LocationObj;
import online.findfootball.android.location.gmaps.fragments.LocationSelectFragment;

/**
 * Created by WiskiW on 16.04.2017.
 */

public class FGSelectLocationDialog extends DialogFragment {

    private static final String TAG = App.G_TAG + ":SelectLocDlg";
    public static final String F_TAG = App.G_TAG + ":FGSelectLocationDialog";

    private LocationSelectFragment locationSelectFragment;
    private Button okButton;
    private Button cancelButton;
    private LocationDialogListener listener;

    public FGSelectLocationDialog() {
    }

    public static FGSelectLocationDialog newInstance() {
        return new FGSelectLocationDialog();
    }

    public void setListener(LocationDialogListener listener) {
        this.listener = listener;
    }

    /*
    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = getResources().getDimensionPixelSize(R.dimen.fg_dialog_height);
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }
*/
    private void setupButtons(View rootView) {
        cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
                listener.onCancel();
            }
        });


        okButton = (Button) rootView.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationSelectFragment != null) {
                    LocationObj location = locationSelectFragment.getMarkerLocation();
                    if (location != null) {
                        listener.onSelect(location);
                        getDialog().cancel();
                    } else {
                        Toast.makeText(getContext(), "Select location!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fg_dialog_location, container, false);
        setupButtons(rootView);

        FragmentManager fragmentManager = getChildFragmentManager();
        locationSelectFragment = LocationSelectFragment.newInstance();
        fragmentManager
                .beginTransaction()
                .replace(R.id.gmap_container, locationSelectFragment, LocationSelectFragment.F_TAG)
                .commit();
        return rootView;
    }


    public interface LocationDialogListener {
        void onSelect(LocationObj location);

        void onCancel();
    }

}
