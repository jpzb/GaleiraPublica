package barcellos.zamborlini.joao_pedro.galeirapublica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class GalleryPagingSource extends ListenableFuturePagingSource<Integer, ImageData> {
    GalleryRepository galleryRepository;

    Integer inititalLoadSize = 0;

    public GalleryPagingSource(GalleryRepository galleryRepository){
        this.galleryRepository = galleryRepository;
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, ImageData> pagingState) {
        return null;
    }

    @NonNull
    @Override
    public ListenableFuture<LoadResult<Integer, ImageData>> loadFuture(@NonNull LoadParams<Integer> loadParams) {
        Integer nextPageNumber = loadParams.getKey();
        if(nextPageNumber == null){
            nextPageNumber = 1;
            inititalLoadSize = loadParams.getLoadSize();
        }

        Integer offset = 0;

        if(nextPageNumber == 2){
            offset = inititalLoadSize;
        } else {
            offset = ((nextPageNumber - 1) * loadParams.getLoadSize()) +
                    (inititalLoadSize - loadParams.getLoadSize());
        }

        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
        Integer finalOffset = offset;
        Integer finalNextPageNumber = nextPageNumber;
        ListenableFuture<LoadResult<Integer, ImageData>> lf = service.submit(new Callable<LoadResult<Integer, ImageData>>() {
            @Override
            public LoadResult<Integer, ImageData> call(){
                List<ImageData> imageDataList = null;
                try{
                    imageDataList = galleryRepository.loadImageData(loadParams.getLoadSize(), finalOffset);
                    Integer nextKey = null;
                    if (imageDataList.size() >= loadParams.getLoadSize()){
                        nextKey = finalNextPageNumber + 1;
                    }
                    return new LoadResult.Page<Integer, ImageData>(imageDataList, null, nextKey);
                } catch (FileNotFoundException e){
                    return new LoadResult.Error<>(e);
                }
            }
        });
        return lf;
    }
}
