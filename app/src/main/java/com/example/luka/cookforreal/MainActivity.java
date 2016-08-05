package com.example.luka.cookforreal;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.luka.cookforreal.models.CookModel;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.CollationKey;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ListView lvCook;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Ucitavam sorry brt");

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
                .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
                .showImageOnFail(R.drawable.ic_empty) // resource or drawable
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .defaultDisplayImageOptions(defaultOptions)
        .build();
        ImageLoader.getInstance().init(config);

        lvCook = (ListView)findViewById(R.id.lvCook);
    }

    public class JSONTask extends AsyncTask<String, String, List<CookModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<CookModel> doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(params[0]).build();

            try {
                Response response = client.newCall(request).execute();
                String final_json = response.body().string();
                JSONObject parentObject = new JSONObject(final_json);
                JSONArray parentArray = parentObject.getJSONArray("recipes");
                List<CookModel> cookModelList = new ArrayList<>();
                Gson gson = new Gson();

                for (int i = 0; i < parentArray.length(); i++)
                   {
                       JSONObject finalObject = parentArray.getJSONObject(i);
                       CookModel cookModel = gson.fromJson(finalObject.toString(),CookModel.class);

                     /*  cookModel.setId(finalObject.getString("id"));
                       cookModel.setImage(finalObject.getString("image_file_name"));
                       cookModel.setTitle(finalObject.getString("title"));
                       cookModel.setLikes("Lajkovi "+finalObject.getString("likes"));
                       cookModel.setDefault_preparation("Vreme pripreme: "+ finalObject.getString("preparation_time")+"min");
                       List<CookModel.Story> storyList = new ArrayList<>();
                       for(int j = 0;j < finalObject.getJSONArray("steps").length();j++){
                           CookModel.Story story = new CookModel.Story();
                           story.setStory(finalObject.getJSONArray("steps").getJSONObject(j).getString("text"));
                           storyList.add(story);
                       }
                       cookModel.setStoryList(storyList);*/
                       cookModelList.add(cookModel);
                   }
                return cookModelList;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<CookModel> result) {
            super.onPostExecute(result);

            CookAdapter adapter = new CookAdapter(getApplicationContext(),R.layout.ow,result);
            lvCook.setAdapter(adapter);

            dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh)
        {
            new JSONTask().execute("http://46.101.236.188/v1/recipes/get-init-recipes");
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public class CookAdapter extends ArrayAdapter
    {
        private List<CookModel> cookModelList;
        private int resource;
        private LayoutInflater inflater;
        public CookAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
            cookModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = null;
                if(convertView == null) {
                    holder = new ViewHolder();
                    Typeface james = Typeface.createFromAsset(getAssets(),getString(R.string.typeface_devroyun));
                    convertView = inflater.inflate(resource, null);
                    holder.icon = (ImageView) convertView.findViewById(R.id.ivIcon);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                    holder.tvTitle.setTypeface(james);
                    holder.tvPrep = (TextView) convertView.findViewById(R.id.tvPrep);
                    holder.tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
                    holder.tvStory = (TextView) convertView.findViewById(R.id.tvStory);
                    convertView.setTag(holder);
                }else{
                  holder = (ViewHolder)convertView.getTag();
            }

            final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
            ImageLoader.getInstance().displayImage(cookModelList.get(position).getImage(), holder.icon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });

            holder.tvTitle.setText(cookModelList.get(position).getTitle());
            holder.tvTitle.setTextColor(Color.parseColor("#DD1021"));
            holder.tvPrep.setText("Vreme pripreme: " + cookModelList.get(position).getDefault_preparation()+"min");
            holder.tvPrep.setTextColor(Color.parseColor("#A9A9A9"));
            holder.tvLikes.setText("Lajkovi " + cookModelList.get(position).getLikes());
            holder.tvLikes.setTextColor(Color.parseColor("#FFC300"));
            StringBuffer stringBuffer = new StringBuffer();
            for(CookModel.Steps steps : cookModelList.get(position).getStoryList())
            {
                stringBuffer.append("Ukratko: "+steps.getText()+"...\n");
                break;
            }
            holder.tvStory.setText(stringBuffer);

            return  convertView;
        }


         class ViewHolder{
             private  ImageView icon;
             private  TextView tvTitle;
             private  TextView tvLikes;
             private  TextView tvStory;
             private  TextView tvPrep;
         }
    }
}
