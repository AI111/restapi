
package com.example.sasha.resyapi;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.sasha.resyapi.domain.Person;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


public class PersonsListFragment extends Fragment  implements  SwipeRefreshLayout.OnRefreshListener,View.OnClickListener {
    List<Person> persons = new LinkedList<>();
    private RecyclerView rv;
    private SwipeRefreshLayout swipeRefreshLayout;

    ItemTouchHelper.Callback simpleItemTouchCallback = new ItemTouchHelper.Callback() {



        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof PlaceRecyclerViewAdapter.ViewHolder) {
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(0, swipeFlags);
            } else
                return 0;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

//        @Override
//        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//            getDefaultUIUtil().clearView(((PlaceRecyclerViewAdapter.ViewHolder) viewHolder).getSwipableView());
//        }
//
//        @Override
//        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//            if (viewHolder != null) {
//                getDefaultUIUtil().onSelected(((PlaceRecyclerViewAdapter.ViewHolder) viewHolder).getSwipableView());
//            }
//        }
//        @Override
//        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//            getDefaultUIUtil().onDraw(c, recyclerView, ((PlaceRecyclerViewAdapter.ViewHolder) viewHolder).getSwipableView(), dX, dY,    actionState, isCurrentlyActive);
//        }
//        @Override
//        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//            getDefaultUIUtil().onDrawOver(c, recyclerView, ((PlaceRecyclerViewAdapter.ViewHolder) viewHolder).getSwipableView(), dX, dY,    actionState, isCurrentlyActive);
//        }

