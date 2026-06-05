package com.registrocarregamento.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.registrocarregamento.data.local.dao.CarregamentoDao;
import com.registrocarregamento.data.local.dao.CarregamentoDao_Impl;
import com.registrocarregamento.data.local.dao.FilaSincronizacaoDao;
import com.registrocarregamento.data.local.dao.FilaSincronizacaoDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile CarregamentoDao _carregamentoDao;

  private volatile FilaSincronizacaoDao _filaSincronizacaoDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `carregamentos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `placa` TEXT NOT NULL, `cliente` TEXT NOT NULL, `cidadeCarregamento` TEXT NOT NULL, `cidadeConfereCte` INTEGER NOT NULL, `dataRegistro` TEXT NOT NULL, `horaRegistro` TEXT NOT NULL, `fotoPlaca` TEXT NOT NULL, `fotoNfe1` TEXT NOT NULL, `fotoNfe2` TEXT, `fotoCte` TEXT NOT NULL, `sincronizado` INTEGER NOT NULL, `criadoEm` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `fila_sincronizacao` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `carregamentoId` INTEGER NOT NULL, `tentativas` INTEGER NOT NULL, `ultimaTentativa` INTEGER, `status` TEXT NOT NULL, FOREIGN KEY(`carregamentoId`) REFERENCES `carregamentos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f9763ca22937b6924a844a8d0c7b185f')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `carregamentos`");
        db.execSQL("DROP TABLE IF EXISTS `fila_sincronizacao`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCarregamentos = new HashMap<String, TableInfo.Column>(13);
        _columnsCarregamentos.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarregamentos.put("placa", new TableInfo.Column("placa", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarregamentos.put("cliente", new TableInfo.Column("cliente", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarregamentos.put("cidadeCarregamento", new TableInfo.Column("cidadeCarregamento", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarregamentos.put("cidadeConfereCte", new TableInfo.Column("cidadeConfereCte", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarregamentos.put("dataRegistro", new TableInfo.Column("dataRegistro", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarregamentos.put("horaRegistro", new TableInfo.Column("horaRegistro", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarregamentos.put("fotoPlaca", new TableInfo.Column("fotoPlaca", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarregamentos.put("fotoNfe1", new TableInfo.Column("fotoNfe1", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarregamentos.put("fotoNfe2", new TableInfo.Column("fotoNfe2", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarregamentos.put("fotoCte", new TableInfo.Column("fotoCte", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarregamentos.put("sincronizado", new TableInfo.Column("sincronizado", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarregamentos.put("criadoEm", new TableInfo.Column("criadoEm", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCarregamentos = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCarregamentos = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCarregamentos = new TableInfo("carregamentos", _columnsCarregamentos, _foreignKeysCarregamentos, _indicesCarregamentos);
        final TableInfo _existingCarregamentos = TableInfo.read(db, "carregamentos");
        if (!_infoCarregamentos.equals(_existingCarregamentos)) {
          return new RoomOpenHelper.ValidationResult(false, "carregamentos(com.registrocarregamento.data.local.entity.CarregamentoEntity).\n"
                  + " Expected:\n" + _infoCarregamentos + "\n"
                  + " Found:\n" + _existingCarregamentos);
        }
        final HashMap<String, TableInfo.Column> _columnsFilaSincronizacao = new HashMap<String, TableInfo.Column>(5);
        _columnsFilaSincronizacao.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilaSincronizacao.put("carregamentoId", new TableInfo.Column("carregamentoId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilaSincronizacao.put("tentativas", new TableInfo.Column("tentativas", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilaSincronizacao.put("ultimaTentativa", new TableInfo.Column("ultimaTentativa", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilaSincronizacao.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFilaSincronizacao = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysFilaSincronizacao.add(new TableInfo.ForeignKey("carregamentos", "CASCADE", "NO ACTION", Arrays.asList("carregamentoId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesFilaSincronizacao = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFilaSincronizacao = new TableInfo("fila_sincronizacao", _columnsFilaSincronizacao, _foreignKeysFilaSincronizacao, _indicesFilaSincronizacao);
        final TableInfo _existingFilaSincronizacao = TableInfo.read(db, "fila_sincronizacao");
        if (!_infoFilaSincronizacao.equals(_existingFilaSincronizacao)) {
          return new RoomOpenHelper.ValidationResult(false, "fila_sincronizacao(com.registrocarregamento.data.local.entity.FilaSincronizacaoEntity).\n"
                  + " Expected:\n" + _infoFilaSincronizacao + "\n"
                  + " Found:\n" + _existingFilaSincronizacao);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f9763ca22937b6924a844a8d0c7b185f", "8fe83419435d336fe4cbb6e89c2e6452");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "carregamentos","fila_sincronizacao");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `carregamentos`");
      _db.execSQL("DELETE FROM `fila_sincronizacao`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CarregamentoDao.class, CarregamentoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FilaSincronizacaoDao.class, FilaSincronizacaoDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CarregamentoDao carregamentoDao() {
    if (_carregamentoDao != null) {
      return _carregamentoDao;
    } else {
      synchronized(this) {
        if(_carregamentoDao == null) {
          _carregamentoDao = new CarregamentoDao_Impl(this);
        }
        return _carregamentoDao;
      }
    }
  }

  @Override
  public FilaSincronizacaoDao filaSincronizacaoDao() {
    if (_filaSincronizacaoDao != null) {
      return _filaSincronizacaoDao;
    } else {
      synchronized(this) {
        if(_filaSincronizacaoDao == null) {
          _filaSincronizacaoDao = new FilaSincronizacaoDao_Impl(this);
        }
        return _filaSincronizacaoDao;
      }
    }
  }
}
