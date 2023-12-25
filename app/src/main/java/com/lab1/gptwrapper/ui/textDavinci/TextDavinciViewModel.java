package com.lab1.gptwrapper.ui.textDavinci;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TextDavinciViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TextDavinciViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}