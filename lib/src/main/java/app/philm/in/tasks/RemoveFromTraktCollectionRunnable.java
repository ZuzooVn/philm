package app.philm.in.tasks;

import com.jakewharton.trakt.Trakt;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.services.MovieService;

import app.philm.in.model.PhilmMovie;
import retrofit.RetrofitError;

public class RemoveFromTraktCollectionRunnable extends BaseTraktActionRunnable {

    public RemoveFromTraktCollectionRunnable(String... ids) {
        super(ids);
    }

    @Override
    public Response doTraktCall(Trakt trakt, MovieService.Movies body) throws RetrofitError {
        return trakt.movieService().unlibrary(body);
    }

    @Override
    protected void movieRequiresModifying(PhilmMovie movie) {
        movie.setInCollection(false);
    }
}