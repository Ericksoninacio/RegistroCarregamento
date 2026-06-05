package com.registrocarregamento.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.registrocarregamento.data.local.entity.CarregamentoEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CarregamentoDao_Impl implements CarregamentoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CarregamentoEntity> __insertionAdapterOfCarregamentoEntity;

  private final EntityDeletionOrUpdateAdapter<CarregamentoEntity> __updateAdapterOfCarregamentoEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarcarComoSincronizado;

  public CarregamentoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCarregamentoEntity = new EntityInsertionAdapter<CarregamentoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `carregamentos` (`id`,`placa`,`cliente`,`cidadeCarregamento`,`cidadeConfereCte`,`dataRegistro`,`horaRegistro`,`fotoPlaca`,`fotoNfe1`,`fotoNfe2`,`fotoCte`,`sincronizado`,`criadoEm`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CarregamentoEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPlaca());
        statement.bindString(3, entity.getCliente());
        statement.bindString(4, entity.getCidadeCarregamento());
        final int _tmp = entity.getCidadeConfereCte() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindString(6, entity.getDataRegistro());
        statement.bindString(7, entity.getHoraRegistro());
        statement.bindString(8, entity.getFotoPlaca());
        statement.bindString(9, entity.getFotoNfe1());
        if (entity.getFotoNfe2() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getFotoNfe2());
        }
        statement.bindString(11, entity.getFotoCte());
        final int _tmp_1 = entity.getSincronizado() ? 1 : 0;
        statement.bindLong(12, _tmp_1);
        statement.bindLong(13, entity.getCriadoEm());
      }
    };
    this.__updateAdapterOfCarregamentoEntity = new EntityDeletionOrUpdateAdapter<CarregamentoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `carregamentos` SET `id` = ?,`placa` = ?,`cliente` = ?,`cidadeCarregamento` = ?,`cidadeConfereCte` = ?,`dataRegistro` = ?,`horaRegistro` = ?,`fotoPlaca` = ?,`fotoNfe1` = ?,`fotoNfe2` = ?,`fotoCte` = ?,`sincronizado` = ?,`criadoEm` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CarregamentoEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPlaca());
        statement.bindString(3, entity.getCliente());
        statement.bindString(4, entity.getCidadeCarregamento());
        final int _tmp = entity.getCidadeConfereCte() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindString(6, entity.getDataRegistro());
        statement.bindString(7, entity.getHoraRegistro());
        statement.bindString(8, entity.getFotoPlaca());
        statement.bindString(9, entity.getFotoNfe1());
        if (entity.getFotoNfe2() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getFotoNfe2());
        }
        statement.bindString(11, entity.getFotoCte());
        final int _tmp_1 = entity.getSincronizado() ? 1 : 0;
        statement.bindLong(12, _tmp_1);
        statement.bindLong(13, entity.getCriadoEm());
        statement.bindLong(14, entity.getId());
      }
    };
    this.__preparedStmtOfMarcarComoSincronizado = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE carregamentos SET sincronizado = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object inserir(final CarregamentoEntity carregamento,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCarregamentoEntity.insertAndReturnId(carregamento);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object atualizar(final CarregamentoEntity carregamento,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCarregamentoEntity.handle(carregamento);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object marcarComoSincronizado(final long id,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarcarComoSincronizado.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfMarcarComoSincronizado.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CarregamentoEntity>> listarTodos() {
    final String _sql = "SELECT * FROM carregamentos ORDER BY criadoEm DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"carregamentos"}, new Callable<List<CarregamentoEntity>>() {
      @Override
      @NonNull
      public List<CarregamentoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlaca = CursorUtil.getColumnIndexOrThrow(_cursor, "placa");
          final int _cursorIndexOfCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "cliente");
          final int _cursorIndexOfCidadeCarregamento = CursorUtil.getColumnIndexOrThrow(_cursor, "cidadeCarregamento");
          final int _cursorIndexOfCidadeConfereCte = CursorUtil.getColumnIndexOrThrow(_cursor, "cidadeConfereCte");
          final int _cursorIndexOfDataRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "dataRegistro");
          final int _cursorIndexOfHoraRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "horaRegistro");
          final int _cursorIndexOfFotoPlaca = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoPlaca");
          final int _cursorIndexOfFotoNfe1 = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoNfe1");
          final int _cursorIndexOfFotoNfe2 = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoNfe2");
          final int _cursorIndexOfFotoCte = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoCte");
          final int _cursorIndexOfSincronizado = CursorUtil.getColumnIndexOrThrow(_cursor, "sincronizado");
          final int _cursorIndexOfCriadoEm = CursorUtil.getColumnIndexOrThrow(_cursor, "criadoEm");
          final List<CarregamentoEntity> _result = new ArrayList<CarregamentoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CarregamentoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPlaca;
            _tmpPlaca = _cursor.getString(_cursorIndexOfPlaca);
            final String _tmpCliente;
            _tmpCliente = _cursor.getString(_cursorIndexOfCliente);
            final String _tmpCidadeCarregamento;
            _tmpCidadeCarregamento = _cursor.getString(_cursorIndexOfCidadeCarregamento);
            final boolean _tmpCidadeConfereCte;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCidadeConfereCte);
            _tmpCidadeConfereCte = _tmp != 0;
            final String _tmpDataRegistro;
            _tmpDataRegistro = _cursor.getString(_cursorIndexOfDataRegistro);
            final String _tmpHoraRegistro;
            _tmpHoraRegistro = _cursor.getString(_cursorIndexOfHoraRegistro);
            final String _tmpFotoPlaca;
            _tmpFotoPlaca = _cursor.getString(_cursorIndexOfFotoPlaca);
            final String _tmpFotoNfe1;
            _tmpFotoNfe1 = _cursor.getString(_cursorIndexOfFotoNfe1);
            final String _tmpFotoNfe2;
            if (_cursor.isNull(_cursorIndexOfFotoNfe2)) {
              _tmpFotoNfe2 = null;
            } else {
              _tmpFotoNfe2 = _cursor.getString(_cursorIndexOfFotoNfe2);
            }
            final String _tmpFotoCte;
            _tmpFotoCte = _cursor.getString(_cursorIndexOfFotoCte);
            final boolean _tmpSincronizado;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSincronizado);
            _tmpSincronizado = _tmp_1 != 0;
            final long _tmpCriadoEm;
            _tmpCriadoEm = _cursor.getLong(_cursorIndexOfCriadoEm);
            _item = new CarregamentoEntity(_tmpId,_tmpPlaca,_tmpCliente,_tmpCidadeCarregamento,_tmpCidadeConfereCte,_tmpDataRegistro,_tmpHoraRegistro,_tmpFotoPlaca,_tmpFotoNfe1,_tmpFotoNfe2,_tmpFotoCte,_tmpSincronizado,_tmpCriadoEm);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object listarPendentes(final Continuation<? super List<CarregamentoEntity>> $completion) {
    final String _sql = "SELECT * FROM carregamentos WHERE sincronizado = 0 ORDER BY criadoEm ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CarregamentoEntity>>() {
      @Override
      @NonNull
      public List<CarregamentoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlaca = CursorUtil.getColumnIndexOrThrow(_cursor, "placa");
          final int _cursorIndexOfCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "cliente");
          final int _cursorIndexOfCidadeCarregamento = CursorUtil.getColumnIndexOrThrow(_cursor, "cidadeCarregamento");
          final int _cursorIndexOfCidadeConfereCte = CursorUtil.getColumnIndexOrThrow(_cursor, "cidadeConfereCte");
          final int _cursorIndexOfDataRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "dataRegistro");
          final int _cursorIndexOfHoraRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "horaRegistro");
          final int _cursorIndexOfFotoPlaca = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoPlaca");
          final int _cursorIndexOfFotoNfe1 = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoNfe1");
          final int _cursorIndexOfFotoNfe2 = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoNfe2");
          final int _cursorIndexOfFotoCte = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoCte");
          final int _cursorIndexOfSincronizado = CursorUtil.getColumnIndexOrThrow(_cursor, "sincronizado");
          final int _cursorIndexOfCriadoEm = CursorUtil.getColumnIndexOrThrow(_cursor, "criadoEm");
          final List<CarregamentoEntity> _result = new ArrayList<CarregamentoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CarregamentoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPlaca;
            _tmpPlaca = _cursor.getString(_cursorIndexOfPlaca);
            final String _tmpCliente;
            _tmpCliente = _cursor.getString(_cursorIndexOfCliente);
            final String _tmpCidadeCarregamento;
            _tmpCidadeCarregamento = _cursor.getString(_cursorIndexOfCidadeCarregamento);
            final boolean _tmpCidadeConfereCte;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCidadeConfereCte);
            _tmpCidadeConfereCte = _tmp != 0;
            final String _tmpDataRegistro;
            _tmpDataRegistro = _cursor.getString(_cursorIndexOfDataRegistro);
            final String _tmpHoraRegistro;
            _tmpHoraRegistro = _cursor.getString(_cursorIndexOfHoraRegistro);
            final String _tmpFotoPlaca;
            _tmpFotoPlaca = _cursor.getString(_cursorIndexOfFotoPlaca);
            final String _tmpFotoNfe1;
            _tmpFotoNfe1 = _cursor.getString(_cursorIndexOfFotoNfe1);
            final String _tmpFotoNfe2;
            if (_cursor.isNull(_cursorIndexOfFotoNfe2)) {
              _tmpFotoNfe2 = null;
            } else {
              _tmpFotoNfe2 = _cursor.getString(_cursorIndexOfFotoNfe2);
            }
            final String _tmpFotoCte;
            _tmpFotoCte = _cursor.getString(_cursorIndexOfFotoCte);
            final boolean _tmpSincronizado;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSincronizado);
            _tmpSincronizado = _tmp_1 != 0;
            final long _tmpCriadoEm;
            _tmpCriadoEm = _cursor.getLong(_cursorIndexOfCriadoEm);
            _item = new CarregamentoEntity(_tmpId,_tmpPlaca,_tmpCliente,_tmpCidadeCarregamento,_tmpCidadeConfereCte,_tmpDataRegistro,_tmpHoraRegistro,_tmpFotoPlaca,_tmpFotoNfe1,_tmpFotoNfe2,_tmpFotoCte,_tmpSincronizado,_tmpCriadoEm);
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

  @Override
  public Object buscarPorId(final long id,
      final Continuation<? super CarregamentoEntity> $completion) {
    final String _sql = "SELECT * FROM carregamentos WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CarregamentoEntity>() {
      @Override
      @Nullable
      public CarregamentoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlaca = CursorUtil.getColumnIndexOrThrow(_cursor, "placa");
          final int _cursorIndexOfCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "cliente");
          final int _cursorIndexOfCidadeCarregamento = CursorUtil.getColumnIndexOrThrow(_cursor, "cidadeCarregamento");
          final int _cursorIndexOfCidadeConfereCte = CursorUtil.getColumnIndexOrThrow(_cursor, "cidadeConfereCte");
          final int _cursorIndexOfDataRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "dataRegistro");
          final int _cursorIndexOfHoraRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "horaRegistro");
          final int _cursorIndexOfFotoPlaca = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoPlaca");
          final int _cursorIndexOfFotoNfe1 = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoNfe1");
          final int _cursorIndexOfFotoNfe2 = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoNfe2");
          final int _cursorIndexOfFotoCte = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoCte");
          final int _cursorIndexOfSincronizado = CursorUtil.getColumnIndexOrThrow(_cursor, "sincronizado");
          final int _cursorIndexOfCriadoEm = CursorUtil.getColumnIndexOrThrow(_cursor, "criadoEm");
          final CarregamentoEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPlaca;
            _tmpPlaca = _cursor.getString(_cursorIndexOfPlaca);
            final String _tmpCliente;
            _tmpCliente = _cursor.getString(_cursorIndexOfCliente);
            final String _tmpCidadeCarregamento;
            _tmpCidadeCarregamento = _cursor.getString(_cursorIndexOfCidadeCarregamento);
            final boolean _tmpCidadeConfereCte;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCidadeConfereCte);
            _tmpCidadeConfereCte = _tmp != 0;
            final String _tmpDataRegistro;
            _tmpDataRegistro = _cursor.getString(_cursorIndexOfDataRegistro);
            final String _tmpHoraRegistro;
            _tmpHoraRegistro = _cursor.getString(_cursorIndexOfHoraRegistro);
            final String _tmpFotoPlaca;
            _tmpFotoPlaca = _cursor.getString(_cursorIndexOfFotoPlaca);
            final String _tmpFotoNfe1;
            _tmpFotoNfe1 = _cursor.getString(_cursorIndexOfFotoNfe1);
            final String _tmpFotoNfe2;
            if (_cursor.isNull(_cursorIndexOfFotoNfe2)) {
              _tmpFotoNfe2 = null;
            } else {
              _tmpFotoNfe2 = _cursor.getString(_cursorIndexOfFotoNfe2);
            }
            final String _tmpFotoCte;
            _tmpFotoCte = _cursor.getString(_cursorIndexOfFotoCte);
            final boolean _tmpSincronizado;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSincronizado);
            _tmpSincronizado = _tmp_1 != 0;
            final long _tmpCriadoEm;
            _tmpCriadoEm = _cursor.getLong(_cursorIndexOfCriadoEm);
            _result = new CarregamentoEntity(_tmpId,_tmpPlaca,_tmpCliente,_tmpCidadeCarregamento,_tmpCidadeConfereCte,_tmpDataRegistro,_tmpHoraRegistro,_tmpFotoPlaca,_tmpFotoNfe1,_tmpFotoNfe2,_tmpFotoCte,_tmpSincronizado,_tmpCriadoEm);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<Integer> contarPendentes() {
    final String _sql = "SELECT COUNT(*) FROM carregamentos WHERE sincronizado = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"carregamentos"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
