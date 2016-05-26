/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.support.android.designlibdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CheeseListFragment extends Fragment {
    public String tabType;

    public void setConstructor(String tabType){
        this.tabType = tabType;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.fragment_cheese_list, container, false);
        setupRecyclerView(rv);
        return rv;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),
                getRandomSublist(Cheeses.sCheeseStrings, 30), tabType));
    }

    private List<String> getRandomSublist(String[] array, int amount) {
        ArrayList<String> list = new ArrayList<>(amount);
        Random random = new Random();
        while (list.size() < amount) {
            list.add(array[random.nextInt(array.length)]);
        }
        return list;
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> mValues;
        String tabType;
        Context context;

        public SimpleStringRecyclerViewAdapter(Context context, List<String> items, String tabType) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
            this.tabType = tabType;
            this.context = context;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageViewGlide;
            public final ImageView mImageViewPicasso;
            public final ImageView mImageViewFresco;



            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageViewGlide = (ImageView) view.findViewById(R.id.imageview_case1);
                mImageViewPicasso = (ImageView) view.findViewById(R.id.imageview_case2);
                mImageViewFresco = (ImageView) view.findViewById(R.id.imageview_case3) ;
            }

        }

        public String getValueAt(int position) {
            return mValues.get(position);
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
            holder.mBoundString = mValues.get(position);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, WebtoonInfoActivity.class);
                    intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);

                    context.startActivity(intent);
                }
            });
            String st = MainActivity.imgUrlList.get(position).toString().replace("\"", "");

            if(tabType == "glide") {
                Glide.with(holder.mImageViewGlide.getContext())
                        .load(st)
                        .fitCenter()
                        .into(holder.mImageViewGlide);

                Picasso.with(holder.mImageViewPicasso.getContext())
                        .load(st)
                        .into(holder.mImageViewPicasso);
                SimpleDraweeView simpleDraweeView = (SimpleDraweeView)holder.mView.findViewById(R.id.imageview_case3);
                simpleDraweeView.setImageURI(Uri.parse(st));

            } else if(tabType == "picasso") {
                SimpleDraweeView simpleDraweeView = (SimpleDraweeView)holder.mView.findViewById(R.id.imageview_case3);


                DraweeController animatedGifController = Fresco.newDraweeControllerBuilder()
                        .setAutoPlayAnimations(true)
                        .setUri(Uri.parse("https://g.twimg.com/blog/blog/image/Taco_Party_0.gif"))
                        .build();


                simpleDraweeView.setController(animatedGifController);

            } else if(tabType == "fresco") {

            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
