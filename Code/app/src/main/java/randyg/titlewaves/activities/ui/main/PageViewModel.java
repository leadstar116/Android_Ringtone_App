package randyg.titlewaves.activities.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private MutableLiveData<String> mText = new MutableLiveData<>();

    public void setIndex(int index) {
        mIndex.setValue(index);
    }
    public void setText(String text) {
        mText.setValue(text);
    }
    public LiveData<String> getText() {
        return mText;
    }
}