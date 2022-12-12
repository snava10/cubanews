package com.snava.cubanews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.snava.cubanews.data.access.SqliteMetadataDatabase;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class DeletePagesManagerTest {

  @Test
  void deleteOldPages() throws Exception {
    SqliteMetadataDatabase db = mock(SqliteMetadataDatabase.class);
    List<List<String>> iterable = Collections.singletonList(List.of("url1"));
    when(db.getDeletablePages(anyInt(), any(TimeUnit.class), anyInt())).thenReturn(iterable);
    when(db.updateStateByAgeAndState(anyInt(), any(TimeUnit.class), any(DocumentState.class),
        any(DocumentState.class)))
        .thenReturn(1);

    Indexer indexer = mock(Indexer.class);

    assertThat(DeletePagesManager.deleteOldPages(24, TimeUnit.HOURS, db, indexer))
        .isEqualTo(1);

    verify(db, times(1))
        .getDeletablePages(24, TimeUnit.HOURS, 100);
    verify(indexer, times(1)).delete(List.of("url1"));
    verify(db, times(1))
        .updateStateByAgeAndState(24, TimeUnit.HOURS, DocumentState.ACTIVE, DocumentState.DELETED);
  }
}