        @Override
        public void onSwiped( final RecyclerView.ViewHolder viewHolder, int swipeDir) {

            final int pos =viewHolder.getAdapterPosition();
            Log.d(MainActivity.LOG_TAG, "POSITION = " + pos);
            final Person person = persons.get(pos);
            Snackbar.make(rv,"item # "+viewHolder.getAdapterPosition(),Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    persons.add(pos,person);
                    rv.getAdapter().notifyItemInserted(pos);
                }
            }).setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    onDismiss(rv, viewHolder, person);
                }
            }).show();
            persons.remove(pos);
            rv.getAdapter().notifyItemRemoved(pos);
        }
    };
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
    private JsonArrayRequest getAllPersons;
    private FloatingActionButton fab;
    private View rootView;
    private Person person;
    private TextView firstName;
    private  TextView lastName;
    private  TextView middleName;
    private  TextView birthday;
    private Button savaBtn;

    public List<Person> getPlaces() {
        return persons;
    }

    public void setPlaces(ArrayList<Person> places) {
        this.persons = places;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(
                R.layout.fragment_places_list, container, false);
        rv = (RecyclerView)rootView.findViewById(R.id.recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        setupRecyclerView(rv);
        configAnketta();
        return rootView;
    }
    private  JsonArrayRequest createGetAllHandler(){
        String url = getString(R.string.server_url)+getString(R.string.all_personse);
        Log.d(MainActivity.LOG_TAG, url);
        return new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(MainActivity.LOG_TAG, response.toString());
                persons.clear();
                try {
                    persons.addAll(Person.getPersonListFromJSONArray(response));
                    rv.getAdapter().notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                networkErrorHandler(error);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }
    private JsonObjectRequest getOnCreatePersonhendler(Person person) throws JSONException {
        String url = getString(R.string.server_url)+getString(R.string.all_personse);

        return new JsonObjectRequest(Request.Method.POST, url,person.toJSON(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Snackbar.make(rootView,"SUCCESS ",Snackbar.LENGTH_SHORT).show();;
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(rootView,"Error ",Snackbar.LENGTH_SHORT).show();;
            }
        });
    }
    private StringRequest createDeleteHandler(int id){
        String url = getString(R.string.server_url)+getString(R.string.all_personse);
        return new StringRequest(Request.Method.DELETE, url+"/"+id, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Snackbar.make(rv,error.getLocalizedMessage(),Snackbar.LENGTH_LONG).show();
                Log.d(MainActivity.LOG_TAG, error.getLocalizedMessage());
            }
        });

    }
    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new PlaceRecyclerViewAdapter(getActivity(),
                persons));
        recyclerView.setItemViewCacheSize(20);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        itemTouchHelper.attachToRecyclerView(recyclerView);


    }


    public void onDismiss(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, Person person) {
        Log.d("REST CLIENT", "SWIPED " + " item = " + viewHolder.getAdapterPosition());
        ApplicationController.getInstance().addToRequestQueue(createDeleteHandler(person.getId()));
        //persons.remove(position);
        //recyclerView.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void onRefresh() {
        ApplicationController.getInstance().addToRequestQueue(createGetAllHandler());
    }
    private void networkErrorHandler(VolleyError error){
        if(error.getLocalizedMessage()==null){
            if(error instanceof TimeoutError){
                Snackbar.make(rv,"TIMEOUT ERROR",Snackbar.LENGTH_LONG).show();
            }else{
                Snackbar.make(rv,"UNKNOWN NETWORK ERROR",Snackbar.LENGTH_LONG).show();
            }


        }else{
            Snackbar.make(rv,error.getLocalizedMessage(),Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void onClick(View v) {
        toggleInformationView(v);
    }

    public static class PlaceRecyclerViewAdapter
            extends RecyclerView.Adapter<PlaceRecyclerViewAdapter.ViewHolder> {
        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Person> mValues;


        public static class ViewHolder extends RecyclerView.ViewHolder  {
            private final View swipedView;
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView name;
            public final TextView age;
            public final TextView undo;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                swipedView = mView.findViewById(R.id.person_item);
                mImageView = (ImageView) view.findViewById(R.id.image_view);
                name = (TextView) view.findViewById(R.id.person_name);
                age = (TextView) view.findViewById(R.id.person_age);
                undo = (TextView)view.findViewById(R.id.undo_button);

            }

            public View getSwipableView() {
                return swipedView;
            }

            @Override
            public String toString() {
                return super.toString() + " '" + name.getText();
            }

        }

        public Person getValueAt(int position) {
            return mValues.get(position);
        }

        public PlaceRecyclerViewAdapter(Context context, List<Person> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            // holder.mBoundString = mValues.get(position);
            Person person = mValues.get(position);
            holder.name.setText(person.getFirstName());
            holder.age.setText(person.getBirthday().toString());
            holder.undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(MainActivity.LOG_TAG, "UNDO");
                }
            });

            holder.mImageView.setImageDrawable(person.getDrawable());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
    private void configAnketta(){
        person = new Person();
        firstName = (TextView)rootView.findViewById(R.id.first_name);
        middleName = (TextView)rootView.findViewById(R.id.middle_name);
        lastName = (TextView)rootView.findViewById(R.id.lastn_name);
        savaBtn =  (Button)rootView.findViewById(R.id.save_btn);
        birthday = (TextView)rootView.findViewById(R.id.birthday);
        final DatePickerFragment datePickerFragment = new DatePickerFragment(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year+"-"+monthOfYear+"-"+dayOfMonth;
                person.setBirthday(Date.valueOf(date));
                birthday.setText(date);
            }
        });
        birthday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {

                    datePickerFragment.show(getFragmentManager(), "timePicker");
                }

                Log.d(MainActivity.LOG_TAG, "onTouch");
                return true;
            }
        });
        savaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.LOG_TAG, "onClick");
                person.setFirstName(firstName.getText().toString());
                person.setMiddleName(middleName.getText().toString());
                person.setLastname(lastName.getText().toString());
                if(firstName.getText()!=null&&person.getBirthday()!=null){
                    try {
                        ApplicationController.getInstance().addToRequestQueue(getOnCreatePersonhendler(person));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Snackbar.make(rv,"NAME AND BIRTHDAY IS EMPTY",Snackbar.LENGTH_SHORT).show();
                }
            }
        });


    }
    private void toggleInformationView(View view) {
        final View infoContainer = rootView.findViewById(R.id.information_container);

        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

        float radius = Math.max(infoContainer.getWidth(), infoContainer.getHeight()) * 2.0f;

        SupportAnimator reveal;
        if (infoContainer.getVisibility() == View.INVISIBLE) {
            infoContainer.setVisibility(View.VISIBLE);
            ObjectAnimator.ofObject(infoContainer,
                    "backgroundColor",
                    new ArgbEvaluator(),
                    0xFFFF5722,
                    0xFFFFFFFF).setDuration(700).start();
            reveal = ViewAnimationUtils.createCircularReveal(
                    infoContainer, cx, cy, 0, radius);

            reveal.setInterpolator(new AccelerateDecelerateInterpolator());

        } else {
            reveal = ViewAnimationUtils.createCircularReveal(
                    infoContainer, cx, cy, radius, 0);
            reveal.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    infoContainer.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationRepeat() {

                }
            });
            reveal.setInterpolator(new AccelerateDecelerateInterpolator());
            ObjectAnimator.ofObject(infoContainer,
                    "backgroundColor",
                    new ArgbEvaluator(),
                    0xFFFFFFFF, 0xFFFF5722).setDuration(700).start();
        }
        reveal.setDuration(600);
        reveal.start();
    }
}
