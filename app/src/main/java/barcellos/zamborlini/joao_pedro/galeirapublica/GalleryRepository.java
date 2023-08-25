package barcellos.zamborlini.joao_pedro.galeirapublica;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GalleryRepository {
    Context context;

    public GalleryRepository(Context context){
        this.context = context;
    }

    public List<ImageData> loadImageData(Integer limit, Integer offset) throws
            FileNotFoundException{
        List<ImageData> imageDataList = new ArrayList<>();

        // Pegando as dimensões da imagem
        int w = (int)context.getResources().getDimension(R.dimen.im_width);
        int h = (int)context.getResources().getDimension(R.dimen.im_heigth);

        // Pegando informações de cada foto salva na galeria do celular
        String[] projection = new String[]{MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE};

        // Definindo a seleção como nulo para pegar todas as fotos
        String selection = null;
        // Definindo os argumentos da seleção como nulo, por ela ser nula também
        String selectionArgs[] = null;

        // Definindo que a lista será ordenada pela data da foto
        String sort = MediaStore.Images.Media.DATE_ADDED;

        Cursor cursor = null;

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
            Bundle queryArgs = new Bundle();

            // Definindo os valores da seleção e seus argumentos
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
            queryArgs.putStringArray(
                    ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);

            // Definindo o método de ordenação
            queryArgs.putString(ContentResolver.QUERY_ARG_SORT_COLUMNS, sort);

            // Definindo a direção da ordenação
            queryArgs.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    ContentResolver.QUERY_SORT_DIRECTION_ASCENDING);

            queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, limit);
            queryArgs.putInt(ContentResolver.QUERY_ARG_OFFSET, offset);

            // Realizando a consulta por meio dos parãmetros dados
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, queryArgs, null);
        } else {
            // Caso a versão seja diferente, irá realizar uma busca de maneira diferente
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, selectionArgs, sort + " ASC + LIMIT " +
                            String.valueOf(limit) + " OFFSET " + String.valueOf(offset));
        }

        // Pegando os dados da imagem
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

        while (cursor.moveToNext()){
            // Pegando os valores das colunas de uma imagem

            // Pegando o id da imagem
            long id = cursor.getLong(idColumn);

            // Pegando o URI da imagem
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            // Pegando o nome da imagem
            String name = cursor.getString(nameColumn);

            // Pegando a data da imagem
            int dateAdded = cursor.getInt(dateAddedColumn);

            // Pegando o tamanho da imagem
            int size = cursor.getInt(sizeColumn);

            // Gerando uma miniatura da imagem
            Bitmap thumb = Util.getBitmap(context, contentUri, w, h);
            // Armazena o valor da coluna e URI do conteudo em um objeto local

            // Adicionando a imagem na lista de imagem
            imageDataList.add(new ImageData(contentUri, thumb, name, new Date(dateAdded*1000L), size));
        }
        return imageDataList;
    }
}
