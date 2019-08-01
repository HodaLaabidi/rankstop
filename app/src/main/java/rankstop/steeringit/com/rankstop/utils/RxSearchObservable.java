package rankstop.steeringit.com.rankstop.utils;

import androidx.appcompat.widget.SearchView;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;


public class RxSearchObservable {

    public static Observable<String> fromView(SearchView searchView) {
        final PublishSubject<String> subject = PublishSubject.create();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //subject.onComplete();
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                subject.onNext(s);
                return false;
            }
        });

        return subject;
    }
}
