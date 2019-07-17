    package com.boardactive.bakit;

    import android.content.Context;
    import android.graphics.Bitmap;
    import android.util.LruCache;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.toolbox.ImageLoader;
    import com.android.volley.toolbox.Volley;

    public final class AppSingleton {
        private static volatile AppSingleton mAppSingletonInstance;
        private RequestQueue mRequestQueue;
        private ImageLoader mImageLoader;
        private final Context mContext;

        private AppSingleton(final Context context) {
            mContext = context;
            mRequestQueue = getRequestQueue();

            mImageLoader = new ImageLoader(mRequestQueue,
                    new ImageLoader.ImageCache() {
                        private final LruCache<String, Bitmap>
                                cache = new LruCache<String, Bitmap>(20);

                        @Override
                        public Bitmap getBitmap(String url) {
                            return cache.get(url);
                        }

                        @Override
                        public void putBitmap(String url, Bitmap bitmap) {
                            cache.put(url, bitmap);
                        }
                    });
        }

        public static AppSingleton getInstance(Context context) {
            if (mAppSingletonInstance == null) {
                synchronized (AppSingleton.class) {
                    if (context.getApplicationContext() != null) {
                        context = context.getApplicationContext();
                    }
                }
                mAppSingletonInstance = new AppSingleton(context);
            }
            return mAppSingletonInstance;
        }

        public RequestQueue getRequestQueue() {
            if (mRequestQueue == null) {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes one in.
                mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
                return mRequestQueue;
            } else {
                return mRequestQueue;
            }
        }

        public <T> void addToRequestQueue(Request<T> req, String tag) {
            req.setTag(tag);
            getRequestQueue().add(req);
        }

        public ImageLoader getImageLoader() {
            return mImageLoader;
        }

        public void cancelPendingRequests(Object tag) {
            if (mRequestQueue != null) {
                mRequestQueue.cancelAll(tag);
            }
        }
    }