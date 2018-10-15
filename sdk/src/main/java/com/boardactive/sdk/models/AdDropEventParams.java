package com.boardactive.sdk.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class AdDropEventParams implements List<AdDropEventParams> {

    @SerializedName("promotion_id")
    @Expose
    private String promotion_id;

    @SerializedName("advertisement_id")
    @Expose
    private String advertisement_id;

    @SerializedName("firebaseNotificationId")
    @Expose
    private String firebaseNotificationId;

    /**
     * No args constructor for use in serialization
     */
    public AdDropEventParams() {
    }

    /**
     * @param lat
     * @param lng
     * @param deviceTimeString
     */
    public AdDropEventParams(
            String promotion_id,
            String advertisement_id,
            String firebaseNotificationId
    ) {
        super();
        this.promotion_id = promotion_id;
        this.advertisement_id = advertisement_id;
        this.firebaseNotificationId = firebaseNotificationId;
    }

    public String getPromotion_id() { return promotion_id; }

    public void setPromotion_id(String promotion_id) {
        this.promotion_id = promotion_id;
    }

    public String getAdvertisement_id() {
        return advertisement_id;
    }

    public void setAdvertisement_id(String advertisement_id) {
        this.advertisement_id = advertisement_id;
    }

    public String getFirebaseNotificationId() {
        return firebaseNotificationId;
    }

    public void setFirebaseNotificationId(String firebaseNotificationId) {
        this.firebaseNotificationId = firebaseNotificationId;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @NonNull
    @Override
    public Iterator<AdDropEventParams> iterator() {
        return null;
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return null;
    }

    @Override
    public boolean add(AdDropEventParams adDropEventParams) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends AdDropEventParams> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends AdDropEventParams> c) {
        return false;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public AdDropEventParams get(int index) {
        return null;
    }

    @Override
    public AdDropEventParams set(int index, AdDropEventParams element) {
        return null;
    }

    @Override
    public void add(int index, AdDropEventParams element) {

    }

    @Override
    public AdDropEventParams remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @NonNull
    @Override
    public ListIterator<AdDropEventParams> listIterator() {
        return null;
    }

    @NonNull
    @Override
    public ListIterator<AdDropEventParams> listIterator(int index) {
        return null;
    }

    @NonNull
    @Override
    public List<AdDropEventParams> subList(int fromIndex, int toIndex) {
        return null;
    }
}
