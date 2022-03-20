package com.snava.cubanews;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeletePagesManager {

  public static int deleteOldPages(int amount, TimeUnit timeUnit, SqliteMetadataDatabase db,
      Indexer indexer) throws IOException {
    long deleted = 0;
    for (List<String> urls : db.getDeletablePages(amount, timeUnit, 100)) {
      deleted += urls.size();
      indexer.delete(urls);
    }
    int deletedMetadata = db.updateStateByAgeAndState(amount, timeUnit, DocumentState.ACTIVE,
        DocumentState.DELETED);
    indexer.close();
    if (deleted != deletedMetadata) {
      throw new RuntimeException(
          String.format("Deleted %d, Deleted Metadata: %d", deleted, deletedMetadata)
      );
    }
    return deletedMetadata;
  }

}
