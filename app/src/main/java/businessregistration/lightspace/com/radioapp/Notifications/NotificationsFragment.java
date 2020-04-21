package businessregistration.lightspace.com.radioapp.Notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Locale;

import businessregistration.lightspace.com.radioapp.Configuration.Config;
import businessregistration.lightspace.com.radioapp.Database.RealmHelper;
import businessregistration.lightspace.com.radioapp.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static RecyclerView.Adapter adapter;
    private ArrayList<String> textNotifications;
    private SQLiteDatabase db;

    private Realm realm;
    private RealmConfiguration realmConfig;
    private Config config;
    public static ArrayList<String> textLists;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int MY_DATA_CHECK_CODE = 20;
    private TextToSpeech mTts;


    private OnFragmentInteractionListener mListener;
    private ViewPager mViewPager;


    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }


    public interface ClickListener {

        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.notifications_radio_fragment_layout, container, false);
        //set up recyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //set up Realm database
        config = new Config(getActivity());
        realm = config.setUpRealm();

        setRecyclerViewCallbacks();
        checkForTextToSpeechData();

        // saveToDatabase("Thank You So Much THE TWO DREADFUL WITNESSES FOR REMEMBERING US BEFORE YOUR MIGHTY GOD OF ISRAEL");
        RealmHelper helper = new RealmHelper(realm);
        textNotifications = helper.retrieveTexts();

        //bind to recyclerView
        adapter = new RecyclerAdapter(getActivity(), textNotifications);
        recyclerView.setAdapter(adapter);

/*
        mViewPager =  view.findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(4);
*/

        return view;
    }

    private void checkForTextToSpeechData() {

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }

    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                mTts = new TextToSpeech(getActivity(), i -> {
                    mTts.setLanguage(Locale.US);

                });

            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    private void setRecyclerViewCallbacks() {

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                String text = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.item_detail)).getText().toString();
            }

            @Override
            public void onLongClick(View view, int position) {

                String text = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.item_detail)).getText().toString();
                readText(text);

            }
        }));

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();

                confirmAndDelete(position);
            }
        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;


        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    private void readText(String text) {

        String myText1 = "Did you sleep well?";
        String myText2 = "I hope so, because it's time to wake up.";
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        // mTts.speak(myText2, TextToSpeech.QUEUE_ADD, null);
    }

    private void confirmAndDelete(final int positionToDelete) {

        new AlertDialog.Builder(getActivity())
                .setTitle("CONFIRM DELETE?")
                .setMessage("By Swiping You will Delete this message.")
                .setIcon(R.drawable.jesusislordradio2)
                .setPositiveButton("DELETE", (dialog, which) -> {

                    config.setUpRealm().beginTransaction();
                    textNotifications.remove(positionToDelete);
                    config.setUpRealm().commitTransaction();
                    adapter.notifyItemRemoved(positionToDelete);

                    Log.d("Notifications", "Deleted message");
                })
                .setNegativeButton("CANCEL", (dialog, which) -> {

                    dialog.cancel();
                    Log.d("RecordMain", "Aborting mission...");

                    return;

                })
                .show();


    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        mTts.shutdown();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
