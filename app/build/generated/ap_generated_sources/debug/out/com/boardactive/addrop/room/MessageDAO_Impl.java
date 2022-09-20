package com.boardactive.addrop.room;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.boardactive.addrop.room.table.MessageEntity;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class MessageDAO_Impl implements MessageDAO {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfMessageEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMessage;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllMessage;

  public MessageDAO_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMessageEntity = new EntityInsertionAdapter<MessageEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `message`(`id`,`baMessageId`,`baNotificationId`,`firebaseNotificationId`,`title`,`body`,`imageUrl`,`latitude`,`longitude`,`messageData`,`isTestMessage`,`isRead`,`dateCreated`,`dateLastUpdated`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, MessageEntity value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, value.getId());
        }
        if (value.getBaMessageId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getBaMessageId());
        }
        if (value.getBaNotificationId() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getBaNotificationId());
        }
        if (value.getFirebaseNotificationId() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getFirebaseNotificationId());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getTitle());
        }
        if (value.getBody() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getBody());
        }
        if (value.getImageUrl() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getImageUrl());
        }
        if (value.getLatitude() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getLatitude());
        }
        if (value.getLongitude() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getLongitude());
        }
        if (value.getMessageData() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getMessageData());
        }
        if (value.getIsTestMessage() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getIsTestMessage());
        }
        final Integer _tmp;
        _tmp = value.getIsRead() == null ? null : (value.getIsRead() ? 1 : 0);
        if (_tmp == null) {
          stmt.bindNull(12);
        } else {
          stmt.bindLong(12, _tmp);
        }
        if (value.getDateCreated() == null) {
          stmt.bindNull(13);
        } else {
          stmt.bindLong(13, value.getDateCreated());
        }
        if (value.getDateLastUpdated() == null) {
          stmt.bindNull(14);
        } else {
          stmt.bindLong(14, value.getDateLastUpdated());
        }
      }
    };
    this.__preparedStmtOfDeleteMessage = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM message WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllMessage = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM message";
        return _query;
      }
    };
  }

  @Override
  public void insertMessage(final MessageEntity message) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfMessageEntity.insert(message);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteMessage(final long id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMessage.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteMessage.release(_stmt);
    }
  }

  @Override
  public void deleteAllMessage() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllMessage.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAllMessage.release(_stmt);
    }
  }

  @Override
  public List<MessageEntity> getMessageByPage(final int limit, final int offset) {
    final String _sql = "SELECT * FROM message ORDER BY dateCreated DESC LIMIT ? OFFSET ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    _argIndex = 2;
    _statement.bindLong(_argIndex, offset);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfBaMessageId = CursorUtil.getColumnIndexOrThrow(_cursor, "baMessageId");
      final int _cursorIndexOfBaNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "baNotificationId");
      final int _cursorIndexOfFirebaseNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "firebaseNotificationId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
      final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfMessageData = CursorUtil.getColumnIndexOrThrow(_cursor, "messageData");
      final int _cursorIndexOfIsTestMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "isTestMessage");
      final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
      final int _cursorIndexOfDateCreated = CursorUtil.getColumnIndexOrThrow(_cursor, "dateCreated");
      final int _cursorIndexOfDateLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "dateLastUpdated");
      final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final MessageEntity _item;
        _item = new MessageEntity();
        final Integer _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getInt(_cursorIndexOfId);
        }
        _item.setId(_tmpId);
        final String _tmpBaMessageId;
        _tmpBaMessageId = _cursor.getString(_cursorIndexOfBaMessageId);
        _item.setBaMessageId(_tmpBaMessageId);
        final String _tmpBaNotificationId;
        _tmpBaNotificationId = _cursor.getString(_cursorIndexOfBaNotificationId);
        _item.setBaNotificationId(_tmpBaNotificationId);
        final String _tmpFirebaseNotificationId;
        _tmpFirebaseNotificationId = _cursor.getString(_cursorIndexOfFirebaseNotificationId);
        _item.setFirebaseNotificationId(_tmpFirebaseNotificationId);
        final String _tmpTitle;
        _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        _item.setTitle(_tmpTitle);
        final String _tmpBody;
        _tmpBody = _cursor.getString(_cursorIndexOfBody);
        _item.setBody(_tmpBody);
        final String _tmpImageUrl;
        _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
        _item.setImageUrl(_tmpImageUrl);
        final String _tmpLatitude;
        _tmpLatitude = _cursor.getString(_cursorIndexOfLatitude);
        _item.setLatitude(_tmpLatitude);
        final String _tmpLongitude;
        _tmpLongitude = _cursor.getString(_cursorIndexOfLongitude);
        _item.setLongitude(_tmpLongitude);
        final String _tmpMessageData;
        _tmpMessageData = _cursor.getString(_cursorIndexOfMessageData);
        _item.setMessageData(_tmpMessageData);
        final String _tmpIsTestMessage;
        _tmpIsTestMessage = _cursor.getString(_cursorIndexOfIsTestMessage);
        _item.setIsTestMessage(_tmpIsTestMessage);
        final Boolean _tmpIsRead;
        final Integer _tmp;
        if (_cursor.isNull(_cursorIndexOfIsRead)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(_cursorIndexOfIsRead);
        }
        _tmpIsRead = _tmp == null ? null : _tmp != 0;
        _item.setIsRead(_tmpIsRead);
        final Long _tmpDateCreated;
        if (_cursor.isNull(_cursorIndexOfDateCreated)) {
          _tmpDateCreated = null;
        } else {
          _tmpDateCreated = _cursor.getLong(_cursorIndexOfDateCreated);
        }
        _item.setDateCreated(_tmpDateCreated);
        final Long _tmpDateLastUpdated;
        if (_cursor.isNull(_cursorIndexOfDateLastUpdated)) {
          _tmpDateLastUpdated = null;
        } else {
          _tmpDateLastUpdated = _cursor.getLong(_cursorIndexOfDateLastUpdated);
        }
        _item.setDateLastUpdated(_tmpDateLastUpdated);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public MessageEntity getMessage(final long id) {
    final String _sql = "SELECT * FROM message WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfBaMessageId = CursorUtil.getColumnIndexOrThrow(_cursor, "baMessageId");
      final int _cursorIndexOfBaNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "baNotificationId");
      final int _cursorIndexOfFirebaseNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "firebaseNotificationId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
      final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfMessageData = CursorUtil.getColumnIndexOrThrow(_cursor, "messageData");
      final int _cursorIndexOfIsTestMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "isTestMessage");
      final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
      final int _cursorIndexOfDateCreated = CursorUtil.getColumnIndexOrThrow(_cursor, "dateCreated");
      final int _cursorIndexOfDateLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "dateLastUpdated");
      final MessageEntity _result;
      if(_cursor.moveToFirst()) {
        _result = new MessageEntity();
        final Integer _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getInt(_cursorIndexOfId);
        }
        _result.setId(_tmpId);
        final String _tmpBaMessageId;
        _tmpBaMessageId = _cursor.getString(_cursorIndexOfBaMessageId);
        _result.setBaMessageId(_tmpBaMessageId);
        final String _tmpBaNotificationId;
        _tmpBaNotificationId = _cursor.getString(_cursorIndexOfBaNotificationId);
        _result.setBaNotificationId(_tmpBaNotificationId);
        final String _tmpFirebaseNotificationId;
        _tmpFirebaseNotificationId = _cursor.getString(_cursorIndexOfFirebaseNotificationId);
        _result.setFirebaseNotificationId(_tmpFirebaseNotificationId);
        final String _tmpTitle;
        _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        _result.setTitle(_tmpTitle);
        final String _tmpBody;
        _tmpBody = _cursor.getString(_cursorIndexOfBody);
        _result.setBody(_tmpBody);
        final String _tmpImageUrl;
        _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
        _result.setImageUrl(_tmpImageUrl);
        final String _tmpLatitude;
        _tmpLatitude = _cursor.getString(_cursorIndexOfLatitude);
        _result.setLatitude(_tmpLatitude);
        final String _tmpLongitude;
        _tmpLongitude = _cursor.getString(_cursorIndexOfLongitude);
        _result.setLongitude(_tmpLongitude);
        final String _tmpMessageData;
        _tmpMessageData = _cursor.getString(_cursorIndexOfMessageData);
        _result.setMessageData(_tmpMessageData);
        final String _tmpIsTestMessage;
        _tmpIsTestMessage = _cursor.getString(_cursorIndexOfIsTestMessage);
        _result.setIsTestMessage(_tmpIsTestMessage);
        final Boolean _tmpIsRead;
        final Integer _tmp;
        if (_cursor.isNull(_cursorIndexOfIsRead)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(_cursorIndexOfIsRead);
        }
        _tmpIsRead = _tmp == null ? null : _tmp != 0;
        _result.setIsRead(_tmpIsRead);
        final Long _tmpDateCreated;
        if (_cursor.isNull(_cursorIndexOfDateCreated)) {
          _tmpDateCreated = null;
        } else {
          _tmpDateCreated = _cursor.getLong(_cursorIndexOfDateCreated);
        }
        _result.setDateCreated(_tmpDateCreated);
        final Long _tmpDateLastUpdated;
        if (_cursor.isNull(_cursorIndexOfDateLastUpdated)) {
          _tmpDateLastUpdated = null;
        } else {
          _tmpDateLastUpdated = _cursor.getLong(_cursorIndexOfDateLastUpdated);
        }
        _result.setDateLastUpdated(_tmpDateLastUpdated);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public MessageEntity getMessageByMessageId(final String messageId) {
    final String _sql = "SELECT * FROM message WHERE baMessageId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (messageId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, messageId);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfBaMessageId = CursorUtil.getColumnIndexOrThrow(_cursor, "baMessageId");
      final int _cursorIndexOfBaNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "baNotificationId");
      final int _cursorIndexOfFirebaseNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "firebaseNotificationId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
      final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfMessageData = CursorUtil.getColumnIndexOrThrow(_cursor, "messageData");
      final int _cursorIndexOfIsTestMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "isTestMessage");
      final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
      final int _cursorIndexOfDateCreated = CursorUtil.getColumnIndexOrThrow(_cursor, "dateCreated");
      final int _cursorIndexOfDateLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "dateLastUpdated");
      final MessageEntity _result;
      if(_cursor.moveToFirst()) {
        _result = new MessageEntity();
        final Integer _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getInt(_cursorIndexOfId);
        }
        _result.setId(_tmpId);
        final String _tmpBaMessageId;
        _tmpBaMessageId = _cursor.getString(_cursorIndexOfBaMessageId);
        _result.setBaMessageId(_tmpBaMessageId);
        final String _tmpBaNotificationId;
        _tmpBaNotificationId = _cursor.getString(_cursorIndexOfBaNotificationId);
        _result.setBaNotificationId(_tmpBaNotificationId);
        final String _tmpFirebaseNotificationId;
        _tmpFirebaseNotificationId = _cursor.getString(_cursorIndexOfFirebaseNotificationId);
        _result.setFirebaseNotificationId(_tmpFirebaseNotificationId);
        final String _tmpTitle;
        _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        _result.setTitle(_tmpTitle);
        final String _tmpBody;
        _tmpBody = _cursor.getString(_cursorIndexOfBody);
        _result.setBody(_tmpBody);
        final String _tmpImageUrl;
        _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
        _result.setImageUrl(_tmpImageUrl);
        final String _tmpLatitude;
        _tmpLatitude = _cursor.getString(_cursorIndexOfLatitude);
        _result.setLatitude(_tmpLatitude);
        final String _tmpLongitude;
        _tmpLongitude = _cursor.getString(_cursorIndexOfLongitude);
        _result.setLongitude(_tmpLongitude);
        final String _tmpMessageData;
        _tmpMessageData = _cursor.getString(_cursorIndexOfMessageData);
        _result.setMessageData(_tmpMessageData);
        final String _tmpIsTestMessage;
        _tmpIsTestMessage = _cursor.getString(_cursorIndexOfIsTestMessage);
        _result.setIsTestMessage(_tmpIsTestMessage);
        final Boolean _tmpIsRead;
        final Integer _tmp;
        if (_cursor.isNull(_cursorIndexOfIsRead)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(_cursorIndexOfIsRead);
        }
        _tmpIsRead = _tmp == null ? null : _tmp != 0;
        _result.setIsRead(_tmpIsRead);
        final Long _tmpDateCreated;
        if (_cursor.isNull(_cursorIndexOfDateCreated)) {
          _tmpDateCreated = null;
        } else {
          _tmpDateCreated = _cursor.getLong(_cursorIndexOfDateCreated);
        }
        _result.setDateCreated(_tmpDateCreated);
        final Long _tmpDateLastUpdated;
        if (_cursor.isNull(_cursorIndexOfDateLastUpdated)) {
          _tmpDateLastUpdated = null;
        } else {
          _tmpDateLastUpdated = _cursor.getLong(_cursorIndexOfDateLastUpdated);
        }
        _result.setDateLastUpdated(_tmpDateLastUpdated);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Integer getMessageUnreadCount() {
    final String _sql = "SELECT COUNT(id) FROM message WHERE isRead = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false);
    try {
      final Integer _result;
      if(_cursor.moveToFirst()) {
        final Integer _tmp;
        if (_cursor.isNull(0)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(0);
        }
        _result = _tmp;
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Integer getMessageCount() {
    final String _sql = "SELECT COUNT(id) FROM message";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false);
    try {
      final Integer _result;
      if(_cursor.moveToFirst()) {
        final Integer _tmp;
        if (_cursor.isNull(0)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(0);
        }
        _result = _tmp;
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Integer getMaxId() {
    final String _sql = "SELECT id FROM message ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false);
    try {
      final Integer _result;
      if(_cursor.moveToFirst()) {
        if (_cursor.isNull(0)) {
          _result = null;
        } else {
          _result = _cursor.getInt(0);
        }
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
