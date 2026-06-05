package com.registrocarregamento.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.registrocarregamento.data.local.entity.FilaSincronizacaoEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FilaSincronizacaoDao_Impl implements FilaSincronizacaoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FilaSincronizacaoEntity> __insertionAdapterOfFilaSincronizacaoEntity;

  private final SharedSQLiteStatement __preparedStmtOfAtualizarStatus;

  private final SharedSQLiteStatement __preparedStmtOfRemover;

  public FilaSincronizacaoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFilaSincronizacaoEntity = new EntityInsertionAdapter<FilaSincronizacaoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `fila_sincronizacao` (`id`,`carregamentoId`,`tentativas`,`ultimaTentativa`,`status`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FilaSincronizacaoEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCarregamentoId());
        statement.bindLong(3, entity.getTentativas());
        if (entity.getUltimaTentativa() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getUltimaTentativa());
        }
        statement.bindString(5, entity.getStatus());
      }
    };
    this.__preparedStmtOfAtualizarStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE fila_sincronizacao SET status = ?, tentativas = tentativas + 1, ultimaTentativa = ? WHERE carregamentoId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRemover = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM fila_sincronizacao WHERE carregamentoId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object inserir(final FilaSincronizacaoEntity fila,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFilaSincronizacaoEntity.insertAndReturnId(fila);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object atualizarStatus(final long carregamentoId, final String status, final long agora,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAtualizarStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, agora);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, carregamentoId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfAtualizarStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object remover(final long carregamentoId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRemover.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, carregamentoId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfRemover.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object listarParaEnvio(
      final Continuation<? super List<FilaSincronizacaoEntity>> $completion) {
    final String _sql = "SELECT * FROM fila_sincronizacao WHERE status IN ('PENDENTE', 'ERRO') ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FilaSincronizacaoEntity>>() {
      @Override
      @NonNull
      public List<FilaSincronizacaoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCarregamentoId = CursorUtil.getColumnIndexOrThrow(_cursor, "carregamentoId");
          final int _cursorIndexOfTentativas = CursorUtil.getColumnIndexOrThrow(_cursor, "tentativas");
          final int _cursorIndexOfUltimaTentativa = CursorUtil.getColumnIndexOrThrow(_cursor, "ultimaTentativa");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<FilaSincronizacaoEntity> _result = new ArrayList<FilaSincronizacaoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FilaSincronizacaoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCarregamentoId;
            _tmpCarregamentoId = _cursor.getLong(_cursorIndexOfCarregamentoId);
            final int _tmpTentativas;
            _tmpTentativas = _cursor.getInt(_cursorIndexOfTentativas);
            final Long _tmpUltimaTentativa;
            if (_cursor.isNull(_cursorIndexOfUltimaTentativa)) {
              _tmpUltimaTentativa = null;
            } else {
              _tmpUltimaTentativa = _cursor.getLong(_cursorIndexOfUltimaTentativa);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new FilaSincronizacaoEntity(_tmpId,_tmpCarregamentoId,_tmpTentativas,_tmpUltimaTentativa,_tmpStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
