package com.example.disabledpeople.ui.notifications;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.disabledpeople.R;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}