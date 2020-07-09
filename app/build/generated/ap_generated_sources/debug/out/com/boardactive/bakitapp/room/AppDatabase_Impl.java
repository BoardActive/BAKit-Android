package com.boardactive.bakitapp.room;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile MessageDAO _messageDAO;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(6) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `message` (`id` INTEGER, `baMessageId` TEXT, `baNotificationId` TEXT, `firebaseNotificationId` TEXT, `title` TEXT, `body` TEXT, `imageUrl` TEXT, `latitude` TEXT, `longitude` TEXT, `messageData` TEXT, `isTestMessage` TEXT, `isRead` INTEGER, `dateCreated` INTEGER, `dateLastUpdated` INTEGER, PRIMARY KEY(`id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c7956f4d7be32f15feffafa76dbee9f4')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `message`");
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsMessage = new HashMap<String, TableInfo.Column>(14);
        _columnsMessage.put("id", new TableInfo.Column("id", "INTEGER", false, 1));
        _columnsMessage.put("baMessageId", new TableInfo.Column("baMessageId", "TEXT", false, 0));
        _columnsMessage.put("baNotificationId", new TableInfo.Column("baNotificationId", "TEXT", false, 0));
        _columnsMessage.put("firebaseNotificationId", new TableInfo.Column("firebaseNotificationId", "TEXT", false, 0));
        _columnsMessage.put("title", new TableInfo.Column("title", "TEXT", false, 0));
        _columnsMessage.put("body", new TableInfo.Column("body", "TEXT", false, 0));
        _columnsMessage.put("imageUrl", new TableInfo.Column("imageUrl", "TEXT", false, 0));
        _columnsMessage.put("latitude", new TableInfo.Column("latitude", "TEXT", false, 0));
        _columnsMessage.put("longitude", new TableInfo.Column("longitude", "TEXT", false, 0));
        _columnsMessage.put("messageData", new TableInfo.Column("messageData", "TEXT", false, 0));
        _columnsMessage.put("isTestMessage", new TableInfo.Column("isTestMessage", "TEXT", false, 0));
        _columnsMessage.put("isRead", new TableInfo.Column("isRead", "INTEGER", false, 0));
        _columnsMessage.put("dateCreated", new TableInfo.Column("dateCreated", "INTEGER", false, 0));
        _columnsMessage.put("dateLastUpdated", new TableInfo.Column("dateLastUpdated", "INTEGER", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMessage = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMessage = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMessage = new TableInfo("message", _columnsMessage, _foreignKeysMessage, _indicesMessage);
        final TableInfo _existingMessage = TableInfo.read(_db, "message");
        if (! _infoMessage.equals(_existingMessage)) {
          throw new IllegalStateException("Migration didn't properly handle message(com.boardactive.bakitapp.room.table.MessageEntity).\n"
                  + " Expected:\n" + _infoMessage + "\n"
                  + " Found:\n" + _existingMessage);
        }
      }
    }, "c7956f4d7be32f15feffafa76dbee9f4", "f0b1039fac7edf931ad38c93246bfcb2");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "message");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `message`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public MessageDAO getMessageDAO() {
    if (_messageDAO != null) {
      return _messageDAO;
    } else {
      synchronized(this) {
        if(_messageDAO == null) {
          _messageDAO = new MessageDAO_Impl(this);
        }
        return _messageDAO;
      }
    }
  }
}
