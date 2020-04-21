package businessregistration.lightspace.com.radioapp;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import businessregistration.lightspace.com.radioapp.Configuration.Config;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Webviewfragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Webviewfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Webviewfragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Config radioConfig;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager mViewPager;

    private OnFragmentInteractionListener mListener;

    public Webviewfragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Webviewfragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Webviewfragment newInstance(String param1, String param2) {
        Webviewfragment fragment = new Webviewfragment();
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webviewfragment, container, false);
        // Inflate the layout for this fragment
        WebView webView = view.findViewById(R.id.website);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        setUpWebView(webView);
        swipeRefreshLayout.setOnRefreshListener(() -> setUpWebView(webView));

       /*
        mViewPager =  view.findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(4);
*/

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setUpWebView(WebView webView) {

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getProgress();
        webView.getSettings().getDisplayZoomControls();
//        webView.getSettings().getMediaPlaybackRequiresUserGesture();
        webView.getSettings().getTextZoom();
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().getAllowFileAccessFromFileURLs();
        webView.getSettings().getAllowUniversalAccessFromFileURLs();

        radioConfig = new Config(getActivity());
        webView.loadUrl(radioConfig.getRadioWebsiteURL());

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
